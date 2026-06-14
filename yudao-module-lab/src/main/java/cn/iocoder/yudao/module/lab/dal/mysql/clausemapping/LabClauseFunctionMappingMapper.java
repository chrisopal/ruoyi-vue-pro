package cn.iocoder.yudao.module.lab.dal.mysql.clausemapping;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.lab.dal.dataobject.clausemapping.LabClauseFunctionMappingDO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LabClauseFunctionMappingMapper extends BaseMapperX<LabClauseFunctionMappingDO> {

    default PageResult<LabClauseFunctionMappingDO> selectPage(PageParam reqVO) {
        return selectPage(reqVO, new QueryWrapper<LabClauseFunctionMappingDO>().orderByDesc("id"));
    }

}
