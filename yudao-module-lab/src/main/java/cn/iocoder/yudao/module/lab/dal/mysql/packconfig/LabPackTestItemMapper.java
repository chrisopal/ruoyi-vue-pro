package cn.iocoder.yudao.module.lab.dal.mysql.packconfig;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lab.dal.dataobject.packconfig.LabPackTestItemDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LabPackTestItemMapper extends BaseMapperX<LabPackTestItemDO> {

    default List<LabPackTestItemDO> selectListByDomainPackId(Long domainPackId) {
        return selectList(new LambdaQueryWrapperX<LabPackTestItemDO>()
                .eq(LabPackTestItemDO::getDomainPackId, domainPackId)
                .orderByAsc(LabPackTestItemDO::getSort)
                .orderByAsc(LabPackTestItemDO::getId));
    }

    default void deleteByDomainPackId(Long domainPackId) {
        delete(new LambdaQueryWrapperX<LabPackTestItemDO>().eq(LabPackTestItemDO::getDomainPackId, domainPackId));
    }

}
