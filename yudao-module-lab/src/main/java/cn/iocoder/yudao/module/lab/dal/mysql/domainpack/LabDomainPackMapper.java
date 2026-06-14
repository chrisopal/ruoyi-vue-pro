package cn.iocoder.yudao.module.lab.dal.mysql.domainpack;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lab.controller.admin.domainpack.vo.LabDomainPackPageReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.domainpack.LabDomainPackDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LabDomainPackMapper extends BaseMapperX<LabDomainPackDO> {

    default PageResult<LabDomainPackDO> selectPage(LabDomainPackPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LabDomainPackDO>()
                .eqIfPresent(LabDomainPackDO::getDomainId, reqVO.getDomainId())
                .likeIfPresent(LabDomainPackDO::getPackCode, reqVO.getPackCode())
                .likeIfPresent(LabDomainPackDO::getPackName, reqVO.getPackName())
                .likeIfPresent(LabDomainPackDO::getIndustry, reqVO.getIndustry())
                .eqIfPresent(LabDomainPackDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(LabDomainPackDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(LabDomainPackDO::getId));
    }

    default LabDomainPackDO selectByPackCode(String packCode) {
        return selectOne(LabDomainPackDO::getPackCode, packCode);
    }

    default LabDomainPackDO selectByPackCodeAndVersion(String packCode, String packVersion) {
        return selectOne(new LambdaQueryWrapperX<LabDomainPackDO>()
                .eq(LabDomainPackDO::getPackCode, packCode)
                .eq(LabDomainPackDO::getPackVersion, packVersion));
    }

}
