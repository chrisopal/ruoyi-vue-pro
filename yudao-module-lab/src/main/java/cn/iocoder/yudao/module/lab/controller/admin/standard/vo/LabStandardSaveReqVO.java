package cn.iocoder.yudao.module.lab.controller.admin.standard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - 实验室标准创建/修改 Request VO")
@Data
public class LabStandardSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "标准编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "ISO_IEC_17025")
    @NotBlank(message = "标准编码不能为空")
    @Size(max = 64, message = "标准编码长度不能超过 64 个字符")
    private String standardCode;

    @Schema(description = "标准名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "ISO/IEC 17025")
    @NotBlank(message = "标准名称不能为空")
    @Size(max = 256, message = "标准名称长度不能超过 256 个字符")
    private String standardName;

    @Schema(description = "标准版本", requiredMode = Schema.RequiredMode.REQUIRED, example = "2017")
    @NotBlank(message = "标准版本不能为空")
    @Size(max = 64, message = "标准版本长度不能超过 64 个字符")
    private String standardVersion;

    @Schema(description = "标准类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "accreditation")
    @NotBlank(message = "标准类型不能为空")
    @Size(max = 64, message = "标准类型长度不能超过 64 个字符")
    private String standardType;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "active")
    @NotBlank(message = "状态不能为空")
    @Size(max = 32, message = "状态长度不能超过 32 个字符")
    private String status;

    @Schema(description = "备注")
    @Size(max = 512, message = "备注长度不能超过 512 个字符")
    private String remark;

}
