package cn.iocoder.yudao.module.lab.service.equipment;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.lab.controller.admin.evidencelink.vo.LabEvidenceLinkSaveReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.quality.vo.LabQualityRecordSaveReqVO;
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
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static cn.iocoder.yudao.module.lab.enums.ErrorCodeConstants.EQUIPMENT_ASSET_NOT_EXISTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LabEquipmentTraceabilityServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private LabEquipmentTraceabilityServiceImpl service;

    @Mock
    private LabEquipmentTraceabilityMapper traceabilityMapper;
    @Mock
    private LabEquipmentAssetMapper equipmentAssetMapper;
    @Mock
    private LabEvidenceObjectService evidenceObjectService;
    @Mock
    private LabEvidenceLinkService evidenceLinkService;
    @Mock
    private LabStandardClauseMapper standardClauseMapper;

    @Test
    void createEquipmentTraceability_shouldRejectMissingEquipmentAsset() {
        when(equipmentAssetMapper.selectById(100L)).thenReturn(null);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.createEquipmentTraceability(saveReq(100L, "CERT-001", null)));

        assertEquals(EQUIPMENT_ASSET_NOT_EXISTS.getCode(), ex.getCode());
    }

    @Test
    void createEquipmentTraceability_shouldBindToExistingAssetAndDefaultValidStatus() {
        LabEquipmentAssetDO equipmentAsset = equipmentAsset();
        when(equipmentAssetMapper.selectById(100L)).thenReturn(equipmentAsset);
        when(traceabilityMapper.insert(any(LabEquipmentTraceabilityDO.class))).thenAnswer(invocation -> {
            LabEquipmentTraceabilityDO evidence = invocation.getArgument(0);
            evidence.setId(1L);
            return 1;
        });
        when(evidenceObjectService.createEvidenceObject(any(LabEvidenceObjectDO.class))).thenReturn(501L);
        LabStandardClauseDO equipmentClause = new LabStandardClauseDO();
        equipmentClause.setId(64L);
        equipmentClause.setClauseCategory("equipment");
        when(standardClauseMapper.selectFirstEquipmentClause()).thenReturn(equipmentClause);

        Long id = service.createEquipmentTraceability(saveReq(100L, "CERT-001", null));

        assertEquals(1L, id);
        verify(traceabilityMapper).insert(argThat((LabEquipmentTraceabilityDO evidence) ->
                Long.valueOf(100L).equals(evidence.getEquipmentId())
                        && "calibration".equals(evidence.getTraceabilityType())
                        && "valid".equals(evidence.getStatus())));
        verify(evidenceObjectService).createEvidenceObject(argThat((LabEvidenceObjectDO evidenceObject) ->
                "EQ-CERT-100-1".equals(evidenceObject.getEvidenceCode())
                        && "EQUIPMENT_CERTIFICATE".equals(evidenceObject.getEvidenceType())
                        && "lab_equipment_traceability".equals(evidenceObject.getSourceObject())
                        && Long.valueOf(1L).equals(evidenceObject.getSourceObjectId())
                        && "equipment".equals(evidenceObject.getBusinessDomain())
                        && "CERT-001".equals(evidenceObject.getSourceObjectNo())));
        verify(evidenceLinkService).createEvidenceLink(argThat((LabEvidenceLinkSaveReqVO link) ->
                Long.valueOf(501L).equals(link.getEvidenceObjectId())
                        && "equipment_asset".equals(link.getLinkedBizType())
                        && Long.valueOf(100L).equals(link.getLinkedBizId())
                        && "EQ-100".equals(link.getLinkedBizNo())
                        && Long.valueOf(64L).equals(link.getClauseId())
                        && "equipment".equals(link.getClauseCategory())));
    }

    @Test
    void getCurrentCalibrationEvidence_shouldExcludeExpiredEvidence() {
        when(equipmentAssetMapper.selectById(100L)).thenReturn(new LabEquipmentAssetDO());
        when(traceabilityMapper.selectByEquipmentId(100L)).thenReturn(List.of(
                evidence(1L, "CERT-OLD", "2025-01-01", "valid"),
                evidence(2L, "CERT-NEW", "2099-12-31", "valid"),
                evidence(3L, "CERT-DRAFT", "2099-12-31", "draft")));

        List<LabEquipmentCalibrationEvidenceDTO> result = service.getCurrentCalibrationEvidence(100L);

        assertEquals(1, result.size());
        assertEquals("CERT-NEW", result.get(0).getCertificateNo());
        assertTrue(result.get(0).isEffective());
    }

    private static LabQualityRecordSaveReqVO saveReq(Long equipmentId, String certificateNo, String status) {
        LabQualityRecordSaveReqVO reqVO = new LabQualityRecordSaveReqVO();
        reqVO.setEquipmentId(equipmentId);
        reqVO.setCertificateNo(certificateNo);
        reqVO.setCalibrationOrg("省计量院");
        reqVO.setCalibrationDate("2026-01-01");
        reqVO.setValidTo("2099-12-31");
        reqVO.setResult("合格");
        reqVO.setCertificateFileUrl("https://example.test/cert.pdf");
        reqVO.setStatus(status);
        return reqVO;
    }

    private static LabEquipmentAssetDO equipmentAsset() {
        LabEquipmentAssetDO asset = new LabEquipmentAssetDO();
        asset.setId(100L);
        asset.setEquipmentCode("EQ-100");
        asset.setEquipmentName("酸度计");
        return asset;
    }

    private static LabEquipmentTraceabilityDO evidence(Long id, String certificateNo, String validTo, String status) {
        LabEquipmentTraceabilityDO evidence = new LabEquipmentTraceabilityDO();
        evidence.setId(id);
        evidence.setEquipmentId(100L);
        evidence.setTraceabilityType("calibration");
        evidence.setCertificateNo(certificateNo);
        evidence.setValidTo(validTo);
        evidence.setStatus(status);
        return evidence;
    }

}
