package cn.iocoder.yudao.module.lims.dal.mysql.workflow;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsWorkflowPageReqVO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestResultDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LimsTestResultMapper extends BaseMapperX<LimsTestResultDO> {

    default PageResult<LimsTestResultDO> selectPage(LimsWorkflowPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LimsTestResultDO>()
                .eqIfPresent(LimsTestResultDO::getRequestId, reqVO.getRequestId())
                .eqIfPresent(LimsTestResultDO::getSampleId, reqVO.getSampleId())
                .eqIfPresent(LimsTestResultDO::getTaskId, reqVO.getTaskId())
                .likeIfPresent(LimsTestResultDO::getTestItem, reqVO.getKeyword())
                .eqIfPresent(LimsTestResultDO::getStatus, reqVO.getStatus())
                .orderByDesc(LimsTestResultDO::getId));
    }

    default List<LimsTestResultDO> selectListByRequestId(Long requestId) {
        return selectList(new LambdaQueryWrapperX<LimsTestResultDO>()
                .eq(LimsTestResultDO::getRequestId, requestId)
                .orderByAsc(LimsTestResultDO::getId));
    }

    default List<LimsTestResultDO> selectListByTaskId(Long taskId) {
        return selectList(new LambdaQueryWrapperX<LimsTestResultDO>()
                .eq(LimsTestResultDO::getTaskId, taskId)
                .orderByAsc(LimsTestResultDO::getId));
    }

}
