package cn.iocoder.yudao.module.lims.dal.mysql.workflow;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsWorkflowPageReqVO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsSampleDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LimsSampleMapper extends BaseMapperX<LimsSampleDO> {

    default PageResult<LimsSampleDO> selectPage(LimsWorkflowPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LimsSampleDO>()
                .eqIfPresent(LimsSampleDO::getRequestId, reqVO.getRequestId())
                .likeIfPresent(LimsSampleDO::getSampleName, reqVO.getKeyword())
                .eqIfPresent(LimsSampleDO::getStatus, reqVO.getStatus())
                .orderByDesc(LimsSampleDO::getId));
    }

    default List<LimsSampleDO> selectListByRequestId(Long requestId) {
        return selectList(new LambdaQueryWrapperX<LimsSampleDO>()
                .eq(LimsSampleDO::getRequestId, requestId)
                .orderByAsc(LimsSampleDO::getId));
    }

}
