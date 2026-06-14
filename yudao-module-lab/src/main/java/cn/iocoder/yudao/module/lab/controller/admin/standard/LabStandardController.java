package cn.iocoder.yudao.module.lab.controller.admin.standard;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.lab.controller.admin.standard.vo.LabStandardPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.standard.vo.LabStandardRespVO;
import cn.iocoder.yudao.module.lab.controller.admin.standard.vo.LabStandardSaveReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.standard.LabStandardDO;
import cn.iocoder.yudao.module.lab.service.standard.LabStandardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 实验室标准体系")
@RestController
@RequestMapping("/lab/standard")
@Validated
public class LabStandardController {

    @Resource
    private LabStandardService standardService;

    @PostMapping("/create")
    @Operation(summary = "创建实验室标准")
    @PreAuthorize("@ss.hasPermission('lab:standard:create')")
    public CommonResult<Long> createStandard(@Valid @RequestBody LabStandardSaveReqVO createReqVO) {
        return success(standardService.createStandard(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新实验室标准")
    @PreAuthorize("@ss.hasPermission('lab:standard:update')")
    public CommonResult<Boolean> updateStandard(@Valid @RequestBody LabStandardSaveReqVO updateReqVO) {
        standardService.updateStandard(updateReqVO);
        return success(true);
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新实验室标准状态")
    @PreAuthorize("@ss.hasPermission('lab:standard:update')")
    public CommonResult<Boolean> updateStandardStatus(@RequestParam("id") Long id, @RequestParam("status") String status) {
        standardService.updateStandardStatus(id, status);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除实验室标准")
    @PreAuthorize("@ss.hasPermission('lab:standard:delete')")
    public CommonResult<Boolean> deleteStandard(@RequestParam("id") Long id) {
        standardService.deleteStandard(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得实验室标准")
    @PreAuthorize("@ss.hasPermission('lab:standard:query')")
    public CommonResult<LabStandardRespVO> getStandard(@RequestParam("id") Long id) {
        LabStandardDO standard = standardService.getStandard(id);
        return success(BeanUtils.toBean(standard, LabStandardRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得实验室标准分页")
    @PreAuthorize("@ss.hasPermission('lab:standard:query')")
    public CommonResult<PageResult<LabStandardRespVO>> getStandardPage(@Valid LabStandardPageReqVO pageReqVO) {
        PageResult<LabStandardDO> pageResult = standardService.getStandardPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, LabStandardRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出实验室标准")
    @PreAuthorize("@ss.hasPermission('lab:standard:export')")
    public CommonResult<String> exportStandardExcel(@Valid LabStandardPageReqVO pageReqVO) {
        return success("STANDARD-EXPORT");
    }

}
