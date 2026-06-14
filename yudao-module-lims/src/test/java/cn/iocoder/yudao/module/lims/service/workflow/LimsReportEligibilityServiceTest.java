package cn.iocoder.yudao.module.lims.service.workflow;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestResultDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestTaskDO;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestResultMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestTaskMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class LimsReportEligibilityServiceTest extends BaseMockitoUnitTest {

    @InjectMocks
    private LimsReportEligibilityService service;

    @Mock
    private LimsTestTaskMapper taskMapper;
    @Mock
    private LimsTestResultMapper resultMapper;

    @Test
    void assertRequestReportable_shouldPassWhenTasksAndResultsAreApproved() {
        when(taskMapper.selectListByRequestId(1L)).thenReturn(List.of(
                task(LimsTaskStatus.APPROVED, true),
                task(LimsTaskStatus.COMPLETED, true)));
        when(resultMapper.selectListByRequestId(1L)).thenReturn(List.of(result("approved"), result("approved")));

        assertDoesNotThrow(() -> service.assertRequestReportable(1L));
    }

    @Test
    void assertRequestReportable_shouldRejectTestingTask() {
        when(taskMapper.selectListByRequestId(1L)).thenReturn(List.of(task(LimsTaskStatus.TESTING, false)));
        when(resultMapper.selectListByRequestId(1L)).thenReturn(List.of(result("approved")));

        assertThrows(Exception.class, () -> service.assertRequestReportable(1L));
    }

    @Test
    void assertRequestReportable_shouldRejectUnapprovedResult() {
        when(taskMapper.selectListByRequestId(1L)).thenReturn(List.of(task(LimsTaskStatus.APPROVED, true)));
        when(resultMapper.selectListByRequestId(1L)).thenReturn(List.of(result("recorded")));

        assertThrows(Exception.class, () -> service.assertRequestReportable(1L));
    }

    private static LimsTestTaskDO task(String status, boolean reportEligible) {
        LimsTestTaskDO task = new LimsTestTaskDO();
        task.setTaskStatus(status);
        task.setReportEligible(reportEligible);
        return task;
    }

    private static LimsTestResultDO result(String status) {
        LimsTestResultDO result = new LimsTestResultDO();
        result.setStatus(status);
        return result;
    }

}
