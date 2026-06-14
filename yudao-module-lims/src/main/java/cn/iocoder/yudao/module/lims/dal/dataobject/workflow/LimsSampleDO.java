package cn.iocoder.yudao.module.lims.dal.dataobject.workflow;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lims_sample")
@KeySequence("lims_sample_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LimsSampleDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long requestId;
    private String requestNo;
    private String sampleNo;
    private String sampleName;
    private String sampleType;
    private String sampleSpec;
    private String sampleQty;
    private String receivedDate;
    private String storageCondition;
    private String status;
    private String remark;

}
