package cn.iocoder.yudao.module.lab.controller.admin.domain;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.lab.controller.admin.domain.vo.LabDomainProfilePageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.domain.vo.LabDomainProfileRespVO;
import cn.iocoder.yudao.module.lab.controller.admin.domain.vo.LabDomainProfileSaveReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.domain.LabDomainProfileDO;
import cn.iocoder.yudao.module.lab.service.domain.LabDomainProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 实验室检测方向")
@RestController
@RequestMapping("/lab/domain")
@Validated
public class LabDomainProfileController {

    @Resource
    private LabDomainProfileService domainProfileService;

    @PostMapping("/create")
    @Operation(summary = "创建检测方向")
    @PreAuthorize("@ss.hasPermission('lab:domain:create')")
    public CommonResult<Long> createDomainProfile(@Valid @RequestBody LabDomainProfileSaveReqVO createReqVO) {
        return success(domainProfileService.createDomainProfile(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新检测方向")
    @PreAuthorize("@ss.hasPermission('lab:domain:update')")
    public CommonResult<Boolean> updateDomainProfile(@Valid @RequestBody LabDomainProfileSaveReqVO updateReqVO) {
        domainProfileService.updateDomainProfile(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除检测方向")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('lab:domain:delete')")
    public CommonResult<Boolean> deleteDomainProfile(@RequestParam("id") Long id) {
        domainProfileService.deleteDomainProfile(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得检测方向")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('lab:domain:query')")
    public CommonResult<LabDomainProfileRespVO> getDomainProfile(@RequestParam("id") Long id) {
        LabDomainProfileDO domainProfile = domainProfileService.getDomainProfile(id);
        return success(BeanUtils.toBean(domainProfile, LabDomainProfileRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得检测方向分页")
    @PreAuthorize("@ss.hasPermission('lab:domain:query')")
    public CommonResult<PageResult<LabDomainProfileRespVO>> getDomainProfilePage(@Valid LabDomainProfilePageReqVO pageReqVO) {
        PageResult<LabDomainProfileDO> pageResult = domainProfileService.getDomainProfilePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, LabDomainProfileRespVO.class));
    }

}
