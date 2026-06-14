package cn.iocoder.yudao.module.lims.dal.mysql.resultvalue;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lims.dal.dataobject.resultvalue.LimsTestResultValueDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LimsTestResultValueMapper extends BaseMapperX<LimsTestResultValueDO> {

    default List<LimsTestResultValueDO> selectListByRequestId(Long requestId) {
        return selectList(new LambdaQueryWrapperX<LimsTestResultValueDO>()
                .eq(LimsTestResultValueDO::getRequestId, requestId)
                .orderByAsc(LimsTestResultValueDO::getTaskId)
                .orderByAsc(LimsTestResultValueDO::getSort)
                .orderByAsc(LimsTestResultValueDO::getId));
    }

}
