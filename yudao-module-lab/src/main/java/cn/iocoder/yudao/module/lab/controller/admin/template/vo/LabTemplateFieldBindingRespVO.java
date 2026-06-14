package cn.iocoder.yudao.module.lab.controller.admin.template.vo;

import lombok.Data;

@Data
public class LabTemplateFieldBindingRespVO {

    private Long id;
    private Long templateId;
    private String fieldCode;
    private String fieldName;
    private String sourceType;
    private String sourcePath;
    private Boolean requiredFlag;
    private Integer sort;

}
