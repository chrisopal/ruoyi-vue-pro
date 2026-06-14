package cn.iocoder.yudao.module.lab.service.equipment.dto;

import lombok.Data;

@Data
public class LabEquipmentCalibrationEvidenceDTO {

    private Long id;
    private Long equipmentId;
    private String traceabilityType;
    private String certificateNo;
    private String calibrationOrg;
    private String calibrationDate;
    private String validTo;
    private String result;
    private String uncertainty;
    private String traceabilityChain;
    private String certificateFileUrl;
    private String nextDueDate;
    private String status;
    private boolean effective;

}
