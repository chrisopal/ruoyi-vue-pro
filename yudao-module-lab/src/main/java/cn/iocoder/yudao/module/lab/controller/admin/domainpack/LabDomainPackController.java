package cn.iocoder.yudao.module.lab.controller.admin.domainpack;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.lab.controller.admin.domainpack.vo.LabDomainPackPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.domainpack.vo.LabDomainPackRespVO;
import cn.iocoder.yudao.module.lab.controller.admin.domainpack.vo.LabDomainPackSaveReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.domainpack.LabDomainPackDO;
import cn.iocoder.yudao.module.lab.service.domainpack.LabDomainPackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 检测方案包")
@RestController
@RequestMapping("/lab/domain-pack")
@Validated
public class LabDomainPackController {

    @Resource
    private LabDomainPackService domainPackService;

    @PostMapping("/create")
    @Operation(summary = "创建检测方案包")
    @PreAuthorize("@ss.hasPermission('lab:domain-pack:create')")
    public CommonResult<Long> createDomainPack(@Valid @RequestBody LabDomainPackSaveReqVO createReqVO) {
        return success(domainPackService.createDomainPack(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新检测方案包")
    @PreAuthorize("@ss.hasPermission('lab:domain-pack:update')")
    public CommonResult<Boolean> updateDomainPack(@Valid @RequestBody LabDomainPackSaveReqVO updateReqVO) {
        domainPackService.updateDomainPack(updateReqVO);
        return success(true);
    }

    @PostMapping("/{id}/publish")
    @Operation(summary = "发布检测方案包")
    @PreAuthorize("@ss.hasPermission('lab:domain-pack:update')")
    public CommonResult<Boolean> publishDomainPack(@PathVariable("id") Long id) {
        domainPackService.publishDomainPack(id);
        return success(true);
    }

    @PostMapping("/{id}/copy-version")
    @Operation(summary = "复制检测方案包为新草稿版本")
    @PreAuthorize("@ss.hasPermission('lab:domain-pack:create')")
    public CommonResult<Long> copyDomainPackVersion(@PathVariable("id") Long id,
                                                    @RequestParam("targetVersion") String targetVersion) {
        return success(domainPackService.copyDomainPackVersion(id, targetVersion));
    }

    @PostMapping("/{id}/archive")
    @Operation(summary = "归档检测方案包")
    @PreAuthorize("@ss.hasPermission('lab:domain-pack:update')")
    public CommonResult<Boolean> archiveDomainPack(@PathVariable("id") Long id) {
        domainPackService.archiveDomainPack(id);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除检测方案包")
    @PreAuthorize("@ss.hasPermission('lab:domain-pack:delete')")
    public CommonResult<Boolean> deleteDomainPack(@RequestParam("id") Long id) {
        domainPackService.deleteDomainPack(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得检测方案包")
    @PreAuthorize("@ss.hasPermission('lab:domain-pack:query')")
    public CommonResult<LabDomainPackRespVO> getDomainPack(@RequestParam("id") Long id) {
        LabDomainPackDO domainPack = domainPackService.getDomainPack(id);
        return success(BeanUtils.toBean(domainPack, LabDomainPackRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得检测方案包分页")
    @PreAuthorize("@ss.hasPermission('lab:domain-pack:query')")
    public CommonResult<PageResult<LabDomainPackRespVO>> getDomainPackPage(@Valid LabDomainPackPageReqVO pageReqVO) {
        PageResult<LabDomainPackDO> pageResult = domainPackService.getDomainPackPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, LabDomainPackRespVO.class));
    }

}
