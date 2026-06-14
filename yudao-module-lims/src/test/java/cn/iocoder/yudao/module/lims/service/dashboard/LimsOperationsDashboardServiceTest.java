package cn.iocoder.yudao.module.lims.service.dashboard;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.lab.dal.dataobject.domainpack.LabDomainPackDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.equipment.LabEquipmentAssetDO;
import cn.iocoder.yudao.module.lab.service.domainpack.LabDomainPackService;
import cn.iocoder.yudao.module.lab.service.equipment.LabEquipmentAssetService;
import cn.iocoder.yudao.module.lab.service.evidencelink.LabEvidenceLinkService;
import cn.iocoder.yudao.module.lab.service.evidenceobject.LabEvidenceObjectService;
import cn.iocoder.yudao.module.lab.service.quality.LabQualityRecordService;
import cn.iocoder.yudao.module.lab.service.standard.LabStandardService;
import cn.iocoder.yudao.module.lims.controller.admin.dashboard.vo.LimsOperationsDashboardRespVO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsReportDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestRequestDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestTaskDO;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsReportMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestRequestMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestTaskMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class LimsOperationsDashboardServiceTest extends BaseMockitoUnitTest {

    @InjectMocks
    private LimsOperationsDashboardService service;

    @Mock
    private LimsTestRequestMapper requestMapper;
    @Mock
    private LimsTestTaskMapper taskMapper;
    @Mock
    private LimsReportMapper reportMapper;
    @Mock
    private LabDomainPackService domainPackService;
    @Mock
    private LabEquipmentAssetService equipmentAssetService;
    @Mock
    private LabEvidenceObjectService evidenceObjectService;
    @Mock
    private LabEvidenceLinkService evidenceLinkService;
    @Mock
    private LabStandardService standardService;
    @Mock
    private LabQualityRecordService qualityRecordService;

    @Test
    void getOperationsDashboard_shouldAggregateReadinessRisksAndCoverage() {
        when(requestMapper.selectCount(any())).thenReturn(3L, 1L);
        when(taskMapper.selectCount(any())).thenReturn(12L, 8L, 1L, 7L);
        when(reportMapper.selectCount(any())).thenReturn(2L, 1L);
        when(domainPackService.getDomainPackPage(any())).thenReturn(
                new PageResult<>(List.of(domainPack("draft"), domainPack("published")), 2L),
                new PageResult<>(List.of(domainPack("published")), 1L));
        when(equipmentAssetService.getEquipmentAssetPage(any())).thenReturn(
                new PageResult<>(List.of(equipment("FOOD", "enabled", LocalDate.now().plusDays(10)),
                        equipment("FOOD", "expired", LocalDate.now().minusDays(1))), 2L),
                new PageResult<>(List.of(equipment("FOOD", "enabled", LocalDate.now().plusDays(10))), 1L),
                new PageResult<>(List.of(equipment("FOOD", "expired", LocalDate.now().minusDays(1))), 1L));
        when(evidenceObjectService.getEvidenceObjectPage(any())).thenReturn(new PageResult<>(List.of(), 3L));
        when(evidenceLinkService.getEvidenceLinkPage(any())).thenReturn(new PageResult<>(List.of(), 2L));
        when(standardService.getStandardPage(any())).thenReturn(new PageResult<>(List.of(), 2L));
        when(standardService.getStandardClausePage(any())).thenReturn(new PageResult<>(List.of(), 5L));
        when(qualityRecordService.getNonconformityPage(any())).thenReturn(
                new PageResult<>(List.of(), 2L),
                new PageResult<>(List.of(), 1L),
                PageResult.empty(),
                PageResult.empty(),
                PageResult.empty());
        when(qualityRecordService.getCorrectiveActionPage(any())).thenReturn(
                new PageResult<>(List.of(), 2L),
                new PageResult<>(List.of(), 1L),
                PageResult.empty(),
                PageResult.empty(),
                PageResult.empty());
        when(requestMapper.selectList(any())).thenReturn(List.of(request()));
        when(taskMapper.selectList(any())).thenReturn(List.of(task()));
        when(reportMapper.selectList(any())).thenReturn(List.of(report()), List.of(report()));

        LimsOperationsDashboardRespVO dashboard = service.getOperationsDashboard();

        assertEquals(62, dashboard.getApplicationReadinessScore());
        assertEquals(95, dashboard.getReassessmentRiskScore());
        assertEquals(100, dashboard.getCapabilityCoverageRate());
        assertEquals(50, dashboard.getCorrectionClosureRate());
        assertTrue(dashboard.getRisks().stream().anyMatch(risk -> "EXPIRED_EQUIPMENT".equals(risk.getCode())));
        assertTrue(dashboard.getRisks().stream().anyMatch(risk -> "EVIDENCE_GAP".equals(risk.getCode())));
        assertTrue(dashboard.getCapabilityCoverage().stream().anyMatch(item -> "FOOD".equals(item.getDomainCode())
                && Long.valueOf(1L).equals(item.getEnabledEquipmentCount())
                && Long.valueOf(1L).equals(item.getTaskCount())
                && Long.valueOf(1L).equals(item.getReportCount())));
        assertEquals("RPT-REQ-001", dashboard.getRecentReports().get(0).getReportNo());
    }

    private static LabDomainPackDO domainPack(String status) {
        LabDomainPackDO domainPack = new LabDomainPackDO();
        domainPack.setId("published".equals(status) ? 2L : 1L);
        domainPack.setPackCode("FOOD_ROUTINE");
        domainPack.setPackName("食品常规检测方向包");
        domainPack.setPackVersion("1.0");
        domainPack.setIndustry("食品");
        domainPack.setStatus(status);
        return domainPack;
    }

    private static LabEquipmentAssetDO equipment(String domainCode, String status, LocalDate calibrationValidUntil) {
        LabEquipmentAssetDO equipment = new LabEquipmentAssetDO();
        equipment.setId("enabled".equals(status) ? 10L : 11L);
        equipment.setEquipmentCode("FOOD-PH-001");
        equipment.setEquipmentName("食品 pH 计");
        equipment.setDomainCode(domainCode);
        equipment.setStatus(status);
        equipment.setCalibrationValidUntil(calibrationValidUntil);
        return equipment;
    }

    private static LimsTestRequestDO request() {
        LimsTestRequestDO request = new LimsTestRequestDO();
        request.setId(100L);
        request.setRequestNo("REQ-001");
        request.setDomainCode("FOOD");
        return request;
    }

    private static LimsTestTaskDO task() {
        LimsTestTaskDO task = new LimsTestTaskDO();
        task.setId(200L);
        task.setRequestId(100L);
        task.setTaskNo("REQ-001-T01");
        task.setTaskStatus("approved");
        task.setReportEligible(true);
        return task;
    }

    private static LimsReportDO report() {
        LimsReportDO report = new LimsReportDO();
        report.setId(300L);
        report.setRequestId(100L);
        report.setRequestNo("REQ-001");
        report.setReportNo("RPT-REQ-001");
        report.setReportName("食品常规检测报告");
        report.setStatus("issued");
        report.setTemplateVersion("1.0");
        report.setFileUrl("/lims/report-output/RPT-REQ-001/RPT-REQ-001.pdf");
        return report;
    }

}
