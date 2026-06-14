package cn.iocoder.yudao.module.lab.dal.dataobject.method;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lab_method_validation")
@KeySequence("lab_method_validation_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LabMethodValidationDO extends TenantBaseDO {

    private Long id;
    private Long methodId;
    private String validationNo;
    private String validationType;
    private String validationDate;
    private String validationItems;
    private String conclusion;
    private Long responsibleUserId;
    private Long reviewerId;
    private String reportFileUrl;
    private String status;
    private String remark;

}
