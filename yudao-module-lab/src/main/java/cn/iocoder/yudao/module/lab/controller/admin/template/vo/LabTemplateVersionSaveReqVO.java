package cn.iocoder.yudao.module.lab.controller.admin.template.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LabTemplateVersionSaveReqVO {

    private Long id;

    @NotNull(message = "检测方案包不能为空")
    private Long domainPackId;

    @NotBlank(message = "模板编码不能为空")
    @Size(max = 64, message = "模板编码长度不能超过 64 个字符")
    private String templateCode;

    @NotBlank(message = "模板名称不能为空")
    @Size(max = 128, message = "模板名称长度不能超过 128 个字符")
    private String templateName;

    @NotBlank(message = "模板版本不能为空")
    @Size(max = 32, message = "模板版本长度不能超过 32 个字符")
    private String templateVersion;

    @NotBlank(message = "模板类型不能为空")
    @Size(max = 64, message = "模板类型长度不能超过 64 个字符")
    private String templateType;

    @Size(max = 32, message = "模板发布状态长度不能超过 32 个字符")
    private String templateStatus;

    private String sectionSchema;

    private String outputFormats;

    private String dataSourceSchema;

    private String previewSchema;

    @Size(max = 32, message = "状态长度不能超过 32 个字符")
    private String status;

}
