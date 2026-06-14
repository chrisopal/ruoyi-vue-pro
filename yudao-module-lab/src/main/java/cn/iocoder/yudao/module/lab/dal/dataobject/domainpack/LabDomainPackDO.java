package cn.iocoder.yudao.module.lab.dal.dataobject.domainpack;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lab_domain_pack")
@KeySequence("lab_domain_pack_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LabDomainPackDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long domainId;
    private String packCode;
    private String packName;
    private String packVersion;
    private String industry;
    private String applicationScope;
    private String workflowSchema;
    private String templateSchema;
    private String status;
    private String remark;

}
