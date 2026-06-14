package cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 评审批次分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LabReviewBatchPageReqVO extends PageParam {

    @Schema(description = "批次编码", example = "RP-MVP-001")
    private String batchCode;

    @Schema(description = "批次名称", example = "MVP 评审材料包")
    private String batchName;

    @Schema(description = "评审类型", example = "accreditation")
    private String reviewType;

    @Schema(description = "状态", example = "active")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
