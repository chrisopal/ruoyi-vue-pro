package cn.iocoder.yudao.module.lab.service.equipment;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.lab.controller.admin.quality.vo.LabQualityRecordPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.quality.vo.LabQualityRecordRespVO;
import cn.iocoder.yudao.module.lab.controller.admin.quality.vo.LabQualityRecordSaveReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.evidencelink.vo.LabEvidenceLinkSaveReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.evidenceobject.LabEvidenceObjectDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.equipment.LabEquipmentAssetDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.equipment.LabEquipmentTraceabilityDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.standard.LabStandardClauseDO;
import cn.iocoder.yudao.module.lab.dal.mysql.equipment.LabEquipmentAssetMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.equipment.LabEquipmentTraceabilityMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.standard.LabStandardClauseMapper;
import cn.iocoder.yudao.module.lab.service.evidencelink.LabEvidenceLinkService;
import cn.iocoder.yudao.module.lab.service.evidenceobject.LabEvidenceObjectService;
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
    private static final String STATUS_ENABLED = "enabled";
    private static final String STATUS_EXPIRED = "expired";
    private static final String STATUS_DISABLED = "disabled";
    private static final String EVIDENCE_TYPE_EQUIPMENT_CERTIFICATE = "EQUIPMENT_CERTIFICATE";
    private static final String EVIDENCE_SOURCE_OBJECT = "lab_equipment_traceability";
    private static final String BUSINESS_DOMAIN_EQUIPMENT = "equipment";
    private static final String LINKED_BIZ_TYPE_EQUIPMENT_ASSET = "equipment_asset";

    @Resource
    private LabEquipmentTraceabilityMapper traceabilityMapper;
    @Resource
    private LabEquipmentAssetMapper equipmentAssetMapper;
    @Resource
    private LabEvidenceObjectService evidenceObjectService;
    @Resource
    private LabEvidenceLinkService evidenceLinkService;
    @Resource
    private LabStandardClauseMapper standardClauseMapper;

    @Override
    public Long createEquipmentTraceability(LabQualityRecordSaveReqVO createReqVO) {
        LabEquipmentAssetDO equipmentAsset = validateEquipmentAssetExists(createReqVO.getEquipmentId());

        LabEquipmentTraceabilityDO evidence = BeanUtils.toBean(createReqVO, LabEquipmentTraceabilityDO.class);
        normalizeEvidence(evidence);
        traceabilityMapper.insert(evidence);
        createCalibrationEvidenceChain(equipmentAsset, evidence);
        syncEquipmentCalibrationStatus(equipmentAsset, evidence);
        return evidence.getId();
    }

    @Override
    public void updateEquipmentTraceability(LabQualityRecordSaveReqVO updateReqVO) {
        validateEquipmentTraceabilityExists(updateReqVO.getId());
        LabEquipmentAssetDO equipmentAsset = validateEquipmentAssetExists(updateReqVO.getEquipmentId());

        LabEquipmentTraceabilityDO evidence = BeanUtils.toBean(updateReqVO, LabEquipmentTraceabilityDO.class);
        normalizeEvidence(evidence);
        traceabilityMapper.updateById(evidence);
        syncEquipmentCalibrationStatus(equipmentAsset, evidence);
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

    private LabEquipmentAssetDO validateEquipmentAssetExists(Long equipmentId) {
        LabEquipmentAssetDO asset = equipmentId == null ? null : equipmentAssetMapper.selectById(equipmentId);
        if (asset == null) {
            throw exception(EQUIPMENT_ASSET_NOT_EXISTS);
        }
        return asset;
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

    private void createCalibrationEvidenceChain(LabEquipmentAssetDO equipmentAsset, LabEquipmentTraceabilityDO evidence) {
        Long evidenceObjectId = evidenceObjectService.createEvidenceObject(buildCalibrationEvidenceObject(equipmentAsset, evidence));

        LabStandardClauseDO equipmentClause = standardClauseMapper.selectFirstEquipmentClause();
        LabEvidenceLinkSaveReqVO link = new LabEvidenceLinkSaveReqVO();
        link.setEvidenceObjectId(evidenceObjectId);
        link.setLinkedBizType(LINKED_BIZ_TYPE_EQUIPMENT_ASSET);
        link.setLinkedBizId(equipmentAsset.getId());
        link.setLinkedBizNo(equipmentAsset.getEquipmentCode());
        link.setClauseId(equipmentClause == null ? null : equipmentClause.getId());
        link.setClauseCategory(BUSINESS_DOMAIN_EQUIPMENT);
        link.setLinkStatus("linked");
        link.setLinkReason("设备校准证书支撑 CNAS/CMA 设备溯源条款");
        link.setRemark("由设备校准记录自动生成");
        evidenceLinkService.createEvidenceLink(link);
    }

    private void syncEquipmentCalibrationStatus(LabEquipmentAssetDO equipmentAsset, LabEquipmentTraceabilityDO evidence) {
        LocalDate validTo = parseLocalDate(evidence.getValidTo());
        if (validTo == null || !STATUS_VALID.equalsIgnoreCase(evidence.getStatus())) {
            return;
        }
        UpdateWrapper<LabEquipmentAssetDO> update = new UpdateWrapper<LabEquipmentAssetDO>()
                .eq("id", equipmentAsset.getId())
                .set("calibration_valid_until", validTo);
        if (!STATUS_DISABLED.equalsIgnoreCase(equipmentAsset.getStatus())) {
            update.set("status", validTo.isBefore(LocalDate.now()) ? STATUS_EXPIRED : STATUS_ENABLED);
        }
        equipmentAssetMapper.update(null, update);
    }

    private LabEvidenceObjectDO buildCalibrationEvidenceObject(LabEquipmentAssetDO equipmentAsset, LabEquipmentTraceabilityDO evidence) {
        LabEvidenceObjectDO evidenceObject = new LabEvidenceObjectDO();
        evidenceObject.setEvidenceCode(buildEvidenceCode(equipmentAsset, evidence));
        evidenceObject.setEvidenceName("设备校准证书-" + nullToEmpty(equipmentAsset.getEquipmentCode()));
        evidenceObject.setEvidenceType(EVIDENCE_TYPE_EQUIPMENT_CERTIFICATE);
        evidenceObject.setSourceObject(EVIDENCE_SOURCE_OBJECT);
        evidenceObject.setSourceObjectId(evidence.getId());
        evidenceObject.setSourceObjectNo(evidence.getCertificateNo());
        evidenceObject.setBusinessDomain(BUSINESS_DOMAIN_EQUIPMENT);
        evidenceObject.setFileUrl(evidence.getCertificateFileUrl());
        evidenceObject.setFileName(evidence.getCertificateNo());
        evidenceObject.setFileFormat(resolveFileFormat(evidence.getCertificateFileUrl()));
        evidenceObject.setIssuedBy(evidence.getCalibrationOrg());
        evidenceObject.setIssuedAt(parseLocalDate(evidence.getCalibrationDate()));
        evidenceObject.setValidFrom(parseLocalDate(evidence.getCalibrationDate()));
        evidenceObject.setValidTo(parseLocalDate(evidence.getValidTo()));
        evidenceObject.setStatus(STATUS_VALID.equalsIgnoreCase(evidence.getStatus()) ? "effective" : evidence.getStatus());
        evidenceObject.setSummary(String.format("设备%s的校准证书，证书号%s，结果%s",
                nullToEmpty(equipmentAsset.getEquipmentCode()),
                nullToEmpty(evidence.getCertificateNo()),
                nullToEmpty(evidence.getResult())));
        return evidenceObject;
    }

    private String buildEvidenceCode(LabEquipmentAssetDO equipmentAsset, LabEquipmentTraceabilityDO evidence) {
        return "EQ-CERT-" + equipmentAsset.getId() + "-" + evidence.getId();
    }

    private String resolveFileFormat(String fileUrl) {
        if (!StringUtils.hasText(fileUrl) || !fileUrl.contains(".")) {
            return null;
        }
        String suffix = fileUrl.substring(fileUrl.lastIndexOf('.') + 1);
        return suffix.length() > 32 ? null : suffix.toLowerCase();
    }

    private LocalDate parseLocalDate(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

}
