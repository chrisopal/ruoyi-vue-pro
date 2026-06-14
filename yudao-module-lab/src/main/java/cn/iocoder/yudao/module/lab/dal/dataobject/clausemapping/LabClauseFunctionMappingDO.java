package cn.iocoder.yudao.module.lab.dal.dataobject.clausemapping;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lab_clause_function_mapping")
@KeySequence("lab_clause_function_mapping_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LabClauseFunctionMappingDO extends TenantBaseDO {

    private Long id;
    private Long clauseId;
    private String moduleCode;
    private String moduleName;
    private String functionCode;
    private String functionName;
    private String evidenceType;
    private String evidenceTable;
    private String evidenceDescription;
    private String requiredFlag;

}
