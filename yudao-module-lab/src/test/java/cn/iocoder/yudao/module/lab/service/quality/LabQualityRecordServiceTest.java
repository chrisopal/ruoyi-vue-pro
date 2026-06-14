package cn.iocoder.yudao.module.lab.service.quality;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.lab.controller.admin.evidencelink.vo.LabEvidenceLinkSaveReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.quality.vo.LabQualityRecordSaveReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.evidenceobject.LabEvidenceObjectDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.personnel.LabPersonnelAuthorizationDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.personnel.LabPersonnelCompetenceDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.standard.LabStandardClauseDO;
import cn.iocoder.yudao.module.lab.dal.mysql.personnel.LabPersonnelAuthorizationMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.personnel.LabPersonnelCompetenceMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.standard.LabStandardClauseMapper;
import cn.iocoder.yudao.module.lab.service.evidencelink.LabEvidenceLinkService;
import cn.iocoder.yudao.module.lab.service.evidenceobject.LabEvidenceObjectService;
import cn.iocoder.yudao.module.lab.service.quality.dto.LabPersonnelAuthorizationSummaryDTO;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LabQualityRecordServiceTest extends BaseMockitoUnitTest {

    @InjectMocks
    private LabQualityRecordService service;

    @Mock
    private LabPersonnelAuthorizationMapper personnelAuthorizationMapper;
    @Mock
    private LabPersonnelCompetenceMapper personnelCompetenceMapper;
    @Mock
    private LabEvidenceObjectService evidenceObjectService;
    @Mock
    private LabEvidenceLinkService evidenceLinkService;
    @Mock
    private LabStandardClauseMapper standardClauseMapper;

    @Test
    void createPersonnelAuthorization_shouldCreateEvidenceChainAndDefaultActive() {
        when(personnelAuthorizationMapper.insert(any(LabPersonnelAuthorizationDO.class))).thenAnswer(invocation -> {
            LabPersonnelAuthorizationDO authorization = invocation.getArgument(0);
            authorization.setId(200L);
            return 1;
        });
        when(evidenceObjectService.createEvidenceObject(any(LabEvidenceObjectDO.class))).thenReturn(501L);
        LabStandardClauseDO personnelClause = new LabStandardClauseDO();
        personnelClause.setId(64L);
        when(standardClauseMapper.selectFirstByClauseCategory("personnel")).thenReturn(personnelClause);

        Long id = service.createPersonnelAuthorization(authorizationReq());

        assertEquals(200L, id);
        verify(personnelAuthorizationMapper).insert(argThat((LabPersonnelAuthorizationDO authorization) ->
                Long.valueOf(99L).equals(authorization.getUserId())
                        && "pH".equals(authorization.getAuthScope())
                        && "active".equals(authorization.getStatus())));
        verify(evidenceObjectService).createEvidenceObject(argThat((LabEvidenceObjectDO evidenceObject) ->
                "PERSON-AUTH-99-200".equals(evidenceObject.getEvidenceCode())
                        && "PERSON_AUTH".equals(evidenceObject.getEvidenceType())
                        && "lab_personnel_authorization".equals(evidenceObject.getSourceObject())
                        && Long.valueOf(200L).equals(evidenceObject.getSourceObjectId())
                        && "personnel".equals(evidenceObject.getBusinessDomain())));
        verify(evidenceLinkService).createEvidenceLink(argThat((LabEvidenceLinkSaveReqVO link) ->
                Long.valueOf(501L).equals(link.getEvidenceObjectId())
                        && "personnel_user".equals(link.getLinkedBizType())
                        && Long.valueOf(99L).equals(link.getLinkedBizId())
                        && Long.valueOf(64L).equals(link.getClauseId())
                        && "personnel".equals(link.getClauseCategory())));
    }

    @Test
    void getAvailablePersonnel_shouldRequireActiveAuthorizationAndCompetence() {
        when(personnelAuthorizationMapper.selectListForAvailability()).thenReturn(List.of(
                authorization(200L, 99L, "pH", "active", "2099-12-31"),
                authorization(201L, 100L, "pH", "revoked", "2099-12-31")));
        when(personnelCompetenceMapper.selectListByUserIds(List.of(99L))).thenReturn(List.of(
                competence(300L, 99L, "张三", "pH", "active", "2099-12-31")));

        List<LabPersonnelAuthorizationSummaryDTO> result = service.getAvailablePersonnel("pH", null, null);

        assertEquals(1, result.size());
        assertEquals(99L, result.get(0).getUserId());
        assertEquals("张三", result.get(0).getUserName());
        assertEquals(200L, result.get(0).getAuthorizationId());
        assertEquals(300L, result.get(0).getCompetenceId());
        assertTrue(result.get(0).isEffective());
    }

    private static LabQualityRecordSaveReqVO authorizationReq() {
        LabQualityRecordSaveReqVO reqVO = new LabQualityRecordSaveReqVO();
        reqVO.setUserId(99L);
        reqVO.setAuthType("testing");
        reqVO.setAuthScope("pH");
        reqVO.setAuthorizedBy(1L);
        reqVO.setAuthorizedTime("2026-06-15");
        reqVO.setValidFrom("2026-06-15");
        reqVO.setValidTo("2099-12-31");
        reqVO.setFileUrl("https://example.test/person-auth.pdf");
        reqVO.setStatus("draft");
        return reqVO;
    }

    private static LabPersonnelAuthorizationDO authorization(Long id, Long userId, String authScope,
                                                             String status, String validTo) {
        LabPersonnelAuthorizationDO authorization = new LabPersonnelAuthorizationDO();
        authorization.setId(id);
        authorization.setUserId(userId);
        authorization.setAuthType("testing");
        authorization.setAuthScope(authScope);
        authorization.setValidFrom("2026-06-15");
        authorization.setValidTo(validTo);
        authorization.setStatus(status);
        return authorization;
    }

    private static LabPersonnelCompetenceDO competence(Long id, Long userId, String userName, String competenceItem,
                                                       String status, String validTo) {
        LabPersonnelCompetenceDO competence = new LabPersonnelCompetenceDO();
        competence.setId(id);
        competence.setUserId(userId);
        competence.setUserName(userName);
        competence.setCompetenceType("testing");
        competence.setCompetenceItem(competenceItem);
        competence.setCertificateNo("COMP-001");
        competence.setValidFrom("2026-06-15");
        competence.setValidTo(validTo);
        competence.setAssessmentResult("合格");
        competence.setStatus(status);
        return competence;
    }

}
