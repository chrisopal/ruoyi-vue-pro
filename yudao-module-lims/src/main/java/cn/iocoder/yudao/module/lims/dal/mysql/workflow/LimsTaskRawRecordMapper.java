package cn.iocoder.yudao.module.lims.dal.mysql.workflow;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskRawRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LimsTaskRawRecordMapper extends BaseMapperX<LimsTaskRawRecordDO> {

    default List<LimsTaskRawRecordDO> selectListByTaskId(Long taskId) {
        return selectList(new LambdaQueryWrapperX<LimsTaskRawRecordDO>()
                .eq(LimsTaskRawRecordDO::getTaskId, taskId)
                .orderByDesc(LimsTaskRawRecordDO::getVersionNo)
                .orderByDesc(LimsTaskRawRecordDO::getId));
    }

}
