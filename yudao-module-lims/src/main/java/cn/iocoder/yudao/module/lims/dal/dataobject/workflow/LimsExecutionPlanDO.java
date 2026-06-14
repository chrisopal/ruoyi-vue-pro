package cn.iocoder.yudao.module.lims.dal.dataobject.workflow;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lims_execution_plan")
@KeySequence("lims_execution_plan_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LimsExecutionPlanDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long requestId;
    private String workflowSnapshotHash;
    private String planJson;
    private String status;

}
