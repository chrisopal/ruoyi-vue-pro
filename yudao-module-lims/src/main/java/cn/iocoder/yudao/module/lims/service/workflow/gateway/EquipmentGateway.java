package cn.iocoder.yudao.module.lims.service.workflow.gateway;

import cn.iocoder.yudao.module.lims.service.workflow.model.AvailableEquipment;
import cn.iocoder.yudao.module.lims.service.workflow.model.CalibrationEvidence;

import java.util.List;

public interface EquipmentGateway {

    List<AvailableEquipment> getAvailableEquipment(String domainCode, String testItem);

    List<CalibrationEvidence> getCurrentCalibrationEvidence(Long equipmentId);

}
