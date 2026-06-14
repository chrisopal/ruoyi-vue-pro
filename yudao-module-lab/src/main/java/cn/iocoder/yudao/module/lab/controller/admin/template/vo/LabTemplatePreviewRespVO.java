package cn.iocoder.yudao.module.lab.controller.admin.template.vo;

import lombok.Data;

import java.util.List;

@Data
public class LabTemplatePreviewRespVO {

    private LabTemplateVersionRespVO template;
    private String sectionSchema;
    private String outputFormats;
    private String dataSourceSchema;
    private List<LabTemplateFieldBindingRespVO> fields;

}
