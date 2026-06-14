package cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 评审材料条目 Response VO")
@Data
public class LabReviewItemRespVO {

    private Long id;
    private Long batchId;
    private String itemType;
    private String standardName;
    private String clauseCode;
    private String clauseCategory;
    private String checkPoint;
    private String expectedEvidence;
    private String ownerRole;
    private String evidenceName;
    private String sourceObject;
    private String linkedObjectNo;
    private String linkStatus;
    private String ncNo;
    private String severity;
    private String description;
    private String owner;
    private String dueDate;
    private String capaNo;
    private String rootCause;
    private String action;
    private String status;
    private Integer sort;
    private String remark;
    private LocalDateTime createTime;

}
