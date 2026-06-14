package cn.iocoder.yudao.module.lab.dal.mysql.packconfig;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lab.dal.dataobject.packconfig.LabPackWorkflowNodeDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LabPackWorkflowNodeMapper extends BaseMapperX<LabPackWorkflowNodeDO> {

    default List<LabPackWorkflowNodeDO> selectListByDomainPackId(Long domainPackId) {
        return selectList(new LambdaQueryWrapperX<LabPackWorkflowNodeDO>()
                .eq(LabPackWorkflowNodeDO::getDomainPackId, domainPackId)
                .orderByAsc(LabPackWorkflowNodeDO::getSort)
                .orderByAsc(LabPackWorkflowNodeDO::getId));
    }

    default void deleteByDomainPackId(Long domainPackId) {
        delete(new LambdaQueryWrapperX<LabPackWorkflowNodeDO>().eq(LabPackWorkflowNodeDO::getDomainPackId, domainPackId));
    }

}
