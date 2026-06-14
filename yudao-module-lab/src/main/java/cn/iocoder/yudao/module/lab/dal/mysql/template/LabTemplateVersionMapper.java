package cn.iocoder.yudao.module.lab.dal.mysql.template;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lab.controller.admin.template.vo.LabTemplateVersionPageReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.template.LabTemplateVersionDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LabTemplateVersionMapper extends BaseMapperX<LabTemplateVersionDO> {

    default PageResult<LabTemplateVersionDO> selectPage(LabTemplateVersionPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LabTemplateVersionDO>()
                .eqIfPresent(LabTemplateVersionDO::getDomainPackId, reqVO.getDomainPackId())
                .likeIfPresent(LabTemplateVersionDO::getTemplateCode, reqVO.getTemplateCode())
                .likeIfPresent(LabTemplateVersionDO::getTemplateName, reqVO.getTemplateName())
                .eqIfPresent(LabTemplateVersionDO::getTemplateType, reqVO.getTemplateType())
                .eqIfPresent(LabTemplateVersionDO::getTemplateStatus, reqVO.getTemplateStatus())
                .eqIfPresent(LabTemplateVersionDO::getStatus, reqVO.getStatus())
                .orderByDesc(LabTemplateVersionDO::getId));
    }

}
