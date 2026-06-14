package cn.iocoder.yudao.module.lab.dal.mysql.audit;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.lab.dal.dataobject.audit.LabManagementReviewDO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LabManagementReviewMapper extends BaseMapperX<LabManagementReviewDO> {

    default PageResult<LabManagementReviewDO> selectPage(PageParam reqVO) {
        return selectPage(reqVO, new QueryWrapper<LabManagementReviewDO>().orderByDesc("id"));
    }

}
