package cn.iocoder.yudao.module.lims.service.workflow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExecutionPlanFactoryTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ReportDraftPlanFactory reportDraftPlanFactory = new ReportDraftPlanFactory(objectMapper);
    private final ExecutionPlanFactory factory = new ExecutionPlanFactory(objectMapper, reportDraftPlanFactory);

    @Test
    void createPlanJson_shouldMaterializePackRulesForExecutionAndReportDrafting() throws Exception {
        JsonNode plan = objectMapper.readTree(factory.createPlanJson(snapshotWithAllSections()));
        JsonNode taskPlan = plan.path("taskPlans").get(0);
        JsonNode reportDraftPlan = plan.path("reportDraftPlan");

        assertEquals(1L, plan.path("domainPackId").asLong());
        assertEquals("FOOD_ROUTINE", plan.path("packCode").asText());
        assertEquals("1.0", plan.path("packVersion").asText());
        assertEquals("sample_receive", plan.path("workflowNodes").get(0).path("nodeCode").asText());
        assertEquals("SAMPLE_QTY", plan.path("sampleRequirements").get(0).path("requirementCode").asText());

        assertEquals("PH", taskPlan.path("itemCode").asText());
        assertEquals("PH_VALUE", taskPlan.path("resultFields").get(0).path("fieldCode").asText());
        assertEquals(1, taskPlan.path("resultFields").size());
        assertEquals("BLANK", taskPlan.path("qcRules").get(0).path("ruleCode").asText());
        assertEquals("EQUIPMENT_CERT", taskPlan.path("evidenceRequirements").get(0).path("requirementCode").asText());
        assertEquals("RESULTS", taskPlan.path("reportSections").get(0).path("sectionCode").asText());
        assertTrue(taskPlan.path("sampleRequirements").isArray());

        assertEquals("FOOD_ROUTINE", reportDraftPlan.path("packCode").asText());
        assertEquals("REPORT_BASIC_V1", reportDraftPlan.path("templateCodes").get(0).asText());
        assertEquals("PH_VALUE", reportDraftPlan.path("resultFieldRules").get(0).path("fieldCode").asText());
        assertEquals("BLANK", reportDraftPlan.path("qcRules").get(0).path("ruleCode").asText());
        assertEquals("EQUIPMENT_CERT", reportDraftPlan.path("evidenceRequirements").get(0).path("requirementCode").asText());
        assertEquals("RESULTS", reportDraftPlan.path("sections").get(0).path("sectionCode").asText());
    }

    private static String snapshotWithAllSections() {
        return """
                {
                  "domainPackId": 1,
                  "packCode": "FOOD_ROUTINE",
                  "packVersion": "1.0",
                  "frozenAt": "2026-06-15T01:00:00",
                  "workflowNodes": [
                    {"nodeCode": "sample_receive", "nodeName": "样品接收"}
                  ],
                  "sampleRequirements": [
                    {"requirementCode": "SAMPLE_QTY", "requirementName": "样品量", "requirementText": ">= 500g"}
                  ],
                  "testItems": [
                    {"itemCode": "PH", "itemName": "pH", "methodCode": "GB6920", "methodName": "玻璃电极法"}
                  ],
                  "resultFields": [
                    {"itemCode": "PH", "fieldCode": "PH_VALUE", "fieldName": "pH值", "fieldType": "number"},
                    {"itemCode": "COD", "fieldCode": "COD_VALUE", "fieldName": "COD", "fieldType": "number"}
                  ],
                  "qcRules": [
                    {"ruleCode": "BLANK", "ruleName": "空白样", "acceptanceCriteria": "每批至少 1 个"}
                  ],
                  "reportSections": [
                    {"sectionCode": "RESULTS", "sectionName": "检测结果", "sourceType": "result_values"}
                  ],
                  "evidenceRequirements": [
                    {"requirementCode": "EQUIPMENT_CERT", "requirementName": "设备校准证书", "evidenceType": "EQUIPMENT_CERTIFICATE"}
                  ],
                  "template": {
                    "templates": ["REPORT_BASIC_V1"],
                    "outputFormats": ["WORD", "PDF"],
                    "reportSections": [
                      {"sectionCode": "RESULTS", "sectionName": "检测结果", "sourceType": "result_values"}
                    ]
                  }
                }
                """;
    }

}
