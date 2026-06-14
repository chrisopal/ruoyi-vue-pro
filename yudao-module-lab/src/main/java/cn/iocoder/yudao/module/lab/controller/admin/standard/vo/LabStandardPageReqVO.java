package cn.iocoder.yudao.module.lab.controller.admin.standard.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 实验室标准分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LabStandardPageReqVO extends PageParam {

    @Schema(description = "标准编码", example = "ISO_IEC_17025")
    private String standardCode;

    @Schema(description = "标准名称", example = "ISO/IEC 17025")
    private String standardName;

    @Schema(description = "标准类型", example = "accreditation")
    private String standardType;

    @Schema(description = "状态", example = "active")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
