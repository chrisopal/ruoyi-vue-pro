package cn.iocoder.yudao.module.lab.dal.dataobject.personnel;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lab_personnel_authorization")
@KeySequence("lab_personnel_authorization_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LabPersonnelAuthorizationDO extends TenantBaseDO {

    private Long id;
    private Long userId;
    private String authType;
    private String authScope;
    private Long methodId;
    private Long equipmentId;
    private Long authorizedBy;
    private String authorizedTime;
    private String validFrom;
    private String validTo;
    private String status;
    private String fileUrl;
    private String remark;

}
