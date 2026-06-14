package cn.iocoder.yudao.module.lab.controller.admin.evidencelink.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 证据关联分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LabEvidenceLinkPageReqVO extends PageParam {

    @Schema(description = "证据编码", example = "REPORT")
    private String evidenceCode;

    @Schema(description = "证据来源对象", example = "lims_report")
    private String sourceObject;

    @Schema(description = "证据来源对象单号", example = "RPT-2026-MVP-001")
    private String sourceObjectNo;

    @Schema(description = "关联业务类型", example = "review_package")
    private String linkedBizType;

    @Schema(description = "关联业务单号", example = "RP-MVP-001")
    private String linkedBizNo;

    @Schema(description = "条款业务分类", example = "report")
    private String clauseCategory;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
