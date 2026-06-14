package cn.iocoder.yudao.module.lab.dal.mysql.reviewpackage;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo.LabReviewBatchPageReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.reviewpackage.LabReviewBatchDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LabReviewBatchMapper extends BaseMapperX<LabReviewBatchDO> {

    default LabReviewBatchDO selectByBatchCode(String batchCode) {
        return selectOne(LabReviewBatchDO::getBatchCode, batchCode);
    }

    default PageResult<LabReviewBatchDO> selectPage(LabReviewBatchPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LabReviewBatchDO>()
                .likeIfPresent(LabReviewBatchDO::getBatchCode, reqVO.getBatchCode())
                .likeIfPresent(LabReviewBatchDO::getBatchName, reqVO.getBatchName())
                .eqIfPresent(LabReviewBatchDO::getReviewType, reqVO.getReviewType())
                .eqIfPresent(LabReviewBatchDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(LabReviewBatchDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(LabReviewBatchDO::getId));
    }

}
