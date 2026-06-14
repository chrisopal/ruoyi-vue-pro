package cn.iocoder.yudao.module.lab.dal.mysql.compliancecheck;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.lab.dal.dataobject.compliancecheck.LabComplianceCheckItemDO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LabComplianceCheckItemMapper extends BaseMapperX<LabComplianceCheckItemDO> {

    default PageResult<LabComplianceCheckItemDO> selectPage(PageParam reqVO) {
        return selectPage(reqVO, new QueryWrapper<LabComplianceCheckItemDO>().orderByDesc("id"));
    }

}
