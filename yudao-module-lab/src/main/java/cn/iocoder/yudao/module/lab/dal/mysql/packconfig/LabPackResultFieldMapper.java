package cn.iocoder.yudao.module.lab.dal.mysql.packconfig;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lab.dal.dataobject.packconfig.LabPackResultFieldDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LabPackResultFieldMapper extends BaseMapperX<LabPackResultFieldDO> {

    default List<LabPackResultFieldDO> selectListByDomainPackId(Long domainPackId) {
        return selectList(new LambdaQueryWrapperX<LabPackResultFieldDO>()
                .eq(LabPackResultFieldDO::getDomainPackId, domainPackId)
                .orderByAsc(LabPackResultFieldDO::getSort)
                .orderByAsc(LabPackResultFieldDO::getId));
    }

    default void deleteByDomainPackId(Long domainPackId) {
        delete(new LambdaQueryWrapperX<LabPackResultFieldDO>().eq(LabPackResultFieldDO::getDomainPackId, domainPackId));
    }

}
