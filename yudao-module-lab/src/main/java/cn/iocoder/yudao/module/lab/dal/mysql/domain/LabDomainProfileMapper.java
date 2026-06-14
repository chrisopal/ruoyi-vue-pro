package cn.iocoder.yudao.module.lab.dal.mysql.domain;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lab.controller.admin.domain.vo.LabDomainProfilePageReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.domain.LabDomainProfileDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LabDomainProfileMapper extends BaseMapperX<LabDomainProfileDO> {

    default PageResult<LabDomainProfileDO> selectPage(LabDomainProfilePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LabDomainProfileDO>()
                .likeIfPresent(LabDomainProfileDO::getDomainCode, reqVO.getDomainCode())
                .likeIfPresent(LabDomainProfileDO::getDomainName, reqVO.getDomainName())
                .eqIfPresent(LabDomainProfileDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(LabDomainProfileDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(LabDomainProfileDO::getId));
    }

    default LabDomainProfileDO selectByDomainCode(String domainCode) {
        return selectOne(LabDomainProfileDO::getDomainCode, domainCode);
    }

}
