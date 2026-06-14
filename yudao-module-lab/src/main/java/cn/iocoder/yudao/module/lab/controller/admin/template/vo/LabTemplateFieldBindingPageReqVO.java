package cn.iocoder.yudao.module.lab.controller.admin.template.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LabTemplateFieldBindingPageReqVO extends PageParam {

    @NotNull(message = "模板编号不能为空")
    private Long templateId;

    private String fieldCode;
    private String fieldName;
    private String sourceType;

}
