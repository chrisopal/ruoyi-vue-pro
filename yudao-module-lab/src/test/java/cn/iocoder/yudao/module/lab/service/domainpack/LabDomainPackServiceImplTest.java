package cn.iocoder.yudao.module.lab.service.domainpack;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.lab.controller.admin.domainpack.vo.LabDomainPackSaveReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.domain.LabDomainProfileDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.domainpack.LabDomainPackDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.packconfig.LabPackEvidenceRequirementDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.packconfig.LabPackQcRuleDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.packconfig.LabPackReportSectionDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.packconfig.LabPackResultFieldDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.packconfig.LabPackSampleRequirementDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.packconfig.LabPackTestItemDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.packconfig.LabPackWorkflowNodeDO;
import cn.iocoder.yudao.module.lab.dal.mysql.domainpack.LabDomainPackMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.packconfig.LabPackEvidenceRequirementMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.packconfig.LabPackQcRuleMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.packconfig.LabPackReportSectionMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.packconfig.LabPackResultFieldMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.packconfig.LabPackSampleRequirementMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.packconfig.LabPackTestItemMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.packconfig.LabPackWorkflowNodeMapper;
import cn.iocoder.yudao.module.lab.service.domain.LabDomainProfileService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static cn.iocoder.yudao.module.lab.enums.ErrorCodeConstants.DOMAIN_PACK_PUBLISHED_IMMUTABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LabDomainPackServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private LabDomainPackServiceImpl service;

    @Mock
    private LabDomainPackMapper domainPackMapper;
    @Mock
    private LabDomainProfileService domainProfileService;
    @Mock
    private LabPackWorkflowNodeMapper workflowNodeMapper;
    @Mock
    private LabPackSampleRequirementMapper sampleRequirementMapper;
    @Mock
    private LabPackTestItemMapper testItemMapper;
    @Mock
    private LabPackResultFieldMapper resultFieldMapper;
    @Mock
    private LabPackQcRuleMapper qcRuleMapper;
    @Mock
    private LabPackReportSectionMapper reportSectionMapper;
    @Mock
    private LabPackEvidenceRequirementMapper evidenceRequirementMapper;

    @Test
    void updateDomainPack_shouldRejectPublishedPack() {
        LabDomainPackDO existing = domainPack(1L, "FOOD_ROUTINE", "1.0", "published");
        when(domainPackMapper.selectById(1L)).thenReturn(existing);

        LabDomainPackSaveReqVO reqVO = saveReq(1L, "FOOD_ROUTINE", "1.0", "published");

        ServiceException ex = assertThrows(ServiceException.class, () -> service.updateDomainPack(reqVO));

        assertEquals(DOMAIN_PACK_PUBLISHED_IMMUTABLE.getCode(), ex.getCode());
    }

    @Test
    void publishDomainPack_shouldMoveDraftToPublished() {
        LabDomainPackDO existing = domainPack(1L, "FOOD_ROUTINE", "1.0", "draft");
        when(domainPackMapper.selectById(1L)).thenReturn(existing);

        service.publishDomainPack(1L);

        verify(domainPackMapper).updateById(argThat((LabDomainPackDO pack) -> "published".equals(pack.getStatus())));
    }

    @Test
    void copyDomainPackVersion_shouldCreateDraftVersion() {
        LabDomainPackDO existing = domainPack(1L, "FOOD_ROUTINE", "1.0", "published");
        when(domainPackMapper.selectById(1L)).thenReturn(existing);
        when(domainPackMapper.selectByPackCodeAndVersion("FOOD_ROUTINE", "1.1")).thenReturn(null);
        when(domainPackMapper.insert(any(LabDomainPackDO.class))).thenAnswer(invocation -> {
            LabDomainPackDO inserted = invocation.getArgument(0);
            inserted.setId(2L);
            return 1;
        });
        when(workflowNodeMapper.selectListByDomainPackId(1L)).thenReturn(List.of(workflowNode()));
        when(sampleRequirementMapper.selectListByDomainPackId(1L)).thenReturn(List.of(sampleRequirement()));
        when(testItemMapper.selectListByDomainPackId(1L)).thenReturn(List.of(testItem()));
        when(resultFieldMapper.selectListByDomainPackId(1L)).thenReturn(List.of(resultField()));
        when(qcRuleMapper.selectListByDomainPackId(1L)).thenReturn(List.of(qcRule()));
        when(reportSectionMapper.selectListByDomainPackId(1L)).thenReturn(List.of(reportSection()));
        when(evidenceRequirementMapper.selectListByDomainPackId(1L)).thenReturn(List.of(evidenceRequirement()));

        Long copiedId = service.copyDomainPackVersion(1L, "1.1");

        assertNotNull(copiedId);
        verify(domainPackMapper).insert(argThat((LabDomainPackDO pack) ->
                "FOOD_ROUTINE".equals(pack.getPackCode())
                        && "1.1".equals(pack.getPackVersion())
                        && "draft".equals(pack.getStatus())));
        verify(workflowNodeMapper).insert(argThat((LabPackWorkflowNodeDO row) ->
                row.getId() == null && Long.valueOf(2L).equals(row.getDomainPackId())
                        && "sample_receive".equals(row.getNodeCode())));
        verify(sampleRequirementMapper).insert(argThat((LabPackSampleRequirementDO row) ->
                row.getId() == null && Long.valueOf(2L).equals(row.getDomainPackId())
                        && "SAMPLE_QTY".equals(row.getRequirementCode())));
        verify(testItemMapper).insert(argThat((LabPackTestItemDO row) ->
                row.getId() == null && Long.valueOf(2L).equals(row.getDomainPackId())
                        && "PH".equals(row.getItemCode())));
        verify(resultFieldMapper).insert(argThat((LabPackResultFieldDO row) ->
                row.getId() == null && Long.valueOf(2L).equals(row.getDomainPackId())
                        && "PH_VALUE".equals(row.getFieldCode())));
        verify(qcRuleMapper).insert(argThat((LabPackQcRuleDO row) ->
                row.getId() == null && Long.valueOf(2L).equals(row.getDomainPackId())
                        && "BLANK".equals(row.getRuleCode())));
        verify(reportSectionMapper).insert(argThat((LabPackReportSectionDO row) ->
                row.getId() == null && Long.valueOf(2L).equals(row.getDomainPackId())
                        && "RESULTS".equals(row.getSectionCode())));
        verify(evidenceRequirementMapper).insert(argThat((LabPackEvidenceRequirementDO row) ->
                row.getId() == null && Long.valueOf(2L).equals(row.getDomainPackId())
                        && "EQUIPMENT_CERT".equals(row.getRequirementCode())));
    }

    @Test
    void updateDomainPack_shouldAllowDraftPack() {
        LabDomainPackDO existing = domainPack(1L, "FOOD_ROUTINE", "1.0", "draft");
        when(domainPackMapper.selectById(1L)).thenReturn(existing);
        when(domainProfileService.getDomainProfile(10L)).thenReturn(new LabDomainProfileDO());
        when(domainPackMapper.selectByPackCodeAndVersion("FOOD_ROUTINE", "1.0")).thenReturn(existing);

        service.updateDomainPack(saveReq(1L, "FOOD_ROUTINE", "1.0", "draft"));

        verify(domainPackMapper).updateById(argThat((LabDomainPackDO pack) -> Long.valueOf(1L).equals(pack.getId())
                && "FOOD_ROUTINE".equals(pack.getPackCode())
                && "1.0".equals(pack.getPackVersion())
                && "draft".equals(pack.getStatus())));
    }

    private static LabDomainPackSaveReqVO saveReq(Long id, String packCode, String packVersion, String status) {
        LabDomainPackSaveReqVO reqVO = new LabDomainPackSaveReqVO();
        reqVO.setId(id);
        reqVO.setDomainId(10L);
        reqVO.setPackCode(packCode);
        reqVO.setPackName("食品常规检测方案包");
        reqVO.setPackVersion(packVersion);
        reqVO.setIndustry("食品");
        reqVO.setApplicationScope("食品理化与微生物常规项目");
        reqVO.setWorkflowSchema("{\"stages\":[\"request\",\"sample\",\"task\",\"report\"]}");
        reqVO.setTemplateSchema("{\"templates\":[\"report\"]}");
        reqVO.setStatus(status);
        return reqVO;
    }

    private static LabDomainPackDO domainPack(Long id, String packCode, String packVersion, String status) {
        LabDomainPackDO domainPack = new LabDomainPackDO();
        domainPack.setId(id);
        domainPack.setDomainId(10L);
        domainPack.setPackCode(packCode);
        domainPack.setPackName("食品常规检测方案包");
        domainPack.setPackVersion(packVersion);
        domainPack.setIndustry("食品");
        domainPack.setApplicationScope("食品理化与微生物常规项目");
        domainPack.setWorkflowSchema("{\"stages\":[\"request\",\"sample\",\"task\",\"report\"]}");
        domainPack.setTemplateSchema("{\"templates\":[\"report\"]}");
        domainPack.setStatus(status);
        return domainPack;
    }

    private static LabPackWorkflowNodeDO workflowNode() {
        LabPackWorkflowNodeDO row = new LabPackWorkflowNodeDO();
        row.setId(101L);
        row.setDomainPackId(1L);
        row.setNodeCode("sample_receive");
        row.setNodeName("样品接收");
        row.setRequiredFlag(true);
        row.setSort(10);
        row.setStatus("active");
        return row;
    }

    private static LabPackSampleRequirementDO sampleRequirement() {
        LabPackSampleRequirementDO row = new LabPackSampleRequirementDO();
        row.setId(102L);
        row.setDomainPackId(1L);
        row.setRequirementCode("SAMPLE_QTY");
        row.setRequirementName("样品量");
        row.setRequirementText(">= 500g");
        row.setSort(10);
        row.setStatus("active");
        return row;
    }

    private static LabPackTestItemDO testItem() {
        LabPackTestItemDO row = new LabPackTestItemDO();
        row.setId(103L);
        row.setDomainPackId(1L);
        row.setItemCode("PH");
        row.setItemName("pH");
        row.setMethodCode("GB6920");
        row.setMethodName("玻璃电极法");
        row.setSort(10);
        row.setStatus("active");
        return row;
    }

    private static LabPackResultFieldDO resultField() {
        LabPackResultFieldDO row = new LabPackResultFieldDO();
        row.setId(104L);
        row.setDomainPackId(1L);
        row.setItemCode("PH");
        row.setFieldCode("PH_VALUE");
        row.setFieldName("pH值");
        row.setFieldType("number");
        row.setRequiredFlag(true);
        row.setSort(10);
        row.setStatus("active");
        return row;
    }

    private static LabPackQcRuleDO qcRule() {
        LabPackQcRuleDO row = new LabPackQcRuleDO();
        row.setId(105L);
        row.setDomainPackId(1L);
        row.setRuleCode("BLANK");
        row.setRuleName("空白样");
        row.setAcceptanceCriteria("每批至少 1 个");
        row.setSort(10);
        row.setStatus("active");
        return row;
    }

    private static LabPackReportSectionDO reportSection() {
        LabPackReportSectionDO row = new LabPackReportSectionDO();
        row.setId(106L);
        row.setDomainPackId(1L);
        row.setSectionCode("RESULTS");
        row.setSectionName("检测结果");
        row.setSourceType("result_values");
        row.setVisibleFlag(true);
        row.setSort(10);
        row.setStatus("active");
        return row;
    }

    private static LabPackEvidenceRequirementDO evidenceRequirement() {
        LabPackEvidenceRequirementDO row = new LabPackEvidenceRequirementDO();
        row.setId(107L);
        row.setDomainPackId(1L);
        row.setRequirementCode("EQUIPMENT_CERT");
        row.setRequirementName("设备校准证书");
        row.setEvidenceType("EQUIPMENT_CERTIFICATE");
        row.setRequiredFlag(true);
        row.setSort(10);
        row.setStatus("active");
        return row;
    }

}
