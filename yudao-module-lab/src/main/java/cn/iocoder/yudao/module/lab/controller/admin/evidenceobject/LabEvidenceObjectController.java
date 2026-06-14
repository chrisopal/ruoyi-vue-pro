package cn.iocoder.yudao.module.lab.controller.admin.evidenceobject;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.lab.controller.admin.evidenceobject.vo.LabEvidenceObjectPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.evidenceobject.vo.LabEvidenceObjectRespVO;
import cn.iocoder.yudao.module.lab.controller.admin.evidenceobject.vo.LabEvidenceObjectSaveReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.evidenceobject.LabEvidenceObjectDO;
import cn.iocoder.yudao.module.lab.service.evidenceobject.LabEvidenceObjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 证据对象")
@RestController
@RequestMapping("/lab/evidence-object")
@Validated
public class LabEvidenceObjectController {

    @Resource
    private LabEvidenceObjectService evidenceObjectService;

    @PostMapping("/create")
    @Operation(summary = "创建证据对象")
    @PreAuthorize("@ss.hasPermission('lab:evidence-object:create')")
    public CommonResult<Long> createEvidenceObject(@Valid @RequestBody LabEvidenceObjectSaveReqVO createReqVO) {
        return success(evidenceObjectService.createEvidenceObject(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新证据对象")
    @PreAuthorize("@ss.hasPermission('lab:evidence-object:update')")
    public CommonResult<Boolean> updateEvidenceObject(@Valid @RequestBody LabEvidenceObjectSaveReqVO updateReqVO) {
        evidenceObjectService.updateEvidenceObject(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除证据对象")
    @PreAuthorize("@ss.hasPermission('lab:evidence-object:delete')")
    public CommonResult<Boolean> deleteEvidenceObject(@RequestParam("id") Long id) {
        evidenceObjectService.deleteEvidenceObject(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得证据对象")
    @PreAuthorize("@ss.hasPermission('lab:evidence-object:query')")
    public CommonResult<LabEvidenceObjectRespVO> getEvidenceObject(@RequestParam("id") Long id) {
        LabEvidenceObjectDO evidenceObject = evidenceObjectService.getEvidenceObject(id);
        return success(BeanUtils.toBean(evidenceObject, LabEvidenceObjectRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得证据对象分页")
    @PreAuthorize("@ss.hasPermission('lab:evidence-object:query')")
    public CommonResult<PageResult<LabEvidenceObjectRespVO>> getEvidenceObjectPage(@Valid LabEvidenceObjectPageReqVO pageReqVO) {
        PageResult<LabEvidenceObjectDO> pageResult = evidenceObjectService.getEvidenceObjectPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, LabEvidenceObjectRespVO.class));
    }

}
