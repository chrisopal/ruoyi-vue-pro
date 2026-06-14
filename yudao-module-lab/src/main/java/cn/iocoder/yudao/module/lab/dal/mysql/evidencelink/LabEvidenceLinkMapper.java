package cn.iocoder.yudao.module.lab.dal.mysql.evidencelink;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lab.controller.admin.evidencelink.vo.LabEvidenceLinkPageReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.evidencelink.LabEvidenceLinkDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LabEvidenceLinkMapper extends BaseMapperX<LabEvidenceLinkDO> {

    default PageResult<LabEvidenceLinkDO> selectPage(LabEvidenceLinkPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LabEvidenceLinkDO>()
                .eqIfPresent(LabEvidenceLinkDO::getEvidenceCode, reqVO.getEvidenceCode())
                .eqIfPresent(LabEvidenceLinkDO::getSourceObject, reqVO.getSourceObject())
                .likeIfPresent(LabEvidenceLinkDO::getSourceObjectNo, reqVO.getSourceObjectNo())
                .eqIfPresent(LabEvidenceLinkDO::getLinkedBizType, reqVO.getLinkedBizType())
                .likeIfPresent(LabEvidenceLinkDO::getLinkedBizNo, reqVO.getLinkedBizNo())
                .eqIfPresent(LabEvidenceLinkDO::getClauseCategory, reqVO.getClauseCategory())
                .betweenIfPresent(LabEvidenceLinkDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(LabEvidenceLinkDO::getId));
    }

    default List<LabEvidenceLinkDO> selectListBySource(String sourceType, Long sourceId) {
        return selectList(new LambdaQueryWrapperX<LabEvidenceLinkDO>()
                .eqIfPresent(LabEvidenceLinkDO::getSourceObject, sourceType)
                .eqIfPresent(LabEvidenceLinkDO::getSourceObjectId, sourceId)
                .orderByDesc(LabEvidenceLinkDO::getId));
    }

}
