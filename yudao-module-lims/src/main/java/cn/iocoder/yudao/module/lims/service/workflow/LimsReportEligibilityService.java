package cn.iocoder.yudao.module.lims.service.workflow;

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
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lims.enums.ErrorCodeConstants.TEST_REQUEST_NOT_EXISTS;
import static cn.iocoder.yudao.module.lims.enums.ErrorCodeConstants.TEST_TASK_REPORT_BLOCKED;

@Service
public class LimsReportEligibilityService {

    @Resource
    private LimsTestRequestMapper requestMapper;
    @Resource
    private LimsTestTaskMapper taskMapper;
    @Resource
    private LimsTestResultMapper resultMapper;
    @Resource
    private LimsTaskRawRecordMapper rawRecordMapper;
    @Resource
    private LimsTaskQcRecordMapper qcRecordMapper;
    @Resource
    private LimsTaskReviewMapper reviewMapper;
    @Resource
    private LimsQualityGateService qualityGateService;
    @Resource
    private ExecutionPlanResolver executionPlanResolver;

    public void assertRequestReportable(Long requestId) {
        LimsTestRequestDO request = validateRequest(requestId);
        List<LimsTestTaskDO> tasks = taskMapper.selectListByRequestId(requestId);
        List<LimsTestResultDO> results = resultMapper.selectListByRequestId(requestId);
        if (tasks.isEmpty() || results.isEmpty()) {
            throw exception(TEST_TASK_REPORT_BLOCKED);
        }
        boolean allTasksAllowed = tasks.stream().allMatch(task ->
                Boolean.TRUE.equals(task.getReportEligible())
                        && LimsTaskStatus.REPORT_ALLOWED.contains(resolveTaskStatus(task)));
        boolean allResultsApproved = results.stream().allMatch(result -> "approved".equalsIgnoreCase(result.getStatus()));
        if (!allTasksAllowed || !allResultsApproved) {
            throw exception(TEST_TASK_REPORT_BLOCKED);
        }
        ExecutionPlanResolver.ResolvedExecutionPlan executionPlan = executionPlanResolver.resolve(request);
        qualityGateService.assertExecutionPlanGatesComplete(executionPlan.plan(), tasks, rawRecordsByTaskId(tasks),
                qcRecordsByTaskId(tasks), reviewsByTaskId(tasks));
    }

    private LimsTestRequestDO validateRequest(Long requestId) {
        LimsTestRequestDO request = requestId == null ? null : requestMapper.selectById(requestId);
        if (request == null) {
            throw exception(TEST_REQUEST_NOT_EXISTS);
        }
        return request;
    }

    private Map<Long, List<LimsTaskRawRecordDO>> rawRecordsByTaskId(List<LimsTestTaskDO> tasks) {
        Map<Long, List<LimsTaskRawRecordDO>> records = new HashMap<>();
        tasks.forEach(task -> records.put(task.getId(), rawRecordMapper.selectListByTaskId(task.getId())));
        return records;
    }

    private Map<Long, List<LimsTaskQcRecordDO>> qcRecordsByTaskId(List<LimsTestTaskDO> tasks) {
        Map<Long, List<LimsTaskQcRecordDO>> records = new HashMap<>();
        tasks.forEach(task -> records.put(task.getId(), qcRecordMapper.selectListByTaskId(task.getId())));
        return records;
    }

    private Map<Long, List<LimsTaskReviewDO>> reviewsByTaskId(List<LimsTestTaskDO> tasks) {
        Map<Long, List<LimsTaskReviewDO>> records = new HashMap<>();
        tasks.forEach(task -> records.put(task.getId(), reviewMapper.selectListByTaskId(task.getId())));
        return records;
    }

    private String resolveTaskStatus(LimsTestTaskDO task) {
        return StringUtils.hasText(task.getTaskStatus()) ? task.getTaskStatus() : task.getStatus();
    }

}
