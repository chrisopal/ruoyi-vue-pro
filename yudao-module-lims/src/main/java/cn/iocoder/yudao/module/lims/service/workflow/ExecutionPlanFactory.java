package cn.iocoder.yudao.module.lims.service.workflow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
public class ExecutionPlanFactory {

    private final ObjectMapper objectMapper;
    private final ReportDraftPlanFactory reportDraftPlanFactory;

    public ExecutionPlanFactory(ObjectMapper objectMapper, ReportDraftPlanFactory reportDraftPlanFactory) {
        this.objectMapper = objectMapper;
        this.reportDraftPlanFactory = reportDraftPlanFactory;
    }

    public String createPlanJson(String workflowSnapshotJson) {
        JsonNode snapshot = readObject(workflowSnapshotJson);
        ObjectNode plan = objectMapper.createObjectNode();
        plan.put("domainPackId", snapshot.path("domainPackId").asLong());
        plan.put("packCode", snapshot.path("packCode").asText(""));
        plan.put("packVersion", snapshot.path("packVersion").asText(""));
        plan.put("workflowSnapshotHash", sha256(workflowSnapshotJson));
        plan.put("frozenAt", snapshot.path("frozenAt").asText(""));
        plan.set("workflowNodes", copyArray(snapshot.path("workflowNodes")));
        plan.set("sampleRequirements", copyArray(snapshot.path("sampleRequirements")));
        plan.set("taskPlans", createTaskPlans(snapshot));
        plan.set("resultFieldPlans", copyArray(snapshot.path("resultFields")));
        plan.set("qcCheckPlans", copyArray(snapshot.path("qcRules")));
        plan.set("evidenceRequirementPlans", copyArray(snapshot.path("evidenceRequirements")));
        plan.set("reportDraftPlan", reportDraftPlanFactory.createReportDraftPlan(snapshot));
        return plan.toString();
    }

    private ArrayNode createTaskPlans(JsonNode snapshot) {
        JsonNode testItems = snapshot.path("testItems");
        ArrayNode tasks = objectMapper.createArrayNode();
        if (!testItems.isArray()) {
            return tasks;
        }
        for (JsonNode item : testItems) {
            ObjectNode task = tasks.addObject();
            String itemCode = item.path("itemCode").asText("");
            task.put("itemCode", itemCode);
            task.put("itemName", item.path("itemName").asText(item.path("name").asText("常规检测")));
            task.put("methodCode", item.path("methodCode").asText("METHOD"));
            task.put("methodName", item.path("methodName").asText("配置方法"));
            task.put("standardCode", item.path("standardCode").asText(""));
            task.set("resultFields", filterByItemCode(snapshot.path("resultFields"), itemCode));
            task.set("qcRules", filterByItemCode(snapshot.path("qcRules"), itemCode));
            task.set("evidenceRequirements", filterByItemCode(snapshot.path("evidenceRequirements"), itemCode));
            task.set("reportSections", copyArray(snapshot.path("reportSections")));
            task.set("sampleRequirements", copyArray(snapshot.path("sampleRequirements")));
            task.put("status", "planned");
        }
        return tasks;
    }

    private ArrayNode filterByItemCode(JsonNode node, String itemCode) {
        ArrayNode array = objectMapper.createArrayNode();
        if (node == null || !node.isArray()) {
            return array;
        }
        for (JsonNode item : node) {
            String configuredItemCode = item.path("itemCode").asText("");
            if (!StringUtils.hasText(configuredItemCode) || !StringUtils.hasText(itemCode) || itemCode.equals(configuredItemCode)) {
                array.add(item.deepCopy());
            }
        }
        return array;
    }

    private ArrayNode copyArray(JsonNode node) {
        ArrayNode array = objectMapper.createArrayNode();
        if (node != null && node.isArray()) {
            node.forEach(item -> array.add(item.deepCopy()));
        }
        return array;
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

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest((value == null ? "" : value).getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm is unavailable", ex);
        }
    }

}
