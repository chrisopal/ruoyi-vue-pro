package cn.iocoder.yudao.module.lims.service.workflow;

import cn.iocoder.yudao.module.lims.dal.dataobject.resultvalue.LimsTestResultValueDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestResultDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestTaskDO;
import cn.iocoder.yudao.module.lims.dal.mysql.resultvalue.LimsTestResultValueMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LimsResultValueSyncService {

    @Resource
    private LimsTestResultValueMapper resultValueMapper;
    @Resource
    private ObjectMapper objectMapper;

    public void replaceValues(LimsTestResultDO result, LimsTestTaskDO task) {
        if (result == null || task == null || !StringUtils.hasText(result.getRawData())) {
            return;
        }
        if (result.getId() != null) {
            resultValueMapper.deleteByResultId(result.getId());
        }
        JsonNode values = readObject(result.getRawData()).path("resultValues");
        if (!values.isArray()) {
            return;
        }
        for (int i = 0; i < values.size(); i++) {
            JsonNode value = values.get(i);
            String fieldCode = value.path("fieldCode").asText("");
            String fieldName = value.path("fieldName").asText("");
            if (!StringUtils.hasText(fieldCode) || !StringUtils.hasText(fieldName)) {
                continue;
            }
            String fieldValue = value.path("fieldValue").asText(value.path("value").asText(""));
            String unit = value.path("unit").asText("");
            LimsTestResultValueDO resultValue = new LimsTestResultValueDO();
            resultValue.setResultId(result.getId());
            resultValue.setRequestId(result.getRequestId());
            resultValue.setRequestNo(result.getRequestNo());
            resultValue.setSampleId(result.getSampleId());
            resultValue.setSampleNo(result.getSampleNo());
            resultValue.setTaskId(task.getId());
            resultValue.setTaskNo(task.getTaskNo());
            resultValue.setTestItem(task.getTestItem());
            resultValue.setFieldCode(fieldCode);
            resultValue.setFieldName(fieldName);
            resultValue.setFieldType(value.path("fieldType").asText(""));
            resultValue.setFieldValue(fieldValue);
            resultValue.setDisplayValue(value.path("displayValue").asText(fieldValue + unit));
            resultValue.setUnit(unit);
            resultValue.setConclusion(value.path("conclusion").asText(result.getResultConclusion()));
            resultValue.setSort(i + 1);
            resultValue.setStatus("recorded");
            resultValueMapper.insert(resultValue);
        }
    }

    private JsonNode readObject(String json) {
        if (!StringUtils.hasText(json)) {
            return objectMapper.createObjectNode();
        }
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Invalid result value json", ex);
        }
    }

}
