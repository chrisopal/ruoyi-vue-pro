package cn.iocoder.yudao.module.lab.dal.mysql.evidenceobject;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lab.controller.admin.evidenceobject.vo.LabEvidenceObjectPageReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.evidenceobject.LabEvidenceObjectDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LabEvidenceObjectMapper extends BaseMapperX<LabEvidenceObjectDO> {

    default PageResult<LabEvidenceObjectDO> selectPage(LabEvidenceObjectPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LabEvidenceObjectDO>()
                .likeIfPresent(LabEvidenceObjectDO::getEvidenceCode, reqVO.getEvidenceCode())
                .likeIfPresent(LabEvidenceObjectDO::getEvidenceName, reqVO.getEvidenceName())
                .eqIfPresent(LabEvidenceObjectDO::getEvidenceType, reqVO.getEvidenceType())
                .eqIfPresent(LabEvidenceObjectDO::getSourceObject, reqVO.getSourceObject())
                .eqIfPresent(LabEvidenceObjectDO::getBusinessDomain, reqVO.getBusinessDomain())
                .eqIfPresent(LabEvidenceObjectDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(LabEvidenceObjectDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(LabEvidenceObjectDO::getId));
    }

    default LabEvidenceObjectDO selectByEvidenceCode(String evidenceCode) {
        return selectOne(LabEvidenceObjectDO::getEvidenceCode, evidenceCode);
    }

}
