package cn.iocoder.yudao.module.lims.service.workflow;

import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskQcRecordDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskRawRecordDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskReviewDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestRequestDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestTaskDO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lims.enums.ErrorCodeConstants.TEST_EVIDENCE_INCOMPLETE;
import static cn.iocoder.yudao.module.lims.enums.ErrorCodeConstants.TEST_QC_RULE_UNSATISFIED;
import static cn.iocoder.yudao.module.lims.enums.ErrorCodeConstants.TEST_RESULT_FIELD_INVALID;

@Service
public class LimsQualityGateService {

    @Resource
    private ObjectMapper objectMapper;

    public void validateResultValues(LimsTestRequestDO request, LimsTestTaskDO task, String rawData) {
        List<JsonNode> configuredFields = resultFieldsForTask(readObject(resolveWorkflowSnapshot(request)), task);
        if (configuredFields.isEmpty()) {
            return;
        }
        Map<String, JsonNode> submittedValues = submittedValuesByFieldCode(rawData);
        for (JsonNode field : configuredFields) {
            String fieldCode = field.path("fieldCode").asText("");
            if (!StringUtils.hasText(fieldCode)) {
                continue;
            }
            JsonNode value = submittedValues.get(fieldCode);
            if (isRequiredField(field) && isBlankValue(value)) {
                throw exception(TEST_RESULT_FIELD_INVALID);
            }
            if (value == null || value.isMissingNode() || value.isNull()) {
                continue;
            }
            validateFieldValue(field, value);
        }
    }

    public void assertQcAndEvidenceComplete(LimsTestRequestDO request, List<LimsTestTaskDO> tasks,
                                            Map<Long, List<LimsTaskRawRecordDO>> rawRecordsByTaskId,
                                            Map<Long, List<LimsTaskQcRecordDO>> qcRecordsByTaskId,
                                            Map<Long, List<LimsTaskReviewDO>> reviewsByTaskId) {
        JsonNode snapshot = readObject(resolveWorkflowSnapshot(request));
        List<JsonNode> qcRules = activeArrayItems(snapshot.path("qcRules"));
        List<JsonNode> evidenceRequirements = activeArrayItems(snapshot.path("evidenceRequirements"));
        for (LimsTestTaskDO task : tasks) {
            assertQcRulesSatisfied(task, qcRules, qcRecordsByTaskId.getOrDefault(task.getId(), List.of()));
            assertEvidenceRequirementsSatisfied(task, evidenceRequirements,
                    rawRecordsByTaskId.getOrDefault(task.getId(), List.of()),
                    reviewsByTaskId.getOrDefault(task.getId(), List.of()));
        }
    }

    public void assertExecutionPlanGatesComplete(JsonNode executionPlan, List<LimsTestTaskDO> tasks,
                                                 Map<Long, List<LimsTaskRawRecordDO>> rawRecordsByTaskId,
                                                 Map<Long, List<LimsTaskQcRecordDO>> qcRecordsByTaskId,
                                                 Map<Long, List<LimsTaskReviewDO>> reviewsByTaskId) {
        for (LimsTestTaskDO task : tasks) {
            JsonNode taskPlan = selectTaskPlan(executionPlan, task);
            TaskQualityGateProgress progress = evaluateTaskGate(task,
                    selectArray(taskPlan.path("resultFields"), executionPlan.path("resultFieldPlans")),
                    selectArray(taskPlan.path("qcRules"), executionPlan.path("qcCheckPlans")),
                    selectArray(taskPlan.path("evidenceRequirements"), executionPlan.path("evidenceRequirementPlans")),
                    rawRecordsByTaskId.getOrDefault(task.getId(), List.of()),
                    qcRecordsByTaskId.getOrDefault(task.getId(), List.of()),
                    reviewsByTaskId.getOrDefault(task.getId(), List.of()));
            if (!progress.rawRecordSatisfied()) {
                throw exception(TEST_RESULT_FIELD_INVALID);
            }
            if (!progress.qcSatisfied()) {
                throw exception(TEST_QC_RULE_UNSATISFIED);
            }
            if (!progress.equipmentEvidenceSatisfied()
                    || !progress.personnelEvidenceSatisfied()
                    || !progress.reviewSatisfied()) {
                throw exception(TEST_EVIDENCE_INCOMPLETE);
            }
        }
    }

    public TaskQualityGateProgress evaluateTaskGate(LimsTestTaskDO task, JsonNode resultFields, JsonNode qcRules,
                                                    JsonNode evidenceRequirements,
                                                    List<LimsTaskRawRecordDO> rawRecords,
                                                    List<LimsTaskQcRecordDO> qcRecords,
                                                    List<LimsTaskReviewDO> reviews) {
        List<LimsTaskRawRecordDO> safeRawRecords = safeList(rawRecords);
        List<LimsTaskQcRecordDO> safeQcRecords = safeList(qcRecords);
        List<LimsTaskReviewDO> safeReviews = safeList(reviews);
        List<JsonNode> activeResultFields = activeArrayItems(resultFields);
        List<JsonNode> activeQcRules = activeArrayItems(qcRules);
        List<JsonNode> activeEvidenceRequirements = activeArrayItems(evidenceRequirements);
        ArrayNode missingRequirements = objectMapper.createArrayNode();

        boolean rawRecordRequired = !activeResultFields.isEmpty()
                || hasEvidenceRequirement(activeEvidenceRequirements, "RAW", "RECORD");
        List<JsonNode> missingRequiredFields = missingRequiredResultFields(activeResultFields, safeRawRecords);
        boolean rawRecordSatisfied = (!rawRecordRequired || !safeRawRecords.isEmpty()) && missingRequiredFields.isEmpty();
        if (rawRecordRequired && safeRawRecords.isEmpty()) {
            addMissingRequirement(missingRequirements, "RAW_RECORD", "RAW_RECORD", "原始记录",
                    "结果字段或证据要求需要原始记录，但当前任务还没有原始记录。");
        } else {
            for (JsonNode field : missingRequiredFields) {
                addMissingRequirement(missingRequirements, "RAW_RESULT_FIELD",
                        field.path("fieldCode").asText(field.path("code").asText("RESULT_FIELD")),
                        field.path("fieldName").asText(field.path("name").asText("必填结果字段")),
                        "必填结果字段未在原始记录 rawData.resultValues 中提交有效值。");
            }
        }

        Set<String> approvedRuleCodes = approvedRuleCodes(safeQcRecords);
        int approvedQcRecordCount = (int) safeQcRecords.stream().filter(record -> isApproved(record.getQcResult())).count();
        int satisfiedQcRuleCount = 0;
        for (JsonNode rule : activeQcRules) {
            String ruleCode = rule.path("ruleCode").asText(rule.path("code").asText(""));
            boolean satisfied = StringUtils.hasText(ruleCode) ? approvedRuleCodes.contains(ruleCode) : approvedQcRecordCount > 0;
            if (satisfied) {
                satisfiedQcRuleCount++;
            } else {
                addMissingRequirement(missingRequirements, "QC_RULE",
                        StringUtils.hasText(ruleCode) ? ruleCode : "QC_RULE",
                        rule.path("ruleName").asText(rule.path("name").asText("QC 规则")),
                        "缺少通过的 QC 记录或 QC 记录未覆盖该规则。");
            }
        }
        boolean qcSatisfied = activeQcRules.isEmpty() || satisfiedQcRuleCount == activeQcRules.size();

        boolean equipmentEvidenceRequired = hasEvidenceRequirement(activeEvidenceRequirements, "EQUIPMENT", "CALIBRATION");
        boolean equipmentEvidenceSatisfied = !equipmentEvidenceRequired || hasEquipmentEvidence(task);
        if (!equipmentEvidenceSatisfied) {
            addMissingEvidenceRequirements(missingRequirements, activeEvidenceRequirements,
                    "EQUIPMENT_EVIDENCE", "设备证据", "设备/校准证据要求未满足。");
        }

        boolean personnelEvidenceRequired = hasEvidenceRequirement(activeEvidenceRequirements, "PERSON", "PERSONNEL");
        boolean personnelEvidenceSatisfied = !personnelEvidenceRequired || hasPersonnelEvidence(task);
        if (!personnelEvidenceSatisfied) {
            addMissingEvidenceRequirements(missingRequirements, activeEvidenceRequirements,
                    "PERSONNEL_EVIDENCE", "人员证据", "人员授权证据要求未满足。");
        }

        int approvedReviewCount = (int) safeReviews.stream().filter(review -> isApproved(review.getReviewStatus())).count();
        boolean reviewSatisfied = LimsTaskReviewStatus.APPROVED.equalsIgnoreCase(task.getReviewStatus())
                || approvedReviewCount > 0;
        if (!reviewSatisfied) {
            addMissingRequirement(missingRequirements, "TECH_REVIEW", "TECH_REVIEW", "技术复核",
                    "任务还没有通过技术复核。");
        }

        return new TaskQualityGateProgress(
                safeRawRecords.size(),
                safeQcRecords.size(),
                approvedQcRecordCount,
                safeReviews.size(),
                approvedReviewCount,
                activeQcRules.size(),
                satisfiedQcRuleCount,
                activeEvidenceRequirements.size(),
                missingRequirements.size(),
                rawRecordSatisfied,
                qcSatisfied,
                equipmentEvidenceSatisfied,
                personnelEvidenceSatisfied,
                reviewSatisfied,
                rawRecordSatisfied && qcSatisfied && equipmentEvidenceSatisfied && personnelEvidenceSatisfied && reviewSatisfied,
                missingRequirements);
    }

    private void assertQcRulesSatisfied(LimsTestTaskDO task, List<JsonNode> qcRules, List<LimsTaskQcRecordDO> qcRecords) {
        if (qcRules.isEmpty()) {
            return;
        }
        Set<String> approvedRuleCodes = new HashSet<>();
        for (LimsTaskQcRecordDO record : qcRecords) {
            if (!isApproved(record.getQcResult())) {
                continue;
            }
            collectRuleCodes(record.getQcRuleSnapshot(), approvedRuleCodes);
            collectRuleCodes(record.getQcDataJson(), approvedRuleCodes);
        }
        for (JsonNode rule : qcRules) {
            String ruleCode = rule.path("ruleCode").asText("");
            if (StringUtils.hasText(ruleCode) && !approvedRuleCodes.contains(ruleCode)) {
                throw exception(TEST_QC_RULE_UNSATISFIED);
            }
        }
    }

    private void assertEvidenceRequirementsSatisfied(LimsTestTaskDO task, List<JsonNode> requirements,
                                                     List<LimsTaskRawRecordDO> rawRecords,
                                                     List<LimsTaskReviewDO> reviews) {
        if (requirements.isEmpty()) {
            return;
        }
        for (JsonNode requirement : requirements) {
            if (!isRequiredEvidence(requirement)) {
                continue;
            }
            String evidenceType = requirement.path("evidenceType").asText("").toUpperCase(Locale.ROOT);
            String sourceType = requirement.path("sourceType").asText("").toUpperCase(Locale.ROOT);
            if (requiresRawData(evidenceType, sourceType) && rawRecords.isEmpty()) {
                throw exception(TEST_EVIDENCE_INCOMPLETE);
            }
            if (requiresEquipmentEvidence(evidenceType, sourceType) && !hasEquipmentEvidence(task)) {
                throw exception(TEST_EVIDENCE_INCOMPLETE);
            }
            if (requiresPersonnelEvidence(evidenceType, sourceType) && !hasPersonnelEvidence(task)) {
                throw exception(TEST_EVIDENCE_INCOMPLETE);
            }
            if (requiresReviewEvidence(evidenceType, sourceType) && !hasApprovedReview(reviews)) {
                throw exception(TEST_EVIDENCE_INCOMPLETE);
            }
        }
    }

    private JsonNode selectTaskPlan(JsonNode plan, LimsTestTaskDO task) {
        JsonNode taskPlans = plan.path("taskPlans");
        if (!taskPlans.isArray()) {
            return objectMapper.createObjectNode();
        }
        for (JsonNode taskPlan : taskPlans) {
            if (matchesTaskPlan(taskPlan, task)) {
                return taskPlan;
            }
        }
        return objectMapper.createObjectNode();
    }

    private boolean matchesTaskPlan(JsonNode taskPlan, LimsTestTaskDO task) {
        return sameText(task.getTestItem(), taskPlan.path("itemCode").asText(""))
                || sameText(task.getTestItem(), taskPlan.path("itemName").asText(""))
                || sameText(task.getTaskName(), taskPlan.path("itemName").asText(""))
                || sameText(task.getMethodCode(), taskPlan.path("methodCode").asText(""))
                || sameText(task.getMethodName(), taskPlan.path("methodName").asText(""));
    }

    private boolean sameText(String left, String right) {
        return StringUtils.hasText(left) && StringUtils.hasText(right) && left.equalsIgnoreCase(right);
    }

    private JsonNode selectArray(JsonNode primary, JsonNode fallback) {
        if (primary != null && primary.isArray() && primary.size() > 0) {
            return copyArray(primary);
        }
        return copyArray(fallback);
    }

    private ArrayNode copyArray(JsonNode node) {
        ArrayNode array = objectMapper.createArrayNode();
        if (node != null && node.isArray()) {
            node.forEach(item -> array.add(item.deepCopy()));
        }
        return array;
    }

    private List<JsonNode> resultFieldsForTask(JsonNode snapshot, LimsTestTaskDO task) {
        Set<String> itemCodes = itemCodesForTask(snapshot, task);
        return activeArrayItems(snapshot.path("resultFields")).stream()
                .filter(field -> {
                    String itemCode = field.path("itemCode").asText("");
                    return !StringUtils.hasText(itemCode) || itemCodes.isEmpty() || itemCodes.contains(itemCode);
                })
                .toList();
    }

    private Set<String> itemCodesForTask(JsonNode snapshot, LimsTestTaskDO task) {
        Set<String> itemCodes = new HashSet<>();
        String testItem = task == null ? "" : task.getTestItem();
        if (!StringUtils.hasText(testItem)) {
            return itemCodes;
        }
        for (JsonNode item : activeArrayItems(snapshot.path("testItems"))) {
            String itemCode = item.path("itemCode").asText("");
            String itemName = item.path("itemName").asText(item.path("name").asText(""));
            if (testItem.equalsIgnoreCase(itemCode) || testItem.equalsIgnoreCase(itemName)) {
                itemCodes.add(itemCode);
            }
        }
        return itemCodes;
    }

    private Map<String, JsonNode> submittedValuesByFieldCode(String rawData) {
        Map<String, JsonNode> values = new HashMap<>();
        JsonNode resultValues = readObject(rawData).path("resultValues");
        if (!resultValues.isArray()) {
            return values;
        }
        for (JsonNode value : resultValues) {
            String fieldCode = value.path("fieldCode").asText("");
            if (StringUtils.hasText(fieldCode)) {
                values.put(fieldCode, value);
            }
        }
        return values;
    }

    private List<JsonNode> missingRequiredResultFields(List<JsonNode> resultFields, List<LimsTaskRawRecordDO> rawRecords) {
        if (rawRecords.isEmpty()) {
            return resultFields.stream()
                    .filter(this::isRequiredField)
                    .toList();
        }
        Set<String> submittedFieldCodes = submittedFieldCodes(rawRecords);
        return resultFields.stream()
                .filter(this::isRequiredField)
                .filter(field -> {
                    String fieldCode = field.path("fieldCode").asText(field.path("code").asText(""));
                    return StringUtils.hasText(fieldCode) && !submittedFieldCodes.contains(fieldCode);
                })
                .toList();
    }

    private Set<String> submittedFieldCodes(List<LimsTaskRawRecordDO> rawRecords) {
        Set<String> fieldCodes = new HashSet<>();
        for (LimsTaskRawRecordDO record : rawRecords) {
            JsonNode root = readObject(record.getRecordJson());
            collectSubmittedFieldCodes(root.path("resultValues"), fieldCodes);
            collectSubmittedFieldCodes(parseMaybeJson(root.path("rawData")).path("resultValues"), fieldCodes);
            collectSubmittedFieldCodes(root.path("measurements"), fieldCodes);
        }
        return fieldCodes;
    }

    private void collectSubmittedFieldCodes(JsonNode values, Set<String> fieldCodes) {
        if (!values.isArray()) {
            return;
        }
        for (JsonNode value : values) {
            String fieldCode = value.path("fieldCode").asText(value.path("code").asText(""));
            if (StringUtils.hasText(fieldCode) && hasSubmittedValue(value)) {
                fieldCodes.add(fieldCode);
            }
        }
    }

    private boolean hasSubmittedValue(JsonNode value) {
        return hasTextValue(value.path("fieldValue")) || hasTextValue(value.path("value"))
                || hasTextValue(value.path("displayValue")) || hasTextValue(value.path("resultValue"));
    }

    private boolean hasTextValue(JsonNode value) {
        return value != null && !value.isMissingNode() && !value.isNull() && StringUtils.hasText(value.asText());
    }

    private JsonNode parseMaybeJson(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return objectMapper.createObjectNode();
        }
        if (node.isTextual()) {
            return readObject(node.asText());
        }
        return node;
    }

    private void validateFieldValue(JsonNode field, JsonNode value) {
        String fieldValue = value.path("fieldValue").asText(value.path("value").asText(""));
        String fieldType = field.path("fieldType").asText("");
        if (isNumberType(fieldType)) {
            BigDecimal numericValue = parseNumber(fieldValue);
            validateMinMax(field, numericValue);
        }
        JsonNode enumOptions = resolveEnumOptions(field.path("enumOptions"));
        if (enumOptions.isArray() && enumOptions.size() > 0 && !containsEnumValue(enumOptions, fieldValue)) {
            throw exception(TEST_RESULT_FIELD_INVALID);
        }
    }

    private JsonNode resolveEnumOptions(JsonNode enumOptions) {
        if (enumOptions == null || enumOptions.isMissingNode() || enumOptions.isNull()) {
            return objectMapper.createArrayNode();
        }
        if (enumOptions.isArray()) {
            return enumOptions;
        }
        if (enumOptions.isTextual() && StringUtils.hasText(enumOptions.asText())) {
            JsonNode parsed = readObject(enumOptions.asText());
            return parsed.isArray() ? parsed : objectMapper.createArrayNode();
        }
        return objectMapper.createArrayNode();
    }

    private void validateMinMax(JsonNode field, BigDecimal value) {
        String minValue = field.path("minValue").asText("");
        if (StringUtils.hasText(minValue) && value.compareTo(parseNumber(minValue)) < 0) {
            throw exception(TEST_RESULT_FIELD_INVALID);
        }
        String maxValue = field.path("maxValue").asText("");
        if (StringUtils.hasText(maxValue) && value.compareTo(parseNumber(maxValue)) > 0) {
            throw exception(TEST_RESULT_FIELD_INVALID);
        }
    }

    private BigDecimal parseNumber(String value) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException ex) {
            throw exception(TEST_RESULT_FIELD_INVALID);
        }
    }

    private boolean containsEnumValue(JsonNode enumOptions, String fieldValue) {
        for (JsonNode option : enumOptions) {
            String configured = option.isObject() ? option.path("value").asText(option.path("label").asText("")) : option.asText("");
            if (fieldValue.equals(configured)) {
                return true;
            }
        }
        return false;
    }

    private Set<String> approvedRuleCodes(List<LimsTaskQcRecordDO> qcRecords) {
        Set<String> ruleCodes = new HashSet<>();
        for (LimsTaskQcRecordDO record : qcRecords) {
            if (!isApproved(record.getQcResult())) {
                continue;
            }
            collectRuleCodes(record.getQcRuleSnapshot(), ruleCodes);
            collectRuleCodes(record.getQcDataJson(), ruleCodes);
        }
        return ruleCodes;
    }

    private void collectRuleCodes(String json, Set<String> ruleCodes) {
        JsonNode root = readObject(json);
        collectRuleCode(root, ruleCodes);
        JsonNode rules = root.path("rules");
        if (rules.isArray()) {
            rules.forEach(rule -> collectRuleCode(rule, ruleCodes));
        }
        JsonNode ruleCodesNode = root.path("ruleCodes");
        if (ruleCodesNode.isArray()) {
            ruleCodesNode.forEach(rule -> addRuleCode(rule.asText(""), ruleCodes));
        }
    }

    private void collectRuleCode(JsonNode node, Set<String> ruleCodes) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return;
        }
        if (node.isTextual()) {
            addRuleCode(node.asText(), ruleCodes);
            return;
        }
        addRuleCode(node.path("ruleCode").asText(""), ruleCodes);
        addRuleCode(node.path("code").asText(""), ruleCodes);
    }

    private void addRuleCode(String ruleCode, Set<String> ruleCodes) {
        if (StringUtils.hasText(ruleCode)) {
            ruleCodes.add(ruleCode);
        }
    }

    private boolean hasEvidenceRequirement(List<JsonNode> requirements, String evidenceToken, String sourceToken) {
        for (JsonNode requirement : requirements) {
            if (!isRequiredEvidence(requirement)) {
                continue;
            }
            String evidenceType = requirement.path("evidenceType").asText("").toUpperCase(Locale.ROOT);
            String sourceType = requirement.path("sourceType").asText("").toUpperCase(Locale.ROOT);
            if (evidenceType.contains(evidenceToken) || sourceType.contains(evidenceToken)
                    || evidenceType.contains(sourceToken) || sourceType.contains(sourceToken)) {
                return true;
            }
        }
        return false;
    }

    private void addMissingEvidenceRequirements(ArrayNode missingRequirements, List<JsonNode> evidenceRequirements,
                                                String type, String fallbackName, String message) {
        boolean added = false;
        for (JsonNode requirement : evidenceRequirements) {
            if (!isRequiredEvidence(requirement)) {
                continue;
            }
            String evidenceType = requirement.path("evidenceType").asText("").toUpperCase(Locale.ROOT);
            String sourceType = requirement.path("sourceType").asText("").toUpperCase(Locale.ROOT);
            boolean matchesEquipment = "EQUIPMENT_EVIDENCE".equals(type)
                    && (evidenceType.contains("EQUIPMENT") || evidenceType.contains("CALIBRATION")
                    || sourceType.contains("EQUIPMENT") || sourceType.contains("CALIBRATION"));
            boolean matchesPersonnel = "PERSONNEL_EVIDENCE".equals(type)
                    && (evidenceType.contains("PERSON") || sourceType.contains("PERSONNEL"));
            if (!matchesEquipment && !matchesPersonnel) {
                continue;
            }
            addMissingRequirement(missingRequirements, type,
                    requirement.path("requirementCode").asText(requirement.path("code").asText(type)),
                    requirement.path("requirementName").asText(requirement.path("name").asText(fallbackName)),
                    message);
            added = true;
        }
        if (!added) {
            addMissingRequirement(missingRequirements, type, type, fallbackName, message);
        }
    }

    private void addMissingRequirement(ArrayNode missingRequirements, String type, String code, String name, String message) {
        ObjectNode missing = missingRequirements.addObject();
        missing.put("type", type);
        missing.put("code", code);
        missing.put("name", name);
        missing.put("message", message);
    }

    private boolean hasEquipmentEvidence(LimsTestTaskDO task) {
        JsonNode evidence = readObject(task.getEquipmentEvidenceSnapshot());
        return evidence.isArray() && evidence.size() > 0;
    }

    private boolean hasPersonnelEvidence(LimsTestTaskDO task) {
        JsonNode evidence = readObject(task.getPersonnelEvidenceSnapshot());
        return evidence.isArray() && evidence.size() > 0;
    }

    private boolean hasApprovedReview(List<LimsTaskReviewDO> reviews) {
        return reviews.stream().anyMatch(review -> LimsTaskReviewStatus.APPROVED.equalsIgnoreCase(review.getReviewStatus()));
    }

    private boolean requiresRawData(String evidenceType, String sourceType) {
        return evidenceType.contains("RAW") || sourceType.contains("RAW") || sourceType.contains("RECORD");
    }

    private boolean requiresEquipmentEvidence(String evidenceType, String sourceType) {
        return evidenceType.contains("EQUIPMENT") || evidenceType.contains("CALIBRATION") || sourceType.contains("EQUIPMENT");
    }

    private boolean requiresPersonnelEvidence(String evidenceType, String sourceType) {
        return evidenceType.contains("PERSON") || sourceType.contains("PERSONNEL");
    }

    private boolean requiresReviewEvidence(String evidenceType, String sourceType) {
        return evidenceType.contains("REVIEW") || sourceType.contains("REVIEW");
    }

    private boolean isApproved(String value) {
        if (!StringUtils.hasText(value)) {
            return false;
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        return "approved".equals(normalized) || "pass".equals(normalized)
                || "passed".equals(normalized) || "通过".equals(value.trim()) || "合格".equals(value.trim());
    }

    private boolean isRequiredField(JsonNode node) {
        return node.has("required") && node.path("required").asBoolean(false);
    }

    private boolean isRequiredEvidence(JsonNode node) {
        return !node.has("required") || node.path("required").asBoolean(true);
    }

    private boolean isBlankValue(JsonNode value) {
        if (value == null || value.isMissingNode() || value.isNull()) {
            return true;
        }
        String fieldValue = value.path("fieldValue").asText(value.path("value").asText(""));
        return !StringUtils.hasText(fieldValue);
    }

    private boolean isNumberType(String fieldType) {
        return "number".equalsIgnoreCase(fieldType) || "decimal".equalsIgnoreCase(fieldType)
                || "integer".equalsIgnoreCase(fieldType);
    }

    private List<JsonNode> activeArrayItems(JsonNode node) {
        if (!node.isArray()) {
            return List.of();
        }
        return java.util.stream.StreamSupport.stream(node.spliterator(), false)
                .filter(item -> !"disabled".equalsIgnoreCase(item.path("status").asText(""))
                        && !"inactive".equalsIgnoreCase(item.path("status").asText("")))
                .toList();
    }

    private <T> List<T> safeList(List<T> values) {
        return values == null ? List.of() : values;
    }

    private String resolveWorkflowSnapshot(LimsTestRequestDO request) {
        return StringUtils.hasText(request.getWorkflowSnapshot()) ? request.getWorkflowSnapshot() : request.getScenarioConfig();
    }

    private JsonNode readObject(String json) {
        if (!StringUtils.hasText(json)) {
            return objectMapper.createObjectNode();
        }
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException ex) {
            return objectMapper.createObjectNode();
        }
    }

    public record TaskQualityGateProgress(Integer rawRecordCount,
                                          Integer qcRecordCount,
                                          Integer approvedQcRecordCount,
                                          Integer reviewRecordCount,
                                          Integer approvedReviewCount,
                                          Integer qcRuleCount,
                                          Integer satisfiedQcRuleCount,
                                          Integer evidenceRequirementCount,
                                          Integer missingRequirementCount,
                                          Boolean rawRecordSatisfied,
                                          Boolean qcSatisfied,
                                          Boolean equipmentEvidenceSatisfied,
                                          Boolean personnelEvidenceSatisfied,
                                          Boolean reviewSatisfied,
                                          Boolean qualityGateSatisfied,
                                          JsonNode missingRequirements) {
    }

}
