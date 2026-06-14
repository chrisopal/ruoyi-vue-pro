package cn.iocoder.yudao.module.lab.service.template;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.lab.controller.admin.template.vo.*;
import cn.iocoder.yudao.module.lab.dal.dataobject.template.LabTemplateFieldBindingDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.template.LabTemplateVersionDO;
import jakarta.validation.Valid;

public interface LabTemplateService {

    Long createTemplateVersion(@Valid LabTemplateVersionSaveReqVO createReqVO);

    void updateTemplateVersion(@Valid LabTemplateVersionSaveReqVO updateReqVO);

    void publishTemplateVersion(Long id);

    void archiveTemplateVersion(Long id);

    void deleteTemplateVersion(Long id);

    LabTemplateVersionDO getTemplateVersion(Long id);

    PageResult<LabTemplateVersionDO> getTemplateVersionPage(LabTemplateVersionPageReqVO pageReqVO);

    Long createFieldBinding(@Valid LabTemplateFieldBindingSaveReqVO createReqVO);

    void updateFieldBinding(@Valid LabTemplateFieldBindingSaveReqVO updateReqVO);

    void deleteFieldBinding(Long id);

    PageResult<LabTemplateFieldBindingDO> getFieldBindingPage(LabTemplateFieldBindingPageReqVO pageReqVO);

    LabTemplatePreviewRespVO getTemplatePreview(Long templateId);

}
