package cn.iocoder.yudao.module.lab.service.equipment;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.lab.controller.admin.quality.vo.LabQualityRecordPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.quality.vo.LabQualityRecordRespVO;
import cn.iocoder.yudao.module.lab.controller.admin.quality.vo.LabQualityRecordSaveReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.equipment.LabEquipmentTraceabilityDO;
import cn.iocoder.yudao.module.lab.dal.mysql.equipment.LabEquipmentAssetMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.equipment.LabEquipmentTraceabilityMapper;
import cn.iocoder.yudao.module.lab.service.equipment.dto.LabEquipmentCalibrationEvidenceDTO;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lab.enums.ErrorCodeConstants.EQUIPMENT_ASSET_NOT_EXISTS;
import static cn.iocoder.yudao.module.lab.enums.ErrorCodeConstants.EQUIPMENT_TRACEABILITY_NOT_EXISTS;

@Service
@Validated
public class LabEquipmentTraceabilityServiceImpl implements LabEquipmentTraceabilityService {

    private static final String TRACEABILITY_TYPE_CALIBRATION = "calibration";
    private static final String STATUS_VALID = "valid";
    private static final String STATUS_DRAFT = "draft";

    @Resource
    private LabEquipmentTraceabilityMapper traceabilityMapper;
    @Resource
    private LabEquipmentAssetMapper equipmentAssetMapper;

    @Override
    public Long createEquipmentTraceability(LabQualityRecordSaveReqVO createReqVO) {
        validateEquipmentAssetExists(createReqVO.getEquipmentId());

        LabEquipmentTraceabilityDO evidence = BeanUtils.toBean(createReqVO, LabEquipmentTraceabilityDO.class);
        normalizeEvidence(evidence);
        traceabilityMapper.insert(evidence);
        return evidence.getId();
    }

    @Override
    public void updateEquipmentTraceability(LabQualityRecordSaveReqVO updateReqVO) {
        validateEquipmentTraceabilityExists(updateReqVO.getId());
        validateEquipmentAssetExists(updateReqVO.getEquipmentId());

        LabEquipmentTraceabilityDO evidence = BeanUtils.toBean(updateReqVO, LabEquipmentTraceabilityDO.class);
        normalizeEvidence(evidence);
        traceabilityMapper.updateById(evidence);
    }

    @Override
    public void deleteEquipmentTraceability(Long id) {
        validateEquipmentTraceabilityExists(id);
        traceabilityMapper.deleteById(id);
    }

    @Override
    public LabQualityRecordRespVO getEquipmentTraceability(Long id) {
        return BeanUtils.toBean(traceabilityMapper.selectById(id), LabQualityRecordRespVO.class);
    }

    @Override
    public PageResult<LabQualityRecordRespVO> getEquipmentTraceabilityPage(LabQualityRecordPageReqVO pageReqVO) {
        return BeanUtils.toBean(traceabilityMapper.selectPage(pageReqVO), LabQualityRecordRespVO.class);
    }

    @Override
    public void updateEquipmentTraceabilityStatus(Long id, String status) {
        validateEquipmentTraceabilityExists(id);
        traceabilityMapper.update(null, new UpdateWrapper<LabEquipmentTraceabilityDO>().eq("id", id).set("status", status));
    }

    @Override
    public List<LabEquipmentCalibrationEvidenceDTO> getCurrentCalibrationEvidence(Long equipmentId) {
        validateEquipmentAssetExists(equipmentId);
        LocalDate today = LocalDate.now();
        return traceabilityMapper.selectByEquipmentId(equipmentId).stream()
                .filter(evidence -> STATUS_VALID.equalsIgnoreCase(evidence.getStatus()))
                .filter(evidence -> matchesCalibrationType(evidence))
                .filter(evidence -> isEffective(evidence, today))
                .map(evidence -> {
                    LabEquipmentCalibrationEvidenceDTO dto = BeanUtils.toBean(evidence, LabEquipmentCalibrationEvidenceDTO.class);
                    dto.setEffective(true);
                    return dto;
                })
                .toList();
    }

    private void validateEquipmentAssetExists(Long equipmentId) {
        if (equipmentId == null || equipmentAssetMapper.selectById(equipmentId) == null) {
            throw exception(EQUIPMENT_ASSET_NOT_EXISTS);
        }
    }

    private void validateEquipmentTraceabilityExists(Long id) {
        if (id == null || traceabilityMapper.selectById(id) == null) {
            throw exception(EQUIPMENT_TRACEABILITY_NOT_EXISTS);
        }
    }

    private void normalizeEvidence(LabEquipmentTraceabilityDO evidence) {
        if (!StringUtils.hasText(evidence.getTraceabilityType())) {
            evidence.setTraceabilityType(TRACEABILITY_TYPE_CALIBRATION);
        }
        if (!StringUtils.hasText(evidence.getStatus()) || STATUS_DRAFT.equalsIgnoreCase(evidence.getStatus())) {
            evidence.setStatus(STATUS_VALID);
        }
    }

    private boolean matchesCalibrationType(LabEquipmentTraceabilityDO evidence) {
        return !StringUtils.hasText(evidence.getTraceabilityType())
                || TRACEABILITY_TYPE_CALIBRATION.equalsIgnoreCase(evidence.getTraceabilityType());
    }

    private boolean isEffective(LabEquipmentTraceabilityDO evidence, LocalDate today) {
        if (!StringUtils.hasText(evidence.getValidTo())) {
            return false;
        }
        try {
            return !LocalDate.parse(evidence.getValidTo()).isBefore(today);
        } catch (DateTimeParseException ignored) {
            return false;
        }
    }

}
