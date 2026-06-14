package cn.iocoder.yudao.module.lims.service.workflow;

import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestResultDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestTaskDO;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestResultMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestTaskMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lims.enums.ErrorCodeConstants.TEST_TASK_REPORT_BLOCKED;

@Service
public class LimsReportEligibilityService {

    @Resource
    private LimsTestTaskMapper taskMapper;
    @Resource
    private LimsTestResultMapper resultMapper;

    public void assertRequestReportable(Long requestId) {
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
    }

    private String resolveTaskStatus(LimsTestTaskDO task) {
        return StringUtils.hasText(task.getTaskStatus()) ? task.getTaskStatus() : task.getStatus();
    }

}
