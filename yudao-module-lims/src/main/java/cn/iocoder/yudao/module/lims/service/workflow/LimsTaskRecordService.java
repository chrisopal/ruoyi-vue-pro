package cn.iocoder.yudao.module.lims.service.workflow;

import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsWorkflowSaveReqVO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskQcRecordDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskRawRecordDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskReviewDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestTaskDO;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTaskQcRecordMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTaskRawRecordMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTaskReviewMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestTaskMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lims.enums.ErrorCodeConstants.TEST_TASK_NOT_EXISTS;

@Service
public class LimsTaskRecordService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Resource
    private LimsTestTaskMapper taskMapper;
    @Resource
    private LimsTaskRawRecordMapper rawRecordMapper;
    @Resource
    private LimsTaskQcRecordMapper qcRecordMapper;
    @Resource
    private LimsTaskReviewMapper reviewMapper;
    @Resource
    private LimsTaskLifecycleService lifecycleService;

    @Transactional(rollbackFor = Exception.class)
    public Long submitRawRecord(LimsWorkflowSaveReqVO reqVO) {
        LimsTestTaskDO task = validateTask(resolveTaskId(reqVO));
        String submittedTime = StringUtils.hasText(reqVO.getSubmittedTime()) ? reqVO.getSubmittedTime() : now();
        LimsTaskRawRecordDO record = new LimsTaskRawRecordDO();
        record.setTaskId(task.getId());
        record.setTaskNo(task.getTaskNo());
        record.setRecordType(reqVO.getRecordType());
        record.setRecordJson(reqVO.getRecordJson());
        record.setAttachmentUrl(reqVO.getAttachmentUrl());
        record.setVersionNo(reqVO.getVersionNo() == null ? 1L : reqVO.getVersionNo());
        record.setSubmittedBy(reqVO.getSubmittedBy());
        record.setSubmittedTime(submittedTime);
        record.setStatus(StringUtils.hasText(reqVO.getStatus()) ? reqVO.getStatus() : "submitted");
        rawRecordMapper.insert(record);

        String reason = fallback(reqVO.getRemark(), "原始记录已提交");
        lifecycleService.transition(task.getId(), LimsTaskStatus.DATA_SUBMITTED,
                LimsTaskEventType.RECORD_SUBMITTED, reason, null);
        LimsTestTaskDO update = new LimsTestTaskDO();
        update.setId(task.getId());
        update.setQcStatus(LimsTaskReviewStatus.PENDING);
        update.setReportEligible(false);
        taskMapper.updateById(update);
        return record.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public Long submitQcRecord(LimsWorkflowSaveReqVO reqVO) {
        LimsTestTaskDO task = validateTask(resolveTaskId(reqVO));
        LimsTaskQcRecordDO record = new LimsTaskQcRecordDO();
        record.setTaskId(task.getId());
        record.setTaskNo(task.getTaskNo());
        record.setQcType(reqVO.getQcType());
        record.setQcRuleSnapshot(reqVO.getQcRuleSnapshot());
        record.setQcDataJson(reqVO.getQcDataJson());
        record.setQcResult(reqVO.getQcResult());
        record.setReviewComment(reqVO.getReviewComment());
        qcRecordMapper.insert(record);

        boolean approved = isApproved(reqVO.getQcResult());
        String reason = fallback(reqVO.getReviewComment(), approved ? "QC通过，待技术复核" : "QC未通过");
        lifecycleService.transition(task.getId(), approved ? LimsTaskStatus.REVIEWING : LimsTaskStatus.REWORK,
                approved ? LimsTaskEventType.REVIEW_SUBMITTED : LimsTaskEventType.REJECTED, reason, null);

        LimsTestTaskDO update = new LimsTestTaskDO();
        update.setId(task.getId());
        update.setQcStatus(approved ? LimsTaskReviewStatus.APPROVED : LimsTaskReviewStatus.REJECTED);
        if (approved) {
            update.setReviewStatus(LimsTaskReviewStatus.PENDING);
        } else {
            update.setBlockReason(reason);
        }
        update.setReportEligible(false);
        taskMapper.updateById(update);
        return record.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void approveReview(LimsWorkflowSaveReqVO reqVO) {
        LimsTestTaskDO task = validateTask(resolveTaskId(reqVO));
        String reviewTime = now();
        String remark = fallback(reqVO.getRemark(), "技术复核通过");
        LimsTaskReviewDO review = buildReview(task, reqVO, LimsTaskReviewStatus.APPROVED, reviewTime, remark);
        reviewMapper.insert(review);

        lifecycleService.transition(task.getId(), LimsTaskStatus.APPROVED,
                LimsTaskEventType.APPROVED, remark, null);
        LimsTestTaskDO update = new LimsTestTaskDO();
        update.setId(task.getId());
        update.setReviewerId(reqVO.getReviewerId());
        update.setReviewStatus(LimsTaskReviewStatus.APPROVED);
        update.setReportEligible(true);
        update.setActualEndTime(reviewTime);
        taskMapper.updateById(update);
    }

    @Transactional(rollbackFor = Exception.class)
    public void rejectReview(LimsWorkflowSaveReqVO reqVO) {
        LimsTestTaskDO task = validateTask(resolveTaskId(reqVO));
        String reviewTime = now();
        String remark = fallback(reqVO.getRemark(), "技术复核驳回");
        LimsTaskReviewDO review = buildReview(task, reqVO, LimsTaskReviewStatus.REJECTED, reviewTime, remark);
        reviewMapper.insert(review);

        lifecycleService.transition(task.getId(), LimsTaskStatus.REWORK,
                LimsTaskEventType.REJECTED, remark, null);
        LimsTestTaskDO update = new LimsTestTaskDO();
        update.setId(task.getId());
        update.setReviewerId(reqVO.getReviewerId());
        update.setReviewStatus(LimsTaskReviewStatus.REJECTED);
        update.setReportEligible(false);
        update.setBlockReason(remark);
        taskMapper.updateById(update);
    }

    public List<LimsTaskReviewDO> listReviewsByTaskId(Long taskId) {
        validateTask(taskId);
        return reviewMapper.selectListByTaskId(taskId);
    }

    private LimsTaskReviewDO buildReview(LimsTestTaskDO task, LimsWorkflowSaveReqVO reqVO,
                                         String reviewStatus, String reviewTime, String comment) {
        LimsTaskReviewDO review = new LimsTaskReviewDO();
        review.setTaskId(task.getId());
        review.setTaskNo(task.getTaskNo());
        review.setReviewType(fallback(reqVO.getReviewType(), "technical"));
        review.setReviewStatus(reviewStatus);
        review.setReviewerId(reqVO.getReviewerId());
        review.setReviewTime(reviewTime);
        review.setComment(comment);
        review.setSnapshotHash(reqVO.getSnapshotHash());
        return review;
    }

    private LimsTestTaskDO validateTask(Long taskId) {
        LimsTestTaskDO task = taskId == null ? null : taskMapper.selectById(taskId);
        if (task == null) {
            throw exception(TEST_TASK_NOT_EXISTS);
        }
        return task;
    }

    private Long resolveTaskId(LimsWorkflowSaveReqVO reqVO) {
        return reqVO.getTaskId() != null ? reqVO.getTaskId() : reqVO.getId();
    }

    private boolean isApproved(String qcResult) {
        if (!StringUtils.hasText(qcResult)) {
            return false;
        }
        String normalized = qcResult.trim().toLowerCase(Locale.ROOT);
        return "approved".equals(normalized) || "pass".equals(normalized)
                || "passed".equals(normalized) || "通过".equals(qcResult.trim())
                || "合格".equals(qcResult.trim());
    }

    private String fallback(String value, String defaultValue) {
        return StringUtils.hasText(value) ? value : defaultValue;
    }

    private static String now() {
        return LocalDateTime.now().format(TIME_FORMATTER);
    }

}
