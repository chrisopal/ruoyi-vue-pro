package cn.iocoder.yudao.module.lab.dal.dataobject.evidenceobject;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@TableName("lab_evidence_object")
@KeySequence("lab_evidence_object_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LabEvidenceObjectDO extends TenantBaseDO {

    @TableId
    private Long id;
    private String evidenceCode;
    private String evidenceName;
    private String evidenceType;
    private String sourceObject;
    private Long sourceObjectId;
    private String sourceObjectNo;
    private String businessDomain;
    private String fileUrl;
    private String fileName;
    private String fileFormat;
    private String evidenceHash;
    private String issuedBy;
    private LocalDate issuedAt;
    private LocalDate validFrom;
    private LocalDate validTo;
    private String status;
    private String summary;
    private String remark;

}
