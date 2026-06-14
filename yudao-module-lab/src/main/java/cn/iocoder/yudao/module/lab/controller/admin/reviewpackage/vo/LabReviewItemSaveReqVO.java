package cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - 评审材料条目创建/修改 Request VO")
@Data
public class LabReviewItemSaveReqVO {

    private Long id;

    @NotNull(message = "评审批次不能为空")
    private Long batchId;

    @NotBlank(message = "条目类型不能为空")
    @Size(max = 32, message = "条目类型长度不能超过 32 个字符")
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

}
