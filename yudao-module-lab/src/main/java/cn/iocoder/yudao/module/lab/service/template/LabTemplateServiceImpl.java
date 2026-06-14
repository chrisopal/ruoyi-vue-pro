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
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Locale;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lab.enums.ErrorCodeConstants.*;

@Service
@Validated
public class LabTemplateServiceImpl implements LabTemplateService {

    private static final String TEMPLATE_STATUS_DRAFT = "draft";
    private static final String TEMPLATE_STATUS_PUBLISHED = "published";
    private static final String TEMPLATE_STATUS_ARCHIVED = "archived";
    private static final String STATUS_ACTIVE = "active";

    @Resource
    private LabTemplateVersionMapper templateVersionMapper;
    @Resource
    private LabTemplateFieldBindingMapper fieldBindingMapper;

    @Override
    public Long createTemplateVersion(LabTemplateVersionSaveReqVO createReqVO) {
        LabTemplateVersionDO templateVersion = BeanUtils.toBean(createReqVO, LabTemplateVersionDO.class);
        normalizeTemplateVersion(templateVersion);
        validateDraftStatus(templateVersion.getTemplateStatus());
        templateVersionMapper.insert(templateVersion);
        return templateVersion.getId();
    }

    @Override
    public void updateTemplateVersion(LabTemplateVersionSaveReqVO updateReqVO) {
        LabTemplateVersionDO existing = validateTemplateVersionExists(updateReqVO.getId());
        validateTemplateEditable(existing);
        LabTemplateVersionDO templateVersion = BeanUtils.toBean(updateReqVO, LabTemplateVersionDO.class);
        normalizeTemplateVersion(templateVersion);
        validateDraftStatus(templateVersion.getTemplateStatus());
        templateVersionMapper.updateById(templateVersion);
    }

    @Override
    public void publishTemplateVersion(Long id) {
        LabTemplateVersionDO templateVersion = validateTemplateVersionExists(id);
        if (!TEMPLATE_STATUS_DRAFT.equals(resolveTemplateStatus(templateVersion))) {
            throw exception(TEMPLATE_VERSION_STATUS_INVALID);
        }
        LabTemplateVersionDO update = new LabTemplateVersionDO();
        update.setId(id);
        update.setTemplateStatus(TEMPLATE_STATUS_PUBLISHED);
        templateVersionMapper.updateById(update);
    }

    @Override
    public void archiveTemplateVersion(Long id) {
        LabTemplateVersionDO templateVersion = validateTemplateVersionExists(id);
        if (!TEMPLATE_STATUS_PUBLISHED.equals(resolveTemplateStatus(templateVersion))) {
            throw exception(TEMPLATE_VERSION_STATUS_INVALID);
        }
        LabTemplateVersionDO update = new LabTemplateVersionDO();
        update.setId(id);
        update.setTemplateStatus(TEMPLATE_STATUS_ARCHIVED);
        templateVersionMapper.updateById(update);
    }

    @Override
    public void deleteTemplateVersion(Long id) {
        LabTemplateVersionDO templateVersion = validateTemplateVersionExists(id);
        validateTemplateEditable(templateVersion);
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
        validateTemplateEditable(validateTemplateVersionExists(createReqVO.getTemplateId()));
        LabTemplateFieldBindingDO fieldBinding = BeanUtils.toBean(createReqVO, LabTemplateFieldBindingDO.class);
        fieldBindingMapper.insert(fieldBinding);
        return fieldBinding.getId();
    }

    @Override
    public void updateFieldBinding(LabTemplateFieldBindingSaveReqVO updateReqVO) {
        LabTemplateFieldBindingDO existing = validateFieldBindingExists(updateReqVO.getId());
        validateTemplateEditable(validateTemplateVersionExists(existing.getTemplateId()));
        if (!existing.getTemplateId().equals(updateReqVO.getTemplateId())) {
            validateTemplateEditable(validateTemplateVersionExists(updateReqVO.getTemplateId()));
        }
        fieldBindingMapper.updateById(BeanUtils.toBean(updateReqVO, LabTemplateFieldBindingDO.class));
    }

    @Override
    public void deleteFieldBinding(Long id) {
        LabTemplateFieldBindingDO fieldBinding = validateFieldBindingExists(id);
        validateTemplateEditable(validateTemplateVersionExists(fieldBinding.getTemplateId()));
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
        respVO.setSectionSchema(templateVersion.getSectionSchema());
        respVO.setOutputFormats(templateVersion.getOutputFormats());
        respVO.setDataSourceSchema(templateVersion.getDataSourceSchema());
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

    private LabTemplateFieldBindingDO validateFieldBindingExists(Long id) {
        LabTemplateFieldBindingDO fieldBinding = id == null ? null : fieldBindingMapper.selectById(id);
        if (fieldBinding == null) {
            throw exception(TEMPLATE_FIELD_BINDING_NOT_EXISTS);
        }
        return fieldBinding;
    }

    private void normalizeTemplateVersion(LabTemplateVersionDO templateVersion) {
        if (!StringUtils.hasText(templateVersion.getTemplateStatus())) {
            templateVersion.setTemplateStatus(TEMPLATE_STATUS_DRAFT);
        } else {
            templateVersion.setTemplateStatus(templateVersion.getTemplateStatus().trim().toLowerCase(Locale.ROOT));
        }
        if (!StringUtils.hasText(templateVersion.getStatus())) {
            templateVersion.setStatus(STATUS_ACTIVE);
        }
    }

    private void validateDraftStatus(String templateStatus) {
        if (!TEMPLATE_STATUS_DRAFT.equals(templateStatus)) {
            throw exception(TEMPLATE_VERSION_STATUS_INVALID);
        }
    }

    private void validateTemplateEditable(LabTemplateVersionDO templateVersion) {
        String templateStatus = resolveTemplateStatus(templateVersion);
        if (TEMPLATE_STATUS_PUBLISHED.equals(templateStatus) || TEMPLATE_STATUS_ARCHIVED.equals(templateStatus)) {
            throw exception(TEMPLATE_VERSION_IMMUTABLE);
        }
    }

    private String resolveTemplateStatus(LabTemplateVersionDO templateVersion) {
        return StringUtils.hasText(templateVersion.getTemplateStatus())
                ? templateVersion.getTemplateStatus().trim().toLowerCase(Locale.ROOT)
                : TEMPLATE_STATUS_DRAFT;
    }

}
