package cn.iocoder.yudao.module.lab.dal.mysql.nonconformity;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.lab.dal.dataobject.nonconformity.LabCorrectiveActionDO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LabCorrectiveActionMapper extends BaseMapperX<LabCorrectiveActionDO> {

    default PageResult<LabCorrectiveActionDO> selectPage(PageParam reqVO) {
        return selectPage(reqVO, new QueryWrapper<LabCorrectiveActionDO>().orderByDesc("id"));
    }

}
