package cn.iocoder.yudao.module.lab.service.template;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.lab.controller.admin.template.vo.*;
import cn.iocoder.yudao.module.lab.dal.dataobject.template.LabTemplateFieldBindingDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.template.LabTemplateVersionDO;
import cn.iocoder.yudao.module.lab.dal.mysql.template.LabTemplateFieldBindingMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.template.LabTemplateVersionMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lab.enums.ErrorCodeConstants.*;

@Service
@Validated
public class LabTemplateServiceImpl implements LabTemplateService {

    @Resource
    private LabTemplateVersionMapper templateVersionMapper;
    @Resource
    private LabTemplateFieldBindingMapper fieldBindingMapper;

    @Override
    public Long createTemplateVersion(LabTemplateVersionSaveReqVO createReqVO) {
        LabTemplateVersionDO templateVersion = BeanUtils.toBean(createReqVO, LabTemplateVersionDO.class);
        templateVersionMapper.insert(templateVersion);
        return templateVersion.getId();
    }

    @Override
    public void updateTemplateVersion(LabTemplateVersionSaveReqVO updateReqVO) {
        validateTemplateVersionExists(updateReqVO.getId());
        templateVersionMapper.updateById(BeanUtils.toBean(updateReqVO, LabTemplateVersionDO.class));
    }

    @Override
    public void deleteTemplateVersion(Long id) {
        validateTemplateVersionExists(id);
        templateVersionMapper.deleteById(id);
    }

    @Override
    public LabTemplateVersionDO getTemplateVersion(Long id) {
        return templateVersionMapper.selectById(id);
    }

    @Override
    public PageResult<LabTemplateVersionDO> getTemplateVersionPage(LabTemplateVersionPageReqVO pageReqVO) {
        return templateVersionMapper.selectPage(pageReqVO);
    }

    @Override
    public Long createFieldBinding(LabTemplateFieldBindingSaveReqVO createReqVO) {
        validateTemplateVersionExists(createReqVO.getTemplateId());
        LabTemplateFieldBindingDO fieldBinding = BeanUtils.toBean(createReqVO, LabTemplateFieldBindingDO.class);
        fieldBindingMapper.insert(fieldBinding);
        return fieldBinding.getId();
    }

    @Override
    public void updateFieldBinding(LabTemplateFieldBindingSaveReqVO updateReqVO) {
        validateFieldBindingExists(updateReqVO.getId());
        validateTemplateVersionExists(updateReqVO.getTemplateId());
        fieldBindingMapper.updateById(BeanUtils.toBean(updateReqVO, LabTemplateFieldBindingDO.class));
    }

    @Override
    public void deleteFieldBinding(Long id) {
        validateFieldBindingExists(id);
        fieldBindingMapper.deleteById(id);
    }

    @Override
    public PageResult<LabTemplateFieldBindingDO> getFieldBindingPage(LabTemplateFieldBindingPageReqVO pageReqVO) {
        return fieldBindingMapper.selectPage(pageReqVO);
    }

    @Override
    public LabTemplatePreviewRespVO getTemplatePreview(Long templateId) {
        LabTemplateVersionDO templateVersion = validateTemplateVersionExists(templateId);
        List<LabTemplateFieldBindingDO> fieldBindings = fieldBindingMapper.selectListByTemplateId(templateId);
        LabTemplatePreviewRespVO respVO = new LabTemplatePreviewRespVO();
        respVO.setTemplate(BeanUtils.toBean(templateVersion, LabTemplateVersionRespVO.class));
        respVO.setFields(BeanUtils.toBean(fieldBindings, LabTemplateFieldBindingRespVO.class));
        return respVO;
    }

    private LabTemplateVersionDO validateTemplateVersionExists(Long id) {
        LabTemplateVersionDO templateVersion = id == null ? null : templateVersionMapper.selectById(id);
        if (templateVersion == null) {
            throw exception(TEMPLATE_VERSION_NOT_EXISTS);
        }
        return templateVersion;
    }

    private void validateFieldBindingExists(Long id) {
        if (id == null || fieldBindingMapper.selectById(id) == null) {
            throw exception(TEMPLATE_FIELD_BINDING_NOT_EXISTS);
        }
    }

}
