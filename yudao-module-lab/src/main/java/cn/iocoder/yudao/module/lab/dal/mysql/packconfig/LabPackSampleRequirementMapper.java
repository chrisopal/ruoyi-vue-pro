package cn.iocoder.yudao.module.lab.dal.mysql.packconfig;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lab.dal.dataobject.packconfig.LabPackSampleRequirementDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LabPackSampleRequirementMapper extends BaseMapperX<LabPackSampleRequirementDO> {

    default List<LabPackSampleRequirementDO> selectListByDomainPackId(Long domainPackId) {
        return selectList(new LambdaQueryWrapperX<LabPackSampleRequirementDO>()
                .eq(LabPackSampleRequirementDO::getDomainPackId, domainPackId)
                .orderByAsc(LabPackSampleRequirementDO::getSort)
                .orderByAsc(LabPackSampleRequirementDO::getId));
    }

    default void deleteByDomainPackId(Long domainPackId) {
        delete(new LambdaQueryWrapperX<LabPackSampleRequirementDO>().eq(LabPackSampleRequirementDO::getDomainPackId, domainPackId));
    }

}
