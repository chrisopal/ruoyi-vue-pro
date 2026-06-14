package cn.iocoder.yudao.module.lab.dal.dataobject.compliancecheck;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lab_compliance_check")
@KeySequence("lab_compliance_check_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LabComplianceCheckDO extends TenantBaseDO {

    private Long id;
    private String checkNo;
    private String checkName;
    private String checkType;
    private Long standardId;
    private String checkScope;
    private String startDate;
    private String endDate;
    private Long responsibleUserId;
    private String status;
    private String summary;

}
