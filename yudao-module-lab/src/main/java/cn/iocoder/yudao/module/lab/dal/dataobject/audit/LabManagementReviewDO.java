package cn.iocoder.yudao.module.lab.dal.dataobject.audit;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lab_management_review")
@KeySequence("lab_management_review_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LabManagementReviewDO extends TenantBaseDO {

    private Long id;
    private String reviewNo;
    private String reviewName;
    private String reviewDate;
    private Long hostUserId;
    private String participants;
    private String inputSummary;
    private String outputDecision;
    private String improvementActions;
    private String status;
    private String reportFileUrl;

}
