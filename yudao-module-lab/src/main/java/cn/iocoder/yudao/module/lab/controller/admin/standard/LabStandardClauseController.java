package cn.iocoder.yudao.module.lab.controller.admin.standard;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.lab.controller.admin.standard.vo.LabStandardClausePageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.standard.vo.LabStandardClauseRespVO;
import cn.iocoder.yudao.module.lab.controller.admin.standard.vo.LabStandardClauseSaveReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.standard.LabStandardClauseDO;
import cn.iocoder.yudao.module.lab.service.standard.LabStandardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 实验室标准条款")
@RestController
@RequestMapping("/lab/standard-clause")
@Validated
public class LabStandardClauseController {

    @Resource
    private LabStandardService standardService;

    @PostMapping("/create")
    @Operation(summary = "创建实验室标准条款")
    @PreAuthorize("@ss.hasPermission('lab:standard-clause:create')")
    public CommonResult<Long> createStandardClause(@Valid @RequestBody LabStandardClauseSaveReqVO createReqVO) {
        return success(standardService.createStandardClause(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新实验室标准条款")
    @PreAuthorize("@ss.hasPermission('lab:standard-clause:update')")
    public CommonResult<Boolean> updateStandardClause(@Valid @RequestBody LabStandardClauseSaveReqVO updateReqVO) {
        standardService.updateStandardClause(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除实验室标准条款")
    @PreAuthorize("@ss.hasPermission('lab:standard-clause:delete')")
    public CommonResult<Boolean> deleteStandardClause(@RequestParam("id") Long id) {
        standardService.deleteStandardClause(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得实验室标准条款")
    @PreAuthorize("@ss.hasPermission('lab:standard-clause:query')")
    public CommonResult<LabStandardClauseRespVO> getStandardClause(@RequestParam("id") Long id) {
        LabStandardClauseDO clause = standardService.getStandardClause(id);
        return success(BeanUtils.toBean(clause, LabStandardClauseRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得实验室标准条款分页")
    @PreAuthorize("@ss.hasPermission('lab:standard-clause:query')")
    public CommonResult<PageResult<LabStandardClauseRespVO>> getStandardClausePage(@Valid LabStandardClausePageReqVO pageReqVO) {
        PageResult<LabStandardClauseDO> pageResult = standardService.getStandardClausePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, LabStandardClauseRespVO.class));
    }

    @GetMapping("/tree")
    @Operation(summary = "获得实验室标准条款树")
    @PreAuthorize("@ss.hasPermission('lab:standard-clause:query')")
    public CommonResult<List<LabStandardClauseRespVO>> getStandardClauseTree(@RequestParam("standardId") Long standardId) {
        List<LabStandardClauseDO> clauses = standardService.getStandardClauseListByStandardId(standardId);
        return success(BeanUtils.toBean(clauses, LabStandardClauseRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出实验室标准条款")
    @PreAuthorize("@ss.hasPermission('lab:standard-clause:export')")
    public CommonResult<String> exportStandardClauseExcel(@Valid LabStandardClausePageReqVO pageReqVO) {
        return success("STANDARD-CLAUSE-EXPORT");
    }

}
