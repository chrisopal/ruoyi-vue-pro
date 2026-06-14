package cn.iocoder.yudao.module.lims.service.workflow.model;

public record AvailablePersonnel(
        Long userId,
        String userName,
        String authType,
        String authScope,
        Long authorizationId,
        String validTo,
        String competenceType,
        String competenceItem,
        String certificateNo,
        boolean effective) {
}
