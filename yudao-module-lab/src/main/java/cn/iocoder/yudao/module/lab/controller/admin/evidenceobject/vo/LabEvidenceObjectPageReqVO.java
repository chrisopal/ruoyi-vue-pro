package cn.iocoder.yudao.module.lab.controller.admin.evidenceobject.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 证据对象分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LabEvidenceObjectPageReqVO extends PageParam {

    @Schema(description = "证据编码", example = "EQ-CERT-001")
    private String evidenceCode;

    @Schema(description = "证据名称", example = "设备校准证书")
    private String evidenceName;

    @Schema(description = "证据类型", example = "EQUIPMENT_CERTIFICATE")
    private String evidenceType;

    @Schema(description = "来源对象", example = "lab_equipment_traceability")
    private String sourceObject;

    @Schema(description = "业务域", example = "equipment")
    private String businessDomain;

    @Schema(description = "状态", example = "effective")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
