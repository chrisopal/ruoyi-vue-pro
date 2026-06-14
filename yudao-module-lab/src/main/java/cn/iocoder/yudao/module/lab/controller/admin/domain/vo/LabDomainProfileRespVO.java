package cn.iocoder.yudao.module.lab.controller.admin.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 实验室检测方向 Response VO")
@Data
public class LabDomainProfileRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "方向编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "industrial")
    private String domainCode;

    @Schema(description = "方向名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "工业品")
    private String domainName;

    @Schema(description = "方向说明", example = "工业材料、力学、硬度、冲击、焊接等检测方向")
    private String description;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "active")
    private String status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
