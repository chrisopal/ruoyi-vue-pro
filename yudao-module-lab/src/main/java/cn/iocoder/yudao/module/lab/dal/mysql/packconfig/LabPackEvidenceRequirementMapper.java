package cn.iocoder.yudao.module.lab.dal.mysql.packconfig;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lab.dal.dataobject.packconfig.LabPackEvidenceRequirementDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LabPackEvidenceRequirementMapper extends BaseMapperX<LabPackEvidenceRequirementDO> {

    default List<LabPackEvidenceRequirementDO> selectListByDomainPackId(Long domainPackId) {
        return selectList(new LambdaQueryWrapperX<LabPackEvidenceRequirementDO>()
                .eq(LabPackEvidenceRequirementDO::getDomainPackId, domainPackId)
                .orderByAsc(LabPackEvidenceRequirementDO::getSort)
                .orderByAsc(LabPackEvidenceRequirementDO::getId));
    }

    default void deleteByDomainPackId(Long domainPackId) {
        delete(new LambdaQueryWrapperX<LabPackEvidenceRequirementDO>().eq(LabPackEvidenceRequirementDO::getDomainPackId, domainPackId));
    }

}
