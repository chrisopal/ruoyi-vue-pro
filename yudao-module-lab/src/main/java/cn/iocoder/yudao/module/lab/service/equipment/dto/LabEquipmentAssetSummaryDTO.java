package cn.iocoder.yudao.module.lab.service.equipment.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LabEquipmentAssetSummaryDTO {

    private Long id;
    private String equipmentCode;
    private String equipmentName;
    private String equipmentType;
    private String domainCode;
    private String capabilityScope;
    private LocalDate calibrationValidUntil;
    private Boolean iotEnabled;
    private Long iotProductId;
    private String iotDeviceId;
    private String dataSourceType;

}
