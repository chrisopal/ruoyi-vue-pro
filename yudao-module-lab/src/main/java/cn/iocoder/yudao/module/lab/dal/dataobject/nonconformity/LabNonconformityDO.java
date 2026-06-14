package cn.iocoder.yudao.module.lab.dal.dataobject.nonconformity;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lab_nonconformity")
@KeySequence("lab_nonconformity_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LabNonconformityDO extends TenantBaseDO {

    private Long id;
    private String ncNo;
    private String sourceType;
    private Long sourceId;
    private Long clauseId;
    private String title;
    private String description;
    private String severity;
    private Long responsibleDeptId;
    private Long responsibleUserId;
    private String discoveredDate;
    private String dueDate;
    private String status;

}
