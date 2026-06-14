package cn.iocoder.yudao.module.lab.controller.admin.equipmentasset.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 设备主档分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LabEquipmentAssetPageReqVO extends PageParam {

    @Schema(description = "设备编码", example = "ICP-MS-001")
    private String equipmentCode;

    @Schema(description = "设备名称", example = "电感耦合等离子体质谱仪")
    private String equipmentName;

    @Schema(description = "设备类型", example = "instrument")
    private String equipmentType;

    @Schema(description = "检测方向编码", example = "FOOD")
    private String domainCode;

    @Schema(description = "状态", example = "enabled")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
