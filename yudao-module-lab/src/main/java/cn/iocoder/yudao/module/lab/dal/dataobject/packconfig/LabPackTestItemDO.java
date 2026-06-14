package cn.iocoder.yudao.module.lab.dal.dataobject.packconfig;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lab_pack_test_item")
@KeySequence("lab_pack_test_item_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LabPackTestItemDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long domainPackId;
    private String itemCode;
    private String itemName;
    private String methodCode;
    private String methodName;
    private String standardCode;
    private String resultUnit;
    private String demoValue;
    private Integer sort;
    private String status;
    private String remark;

}
