package cn.iocoder.yudao.module.lims.dal.dataobject.workflow;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lims_test_request")
@KeySequence("lims_test_request_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LimsTestRequestDO extends TenantBaseDO {

    @TableId
    private Long id;
    private String requestNo;
    private String requestName;
    private String requestType;
    private String requestSourceType;
    private String customerName;
    private String requesterName;
    private Long requesterOrgId;
    private Long costCenterId;
    private String commercialOrderId;
    private String internalProjectNo;
    private String domainCode;
    private Long domainPackId;
    private String domainPackCode;
    private String domainPackVersion;
    private Long standardId;
    private String priority;
    private String dueDate;
    private String status;
    private String scenarioConfig;
    private String workflowSnapshot;
    private String workflowSnapshotHash;
    private String workflowSnapshotTime;
    private String remark;

}
