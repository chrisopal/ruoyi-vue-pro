package cn.iocoder.yudao.module.lims.service.workflow;

import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskEventLogDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestTaskDO;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTaskEventLogMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestTaskMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lims.enums.ErrorCodeConstants.TEST_TASK_INVALID_STATUS_TRANSITION;
import static cn.iocoder.yudao.module.lims.enums.ErrorCodeConstants.TEST_TASK_NOT_EXISTS;

@Service
public class LimsTaskLifecycleService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final Map<String, Set<String>> ALLOWED = Map.ofEntries(
            Map.entry(LimsTaskStatus.GENERATED, Set.of(LimsTaskStatus.SCHEDULED, LimsTaskStatus.ASSIGNED, LimsTaskStatus.CANCELLED, LimsTaskStatus.HOLD)),
            Map.entry(LimsTaskStatus.SCHEDULED, Set.of(LimsTaskStatus.ASSIGNED, LimsTaskStatus.READY, LimsTaskStatus.CANCELLED, LimsTaskStatus.HOLD)),
            Map.entry(LimsTaskStatus.ASSIGNED, Set.of(LimsTaskStatus.READY, LimsTaskStatus.CANCELLED, LimsTaskStatus.HOLD)),
            Map.entry(LimsTaskStatus.READY, Set.of(LimsTaskStatus.TESTING, LimsTaskStatus.HOLD)),
            Map.entry(LimsTaskStatus.TESTING, Set.of(LimsTaskStatus.DATA_SUBMITTED, LimsTaskStatus.HOLD)),
            Map.entry(LimsTaskStatus.DATA_SUBMITTED, Set.of(LimsTaskStatus.REVIEWING, LimsTaskStatus.REWORK, LimsTaskStatus.HOLD)),
            Map.entry(LimsTaskStatus.REVIEWING, Set.of(LimsTaskStatus.APPROVED, LimsTaskStatus.REWORK, LimsTaskStatus.HOLD)),
            Map.entry(LimsTaskStatus.REWORK, Set.of(LimsTaskStatus.TESTING, LimsTaskStatus.HOLD)),
            Map.entry(LimsTaskStatus.APPROVED, Set.of(LimsTaskStatus.COMPLETED, LimsTaskStatus.HOLD)),
            Map.entry(LimsTaskStatus.COMPLETED, Set.of(LimsTaskStatus.REPORTED)),
            Map.entry(LimsTaskStatus.HOLD, Set.of(LimsTaskStatus.GENERATED, LimsTaskStatus.SCHEDULED, LimsTaskStatus.ASSIGNED,
                    LimsTaskStatus.READY, LimsTaskStatus.TESTING, LimsTaskStatus.DATA_SUBMITTED, LimsTaskStatus.REVIEWING,
                    LimsTaskStatus.REWORK))
    );

    @Resource
    private LimsTestTaskMapper taskMapper;
    @Resource
    private LimsTaskEventLogMapper eventLogMapper;

    @Transactional(rollbackFor = Exception.class)
    public void start(Long taskId) {
        LimsTestTaskDO task = validateTask(taskId);
        String fromStatus = currentStatus(task);
        ensureAllowed(fromStatus, LimsTaskStatus.TESTING);
        task.setTaskStatus(LimsTaskStatus.TESTING);
        task.setStatus(LimsTaskStatus.TESTING);
        task.setActualStartTime(now());
        taskMapper.updateById(task);
        writeEvent(task.getId(), task.getTaskNo(), LimsTaskEventType.STARTED, fromStatus, LimsTaskStatus.TESTING, null, null);
    }

    @Transactional(rollbackFor = Exception.class)
    public void markReady(Long taskId) {
        transition(taskId, LimsTaskStatus.READY, LimsTaskEventType.READINESS_PASSED, "任务排程与资源确认完成", null);
    }

    @Transactional(rollbackFor = Exception.class)
    public void transition(Long taskId, String toStatus, String eventType, String reason, String payloadJson) {
        LimsTestTaskDO task = validateTask(taskId);
        String fromStatus = currentStatus(task);
        ensureAllowed(fromStatus, toStatus);
        task.setTaskStatus(toStatus);
        task.setStatus(toStatus);
        taskMapper.updateById(task);
        writeEvent(task.getId(), task.getTaskNo(), eventType, fromStatus, toStatus, reason, payloadJson);
    }

    public void writeEvent(Long taskId, String taskNo, String eventType, String fromStatus, String toStatus,
                           String reason, String payloadJson) {
        LimsTaskEventLogDO event = new LimsTaskEventLogDO();
        event.setTaskId(taskId);
        event.setTaskNo(taskNo);
        event.setEventType(eventType);
        event.setFromStatus(fromStatus);
        event.setToStatus(toStatus);
        event.setEventTime(now());
        event.setReason(reason);
        event.setPayloadJson(payloadJson);
        eventLogMapper.insert(event);
    }

    private void ensureAllowed(String fromStatus, String toStatus) {
        if (!ALLOWED.getOrDefault(fromStatus, Set.of()).contains(toStatus)) {
            throw exception(TEST_TASK_INVALID_STATUS_TRANSITION);
        }
    }

    private LimsTestTaskDO validateTask(Long taskId) {
        LimsTestTaskDO task = taskId == null ? null : taskMapper.selectById(taskId);
        if (task == null) {
            throw exception(TEST_TASK_NOT_EXISTS);
        }
        return task;
    }

    private String currentStatus(LimsTestTaskDO task) {
        return StringUtils.hasText(task.getTaskStatus()) ? task.getTaskStatus() : task.getStatus();
    }

    private static String now() {
        return LocalDateTime.now().format(TIME_FORMATTER);
    }

}
