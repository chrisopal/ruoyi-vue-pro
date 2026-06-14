package cn.iocoder.yudao.module.lab.dal.dataobject.environment;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lab_environment_record")
@KeySequence("lab_environment_record_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LabEnvironmentRecordDO extends TenantBaseDO {

    private Long id;
    private Long areaId;
    private String areaName;
    private String recordTime;
    private String temperature;
    private String humidity;
    private String pressure;
    private String cleanliness;
    private String otherParams;
    private String dataSource;
    private Long recorderId;
    private String status;
    private String abnormalDescription;

}
