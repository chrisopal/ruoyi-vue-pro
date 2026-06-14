package cn.iocoder.yudao.module.lims.dal.mysql.workflow;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskQcRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LimsTaskQcRecordMapper extends BaseMapperX<LimsTaskQcRecordDO> {

    default List<LimsTaskQcRecordDO> selectListByTaskId(Long taskId) {
        return selectList(new LambdaQueryWrapperX<LimsTaskQcRecordDO>()
                .eq(LimsTaskQcRecordDO::getTaskId, taskId)
                .orderByDesc(LimsTaskQcRecordDO::getId));
    }

}
