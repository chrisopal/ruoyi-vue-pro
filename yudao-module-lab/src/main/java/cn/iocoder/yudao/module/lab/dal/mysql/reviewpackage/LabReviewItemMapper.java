package cn.iocoder.yudao.module.lab.dal.mysql.reviewpackage;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo.LabReviewItemPageReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.reviewpackage.LabReviewItemDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LabReviewItemMapper extends BaseMapperX<LabReviewItemDO> {

    default List<LabReviewItemDO> selectListByBatchIdAndType(Long batchId, String itemType) {
        return selectList(new LambdaQueryWrapperX<LabReviewItemDO>()
                .eq(LabReviewItemDO::getBatchId, batchId)
                .eq(LabReviewItemDO::getItemType, itemType)
                .orderByAsc(LabReviewItemDO::getSort)
                .orderByAsc(LabReviewItemDO::getId));
    }

    default PageResult<LabReviewItemDO> selectPage(LabReviewItemPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LabReviewItemDO>()
                .eqIfPresent(LabReviewItemDO::getBatchId, reqVO.getBatchId())
                .eqIfPresent(LabReviewItemDO::getItemType, reqVO.getItemType())
                .likeIfPresent(LabReviewItemDO::getClauseCode, reqVO.getClauseCode())
                .eqIfPresent(LabReviewItemDO::getClauseCategory, reqVO.getClauseCategory())
                .likeIfPresent(LabReviewItemDO::getNcNo, reqVO.getNcNo())
                .likeIfPresent(LabReviewItemDO::getCapaNo, reqVO.getCapaNo())
                .orderByAsc(LabReviewItemDO::getSort)
                .orderByAsc(LabReviewItemDO::getId));
    }

}
