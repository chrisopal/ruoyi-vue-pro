package cn.iocoder.yudao.module.lab.dal.dataobject.nonconformity;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lab_corrective_action")
@KeySequence("lab_corrective_action_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LabCorrectiveActionDO extends TenantBaseDO {

    private Long id;
    private String actionNo;
    private Long nonconformityId;
    private String rootCause;
    private String correction;
    private String correctiveAction;
    private String preventiveAction;
    private Long responsibleUserId;
    private String plannedFinishDate;
    private String actualFinishDate;
    private String verificationResult;
    private Long verifierId;
    private String verifiedTime;
    private String status;

}
