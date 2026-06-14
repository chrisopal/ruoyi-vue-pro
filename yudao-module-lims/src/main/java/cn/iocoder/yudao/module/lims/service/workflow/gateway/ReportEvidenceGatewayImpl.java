package cn.iocoder.yudao.module.lims.service.workflow.gateway;

import cn.iocoder.yudao.module.lab.controller.admin.evidencelink.vo.LabEvidenceLinkSaveReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.evidenceobject.vo.LabEvidenceObjectSaveReqVO;
import cn.iocoder.yudao.module.lab.service.evidencelink.LabEvidenceLinkService;
import cn.iocoder.yudao.module.lab.service.evidenceobject.LabEvidenceObjectService;
import cn.iocoder.yudao.module.lab.service.standard.LabStandardService;
import cn.iocoder.yudao.module.lims.service.workflow.model.IssuedReportEvidence;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Service
public class ReportEvidenceGatewayImpl implements ReportEvidenceGateway {

    private static final String EVIDENCE_TYPE_REPORT = "REPORT";
    private static final String SOURCE_OBJECT_LIMS_REPORT = "lims_report";
    private static final String BUSINESS_DOMAIN_REPORT = "report";
    private static final String LINKED_BIZ_TYPE_LIMS_REQUEST = "lims_request";

    @Resource
    private LabEvidenceObjectService evidenceObjectService;
    @Resource
    private LabEvidenceLinkService evidenceLinkService;
    @Resource
    private LabStandardService standardService;

    @Override
    public Long registerIssuedReportEvidence(IssuedReportEvidence evidence) {
        Long evidenceObjectId = evidenceObjectService.createEvidenceObject(buildEvidenceObject(evidence));
        evidenceLinkService.createEvidenceLink(buildEvidenceLink(evidence, evidenceObjectId));
        return evidenceObjectId;
    }

    private LabEvidenceObjectSaveReqVO buildEvidenceObject(IssuedReportEvidence evidence) {
        LabEvidenceObjectSaveReqVO reqVO = new LabEvidenceObjectSaveReqVO();
        reqVO.setEvidenceCode("RPT-EVD-" + evidence.reportId());
        reqVO.setEvidenceName(resolveReportName(evidence));
        reqVO.setEvidenceType(EVIDENCE_TYPE_REPORT);
        reqVO.setSourceObject(SOURCE_OBJECT_LIMS_REPORT);
        reqVO.setSourceObjectId(evidence.reportId());
        reqVO.setSourceObjectNo(evidence.reportNo());
        reqVO.setBusinessDomain(BUSINESS_DOMAIN_REPORT);
        reqVO.setFileUrl(evidence.fileUrl());
        reqVO.setFileName(evidence.reportNo());
        reqVO.setFileFormat(resolveFileFormat(evidence.fileUrl()));
        reqVO.setEvidenceHash(evidence.dataSnapshotHash());
        reqVO.setIssuedBy("LIMS");
        reqVO.setIssuedAt(LocalDate.now());
        reqVO.setStatus("effective");
        reqVO.setSummary(evidence.summary());
        return reqVO;
    }

    private LabEvidenceLinkSaveReqVO buildEvidenceLink(IssuedReportEvidence evidence, Long evidenceObjectId) {
        LabEvidenceLinkSaveReqVO reqVO = new LabEvidenceLinkSaveReqVO();
        reqVO.setEvidenceObjectId(evidenceObjectId);
        reqVO.setLinkedBizType(LINKED_BIZ_TYPE_LIMS_REQUEST);
        reqVO.setLinkedBizId(evidence.requestId());
        reqVO.setLinkedBizNo(evidence.requestNo());
        reqVO.setClauseId(standardService.getFirstClauseIdByCategory(BUSINESS_DOMAIN_REPORT));
        reqVO.setClauseCategory(BUSINESS_DOMAIN_REPORT);
        reqVO.setLinkStatus("linked");
        reqVO.setLinkReason("已签发检测报告支撑报告条款证据");
        reqVO.setRemark("由 LIMS 报告签发自动生成");
        return reqVO;
    }

    private String resolveReportName(IssuedReportEvidence evidence) {
        return StringUtils.hasText(evidence.reportName()) ? evidence.reportName() : evidence.reportNo();
    }

    private String resolveFileFormat(String fileUrl) {
        if (!StringUtils.hasText(fileUrl) || !fileUrl.contains(".")) {
            return null;
        }
        String suffix = fileUrl.substring(fileUrl.lastIndexOf('.') + 1);
        return suffix.length() > 32 ? null : suffix.toLowerCase();
    }

}
