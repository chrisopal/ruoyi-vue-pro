package cn.iocoder.yudao.module.lab.dal.mysql.packconfig;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lab.dal.dataobject.packconfig.LabPackReportSectionDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LabPackReportSectionMapper extends BaseMapperX<LabPackReportSectionDO> {

    default List<LabPackReportSectionDO> selectListByDomainPackId(Long domainPackId) {
        return selectList(new LambdaQueryWrapperX<LabPackReportSectionDO>()
                .eq(LabPackReportSectionDO::getDomainPackId, domainPackId)
                .orderByAsc(LabPackReportSectionDO::getSort)
                .orderByAsc(LabPackReportSectionDO::getId));
    }

    default void deleteByDomainPackId(Long domainPackId) {
        delete(new LambdaQueryWrapperX<LabPackReportSectionDO>().eq(LabPackReportSectionDO::getDomainPackId, domainPackId));
    }

}
