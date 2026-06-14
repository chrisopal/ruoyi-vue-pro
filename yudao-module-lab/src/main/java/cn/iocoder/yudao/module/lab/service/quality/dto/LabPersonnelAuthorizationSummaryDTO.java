package cn.iocoder.yudao.module.lab.service.quality.dto;

import lombok.Data;

@Data
public class LabPersonnelAuthorizationSummaryDTO {

    private Long authorizationId;
    private Long userId;
    private String userName;
    private String authType;
    private String authScope;
    private Long methodId;
    private Long equipmentId;
    private String authorizedTime;
    private String validFrom;
    private String validTo;
    private String status;
    private String fileUrl;
    private Long competenceId;
    private String competenceType;
    private String competenceItem;
    private String certificateNo;
    private String certificateFileUrl;
    private String assessmentResult;
    private boolean effective;

}
