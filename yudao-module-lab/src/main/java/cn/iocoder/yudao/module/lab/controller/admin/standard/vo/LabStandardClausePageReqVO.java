package cn.iocoder.yudao.module.lab.controller.admin.standard.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 实验室标准条款分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LabStandardClausePageReqVO extends PageParam {

    @Schema(description = "标准编号", example = "1")
    private Long standardId;

    @Schema(description = "条款编号", example = "PERSONNEL")
    private String clauseCode;

    @Schema(description = "条款标题", example = "人员能力与授权")
    private String clauseTitle;

    @Schema(description = "条款业务分类", example = "personnel")
    private String clauseCategory;

    @Schema(description = "状态", example = "active")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
