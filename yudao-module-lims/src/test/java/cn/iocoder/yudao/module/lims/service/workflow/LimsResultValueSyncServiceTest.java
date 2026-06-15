package cn.iocoder.yudao.module.lims.service.workflow;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.lims.dal.dataobject.resultvalue.LimsTestResultValueDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestResultDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestTaskDO;
import cn.iocoder.yudao.module.lims.dal.mysql.resultvalue.LimsTestResultValueMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class LimsResultValueSyncServiceTest extends BaseMockitoUnitTest {

    @InjectMocks
    private LimsResultValueSyncService service;

    @Mock
    private LimsTestResultValueMapper resultValueMapper;
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void replaceValues_shouldRewriteDynamicResultFields() {
        LimsTestResultDO result = result();
        LimsTestTaskDO task = task();

        service.replaceValues(result, task);

        verify(resultValueMapper).deleteByResultId(100L);
        verify(resultValueMapper).insert(argThat((LimsTestResultValueDO value) ->
                Long.valueOf(100L).equals(value.getResultId())
                        && Long.valueOf(1L).equals(value.getRequestId())
                        && "REQ-001".equals(value.getRequestNo())
                        && Long.valueOf(20L).equals(value.getTaskId())
                        && "REQ-001-T01".equals(value.getTaskNo())
                        && "MOISTURE_VALUE".equals(value.getFieldCode())
                        && "水分含量".equals(value.getFieldName())
                        && "number".equals(value.getFieldType())
                        && "12.5".equals(value.getFieldValue())
                        && "12.5%".equals(value.getDisplayValue())
                        && "%".equals(value.getUnit())
                        && "合格".equals(value.getConclusion())
                        && Integer.valueOf(1).equals(value.getSort())
                        && "recorded".equals(value.getStatus())));
    }

    @Test
    void replaceValues_shouldSkipWhenRawDataIsEmpty() {
        LimsTestResultDO result = result();
        result.setRawData(null);

        service.replaceValues(result, task());

        verify(resultValueMapper, never()).deleteByResultId(100L);
        verify(resultValueMapper, never()).insert(org.mockito.ArgumentMatchers.<LimsTestResultValueDO>any());
    }

    private static LimsTestResultDO result() {
        LimsTestResultDO result = new LimsTestResultDO();
        result.setId(100L);
        result.setRequestId(1L);
        result.setRequestNo("REQ-001");
        result.setSampleId(10L);
        result.setSampleNo("REQ-001-S01");
        result.setTaskId(20L);
        result.setTaskNo("REQ-001-T01");
        result.setTestItem("水分");
        result.setResultConclusion("合格");
        result.setRawData("""
                {"resultValues":[{"fieldCode":"MOISTURE_VALUE","fieldName":"水分含量","fieldType":"number","fieldValue":"12.5","displayValue":"12.5%","unit":"%","conclusion":"合格"}]}
                """);
        return result;
    }

    private static LimsTestTaskDO task() {
        LimsTestTaskDO task = new LimsTestTaskDO();
        task.setId(20L);
        task.setTaskNo("REQ-001-T01");
        task.setTestItem("水分");
        return task;
    }

}
