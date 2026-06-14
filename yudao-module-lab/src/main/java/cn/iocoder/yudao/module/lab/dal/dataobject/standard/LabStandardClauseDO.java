package cn.iocoder.yudao.module.lab.dal.dataobject.standard;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lab_standard_clause")
@KeySequence("lab_standard_clause_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LabStandardClauseDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long standardId;
    private String clauseCode;
    private String clauseTitle;
    private String clauseCategory;
    private String requirementText;
    private String evidenceTypeCodes;
    private String status;

}
