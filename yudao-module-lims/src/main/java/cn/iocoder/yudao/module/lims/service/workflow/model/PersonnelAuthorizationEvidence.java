package cn.iocoder.yudao.module.lims.service.workflow.model;

public record PersonnelAuthorizationEvidence(
        Long authorizationId,
        Long userId,
        String userName,
        String authType,
        String authScope,
        String validFrom,
        String validTo,
        String status,
        String fileUrl,
        Long competenceId,
        String competenceType,
        String competenceItem,
        String certificateNo,
        String certificateFileUrl,
        String assessmentResult,
        boolean effective) {
}
