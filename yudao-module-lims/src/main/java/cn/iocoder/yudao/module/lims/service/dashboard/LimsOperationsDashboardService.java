package cn.iocoder.yudao.module.lims.service.dashboard;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lab.controller.admin.domainpack.vo.LabDomainPackPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.equipmentasset.vo.LabEquipmentAssetPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.evidencelink.vo.LabEvidenceLinkPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.evidenceobject.vo.LabEvidenceObjectPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.quality.vo.LabQualityRecordPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.standard.vo.LabStandardClausePageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.standard.vo.LabStandardPageReqVO;
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
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class LimsOperationsDashboardService {

    private static final int PAGE_SIZE_SUMMARY = 1;
    private static final int PAGE_SIZE_SAMPLE = 200;

    @Resource
    private LimsTestRequestMapper requestMapper;
    @Resource
    private LimsTestTaskMapper taskMapper;
    @Resource
    private LimsReportMapper reportMapper;
    @Resource
    private LabDomainPackService domainPackService;
    @Resource
    private LabEquipmentAssetService equipmentAssetService;
    @Resource
    private LabEvidenceObjectService evidenceObjectService;
    @Resource
    private LabEvidenceLinkService evidenceLinkService;
    @Resource
    private LabStandardService standardService;
    @Resource
    private LabQualityRecordService qualityRecordService;

    public LimsOperationsDashboardRespVO getOperationsDashboard() {
        DashboardNumbers numbers = loadNumbers();
        LimsOperationsDashboardRespVO respVO = new LimsOperationsDashboardRespVO();
        respVO.setApplicationReadinessScore(calculateReadinessScore(numbers));
        respVO.setReassessmentRiskScore(calculateRiskScore(numbers));
        respVO.setCapabilityCoverageRate(calculateCapabilityCoverageRate(numbers));
        respVO.setCorrectionClosureRate(percent(numbers.closedCorrectiveActionCount(), numbers.correctiveActionCount(), 100));
        respVO.setMetrics(createMetrics(numbers));
        respVO.setWorkflowStages(createWorkflowStages(numbers));
        respVO.setCapabilityCoverage(createCapabilityCoverage(numbers));
        respVO.setRisks(createRisks(numbers));
        respVO.setRecentReports(createRecentReports());
        return respVO;
    }

    private DashboardNumbers loadNumbers() {
        long requestTotal = countRequests();
        long completedRequestCount = countRequestsByStatus("completed");
        long taskTotal = countTasks();
        long reportEligibleTaskCount = countReportEligibleTasks();
        long blockedTaskCount = countBlockedTasks();
        long approvedTaskCount = countTasksByStatus("approved");
        long reportTotal = countReports();
        long issuedReportCount = countReportsByStatus("issued");

        PageResult<LabDomainPackDO> allPacks = domainPackPage(null, PAGE_SIZE_SAMPLE);
        PageResult<LabDomainPackDO> publishedPacks = domainPackPage("published", PAGE_SIZE_SAMPLE);
        PageResult<LabEquipmentAssetDO> allEquipment = equipmentPage(null, PAGE_SIZE_SAMPLE);
        PageResult<LabEquipmentAssetDO> enabledEquipment = equipmentPage("enabled", PAGE_SIZE_SAMPLE);
        long expiringEquipmentCount = countExpiringEquipment(safeList(allEquipment.getList()));
        long expiredEquipmentCount = Math.max(total(equipmentPage("expired", PAGE_SIZE_SUMMARY)),
                countExpiredEquipment(safeList(allEquipment.getList())));

        long evidenceObjectCount = total(evidenceObjectPage(null, PAGE_SIZE_SUMMARY));
        long evidenceLinkCount = total(evidenceLinkPage(null, PAGE_SIZE_SUMMARY));
        long standardCount = total(standardPage(PAGE_SIZE_SUMMARY));
        long standardClauseCount = total(standardClausePage(PAGE_SIZE_SUMMARY));
        long nonconformityCount = total(qualityPage("nonconformity", null, PAGE_SIZE_SUMMARY));
        long closedNonconformityCount = countQualityClosed("nonconformity");
        long correctiveActionCount = total(qualityPage("correctiveAction", null, PAGE_SIZE_SUMMARY));
        long closedCorrectiveActionCount = countQualityClosed("correctiveAction");

        return new DashboardNumbers(
                requestTotal,
                completedRequestCount,
                taskTotal,
                reportEligibleTaskCount,
                blockedTaskCount,
                approvedTaskCount,
                reportTotal,
                issuedReportCount,
                total(allPacks),
                total(publishedPacks),
                total(allEquipment),
                total(enabledEquipment),
                expiredEquipmentCount,
                expiringEquipmentCount,
                safeList(allEquipment.getList()),
                evidenceObjectCount,
                evidenceLinkCount,
                standardCount,
                standardClauseCount,
                nonconformityCount,
                closedNonconformityCount,
                correctiveActionCount,
                closedCorrectiveActionCount,
                sampleRequests(),
                sampleTasks(),
                sampleReports());
    }

    private List<LimsOperationsDashboardRespVO.MetricCard> createMetrics(DashboardNumbers numbers) {
        List<LimsOperationsDashboardRespVO.MetricCard> metrics = new ArrayList<>();
        metrics.add(metric("applicationReadiness", "申请准备度", (long) calculateReadinessScore(numbers), "%",
                readinessLevel(calculateReadinessScore(numbers)), "方向包、设备、证据、报告和整改闭环综合分"));
        metrics.add(metric("reassessmentRisk", "复评审风险", (long) calculateRiskScore(numbers), "%",
                riskLevel(calculateRiskScore(numbers)), "过期、阻塞、证据缺口和未签发报告风险"));
        metrics.add(metric("publishedPacks", "已发布方向包", numbers.publishedPackCount(), "个",
                numbers.publishedPackCount() > 0 ? "success" : "danger", "可被检测需求冻结使用的方向包版本"));
        metrics.add(metric("standardClauses", "标准条款", numbers.standardClauseCount(), "条",
                numbers.standardClauseCount() > 0 ? "success" : "danger", "国家/行业/国际标准条款沉淀"));
        metrics.add(metric("issuedReports", "已签发报告", numbers.issuedReportCount(), "份",
                numbers.issuedReportCount() == numbers.reportCount() ? "success" : "warning", "完成证据链登记的报告"));
        metrics.add(metric("equipmentAlerts", "设备预警", numbers.expiredEquipmentCount() + numbers.expiringEquipmentCount(), "台",
                numbers.expiredEquipmentCount() > 0 ? "danger" : numbers.expiringEquipmentCount() > 0 ? "warning" : "success",
                "已过期或 30 天内到期的设备"));
        metrics.add(metric("evidenceLinks", "证据关联", numbers.evidenceLinkCount(), "条",
                numbers.evidenceLinkCount() >= numbers.evidenceObjectCount() ? "success" : "warning",
                "证据对象到条款/业务对象的映射"));
        metrics.add(metric("blockedTasks", "阻塞任务", numbers.blockedTaskCount(), "项",
                numbers.blockedTaskCount() > 0 ? "danger" : "success", "挂起、返工、取消或存在阻塞原因的任务"));
        metrics.add(metric("openCapa", "未闭环整改", Math.max(numbers.correctiveActionCount() - numbers.closedCorrectiveActionCount(), 0L), "项",
                numbers.correctiveActionCount() > numbers.closedCorrectiveActionCount() ? "warning" : "success",
                "CAPA 未完成或未验证的记录"));
        return metrics;
    }

    private List<LimsOperationsDashboardRespVO.WorkflowStage> createWorkflowStages(DashboardNumbers numbers) {
        return List.of(
                stage("request", "检测需求", numbers.requestCount(), numbers.completedRequestCount(),
                        Math.max(numbers.requestCount() - numbers.completedRequestCount(), 0L)),
                stage("task", "检测任务", numbers.taskCount(), numbers.approvedTaskCount(), numbers.blockedTaskCount()),
                stage("report", "检测报告", numbers.reportCount(), numbers.issuedReportCount(),
                        Math.max(numbers.reportCount() - numbers.issuedReportCount(), 0L)),
                stage("evidence", "证据映射", numbers.evidenceObjectCount(), Math.min(numbers.evidenceLinkCount(), numbers.evidenceObjectCount()),
                        Math.max(numbers.evidenceObjectCount() - numbers.evidenceLinkCount(), 0L)),
                stage("capa", "整改闭环", numbers.correctiveActionCount(), numbers.closedCorrectiveActionCount(),
                        Math.max(numbers.correctiveActionCount() - numbers.closedCorrectiveActionCount(), 0L)));
    }

    private List<LimsOperationsDashboardRespVO.CoverageItem> createCapabilityCoverage(DashboardNumbers numbers) {
        Map<String, LimsOperationsDashboardRespVO.CoverageItem> coverage = new LinkedHashMap<>();
        for (LabEquipmentAssetDO equipment : numbers.equipment()) {
            String domainCode = normalizeDomain(equipment.getDomainCode(), "UNSPECIFIED");
            LimsOperationsDashboardRespVO.CoverageItem item = coverage.computeIfAbsent(domainCode, this::coverageItem);
            item.setEnabledEquipmentCount(item.getEnabledEquipmentCount() + ("enabled".equalsIgnoreCase(equipment.getStatus()) ? 1L : 0L));
        }
        for (LimsTestRequestDO request : numbers.requests()) {
            coverage.computeIfAbsent(normalizeDomain(request.getDomainCode(), "UNSPECIFIED"), this::coverageItem);
        }
        for (LimsTestTaskDO task : numbers.tasks()) {
            String domainCode = resolveTaskDomain(numbers.requests(), task);
            LimsOperationsDashboardRespVO.CoverageItem item = coverage.computeIfAbsent(domainCode, this::coverageItem);
            item.setTaskCount(item.getTaskCount() + 1);
        }
        for (LimsReportDO report : numbers.reports()) {
            String domainCode = resolveReportDomain(numbers.requests(), report);
            LimsOperationsDashboardRespVO.CoverageItem item = coverage.computeIfAbsent(domainCode, this::coverageItem);
            item.setReportCount(item.getReportCount() + 1);
        }
        if (coverage.isEmpty()) {
            coverage.put("ALL", coverageItem("ALL"));
        }
        long publishedPackCount = numbers.publishedPackCount();
        coverage.values().forEach(item -> {
            item.setPublishedPackCount(publishedPackCount);
            item.setCoverageRate(average(
                    publishedPackCount > 0 ? 100 : 0,
                    item.getEnabledEquipmentCount() > 0 ? 100 : 0,
                    item.getTaskCount() > 0 ? 100 : 0,
                    item.getReportCount() > 0 ? 100 : 0));
        });
        return new ArrayList<>(coverage.values());
    }

    private List<LimsOperationsDashboardRespVO.RiskItem> createRisks(DashboardNumbers numbers) {
        List<LimsOperationsDashboardRespVO.RiskItem> risks = new ArrayList<>();
        addRiskIf(risks, numbers.publishedPackCount() == 0, "NO_PUBLISHED_PACK", "没有已发布方向包",
                "新的检测需求无法冻结方向包版本，申请准备度不可验证。", "danger", "Domain Pack",
                numbers.domainPackCount(), "发布至少一个检测方向包版本");
        addRiskIf(risks, numbers.standardClauseCount() == 0, "NO_STANDARD_CLAUSE", "没有可用标准条款",
                "检测方法、报告解读和证据映射缺少标准依据。", "danger", "CNAS/CMA",
                numbers.standardCount(), "维护标准库并沉淀条款版本");
        addRiskIf(risks, numbers.enabledEquipmentCount() == 0, "NO_ENABLED_EQUIPMENT", "没有可用设备",
                "任务排程无法形成设备证据链。", "danger", "Equipment",
                numbers.equipmentCount(), "维护设备主档并确认校准有效期");
        addRiskIf(risks, numbers.expiredEquipmentCount() > 0, "EXPIRED_EQUIPMENT", "存在校准过期设备",
                "过期设备会影响 CNAS/CMA 设备溯源证据。", "danger", "Equipment",
                numbers.expiredEquipmentCount(), "更新校准记录或停用设备");
        addRiskIf(risks, numbers.expiringEquipmentCount() > 0, "EXPIRING_EQUIPMENT", "设备即将到期",
                "30 天内到期设备需要提前安排校准或期间核查。", "warning", "Equipment",
                numbers.expiringEquipmentCount(), "安排校准计划并上传证书");
        addRiskIf(risks, numbers.evidenceObjectCount() > numbers.evidenceLinkCount(), "EVIDENCE_GAP", "存在证据未映射条款",
                "证据对象未全部关联到条款或业务对象，评审时难以追溯。", "warning", "Evidence Chain",
                numbers.evidenceObjectCount() - numbers.evidenceLinkCount(), "补齐证据关联和条款映射");
        addRiskIf(risks, numbers.blockedTaskCount() > 0, "BLOCKED_TASK", "存在阻塞检测任务",
                "任务挂起、返工或取消会影响报告准出。", "danger", "LIMS Execution",
                numbers.blockedTaskCount(), "处理阻塞原因并重新进入检测流程");
        addRiskIf(risks, numbers.reportCount() > numbers.issuedReportCount(), "UNISSUED_REPORT", "存在未签发报告",
                "报告未签发时不会自动形成报告证据对象。", "warning", "Report Center",
                numbers.reportCount() - numbers.issuedReportCount(), "完成报告审核签发");
        addRiskIf(risks, numbers.nonconformityCount() > numbers.closedNonconformityCount(), "OPEN_NC", "不符合项未关闭",
                "未关闭 NC 会拉低复评审准备度。", "warning", "CNAS/CMA",
                numbers.nonconformityCount() - numbers.closedNonconformityCount(), "闭环不符合项并关联 CAPA");
        addRiskIf(risks, numbers.correctiveActionCount() > numbers.closedCorrectiveActionCount(), "OPEN_CAPA", "整改措施未验证",
                "CAPA 未验证会影响管理体系持续有效性。", "warning", "CNAS/CMA",
                numbers.correctiveActionCount() - numbers.closedCorrectiveActionCount(), "完成验证并归档证据");
        if (risks.isEmpty()) {
            risks.add(risk("NO_MAJOR_RISK", "暂无主要风险", "当前聚合指标未发现阻断申请或复评审的主要问题。",
                    "success", "Review Operations", 0L, "持续维护证据链和到期预警"));
        }
        return risks;
    }

    private List<LimsOperationsDashboardRespVO.RecentReport> createRecentReports() {
        return reportMapper.selectList(new LambdaQueryWrapperX<LimsReportDO>()
                        .orderByDesc(LimsReportDO::getId)
                        .last("LIMIT 5"))
                .stream()
                .map(this::recentReport)
                .toList();
    }

    private LimsOperationsDashboardRespVO.RecentReport recentReport(LimsReportDO report) {
        LimsOperationsDashboardRespVO.RecentReport item = new LimsOperationsDashboardRespVO.RecentReport();
        item.setReportNo(report.getReportNo());
        item.setRequestNo(report.getRequestNo());
        item.setReportName(report.getReportName());
        item.setStatus(report.getStatus());
        item.setTemplateVersion(report.getTemplateVersion());
        item.setFileUrl(report.getFileUrl());
        item.setIssuedTime(report.getIssuedTime());
        return item;
    }

    private int calculateReadinessScore(DashboardNumbers numbers) {
        return average(
                percent(numbers.publishedPackCount(), numbers.domainPackCount(), 0),
                percent(numbers.enabledEquipmentCount(), numbers.equipmentCount(), 0),
                percent(Math.min(numbers.evidenceLinkCount(), numbers.evidenceObjectCount()), numbers.evidenceObjectCount(), 0),
                percent(numbers.reportEligibleTaskCount(), numbers.taskCount(), 0),
                percent(numbers.issuedReportCount(), numbers.reportCount(), 0),
                numbers.standardClauseCount() > 0 ? 100 : 0,
                percent(numbers.closedCorrectiveActionCount(), numbers.correctiveActionCount(), 100));
    }

    private int calculateRiskScore(DashboardNumbers numbers) {
        int score = 0;
        score += numbers.publishedPackCount() == 0 ? 25 : 0;
        score += numbers.enabledEquipmentCount() == 0 ? 25 : 0;
        score += numbers.expiredEquipmentCount() > 0 ? 20 : 0;
        score += numbers.expiringEquipmentCount() > 0 ? 10 : 0;
        score += numbers.evidenceObjectCount() > numbers.evidenceLinkCount() ? 15 : 0;
        score += numbers.blockedTaskCount() > 0 ? 20 : 0;
        score += numbers.reportCount() > numbers.issuedReportCount() ? 10 : 0;
        score += numbers.nonconformityCount() > numbers.closedNonconformityCount() ? 10 : 0;
        score += numbers.correctiveActionCount() > numbers.closedCorrectiveActionCount() ? 10 : 0;
        return clamp(score);
    }

    private int calculateCapabilityCoverageRate(DashboardNumbers numbers) {
        return average(
                numbers.publishedPackCount() > 0 ? 100 : 0,
                numbers.enabledEquipmentCount() > 0 ? 100 : 0,
                numbers.taskCount() > 0 ? 100 : 0,
                numbers.issuedReportCount() > 0 ? 100 : 0);
    }

    private long countRequests() {
        return count(requestMapper.selectCount(new LambdaQueryWrapperX<>()));
    }

    private long countRequestsByStatus(String status) {
        return count(requestMapper.selectCount(new LambdaQueryWrapperX<LimsTestRequestDO>()
                .eq(LimsTestRequestDO::getStatus, status)));
    }

    private long countTasks() {
        return count(taskMapper.selectCount(new LambdaQueryWrapperX<>()));
    }

    private long countTasksByStatus(String status) {
        return count(taskMapper.selectCount(new LambdaQueryWrapperX<LimsTestTaskDO>()
                .eq(LimsTestTaskDO::getTaskStatus, status)));
    }

    private long countReportEligibleTasks() {
        return count(taskMapper.selectCount(new LambdaQueryWrapperX<LimsTestTaskDO>()
                .eq(LimsTestTaskDO::getReportEligible, true)));
    }

    private long countBlockedTasks() {
        return count(taskMapper.selectCount(new LambdaQueryWrapperX<LimsTestTaskDO>()
                .and(wrapper -> wrapper.in(LimsTestTaskDO::getTaskStatus, List.of("hold", "rework", "cancelled"))
                        .or().isNotNull(LimsTestTaskDO::getBlockReason))));
    }

    private long countReports() {
        return count(reportMapper.selectCount(new LambdaQueryWrapperX<>()));
    }

    private long countReportsByStatus(String status) {
        return count(reportMapper.selectCount(new LambdaQueryWrapperX<LimsReportDO>()
                .eq(LimsReportDO::getStatus, status)));
    }

    private List<LimsTestRequestDO> sampleRequests() {
        return requestMapper.selectList(new LambdaQueryWrapperX<LimsTestRequestDO>()
                .orderByDesc(LimsTestRequestDO::getId)
                .last("LIMIT " + PAGE_SIZE_SAMPLE));
    }

    private List<LimsTestTaskDO> sampleTasks() {
        return taskMapper.selectList(new LambdaQueryWrapperX<LimsTestTaskDO>()
                .orderByDesc(LimsTestTaskDO::getId)
                .last("LIMIT " + PAGE_SIZE_SAMPLE));
    }

    private List<LimsReportDO> sampleReports() {
        return reportMapper.selectList(new LambdaQueryWrapperX<LimsReportDO>()
                .orderByDesc(LimsReportDO::getId)
                .last("LIMIT " + PAGE_SIZE_SAMPLE));
    }

    private PageResult<LabDomainPackDO> domainPackPage(String status, int pageSize) {
        LabDomainPackPageReqVO reqVO = pageReq(new LabDomainPackPageReqVO(), pageSize);
        reqVO.setStatus(status);
        return domainPackService.getDomainPackPage(reqVO);
    }

    private PageResult<LabEquipmentAssetDO> equipmentPage(String status, int pageSize) {
        LabEquipmentAssetPageReqVO reqVO = pageReq(new LabEquipmentAssetPageReqVO(), pageSize);
        reqVO.setStatus(status);
        return equipmentAssetService.getEquipmentAssetPage(reqVO);
    }

    private PageResult<?> evidenceObjectPage(String status, int pageSize) {
        LabEvidenceObjectPageReqVO reqVO = pageReq(new LabEvidenceObjectPageReqVO(), pageSize);
        reqVO.setStatus(status);
        return evidenceObjectService.getEvidenceObjectPage(reqVO);
    }

    private PageResult<?> evidenceLinkPage(String clauseCategory, int pageSize) {
        LabEvidenceLinkPageReqVO reqVO = pageReq(new LabEvidenceLinkPageReqVO(), pageSize);
        reqVO.setClauseCategory(clauseCategory);
        return evidenceLinkService.getEvidenceLinkPage(reqVO);
    }

    private PageResult<?> standardPage(int pageSize) {
        return standardService.getStandardPage(pageReq(new LabStandardPageReqVO(), pageSize));
    }

    private PageResult<?> standardClausePage(int pageSize) {
        return standardService.getStandardClausePage(pageReq(new LabStandardClausePageReqVO(), pageSize));
    }

    private PageResult<?> qualityPage(String type, String status, int pageSize) {
        LabQualityRecordPageReqVO reqVO = pageReq(new LabQualityRecordPageReqVO(), pageSize);
        reqVO.setStatus(status);
        if ("nonconformity".equals(type)) {
            return qualityRecordService.getNonconformityPage(reqVO);
        }
        if ("correctiveAction".equals(type)) {
            return qualityRecordService.getCorrectiveActionPage(reqVO);
        }
        return PageResult.empty();
    }

    private long countQualityClosed(String type) {
        return List.of("closed", "completed", "verified", "done").stream()
                .mapToLong(status -> total(qualityPage(type, status, PAGE_SIZE_SUMMARY)))
                .max()
                .orElse(0L);
    }

    private <T extends cn.iocoder.yudao.framework.common.pojo.PageParam> T pageReq(T reqVO, int pageSize) {
        reqVO.setPageNo(1);
        reqVO.setPageSize(pageSize);
        return reqVO;
    }

    private long countExpiringEquipment(List<LabEquipmentAssetDO> equipment) {
        LocalDate today = LocalDate.now();
        LocalDate warningDate = today.plusDays(30);
        return equipment.stream()
                .filter(item -> item.getCalibrationValidUntil() != null)
                .filter(item -> !item.getCalibrationValidUntil().isBefore(today))
                .filter(item -> !item.getCalibrationValidUntil().isAfter(warningDate))
                .count();
    }

    private long countExpiredEquipment(List<LabEquipmentAssetDO> equipment) {
        LocalDate today = LocalDate.now();
        return equipment.stream()
                .filter(item -> item.getCalibrationValidUntil() != null)
                .filter(item -> item.getCalibrationValidUntil().isBefore(today))
                .count();
    }

    private LimsOperationsDashboardRespVO.MetricCard metric(String code, String label, Long value, String unit,
                                                           String level, String hint) {
        LimsOperationsDashboardRespVO.MetricCard item = new LimsOperationsDashboardRespVO.MetricCard();
        item.setCode(code);
        item.setLabel(label);
        item.setValue(value);
        item.setUnit(unit);
        item.setLevel(level);
        item.setHint(hint);
        return item;
    }

    private LimsOperationsDashboardRespVO.WorkflowStage stage(String code, String label, Long total, Long done, Long blocked) {
        LimsOperationsDashboardRespVO.WorkflowStage item = new LimsOperationsDashboardRespVO.WorkflowStage();
        item.setCode(code);
        item.setLabel(label);
        item.setTotal(total);
        item.setDone(done);
        item.setBlocked(blocked);
        item.setCompletionRate(percent(done, total, total == 0 ? 100 : 0));
        return item;
    }

    private LimsOperationsDashboardRespVO.CoverageItem coverageItem(String domainCode) {
        LimsOperationsDashboardRespVO.CoverageItem item = new LimsOperationsDashboardRespVO.CoverageItem();
        item.setDomainCode(domainCode);
        item.setDomainName(domainName(domainCode));
        item.setPublishedPackCount(0L);
        item.setEnabledEquipmentCount(0L);
        item.setTaskCount(0L);
        item.setReportCount(0L);
        item.setCoverageRate(0);
        return item;
    }

    private void addRiskIf(List<LimsOperationsDashboardRespVO.RiskItem> risks, boolean condition,
                           String code, String title, String description, String level,
                           String ownerContext, Long relatedCount, String actionText) {
        if (condition) {
            risks.add(risk(code, title, description, level, ownerContext, relatedCount, actionText));
        }
    }

    private LimsOperationsDashboardRespVO.RiskItem risk(String code, String title, String description, String level,
                                                       String ownerContext, Long relatedCount, String actionText) {
        LimsOperationsDashboardRespVO.RiskItem item = new LimsOperationsDashboardRespVO.RiskItem();
        item.setCode(code);
        item.setTitle(title);
        item.setDescription(description);
        item.setLevel(level);
        item.setOwnerContext(ownerContext);
        item.setRelatedCount(relatedCount);
        item.setActionText(actionText);
        return item;
    }

    private String resolveTaskDomain(List<LimsTestRequestDO> requests, LimsTestTaskDO task) {
        return requests.stream()
                .filter(request -> request.getId() != null && request.getId().equals(task.getRequestId()))
                .map(LimsTestRequestDO::getDomainCode)
                .filter(StringUtils::hasText)
                .findFirst()
                .map(domain -> normalizeDomain(domain, "UNSPECIFIED"))
                .orElse("UNSPECIFIED");
    }

    private String resolveReportDomain(List<LimsTestRequestDO> requests, LimsReportDO report) {
        return requests.stream()
                .filter(request -> request.getId() != null && request.getId().equals(report.getRequestId()))
                .map(LimsTestRequestDO::getDomainCode)
                .filter(StringUtils::hasText)
                .findFirst()
                .map(domain -> normalizeDomain(domain, "UNSPECIFIED"))
                .orElse("UNSPECIFIED");
    }

    private String normalizeDomain(String domainCode, String defaultValue) {
        return StringUtils.hasText(domainCode) ? domainCode.trim().toUpperCase(Locale.ROOT) : defaultValue;
    }

    private String domainName(String domainCode) {
        return switch (domainCode) {
            case "FOOD" -> "食品";
            case "ENVIRONMENT" -> "环境";
            case "INDUSTRIAL" -> "工业品";
            case "MEDICAL" -> "医疗";
            case "DRUG" -> "药品";
            case "CCC" -> "3C";
            case "ENERGY" -> "新能源";
            case "ALL" -> "全部方向";
            default -> domainCode;
        };
    }

    private String readinessLevel(int score) {
        if (score >= 85) {
            return "success";
        }
        if (score >= 60) {
            return "warning";
        }
        return "danger";
    }

    private String riskLevel(int score) {
        if (score >= 60) {
            return "danger";
        }
        if (score >= 25) {
            return "warning";
        }
        return "success";
    }

    private int percent(long numerator, long denominator, int defaultValue) {
        if (denominator <= 0) {
            return defaultValue;
        }
        return clamp(Math.round(numerator * 100.0f / denominator));
    }

    private int average(int... values) {
        if (values.length == 0) {
            return 0;
        }
        int total = 0;
        for (int value : values) {
            total += value;
        }
        return clamp(Math.round(total * 1.0f / values.length));
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }

    private long total(PageResult<?> page) {
        return page == null || page.getTotal() == null ? 0L : page.getTotal();
    }

    private long count(Long value) {
        return value == null ? 0L : value;
    }

    private <T> List<T> safeList(List<T> list) {
        return list == null ? List.of() : list;
    }

    private record DashboardNumbers(
            long requestCount,
            long completedRequestCount,
            long taskCount,
            long reportEligibleTaskCount,
            long blockedTaskCount,
            long approvedTaskCount,
            long reportCount,
            long issuedReportCount,
            long domainPackCount,
            long publishedPackCount,
            long equipmentCount,
            long enabledEquipmentCount,
            long expiredEquipmentCount,
            long expiringEquipmentCount,
            List<LabEquipmentAssetDO> equipment,
            long evidenceObjectCount,
            long evidenceLinkCount,
            long standardCount,
            long standardClauseCount,
            long nonconformityCount,
            long closedNonconformityCount,
            long correctiveActionCount,
            long closedCorrectiveActionCount,
            List<LimsTestRequestDO> requests,
            List<LimsTestTaskDO> tasks,
            List<LimsReportDO> reports) {
    }

}
