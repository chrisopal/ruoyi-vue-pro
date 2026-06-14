package cn.iocoder.yudao.module.lab.dal.mysql.equipment;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.lab.dal.dataobject.equipment.LabEquipmentTraceabilityDO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LabEquipmentTraceabilityMapper extends BaseMapperX<LabEquipmentTraceabilityDO> {

    default PageResult<LabEquipmentTraceabilityDO> selectPage(PageParam reqVO) {
        return selectPage(reqVO, new QueryWrapper<LabEquipmentTraceabilityDO>().orderByDesc("id"));
    }

}
