package cn.iocoder.yudao.module.lims.service.workflow;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsWorkflowSaveReqVO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskQcRecordDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskRawRecordDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskReviewDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestRequestDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestResultDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestTaskDO;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTaskQcRecordMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTaskRawRecordMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTaskReviewMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestRequestMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestResultMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestTaskMapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LimsTaskRecordServiceTest extends BaseMockitoUnitTest {

    @InjectMocks
    private LimsTaskRecordService service;

    @Mock
    private LimsTestTaskMapper taskMapper;
    @Mock
    private LimsTestRequestMapper requestMapper;
    @Mock
    private LimsTaskRawRecordMapper rawRecordMapper;
    @Mock
    private LimsTaskQcRecordMapper qcRecordMapper;
    @Mock
    private LimsTaskReviewMapper reviewMapper;
    @Mock
    private LimsTestResultMapper resultMapper;
    @Mock
    private LimsTaskLifecycleService lifecycleService;
    @Mock
    private LimsQualityGateService qualityGateService;
    @Mock
    private LimsResultValueSyncService resultValueSyncService;
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void submitRawRecord_shouldPersistRecordAndMoveTaskToDataSubmitted() {
        when(taskMapper.selectById(10L)).thenReturn(task(10L, LimsTaskStatus.TESTING));

        service.submitRawRecord(rawReq());

        verify(rawRecordMapper).insert(argThat((LimsTaskRawRecordDO record) ->
                Long.valueOf(10L).equals(record.getTaskId())
                        && "REQ-001-T01".equals(record.getTaskNo())
                        && "instrument".equals(record.getRecordType())
                        && "{\"temperature\":25}".equals(record.getRecordJson())
                        && "https://example.test/raw.pdf".equals(record.getAttachmentUrl())
                        && Long.valueOf(2L).equals(record.getVersionNo())
                        && Long.valueOf(101L).equals(record.getSubmittedBy())
                        && record.getSubmittedTime() != null
                        && "submitted".equals(record.getStatus())));
        verify(lifecycleService).transition(10L, LimsTaskStatus.DATA_SUBMITTED,
                LimsTaskEventType.RECORD_SUBMITTED, "原始记录已提交", null);
        verify(taskMapper).updateById(argThat((LimsTestTaskDO update) ->
                Long.valueOf(10L).equals(update.getId())
                        && LimsTaskReviewStatus.PENDING.equals(update.getQcStatus())
                        && Boolean.FALSE.equals(update.getReportEligible())));
    }

    @Test
    void submitRawRecord_shouldPersistResultPayloadWhenProvided() {
        LimsTestTaskDO task = task(10L, LimsTaskStatus.TESTING);
        task.setRequestNo("REQ-001");
        task.setSampleId(20L);
        task.setSampleNo("S-001");
        task.setTestItem("PH");
        when(taskMapper.selectById(10L)).thenReturn(task);
        when(requestMapper.selectById(1L)).thenReturn(request());
        when(resultMapper.selectListByTaskId(10L)).thenReturn(List.of());
        doAnswer(invocation -> {
            LimsTestResultDO result = invocation.getArgument(0);
            result.setId(100L);
            return 1;
        }).when(resultMapper).insert(any(LimsTestResultDO.class));
        LimsWorkflowSaveReqVO req = rawReq();
        req.setResultValue("7.10");
        req.setResultUnit("pH");
        req.setResultConclusion("pass");
        req.setRawData("{\"resultValues\":[{\"fieldCode\":\"PH\",\"fieldName\":\"酸碱度\",\"fieldValue\":\"7.10\"}]}");

        service.submitRawRecord(req);

        verify(qualityGateService).validateResultValues(argThat((LimsTestRequestDO request) -> Long.valueOf(1L).equals(request.getId())),
                argThat((LimsTestTaskDO validatedTask) -> Long.valueOf(10L).equals(validatedTask.getId())),
                argThat((String rawData) -> rawData.contains("\"fieldCode\":\"PH\"")));
        verify(resultMapper).insert(argThat((LimsTestResultDO result) ->
                Long.valueOf(1L).equals(result.getRequestId())
                        && "REQ-001".equals(result.getRequestNo())
                        && Long.valueOf(20L).equals(result.getSampleId())
                        && "S-001".equals(result.getSampleNo())
                        && Long.valueOf(10L).equals(result.getTaskId())
                        && "REQ-001-T01-R01".equals(result.getResultNo())
                        && "PH".equals(result.getTestItem())
                        && "7.10".equals(result.getResultValue())
                        && "pH".equals(result.getResultUnit())
                        && "pass".equals(result.getResultConclusion())
                        && "recorded".equals(result.getStatus())
                        && result.getRawData().contains("resultValues")));
        verify(resultValueSyncService).replaceValues(argThat((LimsTestResultDO result) ->
                        Long.valueOf(100L).equals(result.getId())
                                && Long.valueOf(10L).equals(result.getTaskId())
                                && result.getRawData().contains("\"fieldName\":\"酸碱度\"")),
                argThat((LimsTestTaskDO syncedTask) -> Long.valueOf(10L).equals(syncedTask.getId())));
    }

    @Test
    void submitRawRecord_shouldUpdateExistingResultAndReplaceDynamicValues() {
        LimsTestTaskDO task = task(10L, LimsTaskStatus.TESTING);
        task.setRequestNo("REQ-001");
        task.setSampleId(20L);
        task.setSampleNo("S-001");
        task.setTestItem("PH");
        LimsTestResultDO existing = result();
        existing.setId(100L);
        existing.setResultValue("7.00");
        when(taskMapper.selectById(10L)).thenReturn(task);
        when(requestMapper.selectById(1L)).thenReturn(request());
        when(resultMapper.selectListByTaskId(10L)).thenReturn(List.of(existing));
        LimsWorkflowSaveReqVO req = rawReq();
        req.setResultValue("7.20");
        req.setResultUnit("pH");
        req.setResultConclusion("pass");
        req.setRawData("{\"resultValues\":[{\"fieldCode\":\"PH\",\"fieldName\":\"酸碱度\",\"fieldValue\":\"7.20\"}]}");

        service.submitRawRecord(req);

        verify(resultMapper, never()).insert(any(LimsTestResultDO.class));
        verify(resultMapper).updateById(argThat((LimsTestResultDO result) ->
                Long.valueOf(100L).equals(result.getId())
                        && "7.20".equals(result.getResultValue())
                        && "pH".equals(result.getResultUnit())
                        && result.getRawData().contains("\"fieldValue\":\"7.20\"")));
        verify(resultValueSyncService).replaceValues(argThat((LimsTestResultDO result) ->
                        Long.valueOf(100L).equals(result.getId())
                                && "7.20".equals(result.getResultValue())),
                argThat((LimsTestTaskDO syncedTask) -> Long.valueOf(10L).equals(syncedTask.getId())));
    }

    @Test
    void submitRawRecord_shouldRejectMissingRecordJsonBeforeInsert() {
        when(taskMapper.selectById(10L)).thenReturn(task(10L, LimsTaskStatus.TESTING));
        LimsWorkflowSaveReqVO req = rawReq();
        req.setRecordJson(null);

        assertThrows(Exception.class, () -> service.submitRawRecord(req));
    }

    @Test
    void submitQcRecord_shouldPersistRecordAndQueueTechnicalReview() {
        when(taskMapper.selectById(10L)).thenReturn(task(10L, LimsTaskStatus.DATA_SUBMITTED));

        service.submitQcRecord(qcReq());

        verify(qcRecordMapper).insert(argThat((LimsTaskQcRecordDO record) ->
                Long.valueOf(10L).equals(record.getTaskId())
                        && "REQ-001-T01".equals(record.getTaskNo())
                        && "blank_sample".equals(record.getQcType())
                        && "{\"rules\":[\"R1\"]}".equals(record.getQcRuleSnapshot())
                        && "{\"actual\":0.2}".equals(record.getQcDataJson())
                        && "approved".equals(record.getQcResult())
                        && "QC pass".equals(record.getReviewComment())));
        verify(lifecycleService).transition(10L, LimsTaskStatus.REVIEWING,
                LimsTaskEventType.REVIEW_SUBMITTED, "QC pass", null);
        verify(taskMapper).updateById(argThat((LimsTestTaskDO update) ->
                Long.valueOf(10L).equals(update.getId())
                        && LimsTaskReviewStatus.APPROVED.equals(update.getQcStatus())
                        && LimsTaskReviewStatus.PENDING.equals(update.getReviewStatus())
                        && Boolean.FALSE.equals(update.getReportEligible())
                        && update.getBlockReason() == null));
    }

    @Test
    void submitQcRecord_shouldRejectMalformedJsonBeforeInsert() {
        when(taskMapper.selectById(10L)).thenReturn(task(10L, LimsTaskStatus.DATA_SUBMITTED));
        LimsWorkflowSaveReqVO req = qcReq();
        req.setQcDataJson("{bad-json");

        assertThrows(Exception.class, () -> service.submitQcRecord(req));
    }

    @Test
    void submitQcRecord_shouldMoveRejectedQcToRework() {
        when(taskMapper.selectById(10L)).thenReturn(task(10L, LimsTaskStatus.DATA_SUBMITTED));
        LimsWorkflowSaveReqVO req = qcReq();
        req.setQcResult("rejected");
        req.setReviewComment("空白样超限");

        service.submitQcRecord(req);

        verify(lifecycleService).transition(10L, LimsTaskStatus.REWORK,
                LimsTaskEventType.REJECTED, "空白样超限", null);
        verify(taskMapper).updateById(argThat((LimsTestTaskDO update) ->
                Long.valueOf(10L).equals(update.getId())
                        && LimsTaskReviewStatus.REJECTED.equals(update.getQcStatus())
                        && Boolean.FALSE.equals(update.getReportEligible())
                        && "空白样超限".equals(update.getBlockReason())));
    }

    @Test
    void approveReview_shouldPersistDecisionTransitionAndMarkTaskEligible() {
        LimsTestTaskDO task = task(10L, LimsTaskStatus.REVIEWING);
        when(taskMapper.selectById(10L)).thenReturn(task);
        when(requestMapper.selectById(1L)).thenReturn(request());
        when(rawRecordMapper.selectListByTaskId(10L)).thenReturn(List.of(rawRecord()));
        when(qcRecordMapper.selectListByTaskId(10L)).thenReturn(List.of(qcRecord()));
        when(reviewMapper.selectListByTaskId(10L)).thenReturn(List.of());
        when(resultMapper.selectListByTaskId(10L)).thenReturn(List.of(result()));

        service.approveReview(reviewReq());

        verify(qualityGateService).assertQcAndEvidenceComplete(argThat((LimsTestRequestDO request) -> Long.valueOf(1L).equals(request.getId())),
                argThat((List<LimsTestTaskDO> tasks) -> tasks.size() == 1 && Long.valueOf(10L).equals(tasks.get(0).getId())),
                argThat((Map<Long, List<LimsTaskRawRecordDO>> records) -> records.containsKey(10L)),
                argThat((Map<Long, List<LimsTaskQcRecordDO>> records) -> records.containsKey(10L)),
                argThat((Map<Long, List<LimsTaskReviewDO>> records) ->
                        records.containsKey(10L) && records.get(10L).stream()
                                .anyMatch(review -> LimsTaskReviewStatus.APPROVED.equals(review.getReviewStatus()))));
        verify(reviewMapper).insert(argThat((LimsTaskReviewDO review) ->
                Long.valueOf(10L).equals(review.getTaskId())
                        && "REQ-001-T01".equals(review.getTaskNo())
                        && "technical".equals(review.getReviewType())
                        && LimsTaskReviewStatus.APPROVED.equals(review.getReviewStatus())
                        && Long.valueOf(202L).equals(review.getReviewerId())
                        && review.getReviewTime() != null
                        && "批准进入报告".equals(review.getComment())
                        && "hash-review-001".equals(review.getSnapshotHash())));
        verify(lifecycleService).transition(10L, LimsTaskStatus.APPROVED,
                LimsTaskEventType.APPROVED, "批准进入报告", null);
        verify(resultMapper).update(argThat((LimsTestResultDO result) -> result == null),
                argThat((UpdateWrapper<LimsTestResultDO> wrapper) -> wrapper != null));
        verify(taskMapper).updateById(argThat((LimsTestTaskDO update) ->
                Long.valueOf(10L).equals(update.getId())
                        && Long.valueOf(202L).equals(update.getReviewerId())
                        && LimsTaskReviewStatus.APPROVED.equals(update.getReviewStatus())
                        && Boolean.TRUE.equals(update.getReportEligible())
                        && update.getActualEndTime() != null
                        && update.getBlockReason() == null));
    }

    @Test
    void approveReview_shouldRejectWhenTaskHasNoResultRows() {
        when(taskMapper.selectById(10L)).thenReturn(task(10L, LimsTaskStatus.REVIEWING));
        when(requestMapper.selectById(1L)).thenReturn(request());
        when(rawRecordMapper.selectListByTaskId(10L)).thenReturn(List.of(rawRecord()));
        when(qcRecordMapper.selectListByTaskId(10L)).thenReturn(List.of(qcRecord()));
        when(reviewMapper.selectListByTaskId(10L)).thenReturn(List.of());
        when(resultMapper.selectListByTaskId(10L)).thenReturn(List.of());

        assertThrows(Exception.class, () -> service.approveReview(reviewReq()));
    }

    @Test
    void approveReview_shouldRejectMissingReviewer() {
        when(taskMapper.selectById(10L)).thenReturn(task(10L, LimsTaskStatus.REVIEWING));
        LimsWorkflowSaveReqVO req = reviewReq();
        req.setReviewerId(null);

        assertThrows(Exception.class, () -> service.approveReview(req));
    }

    @Test
    void rejectReview_shouldPersistDecisionTransitionAndMarkTaskBlocked() {
        when(taskMapper.selectById(10L)).thenReturn(task(10L, LimsTaskStatus.REVIEWING));

        LimsWorkflowSaveReqVO req = reviewReq();
        req.setRemark("数据需返工");

        service.rejectReview(req);

        verify(reviewMapper).insert(argThat((LimsTaskReviewDO review) ->
                Long.valueOf(10L).equals(review.getTaskId())
                        && LimsTaskReviewStatus.REJECTED.equals(review.getReviewStatus())
                        && "数据需返工".equals(review.getComment())));
        verify(lifecycleService).transition(10L, LimsTaskStatus.REWORK,
                LimsTaskEventType.REJECTED, "数据需返工", null);
        verify(taskMapper).updateById(argThat((LimsTestTaskDO update) ->
                Long.valueOf(10L).equals(update.getId())
                        && Long.valueOf(202L).equals(update.getReviewerId())
                        && LimsTaskReviewStatus.REJECTED.equals(update.getReviewStatus())
                        && Boolean.FALSE.equals(update.getReportEligible())
                        && "数据需返工".equals(update.getBlockReason())));
    }

    @Test
    void rejectReview_shouldRejectMissingReviewer() {
        when(taskMapper.selectById(10L)).thenReturn(task(10L, LimsTaskStatus.REVIEWING));
        LimsWorkflowSaveReqVO req = reviewReq();
        req.setReviewerId(null);

        assertThrows(Exception.class, () -> service.rejectReview(req));
    }

    @Test
    void listReviewsByTaskId_shouldReturnPersistedRowsInMapperOrder() {
        LimsTaskReviewDO first = new LimsTaskReviewDO();
        first.setId(2L);
        first.setTaskId(10L);
        first.setReviewStatus(LimsTaskReviewStatus.REJECTED);
        LimsTaskReviewDO second = new LimsTaskReviewDO();
        second.setId(1L);
        second.setTaskId(10L);
        second.setReviewStatus(LimsTaskReviewStatus.APPROVED);
        when(taskMapper.selectById(10L)).thenReturn(task(10L, LimsTaskStatus.REVIEWING));
        when(reviewMapper.selectListByTaskId(10L)).thenReturn(List.of(first, second));

        List<LimsTaskReviewDO> reviews = service.listReviewsByTaskId(10L);

        org.junit.jupiter.api.Assertions.assertEquals(List.of(first, second), reviews);
    }

    private static LimsWorkflowSaveReqVO rawReq() {
        LimsWorkflowSaveReqVO req = new LimsWorkflowSaveReqVO();
        req.setTaskId(10L);
        req.setRecordType("instrument");
        req.setRecordJson("{\"temperature\":25}");
        req.setAttachmentUrl("https://example.test/raw.pdf");
        req.setVersionNo(2L);
        req.setSubmittedBy(101L);
        req.setStatus("submitted");
        req.setRemark("原始记录已提交");
        return req;
    }

    private static LimsWorkflowSaveReqVO qcReq() {
        LimsWorkflowSaveReqVO req = new LimsWorkflowSaveReqVO();
        req.setTaskId(10L);
        req.setQcType("blank_sample");
        req.setQcRuleSnapshot("{\"rules\":[\"R1\"]}");
        req.setQcDataJson("{\"actual\":0.2}");
        req.setQcResult("approved");
        req.setReviewComment("QC pass");
        return req;
    }

    private static LimsWorkflowSaveReqVO reviewReq() {
        LimsWorkflowSaveReqVO req = new LimsWorkflowSaveReqVO();
        req.setTaskId(10L);
        req.setReviewerId(202L);
        req.setReviewType("technical");
        req.setSnapshotHash("hash-review-001");
        req.setRemark("批准进入报告");
        return req;
    }

    private static LimsTestTaskDO task(Long id, String status) {
        LimsTestTaskDO task = new LimsTestTaskDO();
        task.setId(id);
        task.setRequestId(1L);
        task.setTaskNo("REQ-001-T01");
        task.setTaskStatus(status);
        task.setStatus(status);
        task.setReviewStatus(LimsTaskReviewStatus.NONE);
        task.setQcStatus(LimsTaskReviewStatus.NONE);
        return task;
    }

    private static LimsTestRequestDO request() {
        LimsTestRequestDO request = new LimsTestRequestDO();
        request.setId(1L);
        request.setWorkflowSnapshot("{}");
        return request;
    }

    private static LimsTaskRawRecordDO rawRecord() {
        LimsTaskRawRecordDO record = new LimsTaskRawRecordDO();
        record.setId(1L);
        record.setTaskId(10L);
        record.setRecordType("instrument");
        record.setRecordJson("{\"temperature\":25}");
        return record;
    }

    private static LimsTaskQcRecordDO qcRecord() {
        LimsTaskQcRecordDO record = new LimsTaskQcRecordDO();
        record.setId(1L);
        record.setTaskId(10L);
        record.setQcType("blank_sample");
        record.setQcResult("approved");
        record.setQcRuleSnapshot("{\"ruleCode\":\"BLANK\"}");
        return record;
    }

    private static LimsTestResultDO result() {
        LimsTestResultDO result = new LimsTestResultDO();
        result.setId(100L);
        result.setTaskId(10L);
        result.setTaskNo("REQ-001-T01");
        result.setStatus("recorded");
        return result;
    }

}
