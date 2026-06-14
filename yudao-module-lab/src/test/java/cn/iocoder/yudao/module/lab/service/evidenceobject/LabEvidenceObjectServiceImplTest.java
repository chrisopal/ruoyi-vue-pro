package cn.iocoder.yudao.module.lab.service.evidenceobject;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.lab.controller.admin.evidenceobject.vo.LabEvidenceObjectSaveReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.evidenceobject.LabEvidenceObjectDO;
import cn.iocoder.yudao.module.lab.dal.mysql.evidenceobject.LabEvidenceObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static cn.iocoder.yudao.module.lab.enums.ErrorCodeConstants.EVIDENCE_OBJECT_CODE_DUPLICATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LabEvidenceObjectServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private LabEvidenceObjectServiceImpl service;

    @Mock
    private LabEvidenceObjectMapper evidenceObjectMapper;

    @Test
    void createEvidenceObject_shouldRejectDuplicateEvidenceCode() {
        LabEvidenceObjectDO existing = new LabEvidenceObjectDO();
        existing.setId(10L);
        when(evidenceObjectMapper.selectByEvidenceCode("EQ-CERT-001")).thenReturn(existing);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.createEvidenceObject(saveReq("EQ-CERT-001")));

        assertEquals(EVIDENCE_OBJECT_CODE_DUPLICATE.getCode(), ex.getCode());
    }

    @Test
    void createEvidenceObject_shouldDefaultEffectiveStatusAndHash() {
        when(evidenceObjectMapper.selectByEvidenceCode("EQ-CERT-001")).thenReturn(null);
        when(evidenceObjectMapper.insert(any(LabEvidenceObjectDO.class))).thenAnswer(invocation -> {
            LabEvidenceObjectDO evidenceObject = invocation.getArgument(0);
            evidenceObject.setId(1L);
            return 1;
        });

        Long id = service.createEvidenceObject(saveReq("EQ-CERT-001"));

        assertEquals(1L, id);
        verify(evidenceObjectMapper).insert(argThat((LabEvidenceObjectDO evidenceObject) ->
                "effective".equals(evidenceObject.getStatus())
                        && evidenceObject.getEvidenceHash() != null
                        && evidenceObject.getEvidenceHash().length() == 64));
    }

    @Test
    void createEvidenceObjectFromDomainObject_shouldGenerateHash() {
        when(evidenceObjectMapper.selectByEvidenceCode("EQ-CERT-002")).thenReturn(null);
        when(evidenceObjectMapper.insert(any(LabEvidenceObjectDO.class))).thenAnswer(invocation -> {
            LabEvidenceObjectDO evidenceObject = invocation.getArgument(0);
            evidenceObject.setId(2L);
            return 1;
        });
        LabEvidenceObjectDO evidenceObject = new LabEvidenceObjectDO();
        evidenceObject.setEvidenceCode("EQ-CERT-002");
        evidenceObject.setEvidenceName("校准证书");
        evidenceObject.setEvidenceType("EQUIPMENT_CERTIFICATE");
        evidenceObject.setSourceObject("lab_equipment_traceability");
        evidenceObject.setBusinessDomain("equipment");

        Long id = service.createEvidenceObject(evidenceObject);

        assertEquals(2L, id);
        assertNotNull(evidenceObject.getEvidenceHash());
        assertEquals("effective", evidenceObject.getStatus());
    }

    private static LabEvidenceObjectSaveReqVO saveReq(String evidenceCode) {
        LabEvidenceObjectSaveReqVO reqVO = new LabEvidenceObjectSaveReqVO();
        reqVO.setEvidenceCode(evidenceCode);
        reqVO.setEvidenceName("设备校准证书");
        reqVO.setEvidenceType("EQUIPMENT_CERTIFICATE");
        reqVO.setSourceObject("lab_equipment_traceability");
        reqVO.setSourceObjectNo("CERT-001");
        reqVO.setBusinessDomain("equipment");
        reqVO.setFileUrl("https://example.test/cert.pdf");
        return reqVO;
    }

}
