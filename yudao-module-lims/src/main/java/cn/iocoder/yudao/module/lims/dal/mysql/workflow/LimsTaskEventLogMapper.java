package cn.iocoder.yudao.module.lims.dal.mysql.workflow;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskEventLogDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LimsTaskEventLogMapper extends BaseMapperX<LimsTaskEventLogDO> {

    default List<LimsTaskEventLogDO> selectListByTaskId(Long taskId) {
        return selectList(new LambdaQueryWrapperX<LimsTaskEventLogDO>()
                .eq(LimsTaskEventLogDO::getTaskId, taskId)
                .orderByAsc(LimsTaskEventLogDO::getId));
    }

}
