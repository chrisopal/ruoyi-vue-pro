package cn.iocoder.yudao.module.lims.service.workflow.model;

import java.util.List;

public record ReportOutputBundle(
        String primaryFormat,
        String primaryFileUrl,
        String generatedAt,
        List<ReportOutputArtifact> outputs) {
}
