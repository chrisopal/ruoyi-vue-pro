package cn.iocoder.yudao.module.lab.controller.admin.packconfig.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 方案包可视化配置保存 Request VO")
@Data
public class LabPackConfigSaveReqVO {

    @Schema(description = "检测方案包编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "检测方案包不能为空")
    private Long domainPackId;

    @Valid
    private List<WorkflowNode> workflowNodes;

    @Valid
    private List<TestItem> testItems;

    @Valid
    private List<ResultField> resultFields;

    @Valid
    private List<ReportSection> reportSections;

    @Schema(description = "流程节点")
    @Data
    public static class WorkflowNode {
        private String nodeCode;
        private String nodeName;
        private String roleName;
        private Boolean required;
        private Integer sort;
        private String status;
    }

    @Schema(description = "检测项目")
    @Data
    public static class TestItem {
        private String itemCode;
        private String itemName;
        private String methodCode;
        private String methodName;
        private String standardCode;
        private String resultUnit;
        private String demoValue;
        private Integer sort;
        private String status;
    }

    @Schema(description = "结果字段")
    @Data
    public static class ResultField {
        private String itemCode;
        private String fieldCode;
        private String fieldName;
        private String fieldType;
        private String unit;
        private Boolean required;
        private String minValue;
        private String maxValue;
        private List<String> enumOptions;
        private String demoValue;
        private Integer sort;
        private String status;
    }

    @Schema(description = "报告章节")
    @Data
    public static class ReportSection {
        private String sectionCode;
        private String sectionName;
        private String sourceType;
        private Boolean visible;
        private Integer sort;
        private String status;
    }

}
