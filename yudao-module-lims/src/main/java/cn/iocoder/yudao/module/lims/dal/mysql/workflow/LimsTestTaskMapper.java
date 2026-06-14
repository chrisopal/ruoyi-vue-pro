package cn.iocoder.yudao.module.lims.dal.mysql.workflow;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsWorkflowPageReqVO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestTaskDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LimsTestTaskMapper extends BaseMapperX<LimsTestTaskDO> {

    default PageResult<LimsTestTaskDO> selectPage(LimsWorkflowPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LimsTestTaskDO>()
                .eqIfPresent(LimsTestTaskDO::getRequestId, reqVO.getRequestId())
                .eqIfPresent(LimsTestTaskDO::getSampleId, reqVO.getSampleId())
                .likeIfPresent(LimsTestTaskDO::getTaskName, reqVO.getKeyword())
                .eqIfPresent(LimsTestTaskDO::getStatus, reqVO.getStatus())
                .orderByDesc(LimsTestTaskDO::getId));
    }

    default List<LimsTestTaskDO> selectListByRequestId(Long requestId) {
        return selectList(new LambdaQueryWrapperX<LimsTestTaskDO>()
                .eq(LimsTestTaskDO::getRequestId, requestId)
                .orderByAsc(LimsTestTaskDO::getId));
    }

}
