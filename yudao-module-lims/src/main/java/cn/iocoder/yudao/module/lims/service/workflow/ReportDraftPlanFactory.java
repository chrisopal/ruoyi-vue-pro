package cn.iocoder.yudao.module.lims.service.workflow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

@Service
public class ReportDraftPlanFactory {

    private final ObjectMapper objectMapper;

    public ReportDraftPlanFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ObjectNode createReportDraftPlan(JsonNode workflowSnapshot) {
        JsonNode templateSchema = workflowSnapshot.path("template");
        ObjectNode plan = objectMapper.createObjectNode();
        plan.putNull("templateId");
        plan.put("templateVersion", workflowSnapshot.path("packVersion").asText(""));
        plan.set("templateCodes", copyArray(templateSchema.path("templates")));
        plan.set("outputFormats", createOutputFormats(templateSchema));
        plan.set("sections", copyArray(workflowSnapshot.path("reportSections")));
        plan.set("sectionRules", copyArray(templateSchema.path("reportSections")));
        plan.set("dataBindings", createDataBindings(workflowSnapshot.path("resultFields")));
        plan.set("evidenceRequirements", copyArray(workflowSnapshot.path("evidenceRequirements")));
        plan.set("templateSchema", templateSchema.deepCopy());
        return plan;
    }

    private ArrayNode createOutputFormats(JsonNode templateSchema) {
        ArrayNode configured = copyArray(templateSchema.path("outputFormats"));
        if (configured.size() > 0) {
            return configured;
        }
        ArrayNode formats = objectMapper.createArrayNode();
        formats.add("WORD");
        formats.add("PDF");
        formats.add("EXCEL");
        return formats;
    }

    private ArrayNode createDataBindings(JsonNode resultFields) {
        ArrayNode bindings = objectMapper.createArrayNode();
        if (!resultFields.isArray()) {
            return bindings;
        }
        for (JsonNode field : resultFields) {
            ObjectNode binding = bindings.addObject();
            binding.put("fieldCode", field.path("fieldCode").asText(""));
            binding.put("fieldName", field.path("fieldName").asText(""));
            binding.put("sourcePath", "resultValues." + field.path("fieldCode").asText(""));
            binding.put("required", field.path("required").asBoolean(false));
        }
        return bindings;
    }

    private ArrayNode copyArray(JsonNode node) {
        ArrayNode array = objectMapper.createArrayNode();
        if (node != null && node.isArray()) {
            node.forEach(array::add);
        }
        return array;
    }

}
