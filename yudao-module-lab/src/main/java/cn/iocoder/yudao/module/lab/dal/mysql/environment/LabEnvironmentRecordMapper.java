package cn.iocoder.yudao.module.lab.dal.mysql.environment;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.lab.dal.dataobject.environment.LabEnvironmentRecordDO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LabEnvironmentRecordMapper extends BaseMapperX<LabEnvironmentRecordDO> {

    default PageResult<LabEnvironmentRecordDO> selectPage(PageParam reqVO) {
        return selectPage(reqVO, new QueryWrapper<LabEnvironmentRecordDO>().orderByDesc("id"));
    }

}
