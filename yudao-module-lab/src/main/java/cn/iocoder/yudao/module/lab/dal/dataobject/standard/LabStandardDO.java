package cn.iocoder.yudao.module.lab.dal.dataobject.standard;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lab_standard")
@KeySequence("lab_standard_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LabStandardDO extends TenantBaseDO {

    @TableId
    private Long id;
    private String standardCode;
    private String standardName;
    private String standardVersion;
    private String standardType;
    private String status;
    private String remark;

}
