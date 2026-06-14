package cn.iocoder.yudao.module.lab.dal.mysql.compliancecheck;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.lab.dal.dataobject.compliancecheck.LabComplianceCheckDO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LabComplianceCheckMapper extends BaseMapperX<LabComplianceCheckDO> {

    default PageResult<LabComplianceCheckDO> selectPage(PageParam reqVO) {
        return selectPage(reqVO, new QueryWrapper<LabComplianceCheckDO>().orderByDesc("id"));
    }

}
