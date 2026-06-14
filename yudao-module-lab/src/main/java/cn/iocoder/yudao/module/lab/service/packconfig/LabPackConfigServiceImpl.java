package cn.iocoder.yudao.module.lab.service.packconfig;

import cn.iocoder.yudao.module.lab.controller.admin.packconfig.vo.LabPackConfigRespVO;
import cn.iocoder.yudao.module.lab.controller.admin.packconfig.vo.LabPackConfigSaveReqVO;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lab.enums.ErrorCodeConstants.DOMAIN_PACK_NOT_EXISTS;
import static cn.iocoder.yudao.module.lab.enums.ErrorCodeConstants.DOMAIN_PACK_PUBLISHED_IMMUTABLE;

@Service
@Validated
public class LabPackConfigServiceImpl implements LabPackConfigService {

    @Resource
    private LabDomainPackMapper domainPackMapper;
    @Resource
    private LabPackWorkflowNodeMapper workflowNodeMapper;
    @Resource
    private LabPackTestItemMapper testItemMapper;
    @Resource
    private LabPackResultFieldMapper resultFieldMapper;
    @Resource
    private LabPackReportSectionMapper reportSectionMapper;
    @Resource
    private LabPackSampleRequirementMapper sampleRequirementMapper;
    @Resource
    private LabPackQcRuleMapper qcRuleMapper;
    @Resource
    private LabPackEvidenceRequirementMapper evidenceRequirementMapper;
    @Resource
    private ObjectMapper objectMapper;

    @Override
    public LabPackConfigRespVO getPackConfig(Long domainPackId) {
        validateDomainPackExists(domainPackId);
        LabPackConfigRespVO respVO = new LabPackConfigRespVO();
        respVO.setDomainPackId(domainPackId);
        respVO.setWorkflowNodes(workflowNodeMapper.selectListByDomainPackId(domainPackId).stream().map(this::toWorkflowNode).toList());
        respVO.setTestItems(testItemMapper.selectListByDomainPackId(domainPackId).stream().map(this::toTestItem).toList());
        respVO.setResultFields(resultFieldMapper.selectListByDomainPackId(domainPackId).stream().map(this::toResultField).toList());
        respVO.setReportSections(reportSectionMapper.selectListByDomainPackId(domainPackId).stream().map(this::toReportSection).toList());
        respVO.setSampleRequirements(sampleRequirementMapper.selectListByDomainPackId(domainPackId).stream().map(this::toSampleRequirement).toList());
        respVO.setQcRules(qcRuleMapper.selectListByDomainPackId(domainPackId).stream().map(this::toQcRule).toList());
        respVO.setEvidenceRequirements(evidenceRequirementMapper.selectListByDomainPackId(domainPackId).stream().map(this::toEvidenceRequirement).toList());
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePackConfig(LabPackConfigSaveReqVO saveReqVO) {
        Long domainPackId = saveReqVO.getDomainPackId();
        validateDomainPackEditable(domainPackId);
        workflowNodeMapper.deleteByDomainPackId(domainPackId);
        testItemMapper.deleteByDomainPackId(domainPackId);
        resultFieldMapper.deleteByDomainPackId(domainPackId);
        reportSectionMapper.deleteByDomainPackId(domainPackId);
        sampleRequirementMapper.deleteByDomainPackId(domainPackId);
        qcRuleMapper.deleteByDomainPackId(domainPackId);
        evidenceRequirementMapper.deleteByDomainPackId(domainPackId);
        safeList(saveReqVO.getWorkflowNodes()).forEach(item -> workflowNodeMapper.insert(toWorkflowNodeDO(domainPackId, item)));
        safeList(saveReqVO.getTestItems()).forEach(item -> testItemMapper.insert(toTestItemDO(domainPackId, item)));
        safeList(saveReqVO.getResultFields()).forEach(item -> resultFieldMapper.insert(toResultFieldDO(domainPackId, item)));
        safeList(saveReqVO.getReportSections()).forEach(item -> reportSectionMapper.insert(toReportSectionDO(domainPackId, item)));
        safeList(saveReqVO.getSampleRequirements()).forEach(item -> sampleRequirementMapper.insert(toSampleRequirementDO(domainPackId, item)));
        safeList(saveReqVO.getQcRules()).forEach(item -> qcRuleMapper.insert(toQcRuleDO(domainPackId, item)));
        safeList(saveReqVO.getEvidenceRequirements()).forEach(item -> evidenceRequirementMapper.insert(toEvidenceRequirementDO(domainPackId, item)));
    }

    private void validateDomainPackExists(Long domainPackId) {
        if (domainPackId == null || domainPackMapper.selectById(domainPackId) == null) {
            throw exception(DOMAIN_PACK_NOT_EXISTS);
        }
    }

    private void validateDomainPackEditable(Long domainPackId) {
        LabDomainPackDO domainPack = domainPackId == null ? null : domainPackMapper.selectById(domainPackId);
        if (domainPack == null) {
            throw exception(DOMAIN_PACK_NOT_EXISTS);
        }
        if (!"draft".equalsIgnoreCase(domainPack.getStatus())) {
            throw exception(DOMAIN_PACK_PUBLISHED_IMMUTABLE);
        }
    }

    private LabPackWorkflowNodeDO toWorkflowNodeDO(Long domainPackId, LabPackConfigSaveReqVO.WorkflowNode item) {
        LabPackWorkflowNodeDO node = new LabPackWorkflowNodeDO();
        node.setDomainPackId(domainPackId);
        node.setNodeCode(item.getNodeCode());
        node.setNodeName(item.getNodeName());
        node.setRoleName(item.getRoleName());
        node.setRequiredFlag(Boolean.TRUE.equals(item.getRequired()));
        node.setSort(defaultSort(item.getSort()));
        node.setStatus(defaultStatus(item.getStatus()));
        return node;
    }

    private LabPackTestItemDO toTestItemDO(Long domainPackId, LabPackConfigSaveReqVO.TestItem item) {
        LabPackTestItemDO testItem = new LabPackTestItemDO();
        testItem.setDomainPackId(domainPackId);
        testItem.setItemCode(item.getItemCode());
        testItem.setItemName(item.getItemName());
        testItem.setMethodCode(item.getMethodCode());
        testItem.setMethodName(item.getMethodName());
        testItem.setStandardCode(item.getStandardCode());
        testItem.setResultUnit(item.getResultUnit());
        testItem.setDemoValue(item.getDemoValue());
        testItem.setSort(defaultSort(item.getSort()));
        testItem.setStatus(defaultStatus(item.getStatus()));
        return testItem;
    }

    private LabPackResultFieldDO toResultFieldDO(Long domainPackId, LabPackConfigSaveReqVO.ResultField item) {
        LabPackResultFieldDO field = new LabPackResultFieldDO();
        field.setDomainPackId(domainPackId);
        field.setItemCode(item.getItemCode());
        field.setFieldCode(item.getFieldCode());
        field.setFieldName(item.getFieldName());
        field.setFieldType(item.getFieldType());
        field.setUnit(item.getUnit());
        field.setRequiredFlag(Boolean.TRUE.equals(item.getRequired()));
        field.setMinValue(item.getMinValue());
        field.setMaxValue(item.getMaxValue());
        field.setEnumOptions(toJson(item.getEnumOptions()));
        field.setDemoValue(item.getDemoValue());
        field.setSort(defaultSort(item.getSort()));
        field.setStatus(defaultStatus(item.getStatus()));
        return field;
    }

    private LabPackReportSectionDO toReportSectionDO(Long domainPackId, LabPackConfigSaveReqVO.ReportSection item) {
        LabPackReportSectionDO section = new LabPackReportSectionDO();
        section.setDomainPackId(domainPackId);
        section.setSectionCode(item.getSectionCode());
        section.setSectionName(item.getSectionName());
        section.setSourceType(item.getSourceType());
        section.setVisibleFlag(!Boolean.FALSE.equals(item.getVisible()));
        section.setSort(defaultSort(item.getSort()));
        section.setStatus(defaultStatus(item.getStatus()));
        return section;
    }

    private LabPackSampleRequirementDO toSampleRequirementDO(Long domainPackId, LabPackConfigSaveReqVO.SampleRequirement item) {
        LabPackSampleRequirementDO requirement = new LabPackSampleRequirementDO();
        requirement.setDomainPackId(domainPackId);
        requirement.setRequirementCode(item.getRequirementCode());
        requirement.setRequirementName(item.getRequirementName());
        requirement.setRequirementType(item.getRequirementType());
        requirement.setRequirementText(item.getRequirementText());
        requirement.setSort(defaultSort(item.getSort()));
        requirement.setStatus(defaultStatus(item.getStatus()));
        return requirement;
    }

    private LabPackQcRuleDO toQcRuleDO(Long domainPackId, LabPackConfigSaveReqVO.QcRule item) {
        LabPackQcRuleDO rule = new LabPackQcRuleDO();
        rule.setDomainPackId(domainPackId);
        rule.setRuleCode(item.getRuleCode());
        rule.setRuleName(item.getRuleName());
        rule.setRuleType(item.getRuleType());
        rule.setRuleExpression(item.getRuleExpression());
        rule.setAcceptanceCriteria(item.getAcceptanceCriteria());
        rule.setSort(defaultSort(item.getSort()));
        rule.setStatus(defaultStatus(item.getStatus()));
        return rule;
    }

    private LabPackEvidenceRequirementDO toEvidenceRequirementDO(Long domainPackId, LabPackConfigSaveReqVO.EvidenceRequirement item) {
        LabPackEvidenceRequirementDO requirement = new LabPackEvidenceRequirementDO();
        requirement.setDomainPackId(domainPackId);
        requirement.setRequirementCode(item.getRequirementCode());
        requirement.setRequirementName(item.getRequirementName());
        requirement.setEvidenceType(item.getEvidenceType());
        requirement.setSourceType(item.getSourceType());
        requirement.setClauseCategory(item.getClauseCategory());
        requirement.setRequiredFlag(Boolean.TRUE.equals(item.getRequired()));
        requirement.setSort(defaultSort(item.getSort()));
        requirement.setStatus(defaultStatus(item.getStatus()));
        return requirement;
    }

    private LabPackConfigSaveReqVO.WorkflowNode toWorkflowNode(LabPackWorkflowNodeDO node) {
        LabPackConfigSaveReqVO.WorkflowNode item = new LabPackConfigSaveReqVO.WorkflowNode();
        item.setNodeCode(node.getNodeCode());
        item.setNodeName(node.getNodeName());
        item.setRoleName(node.getRoleName());
        item.setRequired(node.getRequiredFlag());
        item.setSort(node.getSort());
        item.setStatus(node.getStatus());
        return item;
    }

    private LabPackConfigSaveReqVO.TestItem toTestItem(LabPackTestItemDO testItem) {
        LabPackConfigSaveReqVO.TestItem item = new LabPackConfigSaveReqVO.TestItem();
        item.setItemCode(testItem.getItemCode());
        item.setItemName(testItem.getItemName());
        item.setMethodCode(testItem.getMethodCode());
        item.setMethodName(testItem.getMethodName());
        item.setStandardCode(testItem.getStandardCode());
        item.setResultUnit(testItem.getResultUnit());
        item.setDemoValue(testItem.getDemoValue());
        item.setSort(testItem.getSort());
        item.setStatus(testItem.getStatus());
        return item;
    }

    private LabPackConfigSaveReqVO.ResultField toResultField(LabPackResultFieldDO field) {
        LabPackConfigSaveReqVO.ResultField item = new LabPackConfigSaveReqVO.ResultField();
        item.setItemCode(field.getItemCode());
        item.setFieldCode(field.getFieldCode());
        item.setFieldName(field.getFieldName());
        item.setFieldType(field.getFieldType());
        item.setUnit(field.getUnit());
        item.setRequired(field.getRequiredFlag());
        item.setMinValue(field.getMinValue());
        item.setMaxValue(field.getMaxValue());
        item.setEnumOptions(fromJsonList(field.getEnumOptions()));
        item.setDemoValue(field.getDemoValue());
        item.setSort(field.getSort());
        item.setStatus(field.getStatus());
        return item;
    }

    private LabPackConfigSaveReqVO.ReportSection toReportSection(LabPackReportSectionDO section) {
        LabPackConfigSaveReqVO.ReportSection item = new LabPackConfigSaveReqVO.ReportSection();
        item.setSectionCode(section.getSectionCode());
        item.setSectionName(section.getSectionName());
        item.setSourceType(section.getSourceType());
        item.setVisible(section.getVisibleFlag());
        item.setSort(section.getSort());
        item.setStatus(section.getStatus());
        return item;
    }

    private LabPackConfigSaveReqVO.SampleRequirement toSampleRequirement(LabPackSampleRequirementDO requirement) {
        LabPackConfigSaveReqVO.SampleRequirement item = new LabPackConfigSaveReqVO.SampleRequirement();
        item.setRequirementCode(requirement.getRequirementCode());
        item.setRequirementName(requirement.getRequirementName());
        item.setRequirementType(requirement.getRequirementType());
        item.setRequirementText(requirement.getRequirementText());
        item.setSort(requirement.getSort());
        item.setStatus(requirement.getStatus());
        return item;
    }

    private LabPackConfigSaveReqVO.QcRule toQcRule(LabPackQcRuleDO rule) {
        LabPackConfigSaveReqVO.QcRule item = new LabPackConfigSaveReqVO.QcRule();
        item.setRuleCode(rule.getRuleCode());
        item.setRuleName(rule.getRuleName());
        item.setRuleType(rule.getRuleType());
        item.setRuleExpression(rule.getRuleExpression());
        item.setAcceptanceCriteria(rule.getAcceptanceCriteria());
        item.setSort(rule.getSort());
        item.setStatus(rule.getStatus());
        return item;
    }

    private LabPackConfigSaveReqVO.EvidenceRequirement toEvidenceRequirement(LabPackEvidenceRequirementDO requirement) {
        LabPackConfigSaveReqVO.EvidenceRequirement item = new LabPackConfigSaveReqVO.EvidenceRequirement();
        item.setRequirementCode(requirement.getRequirementCode());
        item.setRequirementName(requirement.getRequirementName());
        item.setEvidenceType(requirement.getEvidenceType());
        item.setSourceType(requirement.getSourceType());
        item.setClauseCategory(requirement.getClauseCategory());
        item.setRequired(requirement.getRequiredFlag());
        item.setSort(requirement.getSort());
        item.setStatus(requirement.getStatus());
        return item;
    }

    private <T> List<T> safeList(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    private Integer defaultSort(Integer sort) {
        return sort == null ? 0 : sort;
    }

    private String defaultStatus(String status) {
        return Objects.requireNonNullElse(status, "active");
    }

    private String toJson(List<String> values) {
        if (values == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(values);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    private List<String> fromJsonList(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readerForListOf(String.class).readValue(json);
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

}
