package cn.iocoder.yudao.module.lims.controller.admin.workflow.vo;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - LIMS 任务质量门禁 Response VO")
@Data
public class LimsTaskQualityGateRespVO {

    private Long taskId;
    private Long requestId;
    private String taskNo;
    private String taskName;
    private String testItem;
    private String methodCode;
    private String methodName;
    private String requestNo;
    private String domainCode;
    private String domainPackCode;
    private String domainPackVersion;
    private String workflowSnapshotHash;
    private String executionPlanStatus;

    private JsonNode sampleRequirements;
    private JsonNode resultFields;
    private JsonNode qcRules;
    private JsonNode evidenceRequirements;
    private JsonNode reportSections;
    private JsonNode templateCodes;
    private JsonNode sectionRules;
    private JsonNode dataBindings;
    private JsonNode reportDraftPlan;
    private JsonNode qcRuleSnapshot;
    private JsonNode missingRequirements;

    private String taskStatus;
    private String scheduleStatus;
    private String qcStatus;
    private String reviewStatus;
    private Boolean reportEligible;
    private String blockReason;

    private String readinessSnapshot;
    private String equipmentSnapshot;
    private String equipmentEvidenceSnapshot;
    private String personnelSnapshot;
    private String personnelEvidenceSnapshot;
    private Boolean hasEquipmentEvidence;
    private Boolean hasPersonnelEvidence;

    private Integer rawRecordCount;
    private Integer qcRecordCount;
    private Integer approvedQcRecordCount;
    private Integer reviewRecordCount;
    private Integer approvedReviewCount;
    private Integer qcRuleCount;
    private Integer satisfiedQcRuleCount;
    private Integer evidenceRequirementCount;
    private Integer missingRequirementCount;
    private Boolean rawRecordSatisfied;
    private Boolean qcSatisfied;
    private Boolean equipmentEvidenceSatisfied;
    private Boolean personnelEvidenceSatisfied;
    private Boolean reviewSatisfied;
    private Boolean qualityGateSatisfied;

}
