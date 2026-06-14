package cn.iocoder.yudao.module.lab.dal.dataobject.packconfig;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lab_pack_report_section")
@KeySequence("lab_pack_report_section_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LabPackReportSectionDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long domainPackId;
    private String sectionCode;
    private String sectionName;
    private String sourceType;
    private Boolean visibleFlag;
    private Integer sort;
    private String status;
    private String remark;

}
