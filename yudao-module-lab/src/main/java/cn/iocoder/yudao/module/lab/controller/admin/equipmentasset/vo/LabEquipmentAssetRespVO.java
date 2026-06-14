package cn.iocoder.yudao.module.lab.controller.admin.equipmentasset.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 设备主档 Response VO")
@Data
public class LabEquipmentAssetRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "设备编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "ICP-MS-001")
    private String equipmentCode;

    @Schema(description = "设备名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "电感耦合等离子体质谱仪")
    private String equipmentName;

    @Schema(description = "设备类型", example = "instrument")
    private String equipmentType;

    @Schema(description = "制造商", example = "Agilent")
    private String manufacturer;

    @Schema(description = "型号", example = "7900")
    private String model;

    @Schema(description = "序列号", example = "SN-001")
    private String serialNo;

    @Schema(description = "实验区域", example = "食品理化实验室")
    private String labArea;

    @Schema(description = "检测方向编码", example = "FOOD")
    private String domainCode;

    @Schema(description = "能力范围", example = "lead,cadmium")
    private String capabilityScope;

    @Schema(description = "责任人编号", example = "100")
    private Long responsibleUserId;

    @Schema(description = "校准有效期")
    private LocalDate calibrationValidUntil;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "enabled")
    private String status;

    @Schema(description = "是否启用物联", example = "true")
    private Boolean iotEnabled;

    @Schema(description = "IoT 产品编号", example = "10")
    private Long iotProductId;

    @Schema(description = "IoT 设备编号", example = "iot-device-001")
    private String iotDeviceId;

    @Schema(description = "数据来源类型", example = "manual")
    private String dataSourceType;

    @Schema(description = "备注", example = "一期设备主档")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
