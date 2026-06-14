package cn.iocoder.yudao.module.lims.service.workflow.model;

public record IssuedReportEvidence(
        Long reportId,
        String reportNo,
        String reportName,
        Long requestId,
        String requestNo,
        String fileUrl,
        String dataSnapshotHash,
        String issuedTime,
        String summary) {
}
