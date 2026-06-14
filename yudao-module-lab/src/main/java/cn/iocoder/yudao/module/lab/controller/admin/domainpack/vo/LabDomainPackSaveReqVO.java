package cn.iocoder.yudao.module.lab.controller.admin.domainpack.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - 检测方案包创建/修改 Request VO")
@Data
public class LabDomainPackSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "检测领域编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "检测领域不能为空")
    private Long domainId;

    @Schema(description = "方案包编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "FOOD_ROUTINE")
    @NotBlank(message = "方案包编码不能为空")
    @Size(max = 64, message = "方案包编码长度不能超过 64 个字符")
    private String packCode;

    @Schema(description = "方案包名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "食品常规检测方案")
    @NotBlank(message = "方案包名称不能为空")
    @Size(max = 128, message = "方案包名称长度不能超过 128 个字符")
    private String packName;

    @Schema(description = "方案包版本", requiredMode = Schema.RequiredMode.REQUIRED, example = "1.0")
    @NotBlank(message = "方案包版本不能为空")
    @Size(max = 32, message = "方案包版本长度不能超过 32 个字符")
    private String packVersion;

    @Schema(description = "行业方向", example = "食品")
    @Size(max = 64, message = "行业方向长度不能超过 64 个字符")
    private String industry;

    @Schema(description = "适用范围", example = "食品理化与微生物常规项目")
    @Size(max = 512, message = "适用范围长度不能超过 512 个字符")
    private String applicationScope;

    @Schema(description = "流程配置 JSON")
    private String workflowSchema;

    @Schema(description = "模板配置 JSON")
    private String templateSchema;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "active")
    @NotBlank(message = "状态不能为空")
    @Size(max = 32, message = "状态长度不能超过 32 个字符")
    private String status;

    @Schema(description = "备注", example = "用于新客户快速启用")
    @Size(max = 512, message = "备注长度不能超过 512 个字符")
    private String remark;

}
