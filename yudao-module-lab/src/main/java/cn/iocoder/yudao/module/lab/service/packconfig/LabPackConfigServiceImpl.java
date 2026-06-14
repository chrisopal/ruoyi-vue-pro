package cn.iocoder.yudao.module.lab.service.packconfig;

import cn.iocoder.yudao.module.lab.controller.admin.packconfig.vo.LabPackConfigRespVO;
import cn.iocoder.yudao.module.lab.controller.admin.packconfig.vo.LabPackConfigSaveReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.packconfig.LabPackReportSectionDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.packconfig.LabPackResultFieldDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.packconfig.LabPackTestItemDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.packconfig.LabPackWorkflowNodeDO;
import cn.iocoder.yudao.module.lab.dal.mysql.domainpack.LabDomainPackMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.packconfig.LabPackReportSectionMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.packconfig.LabPackResultFieldMapper;
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
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePackConfig(LabPackConfigSaveReqVO saveReqVO) {
        Long domainPackId = saveReqVO.getDomainPackId();
        validateDomainPackExists(domainPackId);
        workflowNodeMapper.deleteByDomainPackId(domainPackId);
        testItemMapper.deleteByDomainPackId(domainPackId);
        resultFieldMapper.deleteByDomainPackId(domainPackId);
        reportSectionMapper.deleteByDomainPackId(domainPackId);
        safeList(saveReqVO.getWorkflowNodes()).forEach(item -> workflowNodeMapper.insert(toWorkflowNodeDO(domainPackId, item)));
        safeList(saveReqVO.getTestItems()).forEach(item -> testItemMapper.insert(toTestItemDO(domainPackId, item)));
        safeList(saveReqVO.getResultFields()).forEach(item -> resultFieldMapper.insert(toResultFieldDO(domainPackId, item)));
        safeList(saveReqVO.getReportSections()).forEach(item -> reportSectionMapper.insert(toReportSectionDO(domainPackId, item)));
    }

    private void validateDomainPackExists(Long domainPackId) {
        if (domainPackId == null || domainPackMapper.selectById(domainPackId) == null) {
            throw exception(DOMAIN_PACK_NOT_EXISTS);
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
