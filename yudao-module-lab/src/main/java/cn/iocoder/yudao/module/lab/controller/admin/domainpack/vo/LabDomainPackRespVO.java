package cn.iocoder.yudao.module.lab.controller.admin.domainpack.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 检测方案包 Response VO")
@Data
public class LabDomainPackRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "检测领域编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long domainId;

    @Schema(description = "方案包编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "FOOD_ROUTINE")
    private String packCode;

    @Schema(description = "方案包名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "食品常规检测方案")
    private String packName;

    @Schema(description = "方案包版本", requiredMode = Schema.RequiredMode.REQUIRED, example = "1.0")
    private String packVersion;

    @Schema(description = "行业方向", example = "食品")
    private String industry;

    @Schema(description = "适用范围", example = "食品理化与微生物常规项目")
    private String applicationScope;

    @Schema(description = "流程配置 JSON")
    private String workflowSchema;

    @Schema(description = "模板配置 JSON")
    private String templateSchema;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "active")
    private String status;

    @Schema(description = "备注", example = "用于新客户快速启用")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
