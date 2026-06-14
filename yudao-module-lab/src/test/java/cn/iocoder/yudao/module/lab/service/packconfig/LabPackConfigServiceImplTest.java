package cn.iocoder.yudao.module.lab.service.packconfig;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.lab.controller.admin.packconfig.vo.LabPackConfigRespVO;
import cn.iocoder.yudao.module.lab.controller.admin.packconfig.vo.LabPackConfigSaveReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.domainpack.LabDomainPackDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.packconfig.LabPackEvidenceRequirementDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.packconfig.LabPackQcRuleDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.packconfig.LabPackSampleRequirementDO;
import cn.iocoder.yudao.module.lab.dal.mysql.domainpack.LabDomainPackMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.packconfig.LabPackEvidenceRequirementMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.packconfig.LabPackQcRuleMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.packconfig.LabPackReportSectionMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.packconfig.LabPackResultFieldMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.packconfig.LabPackSampleRequirementMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.packconfig.LabPackTestItemMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.packconfig.LabPackWorkflowNodeMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.List;

import static cn.iocoder.yudao.module.lab.enums.ErrorCodeConstants.DOMAIN_PACK_PUBLISHED_IMMUTABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class LabPackConfigServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private LabPackConfigServiceImpl service;

    @Mock
    private LabDomainPackMapper domainPackMapper;
    @Mock
    private LabPackWorkflowNodeMapper workflowNodeMapper;
    @Mock
    private LabPackTestItemMapper testItemMapper;
    @Mock
    private LabPackResultFieldMapper resultFieldMapper;
    @Mock
    private LabPackReportSectionMapper reportSectionMapper;
    @Mock
    private LabPackSampleRequirementMapper sampleRequirementMapper;
    @Mock
    private LabPackQcRuleMapper qcRuleMapper;
    @Mock
    private LabPackEvidenceRequirementMapper evidenceRequirementMapper;
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void savePackConfig_shouldPersistSampleQcEvidenceConfig() {
        when(domainPackMapper.selectById(1L)).thenReturn(domainPack("draft"));
        LabPackConfigSaveReqVO reqVO = new LabPackConfigSaveReqVO();
        reqVO.setDomainPackId(1L);
        reqVO.setSampleRequirements(List.of(sampleRequirement("SAMPLE_QTY", "样品量", ">= 500g")));
        reqVO.setQcRules(List.of(qcRule("BLANK", "空白样", "每批至少 1 个")));
        reqVO.setEvidenceRequirements(List.of(evidenceRequirement("EQUIPMENT_CERT", "设备校准证书", "equipment")));

        service.savePackConfig(reqVO);

        verify(sampleRequirementMapper).insert(argThat((LabPackSampleRequirementDO item) ->
                Long.valueOf(1L).equals(item.getDomainPackId())
                        && "SAMPLE_QTY".equals(item.getRequirementCode())
                        && "样品量".equals(item.getRequirementName())
                        && ">= 500g".equals(item.getRequirementText())));
        verify(qcRuleMapper).insert(argThat((LabPackQcRuleDO item) ->
                Long.valueOf(1L).equals(item.getDomainPackId())
                        && "BLANK".equals(item.getRuleCode())
                        && "每批至少 1 个".equals(item.getAcceptanceCriteria())));
        verify(evidenceRequirementMapper).insert(argThat((LabPackEvidenceRequirementDO item) ->
                Long.valueOf(1L).equals(item.getDomainPackId())
                        && "EQUIPMENT_CERT".equals(item.getRequirementCode())
                        && Boolean.TRUE.equals(item.getRequiredFlag())));
    }

    @Test
    void getPackConfig_shouldReturnSampleQcEvidenceConfig() {
        when(domainPackMapper.selectById(1L)).thenReturn(domainPack("published"));
        when(sampleRequirementMapper.selectListByDomainPackId(1L)).thenReturn(List.of(sampleRequirementDO()));
        when(qcRuleMapper.selectListByDomainPackId(1L)).thenReturn(List.of(qcRuleDO()));
        when(evidenceRequirementMapper.selectListByDomainPackId(1L)).thenReturn(List.of(evidenceRequirementDO()));

        LabPackConfigRespVO config = service.getPackConfig(1L);

        assertEquals("SAMPLE_QTY", config.getSampleRequirements().get(0).getRequirementCode());
        assertEquals("BLANK", config.getQcRules().get(0).getRuleCode());
        assertEquals("EQUIPMENT_CERT", config.getEvidenceRequirements().get(0).getRequirementCode());
    }

    @Test
    void savePackConfig_shouldRejectPublishedPack() {
        when(domainPackMapper.selectById(1L)).thenReturn(domainPack("published"));
        LabPackConfigSaveReqVO reqVO = new LabPackConfigSaveReqVO();
        reqVO.setDomainPackId(1L);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.savePackConfig(reqVO));

        assertEquals(DOMAIN_PACK_PUBLISHED_IMMUTABLE.getCode(), ex.getCode());
        verifyNoInteractions(sampleRequirementMapper, qcRuleMapper, evidenceRequirementMapper);
    }

    private static LabPackConfigSaveReqVO.SampleRequirement sampleRequirement(String code, String name, String text) {
        LabPackConfigSaveReqVO.SampleRequirement item = new LabPackConfigSaveReqVO.SampleRequirement();
        item.setRequirementCode(code);
        item.setRequirementName(name);
        item.setRequirementType("quantity");
        item.setRequirementText(text);
        item.setSort(10);
        item.setStatus("active");
        return item;
    }

    private static LabPackConfigSaveReqVO.QcRule qcRule(String code, String name, String criteria) {
        LabPackConfigSaveReqVO.QcRule item = new LabPackConfigSaveReqVO.QcRule();
        item.setRuleCode(code);
        item.setRuleName(name);
        item.setRuleType("batch");
        item.setRuleExpression("batch.count >= 1");
        item.setAcceptanceCriteria(criteria);
        item.setSort(20);
        item.setStatus("active");
        return item;
    }

    private static LabPackConfigSaveReqVO.EvidenceRequirement evidenceRequirement(String code, String name, String sourceType) {
        LabPackConfigSaveReqVO.EvidenceRequirement item = new LabPackConfigSaveReqVO.EvidenceRequirement();
        item.setRequirementCode(code);
        item.setRequirementName(name);
        item.setEvidenceType("EQUIPMENT_CERTIFICATE");
        item.setSourceType(sourceType);
        item.setClauseCategory("equipment");
        item.setRequired(true);
        item.setSort(30);
        item.setStatus("active");
        return item;
    }

    private static LabPackSampleRequirementDO sampleRequirementDO() {
        LabPackSampleRequirementDO item = new LabPackSampleRequirementDO();
        item.setRequirementCode("SAMPLE_QTY");
        item.setRequirementName("样品量");
        item.setRequirementType("quantity");
        item.setRequirementText(">= 500g");
        item.setSort(10);
        item.setStatus("active");
        return item;
    }

    private static LabPackQcRuleDO qcRuleDO() {
        LabPackQcRuleDO item = new LabPackQcRuleDO();
        item.setRuleCode("BLANK");
        item.setRuleName("空白样");
        item.setRuleType("batch");
        item.setRuleExpression("batch.count >= 1");
        item.setAcceptanceCriteria("每批至少 1 个");
        item.setSort(20);
        item.setStatus("active");
        return item;
    }

    private static LabPackEvidenceRequirementDO evidenceRequirementDO() {
        LabPackEvidenceRequirementDO item = new LabPackEvidenceRequirementDO();
        item.setRequirementCode("EQUIPMENT_CERT");
        item.setRequirementName("设备校准证书");
        item.setEvidenceType("EQUIPMENT_CERTIFICATE");
        item.setSourceType("equipment");
        item.setClauseCategory("equipment");
        item.setRequiredFlag(true);
        item.setSort(30);
        item.setStatus("active");
        return item;
    }

    private static LabDomainPackDO domainPack(String status) {
        LabDomainPackDO domainPack = new LabDomainPackDO();
        domainPack.setId(1L);
        domainPack.setPackCode("FOOD_ROUTINE");
        domainPack.setPackVersion("1.0");
        domainPack.setStatus(status);
        return domainPack;
    }

}
