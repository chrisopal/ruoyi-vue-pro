package cn.iocoder.yudao.module.lims.service.workflow.gateway;

import cn.iocoder.yudao.module.lims.service.workflow.model.IssuedReportEvidence;

public interface ReportEvidenceGateway {

    Long registerIssuedReportEvidence(IssuedReportEvidence evidence);

}
