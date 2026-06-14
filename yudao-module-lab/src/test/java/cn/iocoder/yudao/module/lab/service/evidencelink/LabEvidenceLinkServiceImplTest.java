package cn.iocoder.yudao.module.lab.service.evidencelink;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.lab.controller.admin.evidencelink.vo.LabEvidenceLinkSaveReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.evidenceobject.LabEvidenceObjectDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.evidencelink.LabEvidenceLinkDO;
import cn.iocoder.yudao.module.lab.dal.mysql.evidencelink.LabEvidenceLinkMapper;
import cn.iocoder.yudao.module.lab.service.evidenceobject.LabEvidenceObjectService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static cn.iocoder.yudao.module.lab.enums.ErrorCodeConstants.EVIDENCE_LINK_EVIDENCE_REQUIRED;
import static cn.iocoder.yudao.module.lab.enums.ErrorCodeConstants.EVIDENCE_LINK_SOURCE_REQUIRED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LabEvidenceLinkServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private LabEvidenceLinkServiceImpl service;

    @Mock
    private LabEvidenceLinkMapper evidenceLinkMapper;
    @Mock
    private LabEvidenceObjectService evidenceObjectService;

    @Test
    void createEvidenceLink_shouldFillEvidenceMetadataFromObject() {
        when(evidenceObjectService.getEvidenceObjectOrThrow(501L)).thenReturn(evidenceObject());
        when(evidenceLinkMapper.insert(any(LabEvidenceLinkDO.class))).thenAnswer(invocation -> {
            LabEvidenceLinkDO link = invocation.getArgument(0);
            link.setId(1L);
            return 1;
        });

        Long id = service.createEvidenceLink(linkReqWithObject());

        assertEquals(1L, id);
        verify(evidenceLinkMapper).insert(argThat((LabEvidenceLinkDO link) ->
                Long.valueOf(501L).equals(link.getEvidenceObjectId())
                        && "EQ-CERT-100-1".equals(link.getEvidenceCode())
                        && "设备校准证书".equals(link.getEvidenceName())
                        && "https://example.test/cert.pdf".equals(link.getEvidenceUrl())
                        && "abc123".equals(link.getEvidenceHash())
                        && "lab_equipment_traceability".equals(link.getSourceObject())
                        && Long.valueOf(1L).equals(link.getSourceObjectId())
                        && "CERT-001".equals(link.getSourceObjectNo())));
    }

    @Test
    void createEvidenceLink_shouldRequireObjectOrEvidenceCode() {
        LabEvidenceLinkSaveReqVO reqVO = linkReqWithObject();
        reqVO.setEvidenceObjectId(null);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.createEvidenceLink(reqVO));

        assertEquals(EVIDENCE_LINK_EVIDENCE_REQUIRED.getCode(), ex.getCode());
    }

    @Test
    void createEvidenceLink_shouldRequireSourceObjectInLegacyMode() {
        LabEvidenceLinkSaveReqVO reqVO = linkReqWithObject();
        reqVO.setEvidenceObjectId(null);
        reqVO.setEvidenceCode("RAW_DATA");

        ServiceException ex = assertThrows(ServiceException.class, () -> service.createEvidenceLink(reqVO));

        assertEquals(EVIDENCE_LINK_SOURCE_REQUIRED.getCode(), ex.getCode());
    }

    private static LabEvidenceLinkSaveReqVO linkReqWithObject() {
        LabEvidenceLinkSaveReqVO reqVO = new LabEvidenceLinkSaveReqVO();
        reqVO.setEvidenceObjectId(501L);
        reqVO.setLinkedBizType("equipment_asset");
        reqVO.setLinkedBizId(100L);
        reqVO.setLinkedBizNo("EQ-100");
        reqVO.setClauseCategory("equipment");
        reqVO.setLinkStatus("linked");
        return reqVO;
    }

    private static LabEvidenceObjectDO evidenceObject() {
        LabEvidenceObjectDO evidenceObject = new LabEvidenceObjectDO();
        evidenceObject.setId(501L);
        evidenceObject.setEvidenceCode("EQ-CERT-100-1");
        evidenceObject.setEvidenceName("设备校准证书");
        evidenceObject.setEvidenceType("EQUIPMENT_CERTIFICATE");
        evidenceObject.setSourceObject("lab_equipment_traceability");
        evidenceObject.setSourceObjectId(1L);
        evidenceObject.setSourceObjectNo("CERT-001");
        evidenceObject.setBusinessDomain("equipment");
        evidenceObject.setFileUrl("https://example.test/cert.pdf");
        evidenceObject.setEvidenceHash("abc123");
        return evidenceObject;
    }

}
