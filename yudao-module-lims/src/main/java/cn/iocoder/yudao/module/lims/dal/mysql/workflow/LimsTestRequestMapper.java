package cn.iocoder.yudao.module.lims.dal.mysql.workflow;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsWorkflowPageReqVO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestRequestDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LimsTestRequestMapper extends BaseMapperX<LimsTestRequestDO> {

    default LimsTestRequestDO selectByRequestNo(String requestNo) {
        return selectOne(LimsTestRequestDO::getRequestNo, requestNo);
    }

    default PageResult<LimsTestRequestDO> selectPage(LimsWorkflowPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LimsTestRequestDO>()
                .likeIfPresent(LimsTestRequestDO::getRequestNo, reqVO.getRequestNo())
                .likeIfPresent(LimsTestRequestDO::getRequestName, reqVO.getKeyword())
                .eqIfPresent(LimsTestRequestDO::getDomainPackId, reqVO.getDomainPackId())
                .eqIfPresent(LimsTestRequestDO::getStatus, reqVO.getStatus())
                .orderByDesc(LimsTestRequestDO::getId));
    }

}
