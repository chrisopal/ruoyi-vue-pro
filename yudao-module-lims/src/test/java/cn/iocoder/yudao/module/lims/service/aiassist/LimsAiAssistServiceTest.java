package cn.iocoder.yudao.module.lims.service.aiassist;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class LimsAiAssistServiceTest extends BaseMockitoUnitTest {

    @InjectMocks
    private LimsAiAssistService service;

    @Mock
    private LimsTestRequestMapper requestMapper;
    @Mock
    private LimsTestTaskMapper taskMapper;
    @Mock
    private LimsReportMapper reportMapper;
    @Mock
    private LabStandardService standardService;
    @Mock
    private LabEvidenceObjectService evidenceObjectService;
    @Mock
    private LabEvidenceLinkService evidenceLinkService;
    @Mock
    private LimsWorkflowService workflowService;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getCenter_shouldAnswerAndDiagnoseFromCurrentEvidence() {
        when(requestMapper.selectById(100L)).thenReturn(request());
        when(taskMapper.selectListByRequestId(100L)).thenReturn(List.of(task()));
        when(reportMapper.selectByRequestId(100L)).thenReturn(report());
        when(standardService.getStandardPage(any())).thenReturn(new PageResult<>(List.of(), 2L));
        when(standardService.getStandardClausePage(any())).thenReturn(new PageResult<>(List.of(
                clause("REPORT", "报告签发", "report", "报告应包含检测结论和授权签发信息。", "REPORT"),
                clause("EQUIPMENT", "设备校准", "equipment", "检测设备应保持校准有效并形成溯源证据。", "EQUIPMENT_CERTIFICATE")), 2L));
        when(evidenceObjectService.getEvidenceObjectPage(any())).thenReturn(new PageResult<>(List.of(), 3L));
        when(evidenceLinkService.getEvidenceLinkPage(any())).thenReturn(new PageResult<>(List.of(), 0L));
        LimsTaskQualityGateRespVO taskGate = taskGate();
        when(workflowService.getTaskQualityGate(200L)).thenReturn(taskGate);

        LimsAiAssistCenterRespVO center = service.getCenter(100L, "报告需要哪些证据");

        assertEquals("REQ-001", center.getRequestNo());
        assertTrue(center.getStandardAnswer().getAnswer().contains("REPORT"));
        assertTrue(center.getStandardAnswer().getMatchedClauses().stream()
                .anyMatch(clause -> "REPORT".equals(clause.getClauseCode())));
        assertTrue(center.getEvidenceGaps().stream().anyMatch(gap -> "RAW_RESULT_FIELD".equals(gap.getCode())));
        assertTrue(center.getEvidenceGaps().stream().anyMatch(gap -> "REPORT_EVIDENCE_UNLINKED".equals(gap.getCode())));
        assertEquals(List.of("PDF", "EXCEL"), center.getReportInterpretation().getOutputFormats());
        assertTrue(center.getDataInterpretations().get(0).getInterpretation().contains("1 个数据/证据缺口"));
        assertTrue(center.getRecommendations().stream().anyMatch(item -> "CLOSE_EVIDENCE_GAPS".equals(item.getCode())));
    }

    private LimsTaskQualityGateRespVO taskGate() {
        LimsTaskQualityGateRespVO gate = new LimsTaskQualityGateRespVO();
        gate.setTaskId(200L);
        gate.setMissingRequirementCount(1);
        gate.setQualityGateSatisfied(false);
        ArrayNode missing = objectMapper.createArrayNode();
        missing.addObject()
                .put("type", "RAW_RESULT_FIELD")
                .put("code", "RAW_RESULT_FIELD")
                .put("name", "原始结果字段")
                .put("message", "必填结果字段未在原始记录 rawData.resultValues 中提交有效值。");
        gate.setMissingRequirements(missing);
        return gate;
    }

    private static LimsTestRequestDO request() {
        LimsTestRequestDO request = new LimsTestRequestDO();
        request.setId(100L);
        request.setRequestNo("REQ-001");
        request.setRequestName("食品常规检测");
        request.setDomainCode("FOOD");
        request.setDomainPackCode("FOOD_ROUTINE");
        request.setDomainPackVersion("1.0");
        return request;
    }

    private static LimsTestTaskDO task() {
        LimsTestTaskDO task = new LimsTestTaskDO();
        task.setId(200L);
        task.setRequestId(100L);
        task.setTaskNo("TASK-001");
        task.setTestItem("PH");
        task.setMethodName("pH 检测方法");
        task.setTaskStatus("approved");
        return task;
    }

    private static LimsReportDO report() {
        LimsReportDO report = new LimsReportDO();
        report.setId(300L);
        report.setRequestId(100L);
        report.setReportNo("RPT-001");
        report.setStatus("issued");
        report.setConclusion("符合");
        report.setReportOutput("""
                {"primaryFormat":"PDF","outputs":[{"format":"PDF"},{"format":"EXCEL"}]}
                """);
        return report;
    }

    private static LabStandardClauseDO clause(String code, String title, String category, String requirementText,
                                              String evidenceTypeCodes) {
        LabStandardClauseDO clause = new LabStandardClauseDO();
        clause.setId((long) code.hashCode());
        clause.setClauseCode(code);
        clause.setClauseTitle(title);
        clause.setClauseCategory(category);
        clause.setRequirementText(requirementText);
        clause.setEvidenceTypeCodes(evidenceTypeCodes);
        clause.setStatus("active");
        return clause;
    }

}
