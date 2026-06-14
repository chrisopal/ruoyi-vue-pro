package cn.iocoder.yudao.module.lab.dal.mysql.personnel;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.lab.dal.dataobject.personnel.LabPersonnelAuthorizationDO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LabPersonnelAuthorizationMapper extends BaseMapperX<LabPersonnelAuthorizationDO> {

    default PageResult<LabPersonnelAuthorizationDO> selectPage(PageParam reqVO) {
        return selectPage(reqVO, new QueryWrapper<LabPersonnelAuthorizationDO>().orderByDesc("id"));
    }

}
