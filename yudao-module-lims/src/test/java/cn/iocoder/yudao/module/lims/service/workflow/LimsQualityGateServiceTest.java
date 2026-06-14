package cn.iocoder.yudao.module.lims.service.workflow;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskQcRecordDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskRawRecordDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskReviewDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestRequestDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestTaskDO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LimsQualityGateServiceTest extends BaseMockitoUnitTest {

    @InjectMocks
    private LimsQualityGateService service;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void validateResultValues_shouldAcceptConfiguredNumericResult() {
        assertDoesNotThrow(() -> service.validateResultValues(request(snapshotWithRules()), task(),
                "{\"resultValues\":[{\"fieldCode\":\"PH_VALUE\",\"fieldValue\":\"7.1\"}]}"));
    }

    @Test
    void validateResultValues_shouldRejectMissingRequiredField() {
        assertThrows(Exception.class, () -> service.validateResultValues(request(snapshotWithRules()), task(),
                "{\"resultValues\":[]}"));
    }

    @Test
    void validateResultValues_shouldRejectNumericResultOutsideConfiguredRange() {
        assertThrows(Exception.class, () -> service.validateResultValues(request(snapshotWithRules()), task(),
                "{\"resultValues\":[{\"fieldCode\":\"PH_VALUE\",\"fieldValue\":\"15.2\"}]}"));
    }

    @Test
    void validateResultValues_shouldAcceptEnumOptionsStoredAsJsonText() {
        assertDoesNotThrow(() -> service.validateResultValues(request(snapshotWithTextualEnumOptions()), task(),
                "{\"resultValues\":[{\"fieldCode\":\"JUDGEMENT\",\"fieldValue\":\"PASS\"}]}"));
    }

    @Test
    void validateResultValues_shouldRejectEnumOptionsStoredAsJsonText() {
        assertThrows(Exception.class, () -> service.validateResultValues(request(snapshotWithTextualEnumOptions()), task(),
                "{\"resultValues\":[{\"fieldCode\":\"JUDGEMENT\",\"fieldValue\":\"MAYBE\"}]}"));
    }

    @Test
    void assertQcAndEvidenceComplete_shouldAcceptSatisfiedQcRawAndReviewEvidence() {
        assertDoesNotThrow(() -> service.assertQcAndEvidenceComplete(request(snapshotWithRules()), List.of(task()),
                Map.of(10L, List.of(rawRecord())),
                Map.of(10L, List.of(qcRecord("approved", "{\"ruleCode\":\"BLANK\"}"))),
                Map.of(10L, List.of(review(LimsTaskReviewStatus.APPROVED)))));
    }

    @Test
    void assertQcAndEvidenceComplete_shouldRejectMissingQcRuleEvidence() {
        assertThrows(Exception.class, () -> service.assertQcAndEvidenceComplete(request(snapshotWithRules()), List.of(task()),
                Map.of(10L, List.of(rawRecord())),
                Map.of(10L, List.of(qcRecord("approved", "{\"ruleCode\":\"OTHER\"}"))),
                Map.of(10L, List.of(review(LimsTaskReviewStatus.APPROVED)))));
    }

    @Test
    void assertQcAndEvidenceComplete_shouldRejectMissingRawEvidence() {
        assertThrows(Exception.class, () -> service.assertQcAndEvidenceComplete(request(snapshotWithRules()), List.of(task()),
                Map.of(10L, List.of()),
                Map.of(10L, List.of(qcRecord("approved", "{\"ruleCode\":\"BLANK\"}"))),
                Map.of(10L, List.of(review(LimsTaskReviewStatus.APPROVED)))));
    }

    @Test
    void assertQcAndEvidenceComplete_shouldRejectMissingEquipmentEvidence() {
        LimsTestTaskDO task = task();
        task.setEquipmentEvidenceSnapshot("[]");

        assertThrows(Exception.class, () -> service.assertQcAndEvidenceComplete(request(snapshotWithRules()), List.of(task),
                Map.of(10L, List.of(rawRecord())),
                Map.of(10L, List.of(qcRecord("approved", "{\"ruleCode\":\"BLANK\"}"))),
                Map.of(10L, List.of(review(LimsTaskReviewStatus.APPROVED)))));
    }

    @Test
    void assertQcAndEvidenceComplete_shouldRejectMissingReviewEvidence() {
        assertThrows(Exception.class, () -> service.assertQcAndEvidenceComplete(request(snapshotWithRules()), List.of(task()),
                Map.of(10L, List.of(rawRecord())),
                Map.of(10L, List.of(qcRecord("approved", "{\"ruleCode\":\"BLANK\"}"))),
                Map.of(10L, List.of())));
    }

    @Test
    void assertQcAndEvidenceComplete_shouldRejectMissingPersonnelEvidence() {
        LimsTestTaskDO task = task();
        task.setPersonnelEvidenceSnapshot("[]");

        assertThrows(Exception.class, () -> service.assertQcAndEvidenceComplete(request(snapshotWithPersonnelEvidence()), List.of(task),
                Map.of(10L, List.of(rawRecord())),
                Map.of(10L, List.of(qcRecord("approved", "{\"ruleCode\":\"BLANK\"}"))),
                Map.of(10L, List.of(review(LimsTaskReviewStatus.APPROVED)))));
    }

    private static LimsTestRequestDO request(String snapshot) {
        LimsTestRequestDO request = new LimsTestRequestDO();
        request.setId(1L);
        request.setWorkflowSnapshot(snapshot);
        return request;
    }

    private static LimsTestTaskDO task() {
        LimsTestTaskDO task = new LimsTestTaskDO();
        task.setId(10L);
        task.setTestItem("pH");
        task.setEquipmentEvidenceSnapshot("[{\"certificateNo\":\"CERT-001\"}]");
        task.setPersonnelEvidenceSnapshot("[{\"authorizationId\":200,\"userName\":\"张三\"}]");
        return task;
    }

    private static LimsTaskRawRecordDO rawRecord() {
        LimsTaskRawRecordDO record = new LimsTaskRawRecordDO();
        record.setTaskId(10L);
        record.setRecordJson("{\"temperature\":25}");
        return record;
    }

    private static LimsTaskQcRecordDO qcRecord(String result, String ruleSnapshot) {
        LimsTaskQcRecordDO record = new LimsTaskQcRecordDO();
        record.setTaskId(10L);
        record.setQcResult(result);
        record.setQcRuleSnapshot(ruleSnapshot);
        return record;
    }

    private static LimsTaskReviewDO review(String status) {
        LimsTaskReviewDO review = new LimsTaskReviewDO();
        review.setTaskId(10L);
        review.setReviewStatus(status);
        return review;
    }

    private static String snapshotWithRules() {
        return """
                {
                  "testItems": [
                    {"itemCode": "PH", "itemName": "pH"}
                  ],
                  "resultFields": [
                    {"itemCode": "PH", "fieldCode": "PH_VALUE", "fieldType": "number", "required": true, "minValue": "0", "maxValue": "14"}
                  ],
                  "qcRules": [
                    {"ruleCode": "BLANK", "required": true}
                  ],
                  "evidenceRequirements": [
                    {"evidenceType": "RAW_DATA", "required": true},
                    {"evidenceType": "EQUIPMENT_CERTIFICATE", "required": true},
                    {"evidenceType": "TECHNICAL_REVIEW", "required": true}
                  ]
                }
                """;
    }

    private static String snapshotWithTextualEnumOptions() {
        return """
                {
                  "testItems": [
                    {"itemCode": "PH", "itemName": "pH"}
                  ],
                  "resultFields": [
                    {"itemCode": "PH", "fieldCode": "JUDGEMENT", "fieldType": "enum", "required": true, "enumOptions": "[\\"PASS\\",\\"FAIL\\"]"}
                  ]
                }
                """;
    }

    private static String snapshotWithPersonnelEvidence() {
        return """
                {
                  "testItems": [
                    {"itemCode": "PH", "itemName": "pH"}
                  ],
                  "qcRules": [
                    {"ruleCode": "BLANK", "required": true}
                  ],
                  "evidenceRequirements": [
                    {"evidenceType": "RAW_DATA", "required": true},
                    {"evidenceType": "EQUIPMENT_CERTIFICATE", "required": true},
                    {"evidenceType": "PERSON_AUTH", "sourceType": "personnel", "required": true},
                    {"evidenceType": "TECHNICAL_REVIEW", "required": true}
                  ]
                }
                """;
    }

}
