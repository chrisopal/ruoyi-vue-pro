package cn.iocoder.yudao.module.lab.service.equipment;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.lab.controller.admin.equipmentasset.vo.LabEquipmentAssetPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.equipmentasset.vo.LabEquipmentAssetSaveReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.equipment.LabEquipmentAssetDO;
import cn.iocoder.yudao.module.lab.service.equipment.dto.LabEquipmentAssetSummaryDTO;

import java.util.List;

public interface LabEquipmentAssetService {

    Long createEquipmentAsset(LabEquipmentAssetSaveReqVO createReqVO);

    void updateEquipmentAsset(LabEquipmentAssetSaveReqVO updateReqVO);

    void deleteEquipmentAsset(Long id);

    LabEquipmentAssetDO getEquipmentAsset(Long id);

    PageResult<LabEquipmentAssetDO> getEquipmentAssetPage(LabEquipmentAssetPageReqVO pageReqVO);

    List<LabEquipmentAssetSummaryDTO> getAvailableEquipment(String domainCode, String testItem);

}
