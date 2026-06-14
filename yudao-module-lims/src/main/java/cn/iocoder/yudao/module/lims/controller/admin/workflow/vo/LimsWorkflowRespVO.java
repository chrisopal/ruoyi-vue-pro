package cn.iocoder.yudao.module.lims.controller.admin.workflow.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - LIMS 业务闭环 Response VO")
@Data
public class LimsWorkflowRespVO {

    private Long id;
    private String requestNo;
    private String requestName;
    private String requestType;
    private String requestSourceType;
    private String customerName;
    private String requesterName;
    private Long requesterOrgId;
    private Long costCenterId;
    private String commercialOrderId;
    private String internalProjectNo;
    private String domainCode;
    private Long domainPackId;
    private String domainPackCode;
    private String domainPackVersion;
    private Long standardId;
    private String priority;
    private String dueDate;
    private String status;
    private String scenarioConfig;
    private String workflowSnapshot;
    private String workflowSnapshotHash;
    private String workflowSnapshotTime;
    private String remark;

    private Long requestId;
    private String sampleNo;
    private String sampleName;
    private String sampleType;
    private String sampleSpec;
    private String sampleQty;
    private String receivedDate;
    private String storageCondition;

    private Long sampleId;
    private String taskNo;
    private String taskName;
    private String testItem;
    private String methodCode;
    private String methodName;
    private Long standardClauseId;
    private Long assignedUserId;
    private Long equipmentId;
    private String equipmentCode;
    private String equipmentName;
    private String equipmentSnapshot;
    private String equipmentEvidenceSnapshot;
    private String plannedStartTime;
    private String plannedEndTime;
    private String taskStatus;
    private String scheduleStatus;
    private String actualStartTime;
    private String actualEndTime;
    private Long durationMinutes;
    private String methodSnapshot;
    private String readinessSnapshot;
    private String qcStatus;
    private String reviewStatus;
    private Boolean reportEligible;
    private String blockReason;
    private String reviewType;
    private String reviewTime;
    private String comment;
    private String snapshotHash;

    private Long taskId;
    private String resultNo;
    private String resultValue;
    private String resultUnit;
    private String resultConclusion;
    private String rawData;
    private Long reviewerId;
    private String reviewedTime;

    private String reportNo;
    private String reportName;
    private Long templateId;
    private String templateVersion;
    private String reportContent;
    private String dataSnapshot;
    private String dataSnapshotHash;
    private String conclusion;
    private String fileUrl;
    private String reportOutput;
    private String issuedTime;
    private LocalDateTime createTime;

}
