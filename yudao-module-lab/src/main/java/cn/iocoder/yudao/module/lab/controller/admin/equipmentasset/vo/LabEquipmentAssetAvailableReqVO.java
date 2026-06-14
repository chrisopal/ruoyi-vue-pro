package cn.iocoder.yudao.module.lab.controller.admin.equipmentasset.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 可用设备查询 Request VO")
@Data
public class LabEquipmentAssetAvailableReqVO {

    @Schema(description = "检测方向编码", example = "FOOD")
    private String domainCode;

    @Schema(description = "检测项目", example = "lead")
    private String testItem;

}
