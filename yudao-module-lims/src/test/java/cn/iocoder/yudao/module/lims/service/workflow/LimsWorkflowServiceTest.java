package cn.iocoder.yudao.module.lims.service.workflow;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.lab.service.domainpack.dto.LabDomainPackSnapshotDTO;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsWorkflowSaveReqVO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsExecutionPlanDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsSampleDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestRequestDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestTaskDO;
import cn.iocoder.yudao.module.lims.dal.mysql.resultvalue.LimsTestResultValueMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsExecutionPlanMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsReportMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsSampleMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestRequestMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestResultMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestTaskMapper;
import cn.iocoder.yudao.module.lims.service.workflow.gateway.DomainPackGateway;
import cn.iocoder.yudao.module.lims.service.workflow.gateway.EquipmentGateway;
import cn.iocoder.yudao.module.lims.service.workflow.model.AvailableEquipment;
import cn.iocoder.yudao.module.lims.service.workflow.model.CalibrationEvidence;
import cn.iocoder.yudao.module.lims.service.workflow.model.WorkflowSnapshot;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LimsWorkflowServiceTest extends BaseMockitoUnitTest {

    @InjectMocks
    private LimsWorkflowService workflowService;

    @Mock
    private LimsTestRequestMapper requestMapper;
    @Mock
    private LimsSampleMapper sampleMapper;
    @Mock
    private LimsTestTaskMapper taskMapper;
    @Mock
    private LimsTestResultMapper resultMapper;
    @Mock
    private LimsTestResultValueMapper resultValueMapper;
    @Mock
    private LimsReportMapper reportMapper;
    @Mock
    private LimsExecutionPlanMapper executionPlanMapper;
    @Mock
    private DomainPackGateway domainPackGateway;
    @Mock
    private EquipmentGateway equipmentGateway;
    @Mock
    private WorkflowSnapshotFactory workflowSnapshotFactory;
    @Spy
    private ReportDraftPlanFactory reportDraftPlanFactory = new ReportDraftPlanFactory(new ObjectMapper());
    @Spy
    private ExecutionPlanFactory executionPlanFactory = new ExecutionPlanFactory(new ObjectMapper(), reportDraftPlanFactory);
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createRequest_shouldFreezeSnapshotFromDomainPackGateway() {
        LabDomainPackSnapshotDTO pack = publishedPackSnapshot();
        when(requestMapper.selectByRequestNo("REQ-2026-001")).thenReturn(null);
        when(domainPackGateway.getPublishedPackSnapshot(1L)).thenReturn(pack);
        WorkflowSnapshot snapshot = new WorkflowSnapshot(
                "{\"packCode\":\"FOOD_ROUTINE\",\"testItems\":[]}",
                "hash-001",
                LocalDateTime.of(2026, 6, 14, 10, 30, 0),
                1L,
                "FOOD_ROUTINE",
                "1.0");
        when(workflowSnapshotFactory.createSnapshot(pack, "{\"channel\":\"internal\"}")).thenReturn(snapshot);

        workflowService.createRequest(createReq());

        verify(domainPackGateway).getPublishedPackSnapshot(1L);
        verify(workflowSnapshotFactory).createSnapshot(pack, "{\"channel\":\"internal\"}");
        verify(requestMapper).insert(argThat((LimsTestRequestDO request) ->
                "FOOD_ROUTINE".equals(request.getDomainPackCode())
                        && "1.0".equals(request.getDomainPackVersion())
                        && "hash-001".equals(request.getWorkflowSnapshotHash())
                        && request.getScenarioConfig().contains("FOOD_ROUTINE")
                        && "INTERNAL_DEPARTMENT".equals(request.getRequestSourceType())));
    }

    @Test
    void generateTasks_shouldPersistExecutionPlanWithSampleQcEvidenceAndReportDraft() {
        LimsTestRequestDO request = requestWithWorkflowSnapshot(snapshotWithAllSections());
        when(requestMapper.selectById(1L)).thenReturn(request);
        when(sampleMapper.selectListByRequestId(1L)).thenReturn(List.of(sample()));

        Long created = workflowService.generateTasks(1L);

        assertEquals(1L, created);
        verify(executionPlanMapper).insert(argThat((LimsExecutionPlanDO plan) ->
                Long.valueOf(1L).equals(plan.getRequestId())
                        && "snapshot-hash-001".equals(plan.getWorkflowSnapshotHash())
                        && plan.getPlanJson().contains("sampleRequirements")
                        && plan.getPlanJson().contains("qcCheckPlans")
                        && plan.getPlanJson().contains("evidenceRequirementPlans")
                        && plan.getPlanJson().contains("reportDraftPlan")));
    }

    @Test
    void generateTasks_shouldBindAvailableEquipmentAndCalibrationEvidence() {
        LimsTestRequestDO request = requestWithWorkflowSnapshot(snapshotWithAllSections());
        when(requestMapper.selectById(1L)).thenReturn(request);
        when(sampleMapper.selectListByRequestId(1L)).thenReturn(List.of(sample()));
        when(equipmentGateway.getAvailableEquipment("FOOD", "pH")).thenReturn(List.of(new AvailableEquipment(
                88L, "PH-METER-001", "酸度计", "instrument", "FOOD", "pH", "2099-12-31",
                false, null, null, "manual")));
        when(equipmentGateway.getCurrentCalibrationEvidence(88L)).thenReturn(List.of(new CalibrationEvidence(
                99L, 88L, "calibration", "CERT-001", "省计量院", "2026-01-01",
                "2099-12-31", "合格", "https://example.test/cert.pdf", true)));

        workflowService.generateTasks(1L);

        verify(taskMapper).insert(argThat((LimsTestTaskDO task) -> Long.valueOf(88L).equals(task.getEquipmentId())
                && "PH-METER-001".equals(task.getEquipmentCode())
                && task.getEquipmentSnapshot().contains("酸度计")
                && task.getEquipmentEvidenceSnapshot().contains("CERT-001")));
    }

    private static LimsWorkflowSaveReqVO createReq() {
        LimsWorkflowSaveReqVO reqVO = new LimsWorkflowSaveReqVO();
        reqVO.setRequestNo("REQ-2026-001");
        reqVO.setRequestName("食品委托检测");
        reqVO.setRequestType("internal");
        reqVO.setDomainCode("FOOD");
        reqVO.setDomainPackId(1L);
        reqVO.setScenarioConfig("{\"channel\":\"internal\"}");
        return reqVO;
    }

    private static LabDomainPackSnapshotDTO publishedPackSnapshot() {
        LabDomainPackSnapshotDTO pack = new LabDomainPackSnapshotDTO();
        pack.setDomainPackId(1L);
        pack.setPackCode("FOOD_ROUTINE");
        pack.setPackName("食品常规检测方案包");
        pack.setPackVersion("1.0");
        pack.setIndustry("食品");
        return pack;
    }

    private static LimsTestRequestDO requestWithWorkflowSnapshot(String snapshotJson) {
        LimsTestRequestDO request = new LimsTestRequestDO();
        request.setId(1L);
        request.setRequestNo("REQ-2026-001");
        request.setRequestName("食品委托检测");
        request.setDomainCode("FOOD");
        request.setWorkflowSnapshot(snapshotJson);
        request.setWorkflowSnapshotHash("snapshot-hash-001");
        return request;
    }

    private static LimsSampleDO sample() {
        LimsSampleDO sample = new LimsSampleDO();
        sample.setId(10L);
        sample.setRequestId(1L);
        sample.setRequestNo("REQ-2026-001");
        sample.setSampleNo("REQ-2026-001-S01");
        sample.setSampleName("食品样品");
        return sample;
    }

    private static String snapshotWithAllSections() {
        return """
                {
                  "domainPackId": 1,
                  "packCode": "FOOD_ROUTINE",
                  "packVersion": "1.0",
                  "sampleRequirements": [
                    {"requirementCode": "SAMPLE_QTY", "requirementName": "样品量", "requirementText": ">= 500g"}
                  ],
                  "testItems": [
                    {"itemCode": "PH", "itemName": "pH", "methodCode": "GB6920", "methodName": "玻璃电极法"}
                  ],
                  "resultFields": [
                    {"itemCode": "PH", "fieldCode": "PH_VALUE", "fieldName": "pH值", "fieldType": "number"}
                  ],
                  "qcRules": [
                    {"ruleCode": "BLANK", "ruleName": "空白样", "acceptanceCriteria": "每批至少 1 个"}
                  ],
                  "reportSections": [
                    {"sectionCode": "RESULTS", "sectionName": "检测结果", "sourceType": "result_values"}
                  ],
                  "evidenceRequirements": [
                    {"requirementCode": "EQUIPMENT_CERT", "requirementName": "设备校准证书", "evidenceType": "EQUIPMENT_CERTIFICATE"}
                  ]
                }
                """;
    }

}
