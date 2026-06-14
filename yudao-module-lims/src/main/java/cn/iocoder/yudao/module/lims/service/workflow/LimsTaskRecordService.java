package cn.iocoder.yudao.module.lims.service.workflow;

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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lims.enums.ErrorCodeConstants.TEST_REQUEST_NOT_EXISTS;
import static cn.iocoder.yudao.module.lims.enums.ErrorCodeConstants.TEST_TASK_NOT_EXISTS;
import static cn.iocoder.yudao.module.lims.enums.ErrorCodeConstants.TEST_TASK_RAW_RECORD_REQUIRED;
import static cn.iocoder.yudao.module.lims.enums.ErrorCodeConstants.TEST_TASK_REPORT_BLOCKED;
import static cn.iocoder.yudao.module.lims.enums.ErrorCodeConstants.TEST_TASK_REVIEW_REQUIRED;

@Service
public class LimsTaskRecordService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Resource
    private LimsTestTaskMapper taskMapper;
    @Resource
    private LimsTestRequestMapper requestMapper;
    @Resource
    private LimsTaskRawRecordMapper rawRecordMapper;
    @Resource
    private LimsTaskQcRecordMapper qcRecordMapper;
    @Resource
    private LimsTaskReviewMapper reviewMapper;
    @Resource
    private LimsTestResultMapper resultMapper;
    @Resource
    private LimsTaskLifecycleService lifecycleService;
    @Resource
    private LimsQualityGateService qualityGateService;
    @Resource
    private ObjectMapper objectMapper;

    @Transactional(rollbackFor = Exception.class)
    public Long submitRawRecord(LimsWorkflowSaveReqVO reqVO) {
        LimsTestTaskDO task = validateTask(resolveTaskId(reqVO));
        requireText(reqVO.getRecordType(), TEST_TASK_RAW_RECORD_REQUIRED);
        requireText(reqVO.getRecordJson(), TEST_TASK_RAW_RECORD_REQUIRED);
        validateJson(reqVO.getRecordJson(), TEST_TASK_RAW_RECORD_REQUIRED);
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
        syncResultFromRawRecord(task, reqVO);

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
        requireText(reqVO.getQcType(), TEST_TASK_REVIEW_REQUIRED);
        requireText(reqVO.getQcResult(), TEST_TASK_REVIEW_REQUIRED);
        validateJsonIfPresent(reqVO.getQcRuleSnapshot(), TEST_TASK_REVIEW_REQUIRED);
        validateJsonIfPresent(reqVO.getQcDataJson(), TEST_TASK_REVIEW_REQUIRED);
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
            update.setBlockReason(null);
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
        requireReviewer(reqVO);
        String reviewTime = now();
        String remark = fallback(reqVO.getRemark(), "技术复核通过");
        LimsTaskReviewDO review = buildReview(task, reqVO, LimsTaskReviewStatus.APPROVED, reviewTime, remark);
        assertQualityGateSatisfied(task, review);
        approveTaskResults(task.getId(), reqVO.getReviewerId(), reviewTime);
        reviewMapper.insert(review);
        lifecycleService.transition(task.getId(), LimsTaskStatus.APPROVED,
                LimsTaskEventType.APPROVED, remark, null);
        LimsTestTaskDO update = new LimsTestTaskDO();
        update.setId(task.getId());
        update.setReviewerId(reqVO.getReviewerId());
        update.setReviewStatus(LimsTaskReviewStatus.APPROVED);
        update.setReportEligible(true);
        update.setActualEndTime(reviewTime);
        update.setBlockReason(null);
        taskMapper.updateById(update);
    }

    @Transactional(rollbackFor = Exception.class)
    public void rejectReview(LimsWorkflowSaveReqVO reqVO) {
        LimsTestTaskDO task = validateTask(resolveTaskId(reqVO));
        requireReviewer(reqVO);
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

    private void syncResultFromRawRecord(LimsTestTaskDO task, LimsWorkflowSaveReqVO reqVO) {
        boolean hasResultPayload = StringUtils.hasText(reqVO.getResultValue())
                || StringUtils.hasText(reqVO.getResultUnit())
                || StringUtils.hasText(reqVO.getResultConclusion())
                || StringUtils.hasText(reqVO.getRawData());
        if (!hasResultPayload) {
            return;
        }
        validateJsonIfPresent(reqVO.getRawData(), TEST_TASK_RAW_RECORD_REQUIRED);
        if (StringUtils.hasText(reqVO.getRawData())) {
            qualityGateService.validateResultValues(validateRequest(task.getRequestId()), task, reqVO.getRawData());
        }
        List<LimsTestResultDO> results = resultMapper.selectListByTaskId(task.getId());
        if (results.isEmpty()) {
            LimsTestResultDO result = new LimsTestResultDO();
            result.setRequestId(task.getRequestId());
            result.setRequestNo(task.getRequestNo());
            result.setSampleId(task.getSampleId());
            result.setSampleNo(task.getSampleNo());
            result.setTaskId(task.getId());
            result.setTaskNo(task.getTaskNo());
            result.setResultNo(task.getTaskNo() + "-R01");
            result.setTestItem(task.getTestItem());
            result.setResultValue(reqVO.getResultValue());
            result.setResultUnit(reqVO.getResultUnit());
            result.setResultConclusion(reqVO.getResultConclusion());
            result.setRawData(reqVO.getRawData());
            result.setStatus("recorded");
            resultMapper.insert(result);
            return;
        }
        UpdateWrapper<LimsTestResultDO> update = new UpdateWrapper<LimsTestResultDO>().eq("task_id", task.getId());
        update.set(StringUtils.hasText(reqVO.getResultValue()), "result_value", reqVO.getResultValue());
        update.set(StringUtils.hasText(reqVO.getResultUnit()), "result_unit", reqVO.getResultUnit());
        update.set(StringUtils.hasText(reqVO.getResultConclusion()), "result_conclusion", reqVO.getResultConclusion());
        update.set(StringUtils.hasText(reqVO.getRawData()), "raw_data", reqVO.getRawData());
        update.set("status", "recorded");
        resultMapper.update(null, update);
    }

    private void assertQualityGateSatisfied(LimsTestTaskDO task, LimsTaskReviewDO currentReview) {
        LimsTestRequestDO request = validateRequest(task.getRequestId());
        List<LimsTaskReviewDO> reviews = new java.util.ArrayList<>(reviewMapper.selectListByTaskId(task.getId()));
        reviews.add(currentReview);
        qualityGateService.assertQcAndEvidenceComplete(request, List.of(task),
                Map.of(task.getId(), rawRecordMapper.selectListByTaskId(task.getId())),
                Map.of(task.getId(), qcRecordMapper.selectListByTaskId(task.getId())),
                Map.of(task.getId(), reviews));
    }

    private void approveTaskResults(Long taskId, Long reviewerId, String reviewTime) {
        List<LimsTestResultDO> results = resultMapper.selectListByTaskId(taskId);
        if (results.isEmpty()) {
            throw exception(TEST_TASK_REPORT_BLOCKED);
        }
        resultMapper.update(null, new UpdateWrapper<LimsTestResultDO>()
                .eq("task_id", taskId)
                .set("status", LimsTaskReviewStatus.APPROVED)
                .set("reviewer_id", reviewerId)
                .set("reviewed_time", reviewTime));
    }

    private void requireReviewer(LimsWorkflowSaveReqVO reqVO) {
        if (reqVO.getReviewerId() == null) {
            throw exception(TEST_TASK_REVIEW_REQUIRED);
        }
    }

    private LimsTestTaskDO validateTask(Long taskId) {
        LimsTestTaskDO task = taskId == null ? null : taskMapper.selectById(taskId);
        if (task == null) {
            throw exception(TEST_TASK_NOT_EXISTS);
        }
        return task;
    }

    private LimsTestRequestDO validateRequest(Long requestId) {
        LimsTestRequestDO request = requestId == null ? null : requestMapper.selectById(requestId);
        if (request == null) {
            throw exception(TEST_REQUEST_NOT_EXISTS);
        }
        return request;
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

    private void requireText(String value, cn.iocoder.yudao.framework.common.exception.ErrorCode errorCode) {
        if (!StringUtils.hasText(value)) {
            throw exception(errorCode);
        }
    }

    private void validateJsonIfPresent(String value, cn.iocoder.yudao.framework.common.exception.ErrorCode errorCode) {
        if (StringUtils.hasText(value)) {
            validateJson(value, errorCode);
        }
    }

    private void validateJson(String value, cn.iocoder.yudao.framework.common.exception.ErrorCode errorCode) {
        try {
            objectMapper.readTree(value);
        } catch (JsonProcessingException ex) {
            throw exception(errorCode);
        }
    }

    private static String now() {
        return LocalDateTime.now().format(TIME_FORMATTER);
    }

}
