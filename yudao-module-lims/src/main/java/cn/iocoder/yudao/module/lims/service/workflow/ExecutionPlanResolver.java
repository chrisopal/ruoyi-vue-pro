package cn.iocoder.yudao.module.lims.service.workflow;

import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsExecutionPlanDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestRequestDO;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsExecutionPlanMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ExecutionPlanResolver {

    @Resource
    private LimsExecutionPlanMapper executionPlanMapper;
    @Resource
    private ExecutionPlanFactory executionPlanFactory;
    @Resource
    private ObjectMapper objectMapper;

    public ResolvedExecutionPlan resolve(LimsTestRequestDO request) {
        LimsExecutionPlanDO executionPlan = executionPlanMapper.selectByRequestId(request.getId());
        if (executionPlan != null) {
            return new ResolvedExecutionPlan(readObject(executionPlan.getPlanJson()), executionPlan.getStatus());
        }
        return new ResolvedExecutionPlan(
                readObject(executionPlanFactory.createPlanJson(resolveWorkflowSnapshot(request))),
                "derived");
    }

    private String resolveWorkflowSnapshot(LimsTestRequestDO request) {
        return StringUtils.hasText(request.getWorkflowSnapshot()) ? request.getWorkflowSnapshot() : request.getScenarioConfig();
    }

    private JsonNode readObject(String json) {
        if (!StringUtils.hasText(json)) {
            return objectMapper.createObjectNode();
        }
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException ex) {
            return objectMapper.createObjectNode();
        }
    }

    public record ResolvedExecutionPlan(JsonNode plan, String status) {
    }

}
