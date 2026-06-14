package cn.iocoder.yudao.module.lims.service.workflow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
        plan.set("sampleRequirements", copyArray(snapshot.path("sampleRequirements")));
        plan.set("taskPlans", createTaskPlans(snapshot.path("testItems")));
        plan.set("resultFieldPlans", copyArray(snapshot.path("resultFields")));
        plan.set("qcCheckPlans", copyArray(snapshot.path("qcRules")));
        plan.set("evidenceRequirementPlans", copyArray(snapshot.path("evidenceRequirements")));
        plan.set("reportDraftPlan", reportDraftPlanFactory.createReportDraftPlan(snapshot));
        return plan.toString();
    }

    private ArrayNode createTaskPlans(JsonNode testItems) {
        ArrayNode tasks = objectMapper.createArrayNode();
        if (!testItems.isArray()) {
            return tasks;
        }
        for (JsonNode item : testItems) {
            ObjectNode task = tasks.addObject();
            task.put("itemCode", item.path("itemCode").asText(""));
            task.put("itemName", item.path("itemName").asText(item.path("name").asText("常规检测")));
            task.put("methodCode", item.path("methodCode").asText("METHOD"));
            task.put("methodName", item.path("methodName").asText("配置方法"));
            task.put("standardCode", item.path("standardCode").asText(""));
            task.put("status", "planned");
        }
        return tasks;
    }

    private ArrayNode copyArray(JsonNode node) {
        ArrayNode array = objectMapper.createArrayNode();
        if (node != null && node.isArray()) {
            node.forEach(array::add);
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

}
