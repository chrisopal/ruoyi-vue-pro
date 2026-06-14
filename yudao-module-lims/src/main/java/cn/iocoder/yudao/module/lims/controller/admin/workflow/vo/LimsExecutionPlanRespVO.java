package cn.iocoder.yudao.module.lims.controller.admin.workflow.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - LIMS 执行计划 Response VO")
@Data
public class LimsExecutionPlanRespVO {

    @Schema(description = "执行计划编号")
    private Long id;

    @Schema(description = "检测需求编号")
    private Long requestId;

    @Schema(description = "方向包快照哈希")
    private String workflowSnapshotHash;

    @Schema(description = "执行计划 JSON")
    private String planJson;

    @Schema(description = "报告草稿计划 JSON")
    private String reportDraftPlan;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
