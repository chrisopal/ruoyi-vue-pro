package cn.iocoder.yudao.module.lims.service.workflow;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskEventLogDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestTaskDO;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTaskEventLogMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestTaskMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LimsTaskLifecycleServiceTest extends BaseMockitoUnitTest {

    @InjectMocks
    private LimsTaskLifecycleService service;

    @Mock
    private LimsTestTaskMapper taskMapper;
    @Mock
    private LimsTaskEventLogMapper eventLogMapper;

    @Test
    void start_shouldMoveReadyTaskToTestingAndWriteEvent() {
        LimsTestTaskDO task = task(10L, LimsTaskStatus.READY);
        when(taskMapper.selectById(10L)).thenReturn(task);

        service.start(10L);

        verify(taskMapper).updateById(argThat((LimsTestTaskDO updated) ->
                LimsTaskStatus.TESTING.equals(updated.getTaskStatus())
                        && LimsTaskStatus.TESTING.equals(updated.getStatus())
                        && updated.getActualStartTime() != null));
        verify(eventLogMapper).insert(argThat((LimsTaskEventLogDO event) ->
                Long.valueOf(10L).equals(event.getTaskId())
                        && LimsTaskEventType.STARTED.equals(event.getEventType())
                        && LimsTaskStatus.READY.equals(event.getFromStatus())
                        && LimsTaskStatus.TESTING.equals(event.getToStatus())));
    }

    @Test
    void start_shouldRejectGeneratedTask() {
        when(taskMapper.selectById(10L)).thenReturn(task(10L, LimsTaskStatus.GENERATED));

        assertThrows(Exception.class, () -> service.start(10L));
    }

    @Test
    void markReady_shouldMoveScheduledTaskToReadyAndWriteEvent() {
        when(taskMapper.selectById(10L)).thenReturn(task(10L, LimsTaskStatus.SCHEDULED));

        service.markReady(10L);

        verify(taskMapper).updateById(argThat((LimsTestTaskDO updated) ->
                LimsTaskStatus.READY.equals(updated.getTaskStatus())
                        && LimsTaskStatus.READY.equals(updated.getStatus())));
        verify(eventLogMapper).insert(argThat((LimsTaskEventLogDO event) ->
                Long.valueOf(10L).equals(event.getTaskId())
                        && LimsTaskEventType.READINESS_PASSED.equals(event.getEventType())
                        && LimsTaskStatus.SCHEDULED.equals(event.getFromStatus())
                        && LimsTaskStatus.READY.equals(event.getToStatus())));
    }

    @Test
    void transition_shouldRejectGeneratedTaskToReviewing() {
        when(taskMapper.selectById(10L)).thenReturn(task(10L, LimsTaskStatus.GENERATED));

        assertThrows(Exception.class, () -> service.transition(
                10L, LimsTaskStatus.REVIEWING, LimsTaskEventType.REVIEW_SUBMITTED, "检测结果待复核", null));
    }

    private static LimsTestTaskDO task(Long id, String status) {
        LimsTestTaskDO task = new LimsTestTaskDO();
        task.setId(id);
        task.setTaskNo("REQ-001-T01");
        task.setTaskStatus(status);
        task.setStatus(status);
        return task;
    }

}
