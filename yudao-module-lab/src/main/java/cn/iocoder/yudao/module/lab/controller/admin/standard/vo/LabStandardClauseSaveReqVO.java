package cn.iocoder.yudao.module.lab.controller.admin.standard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - 实验室标准条款创建/修改 Request VO")
@Data
public class LabStandardClauseSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "标准编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "标准不能为空")
    private Long standardId;

    @Schema(description = "条款编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "PERSONNEL")
    @NotBlank(message = "条款编号不能为空")
    @Size(max = 64, message = "条款编号长度不能超过 64 个字符")
    private String clauseCode;

    @Schema(description = "条款标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "人员能力与授权")
    @NotBlank(message = "条款标题不能为空")
    @Size(max = 256, message = "条款标题长度不能超过 256 个字符")
    private String clauseTitle;

    @Schema(description = "条款业务分类", requiredMode = Schema.RequiredMode.REQUIRED, example = "personnel")
    @NotBlank(message = "条款业务分类不能为空")
    @Size(max = 64, message = "条款业务分类长度不能超过 64 个字符")
    private String clauseCategory;

    @Schema(description = "要求摘要", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "要求摘要不能为空")
    @Size(max = 1024, message = "要求摘要长度不能超过 1024 个字符")
    private String requirementText;

    @Schema(description = "证据类型编码 JSON", example = "[\"PERSON_AUTH\"]")
    private String evidenceTypeCodes;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "active")
    @NotBlank(message = "状态不能为空")
    @Size(max = 32, message = "状态长度不能超过 32 个字符")
    private String status;

}
