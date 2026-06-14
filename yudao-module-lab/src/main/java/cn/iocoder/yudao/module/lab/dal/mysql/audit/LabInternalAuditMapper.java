package cn.iocoder.yudao.module.lab.dal.mysql.audit;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.lab.dal.dataobject.audit.LabInternalAuditDO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LabInternalAuditMapper extends BaseMapperX<LabInternalAuditDO> {

    default PageResult<LabInternalAuditDO> selectPage(PageParam reqVO) {
        return selectPage(reqVO, new QueryWrapper<LabInternalAuditDO>().orderByDesc("id"));
    }

}
