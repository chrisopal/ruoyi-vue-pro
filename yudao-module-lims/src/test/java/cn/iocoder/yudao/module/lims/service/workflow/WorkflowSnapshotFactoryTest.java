package cn.iocoder.yudao.module.lims.service.workflow;

import cn.iocoder.yudao.module.lab.service.domainpack.dto.LabDomainPackSnapshotDTO;
import cn.iocoder.yudao.module.lims.service.workflow.model.WorkflowSnapshot;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorkflowSnapshotFactoryTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WorkflowSnapshotFactory factory = new WorkflowSnapshotFactory(objectMapper);

    @Test
    void createSnapshot_shouldContainPackVersionAndAllConfigSections() throws Exception {
        LabDomainPackSnapshotDTO pack = publishedPackSnapshot();

        WorkflowSnapshot snapshot = factory.createSnapshot(pack, "{\"channel\":\"third_party_order\"}");

        JsonNode json = objectMapper.readTree(snapshot.getSnapshotJson());
        assertEquals(1L, json.path("domainPackId").asLong());
        assertEquals("FOOD_ROUTINE", json.path("packCode").asText());
        assertEquals("1.0", json.path("packVersion").asText());
        assertEquals("REPORT_BASIC_V1", json.path("template").path("templates").get(0).asText());
        assertTrue(json.path("workflowNodes").isArray());
        assertTrue(json.path("sampleRequirements").isArray());
        assertTrue(json.path("testItems").isArray());
        assertTrue(json.path("resultFields").isArray());
        assertTrue(json.path("qcRules").isArray());
        assertTrue(json.path("reportSections").isArray());
        assertTrue(json.path("evidenceRequirements").isArray());
        assertEquals("third_party_order", json.path("override").path("channel").asText());
        assertEquals(sha256(snapshot.getSnapshotJson()), snapshot.getSnapshotHash());
    }

    private static LabDomainPackSnapshotDTO publishedPackSnapshot() {
        LabDomainPackSnapshotDTO pack = new LabDomainPackSnapshotDTO();
        pack.setDomainPackId(1L);
        pack.setPackCode("FOOD_ROUTINE");
        pack.setPackName("食品常规检测方案包");
        pack.setPackVersion("1.0");
        pack.setIndustry("食品");
        pack.setTemplateSchema("{\"templates\":[\"REPORT_BASIC_V1\"],\"outputFormats\":[\"WORD\",\"PDF\"]}");
        pack.setWorkflowNodes(List.of(workflowNode()));
        pack.setSampleRequirements(List.of(sampleRequirement()));
        pack.setTestItems(List.of(testItem()));
        pack.setResultFields(List.of(resultField()));
        pack.setQcRules(List.of(qcRule()));
        pack.setReportSections(List.of(reportSection()));
        pack.setEvidenceRequirements(List.of(evidenceRequirement()));
        return pack;
    }

    private static LabDomainPackSnapshotDTO.WorkflowNode workflowNode() {
        LabDomainPackSnapshotDTO.WorkflowNode node = new LabDomainPackSnapshotDTO.WorkflowNode();
        node.setNodeCode("sample_receive");
        node.setNodeName("样品接收");
        node.setRequired(true);
        node.setSort(10);
        node.setStatus("active");
        return node;
    }

    private static LabDomainPackSnapshotDTO.SampleRequirement sampleRequirement() {
        LabDomainPackSnapshotDTO.SampleRequirement item = new LabDomainPackSnapshotDTO.SampleRequirement();
        item.setRequirementCode("SAMPLE_QTY");
        item.setRequirementName("样品量");
        item.setRequirementText(">= 500g");
        item.setSort(10);
        item.setStatus("active");
        return item;
    }

    private static LabDomainPackSnapshotDTO.TestItem testItem() {
        LabDomainPackSnapshotDTO.TestItem item = new LabDomainPackSnapshotDTO.TestItem();
        item.setItemCode("PH");
        item.setItemName("pH");
        item.setMethodCode("GB6920");
        item.setMethodName("玻璃电极法");
        item.setSort(10);
        item.setStatus("active");
        return item;
    }

    private static LabDomainPackSnapshotDTO.ResultField resultField() {
        LabDomainPackSnapshotDTO.ResultField field = new LabDomainPackSnapshotDTO.ResultField();
        field.setItemCode("PH");
        field.setFieldCode("PH_VALUE");
        field.setFieldName("pH值");
        field.setFieldType("number");
        field.setRequired(true);
        field.setSort(10);
        field.setStatus("active");
        return field;
    }

    private static LabDomainPackSnapshotDTO.QcRule qcRule() {
        LabDomainPackSnapshotDTO.QcRule rule = new LabDomainPackSnapshotDTO.QcRule();
        rule.setRuleCode("BLANK");
        rule.setRuleName("空白样");
        rule.setAcceptanceCriteria("每批至少 1 个");
        rule.setSort(10);
        rule.setStatus("active");
        return rule;
    }

    private static LabDomainPackSnapshotDTO.ReportSection reportSection() {
        LabDomainPackSnapshotDTO.ReportSection section = new LabDomainPackSnapshotDTO.ReportSection();
        section.setSectionCode("RESULTS");
        section.setSectionName("检测结果");
        section.setSourceType("result_values");
        section.setVisible(true);
        section.setSort(10);
        section.setStatus("active");
        return section;
    }

    private static LabDomainPackSnapshotDTO.EvidenceRequirement evidenceRequirement() {
        LabDomainPackSnapshotDTO.EvidenceRequirement item = new LabDomainPackSnapshotDTO.EvidenceRequirement();
        item.setRequirementCode("EQUIPMENT_CERT");
        item.setRequirementName("设备校准证书");
        item.setEvidenceType("EQUIPMENT_CERTIFICATE");
        item.setSourceType("equipment");
        item.setClauseCategory("equipment");
        item.setRequired(true);
        item.setSort(10);
        item.setStatus("active");
        return item;
    }

    private static String sha256(String value) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
    }

}
