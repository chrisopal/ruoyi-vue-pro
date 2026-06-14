package cn.iocoder.yudao.module.lab.dal.mysql.personnel;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lab.dal.dataobject.personnel.LabPersonnelAuthorizationDO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LabPersonnelAuthorizationMapper extends BaseMapperX<LabPersonnelAuthorizationDO> {

    default PageResult<LabPersonnelAuthorizationDO> selectPage(PageParam reqVO) {
        return selectPage(reqVO, new QueryWrapper<LabPersonnelAuthorizationDO>().orderByDesc("id"));
    }

    default List<LabPersonnelAuthorizationDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<LabPersonnelAuthorizationDO>()
                .eqIfPresent(LabPersonnelAuthorizationDO::getUserId, userId)
                .orderByDesc(LabPersonnelAuthorizationDO::getId));
    }

    default List<LabPersonnelAuthorizationDO> selectListForAvailability() {
        return selectList(new LambdaQueryWrapperX<LabPersonnelAuthorizationDO>()
                .orderByAsc(LabPersonnelAuthorizationDO::getUserId)
                .orderByDesc(LabPersonnelAuthorizationDO::getId));
    }

}
