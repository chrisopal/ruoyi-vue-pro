package cn.iocoder.yudao.module.lims.service.workflow;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsWorkflowSaveReqVO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskQcRecordDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskRawRecordDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskReviewDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestTaskDO;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTaskQcRecordMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTaskRawRecordMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTaskReviewMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestTaskMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LimsTaskRecordServiceTest extends BaseMockitoUnitTest {

    @InjectMocks
    private LimsTaskRecordService service;

    @Mock
    private LimsTestTaskMapper taskMapper;
    @Mock
    private LimsTaskRawRecordMapper rawRecordMapper;
    @Mock
    private LimsTaskQcRecordMapper qcRecordMapper;
    @Mock
    private LimsTaskReviewMapper reviewMapper;
    @Mock
    private LimsTaskLifecycleService lifecycleService;

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
    void submitRawRecord_shouldUseLifecycleGateForCurrentStatus() {
        when(taskMapper.selectById(10L)).thenReturn(task(10L, LimsTaskStatus.ASSIGNED));

        service.submitRawRecord(rawReq());

        verify(lifecycleService).transition(10L, LimsTaskStatus.DATA_SUBMITTED,
                LimsTaskEventType.RECORD_SUBMITTED, "原始记录已提交", null);
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
                        && Boolean.FALSE.equals(update.getReportEligible())));
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
        when(taskMapper.selectById(10L)).thenReturn(task(10L, LimsTaskStatus.REVIEWING));

        service.approveReview(reviewReq());

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
        verify(taskMapper).updateById(argThat((LimsTestTaskDO update) ->
                Long.valueOf(10L).equals(update.getId())
                        && Long.valueOf(202L).equals(update.getReviewerId())
                        && LimsTaskReviewStatus.APPROVED.equals(update.getReviewStatus())
                        && Boolean.TRUE.equals(update.getReportEligible())
                        && update.getActualEndTime() != null));
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
        task.setTaskNo("REQ-001-T01");
        task.setTaskStatus(status);
        task.setStatus(status);
        task.setReviewStatus(LimsTaskReviewStatus.NONE);
        task.setQcStatus(LimsTaskReviewStatus.NONE);
        return task;
    }

}
