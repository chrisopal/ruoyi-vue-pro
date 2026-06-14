package cn.iocoder.yudao.module.lims.dal.dataobject.workflow;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lims_test_result")
@KeySequence("lims_test_result_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LimsTestResultDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long requestId;
    private String requestNo;
    private Long sampleId;
    private String sampleNo;
    private Long taskId;
    private String taskNo;
    private String resultNo;
    private String testItem;
    private String resultValue;
    private String resultUnit;
    private String resultConclusion;
    private String rawData;
    private Long reviewerId;
    private String reviewedTime;
    private String status;
    private String remark;

}
