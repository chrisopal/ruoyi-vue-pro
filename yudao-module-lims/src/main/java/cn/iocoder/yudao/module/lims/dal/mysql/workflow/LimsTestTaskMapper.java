package cn.iocoder.yudao.module.lims.dal.mysql.workflow;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsWorkflowPageReqVO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestTaskDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.util.StringUtils;

import java.util.List;

@Mapper
public interface LimsTestTaskMapper extends BaseMapperX<LimsTestTaskDO> {

    default PageResult<LimsTestTaskDO> selectPage(LimsWorkflowPageReqVO reqVO) {
        LambdaQueryWrapperX<LimsTestTaskDO> query = new LambdaQueryWrapperX<LimsTestTaskDO>()
                .eqIfPresent(LimsTestTaskDO::getRequestId, reqVO.getRequestId())
                .eqIfPresent(LimsTestTaskDO::getSampleId, reqVO.getSampleId())
                .eqIfPresent(LimsTestTaskDO::getAssignedUserId, reqVO.getAssignedUserId())
                .eqIfPresent(LimsTestTaskDO::getEquipmentId, reqVO.getEquipmentId())
                .eqIfPresent(LimsTestTaskDO::getStatus, reqVO.getStatus())
                .eqIfPresent(LimsTestTaskDO::getTaskStatus, reqVO.getTaskStatus())
                .eqIfPresent(LimsTestTaskDO::getScheduleStatus, reqVO.getScheduleStatus())
                .geIfPresent(LimsTestTaskDO::getPlannedStartTime, reqVO.getPlannedStartTimeBegin())
                .leIfPresent(LimsTestTaskDO::getPlannedStartTime, reqVO.getPlannedStartTimeEnd());
        if (StringUtils.hasText(reqVO.getKeyword())) {
            String keyword = reqVO.getKeyword();
            query.and(wrapper -> wrapper.like(LimsTestTaskDO::getTaskNo, keyword)
                    .or().like(LimsTestTaskDO::getTaskName, keyword)
                    .or().like(LimsTestTaskDO::getRequestNo, keyword)
                    .or().like(LimsTestTaskDO::getSampleNo, keyword)
                    .or().like(LimsTestTaskDO::getTestItem, keyword));
        }
        return selectPage(reqVO, query.orderByDesc(LimsTestTaskDO::getId));
    }

    default List<LimsTestTaskDO> selectListByRequestId(Long requestId) {
        return selectList(new LambdaQueryWrapperX<LimsTestTaskDO>()
                .eq(LimsTestTaskDO::getRequestId, requestId)
                .orderByAsc(LimsTestTaskDO::getId));
    }

}
