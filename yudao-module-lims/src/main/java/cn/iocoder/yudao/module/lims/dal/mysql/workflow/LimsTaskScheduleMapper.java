package cn.iocoder.yudao.module.lims.dal.mysql.workflow;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsWorkflowPageReqVO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskScheduleDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LimsTaskScheduleMapper extends BaseMapperX<LimsTaskScheduleDO> {

    default PageResult<LimsTaskScheduleDO> selectPage(LimsWorkflowPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LimsTaskScheduleDO>()
                .eqIfPresent(LimsTaskScheduleDO::getTaskId, reqVO.getTaskId())
                .eqIfPresent(LimsTaskScheduleDO::getAssignedUserId, reqVO.getAssignedUserId())
                .eqIfPresent(LimsTaskScheduleDO::getEquipmentId, reqVO.getEquipmentId())
                .eqIfPresent(LimsTaskScheduleDO::getScheduleStatus, reqVO.getScheduleStatus())
                .orderByAsc(LimsTaskScheduleDO::getPlannedStartTime));
    }

    default List<LimsTaskScheduleDO> selectActiveByEquipmentId(Long equipmentId) {
        return selectList(new LambdaQueryWrapperX<LimsTaskScheduleDO>()
                .eq(LimsTaskScheduleDO::getEquipmentId, equipmentId)
                .in(LimsTaskScheduleDO::getScheduleStatus, List.of("scheduled", "locked")));
    }

    default List<LimsTaskScheduleDO> selectActiveByAssignedUserId(Long assignedUserId) {
        return selectList(new LambdaQueryWrapperX<LimsTaskScheduleDO>()
                .eq(LimsTaskScheduleDO::getAssignedUserId, assignedUserId)
                .in(LimsTaskScheduleDO::getScheduleStatus, List.of("scheduled", "locked")));
    }

}
