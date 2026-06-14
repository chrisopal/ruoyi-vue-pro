package cn.iocoder.yudao.module.lims.service.workflow;

import cn.iocoder.yudao.module.lims.service.workflow.model.ReportOutputBundle;
import cn.iocoder.yudao.module.lims.service.workflow.model.ReportOutputRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportOutputGeneratorTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ReportOutputGenerator generator = new ReportOutputGenerator(objectMapper);

    @TempDir
    private Path outputDir;

    @BeforeEach
    void setUp() {
        System.setProperty(ReportOutputGenerator.OUTPUT_DIR_PROPERTY, outputDir.toString());
    }

    @AfterEach
    void tearDown() {
        System.clearProperty(ReportOutputGenerator.OUTPUT_DIR_PROPERTY);
    }

    @Test
    void generate_shouldCreateConfiguredArtifactsAndPreferPdf() {
        ObjectNode draftPlan = objectMapper.createObjectNode();
        draftPlan.putArray("outputFormats").add("WORD").add("PDF").add("EXCEL");
        String reportContent = """
                {
                  "requestNo": "REQ-1",
                  "requestName": "食品检测",
                  "domainPackCode": "FOOD_ROUTINE",
                  "domainPackVersion": "1.0",
                  "workflowSnapshotHash": "hash-001",
                  "results": [
                    {"sampleNo": "S01", "testItem": "pH", "resultValue": "7.1", "resultUnit": "", "conclusion": "合格"}
                  ],
                  "resultValues": [
                    {"taskNo": "T01", "fieldName": "pH值", "displayValue": "7.1", "conclusion": "合格"}
                  ],
                  "tasks": [
                    {"taskNo": "T01", "testItem": "pH", "equipmentCode": "PH-METER-001", "equipmentName": "酸度计"}
                  ]
                }
                """;

        ReportOutputBundle bundle = generator.generate(new ReportOutputRequest(
                "RPT-REQ-1", "食品检测报告", "合格", reportContent, draftPlan, "2026-06-14 18:30:00"));

        assertEquals("PDF", bundle.primaryFormat());
        assertEquals("/lims/report-output/RPT-REQ-1/RPT-REQ-1.pdf", bundle.primaryFileUrl());
        assertEquals(3, bundle.outputs().size());
        assertTrue(bundle.outputs().stream().allMatch(output -> output.contentHash() != null && output.contentHash().length() == 64));
        assertTrue(Files.exists(outputDir.resolve("RPT-REQ-1/RPT-REQ-1.docx")));
        assertTrue(Files.exists(outputDir.resolve("RPT-REQ-1/RPT-REQ-1.pdf")));
        assertTrue(Files.exists(outputDir.resolve("RPT-REQ-1/RPT-REQ-1.xlsx")));
    }

}
