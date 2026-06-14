package cn.iocoder.yudao.module.lab.controller.admin.template.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LabTemplateFieldBindingSaveReqVO {

    private Long id;

    @NotNull(message = "模板编号不能为空")
    private Long templateId;

    @NotBlank(message = "字段编码不能为空")
    @Size(max = 64, message = "字段编码长度不能超过 64 个字符")
    private String fieldCode;

    @NotBlank(message = "字段名称不能为空")
    @Size(max = 128, message = "字段名称长度不能超过 128 个字符")
    private String fieldName;

    @NotBlank(message = "来源类型不能为空")
    @Size(max = 64, message = "来源类型长度不能超过 64 个字符")
    private String sourceType;

    @NotBlank(message = "来源路径不能为空")
    @Size(max = 256, message = "来源路径长度不能超过 256 个字符")
    private String sourcePath;

    private Boolean requiredFlag;
    private Integer sort;

}
