package cn.iocoder.yudao.module.lims.dal.dataobject.workflow;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lims_task_schedule")
@KeySequence("lims_task_schedule_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LimsTaskScheduleDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long taskId;
    private Long requestId;
    private Long sampleId;
    private Long equipmentId;
    private Long assignedUserId;
    private String plannedStartTime;
    private String plannedEndTime;
    private String scheduleStatus;
    private String conflictReason;
    private Boolean locked;

}
