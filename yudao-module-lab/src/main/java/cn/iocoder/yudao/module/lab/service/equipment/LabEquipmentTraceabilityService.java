package cn.iocoder.yudao.module.lab.service.equipment;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.lab.controller.admin.quality.vo.LabQualityRecordPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.quality.vo.LabQualityRecordRespVO;
import cn.iocoder.yudao.module.lab.controller.admin.quality.vo.LabQualityRecordSaveReqVO;
import cn.iocoder.yudao.module.lab.service.equipment.dto.LabEquipmentCalibrationEvidenceDTO;

import java.util.List;

public interface LabEquipmentTraceabilityService {

    Long createEquipmentTraceability(LabQualityRecordSaveReqVO createReqVO);

    void updateEquipmentTraceability(LabQualityRecordSaveReqVO updateReqVO);

    void deleteEquipmentTraceability(Long id);

    LabQualityRecordRespVO getEquipmentTraceability(Long id);

    PageResult<LabQualityRecordRespVO> getEquipmentTraceabilityPage(LabQualityRecordPageReqVO pageReqVO);

    void updateEquipmentTraceabilityStatus(Long id, String status);

    List<LabEquipmentCalibrationEvidenceDTO> getCurrentCalibrationEvidence(Long equipmentId);

}
