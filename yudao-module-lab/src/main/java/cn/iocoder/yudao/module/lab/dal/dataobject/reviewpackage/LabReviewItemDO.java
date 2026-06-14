package cn.iocoder.yudao.module.lab.dal.dataobject.reviewpackage;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lab_review_item")
@KeySequence("lab_review_item_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LabReviewItemDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long batchId;
    private String itemType;
    private String standardName;
    private String clauseCode;
    private String clauseCategory;
    private String checkPoint;
    private String expectedEvidence;
    private String ownerRole;
    private String evidenceName;
    private String sourceObject;
    private String linkedObjectNo;
    private String linkStatus;
    private String ncNo;
    private String severity;
    private String description;
    private String owner;
    private String dueDate;
    private String capaNo;
    private String rootCause;
    private String action;
    private String status;
    private Integer sort;
    private String remark;

}
