package cn.iocoder.yudao.module.lab.dal.dataobject.audit;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lab_internal_audit")
@KeySequence("lab_internal_audit_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LabInternalAuditDO extends TenantBaseDO {

    private Long id;
    private String auditNo;
    private String auditName;
    private String auditScope;
    private String auditCriteria;
    private Long auditLeaderId;
    private String plannedStartDate;
    private String plannedEndDate;
    private String actualStartDate;
    private String actualEndDate;
    private String status;
    private String summary;
    private String reportFileUrl;

}
