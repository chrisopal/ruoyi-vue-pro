package cn.iocoder.yudao.module.lims.dal.dataobject.workflow;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lims_task_event_log")
@KeySequence("lims_task_event_log_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LimsTaskEventLogDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long taskId;
    private String taskNo;
    private String eventType;
    private String fromStatus;
    private String toStatus;
    private Long operatorId;
    private String eventTime;
    private String reason;
    private String payloadJson;

}
