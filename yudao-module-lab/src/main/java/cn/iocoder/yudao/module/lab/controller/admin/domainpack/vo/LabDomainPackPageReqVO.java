package cn.iocoder.yudao.module.lab.controller.admin.domainpack.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 检测方案包分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LabDomainPackPageReqVO extends PageParam {

    @Schema(description = "检测领域编号", example = "1")
    private Long domainId;

    @Schema(description = "方案包编码", example = "FOOD_ROUTINE")
    private String packCode;

    @Schema(description = "方案包名称", example = "食品常规检测方案")
    private String packName;

    @Schema(description = "行业方向", example = "食品")
    private String industry;

    @Schema(description = "状态", example = "active")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
