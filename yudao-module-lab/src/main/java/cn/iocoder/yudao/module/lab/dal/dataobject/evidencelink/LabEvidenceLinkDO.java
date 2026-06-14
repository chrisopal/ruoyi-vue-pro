package cn.iocoder.yudao.module.lab.dal.dataobject.evidencelink;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@TableName("lab_evidence_link")
@KeySequence("lab_evidence_link_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LabEvidenceLinkDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long evidenceObjectId;
    private String evidenceCode;
    private String evidenceName;
    private String evidenceUrl;
    private String evidenceHash;
    private String sourceObject;
    private Long sourceObjectId;
    private String sourceObjectNo;
    private String linkedBizType;
    private Long linkedBizId;
    private String linkedBizNo;
    private Long clauseId;
    private Long capabilityScopeId;
    private String clauseCategory;
    private String linkStatus;
    private String linkReason;
    private Long verifiedBy;
    private LocalDateTime verifiedAt;
    private String remark;

}
