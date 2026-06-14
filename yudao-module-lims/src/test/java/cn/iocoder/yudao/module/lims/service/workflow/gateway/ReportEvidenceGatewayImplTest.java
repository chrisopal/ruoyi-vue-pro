package cn.iocoder.yudao.module.lims.service.workflow.gateway;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.lab.controller.admin.evidencelink.vo.LabEvidenceLinkSaveReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.evidenceobject.vo.LabEvidenceObjectSaveReqVO;
import cn.iocoder.yudao.module.lab.service.evidencelink.LabEvidenceLinkService;
import cn.iocoder.yudao.module.lab.service.evidenceobject.LabEvidenceObjectService;
import cn.iocoder.yudao.module.lab.service.standard.LabStandardService;
import cn.iocoder.yudao.module.lims.service.workflow.model.IssuedReportEvidence;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReportEvidenceGatewayImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private ReportEvidenceGatewayImpl reportEvidenceGateway;

    @Mock
    private LabEvidenceObjectService evidenceObjectService;
    @Mock
    private LabEvidenceLinkService evidenceLinkService;
    @Mock
    private LabStandardService standardService;

    @Test
    void registerIssuedReportEvidence_shouldLinkClauseToReportBusinessObject() {
        IssuedReportEvidence evidence = new IssuedReportEvidence(
                200L,
                "RPT-2026-001",
                "食品委托检测报告",
                1L,
                "REQ-2026-001",
                "/lims/report-output/RPT-2026-001.pdf",
                "hash-report-001",
                "2026-06-15 05:50:00",
                "检测报告已签发：RPT-2026-001");
        when(evidenceObjectService.createEvidenceObject(any(LabEvidenceObjectSaveReqVO.class))).thenReturn(88L);
        when(standardService.getFirstClauseIdByCategory("report")).thenReturn(77L);

        reportEvidenceGateway.registerIssuedReportEvidence(evidence);

        verify(evidenceObjectService).createEvidenceObject(argThat((LabEvidenceObjectSaveReqVO reqVO) ->
                "RPT-EVD-200".equals(reqVO.getEvidenceCode())
                        && "REPORT".equals(reqVO.getEvidenceType())
                        && "lims_report".equals(reqVO.getSourceObject())
                        && Long.valueOf(200L).equals(reqVO.getSourceObjectId())
                        && "RPT-2026-001".equals(reqVO.getSourceObjectNo())
                        && "/lims/report-output/RPT-2026-001.pdf".equals(reqVO.getFileUrl())));
        verify(evidenceLinkService).createEvidenceLink(argThat((LabEvidenceLinkSaveReqVO reqVO) ->
                Long.valueOf(88L).equals(reqVO.getEvidenceObjectId())
                        && "lims_report".equals(reqVO.getLinkedBizType())
                        && Long.valueOf(200L).equals(reqVO.getLinkedBizId())
                        && "RPT-2026-001".equals(reqVO.getLinkedBizNo())
                        && Long.valueOf(77L).equals(reqVO.getClauseId())
                        && "report".equals(reqVO.getClauseCategory())
                        && reqVO.getRemark().contains("REQ-2026-001")));
    }

}
