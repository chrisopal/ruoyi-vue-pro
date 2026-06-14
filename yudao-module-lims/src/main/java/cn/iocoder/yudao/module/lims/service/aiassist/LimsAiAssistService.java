package cn.iocoder.yudao.module.lims.service.aiassist;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lab.controller.admin.evidencelink.vo.LabEvidenceLinkPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.evidenceobject.vo.LabEvidenceObjectPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.standard.vo.LabStandardClausePageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.standard.vo.LabStandardPageReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.standard.LabStandardClauseDO;
import cn.iocoder.yudao.module.lab.service.evidencelink.LabEvidenceLinkService;
import cn.iocoder.yudao.module.lab.service.evidenceobject.LabEvidenceObjectService;
import cn.iocoder.yudao.module.lab.service.standard.LabStandardService;
import cn.iocoder.yudao.module.lims.controller.admin.aiassist.vo.LimsAiAssistCenterRespVO;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsTaskQualityGateRespVO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsReportDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestRequestDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestTaskDO;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsReportMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestRequestMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestTaskMapper;
import cn.iocoder.yudao.module.lims.service.workflow.LimsWorkflowService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class LimsAiAssistService {

    private static final int PAGE_SIZE_SAMPLE = 200;
    private static final int PAGE_SIZE_METRIC = 1;
    private static final int MAX_CLAUSE_HITS = 5;

    @Resource
    private LimsTestRequestMapper requestMapper;
    @Resource
    private LimsTestTaskMapper taskMapper;
    @Resource
    private LimsReportMapper reportMapper;
    @Resource
    private LabStandardService standardService;
    @Resource
    private LabEvidenceObjectService evidenceObjectService;
    @Resource
    private LabEvidenceLinkService evidenceLinkService;
    @Resource
    private LimsWorkflowService workflowService;
    @Resource
    private ObjectMapper objectMapper;

    public LimsAiAssistCenterRespVO getCenter(Long requestId, String question) {
        LimsTestRequestDO request = resolveRequest(requestId);
        List<LimsTestTaskDO> tasks = request == null ? List.of() : taskMapper.selectListByRequestId(request.getId());
        LimsReportDO report = request == null ? null : reportMapper.selectByRequestId(request.getId());
        List<LabStandardClauseDO> clauses = loadStandardClauses();

        LimsAiAssistCenterRespVO respVO = new LimsAiAssistCenterRespVO();
        fillRequest(respVO, request);
        respVO.setMetrics(createMetrics(clauses));
        respVO.setStandardAnswer(createStandardAnswer(question, clauses));
        respVO.setEvidenceGaps(createEvidenceGaps(request, tasks, report));
        respVO.setReportInterpretation(createReportInterpretation(report));
        respVO.setDataInterpretations(createDataInterpretations(tasks));
        respVO.setRecommendations(createRecommendations(respVO));
        return respVO;
    }

    private void fillRequest(LimsAiAssistCenterRespVO respVO, LimsTestRequestDO request) {
        if (request == null) {
            return;
        }
        respVO.setRequestId(request.getId());
        respVO.setRequestNo(request.getRequestNo());
        respVO.setRequestName(request.getRequestName());
        respVO.setDomainCode(request.getDomainCode());
        respVO.setDomainPackCode(request.getDomainPackCode());
        respVO.setDomainPackVersion(request.getDomainPackVersion());
    }

    private List<LimsAiAssistCenterRespVO.KnowledgeMetric> createMetrics(List<LabStandardClauseDO> clauses) {
        List<LimsAiAssistCenterRespVO.KnowledgeMetric> metrics = new ArrayList<>();
        metrics.add(metric("standardCount", "标准库", total(standardService.getStandardPage(pageReq(new LabStandardPageReqVO(), PAGE_SIZE_METRIC))),
                "项", "已沉淀的国家/行业/国际标准"));
        metrics.add(metric("standardClauseCount", "标准条款", (long) clauses.size(), "条", "可用于问答、解读和证据映射的条款"));
        metrics.add(metric("evidenceObjectCount", "证据对象", total(evidenceObjectService.getEvidenceObjectPage(pageReq(new LabEvidenceObjectPageReqVO(), PAGE_SIZE_METRIC))),
                "份", "报告、原始记录、设备证书等证据对象"));
        metrics.add(metric("evidenceLinkCount", "证据映射", total(evidenceLinkService.getEvidenceLinkPage(pageReq(new LabEvidenceLinkPageReqVO(), PAGE_SIZE_METRIC))),
                "条", "证据对象与条款/业务对象的映射"));
        return metrics;
    }

    private LimsAiAssistCenterRespVO.StandardAnswer createStandardAnswer(String question, List<LabStandardClauseDO> clauses) {
        String normalizedQuestion = StringUtils.hasText(question) ? question.trim() : "证据链和报告签发需要关注哪些条款？";
        List<LabStandardClauseDO> hits = clauses.stream()
                .filter(clause -> matchesQuestion(clause, normalizedQuestion))
                .limit(MAX_CLAUSE_HITS)
                .toList();
        if (hits.isEmpty()) {
            hits = clauses.stream().limit(MAX_CLAUSE_HITS).toList();
        }

        LimsAiAssistCenterRespVO.StandardAnswer answer = new LimsAiAssistCenterRespVO.StandardAnswer();
        answer.setQuestion(normalizedQuestion);
        answer.setMatchedClauses(hits.stream().map(this::clauseHit).toList());
        answer.setConfidence(hits.isEmpty() ? 0 : Math.min(95, 50 + hits.size() * 9));
        answer.setAnswer(buildAnswer(normalizedQuestion, hits));
        return answer;
    }

    private List<LimsAiAssistCenterRespVO.EvidenceGap> createEvidenceGaps(LimsTestRequestDO request,
                                                                           List<LimsTestTaskDO> tasks,
                                                                           LimsReportDO report) {
        List<LimsAiAssistCenterRespVO.EvidenceGap> gaps = new ArrayList<>();
        if (request == null) {
            gaps.add(gap("NO_REQUEST", "没有可分析的检测需求", "warning",
                    "当前系统还没有检测需求，AI 无法形成需求级证据缺口诊断。",
                    "先创建检测需求并冻结方向包版本。", "request", null, "workflow"));
            return gaps;
        }

        if (tasks.isEmpty()) {
            gaps.add(gap("NO_TASK", "检测需求未生成任务", "danger",
                    "检测需求尚未进入样品和任务执行闭环，无法沉淀原始记录、设备和人员证据。",
                    "按冻结方向包生成执行计划和检测任务。", "request", request.getRequestNo(), "workflow"));
        }

        for (LimsTestTaskDO task : tasks) {
            LimsTaskQualityGateRespVO gate = workflowService.getTaskQualityGate(task.getId());
            if (Boolean.TRUE.equals(gate.getQualityGateSatisfied())) {
                continue;
            }
            JsonNode missing = gate.getMissingRequirements();
            if (missing != null && missing.isArray() && missing.size() > 0) {
                for (JsonNode item : missing) {
                    gaps.add(gap(item.path("code").asText(item.path("type").asText("TASK_GATE")),
                            task.getTaskNo() + " " + item.path("name").asText("门禁缺口"),
                            severityForMissing(item.path("type").asText("")),
                            item.path("message").asText("检测任务存在未满足的质量门禁。"),
                            actionForMissing(item.path("type").asText("")),
                            "task", task.getTaskNo(), clauseCategoryForMissing(item.path("type").asText(""))));
                }
            } else {
                gaps.add(gap("TASK_GATE", task.getTaskNo() + " 质量门禁未通过", "warning",
                        "检测任务质量门禁未满足，但未返回明确缺口明细。",
                        "打开任务门禁抽屉查看原始记录、QC、证据和复核状态。", "task", task.getTaskNo(), "quality"));
            }
        }

        if (report == null) {
            gaps.add(gap("NO_REPORT", "检测报告尚未生成", "warning",
                    "报告是第三方检测交付和 CNAS/CMA 证据链的重要输出，当前需求还没有报告记录。",
                    "质量门禁完成后生成并签发报告。", "report", request.getRequestNo(), "report"));
        } else if (!"issued".equalsIgnoreCase(report.getStatus())) {
            gaps.add(gap("REPORT_NOT_ISSUED", "检测报告未签发", "warning",
                    "报告未签发时，报告证据对象和条款映射可能尚未形成。",
                    "完成报告审核签发并登记证据对象。", "report", report.getReportNo(), "report"));
        } else if (countReportEvidenceLinks(report) == 0) {
            gaps.add(gap("REPORT_EVIDENCE_UNLINKED", "报告证据未映射条款", "warning",
                    "报告已经签发，但未查询到对应的报告证据条款映射。",
                    "补齐报告证据对象与报告条款的映射。", "report", report.getReportNo(), "report"));
        }

        if (gaps.isEmpty()) {
            gaps.add(gap("NO_MAJOR_GAP", "暂无主要证据缺口", "success",
                    "当前检测需求的任务门禁、报告状态和报告证据映射未发现主要阻断项。",
                    "持续维护设备、人员、原始记录、QC 和报告证据。", "request", request.getRequestNo(), "review"));
        }
        return gaps;
    }

    private LimsAiAssistCenterRespVO.ReportInterpretation createReportInterpretation(LimsReportDO report) {
        LimsAiAssistCenterRespVO.ReportInterpretation interpretation = new LimsAiAssistCenterRespVO.ReportInterpretation();
        if (report == null) {
            interpretation.setStatus("missing");
            interpretation.setRiskLevel("warning");
            interpretation.setInterpretation("当前检测需求还没有报告，无法进行报告解读。");
            return interpretation;
        }
        interpretation.setReportNo(report.getReportNo());
        interpretation.setStatus(report.getStatus());
        interpretation.setConclusion(report.getConclusion());
        interpretation.setOutputFormats(parseOutputFormats(report.getReportOutput()));
        interpretation.setRiskLevel("issued".equalsIgnoreCase(report.getStatus()) ? "success" : "warning");
        interpretation.setInterpretation(buildReportInterpretation(report, interpretation.getOutputFormats()));
        return interpretation;
    }

    private List<LimsAiAssistCenterRespVO.DataInterpretation> createDataInterpretations(List<LimsTestTaskDO> tasks) {
        return tasks.stream().map(task -> {
            LimsTaskQualityGateRespVO gate = workflowService.getTaskQualityGate(task.getId());
            LimsAiAssistCenterRespVO.DataInterpretation item = new LimsAiAssistCenterRespVO.DataInterpretation();
            item.setTaskNo(task.getTaskNo());
            item.setTestItem(task.getTestItem());
            item.setMethodName(task.getMethodName());
            item.setTaskStatus(task.getTaskStatus());
            item.setMissingRequirementCount(gate.getMissingRequirementCount());
            item.setQualityGateSatisfied(gate.getQualityGateSatisfied());
            item.setInterpretation(buildDataInterpretation(task, gate));
            return item;
        }).toList();
    }

    private List<LimsAiAssistCenterRespVO.SmartRecommendation> createRecommendations(LimsAiAssistCenterRespVO respVO) {
        List<LimsAiAssistCenterRespVO.SmartRecommendation> recommendations = new ArrayList<>();
        long blockingGaps = respVO.getEvidenceGaps().stream()
                .filter(gap -> "danger".equals(gap.getSeverity()) || "warning".equals(gap.getSeverity()))
                .filter(gap -> !"NO_MAJOR_GAP".equals(gap.getCode()))
                .count();
        if (blockingGaps > 0) {
            recommendations.add(recommendation("CLOSE_EVIDENCE_GAPS", "优先关闭证据缺口", "high",
                    "先处理任务门禁、报告签发和报告证据映射缺口，再进入复评审材料汇总。"));
        }
        if (respVO.getStandardAnswer() == null || respVO.getStandardAnswer().getMatchedClauses().isEmpty()) {
            recommendations.add(recommendation("MAINTAIN_STANDARD_KB", "维护标准知识库", "medium",
                    "补齐标准条款和证据类型映射，提高标准问答和报告解读的命中率。"));
        }
        if (respVO.getReportInterpretation() == null || !"issued".equalsIgnoreCase(respVO.getReportInterpretation().getStatus())) {
            recommendations.add(recommendation("ISSUE_REPORT", "完成报告签发", "medium",
                    "报告签发后才能稳定沉淀报告证据对象、文件地址和输出 hash。"));
        }
        if (recommendations.isEmpty()) {
            recommendations.add(recommendation("KEEP_TRACEABILITY", "持续维护证据链", "low",
                    "当前主要闭环可用，建议持续关注标准版本、设备校准、人员授权和环境记录到期风险。"));
        }
        return recommendations;
    }

    private LimsTestRequestDO resolveRequest(Long requestId) {
        if (requestId != null) {
            return requestMapper.selectById(requestId);
        }
        List<LimsTestRequestDO> requests = requestMapper.selectList(new LambdaQueryWrapperX<LimsTestRequestDO>()
                .orderByDesc(LimsTestRequestDO::getId)
                .last("LIMIT 1"));
        return requests.isEmpty() ? null : requests.get(0);
    }

    private List<LabStandardClauseDO> loadStandardClauses() {
        LabStandardClausePageReqVO reqVO = pageReq(new LabStandardClausePageReqVO(), PAGE_SIZE_SAMPLE);
        reqVO.setStatus("active");
        PageResult<LabStandardClauseDO> activePage = standardService.getStandardClausePage(reqVO);
        List<LabStandardClauseDO> clauses = safeList(activePage.getList());
        if (!clauses.isEmpty()) {
            return clauses;
        }
        return safeList(standardService.getStandardClausePage(pageReq(new LabStandardClausePageReqVO(), PAGE_SIZE_SAMPLE)).getList());
    }

    private boolean matchesQuestion(LabStandardClauseDO clause, String question) {
        if (!StringUtils.hasText(question)) {
            return true;
        }
        String haystack = normalizeText(String.join(" ",
                defaultText(clause.getClauseCode()),
                defaultText(clause.getClauseTitle()),
                defaultText(clause.getClauseCategory()),
                defaultText(clause.getRequirementText()),
                defaultText(clause.getEvidenceTypeCodes())));
        String normalizedQuestion = normalizeText(question);
        if (StringUtils.hasText(normalizedQuestion) && haystack.contains(normalizedQuestion)) {
            return true;
        }
        for (String token : questionTokens(normalizedQuestion)) {
            if (haystack.contains(token)) {
                return true;
            }
        }
        return false;
    }

    private String buildAnswer(String question, List<LabStandardClauseDO> hits) {
        if (hits.isEmpty()) {
            return "标准知识库暂未命中与“" + question + "”直接相关的条款，请先维护标准条款和证据类型映射。";
        }
        LabStandardClauseDO first = hits.get(0);
        String requirement = StringUtils.hasText(first.getRequirementText()) ? first.getRequirementText() : "该条款已登记，但要求文本尚未维护。";
        return "基于当前标准知识库，优先参考 " + first.getClauseCode() + "《" + first.getClauseTitle()
                + "》。核心要求：" + requirement + " 相关证据类型：" + defaultText(first.getEvidenceTypeCodes(), "未配置") + "。";
    }

    private LimsAiAssistCenterRespVO.ClauseHit clauseHit(LabStandardClauseDO clause) {
        LimsAiAssistCenterRespVO.ClauseHit hit = new LimsAiAssistCenterRespVO.ClauseHit();
        hit.setClauseId(clause.getId());
        hit.setClauseCode(clause.getClauseCode());
        hit.setClauseTitle(clause.getClauseTitle());
        hit.setClauseCategory(clause.getClauseCategory());
        hit.setRequirementText(clause.getRequirementText());
        hit.setEvidenceTypeCodes(clause.getEvidenceTypeCodes());
        return hit;
    }

    private long countReportEvidenceLinks(LimsReportDO report) {
        LabEvidenceLinkPageReqVO reqVO = pageReq(new LabEvidenceLinkPageReqVO(), PAGE_SIZE_METRIC);
        reqVO.setLinkedBizType("lims_report");
        reqVO.setLinkedBizId(report.getId());
        return total(evidenceLinkService.getEvidenceLinkPage(reqVO));
    }

    private List<String> parseOutputFormats(String reportOutput) {
        if (!StringUtils.hasText(reportOutput)) {
            return List.of();
        }
        try {
            JsonNode root = objectMapper.readTree(reportOutput);
            Set<String> formats = new LinkedHashSet<>();
            JsonNode outputs = root.path("outputs");
            if (outputs.isArray()) {
                for (JsonNode output : outputs) {
                    String format = output.path("format").asText("");
                    if (StringUtils.hasText(format)) {
                        formats.add(format);
                    }
                }
            }
            return new ArrayList<>(formats);
        } catch (Exception ex) {
            return List.of();
        }
    }

    private String buildReportInterpretation(LimsReportDO report, List<String> outputFormats) {
        String statusText = "issued".equalsIgnoreCase(report.getStatus()) ? "已签发" : "未签发";
        String conclusion = StringUtils.hasText(report.getConclusion()) ? report.getConclusion() : "报告结论尚未维护";
        String formats = outputFormats.isEmpty() ? "未登记输出文件" : String.join("、", outputFormats);
        return "报告 " + report.getReportNo() + " 当前状态为" + statusText + "，结论为“" + conclusion
                + "”，已登记输出格式：" + formats + "。";
    }

    private String buildDataInterpretation(LimsTestTaskDO task, LimsTaskQualityGateRespVO gate) {
        if (Boolean.TRUE.equals(gate.getQualityGateSatisfied())) {
            return "任务 " + task.getTaskNo() + " 的原始记录、QC、证据和技术复核已满足报告准出门禁。";
        }
        return "任务 " + task.getTaskNo() + " 当前还有 " + safeInt(gate.getMissingRequirementCount())
                + " 个数据/证据缺口，需优先补齐后再进入报告生成。";
    }

    private String severityForMissing(String type) {
        String normalized = defaultText(type).toUpperCase(Locale.ROOT);
        if (normalized.contains("RAW") || normalized.contains("EVIDENCE")) {
            return "danger";
        }
        if (normalized.contains("QC") || normalized.contains("REVIEW")) {
            return "warning";
        }
        return "warning";
    }

    private String actionForMissing(String type) {
        String normalized = defaultText(type).toUpperCase(Locale.ROOT);
        if (normalized.contains("RAW")) {
            return "补录原始记录和必填结果字段。";
        }
        if (normalized.contains("QC")) {
            return "补齐并批准 QC 记录。";
        }
        if (normalized.contains("EQUIPMENT")) {
            return "绑定可用设备并上传校准/溯源证据。";
        }
        if (normalized.contains("PERSON")) {
            return "确认人员能力和授权证据。";
        }
        if (normalized.contains("REVIEW")) {
            return "完成技术复核。";
        }
        return "打开任务门禁查看并补齐缺口。";
    }

    private String clauseCategoryForMissing(String type) {
        String normalized = defaultText(type).toUpperCase(Locale.ROOT);
        if (normalized.contains("EQUIPMENT")) {
            return "equipment";
        }
        if (normalized.contains("PERSON")) {
            return "personnel";
        }
        if (normalized.contains("QC")) {
            return "quality";
        }
        if (normalized.contains("REVIEW")) {
            return "review";
        }
        return "record";
    }

    private LimsAiAssistCenterRespVO.KnowledgeMetric metric(String code, String label, Long value, String unit, String hint) {
        LimsAiAssistCenterRespVO.KnowledgeMetric metric = new LimsAiAssistCenterRespVO.KnowledgeMetric();
        metric.setCode(code);
        metric.setLabel(label);
        metric.setValue(value);
        metric.setUnit(unit);
        metric.setHint(hint);
        return metric;
    }

    private LimsAiAssistCenterRespVO.EvidenceGap gap(String code, String title, String severity, String message,
                                                     String actionText, String sourceType, String sourceNo, String clauseCategory) {
        LimsAiAssistCenterRespVO.EvidenceGap gap = new LimsAiAssistCenterRespVO.EvidenceGap();
        gap.setCode(code);
        gap.setTitle(title);
        gap.setSeverity(severity);
        gap.setMessage(message);
        gap.setActionText(actionText);
        gap.setSourceType(sourceType);
        gap.setSourceNo(sourceNo);
        gap.setClauseCategory(clauseCategory);
        return gap;
    }

    private LimsAiAssistCenterRespVO.SmartRecommendation recommendation(String code, String title, String priority, String content) {
        LimsAiAssistCenterRespVO.SmartRecommendation item = new LimsAiAssistCenterRespVO.SmartRecommendation();
        item.setCode(code);
        item.setTitle(title);
        item.setPriority(priority);
        item.setContent(content);
        return item;
    }

    private <T extends cn.iocoder.yudao.framework.common.pojo.PageParam> T pageReq(T reqVO, int pageSize) {
        reqVO.setPageNo(1);
        reqVO.setPageSize(pageSize);
        return reqVO;
    }

    private long total(PageResult<?> page) {
        return page == null || page.getTotal() == null ? 0L : page.getTotal();
    }

    private <T> List<T> safeList(List<T> list) {
        return list == null ? List.of() : list;
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }

    private String normalizeText(String text) {
        return defaultText(text).toLowerCase(Locale.ROOT).replaceAll("\\s+", "");
    }

    private List<String> questionTokens(String normalizedQuestion) {
        if (!StringUtils.hasText(normalizedQuestion)) {
            return List.of();
        }
        String[] rawTokens = normalizedQuestion.split("[,，。；;：:、/\\\\|()（）\\[\\]{}<>《》!?！？\\-]+");
        List<String> tokens = new ArrayList<>();
        for (String token : rawTokens) {
            if (token.length() >= 2) {
                tokens.add(token);
            }
        }
        for (String keyword : List.of("报告", "证据", "设备", "人员", "环境", "方法", "质控", "复核", "校准", "授权")) {
            if (normalizedQuestion.contains(keyword)) {
                tokens.add(keyword);
            }
        }
        return tokens;
    }

    private String defaultText(String value) {
        return value == null ? "" : value;
    }

    private String defaultText(String value, String defaultValue) {
        return StringUtils.hasText(value) ? value : defaultValue;
    }

}
