package cn.iocoder.yudao.module.lims.dal.dataobject.resultvalue;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lims_test_result_value")
@KeySequence("lims_test_result_value_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LimsTestResultValueDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long resultId;
    private Long requestId;
    private String requestNo;
    private Long sampleId;
    private String sampleNo;
    private Long taskId;
    private String taskNo;
    private String testItem;
    private String fieldCode;
    private String fieldName;
    private String fieldType;
    private String fieldValue;
    private String displayValue;
    private String unit;
    private String conclusion;
    private Integer sort;
    private String status;

}
