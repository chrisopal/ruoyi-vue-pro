package cn.iocoder.yudao.module.lims.dal.dataobject.workflow;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lims_test_task")
@KeySequence("lims_test_task_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LimsTestTaskDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long requestId;
    private String requestNo;
    private Long sampleId;
    private String sampleNo;
    private String taskNo;
    private String taskName;
    private String testItem;
    private String methodCode;
    private String methodName;
    private Long standardClauseId;
    private Long assignedUserId;
    private Long reviewerId;
    private Long durationMinutes;
    private Long equipmentId;
    private String equipmentCode;
    private String equipmentName;
    private String equipmentSnapshot;
    private String equipmentEvidenceSnapshot;
    private String plannedStartTime;
    private String plannedEndTime;
    private String status;
    private String taskStatus;
    private String scheduleStatus;
    private String actualStartTime;
    private String actualEndTime;
    private String methodSnapshot;
    private String readinessSnapshot;
    private String qcStatus;
    private String reviewStatus;
    private Boolean reportEligible;
    private String blockReason;
    private String remark;

}
