package cn.iocoder.yudao.module.lab.controller.admin.template;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.lab.controller.admin.template.vo.*;
import cn.iocoder.yudao.module.lab.dal.dataobject.template.LabTemplateFieldBindingDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.template.LabTemplateVersionDO;
import cn.iocoder.yudao.module.lab.service.template.LabTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 模板编制")
@RestController
@RequestMapping("/lab/template")
@Validated
public class LabTemplateController {

    @Resource
    private LabTemplateService templateService;

    @PostMapping("/version/create")
    @Operation(summary = "创建模板版本")
    @PreAuthorize("@ss.hasPermission('lab:template:save')")
    public CommonResult<Long> createTemplateVersion(@Valid @RequestBody LabTemplateVersionSaveReqVO createReqVO) {
        return success(templateService.createTemplateVersion(createReqVO));
    }

    @PutMapping("/version/update")
    @Operation(summary = "更新模板版本")
    @PreAuthorize("@ss.hasPermission('lab:template:save')")
    public CommonResult<Boolean> updateTemplateVersion(@Valid @RequestBody LabTemplateVersionSaveReqVO updateReqVO) {
        templateService.updateTemplateVersion(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/version/delete")
    @Operation(summary = "删除模板版本")
    @PreAuthorize("@ss.hasPermission('lab:template:save')")
    public CommonResult<Boolean> deleteTemplateVersion(@RequestParam("id") Long id) {
        templateService.deleteTemplateVersion(id);
        return success(true);
    }

    @GetMapping("/version/get")
    @Operation(summary = "获得模板版本")
    @PreAuthorize("@ss.hasPermission('lab:template:query')")
    public CommonResult<LabTemplateVersionRespVO> getTemplateVersion(@RequestParam("id") Long id) {
        LabTemplateVersionDO templateVersion = templateService.getTemplateVersion(id);
        return success(BeanUtils.toBean(templateVersion, LabTemplateVersionRespVO.class));
    }

    @GetMapping("/version/page")
    @Operation(summary = "获得模板版本分页")
    @PreAuthorize("@ss.hasPermission('lab:template:query')")
    public CommonResult<PageResult<LabTemplateVersionRespVO>> getTemplateVersionPage(@Valid LabTemplateVersionPageReqVO pageReqVO) {
        PageResult<LabTemplateVersionDO> pageResult = templateService.getTemplateVersionPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, LabTemplateVersionRespVO.class));
    }

    @PostMapping("/field/create")
    @Operation(summary = "创建模板字段绑定")
    @PreAuthorize("@ss.hasPermission('lab:template:save')")
    public CommonResult<Long> createFieldBinding(@Valid @RequestBody LabTemplateFieldBindingSaveReqVO createReqVO) {
        return success(templateService.createFieldBinding(createReqVO));
    }

    @PutMapping("/field/update")
    @Operation(summary = "更新模板字段绑定")
    @PreAuthorize("@ss.hasPermission('lab:template:save')")
    public CommonResult<Boolean> updateFieldBinding(@Valid @RequestBody LabTemplateFieldBindingSaveReqVO updateReqVO) {
        templateService.updateFieldBinding(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/field/delete")
    @Operation(summary = "删除模板字段绑定")
    @PreAuthorize("@ss.hasPermission('lab:template:save')")
    public CommonResult<Boolean> deleteFieldBinding(@RequestParam("id") Long id) {
        templateService.deleteFieldBinding(id);
        return success(true);
    }

    @GetMapping("/field/page")
    @Operation(summary = "获得模板字段绑定分页")
    @PreAuthorize("@ss.hasPermission('lab:template:query')")
    public CommonResult<PageResult<LabTemplateFieldBindingRespVO>> getFieldBindingPage(@Valid LabTemplateFieldBindingPageReqVO pageReqVO) {
        PageResult<LabTemplateFieldBindingDO> pageResult = templateService.getFieldBindingPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, LabTemplateFieldBindingRespVO.class));
    }

    @GetMapping("/preview")
    @Operation(summary = "获得报告预览配置")
    @PreAuthorize("@ss.hasPermission('lab:template:preview')")
    public CommonResult<LabTemplatePreviewRespVO> getTemplatePreview(@RequestParam("templateId") Long templateId) {
        return success(templateService.getTemplatePreview(templateId));
    }

}
