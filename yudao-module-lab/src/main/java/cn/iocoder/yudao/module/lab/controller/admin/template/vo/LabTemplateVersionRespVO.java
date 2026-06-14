package cn.iocoder.yudao.module.lab.controller.admin.template.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LabTemplateVersionRespVO {

    private Long id;
    private Long domainPackId;
    private String templateCode;
    private String templateName;
    private String templateVersion;
    private String templateType;
    private String previewSchema;
    private String status;
    private LocalDateTime createTime;

}
