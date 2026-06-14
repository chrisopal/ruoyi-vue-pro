package cn.iocoder.yudao.module.lims.dal.dataobject.workflow;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lims_report")
@KeySequence("lims_report_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LimsReportDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long requestId;
    private String requestNo;
    private String reportNo;
    private String reportName;
    private Long templateId;
    private String templateVersion;
    private String reportContent;
    private String dataSnapshot;
    private String dataSnapshotHash;
    private String workflowSnapshotHash;
    private String conclusion;
    private String fileUrl;
    private String reportOutput;
    private String issuedTime;
    private String status;
    private String remark;

}
