package cn.iocoder.yudao.module.lab.dal.dataobject.packconfig;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lab_pack_evidence_requirement")
@KeySequence("lab_pack_evidence_requirement_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LabPackEvidenceRequirementDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long domainPackId;
    private String requirementCode;
    private String requirementName;
    private String evidenceType;
    private String sourceType;
    private String clauseCategory;
    private Boolean requiredFlag;
    private Integer sort;
    private String status;
    private String remark;

}
