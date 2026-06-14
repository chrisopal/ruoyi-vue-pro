package cn.iocoder.yudao.module.lab.service.equipment;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.lab.controller.admin.equipmentasset.vo.LabEquipmentAssetPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.equipmentasset.vo.LabEquipmentAssetSaveReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.equipment.LabEquipmentAssetDO;
import cn.iocoder.yudao.module.lab.dal.mysql.equipment.LabEquipmentAssetMapper;
import cn.iocoder.yudao.module.lab.service.equipment.dto.LabEquipmentAssetSummaryDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lab.enums.ErrorCodeConstants.EQUIPMENT_ASSET_CODE_DUPLICATE;
import static cn.iocoder.yudao.module.lab.enums.ErrorCodeConstants.EQUIPMENT_ASSET_NOT_EXISTS;

@Service
@Validated
public class LabEquipmentAssetServiceImpl implements LabEquipmentAssetService {

    private static final String STATUS_DRAFT = "draft";
    private static final String STATUS_ENABLED = "enabled";
    private static final String STATUS_EXPIRED = "expired";

    @Resource
    private LabEquipmentAssetMapper equipmentAssetMapper;

    @Override
    public Long createEquipmentAsset(LabEquipmentAssetSaveReqVO createReqVO) {
        validateEquipmentCodeUnique(null, createReqVO.getEquipmentCode());

        LabEquipmentAssetDO asset = BeanUtils.toBean(createReqVO, LabEquipmentAssetDO.class);
        normalizeStatus(asset);
        equipmentAssetMapper.insert(asset);
        return asset.getId();
    }

    @Override
    public void updateEquipmentAsset(LabEquipmentAssetSaveReqVO updateReqVO) {
        validateEquipmentAssetExists(updateReqVO.getId());
        validateEquipmentCodeUnique(updateReqVO.getId(), updateReqVO.getEquipmentCode());

        LabEquipmentAssetDO asset = BeanUtils.toBean(updateReqVO, LabEquipmentAssetDO.class);
        normalizeStatus(asset);
        equipmentAssetMapper.updateById(asset);
    }

    @Override
    public void deleteEquipmentAsset(Long id) {
        validateEquipmentAssetExists(id);
        equipmentAssetMapper.deleteById(id);
    }

    @Override
    public LabEquipmentAssetDO getEquipmentAsset(Long id) {
        return equipmentAssetMapper.selectById(id);
    }

    @Override
    public PageResult<LabEquipmentAssetDO> getEquipmentAssetPage(LabEquipmentAssetPageReqVO pageReqVO) {
        return equipmentAssetMapper.selectPage(pageReqVO);
    }

    @Override
    public List<LabEquipmentAssetSummaryDTO> getAvailableEquipment(String domainCode, String testItem) {
        LocalDate today = LocalDate.now();
        return equipmentAssetMapper.selectEnabledCandidates().stream()
                .filter(asset -> !isCalibrationExpired(asset, today))
                .filter(asset -> matchesDomain(asset, domainCode))
                .filter(asset -> matchesTestItem(asset, testItem))
                .map(asset -> BeanUtils.toBean(asset, LabEquipmentAssetSummaryDTO.class))
                .toList();
    }

    private LabEquipmentAssetDO validateEquipmentAssetExists(Long id) {
        LabEquipmentAssetDO asset = id == null ? null : equipmentAssetMapper.selectById(id);
        if (asset == null) {
            throw exception(EQUIPMENT_ASSET_NOT_EXISTS);
        }
        return asset;
    }

    private void validateEquipmentCodeUnique(Long id, String equipmentCode) {
        LabEquipmentAssetDO asset = equipmentAssetMapper.selectByEquipmentCode(equipmentCode);
        if (asset == null) {
            return;
        }
        if (id == null || !asset.getId().equals(id)) {
            throw exception(EQUIPMENT_ASSET_CODE_DUPLICATE);
        }
    }

    private void normalizeStatus(LabEquipmentAssetDO asset) {
        if (!StringUtils.hasText(asset.getStatus()) || STATUS_DRAFT.equalsIgnoreCase(asset.getStatus())) {
            asset.setStatus(isCalibrationExpired(asset, LocalDate.now()) ? STATUS_EXPIRED : STATUS_ENABLED);
        }
        if (asset.getIotEnabled() == null) {
            asset.setIotEnabled(false);
        }
    }

    private boolean isCalibrationExpired(LabEquipmentAssetDO asset, LocalDate today) {
        return asset.getCalibrationValidUntil() != null && asset.getCalibrationValidUntil().isBefore(today);
    }

    private boolean matchesDomain(LabEquipmentAssetDO asset, String domainCode) {
        if (!StringUtils.hasText(domainCode)) {
            return true;
        }
        if (!StringUtils.hasText(asset.getDomainCode())) {
            return containsIgnoreCase(asset.getCapabilityScope(), domainCode);
        }
        return asset.getDomainCode().equalsIgnoreCase(domainCode)
                || containsIgnoreCase(asset.getCapabilityScope(), domainCode);
    }

    private boolean matchesTestItem(LabEquipmentAssetDO asset, String testItem) {
        return !StringUtils.hasText(testItem)
                || !StringUtils.hasText(asset.getCapabilityScope())
                || containsIgnoreCase(asset.getCapabilityScope(), testItem);
    }

    private boolean containsIgnoreCase(String text, String keyword) {
        if (!StringUtils.hasText(text) || !StringUtils.hasText(keyword)) {
            return false;
        }
        return text.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

}
