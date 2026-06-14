package cn.iocoder.yudao.module.lab.controller.admin.template.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 模板版本分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LabTemplateVersionPageReqVO extends PageParam {

    private Long domainPackId;
    private String templateCode;
    private String templateName;
    private String templateType;
    private String status;

}
