package cn.iocoder.yudao.module.lab.controller.admin.reviewpackage;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo.LabReviewBatchPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo.LabReviewBatchRespVO;
import cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo.LabReviewBatchSaveReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo.LabReviewCapaExcelVO;
import cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo.LabReviewChecklistExcelVO;
import cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo.LabReviewEvidenceExcelVO;
import cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo.LabReviewItemPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo.LabReviewItemRespVO;
import cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo.LabReviewItemSaveReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo.LabReviewNonconformityExcelVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.reviewpackage.LabReviewBatchDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.reviewpackage.LabReviewItemDO;
import cn.iocoder.yudao.module.lab.service.reviewpackage.LabReviewPackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
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

import java.io.IOException;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.module.lab.service.reviewpackage.LabReviewPackageServiceImpl.DEFAULT_BATCH_CODE;

@Tag(name = "管理后台 - 实验室评审材料包")
@RestController
@RequestMapping("/lab/review-package")
@Validated
public class LabReviewPackageController {

    @Resource
    private LabReviewPackageService reviewPackageService;

    @PostMapping("/batch/create")
    @Operation(summary = "创建评审批次")
    @PreAuthorize("@ss.hasPermission('lab:review-package:create')")
    public CommonResult<Long> createReviewBatch(@Valid @RequestBody LabReviewBatchSaveReqVO createReqVO) {
        return success(reviewPackageService.createReviewBatch(createReqVO));
    }

    @PutMapping("/batch/update")
    @Operation(summary = "更新评审批次")
    @PreAuthorize("@ss.hasPermission('lab:review-package:update')")
    public CommonResult<Boolean> updateReviewBatch(@Valid @RequestBody LabReviewBatchSaveReqVO updateReqVO) {
        reviewPackageService.updateReviewBatch(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/batch/delete")
    @Operation(summary = "删除评审批次")
    @PreAuthorize("@ss.hasPermission('lab:review-package:delete')")
    public CommonResult<Boolean> deleteReviewBatch(@RequestParam("id") Long id) {
        reviewPackageService.deleteReviewBatch(id);
        return success(true);
    }

    @GetMapping("/batch/get")
    @Operation(summary = "获得评审批次")
    @PreAuthorize("@ss.hasPermission('lab:review-package:query')")
    public CommonResult<LabReviewBatchRespVO> getReviewBatch(@RequestParam("id") Long id) {
        LabReviewBatchDO batch = reviewPackageService.getReviewBatch(id);
        return success(BeanUtils.toBean(batch, LabReviewBatchRespVO.class));
    }

    @GetMapping("/batch/page")
    @Operation(summary = "获得评审批次分页")
    @PreAuthorize("@ss.hasPermission('lab:review-package:query')")
    public CommonResult<PageResult<LabReviewBatchRespVO>> getReviewBatchPage(@Valid LabReviewBatchPageReqVO pageReqVO) {
        PageResult<LabReviewBatchDO> pageResult = reviewPackageService.getReviewBatchPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, LabReviewBatchRespVO.class));
    }

    @PostMapping("/item/create")
    @Operation(summary = "创建评审材料条目")
    @PreAuthorize("@ss.hasPermission('lab:review-package:create')")
    public CommonResult<Long> createReviewItem(@Valid @RequestBody LabReviewItemSaveReqVO createReqVO) {
        return success(reviewPackageService.createReviewItem(createReqVO));
    }

    @PutMapping("/item/update")
    @Operation(summary = "更新评审材料条目")
    @PreAuthorize("@ss.hasPermission('lab:review-package:update')")
    public CommonResult<Boolean> updateReviewItem(@Valid @RequestBody LabReviewItemSaveReqVO updateReqVO) {
        reviewPackageService.updateReviewItem(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/item/delete")
    @Operation(summary = "删除评审材料条目")
    @PreAuthorize("@ss.hasPermission('lab:review-package:delete')")
    public CommonResult<Boolean> deleteReviewItem(@RequestParam("id") Long id) {
        reviewPackageService.deleteReviewItem(id);
        return success(true);
    }

    @GetMapping("/item/get")
    @Operation(summary = "获得评审材料条目")
    @PreAuthorize("@ss.hasPermission('lab:review-package:query')")
    public CommonResult<LabReviewItemRespVO> getReviewItem(@RequestParam("id") Long id) {
        LabReviewItemDO item = reviewPackageService.getReviewItem(id);
        return success(BeanUtils.toBean(item, LabReviewItemRespVO.class));
    }

    @GetMapping("/item/page")
    @Operation(summary = "获得评审材料条目分页")
    @PreAuthorize("@ss.hasPermission('lab:review-package:query')")
    public CommonResult<PageResult<LabReviewItemRespVO>> getReviewItemPage(@Valid LabReviewItemPageReqVO pageReqVO) {
        PageResult<LabReviewItemDO> pageResult = reviewPackageService.getReviewItemPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, LabReviewItemRespVO.class));
    }

    @GetMapping("/export-checklist")
    @Operation(summary = "导出评审检查表 Excel")
    @PreAuthorize("@ss.hasPermission('lab:review-package:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportChecklist(HttpServletResponse response,
                                @RequestParam(value = "batchCode", defaultValue = DEFAULT_BATCH_CODE) String batchCode)
            throws IOException {
        ExcelUtils.write(response, "评审检查表.xls", "检查表", LabReviewChecklistExcelVO.class,
                reviewPackageService.getChecklistRows(batchCode));
    }

    @GetMapping("/export-evidence")
    @Operation(summary = "导出证据清单 Excel")
    @PreAuthorize("@ss.hasPermission('lab:review-package:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportEvidence(HttpServletResponse response,
                               @RequestParam(value = "batchCode", defaultValue = DEFAULT_BATCH_CODE) String batchCode)
            throws IOException {
        ExcelUtils.write(response, "证据清单.xls", "证据清单", LabReviewEvidenceExcelVO.class,
                reviewPackageService.getEvidenceRows(batchCode));
    }

    @GetMapping("/export-nc")
    @Operation(summary = "导出 NC 清单 Excel")
    @PreAuthorize("@ss.hasPermission('lab:review-package:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportNonconformity(HttpServletResponse response,
                                    @RequestParam(value = "batchCode", defaultValue = DEFAULT_BATCH_CODE) String batchCode)
            throws IOException {
        ExcelUtils.write(response, "NC清单.xls", "NC清单", LabReviewNonconformityExcelVO.class,
                reviewPackageService.getNonconformityRows(batchCode));
    }

    @GetMapping("/export-capa")
    @Operation(summary = "导出 CAPA 清单 Excel")
    @PreAuthorize("@ss.hasPermission('lab:review-package:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportCapa(HttpServletResponse response,
                           @RequestParam(value = "batchCode", defaultValue = DEFAULT_BATCH_CODE) String batchCode)
            throws IOException {
        ExcelUtils.write(response, "CAPA清单.xls", "CAPA清单", LabReviewCapaExcelVO.class,
                reviewPackageService.getCapaRows(batchCode));
    }

}
