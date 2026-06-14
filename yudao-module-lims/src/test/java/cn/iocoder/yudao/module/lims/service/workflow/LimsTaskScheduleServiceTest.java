package cn.iocoder.yudao.module.lims.service.workflow;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsWorkflowSaveReqVO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskScheduleDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestRequestDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestTaskDO;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTaskScheduleMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestRequestMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestTaskMapper;
import cn.iocoder.yudao.module.lims.service.workflow.gateway.EquipmentGateway;
import cn.iocoder.yudao.module.lims.service.workflow.gateway.PersonnelGateway;
import cn.iocoder.yudao.module.lims.service.workflow.model.AvailableEquipment;
import cn.iocoder.yudao.module.lims.service.workflow.model.AvailablePersonnel;
import cn.iocoder.yudao.module.lims.service.workflow.model.CalibrationEvidence;
import cn.iocoder.yudao.module.lims.service.workflow.model.PersonnelAuthorizationEvidence;
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
    private LimsTestRequestMapper requestMapper;
    @Mock
    private LimsTaskLifecycleService lifecycleService;
    @Mock
    private EquipmentGateway equipmentGateway;
    @Mock
    private PersonnelGateway personnelGateway;

    @Test
    void schedule_shouldPersistWindowAndMoveTaskToScheduled() {
        LimsTestTaskDO task = task(10L);
        when(taskMapper.selectById(10L)).thenReturn(task);
        when(requestMapper.selectById(1L)).thenReturn(request("ENV"));
        when(scheduleMapper.selectActiveByEquipmentId(88L)).thenReturn(List.of());
        when(scheduleMapper.selectActiveByAssignedUserId(99L)).thenReturn(List.of());
        when(equipmentGateway.getAvailableEquipment("ENV", "PH")).thenReturn(List.of(equipment()));
        when(equipmentGateway.getCurrentCalibrationEvidence(88L)).thenReturn(List.of(evidence()));
        when(personnelGateway.getAvailablePersonnel("PH", null, 88L)).thenReturn(List.of(personnel()));
        when(personnelGateway.getCurrentAuthorizationEvidence(99L, "PH", null, 88L)).thenReturn(List.of(personnelEvidence()));

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
                        && "2026-06-15 09:00:00".equals(updated.getPlannedStartTime())
                        && Long.valueOf(88L).equals(updated.getEquipmentId())
                        && "PH-METER-001".equals(updated.getEquipmentCode())
                        && "酸度计".equals(updated.getEquipmentName())
                        && updated.getEquipmentSnapshot().contains("PH-METER-001")
                        && updated.getEquipmentEvidenceSnapshot().contains("CERT-001")
                        && Long.valueOf(99L).equals(updated.getAssignedUserId())
                        && "张三".equals(updated.getAssignedUserName())
                        && updated.getPersonnelSnapshot().contains("张三")
                        && updated.getPersonnelEvidenceSnapshot().contains("AUTH-001")));
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
    void schedule_shouldRejectUnauthorizedPersonnel() {
        LimsTestTaskDO task = task(10L);
        when(taskMapper.selectById(10L)).thenReturn(task);
        when(requestMapper.selectById(1L)).thenReturn(request("ENV"));
        when(scheduleMapper.selectActiveByEquipmentId(88L)).thenReturn(List.of());
        when(scheduleMapper.selectActiveByAssignedUserId(99L)).thenReturn(List.of());
        when(equipmentGateway.getAvailableEquipment("ENV", "PH")).thenReturn(List.of(equipment()));
        when(equipmentGateway.getCurrentCalibrationEvidence(88L)).thenReturn(List.of(evidence()));
        when(personnelGateway.getAvailablePersonnel("PH", null, 88L)).thenReturn(List.of());

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
        task.setTestItem("PH");
        task.setTaskStatus(LimsTaskStatus.GENERATED);
        task.setStatus(LimsTaskStatus.GENERATED);
        task.setDurationMinutes(60L);
        return task;
    }

    private static LimsTestRequestDO request(String domainCode) {
        LimsTestRequestDO request = new LimsTestRequestDO();
        request.setId(1L);
        request.setDomainCode(domainCode);
        return request;
    }

    private static LimsTaskScheduleDO existing() {
        LimsTaskScheduleDO schedule = new LimsTaskScheduleDO();
        schedule.setTaskId(99L);
        schedule.setPlannedStartTime("2026-06-15 09:30:00");
        schedule.setPlannedEndTime("2026-06-15 10:30:00");
        schedule.setScheduleStatus(LimsTaskScheduleStatus.SCHEDULED);
        return schedule;
    }

    private static AvailableEquipment equipment() {
        return new AvailableEquipment(88L, "PH-METER-001", "酸度计", "instrument", "ENV",
                "PH", "2099-12-31", true, 100L, "iot-ph-001", "iot");
    }

    private static CalibrationEvidence evidence() {
        return new CalibrationEvidence(1000L, 88L, "calibration", "CERT-001", "计量院",
                "2026-01-01", "2099-12-31", "passed", "https://example.test/cert.pdf", true);
    }

    private static AvailablePersonnel personnel() {
        return new AvailablePersonnel(99L, "张三", "testing", "PH", 200L,
                "2099-12-31", "testing", "PH", "COMP-001", true);
    }

    private static PersonnelAuthorizationEvidence personnelEvidence() {
        return new PersonnelAuthorizationEvidence(200L, 99L, "张三", "testing", "PH",
                "2026-01-01", "2099-12-31", "active", "https://example.test/auth.pdf",
                300L, "testing", "PH", "AUTH-001",
                "https://example.test/competence.pdf", "合格", true);
    }

}
