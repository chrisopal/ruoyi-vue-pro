package cn.iocoder.yudao.module.lab.controller.admin.evidencelink;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.lab.controller.admin.evidencelink.vo.LabEvidenceLinkPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.evidencelink.vo.LabEvidenceLinkRespVO;
import cn.iocoder.yudao.module.lab.controller.admin.evidencelink.vo.LabEvidenceLinkSaveReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.evidencelink.LabEvidenceLinkDO;
import cn.iocoder.yudao.module.lab.service.evidencelink.LabEvidenceLinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 证据关联")
@RestController
@RequestMapping("/lab/evidence-link")
@Validated
public class LabEvidenceLinkController {

    @Resource
    private LabEvidenceLinkService evidenceLinkService;

    @PostMapping("/create")
    @Operation(summary = "创建证据关联")
    @PreAuthorize("@ss.hasPermission('lab:evidence-link:create')")
    public CommonResult<Long> createEvidenceLink(@Valid @RequestBody LabEvidenceLinkSaveReqVO createReqVO) {
        return success(evidenceLinkService.createEvidenceLink(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新证据关联")
    @PreAuthorize("@ss.hasPermission('lab:evidence-link:update')")
    public CommonResult<Boolean> updateEvidenceLink(@Valid @RequestBody LabEvidenceLinkSaveReqVO updateReqVO) {
        evidenceLinkService.updateEvidenceLink(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除证据关联")
    @PreAuthorize("@ss.hasPermission('lab:evidence-link:delete')")
    public CommonResult<Boolean> deleteEvidenceLink(@RequestParam("id") Long id) {
        evidenceLinkService.deleteEvidenceLink(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得证据关联")
    @PreAuthorize("@ss.hasPermission('lab:evidence-link:query')")
    public CommonResult<LabEvidenceLinkRespVO> getEvidenceLink(@RequestParam("id") Long id) {
        LabEvidenceLinkDO evidenceLink = evidenceLinkService.getEvidenceLink(id);
        return success(BeanUtils.toBean(evidenceLink, LabEvidenceLinkRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得证据关联分页")
    @PreAuthorize("@ss.hasPermission('lab:evidence-link:query')")
    public CommonResult<PageResult<LabEvidenceLinkRespVO>> getEvidenceLinkPage(@Valid LabEvidenceLinkPageReqVO pageReqVO) {
        PageResult<LabEvidenceLinkDO> pageResult = evidenceLinkService.getEvidenceLinkPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, LabEvidenceLinkRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "按来源获得证据关联列表")
    @PreAuthorize("@ss.hasPermission('lab:evidence-link:query')")
    public CommonResult<List<LabEvidenceLinkRespVO>> getEvidenceLinkList(@RequestParam(value = "sourceType", required = false) String sourceType,
                                                                         @RequestParam(value = "sourceId", required = false) Long sourceId) {
        List<LabEvidenceLinkDO> list = evidenceLinkService.getEvidenceLinksBySource(sourceType, sourceId);
        return success(BeanUtils.toBean(list, LabEvidenceLinkRespVO.class));
    }

    @PostMapping("/upload")
    @Operation(summary = "上传并登记证据")
    @PreAuthorize("@ss.hasPermission('lab:evidence-link:create')")
    public CommonResult<Long> uploadEvidenceLink(@Valid @RequestBody LabEvidenceLinkSaveReqVO uploadReqVO) {
        return success(evidenceLinkService.uploadEvidenceLink(uploadReqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出证据关联")
    @PreAuthorize("@ss.hasPermission('lab:evidence-link:export')")
    public CommonResult<String> exportEvidenceLinkExcel(@Valid LabEvidenceLinkPageReqVO pageReqVO) {
        return success("EVIDENCE-LINK-EXPORT");
    }

}
