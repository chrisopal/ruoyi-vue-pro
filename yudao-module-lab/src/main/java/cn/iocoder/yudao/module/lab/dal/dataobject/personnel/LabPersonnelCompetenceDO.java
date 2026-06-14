package cn.iocoder.yudao.module.lab.dal.dataobject.personnel;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lab_personnel_competence")
@KeySequence("lab_personnel_competence_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LabPersonnelCompetenceDO extends TenantBaseDO {

    private Long id;
    private Long userId;
    private String userName;
    private String competenceType;
    private String competenceItem;
    private Long relatedMethodId;
    private Long relatedEquipmentId;
    private String certificateNo;
    private String certificateFileUrl;
    private String validFrom;
    private String validTo;
    private String assessmentResult;
    private String status;
    private String remark;

}
