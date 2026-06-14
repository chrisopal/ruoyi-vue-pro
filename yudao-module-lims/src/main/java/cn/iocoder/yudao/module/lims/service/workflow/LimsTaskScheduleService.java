package cn.iocoder.yudao.module.lims.service.workflow;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsWorkflowPageReqVO;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsWorkflowSaveReqVO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskScheduleDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestTaskDO;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTaskScheduleMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestTaskMapper;
import cn.iocoder.yudao.module.lims.service.workflow.gateway.EquipmentGateway;
import cn.iocoder.yudao.module.lims.service.workflow.model.AvailableEquipment;
import cn.iocoder.yudao.module.lims.service.workflow.model.CalibrationEvidence;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lims.enums.ErrorCodeConstants.TEST_TASK_EQUIPMENT_UNAVAILABLE;
import static cn.iocoder.yudao.module.lims.enums.ErrorCodeConstants.TEST_TASK_NOT_EXISTS;
import static cn.iocoder.yudao.module.lims.enums.ErrorCodeConstants.TEST_TASK_SCHEDULE_CONFLICT;

@Service
public class LimsTaskScheduleService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Resource
    private LimsTestTaskMapper taskMapper;
    @Resource
    private LimsTaskScheduleMapper scheduleMapper;
    @Resource
    private LimsTaskLifecycleService lifecycleService;
    @Resource
    private EquipmentGateway equipmentGateway;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional(rollbackFor = Exception.class)
    public Long schedule(LimsWorkflowSaveReqVO reqVO) {
        LimsTestTaskDO task = validateTask(reqVO.getTaskId() == null ? reqVO.getId() : reqVO.getTaskId());
        ScheduleCommand command = normalizeCommand(task, reqVO);
        assertNoConflicts(command);
        LimsTaskScheduleDO schedule = new LimsTaskScheduleDO();
        schedule.setTaskId(task.getId());
        schedule.setRequestId(task.getRequestId());
        schedule.setSampleId(task.getSampleId());
        schedule.setEquipmentId(command.equipmentId());
        schedule.setAssignedUserId(command.assignedUserId());
        schedule.setPlannedStartTime(command.plannedStartTime());
        schedule.setPlannedEndTime(command.plannedEndTime());
        schedule.setScheduleStatus(LimsTaskScheduleStatus.SCHEDULED);
        schedule.setLocked(false);
        scheduleMapper.insert(schedule);

        String fromStatus = currentStatus(task);
        bindEquipment(task, command.equipmentId());
        task.setAssignedUserId(command.assignedUserId());
        task.setPlannedStartTime(command.plannedStartTime());
        task.setPlannedEndTime(command.plannedEndTime());
        task.setDurationMinutes(command.durationMinutes());
        task.setScheduleStatus(LimsTaskScheduleStatus.SCHEDULED);
        task.setTaskStatus(LimsTaskStatus.SCHEDULED);
        task.setStatus(LimsTaskStatus.SCHEDULED);
        taskMapper.updateById(task);
        lifecycleService.writeEvent(task.getId(), task.getTaskNo(), LimsTaskEventType.SCHEDULED,
                fromStatus, LimsTaskStatus.SCHEDULED, "任务已排程", null);
        return schedule.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public Long scheduleDefault(Long taskId) {
        LimsTestTaskDO task = validateTask(taskId);
        LimsWorkflowSaveReqVO reqVO = new LimsWorkflowSaveReqVO();
        reqVO.setTaskId(task.getId());
        reqVO.setEquipmentId(task.getEquipmentId());
        reqVO.setAssignedUserId(task.getAssignedUserId());
        reqVO.setPlannedStartTime(task.getPlannedStartTime());
        reqVO.setPlannedEndTime(task.getPlannedEndTime());
        reqVO.setDurationMinutes(task.getDurationMinutes());
        return schedule(reqVO);
    }

    public void markReady(Long taskId) {
        lifecycleService.markReady(taskId);
    }

    public PageResult<LimsTaskScheduleDO> getSchedulePage(LimsWorkflowPageReqVO reqVO) {
        return scheduleMapper.selectPage(reqVO);
    }

    private ScheduleCommand normalizeCommand(LimsTestTaskDO task, LimsWorkflowSaveReqVO reqVO) {
        Long equipmentId = reqVO.getEquipmentId() == null ? task.getEquipmentId() : reqVO.getEquipmentId();
        Long assignedUserId = reqVO.getAssignedUserId() == null ? task.getAssignedUserId() : reqVO.getAssignedUserId();
        String plannedStartTime = StringUtils.hasText(reqVO.getPlannedStartTime()) ? reqVO.getPlannedStartTime()
                : StringUtils.hasText(task.getPlannedStartTime()) ? task.getPlannedStartTime() : LocalDateTime.now().format(FORMATTER);
        String plannedEndTime = StringUtils.hasText(reqVO.getPlannedEndTime()) ? reqVO.getPlannedEndTime()
                : StringUtils.hasText(task.getPlannedEndTime()) ? task.getPlannedEndTime()
                : parse(plannedStartTime).plusMinutes(task.getDurationMinutes() == null ? 60L : Math.max(task.getDurationMinutes(), 1L)).format(FORMATTER);
        Long durationMinutes = reqVO.getDurationMinutes() == null ? task.getDurationMinutes() : Math.max(reqVO.getDurationMinutes(), 1L);
        if (durationMinutes == null) {
            durationMinutes = java.time.Duration.between(parse(plannedStartTime), parse(plannedEndTime)).toMinutes();
        }
        return new ScheduleCommand(task.getId(), equipmentId, assignedUserId, plannedStartTime, plannedEndTime, Math.max(durationMinutes, 1L));
    }

    private void bindEquipment(LimsTestTaskDO task, Long equipmentId) {
        if (equipmentId == null) {
            return;
        }
        AvailableEquipment selected = equipmentGateway.getAvailableEquipment(null, task.getTestItem()).stream()
                .filter(equipment -> equipmentId.equals(equipment.equipmentId()))
                .findFirst()
                .orElseThrow(() -> exception(TEST_TASK_EQUIPMENT_UNAVAILABLE));
        List<CalibrationEvidence> evidence = equipmentGateway.getCurrentCalibrationEvidence(selected.equipmentId());
        task.setEquipmentId(selected.equipmentId());
        task.setEquipmentCode(selected.equipmentCode());
        task.setEquipmentName(selected.equipmentName());
        task.setEquipmentSnapshot(writeJson(selected));
        task.setEquipmentEvidenceSnapshot(writeJson(evidence));
    }

    private void assertNoConflicts(ScheduleCommand command) {
        if (command.equipmentId() != null && hasOverlap(scheduleMapper.selectActiveByEquipmentId(command.equipmentId()), command)) {
            throw exception(TEST_TASK_SCHEDULE_CONFLICT);
        }
        if (command.assignedUserId() != null && hasOverlap(scheduleMapper.selectActiveByAssignedUserId(command.assignedUserId()), command)) {
            throw exception(TEST_TASK_SCHEDULE_CONFLICT);
        }
    }

    private boolean hasOverlap(List<LimsTaskScheduleDO> existing, ScheduleCommand command) {
        LocalDateTime start = parse(command.plannedStartTime());
        LocalDateTime end = parse(command.plannedEndTime());
        if (!end.isAfter(start)) {
            throw exception(TEST_TASK_SCHEDULE_CONFLICT);
        }
        return existing.stream()
                .filter(item -> !command.taskId().equals(item.getTaskId()))
                .anyMatch(item -> start.isBefore(parse(item.getPlannedEndTime()))
                        && end.isAfter(parse(item.getPlannedStartTime())));
    }

    private static LocalDateTime parse(String value) {
        return LocalDateTime.parse(value, FORMATTER);
    }

    private LimsTestTaskDO validateTask(Long id) {
        LimsTestTaskDO task = id == null ? null : taskMapper.selectById(id);
        if (task == null) {
            throw exception(TEST_TASK_NOT_EXISTS);
        }
        return task;
    }

    private String currentStatus(LimsTestTaskDO task) {
        return StringUtils.hasText(task.getTaskStatus()) ? task.getTaskStatus() : task.getStatus();
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to serialize LIMS equipment snapshot", ex);
        }
    }

    private record ScheduleCommand(Long taskId, Long equipmentId, Long assignedUserId,
                                   String plannedStartTime, String plannedEndTime, Long durationMinutes) {
    }

}
