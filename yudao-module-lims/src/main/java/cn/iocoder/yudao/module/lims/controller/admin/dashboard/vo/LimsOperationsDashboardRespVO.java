package cn.iocoder.yudao.module.lims.controller.admin.dashboard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "管理后台 - TIC LIMS 评审与运营看板 Response VO")
@Data
public class LimsOperationsDashboardRespVO {

    @Schema(description = "申请准备度分数")
    private Integer applicationReadinessScore;

    @Schema(description = "复评审风险分数")
    private Integer reassessmentRiskScore;

    @Schema(description = "能力范围覆盖率")
    private Integer capabilityCoverageRate;

    @Schema(description = "整改闭环率")
    private Integer correctionClosureRate;

    @Schema(description = "关键指标")
    private List<MetricCard> metrics = new ArrayList<>();

    @Schema(description = "执行阶段")
    private List<WorkflowStage> workflowStages = new ArrayList<>();

    @Schema(description = "能力范围覆盖")
    private List<CoverageItem> capabilityCoverage = new ArrayList<>();

    @Schema(description = "风险项")
    private List<RiskItem> risks = new ArrayList<>();

    @Schema(description = "最近报告")
    private List<RecentReport> recentReports = new ArrayList<>();

    @Data
    public static class MetricCard {

        @Schema(description = "指标编码")
        private String code;

        @Schema(description = "指标名称")
        private String label;

        @Schema(description = "指标值")
        private Long value;

        @Schema(description = "单位")
        private String unit;

        @Schema(description = "等级")
        private String level;

        @Schema(description = "说明")
        private String hint;

    }

    @Data
    public static class WorkflowStage {

        @Schema(description = "阶段编码")
        private String code;

        @Schema(description = "阶段名称")
        private String label;

        @Schema(description = "总量")
        private Long total;

        @Schema(description = "完成量")
        private Long done;

        @Schema(description = "阻塞量")
        private Long blocked;

        @Schema(description = "完成率")
        private Integer completionRate;

    }

    @Data
    public static class CoverageItem {

        @Schema(description = "方向编码")
        private String domainCode;

        @Schema(description = "方向名称")
        private String domainName;

        @Schema(description = "已发布方向包数量")
        private Long publishedPackCount;

        @Schema(description = "可用设备数量")
        private Long enabledEquipmentCount;

        @Schema(description = "检测任务数量")
        private Long taskCount;

        @Schema(description = "报告数量")
        private Long reportCount;

        @Schema(description = "覆盖率")
        private Integer coverageRate;

    }

    @Data
    public static class RiskItem {

        @Schema(description = "风险编码")
        private String code;

        @Schema(description = "风险标题")
        private String title;

        @Schema(description = "风险描述")
        private String description;

        @Schema(description = "等级")
        private String level;

        @Schema(description = "所属上下文")
        private String ownerContext;

        @Schema(description = "关联数量")
        private Long relatedCount;

        @Schema(description = "建议动作")
        private String actionText;

    }

    @Data
    public static class RecentReport {

        @Schema(description = "报告编号")
        private String reportNo;

        @Schema(description = "检测需求编号")
        private String requestNo;

        @Schema(description = "报告名称")
        private String reportName;

        @Schema(description = "状态")
        private String status;

        @Schema(description = "模板版本")
        private String templateVersion;

        @Schema(description = "文件地址")
        private String fileUrl;

        @Schema(description = "签发时间")
        private String issuedTime;

    }

}
