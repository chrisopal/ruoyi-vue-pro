package cn.iocoder.yudao.module.lab.service.domainpack;

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
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lab.enums.ErrorCodeConstants.DOMAIN_PACK_NOT_EXISTS;
import static cn.iocoder.yudao.module.lab.enums.ErrorCodeConstants.DOMAIN_PACK_STATUS_INVALID;

@Service
@Validated
public class LabDomainPackQueryServiceImpl implements LabDomainPackQueryService {

    @Resource
    private LabDomainPackMapper domainPackMapper;
    @Resource
    private LabPackWorkflowNodeMapper workflowNodeMapper;
    @Resource
    private LabPackSampleRequirementMapper sampleRequirementMapper;
    @Resource
    private LabPackTestItemMapper testItemMapper;
    @Resource
    private LabPackResultFieldMapper resultFieldMapper;
    @Resource
    private LabPackQcRuleMapper qcRuleMapper;
    @Resource
    private LabPackReportSectionMapper reportSectionMapper;
    @Resource
    private LabPackEvidenceRequirementMapper evidenceRequirementMapper;

    @Override
    public LabDomainPackSnapshotDTO getPublishedSnapshot(Long domainPackId) {
        LabDomainPackDO domainPack = validatePublishedDomainPack(domainPackId);
        LabDomainPackSnapshotDTO snapshot = new LabDomainPackSnapshotDTO();
        snapshot.setDomainPackId(domainPack.getId());
        snapshot.setDomainId(domainPack.getDomainId());
        snapshot.setPackCode(domainPack.getPackCode());
        snapshot.setPackName(domainPack.getPackName());
        snapshot.setPackVersion(domainPack.getPackVersion());
        snapshot.setIndustry(domainPack.getIndustry());
        snapshot.setApplicationScope(domainPack.getApplicationScope());
        snapshot.setWorkflowSchema(domainPack.getWorkflowSchema());
        snapshot.setTemplateSchema(domainPack.getTemplateSchema());
        snapshot.setWorkflowNodes(workflowNodeMapper.selectListByDomainPackId(domainPackId).stream().map(this::toWorkflowNode).toList());
        snapshot.setSampleRequirements(sampleRequirementMapper.selectListByDomainPackId(domainPackId).stream().map(this::toSampleRequirement).toList());
        snapshot.setTestItems(testItemMapper.selectListByDomainPackId(domainPackId).stream().map(this::toTestItem).toList());
        snapshot.setResultFields(resultFieldMapper.selectListByDomainPackId(domainPackId).stream().map(this::toResultField).toList());
        snapshot.setQcRules(qcRuleMapper.selectListByDomainPackId(domainPackId).stream().map(this::toQcRule).toList());
        snapshot.setReportSections(reportSectionMapper.selectListByDomainPackId(domainPackId).stream().map(this::toReportSection).toList());
        snapshot.setEvidenceRequirements(evidenceRequirementMapper.selectListByDomainPackId(domainPackId).stream().map(this::toEvidenceRequirement).toList());
        return snapshot;
    }

    private LabDomainPackDO validatePublishedDomainPack(Long domainPackId) {
        LabDomainPackDO domainPack = domainPackId == null ? null : domainPackMapper.selectById(domainPackId);
        if (domainPack == null) {
            throw exception(DOMAIN_PACK_NOT_EXISTS);
        }
        if (!"published".equalsIgnoreCase(domainPack.getStatus())) {
            throw exception(DOMAIN_PACK_STATUS_INVALID);
        }
        return domainPack;
    }

    private LabDomainPackSnapshotDTO.WorkflowNode toWorkflowNode(LabPackWorkflowNodeDO node) {
        LabDomainPackSnapshotDTO.WorkflowNode item = new LabDomainPackSnapshotDTO.WorkflowNode();
        item.setNodeCode(node.getNodeCode());
        item.setNodeName(node.getNodeName());
        item.setRoleName(node.getRoleName());
        item.setRequired(node.getRequiredFlag());
        item.setSort(node.getSort());
        item.setStatus(node.getStatus());
        return item;
    }

    private LabDomainPackSnapshotDTO.SampleRequirement toSampleRequirement(LabPackSampleRequirementDO requirement) {
        LabDomainPackSnapshotDTO.SampleRequirement item = new LabDomainPackSnapshotDTO.SampleRequirement();
        item.setRequirementCode(requirement.getRequirementCode());
        item.setRequirementName(requirement.getRequirementName());
        item.setRequirementType(requirement.getRequirementType());
        item.setRequirementText(requirement.getRequirementText());
        item.setSort(requirement.getSort());
        item.setStatus(requirement.getStatus());
        return item;
    }

    private LabDomainPackSnapshotDTO.TestItem toTestItem(LabPackTestItemDO testItem) {
        LabDomainPackSnapshotDTO.TestItem item = new LabDomainPackSnapshotDTO.TestItem();
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

    private LabDomainPackSnapshotDTO.ResultField toResultField(LabPackResultFieldDO field) {
        LabDomainPackSnapshotDTO.ResultField item = new LabDomainPackSnapshotDTO.ResultField();
        item.setItemCode(field.getItemCode());
        item.setFieldCode(field.getFieldCode());
        item.setFieldName(field.getFieldName());
        item.setFieldType(field.getFieldType());
        item.setUnit(field.getUnit());
        item.setRequired(field.getRequiredFlag());
        item.setMinValue(field.getMinValue());
        item.setMaxValue(field.getMaxValue());
        item.setEnumOptions(field.getEnumOptions());
        item.setDemoValue(field.getDemoValue());
        item.setSort(field.getSort());
        item.setStatus(field.getStatus());
        return item;
    }

    private LabDomainPackSnapshotDTO.QcRule toQcRule(LabPackQcRuleDO rule) {
        LabDomainPackSnapshotDTO.QcRule item = new LabDomainPackSnapshotDTO.QcRule();
        item.setRuleCode(rule.getRuleCode());
        item.setRuleName(rule.getRuleName());
        item.setRuleType(rule.getRuleType());
        item.setRuleExpression(rule.getRuleExpression());
        item.setAcceptanceCriteria(rule.getAcceptanceCriteria());
        item.setSort(rule.getSort());
        item.setStatus(rule.getStatus());
        return item;
    }

    private LabDomainPackSnapshotDTO.ReportSection toReportSection(LabPackReportSectionDO section) {
        LabDomainPackSnapshotDTO.ReportSection item = new LabDomainPackSnapshotDTO.ReportSection();
        item.setSectionCode(section.getSectionCode());
        item.setSectionName(section.getSectionName());
        item.setSourceType(section.getSourceType());
        item.setVisible(section.getVisibleFlag());
        item.setSort(section.getSort());
        item.setStatus(section.getStatus());
        return item;
    }

    private LabDomainPackSnapshotDTO.EvidenceRequirement toEvidenceRequirement(LabPackEvidenceRequirementDO requirement) {
        LabDomainPackSnapshotDTO.EvidenceRequirement item = new LabDomainPackSnapshotDTO.EvidenceRequirement();
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

}
