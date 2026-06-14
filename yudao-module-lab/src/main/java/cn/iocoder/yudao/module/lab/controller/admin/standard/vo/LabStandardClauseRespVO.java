package cn.iocoder.yudao.module.lab.controller.admin.standard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 实验室标准条款 Response VO")
@Data
public class LabStandardClauseRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "标准编号", example = "1")
    private Long standardId;

    @Schema(description = "条款编号", example = "PERSONNEL")
    private String clauseCode;

    @Schema(description = "条款标题", example = "人员能力与授权")
    private String clauseTitle;

    @Schema(description = "条款业务分类", example = "personnel")
    private String clauseCategory;

    @Schema(description = "要求摘要")
    private String requirementText;

    @Schema(description = "证据类型编码 JSON")
    private String evidenceTypeCodes;

    @Schema(description = "状态", example = "active")
    private String status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
