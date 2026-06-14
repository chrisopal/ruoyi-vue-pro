package cn.iocoder.yudao.module.lab.service.domainpack.dto;

import lombok.Data;

import java.util.List;

@Data
public class LabDomainPackSnapshotDTO {

    private Long domainPackId;
    private Long domainId;
    private String packCode;
    private String packName;
    private String packVersion;
    private String industry;
    private String applicationScope;
    private String workflowSchema;
    private String templateSchema;
    private List<WorkflowNode> workflowNodes;
    private List<SampleRequirement> sampleRequirements;
    private List<TestItem> testItems;
    private List<ResultField> resultFields;
    private List<QcRule> qcRules;
    private List<ReportSection> reportSections;
    private List<EvidenceRequirement> evidenceRequirements;

    @Data
    public static class WorkflowNode {
        private String nodeCode;
        private String nodeName;
        private String roleName;
        private Boolean required;
        private Integer sort;
        private String status;
    }

    @Data
    public static class SampleRequirement {
        private String requirementCode;
        private String requirementName;
        private String requirementType;
        private String requirementText;
        private Integer sort;
        private String status;
    }

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
        private String enumOptions;
        private String demoValue;
        private Integer sort;
        private String status;
    }

    @Data
    public static class QcRule {
        private String ruleCode;
        private String ruleName;
        private String ruleType;
        private String ruleExpression;
        private String acceptanceCriteria;
        private Integer sort;
        private String status;
    }

    @Data
    public static class ReportSection {
        private String sectionCode;
        private String sectionName;
        private String sourceType;
        private Boolean visible;
        private Integer sort;
        private String status;
    }

    @Data
    public static class EvidenceRequirement {
        private String requirementCode;
        private String requirementName;
        private String evidenceType;
        private String sourceType;
        private String clauseCategory;
        private Boolean required;
        private Integer sort;
        private String status;
    }

}
