package cn.iocoder.yudao.module.lab.controller.admin.equipmentasset;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.lab.controller.admin.equipmentasset.vo.LabEquipmentAssetAvailableReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.equipmentasset.vo.LabEquipmentAssetPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.equipmentasset.vo.LabEquipmentAssetRespVO;
import cn.iocoder.yudao.module.lab.controller.admin.equipmentasset.vo.LabEquipmentAssetSaveReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.equipment.LabEquipmentAssetDO;
import cn.iocoder.yudao.module.lab.service.equipment.LabEquipmentAssetService;
import cn.iocoder.yudao.module.lab.service.equipment.dto.LabEquipmentAssetSummaryDTO;
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

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 设备主档")
@RestController
@RequestMapping("/lab/equipment-asset")
@Validated
public class LabEquipmentAssetController {

    @Resource
    private LabEquipmentAssetService equipmentAssetService;

    @PostMapping("/create")
    @Operation(summary = "创建设备主档")
    @PreAuthorize("@ss.hasPermission('lab:equipment-asset:create')")
    public CommonResult<Long> createEquipmentAsset(@Valid @RequestBody LabEquipmentAssetSaveReqVO createReqVO) {
        return success(equipmentAssetService.createEquipmentAsset(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新设备主档")
    @PreAuthorize("@ss.hasPermission('lab:equipment-asset:update')")
    public CommonResult<Boolean> updateEquipmentAsset(@Valid @RequestBody LabEquipmentAssetSaveReqVO updateReqVO) {
        equipmentAssetService.updateEquipmentAsset(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除设备主档")
    @PreAuthorize("@ss.hasPermission('lab:equipment-asset:delete')")
    public CommonResult<Boolean> deleteEquipmentAsset(@RequestParam("id") Long id) {
        equipmentAssetService.deleteEquipmentAsset(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得设备主档")
    @PreAuthorize("@ss.hasPermission('lab:equipment-asset:query')")
    public CommonResult<LabEquipmentAssetRespVO> getEquipmentAsset(@RequestParam("id") Long id) {
        LabEquipmentAssetDO equipmentAsset = equipmentAssetService.getEquipmentAsset(id);
        return success(BeanUtils.toBean(equipmentAsset, LabEquipmentAssetRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得设备主档分页")
    @PreAuthorize("@ss.hasPermission('lab:equipment-asset:query')")
    public CommonResult<PageResult<LabEquipmentAssetRespVO>> getEquipmentAssetPage(@Valid LabEquipmentAssetPageReqVO pageReqVO) {
        PageResult<LabEquipmentAssetDO> pageResult = equipmentAssetService.getEquipmentAssetPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, LabEquipmentAssetRespVO.class));
    }

    @GetMapping("/available")
    @Operation(summary = "获得可用于检测任务的设备")
    @PreAuthorize("@ss.hasPermission('lab:equipment-asset:query')")
    public CommonResult<List<LabEquipmentAssetSummaryDTO>> getAvailableEquipment(@Valid LabEquipmentAssetAvailableReqVO reqVO) {
        return success(equipmentAssetService.getAvailableEquipment(reqVO.getDomainCode(), reqVO.getTestItem()));
    }

}
