package cn.iocoder.yudao.module.lab.dal.dataobject.domain;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 实验室检测方向包 DO。
 */
@TableName("lab_domain_profile")
@KeySequence("lab_domain_profile_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LabDomainProfileDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 方向编码
     */
    private String domainCode;

    /**
     * 方向名称
     */
    private String domainName;

    /**
     * 方向说明
     */
    private String description;

    /**
     * 状态
     */
    private String status;

}
