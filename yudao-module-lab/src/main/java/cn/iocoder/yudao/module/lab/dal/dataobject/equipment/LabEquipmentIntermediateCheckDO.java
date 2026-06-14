package cn.iocoder.yudao.module.lab.dal.dataobject.equipment;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lab_equipment_intermediate_check")
@KeySequence("lab_equipment_intermediate_check_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LabEquipmentIntermediateCheckDO extends TenantBaseDO {

    private Long id;
    private Long equipmentId;
    private String checkNo;
    private String checkDate;
    private String checkMethod;
    private String checkResult;
    private String checkRecord;
    private Long checkerId;
    private Long reviewerId;
    private String fileUrl;
    private String status;

}
