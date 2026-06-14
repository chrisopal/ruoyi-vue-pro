package cn.iocoder.yudao.module.lab.controller.admin.evidencelink.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 证据关联 Response VO")
@Data
public class LabEvidenceLinkRespVO {

    private Long id;
    private String evidenceCode;
    private String evidenceName;
    private String evidenceUrl;
    private String evidenceHash;
    private String sourceObject;
    private Long sourceObjectId;
    private String sourceObjectNo;
    private String linkedBizType;
    private Long linkedBizId;
    private String linkedBizNo;
    private String clauseCategory;
    private String linkStatus;
    private String remark;
    private LocalDateTime createTime;

}
