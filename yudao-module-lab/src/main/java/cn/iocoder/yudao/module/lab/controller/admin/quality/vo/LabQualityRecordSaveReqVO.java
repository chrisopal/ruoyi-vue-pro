package cn.iocoder.yudao.module.lab.controller.admin.quality.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 实验室符合性通用创建/修改 Request VO")
@Data
public class LabQualityRecordSaveReqVO {

    @Schema(description = "编号")
    private Long id;

    private Long clauseId;
    private String moduleCode;
    private String moduleName;
    private String functionCode;
    private String functionName;
    private String evidenceType;
    private String evidenceTable;
    private String evidenceDescription;
    private String requiredFlag;
    private String checkNo;
    private String checkName;
    private String checkType;
    private Long standardId;
    private String checkScope;
    private String startDate;
    private String endDate;
    private Long responsibleUserId;
    private String status;
    private String summary;
    private Long checkId;
    private String checkResult;
    private String evidenceSummary;
    private String evidenceStatus;
    private String findingDescription;
    private String severity;
    private Long responsibleDeptId;
    private String dueDate;
    private Long nonconformityId;
    private Long userId;
    private String userName;
    private String competenceType;
    private String competenceItem;
    private Long relatedMethodId;
    private Long relatedEquipmentId;
    private String certificateNo;
    private String certificateFileUrl;
    private String validFrom;
    private String validTo;
    private String assessmentResult;
    private String remark;
    private String authType;
    private String authScope;
    private Long methodId;
    private Long equipmentId;
    private Long authorizedBy;
    private String authorizedTime;
    private String fileUrl;
    private String traceabilityType;
    private String calibrationOrg;
    private String calibrationDate;
    private String result;
    private String uncertainty;
    private String traceabilityChain;
    private String nextDueDate;
    private String checkDate;
    private String checkMethod;
    private String checkRecord;
    private Long checkerId;
    private Long reviewerId;
    private Long areaId;
    private String areaName;
    private String recordTime;
    private String temperature;
    private String humidity;
    private String pressure;
    private String cleanliness;
    private String otherParams;
    private String dataSource;
    private Long recorderId;
    private String abnormalDescription;
    private String validationNo;
    private String validationType;
    private String validationDate;
    private String validationItems;
    private String conclusion;
    private String reportFileUrl;
    private String ncNo;
    private String sourceType;
    private Long sourceId;
    private String title;
    private String description;
    private String discoveredDate;
    private String actionNo;
    private String rootCause;
    private String correction;
    private String correctiveAction;
    private String preventiveAction;
    private String plannedFinishDate;
    private String actualFinishDate;
    private String verificationResult;
    private Long verifierId;
    private String verifiedTime;
    private String auditNo;
    private String auditName;
    private String auditScope;
    private String auditCriteria;
    private Long auditLeaderId;
    private String plannedStartDate;
    private String plannedEndDate;
    private String actualStartDate;
    private String actualEndDate;
    private String reviewNo;
    private String reviewName;
    private String reviewDate;
    private Long hostUserId;
    private String participants;
    private String inputSummary;
    private String outputDecision;
    private String improvementActions;

    @NotNull(message = "状态动作目标编号不能为空", groups = StatusAction.class)
    public Long getRequiredId() {
        return id;
    }

    public interface StatusAction {
    }

}
