package cn.iocoder.yudao.module.lims.service.workflow;

import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskQcRecordDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskRawRecordDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskReviewDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestRequestDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestTaskDO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
            if (requiresReviewEvidence(evidenceType, sourceType) && !hasApprovedReview(reviews)) {
                throw exception(TEST_EVIDENCE_INCOMPLETE);
            }
        }
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

    private boolean hasEquipmentEvidence(LimsTestTaskDO task) {
        JsonNode evidence = readObject(task.getEquipmentEvidenceSnapshot());
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

}
