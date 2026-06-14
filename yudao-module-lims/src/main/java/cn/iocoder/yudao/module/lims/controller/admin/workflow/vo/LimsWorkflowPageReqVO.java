package cn.iocoder.yudao.module.lims.controller.admin.workflow.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - LIMS 业务闭环分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LimsWorkflowPageReqVO extends PageParam {

    @Schema(description = "关键字")
    private String keyword;

    @Schema(description = "检测需求编号")
    private String requestNo;

    @Schema(description = "检测需求编号")
    private Long requestId;

    @Schema(description = "样品编号")
    private Long sampleId;

    @Schema(description = "检测任务编号")
    private Long taskId;

    @Schema(description = "执行人")
    private Long assignedUserId;

    @Schema(description = "设备编号")
    private Long equipmentId;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "任务生命周期状态")
    private String taskStatus;

    @Schema(description = "排程状态")
    private String scheduleStatus;

    @Schema(description = "计划开始时间起")
    private String plannedStartTimeBegin;

    @Schema(description = "计划开始时间止")
    private String plannedStartTimeEnd;

    @Schema(description = "检测场景方案包编号")
    private Long domainPackId;

}
