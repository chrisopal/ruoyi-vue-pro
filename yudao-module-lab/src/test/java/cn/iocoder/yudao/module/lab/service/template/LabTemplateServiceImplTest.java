package cn.iocoder.yudao.module.lab.service.template;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.lab.controller.admin.template.vo.LabTemplatePreviewRespVO;
import cn.iocoder.yudao.module.lab.controller.admin.template.vo.LabTemplateVersionSaveReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.template.vo.LabTemplateFieldBindingSaveReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.template.LabTemplateFieldBindingDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.template.LabTemplateVersionDO;
import cn.iocoder.yudao.module.lab.dal.mysql.template.LabTemplateFieldBindingMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.template.LabTemplateVersionMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static cn.iocoder.yudao.module.lab.enums.ErrorCodeConstants.TEMPLATE_VERSION_IMMUTABLE;
import static cn.iocoder.yudao.module.lab.enums.ErrorCodeConstants.TEMPLATE_VERSION_STATUS_INVALID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LabTemplateServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private LabTemplateServiceImpl service;

    @Mock
    private LabTemplateVersionMapper templateVersionMapper;
    @Mock
    private LabTemplateFieldBindingMapper fieldBindingMapper;

    @Test
    void createTemplateVersion_shouldDefaultDraftStatusAndKeepReportDesignSchemas() {
        when(templateVersionMapper.insert(any(LabTemplateVersionDO.class))).thenAnswer(invocation -> {
            LabTemplateVersionDO templateVersion = invocation.getArgument(0);
            templateVersion.setId(1L);
            return 1;
        });

        Long id = service.createTemplateVersion(saveReq(null, null));

        assertEquals(1L, id);
        verify(templateVersionMapper).insert(argThat((LabTemplateVersionDO templateVersion) ->
                "draft".equals(templateVersion.getTemplateStatus())
                        && "active".equals(templateVersion.getStatus())
                        && templateVersion.getSectionSchema().contains("resultTable")
                        && templateVersion.getOutputFormats().contains("PDF")
                        && templateVersion.getDataSourceSchema().contains("resultValues")));
    }

    @Test
    void publishTemplateVersion_shouldMoveDraftToPublished() {
        when(templateVersionMapper.selectById(1L)).thenReturn(templateVersion("draft"));

        service.publishTemplateVersion(1L);

        verify(templateVersionMapper).updateById(argThat((LabTemplateVersionDO templateVersion) ->
                Long.valueOf(1L).equals(templateVersion.getId())
                        && "published".equals(templateVersion.getTemplateStatus())));
    }

    @Test
    void archiveTemplateVersion_shouldMovePublishedToArchived() {
        when(templateVersionMapper.selectById(1L)).thenReturn(templateVersion("published"));

        service.archiveTemplateVersion(1L);

        verify(templateVersionMapper).updateById(argThat((LabTemplateVersionDO templateVersion) ->
                Long.valueOf(1L).equals(templateVersion.getId())
                        && "archived".equals(templateVersion.getTemplateStatus())));
    }

    @Test
    void publishTemplateVersion_shouldRejectNonDraft() {
        when(templateVersionMapper.selectById(1L)).thenReturn(templateVersion("published"));

        ServiceException ex = assertThrows(ServiceException.class, () -> service.publishTemplateVersion(1L));

        assertEquals(TEMPLATE_VERSION_STATUS_INVALID.getCode(), ex.getCode());
    }

    @Test
    void updateTemplateVersion_shouldRejectPublishedTemplate() {
        when(templateVersionMapper.selectById(1L)).thenReturn(templateVersion("published"));

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.updateTemplateVersion(saveReq(1L, "published")));

        assertEquals(TEMPLATE_VERSION_IMMUTABLE.getCode(), ex.getCode());
    }

    @Test
    void updateFieldBinding_shouldRejectWhenExistingTemplateIsPublished() {
        LabTemplateFieldBindingDO binding = fieldBinding(10L, 1L);
        when(fieldBindingMapper.selectById(10L)).thenReturn(binding);
        when(templateVersionMapper.selectById(1L)).thenReturn(templateVersion("published"));

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.updateFieldBinding(fieldBindingReq(10L, 2L)));

        assertEquals(TEMPLATE_VERSION_IMMUTABLE.getCode(), ex.getCode());
    }

    @Test
    void getTemplatePreview_shouldExposeGovernedReportDesignFields() {
        LabTemplateVersionDO templateVersion = templateVersion("published");
        templateVersion.setSectionSchema("[{\"sectionCode\":\"resultTable\"}]");
        templateVersion.setOutputFormats("[\"WORD\",\"PDF\"]");
        templateVersion.setDataSourceSchema("{\"resultValues\":\"$.resultValues\"}");
        when(templateVersionMapper.selectById(1L)).thenReturn(templateVersion);
        when(fieldBindingMapper.selectListByTemplateId(1L)).thenReturn(List.of(fieldBinding(10L, 1L)));

        LabTemplatePreviewRespVO preview = service.getTemplatePreview(1L);

        assertEquals("[{\"sectionCode\":\"resultTable\"}]", preview.getSectionSchema());
        assertEquals("[\"WORD\",\"PDF\"]", preview.getOutputFormats());
        assertEquals("{\"resultValues\":\"$.resultValues\"}", preview.getDataSourceSchema());
        assertEquals(1, preview.getFields().size());
    }

    private static LabTemplateVersionSaveReqVO saveReq(Long id, String templateStatus) {
        LabTemplateVersionSaveReqVO reqVO = new LabTemplateVersionSaveReqVO();
        reqVO.setId(id);
        reqVO.setDomainPackId(1L);
        reqVO.setTemplateCode("REPORT_BASIC_V1");
        reqVO.setTemplateName("通用检测报告模板");
        reqVO.setTemplateVersion("1.0");
        reqVO.setTemplateType("report");
        reqVO.setTemplateStatus(templateStatus);
        reqVO.setSectionSchema("[{\"sectionCode\":\"resultTable\"}]");
        reqVO.setOutputFormats("[\"WORD\",\"PDF\",\"EXCEL\"]");
        reqVO.setDataSourceSchema("{\"resultValues\":\"$.resultValues\"}");
        reqVO.setPreviewSchema("{\"layout\":\"basic-report-preview\"}");
        reqVO.setStatus(null);
        return reqVO;
    }

    private static LabTemplateFieldBindingSaveReqVO fieldBindingReq(Long id, Long templateId) {
        LabTemplateFieldBindingSaveReqVO reqVO = new LabTemplateFieldBindingSaveReqVO();
        reqVO.setId(id);
        reqVO.setTemplateId(templateId);
        reqVO.setFieldCode("REPORT_NO");
        reqVO.setFieldName("报告编号");
        reqVO.setSourceType("report");
        reqVO.setSourcePath("$.reportNo");
        reqVO.setRequiredFlag(true);
        reqVO.setSort(10);
        return reqVO;
    }

    private static LabTemplateVersionDO templateVersion(String templateStatus) {
        LabTemplateVersionDO templateVersion = new LabTemplateVersionDO();
        templateVersion.setId(1L);
        templateVersion.setDomainPackId(1L);
        templateVersion.setTemplateCode("REPORT_BASIC_V1");
        templateVersion.setTemplateName("通用检测报告模板");
        templateVersion.setTemplateVersion("1.0");
        templateVersion.setTemplateType("report");
        templateVersion.setTemplateStatus(templateStatus);
        templateVersion.setStatus("active");
        return templateVersion;
    }

    private static LabTemplateFieldBindingDO fieldBinding(Long id, Long templateId) {
        LabTemplateFieldBindingDO fieldBinding = new LabTemplateFieldBindingDO();
        fieldBinding.setId(id);
        fieldBinding.setTemplateId(templateId);
        fieldBinding.setFieldCode("REPORT_NO");
        fieldBinding.setFieldName("报告编号");
        fieldBinding.setSourceType("report");
        fieldBinding.setSourcePath("$.reportNo");
        fieldBinding.setRequiredFlag(true);
        fieldBinding.setSort(10);
        return fieldBinding;
    }

}
