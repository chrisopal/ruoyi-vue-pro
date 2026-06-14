package cn.iocoder.yudao.module.lims.service.workflow.model;

public record AvailableEquipment(
        Long equipmentId,
        String equipmentCode,
        String equipmentName,
        String equipmentType,
        String domainCode,
        String capabilityScope,
        String calibrationValidUntil,
        Boolean iotEnabled,
        Long iotProductId,
        String iotDeviceId,
        String dataSourceType) {
}
