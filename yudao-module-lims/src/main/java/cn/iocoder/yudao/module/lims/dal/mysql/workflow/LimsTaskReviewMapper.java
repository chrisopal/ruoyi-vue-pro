package cn.iocoder.yudao.module.lims.dal.mysql.workflow;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskReviewDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LimsTaskReviewMapper extends BaseMapperX<LimsTaskReviewDO> {

    default List<LimsTaskReviewDO> selectListByTaskId(Long taskId) {
        return selectList(new LambdaQueryWrapperX<LimsTaskReviewDO>()
                .eq(LimsTaskReviewDO::getTaskId, taskId)
                .orderByDesc(LimsTaskReviewDO::getId));
    }

}
