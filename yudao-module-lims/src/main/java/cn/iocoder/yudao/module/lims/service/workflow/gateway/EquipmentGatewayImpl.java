package cn.iocoder.yudao.module.lims.service.workflow.gateway;

import cn.iocoder.yudao.module.lab.service.equipment.LabEquipmentAssetService;
import cn.iocoder.yudao.module.lab.service.equipment.LabEquipmentTraceabilityService;
import cn.iocoder.yudao.module.lab.service.equipment.dto.LabEquipmentAssetSummaryDTO;
import cn.iocoder.yudao.module.lab.service.equipment.dto.LabEquipmentCalibrationEvidenceDTO;
import cn.iocoder.yudao.module.lims.service.workflow.model.AvailableEquipment;
import cn.iocoder.yudao.module.lims.service.workflow.model.CalibrationEvidence;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EquipmentGatewayImpl implements EquipmentGateway {

    @Resource
    private LabEquipmentAssetService equipmentAssetService;
    @Resource
    private LabEquipmentTraceabilityService equipmentTraceabilityService;

    @Override
    public List<AvailableEquipment> getAvailableEquipment(String domainCode, String testItem) {
        return equipmentAssetService.getAvailableEquipment(domainCode, testItem).stream()
                .map(this::toAvailableEquipment)
                .toList();
    }

    @Override
    public List<CalibrationEvidence> getCurrentCalibrationEvidence(Long equipmentId) {
        return equipmentTraceabilityService.getCurrentCalibrationEvidence(equipmentId).stream()
                .map(this::toCalibrationEvidence)
                .toList();
    }

    private AvailableEquipment toAvailableEquipment(LabEquipmentAssetSummaryDTO equipment) {
        return new AvailableEquipment(
                equipment.getId(),
                equipment.getEquipmentCode(),
                equipment.getEquipmentName(),
                equipment.getEquipmentType(),
                equipment.getDomainCode(),
                equipment.getCapabilityScope(),
                equipment.getCalibrationValidUntil() == null ? null : equipment.getCalibrationValidUntil().format(DateTimeFormatter.ISO_LOCAL_DATE),
                equipment.getIotEnabled(),
                equipment.getIotProductId(),
                equipment.getIotDeviceId(),
                equipment.getDataSourceType());
    }

    private CalibrationEvidence toCalibrationEvidence(LabEquipmentCalibrationEvidenceDTO evidence) {
        return new CalibrationEvidence(
                evidence.getId(),
                evidence.getEquipmentId(),
                evidence.getTraceabilityType(),
                evidence.getCertificateNo(),
                evidence.getCalibrationOrg(),
                evidence.getCalibrationDate(),
                evidence.getValidTo(),
                evidence.getResult(),
                evidence.getCertificateFileUrl(),
                evidence.isEffective());
    }

}
