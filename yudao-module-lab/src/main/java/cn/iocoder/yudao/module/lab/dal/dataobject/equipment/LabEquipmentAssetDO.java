package cn.iocoder.yudao.module.lab.dal.dataobject.equipment;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 设备主档 DO。
 */
@TableName("lab_equipment_asset")
@KeySequence("lab_equipment_asset_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LabEquipmentAssetDO extends TenantBaseDO {

    @TableId
    private Long id;
    private String equipmentCode;
    private String equipmentName;
    private String equipmentType;
    private String manufacturer;
    private String model;
    private String serialNo;
    private String labArea;
    private String domainCode;
    private String capabilityScope;
    private Long responsibleUserId;
    private LocalDate calibrationValidUntil;
    private String status;
    private Boolean iotEnabled;
    private Long iotProductId;
    private String iotDeviceId;
    private String dataSourceType;
    private String remark;

}
