package cn.iocoder.yudao.module.lab.service.equipment;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.lab.controller.admin.equipmentasset.vo.LabEquipmentAssetSaveReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.equipment.LabEquipmentAssetDO;
import cn.iocoder.yudao.module.lab.dal.mysql.equipment.LabEquipmentAssetMapper;
import cn.iocoder.yudao.module.lab.service.equipment.dto.LabEquipmentAssetSummaryDTO;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LabEquipmentAssetServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private LabEquipmentAssetServiceImpl service;

    @Mock
    private LabEquipmentAssetMapper equipmentAssetMapper;

    @Test
    void createEquipmentAsset_shouldDefaultDraftToEnabledWhenCalibrationValid() {
        when(equipmentAssetMapper.selectByEquipmentCode("ICP-MS-001")).thenReturn(null);
        when(equipmentAssetMapper.insert(any(LabEquipmentAssetDO.class))).thenAnswer(invocation -> {
            LabEquipmentAssetDO asset = invocation.getArgument(0);
            asset.setId(1L);
            return 1;
        });

        Long id = service.createEquipmentAsset(saveReq("draft", LocalDate.now().plusDays(30)));

        assertEquals(1L, id);
        verify(equipmentAssetMapper).insert(argThat((LabEquipmentAssetDO asset) ->
                "enabled".equals(asset.getStatus())
                        && "ICP-MS-001".equals(asset.getEquipmentCode())
                        && Boolean.TRUE.equals(asset.getIotEnabled())));
    }

    @Test
    void getAvailableEquipment_shouldExcludeExpiredAndUnmatchedAssets() {
        when(equipmentAssetMapper.selectEnabledCandidates()).thenReturn(List.of(
                asset(1L, "ICP-MS-001", "FOOD", "lead,cadmium", LocalDate.now().plusDays(30)),
                asset(2L, "PH-001", "FOOD", "ph", LocalDate.now().minusDays(1)),
                asset(3L, "GC-001", "ENV", "voc", LocalDate.now().plusDays(30))));

        List<LabEquipmentAssetSummaryDTO> result = service.getAvailableEquipment("FOOD", "lead");

        assertEquals(1, result.size());
        assertEquals("ICP-MS-001", result.get(0).getEquipmentCode());
    }

    private static LabEquipmentAssetSaveReqVO saveReq(String status, LocalDate calibrationValidUntil) {
        LabEquipmentAssetSaveReqVO reqVO = new LabEquipmentAssetSaveReqVO();
        reqVO.setEquipmentCode("ICP-MS-001");
        reqVO.setEquipmentName("电感耦合等离子体质谱仪");
        reqVO.setEquipmentType("instrument");
        reqVO.setManufacturer("Agilent");
        reqVO.setModel("7900");
        reqVO.setSerialNo("SN-001");
        reqVO.setLabArea("食品理化实验室");
        reqVO.setDomainCode("FOOD");
        reqVO.setCapabilityScope("lead,cadmium");
        reqVO.setResponsibleUserId(100L);
        reqVO.setCalibrationValidUntil(calibrationValidUntil);
        reqVO.setStatus(status);
        reqVO.setIotEnabled(true);
        reqVO.setIotProductId(10L);
        reqVO.setIotDeviceId("iot-device-001");
        reqVO.setDataSourceType("manual");
        reqVO.setRemark("一期设备主档");
        return reqVO;
    }

    private static LabEquipmentAssetDO asset(Long id, String equipmentCode, String domainCode,
                                             String capabilityScope, LocalDate calibrationValidUntil) {
        LabEquipmentAssetDO asset = new LabEquipmentAssetDO();
        asset.setId(id);
        asset.setEquipmentCode(equipmentCode);
        asset.setEquipmentName(equipmentCode);
        asset.setEquipmentType("instrument");
        asset.setDomainCode(domainCode);
        asset.setCapabilityScope(capabilityScope);
        asset.setCalibrationValidUntil(calibrationValidUntil);
        asset.setStatus("enabled");
        return asset;
    }

}
