package cn.iocoder.yudao.module.lims.service.workflow;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsWorkflowSaveReqVO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskScheduleDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestTaskDO;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTaskScheduleMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestTaskMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LimsTaskScheduleServiceTest extends BaseMockitoUnitTest {

    @InjectMocks
    private LimsTaskScheduleService service;

    @Mock
    private LimsTestTaskMapper taskMapper;
    @Mock
    private LimsTaskScheduleMapper scheduleMapper;
    @Mock
    private LimsTaskLifecycleService lifecycleService;

    @Test
    void schedule_shouldPersistWindowAndMoveTaskToScheduled() {
        LimsTestTaskDO task = task(10L);
        when(taskMapper.selectById(10L)).thenReturn(task);
        when(scheduleMapper.selectActiveByEquipmentId(88L)).thenReturn(List.of());
        when(scheduleMapper.selectActiveByAssignedUserId(99L)).thenReturn(List.of());

        service.schedule(command());

        verify(scheduleMapper).insert(argThat((LimsTaskScheduleDO schedule) ->
                Long.valueOf(10L).equals(schedule.getTaskId())
                        && Long.valueOf(88L).equals(schedule.getEquipmentId())
                        && Long.valueOf(99L).equals(schedule.getAssignedUserId())
                        && LimsTaskScheduleStatus.SCHEDULED.equals(schedule.getScheduleStatus())
                        && Boolean.FALSE.equals(schedule.getLocked())));
        verify(taskMapper).updateById(argThat((LimsTestTaskDO updated) ->
                LimsTaskStatus.SCHEDULED.equals(updated.getTaskStatus())
                        && LimsTaskStatus.SCHEDULED.equals(updated.getStatus())
                        && LimsTaskScheduleStatus.SCHEDULED.equals(updated.getScheduleStatus())
                        && "2026-06-15 09:00:00".equals(updated.getPlannedStartTime())));
        verify(lifecycleService).writeEvent(10L, "REQ-001-T01", LimsTaskEventType.SCHEDULED,
                LimsTaskStatus.GENERATED, LimsTaskStatus.SCHEDULED, "任务已排程", null);
    }

    @Test
    void schedule_shouldRejectOverlappingEquipmentWindow() {
        when(taskMapper.selectById(10L)).thenReturn(task(10L));
        when(scheduleMapper.selectActiveByEquipmentId(88L)).thenReturn(List.of(existing()));

        assertThrows(Exception.class, () -> service.schedule(command()));
    }

    @Test
    void markReady_shouldDelegateLifecycleReadinessTransition() {
        service.markReady(10L);

        verify(lifecycleService).markReady(10L);
    }

    private static LimsWorkflowSaveReqVO command() {
        LimsWorkflowSaveReqVO req = new LimsWorkflowSaveReqVO();
        req.setTaskId(10L);
        req.setEquipmentId(88L);
        req.setAssignedUserId(99L);
        req.setPlannedStartTime("2026-06-15 09:00:00");
        req.setPlannedEndTime("2026-06-15 10:00:00");
        return req;
    }

    private static LimsTestTaskDO task(Long id) {
        LimsTestTaskDO task = new LimsTestTaskDO();
        task.setId(id);
        task.setRequestId(1L);
        task.setSampleId(2L);
        task.setTaskNo("REQ-001-T01");
        task.setTaskStatus(LimsTaskStatus.GENERATED);
        task.setStatus(LimsTaskStatus.GENERATED);
        task.setDurationMinutes(60L);
        return task;
    }

    private static LimsTaskScheduleDO existing() {
        LimsTaskScheduleDO schedule = new LimsTaskScheduleDO();
        schedule.setTaskId(99L);
        schedule.setPlannedStartTime("2026-06-15 09:30:00");
        schedule.setPlannedEndTime("2026-06-15 10:30:00");
        schedule.setScheduleStatus(LimsTaskScheduleStatus.SCHEDULED);
        return schedule;
    }

}
