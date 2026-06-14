package cn.iocoder.yudao.module.lab.controller.admin.packconfig;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.lab.controller.admin.packconfig.vo.LabPackConfigRespVO;
import cn.iocoder.yudao.module.lab.controller.admin.packconfig.vo.LabPackConfigSaveReqVO;
import cn.iocoder.yudao.module.lab.service.packconfig.LabPackConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 方案包可视化配置")
@RestController
@RequestMapping("/lab/pack-config")
@Validated
public class LabPackConfigController {

    @Resource
    private LabPackConfigService packConfigService;

    @GetMapping("/get")
    @Operation(summary = "获得方案包可视化配置")
    @PreAuthorize("@ss.hasPermission('lab:domain-pack:query')")
    public CommonResult<LabPackConfigRespVO> getPackConfig(@RequestParam("domainPackId") Long domainPackId) {
        return success(packConfigService.getPackConfig(domainPackId));
    }

    @PostMapping("/save")
    @Operation(summary = "保存方案包可视化配置")
    @PreAuthorize("@ss.hasPermission('lab:domain-pack:update')")
    public CommonResult<Boolean> savePackConfig(@Valid @RequestBody LabPackConfigSaveReqVO saveReqVO) {
        packConfigService.savePackConfig(saveReqVO);
        return success(true);
    }

}
