package cn.iocoder.yudao.module.lab.dal.mysql.packconfig;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lab.dal.dataobject.packconfig.LabPackQcRuleDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LabPackQcRuleMapper extends BaseMapperX<LabPackQcRuleDO> {

    default List<LabPackQcRuleDO> selectListByDomainPackId(Long domainPackId) {
        return selectList(new LambdaQueryWrapperX<LabPackQcRuleDO>()
                .eq(LabPackQcRuleDO::getDomainPackId, domainPackId)
                .orderByAsc(LabPackQcRuleDO::getSort)
                .orderByAsc(LabPackQcRuleDO::getId));
    }

    default void deleteByDomainPackId(Long domainPackId) {
        delete(new LambdaQueryWrapperX<LabPackQcRuleDO>().eq(LabPackQcRuleDO::getDomainPackId, domainPackId));
    }

}
