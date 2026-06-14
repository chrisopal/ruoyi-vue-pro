package cn.iocoder.yudao.module.lab.dal.dataobject.packconfig;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lab_pack_workflow_node")
@KeySequence("lab_pack_workflow_node_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LabPackWorkflowNodeDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long domainPackId;
    private String nodeCode;
    private String nodeName;
    private String roleName;
    private Boolean requiredFlag;
    private Integer sort;
    private String status;
    private String remark;

}
