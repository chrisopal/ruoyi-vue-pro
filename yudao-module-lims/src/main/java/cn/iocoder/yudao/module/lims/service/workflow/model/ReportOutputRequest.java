package cn.iocoder.yudao.module.lims.service.workflow.model;

import com.fasterxml.jackson.databind.JsonNode;

public record ReportOutputRequest(
        String reportNo,
        String reportName,
        String conclusion,
        String reportContent,
        JsonNode reportDraftPlan,
        String generatedAt) {
}
