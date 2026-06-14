package cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 评审批次 Response VO")
@Data
public class LabReviewBatchRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "批次编码", example = "RP-MVP-001")
    private String batchCode;

    @Schema(description = "批次名称", example = "MVP 评审材料包")
    private String batchName;

    @Schema(description = "检测方案包编号", example = "1")
    private Long domainPackId;

    @Schema(description = "评审类型", example = "accreditation")
    private String reviewType;

    @Schema(description = "标准编码 JSON")
    private String standardCodes;

    @Schema(description = "状态", example = "active")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
