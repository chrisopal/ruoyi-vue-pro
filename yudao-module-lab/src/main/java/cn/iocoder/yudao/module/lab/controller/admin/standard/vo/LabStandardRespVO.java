package cn.iocoder.yudao.module.lab.controller.admin.standard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 实验室标准 Response VO")
@Data
public class LabStandardRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "标准编码", example = "ISO_IEC_17025")
    private String standardCode;

    @Schema(description = "标准名称", example = "ISO/IEC 17025")
    private String standardName;

    @Schema(description = "标准版本", example = "2017")
    private String standardVersion;

    @Schema(description = "标准类型", example = "accreditation")
    private String standardType;

    @Schema(description = "状态", example = "active")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
