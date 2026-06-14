package cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - 评审批次创建/修改 Request VO")
@Data
public class LabReviewBatchSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "批次编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "RP-MVP-001")
    @NotBlank(message = "批次编码不能为空")
    @Size(max = 64, message = "批次编码长度不能超过 64 个字符")
    private String batchCode;

    @Schema(description = "批次名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "MVP 评审材料包")
    @NotBlank(message = "批次名称不能为空")
    @Size(max = 128, message = "批次名称长度不能超过 128 个字符")
    private String batchName;

    @Schema(description = "检测方案包编号", example = "1")
    private Long domainPackId;

    @Schema(description = "评审类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "accreditation")
    @NotBlank(message = "评审类型不能为空")
    @Size(max = 64, message = "评审类型长度不能超过 64 个字符")
    private String reviewType;

    @Schema(description = "标准编码 JSON", example = "[\"ISO_IEC_17025\"]")
    private String standardCodes;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "active")
    @NotBlank(message = "状态不能为空")
    @Size(max = 32, message = "状态长度不能超过 32 个字符")
    private String status;

    @Schema(description = "备注")
    @Size(max = 512, message = "备注长度不能超过 512 个字符")
    private String remark;

}
