package cn.iocoder.yudao.module.lab.dal.dataobject.reviewpackage;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lab_review_batch")
@KeySequence("lab_review_batch_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LabReviewBatchDO extends TenantBaseDO {

    @TableId
    private Long id;
    private String batchCode;
    private String batchName;
    private Long domainPackId;
    private String reviewType;
    private String standardCodes;
    private String status;
    private String remark;

}
