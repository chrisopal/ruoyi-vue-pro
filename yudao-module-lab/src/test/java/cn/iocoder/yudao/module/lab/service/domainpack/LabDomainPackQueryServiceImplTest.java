package cn.iocoder.yudao.module.lab.service.domainpack;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
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
import cn.iocoder.yudao.module.lab.service.domainpack.dto.LabDomainPackSnapshotDTO;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static cn.iocoder.yudao.module.lab.enums.ErrorCodeConstants.DOMAIN_PACK_STATUS_INVALID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class LabDomainPackQueryServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private LabDomainPackQueryServiceImpl queryService;

    @Mock
    private LabDomainPackMapper domainPackMapper;
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
    void getPublishedSnapshot_shouldReturnAllConfigSections() {
        when(domainPackMapper.selectById(1L)).thenReturn(domainPack("published"));
        when(workflowNodeMapper.selectListByDomainPackId(1L)).thenReturn(List.of(workflowNode()));
        when(sampleRequirementMapper.selectListByDomainPackId(1L)).thenReturn(List.of(sampleRequirement()));
        when(testItemMapper.selectListByDomainPackId(1L)).thenReturn(List.of(testItem()));
        when(resultFieldMapper.selectListByDomainPackId(1L)).thenReturn(List.of(resultField()));
        when(qcRuleMapper.selectListByDomainPackId(1L)).thenReturn(List.of(qcRule()));
        when(reportSectionMapper.selectListByDomainPackId(1L)).thenReturn(List.of(reportSection()));
        when(evidenceRequirementMapper.selectListByDomainPackId(1L)).thenReturn(List.of(evidenceRequirement()));

        LabDomainPackSnapshotDTO snapshot = queryService.getPublishedSnapshot(1L);

        assertEquals(1L, snapshot.getDomainPackId());
        assertEquals("FOOD_ROUTINE", snapshot.getPackCode());
        assertEquals("1.0", snapshot.getPackVersion());
        assertEquals("样品接收", snapshot.getWorkflowNodes().get(0).getNodeName());
        assertEquals("SAMPLE_QTY", snapshot.getSampleRequirements().get(0).getRequirementCode());
        assertEquals("PH", snapshot.getTestItems().get(0).getItemCode());
        assertEquals("PH_VALUE", snapshot.getResultFields().get(0).getFieldCode());
        assertEquals("BLANK", snapshot.getQcRules().get(0).getRuleCode());
        assertEquals("RESULTS", snapshot.getReportSections().get(0).getSectionCode());
        assertEquals("EQUIPMENT_CERT", snapshot.getEvidenceRequirements().get(0).getRequirementCode());
    }

    @Test
    void getPublishedSnapshot_shouldRejectDraftPack() {
        when(domainPackMapper.selectById(1L)).thenReturn(domainPack("draft"));

        ServiceException ex = assertThrows(ServiceException.class, () -> queryService.getPublishedSnapshot(1L));

        assertEquals(DOMAIN_PACK_STATUS_INVALID.getCode(), ex.getCode());
    }

    private static LabDomainPackDO domainPack(String status) {
        LabDomainPackDO domainPack = new LabDomainPackDO();
        domainPack.setId(1L);
        domainPack.setPackCode("FOOD_ROUTINE");
        domainPack.setPackName("食品常规检测方案包");
        domainPack.setPackVersion("1.0");
        domainPack.setIndustry("食品");
        domainPack.setWorkflowSchema("{\"stages\":[\"request\",\"sample\",\"task\",\"report\"]}");
        domainPack.setTemplateSchema("{\"templates\":[\"report\"]}");
        domainPack.setStatus(status);
        return domainPack;
    }

    private static LabPackWorkflowNodeDO workflowNode() {
        LabPackWorkflowNodeDO node = new LabPackWorkflowNodeDO();
        node.setNodeCode("sample_receive");
        node.setNodeName("样品接收");
        node.setRequiredFlag(true);
        node.setSort(10);
        node.setStatus("active");
        return node;
    }

    private static LabPackSampleRequirementDO sampleRequirement() {
        LabPackSampleRequirementDO item = new LabPackSampleRequirementDO();
        item.setRequirementCode("SAMPLE_QTY");
        item.setRequirementName("样品量");
        item.setRequirementType("quantity");
        item.setRequirementText(">= 500g");
        item.setSort(10);
        item.setStatus("active");
        return item;
    }

    private static LabPackTestItemDO testItem() {
        LabPackTestItemDO item = new LabPackTestItemDO();
        item.setItemCode("PH");
        item.setItemName("pH");
        item.setMethodCode("GB6920");
        item.setMethodName("玻璃电极法");
        item.setSort(10);
        item.setStatus("active");
        return item;
    }

    private static LabPackResultFieldDO resultField() {
        LabPackResultFieldDO field = new LabPackResultFieldDO();
        field.setItemCode("PH");
        field.setFieldCode("PH_VALUE");
        field.setFieldName("pH值");
        field.setFieldType("number");
        field.setRequiredFlag(true);
        field.setSort(10);
        field.setStatus("active");
        return field;
    }

    private static LabPackQcRuleDO qcRule() {
        LabPackQcRuleDO rule = new LabPackQcRuleDO();
        rule.setRuleCode("BLANK");
        rule.setRuleName("空白样");
        rule.setRuleType("batch");
        rule.setAcceptanceCriteria("每批至少 1 个");
        rule.setSort(10);
        rule.setStatus("active");
        return rule;
    }

    private static LabPackReportSectionDO reportSection() {
        LabPackReportSectionDO section = new LabPackReportSectionDO();
        section.setSectionCode("RESULTS");
        section.setSectionName("检测结果");
        section.setSourceType("result_values");
        section.setVisibleFlag(true);
        section.setSort(10);
        section.setStatus("active");
        return section;
    }

    private static LabPackEvidenceRequirementDO evidenceRequirement() {
        LabPackEvidenceRequirementDO item = new LabPackEvidenceRequirementDO();
        item.setRequirementCode("EQUIPMENT_CERT");
        item.setRequirementName("设备校准证书");
        item.setEvidenceType("EQUIPMENT_CERTIFICATE");
        item.setSourceType("equipment");
        item.setClauseCategory("equipment");
        item.setRequiredFlag(true);
        item.setSort(10);
        item.setStatus("active");
        return item;
    }

}
