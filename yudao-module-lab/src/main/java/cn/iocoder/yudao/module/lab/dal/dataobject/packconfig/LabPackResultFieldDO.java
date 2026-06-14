package cn.iocoder.yudao.module.lab.dal.dataobject.packconfig;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lab_pack_result_field")
@KeySequence("lab_pack_result_field_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LabPackResultFieldDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long domainPackId;
    private String itemCode;
    private String fieldCode;
    private String fieldName;
    private String fieldType;
    private String unit;
    private Boolean requiredFlag;
    private String minValue;
    private String maxValue;
    private String enumOptions;
    private String demoValue;
    private Integer sort;
    private String status;
    private String remark;

}
