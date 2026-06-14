package cn.iocoder.yudao.module.lab.dal.mysql.template;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lab.controller.admin.template.vo.LabTemplateFieldBindingPageReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.template.LabTemplateFieldBindingDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LabTemplateFieldBindingMapper extends BaseMapperX<LabTemplateFieldBindingDO> {

    default PageResult<LabTemplateFieldBindingDO> selectPage(LabTemplateFieldBindingPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LabTemplateFieldBindingDO>()
                .eq(LabTemplateFieldBindingDO::getTemplateId, reqVO.getTemplateId())
                .likeIfPresent(LabTemplateFieldBindingDO::getFieldCode, reqVO.getFieldCode())
                .likeIfPresent(LabTemplateFieldBindingDO::getFieldName, reqVO.getFieldName())
                .eqIfPresent(LabTemplateFieldBindingDO::getSourceType, reqVO.getSourceType())
                .orderByAsc(LabTemplateFieldBindingDO::getSort)
                .orderByAsc(LabTemplateFieldBindingDO::getId));
    }

    default List<LabTemplateFieldBindingDO> selectListByTemplateId(Long templateId) {
        return selectList(new LambdaQueryWrapperX<LabTemplateFieldBindingDO>()
                .eq(LabTemplateFieldBindingDO::getTemplateId, templateId)
                .orderByAsc(LabTemplateFieldBindingDO::getSort)
                .orderByAsc(LabTemplateFieldBindingDO::getId));
    }

}
