package cn.iocoder.yudao.module.lab.dal.mysql.personnel;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lab.dal.dataobject.personnel.LabPersonnelCompetenceDO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface LabPersonnelCompetenceMapper extends BaseMapperX<LabPersonnelCompetenceDO> {

    default PageResult<LabPersonnelCompetenceDO> selectPage(PageParam reqVO) {
        return selectPage(reqVO, new QueryWrapper<LabPersonnelCompetenceDO>().orderByDesc("id"));
    }

    default List<LabPersonnelCompetenceDO> selectListByUserIds(Collection<Long> userIds) {
        return selectList(new LambdaQueryWrapperX<LabPersonnelCompetenceDO>()
                .inIfPresent(LabPersonnelCompetenceDO::getUserId, userIds)
                .orderByDesc(LabPersonnelCompetenceDO::getId));
    }

}
