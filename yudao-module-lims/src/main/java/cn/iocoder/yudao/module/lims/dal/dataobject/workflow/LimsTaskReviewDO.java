package cn.iocoder.yudao.module.lims.dal.dataobject.workflow;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lims_task_review")
@KeySequence("lims_task_review_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LimsTaskReviewDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long taskId;
    private String taskNo;
    private String reviewType;
    private String reviewStatus;
    private Long reviewerId;
    private String reviewTime;
    private String comment;
    private String snapshotHash;

}
