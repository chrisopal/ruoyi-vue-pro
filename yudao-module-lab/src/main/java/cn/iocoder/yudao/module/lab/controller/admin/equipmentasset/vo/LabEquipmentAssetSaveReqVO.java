package cn.iocoder.yudao.module.lab.controller.admin.equipmentasset.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Schema(description = "管理后台 - 设备主档创建/修改 Request VO")
@Data
public class LabEquipmentAssetSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "设备编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "ICP-MS-001")
    @NotBlank(message = "设备编码不能为空")
    @Size(max = 64, message = "设备编码长度不能超过 64 个字符")
    private String equipmentCode;

    @Schema(description = "设备名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "电感耦合等离子体质谱仪")
    @NotBlank(message = "设备名称不能为空")
    @Size(max = 128, message = "设备名称长度不能超过 128 个字符")
    private String equipmentName;

    @Schema(description = "设备类型", example = "instrument")
    @Size(max = 64, message = "设备类型长度不能超过 64 个字符")
    private String equipmentType;

    @Schema(description = "制造商", example = "Agilent")
    @Size(max = 128, message = "制造商长度不能超过 128 个字符")
    private String manufacturer;

    @Schema(description = "型号", example = "7900")
    @Size(max = 128, message = "型号长度不能超过 128 个字符")
    private String model;

    @Schema(description = "序列号", example = "SN-001")
    @Size(max = 128, message = "序列号长度不能超过 128 个字符")
    private String serialNo;

    @Schema(description = "实验区域", example = "食品理化实验室")
    @Size(max = 128, message = "实验区域长度不能超过 128 个字符")
    private String labArea;

    @Schema(description = "检测方向编码", example = "FOOD")
    @Size(max = 64, message = "检测方向编码长度不能超过 64 个字符")
    private String domainCode;

    @Schema(description = "能力范围", example = "lead,cadmium")
    @Size(max = 1024, message = "能力范围长度不能超过 1024 个字符")
    private String capabilityScope;

    @Schema(description = "责任人编号", example = "100")
    private Long responsibleUserId;

    @Schema(description = "校准有效期")
    private LocalDate calibrationValidUntil;

    @Schema(description = "状态", example = "enabled")
    @Size(max = 32, message = "状态长度不能超过 32 个字符")
    private String status;

    @Schema(description = "是否启用物联", example = "true")
    private Boolean iotEnabled;

    @Schema(description = "IoT 产品编号", example = "10")
    private Long iotProductId;

    @Schema(description = "IoT 设备编号", example = "iot-device-001")
    @Size(max = 128, message = "IoT 设备编号长度不能超过 128 个字符")
    private String iotDeviceId;

    @Schema(description = "数据来源类型", example = "manual")
    @Size(max = 32, message = "数据来源类型长度不能超过 32 个字符")
    private String dataSourceType;

    @Schema(description = "备注", example = "一期设备主档")
    @Size(max = 512, message = "备注长度不能超过 512 个字符")
    private String remark;

}
