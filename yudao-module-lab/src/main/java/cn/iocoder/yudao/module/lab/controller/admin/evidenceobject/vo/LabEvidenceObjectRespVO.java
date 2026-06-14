package cn.iocoder.yudao.module.lab.controller.admin.evidenceobject.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 证据对象 Response VO")
@Data
public class LabEvidenceObjectRespVO {

    private Long id;
    private String evidenceCode;
    private String evidenceName;
    private String evidenceType;
    private String sourceObject;
    private Long sourceObjectId;
    private String sourceObjectNo;
    private String businessDomain;
    private String fileUrl;
    private String fileName;
    private String fileFormat;
    private String evidenceHash;
    private String issuedBy;
    private LocalDate issuedAt;
    private LocalDate validFrom;
    private LocalDate validTo;
    private String status;
    private String summary;
    private String remark;
    private LocalDateTime createTime;

}
