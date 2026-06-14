package cn.iocoder.yudao.module.lims.service.workflow.model;

public record CalibrationEvidence(
        Long evidenceId,
        Long equipmentId,
        String traceabilityType,
        String certificateNo,
        String calibrationOrg,
        String calibrationDate,
        String validTo,
        String result,
        String certificateFileUrl,
        boolean effective) {
}
