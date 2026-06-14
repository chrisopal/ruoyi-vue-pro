package cn.iocoder.yudao.module.lims.service.workflow;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.lab.service.domainpack.dto.LabDomainPackSnapshotDTO;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsExecutionPlanRespVO;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsTaskQualityGateRespVO;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsWorkflowPageReqVO;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsWorkflowRespVO;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsWorkflowSaveReqVO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsExecutionPlanDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsReportDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsSampleDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskQcRecordDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskRawRecordDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskReviewDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestRequestDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestResultDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestTaskDO;
import cn.iocoder.yudao.module.lims.dal.mysql.resultvalue.LimsTestResultValueMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsExecutionPlanMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsReportMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsSampleMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTaskQcRecordMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTaskRawRecordMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTaskReviewMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestRequestMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestResultMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestTaskMapper;
import cn.iocoder.yudao.module.lims.service.workflow.gateway.DomainPackGateway;
import cn.iocoder.yudao.module.lims.service.workflow.gateway.EquipmentGateway;
import cn.iocoder.yudao.module.lims.service.workflow.gateway.ReportEvidenceGateway;
import cn.iocoder.yudao.module.lims.service.workflow.model.AvailableEquipment;
import cn.iocoder.yudao.module.lims.service.workflow.model.CalibrationEvidence;
import cn.iocoder.yudao.module.lims.service.workflow.model.IssuedReportEvidence;
import cn.iocoder.yudao.module.lims.service.workflow.model.WorkflowSnapshot;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
    private LimsTaskRawRecordMapper rawRecordMapper;
    @Mock
    private LimsTaskQcRecordMapper qcRecordMapper;
    @Mock
    private LimsTaskReviewMapper reviewMapper;
    @Mock
    private DomainPackGateway domainPackGateway;
    @Mock
    private EquipmentGateway equipmentGateway;
    @Mock
    private ReportEvidenceGateway reportEvidenceGateway;
    @Mock
    private WorkflowSnapshotFactory workflowSnapshotFactory;
    @Mock
    private LimsTaskLifecycleService taskLifecycleService;
    @Mock
    private LimsReportEligibilityService reportEligibilityService;
    @Mock
    private LimsTaskRecordService taskRecordService;
    @Mock
    private LimsQualityGateService qualityGateService;
    @Spy
    private ReportDraftPlanFactory reportDraftPlanFactory = new ReportDraftPlanFactory(new ObjectMapper());
    @Spy
    private ExecutionPlanFactory executionPlanFactory = new ExecutionPlanFactory(new ObjectMapper(), reportDraftPlanFactory);
    @Spy
    private ReportOutputGenerator reportOutputGenerator = new ReportOutputGenerator(new ObjectMapper());
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @TempDir
    private Path reportOutputDir;

    @BeforeEach
    void setUpReportOutputDir() {
        System.setProperty(ReportOutputGenerator.OUTPUT_DIR_PROPERTY, reportOutputDir.toString());
    }

    @AfterEach
    void clearReportOutputDir() {
        System.clearProperty(ReportOutputGenerator.OUTPUT_DIR_PROPERTY);
    }

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
                        && plan.getPlanJson().contains("reportDraftPlan")
                        && plan.getPlanJson().contains("templateCodes")
                        && plan.getPlanJson().contains("REPORT_BASIC_V1")));
    }

    @Test
    void generateTasks_shouldInitializeLifecycleSchedulingAndReportGateFields() {
        LimsTestRequestDO request = requestWithWorkflowSnapshot(snapshotWithAllSections());
        when(requestMapper.selectById(1L)).thenReturn(request);
        when(sampleMapper.selectListByRequestId(1L)).thenReturn(List.of(sample()));

        workflowService.generateTasks(1L);

        verify(taskMapper).insert(argThat((LimsTestTaskDO task) ->
                "generated".equals(task.getStatus())
                        && "generated".equals(task.getTaskStatus())
                        && "unscheduled".equals(task.getScheduleStatus())
                        && "none".equals(task.getQcStatus())
                        && "none".equals(task.getReviewStatus())
                        && Boolean.FALSE.equals(task.getReportEligible())
                        && task.getMethodSnapshot() != null
                        && (task.getMethodSnapshot().contains("pH") || task.getMethodSnapshot().contains("HJ-1147"))
                        && task.getDurationMinutes() != null
                        && Long.valueOf(75L).equals(task.getDurationMinutes())));
    }

    @Test
    void createTask_shouldInitializeManualTaskLifecycleDefaults() {
        LimsTestRequestDO request = requestWithWorkflowSnapshot(snapshotWithAllSections());
        when(requestMapper.selectById(1L)).thenReturn(request);
        when(sampleMapper.selectById(10L)).thenReturn(sample());
        when(equipmentGateway.getAvailableEquipment("FOOD", "pH")).thenReturn(List.of());

        workflowService.createTask(manualTaskReq());

        verify(taskMapper).insert(argThat((LimsTestTaskDO task) ->
                LimsTaskStatus.ASSIGNED.equals(task.getStatus())
                        && LimsTaskStatus.ASSIGNED.equals(task.getTaskStatus())
                        && LimsTaskScheduleStatus.UNSCHEDULED.equals(task.getScheduleStatus())
                        && LimsTaskReviewStatus.NONE.equals(task.getQcStatus())
                        && LimsTaskReviewStatus.NONE.equals(task.getReviewStatus())
                        && Boolean.FALSE.equals(task.getReportEligible())
                        && Long.valueOf(60L).equals(task.getDurationMinutes())
                        && task.getMethodSnapshot() != null
                        && task.getMethodSnapshot().contains("pH")));
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

    @Test
    void generateReport_shouldIncludeEquipmentEvidenceSnapshots() {
        LimsTestRequestDO request = requestWithWorkflowSnapshot(snapshotWithAllSections());
        when(requestMapper.selectById(1L)).thenReturn(request);
        when(reportMapper.selectByRequestId(1L)).thenReturn(null);
        when(resultMapper.selectListByRequestId(1L)).thenReturn(List.of(result()));
        when(taskMapper.selectListByRequestId(1L)).thenReturn(List.of(taskWithEquipmentEvidence()));
        when(resultValueMapper.selectListByRequestId(1L)).thenReturn(List.of());

        workflowService.generateReport(1L);

        verify(reportEligibilityService).assertRequestReportable(1L);
        verify(reportMapper).insert(argThat((LimsReportDO report) ->
                report.getDataSnapshot().contains("equipmentEvidenceSnapshots")
                        && report.getDataSnapshot().contains("PH-METER-001")
                        && report.getDataSnapshot().contains("CERT-001")
                        && report.getDataSnapshotHash() != null
                        && "1.0".equals(report.getTemplateVersion())
                        && "/lims/report-output/RPT-REQ-2026-001/RPT-REQ-2026-001.pdf".equals(report.getFileUrl())
                        && report.getReportOutput().contains("\"primaryFormat\":\"PDF\"")
                        && report.getReportOutput().contains("\"format\":\"WORD\"")
                        && report.getReportOutput().contains("\"format\":\"PDF\"")
                        && report.getReportOutput().contains("\"format\":\"EXCEL\"")
                        && report.getReportOutput().contains("contentHash")));
    }

    @Test
    void approveResult_shouldMarkTaskReportEligibleAndWriteEvent() {
        LimsTestResultDO result = result();
        LimsTestTaskDO task = taskWithEquipmentEvidence();
        task.setTaskStatus(LimsTaskStatus.REVIEWING);
        task.setStatus(LimsTaskStatus.REVIEWING);
        when(resultMapper.selectById(100L)).thenReturn(result);
        when(taskMapper.selectById(20L)).thenReturn(task);
        when(taskMapper.selectListByRequestId(1L)).thenReturn(List.of(task));

        workflowService.approveResult(100L);

        verify(taskMapper).updateById(argThat((LimsTestTaskDO updated) ->
                Long.valueOf(20L).equals(updated.getId())
                        && LimsTaskReviewStatus.APPROVED.equals(updated.getReviewStatus())
                        && Boolean.TRUE.equals(updated.getReportEligible())));
        verify(taskLifecycleService).transition(20L, LimsTaskStatus.APPROVED,
                LimsTaskEventType.APPROVED, "检测结果审核通过", null);
    }

    @Test
    void submitRawRecord_shouldDelegateToTaskRecordService() {
        LimsWorkflowSaveReqVO req = new LimsWorkflowSaveReqVO();
        req.setTaskId(20L);

        workflowService.submitRawRecord(req);

        verify(taskRecordService).submitRawRecord(req);
    }

    @Test
    void submitQcRecord_shouldDelegateToTaskRecordService() {
        LimsWorkflowSaveReqVO req = new LimsWorkflowSaveReqVO();
        req.setTaskId(20L);

        workflowService.submitQcRecord(req);

        verify(taskRecordService).submitQcRecord(req);
    }

    @Test
    void approveTaskReview_shouldDelegateToTaskRecordService() {
        LimsWorkflowSaveReqVO req = new LimsWorkflowSaveReqVO();
        req.setTaskId(20L);

        workflowService.approveTaskReview(req);

        verify(taskRecordService).approveReview(req);
    }

    @Test
    void rejectTaskReview_shouldDelegateToTaskRecordService() {
        LimsWorkflowSaveReqVO req = new LimsWorkflowSaveReqVO();
        req.setTaskId(20L);

        workflowService.rejectTaskReview(req);

        verify(taskRecordService).rejectReview(req);
    }

    @Test
    void getTaskReviews_shouldDelegateToTaskRecordService() {
        LimsTaskReviewDO review = new LimsTaskReviewDO();
        review.setId(1L);
        review.setTaskId(20L);
        review.setReviewType("technical");
        review.setReviewStatus(LimsTaskReviewStatus.APPROVED);
        when(taskRecordService.listReviewsByTaskId(20L)).thenReturn(List.of(review));

        List<LimsWorkflowRespVO> responses = workflowService.getTaskReviews(20L);

        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getId());
        assertEquals(20L, responses.get(0).getTaskId());
        assertEquals("technical", responses.get(0).getReviewType());
        assertEquals(LimsTaskReviewStatus.APPROVED, responses.get(0).getReviewStatus());
    }

    @Test
    void getTask_shouldEnrichDomainFromRequest() {
        LimsTestTaskDO task = taskWithEquipmentEvidence();
        when(taskMapper.selectById(20L)).thenReturn(task);
        when(requestMapper.selectById(1L)).thenReturn(requestWithWorkflowSnapshot(snapshotWithAllSections()));

        LimsWorkflowRespVO response = workflowService.getTask(20L);

        assertEquals("FOOD", response.getDomainCode());
        assertEquals("REQ-2026-001", response.getRequestNo());
    }

    @Test
    void getTaskPage_shouldEnrichDomainFromRequest() {
        LimsTestTaskDO task = taskWithEquipmentEvidence();
        when(taskMapper.selectPage(any())).thenReturn(new PageResult<>(List.of(task), 1L));
        when(requestMapper.selectById(1L)).thenReturn(requestWithWorkflowSnapshot(snapshotWithAllSections()));

        PageResult<LimsWorkflowRespVO> page = workflowService.getTaskPage(new LimsWorkflowPageReqVO());

        assertEquals(1, page.getList().size());
        assertEquals("FOOD", page.getList().get(0).getDomainCode());
    }

    @Test
    void getExecutionPlan_shouldExposePersistedPlanAndReportDraftPlan() {
        when(requestMapper.selectById(1L)).thenReturn(requestWithWorkflowSnapshot(snapshotWithAllSections()));
        LimsExecutionPlanDO plan = new LimsExecutionPlanDO();
        plan.setId(300L);
        plan.setRequestId(1L);
        plan.setWorkflowSnapshotHash("snapshot-hash-001");
        plan.setStatus("generated");
        plan.setPlanJson("""
                {
                  "taskPlans": [{"itemCode": "PH", "itemName": "pH"}],
                  "sampleRequirements": [{"requirementCode": "SAMPLE_QTY"}],
                  "qcCheckPlans": [{"ruleCode": "BLANK"}],
                  "evidenceRequirementPlans": [{"requirementCode": "EQUIPMENT_CERT"}],
                  "reportDraftPlan": {
                    "templateVersion": "1.0",
                    "outputFormats": ["WORD", "PDF", "EXCEL"],
                    "sections": [{"sectionCode": "RESULTS"}]
                  }
                }
                """);
        when(executionPlanMapper.selectByRequestId(1L)).thenReturn(plan);

        LimsExecutionPlanRespVO response = workflowService.getExecutionPlan(1L);

        assertEquals(300L, response.getId());
        assertEquals(1L, response.getRequestId());
        assertEquals("snapshot-hash-001", response.getWorkflowSnapshotHash());
        assertEquals("generated", response.getStatus());
        assertEquals(plan.getPlanJson(), response.getPlanJson());
        assertEquals("""
                {"templateVersion":"1.0","outputFormats":["WORD","PDF","EXCEL"],"sections":[{"sectionCode":"RESULTS"}]}""".trim(), response.getReportDraftPlan());
    }

    @Test
    void getTaskQualityGate_shouldExposeTaskRequirementsFromExecutionPlan() {
        when(taskMapper.selectById(20L)).thenReturn(taskWithEquipmentEvidence());
        when(requestMapper.selectById(1L)).thenReturn(requestWithWorkflowSnapshot(snapshotWithAllSections()));
        LimsExecutionPlanDO plan = new LimsExecutionPlanDO();
        plan.setRequestId(1L);
        plan.setStatus("generated");
        plan.setPlanJson("""
                {
                  "taskPlans": [
                    {
                      "itemCode": "PH",
                      "itemName": "pH",
                      "methodCode": "GB6920",
                      "sampleRequirements": [{"requirementCode": "SAMPLE_QTY"}],
                      "resultFields": [{"fieldCode": "PH_VALUE", "fieldName": "pH值"}],
                      "qcRules": [{"ruleCode": "BLANK", "ruleName": "空白样"}],
                      "evidenceRequirements": [{"requirementCode": "EQUIPMENT_CERT", "evidenceType": "EQUIPMENT_CERTIFICATE"}],
                      "reportSections": [{"sectionCode": "RESULTS"}]
                    }
                  ],
                  "reportDraftPlan": {
                    "templateCodes": ["REPORT_BASIC_V1"],
                    "sections": [{"sectionCode": "RESULTS"}],
                    "dataBindings": [{"fieldCode": "PH_VALUE"}]
                  }
                }
                """);
        when(executionPlanMapper.selectByRequestId(1L)).thenReturn(plan);
        when(rawRecordMapper.selectListByTaskId(20L)).thenReturn(List.of(rawRecord()));
        when(qcRecordMapper.selectListByTaskId(20L)).thenReturn(List.of(qcRecord("approved", "{\"rules\":[{\"ruleCode\":\"BLANK\"}]}")));
        when(reviewMapper.selectListByTaskId(20L)).thenReturn(List.of(review(LimsTaskReviewStatus.APPROVED)));

        LimsTaskQualityGateRespVO response = workflowService.getTaskQualityGate(20L);

        assertEquals(20L, response.getTaskId());
        assertEquals("generated", response.getExecutionPlanStatus());
        assertEquals("SAMPLE_QTY", response.getSampleRequirements().get(0).path("requirementCode").asText());
        assertEquals("PH_VALUE", response.getResultFields().get(0).path("fieldCode").asText());
        assertEquals("BLANK", response.getQcRules().get(0).path("ruleCode").asText());
        assertEquals("EQUIPMENT_CERT", response.getEvidenceRequirements().get(0).path("requirementCode").asText());
        assertEquals("REPORT_BASIC_V1", response.getTemplateCodes().get(0).asText());
        assertEquals("BLANK", response.getQcRuleSnapshot().path("rules").get(0).path("ruleCode").asText());
        assertEquals(true, response.getHasEquipmentEvidence());
        assertEquals(1, response.getRawRecordCount());
        assertEquals(1, response.getApprovedQcRecordCount());
        assertEquals(1, response.getApprovedReviewCount());
        assertEquals(1, response.getSatisfiedQcRuleCount());
        assertEquals(0, response.getMissingRequirementCount());
        assertEquals(true, response.getQualityGateSatisfied());
    }

    @Test
    void getTaskQualityGate_shouldExposeMissingRequirementProgress() {
        LimsTestTaskDO task = taskWithEquipmentEvidence();
        task.setEquipmentEvidenceSnapshot("[]");
        when(taskMapper.selectById(20L)).thenReturn(task);
        when(requestMapper.selectById(1L)).thenReturn(requestWithWorkflowSnapshot(snapshotWithAllSections()));
        LimsExecutionPlanDO plan = new LimsExecutionPlanDO();
        plan.setRequestId(1L);
        plan.setStatus("generated");
        plan.setPlanJson("""
                {
                  "taskPlans": [
                    {
                      "itemCode": "PH",
                      "itemName": "pH",
                      "resultFields": [{"fieldCode": "PH_VALUE"}],
                      "qcRules": [{"ruleCode": "BLANK", "ruleName": "空白样"}],
                      "evidenceRequirements": [{"requirementCode": "EQUIPMENT_CERT", "evidenceType": "EQUIPMENT_CERTIFICATE"}]
                    }
                  ],
                  "reportDraftPlan": {}
                }
                """);
        when(executionPlanMapper.selectByRequestId(1L)).thenReturn(plan);
        when(rawRecordMapper.selectListByTaskId(20L)).thenReturn(List.of());
        when(qcRecordMapper.selectListByTaskId(20L)).thenReturn(List.of(qcRecord("rejected", "{\"rules\":[{\"ruleCode\":\"BLANK\"}]}")));
        when(reviewMapper.selectListByTaskId(20L)).thenReturn(List.of());

        LimsTaskQualityGateRespVO response = workflowService.getTaskQualityGate(20L);

        assertEquals(false, response.getRawRecordSatisfied());
        assertEquals(false, response.getQcSatisfied());
        assertEquals(false, response.getEquipmentEvidenceSatisfied());
        assertEquals(false, response.getReviewSatisfied());
        assertEquals(false, response.getQualityGateSatisfied());
        assertEquals(4, response.getMissingRequirementCount());
        assertEquals("RAW_RECORD", response.getMissingRequirements().get(0).path("type").asText());
        assertEquals("QC_RULE", response.getMissingRequirements().get(1).path("type").asText());
        assertEquals("EQUIPMENT_EVIDENCE", response.getMissingRequirements().get(2).path("type").asText());
        assertEquals("TECH_REVIEW", response.getMissingRequirements().get(3).path("type").asText());
    }

    @Test
    void createResult_shouldSubmitTaskThroughLifecycleAndMarkReviewPending() {
        LimsTestTaskDO task = taskWithEquipmentEvidence();
        task.setTaskStatus(LimsTaskStatus.TESTING);
        task.setStatus(LimsTaskStatus.TESTING);
        when(taskMapper.selectById(20L)).thenReturn(task);
        when(requestMapper.selectById(1L)).thenReturn(requestWithWorkflowSnapshot(snapshotWithAllSections()));
        when(taskMapper.selectListByRequestId(1L)).thenReturn(List.of(task));

        workflowService.createResult(resultReq());

        verify(resultMapper).insert(argThat((LimsTestResultDO result) ->
                Long.valueOf(1L).equals(result.getRequestId())
                        && Long.valueOf(20L).equals(result.getTaskId())
                        && "REQ-2026-001-T01".equals(result.getTaskNo())
                        && "recorded".equals(result.getStatus())));
        verify(qualityGateService).validateResultValues(argThat((LimsTestRequestDO request) -> Long.valueOf(1L).equals(request.getId())),
                argThat((LimsTestTaskDO validatedTask) -> Long.valueOf(20L).equals(validatedTask.getId())),
                argThat(rawData -> rawData.contains("resultValues")));
        verify(taskLifecycleService).transition(20L, LimsTaskStatus.DATA_SUBMITTED,
                LimsTaskEventType.RECORD_SUBMITTED, "检测结果已录入", null);
        verify(taskLifecycleService).transition(20L, LimsTaskStatus.REVIEWING,
                LimsTaskEventType.REVIEW_SUBMITTED, "检测结果待复核", null);
    }

    @Test
    void updateTask_shouldIgnoreLifecycleOwnedFields() {
        LimsTestTaskDO existing = taskWithEquipmentEvidence();
        when(taskMapper.selectById(20L)).thenReturn(existing);
        LimsWorkflowSaveReqVO reqVO = new LimsWorkflowSaveReqVO();
        reqVO.setId(20L);
        reqVO.setTaskName("pH 修订");
        reqVO.setStatus(LimsTaskStatus.APPROVED);
        reqVO.setTaskStatus(LimsTaskStatus.APPROVED);
        reqVO.setScheduleStatus(LimsTaskScheduleStatus.SCHEDULED);
        reqVO.setActualStartTime("2026-06-14 10:00:00");
        reqVO.setActualEndTime("2026-06-14 11:00:00");
        reqVO.setReadinessSnapshot("{\"ready\":true}");
        reqVO.setQcStatus(LimsTaskReviewStatus.APPROVED);
        reqVO.setReviewStatus(LimsTaskReviewStatus.APPROVED);
        reqVO.setReportEligible(true);
        reqVO.setBlockReason("manual");
        reqVO.setMethodSnapshot("{\"method\":\"override\"}");

        workflowService.updateTask(reqVO);

        verify(taskMapper).updateById(argThat((LimsTestTaskDO task) ->
                Long.valueOf(20L).equals(task.getId())
                        && "pH 修订".equals(task.getTaskName())
                        && task.getStatus() == null
                        && task.getTaskStatus() == null
                        && task.getScheduleStatus() == null
                        && task.getActualStartTime() == null
                        && task.getActualEndTime() == null
                        && task.getReadinessSnapshot() == null
                        && task.getQcStatus() == null
                        && task.getReviewStatus() == null
                        && task.getReportEligible() == null
                        && task.getBlockReason() == null
                        && task.getMethodSnapshot() == null));
    }

    @Test
    void updateResult_shouldIgnoreLifecycleOwnedFields() {
        when(resultMapper.selectById(100L)).thenReturn(result());
        LimsWorkflowSaveReqVO reqVO = new LimsWorkflowSaveReqVO();
        reqVO.setId(100L);
        reqVO.setResultValue("7.2");
        reqVO.setStatus("approved");
        reqVO.setReviewerId(9L);
        reqVO.setReviewedTime("2026-06-14 12:00:00");

        workflowService.updateResult(reqVO);

        verify(resultMapper).updateById(argThat((LimsTestResultDO result) ->
                Long.valueOf(100L).equals(result.getId())
                        && "7.2".equals(result.getResultValue())
                        && result.getStatus() == null
                        && result.getReviewerId() == null
                        && result.getReviewedTime() == null));
    }

    @Test
    void updateTaskStatus_shouldMapScheduledStatusToScheduledEvent() {
        when(taskMapper.selectById(20L)).thenReturn(taskWithEquipmentEvidence());

        workflowService.updateTaskStatus(20L, LimsTaskStatus.SCHEDULED);

        verify(taskLifecycleService).transition(20L, LimsTaskStatus.SCHEDULED,
                LimsTaskEventType.SCHEDULED, "手动更新任务状态", null);
    }

    @Test
    void issueReport_shouldRegisterIssuedReportAsEvidenceObject() {
        LimsReportDO report = report();
        LimsTestRequestDO request = requestWithWorkflowSnapshot(snapshotWithAllSections());
        when(reportMapper.selectById(200L)).thenReturn(report);
        when(requestMapper.selectById(1L)).thenReturn(request);

        workflowService.issueReport(200L);

        verify(reportEvidenceGateway).registerIssuedReportEvidence(argThat((IssuedReportEvidence evidence) ->
                Long.valueOf(200L).equals(evidence.reportId())
                        && "RPT-2026-001".equals(evidence.reportNo())
                        && Long.valueOf(1L).equals(evidence.requestId())
                        && "REQ-2026-001".equals(evidence.requestNo())
                        && "hash-report-001".equals(evidence.dataSnapshotHash())
                        && "https://example.test/report.pdf".equals(evidence.fileUrl())));
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

    private static LimsTestResultDO result() {
        LimsTestResultDO result = new LimsTestResultDO();
        result.setId(100L);
        result.setRequestId(1L);
        result.setRequestNo("REQ-2026-001");
        result.setSampleId(10L);
        result.setSampleNo("REQ-2026-001-S01");
        result.setTaskId(20L);
        result.setTaskNo("REQ-2026-001-T01");
        result.setTestItem("pH");
        result.setResultValue("7.1");
        result.setResultUnit("");
        result.setResultConclusion("合格");
        return result;
    }

    private static LimsWorkflowSaveReqVO resultReq() {
        LimsWorkflowSaveReqVO reqVO = new LimsWorkflowSaveReqVO();
        reqVO.setTaskId(20L);
        reqVO.setResultValue("7.1");
        reqVO.setResultUnit("");
        reqVO.setResultConclusion("合格");
        reqVO.setRawData("{\"resultValues\":[]}");
        return reqVO;
    }

    private static LimsTestTaskDO taskWithEquipmentEvidence() {
        LimsTestTaskDO task = new LimsTestTaskDO();
        task.setId(20L);
        task.setRequestId(1L);
        task.setTaskNo("REQ-2026-001-T01");
        task.setTaskName("pH");
        task.setTestItem("pH");
        task.setEquipmentId(88L);
        task.setEquipmentCode("PH-METER-001");
        task.setEquipmentName("酸度计");
        task.setEquipmentSnapshot("{\"equipmentCode\":\"PH-METER-001\",\"equipmentName\":\"酸度计\"}");
        task.setEquipmentEvidenceSnapshot("[{\"certificateNo\":\"CERT-001\",\"validTo\":\"2099-12-31\"}]");
        return task;
    }

    private static LimsTaskRawRecordDO rawRecord() {
        LimsTaskRawRecordDO record = new LimsTaskRawRecordDO();
        record.setId(1L);
        record.setTaskId(20L);
        record.setTaskNo("REQ-2026-001-T01");
        record.setRecordType("instrument");
        record.setRecordJson("{\"resultValues\":[{\"fieldCode\":\"PH_VALUE\",\"fieldValue\":\"7.1\"}]}");
        record.setStatus("submitted");
        return record;
    }

    private static LimsTaskQcRecordDO qcRecord(String result, String ruleSnapshot) {
        LimsTaskQcRecordDO record = new LimsTaskQcRecordDO();
        record.setId(1L);
        record.setTaskId(20L);
        record.setTaskNo("REQ-2026-001-T01");
        record.setQcType("routine_qc");
        record.setQcRuleSnapshot(ruleSnapshot);
        record.setQcResult(result);
        return record;
    }

    private static LimsTaskReviewDO review(String status) {
        LimsTaskReviewDO review = new LimsTaskReviewDO();
        review.setId(1L);
        review.setTaskId(20L);
        review.setTaskNo("REQ-2026-001-T01");
        review.setReviewType("technical");
        review.setReviewStatus(status);
        return review;
    }

    private static LimsReportDO report() {
        LimsReportDO report = new LimsReportDO();
        report.setId(200L);
        report.setRequestId(1L);
        report.setRequestNo("REQ-2026-001");
        report.setReportNo("RPT-2026-001");
        report.setReportName("食品委托检测报告");
        report.setFileUrl("https://example.test/report.pdf");
        report.setDataSnapshot("{\"reportNo\":\"RPT-2026-001\"}");
        report.setDataSnapshotHash("hash-report-001");
        return report;
    }

    private static String snapshotWithAllSections() {
        return """
                {
                  "domainPackId": 1,
                  "packCode": "FOOD_ROUTINE",
                  "packVersion": "1.0",
                  "workflow": {
                    "testItems": [
                      {"itemCode": "PH", "itemName": "pH", "methodCode": "GB6920", "methodName": "玻璃电极法", "estimatedDurationMinutes": 75}
                    ]
                  },
                  "template": {
                    "templates": ["REPORT_BASIC_V1"],
                    "outputFormats": ["WORD", "PDF", "EXCEL"],
                    "reportSections": [
                      {"sectionCode": "RESULTS", "sectionName": "检测结果", "sourceType": "result_values"}
                    ]
                  },
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

    private static LimsWorkflowSaveReqVO manualTaskReq() {
        LimsWorkflowSaveReqVO reqVO = new LimsWorkflowSaveReqVO();
        reqVO.setRequestId(1L);
        reqVO.setSampleId(10L);
        reqVO.setTaskNo("REQ-2026-001-T99");
        reqVO.setTaskName("pH");
        reqVO.setTestItem("pH");
        reqVO.setMethodCode("GB6920");
        reqVO.setMethodName("玻璃电极法");
        return reqVO;
    }

}
