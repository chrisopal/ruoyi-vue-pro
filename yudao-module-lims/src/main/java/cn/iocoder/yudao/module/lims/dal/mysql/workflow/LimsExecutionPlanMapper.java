package cn.iocoder.yudao.module.lims.dal.mysql.workflow;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsExecutionPlanDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LimsExecutionPlanMapper extends BaseMapperX<LimsExecutionPlanDO> {

    default LimsExecutionPlanDO selectByRequestId(Long requestId) {
        return selectOne(LimsExecutionPlanDO::getRequestId, requestId);
    }

}
