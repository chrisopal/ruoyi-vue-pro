package cn.iocoder.yudao.module.lims.service.workflow;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestRequestDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestResultDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestTaskDO;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTaskQcRecordMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTaskRawRecordMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTaskReviewMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestRequestMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestResultMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestTaskMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LimsReportEligibilityServiceTest extends BaseMockitoUnitTest {

    @InjectMocks
    private LimsReportEligibilityService service;

    @Mock
    private LimsTestRequestMapper requestMapper;
    @Mock
    private LimsTestTaskMapper taskMapper;
    @Mock
    private LimsTestResultMapper resultMapper;
    @Mock
    private LimsTaskRawRecordMapper rawRecordMapper;
    @Mock
    private LimsTaskQcRecordMapper qcRecordMapper;
    @Mock
    private LimsTaskReviewMapper reviewMapper;
    @Mock
    private LimsQualityGateService qualityGateService;
    @Mock
    private ExecutionPlanResolver executionPlanResolver;

    @Test
    void assertRequestReportable_shouldPassWhenTasksAndResultsAreApproved() throws Exception {
        LimsTestRequestDO request = request();
        LimsTestTaskDO approvedTask = task(10L, LimsTaskStatus.APPROVED, true);
        LimsTestTaskDO completedTask = task(20L, LimsTaskStatus.COMPLETED, true);
        when(requestMapper.selectById(1L)).thenReturn(request);
        when(taskMapper.selectListByRequestId(1L)).thenReturn(List.of(approvedTask, completedTask));
        when(resultMapper.selectListByRequestId(1L)).thenReturn(List.of(result("approved"), result("approved")));
        when(executionPlanResolver.resolve(request)).thenReturn(new ExecutionPlanResolver.ResolvedExecutionPlan(
                new ObjectMapper().readTree("{\"taskPlans\":[]}"), "generated"));

        assertDoesNotThrow(() -> service.assertRequestReportable(1L));
        verify(qualityGateService).assertExecutionPlanGatesComplete(any(), eq(List.of(approvedTask, completedTask)),
                anyMap(), anyMap(), anyMap());
    }

    @Test
    void assertRequestReportable_shouldRejectTestingTask() {
        when(requestMapper.selectById(1L)).thenReturn(request());
        when(taskMapper.selectListByRequestId(1L)).thenReturn(List.of(task(LimsTaskStatus.TESTING, false)));
        when(resultMapper.selectListByRequestId(1L)).thenReturn(List.of(result("approved")));

        assertThrows(Exception.class, () -> service.assertRequestReportable(1L));
    }

    @Test
    void assertRequestReportable_shouldRejectUnapprovedResult() {
        when(requestMapper.selectById(1L)).thenReturn(request());
        when(taskMapper.selectListByRequestId(1L)).thenReturn(List.of(task(LimsTaskStatus.APPROVED, true)));
        when(resultMapper.selectListByRequestId(1L)).thenReturn(List.of(result("recorded")));

        assertThrows(Exception.class, () -> service.assertRequestReportable(1L));
    }

    private static LimsTestTaskDO task(String status, boolean reportEligible) {
        return task(10L, status, reportEligible);
    }

    private static LimsTestTaskDO task(Long id, String status, boolean reportEligible) {
        LimsTestTaskDO task = new LimsTestTaskDO();
        task.setId(id);
        task.setTaskStatus(status);
        task.setReportEligible(reportEligible);
        return task;
    }

    private static LimsTestResultDO result(String status) {
        LimsTestResultDO result = new LimsTestResultDO();
        result.setStatus(status);
        return result;
    }

    private static LimsTestRequestDO request() {
        LimsTestRequestDO request = new LimsTestRequestDO();
        request.setId(1L);
        request.setWorkflowSnapshot("{}");
        return request;
    }

}
