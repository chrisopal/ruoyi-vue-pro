package cn.iocoder.yudao.module.lab.dal.dataobject.template;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lab_template_field_binding")
@KeySequence("lab_template_field_binding_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LabTemplateFieldBindingDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long templateId;
    private String fieldCode;
    private String fieldName;
    private String sourceType;
    private String sourcePath;
    private Boolean requiredFlag;
    private Integer sort;

}
