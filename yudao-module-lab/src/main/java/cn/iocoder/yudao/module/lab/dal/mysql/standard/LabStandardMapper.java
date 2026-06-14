package cn.iocoder.yudao.module.lab.dal.mysql.standard;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lab.controller.admin.standard.vo.LabStandardPageReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.standard.LabStandardDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LabStandardMapper extends BaseMapperX<LabStandardDO> {

    default LabStandardDO selectByStandardCode(String standardCode) {
        return selectOne(LabStandardDO::getStandardCode, standardCode);
    }

    default PageResult<LabStandardDO> selectPage(LabStandardPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LabStandardDO>()
                .likeIfPresent(LabStandardDO::getStandardCode, reqVO.getStandardCode())
                .likeIfPresent(LabStandardDO::getStandardName, reqVO.getStandardName())
                .eqIfPresent(LabStandardDO::getStandardType, reqVO.getStandardType())
                .eqIfPresent(LabStandardDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(LabStandardDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(LabStandardDO::getId));
    }

}
