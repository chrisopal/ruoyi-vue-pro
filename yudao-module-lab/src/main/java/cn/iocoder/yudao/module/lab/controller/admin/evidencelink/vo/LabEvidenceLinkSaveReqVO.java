package cn.iocoder.yudao.module.lab.controller.admin.evidencelink.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - 证据关联创建/修改 Request VO")
@Data
public class LabEvidenceLinkSaveReqVO {

    private Long id;

    @NotBlank(message = "证据编码不能为空")
    @Size(max = 64, message = "证据编码长度不能超过 64 个字符")
    private String evidenceCode;

    @Size(max = 256, message = "证据名称长度不能超过 256 个字符")
    private String evidenceName;

    @Size(max = 512, message = "证据文件地址长度不能超过 512 个字符")
    private String evidenceUrl;

    @Size(max = 128, message = "证据哈希长度不能超过 128 个字符")
    private String evidenceHash;

    @NotBlank(message = "证据来源对象不能为空")
    @Size(max = 128, message = "证据来源对象长度不能超过 128 个字符")
    private String sourceObject;

    private Long sourceObjectId;

    @Size(max = 128, message = "证据来源对象单号长度不能超过 128 个字符")
    private String sourceObjectNo;

    @NotBlank(message = "关联业务类型不能为空")
    @Size(max = 128, message = "关联业务类型长度不能超过 128 个字符")
    private String linkedBizType;

    private Long linkedBizId;

    @Size(max = 128, message = "关联业务单号长度不能超过 128 个字符")
    private String linkedBizNo;

    @NotBlank(message = "条款业务分类不能为空")
    @Size(max = 64, message = "条款业务分类长度不能超过 64 个字符")
    private String clauseCategory;

    @NotBlank(message = "关联状态不能为空")
    @Size(max = 32, message = "关联状态长度不能超过 32 个字符")
    private String linkStatus;

    @Size(max = 512, message = "备注长度不能超过 512 个字符")
    private String remark;

}
