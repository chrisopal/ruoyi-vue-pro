package cn.iocoder.yudao.module.lab.controller.admin.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - 实验室检测方向新增/修改 Request VO")
@Data
public class LabDomainProfileSaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "方向编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "industrial")
    @NotBlank(message = "方向编码不能为空")
    @Size(max = 64, message = "方向编码长度不能超过 64 个字符")
    private String domainCode;

    @Schema(description = "方向名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "工业品")
    @NotBlank(message = "方向名称不能为空")
    @Size(max = 128, message = "方向名称长度不能超过 128 个字符")
    private String domainName;

    @Schema(description = "方向说明", example = "工业材料、力学、硬度、冲击、焊接等检测方向")
    @Size(max = 512, message = "方向说明长度不能超过 512 个字符")
    private String description;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "active")
    @NotBlank(message = "状态不能为空")
    private String status;

}
