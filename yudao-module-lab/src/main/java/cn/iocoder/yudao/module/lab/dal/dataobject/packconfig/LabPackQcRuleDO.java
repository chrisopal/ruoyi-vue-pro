package cn.iocoder.yudao.module.lab.dal.dataobject.packconfig;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lab_pack_qc_rule")
@KeySequence("lab_pack_qc_rule_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LabPackQcRuleDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long domainPackId;
    private String ruleCode;
    private String ruleName;
    private String ruleType;
    private String ruleExpression;
    private String acceptanceCriteria;
    private Integer sort;
    private String status;
    private String remark;

}
