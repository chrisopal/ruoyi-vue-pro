package cn.iocoder.yudao.module.lims.dal.dataobject.workflow;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lims_task_raw_record")
@KeySequence("lims_task_raw_record_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LimsTaskRawRecordDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long taskId;
    private String taskNo;
    private String recordType;
    private String recordJson;
    private String attachmentUrl;
    private Long versionNo;
    private Long submittedBy;
    private String submittedTime;
    private String status;

}
