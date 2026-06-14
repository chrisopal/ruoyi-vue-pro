package cn.iocoder.yudao.module.lab.controller.admin.evidenceobject.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Schema(description = "管理后台 - 证据对象创建/修改 Request VO")
@Data
public class LabEvidenceObjectSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "证据编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "EQ-CERT-001")
    @NotBlank(message = "证据编码不能为空")
    @Size(max = 64, message = "证据编码长度不能超过 64 个字符")
    private String evidenceCode;

    @Schema(description = "证据名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "设备校准证书")
    @NotBlank(message = "证据名称不能为空")
    @Size(max = 256, message = "证据名称长度不能超过 256 个字符")
    private String evidenceName;

    @Schema(description = "证据类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "EQUIPMENT_CERTIFICATE")
    @NotBlank(message = "证据类型不能为空")
    @Size(max = 64, message = "证据类型长度不能超过 64 个字符")
    private String evidenceType;

    @Schema(description = "来源对象", requiredMode = Schema.RequiredMode.REQUIRED, example = "lab_equipment_traceability")
    @NotBlank(message = "来源对象不能为空")
    @Size(max = 128, message = "来源对象长度不能超过 128 个字符")
    private String sourceObject;

    @Schema(description = "来源对象编号", example = "100")
    private Long sourceObjectId;

    @Schema(description = "来源对象单号", example = "CERT-001")
    @Size(max = 128, message = "来源对象单号长度不能超过 128 个字符")
    private String sourceObjectNo;

    @Schema(description = "业务域", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotBlank(message = "业务域不能为空")
    @Size(max = 64, message = "业务域长度不能超过 64 个字符")
    private String businessDomain;

    @Schema(description = "文件地址", example = "https://example.test/cert.pdf")
    @Size(max = 512, message = "文件地址长度不能超过 512 个字符")
    private String fileUrl;

    @Schema(description = "文件名", example = "cert.pdf")
    @Size(max = 256, message = "文件名长度不能超过 256 个字符")
    private String fileName;

    @Schema(description = "文件格式", example = "pdf")
    @Size(max = 32, message = "文件格式长度不能超过 32 个字符")
    private String fileFormat;

    @Schema(description = "证据哈希")
    @Size(max = 128, message = "证据哈希长度不能超过 128 个字符")
    private String evidenceHash;

    @Schema(description = "签发机构", example = "省计量院")
    @Size(max = 128, message = "签发机构长度不能超过 128 个字符")
    private String issuedBy;

    @Schema(description = "签发日期")
    private LocalDate issuedAt;

    @Schema(description = "有效期开始")
    private LocalDate validFrom;

    @Schema(description = "有效期结束")
    private LocalDate validTo;

    @Schema(description = "状态", example = "effective")
    @Size(max = 32, message = "状态长度不能超过 32 个字符")
    private String status;

    @Schema(description = "摘要")
    @Size(max = 1024, message = "摘要长度不能超过 1024 个字符")
    private String summary;

    @Schema(description = "备注")
    @Size(max = 512, message = "备注长度不能超过 512 个字符")
    private String remark;

}
