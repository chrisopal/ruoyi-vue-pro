package cn.iocoder.yudao.module.lims.dal.mysql.workflow;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsWorkflowPageReqVO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsReportDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LimsReportMapper extends BaseMapperX<LimsReportDO> {

    default PageResult<LimsReportDO> selectPage(LimsWorkflowPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LimsReportDO>()
                .eqIfPresent(LimsReportDO::getRequestId, reqVO.getRequestId())
                .likeIfPresent(LimsReportDO::getReportName, reqVO.getKeyword())
                .eqIfPresent(LimsReportDO::getStatus, reqVO.getStatus())
                .orderByDesc(LimsReportDO::getId));
    }

    default LimsReportDO selectByRequestId(Long requestId) {
        return selectOne(LimsReportDO::getRequestId, requestId);
    }

}
