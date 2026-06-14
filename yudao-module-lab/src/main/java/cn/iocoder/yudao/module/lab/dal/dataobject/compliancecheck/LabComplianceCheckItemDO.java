package cn.iocoder.yudao.module.lab.dal.dataobject.compliancecheck;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lab_compliance_check_item")
@KeySequence("lab_compliance_check_item_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LabComplianceCheckItemDO extends TenantBaseDO {

    private Long id;
    private Long checkId;
    private Long clauseId;
    private String checkResult;
    private String evidenceSummary;
    private String evidenceStatus;
    private String findingDescription;
    private String severity;
    private Long responsibleDeptId;
    private Long responsibleUserId;
    private String dueDate;
    private Long nonconformityId;

}
