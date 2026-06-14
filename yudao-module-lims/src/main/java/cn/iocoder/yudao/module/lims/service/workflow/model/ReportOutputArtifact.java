package cn.iocoder.yudao.module.lims.service.workflow.model;

public record ReportOutputArtifact(
        String format,
        String fileName,
        String fileUrl,
        String contentHash,
        String generatedAt) {
}
