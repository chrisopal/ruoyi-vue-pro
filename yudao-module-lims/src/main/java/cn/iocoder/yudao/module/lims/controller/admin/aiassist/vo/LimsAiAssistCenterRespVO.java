package cn.iocoder.yudao.module.lims.controller.admin.aiassist.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "管理后台 - LIMS AI 标准与解读中心 Response VO")
@Data
public class LimsAiAssistCenterRespVO {

    @Schema(description = "当前分析的检测需求编号")
    private Long requestId;

    @Schema(description = "当前分析的检测需求单号")
    private String requestNo;

    @Schema(description = "当前分析的检测需求名称")
    private String requestName;

    @Schema(description = "检测方向")
    private String domainCode;

    @Schema(description = "方向包编码")
    private String domainPackCode;

    @Schema(description = "方向包版本")
    private String domainPackVersion;

    @Schema(description = "知识库指标")
    private List<KnowledgeMetric> metrics = new ArrayList<>();

    @Schema(description = "标准问答")
    private StandardAnswer standardAnswer;

    @Schema(description = "证据缺口")
    private List<EvidenceGap> evidenceGaps = new ArrayList<>();

    @Schema(description = "报告解读")
    private ReportInterpretation reportInterpretation;

    @Schema(description = "实验数据解读")
    private List<DataInterpretation> dataInterpretations = new ArrayList<>();

    @Schema(description = "智能建议")
    private List<SmartRecommendation> recommendations = new ArrayList<>();

    @Data
    public static class KnowledgeMetric {

        @Schema(description = "指标编码")
        private String code;

        @Schema(description = "指标名称")
        private String label;

        @Schema(description = "指标值")
        private Long value;

        @Schema(description = "单位")
        private String unit;

        @Schema(description = "说明")
        private String hint;

    }

    @Data
    public static class StandardAnswer {

        @Schema(description = "问题")
        private String question;

        @Schema(description = "回答")
        private String answer;

        @Schema(description = "置信度")
        private Integer confidence;

        @Schema(description = "命中的标准条款")
        private List<ClauseHit> matchedClauses = new ArrayList<>();

    }

    @Data
    public static class ClauseHit {

        @Schema(description = "条款编号")
        private Long clauseId;

        @Schema(description = "条款编码")
        private String clauseCode;

        @Schema(description = "条款标题")
        private String clauseTitle;

        @Schema(description = "条款分类")
        private String clauseCategory;

        @Schema(description = "要求文本")
        private String requirementText;

        @Schema(description = "证据类型")
        private String evidenceTypeCodes;

    }

    @Data
    public static class EvidenceGap {

        @Schema(description = "缺口编码")
        private String code;

        @Schema(description = "缺口标题")
        private String title;

        @Schema(description = "风险等级")
        private String severity;

        @Schema(description = "缺口说明")
        private String message;

        @Schema(description = "建议动作")
        private String actionText;

        @Schema(description = "来源类型")
        private String sourceType;

        @Schema(description = "来源单号")
        private String sourceNo;

        @Schema(description = "条款分类")
        private String clauseCategory;

    }

    @Data
    public static class ReportInterpretation {

        @Schema(description = "报告编号")
        private String reportNo;

        @Schema(description = "报告状态")
        private String status;

        @Schema(description = "报告结论")
        private String conclusion;

        @Schema(description = "解读内容")
        private String interpretation;

        @Schema(description = "输出格式")
        private List<String> outputFormats = new ArrayList<>();

        @Schema(description = "风险等级")
        private String riskLevel;

    }

    @Data
    public static class DataInterpretation {

        @Schema(description = "任务编号")
        private String taskNo;

        @Schema(description = "检测项目")
        private String testItem;

        @Schema(description = "方法名称")
        private String methodName;

        @Schema(description = "任务状态")
        private String taskStatus;

        @Schema(description = "缺口数量")
        private Integer missingRequirementCount;

        @Schema(description = "质量门禁是否满足")
        private Boolean qualityGateSatisfied;

        @Schema(description = "解读内容")
        private String interpretation;

    }

    @Data
    public static class SmartRecommendation {

        @Schema(description = "建议编码")
        private String code;

        @Schema(description = "建议标题")
        private String title;

        @Schema(description = "优先级")
        private String priority;

        @Schema(description = "建议内容")
        private String content;

    }

}
