package cn.iocoder.yudao.module.lims.service.workflow;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.lab.service.domainpack.dto.LabDomainPackSnapshotDTO;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsWorkflowSaveReqVO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestRequestDO;
import cn.iocoder.yudao.module.lims.dal.mysql.resultvalue.LimsTestResultValueMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsReportMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsSampleMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestRequestMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestResultMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestTaskMapper;
import cn.iocoder.yudao.module.lims.service.workflow.gateway.DomainPackGateway;
import cn.iocoder.yudao.module.lims.service.workflow.model.WorkflowSnapshot;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LimsWorkflowServiceTest extends BaseMockitoUnitTest {

    @InjectMocks
    private LimsWorkflowService workflowService;

    @Mock
    private LimsTestRequestMapper requestMapper;
    @Mock
    private LimsSampleMapper sampleMapper;
    @Mock
    private LimsTestTaskMapper taskMapper;
    @Mock
    private LimsTestResultMapper resultMapper;
    @Mock
    private LimsTestResultValueMapper resultValueMapper;
    @Mock
    private LimsReportMapper reportMapper;
    @Mock
    private DomainPackGateway domainPackGateway;
    @Mock
    private WorkflowSnapshotFactory workflowSnapshotFactory;
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createRequest_shouldFreezeSnapshotFromDomainPackGateway() {
        LabDomainPackSnapshotDTO pack = publishedPackSnapshot();
        when(requestMapper.selectByRequestNo("REQ-2026-001")).thenReturn(null);
        when(domainPackGateway.getPublishedPackSnapshot(1L)).thenReturn(pack);
        WorkflowSnapshot snapshot = new WorkflowSnapshot(
                "{\"packCode\":\"FOOD_ROUTINE\",\"testItems\":[]}",
                "hash-001",
                LocalDateTime.of(2026, 6, 14, 10, 30, 0),
                1L,
                "FOOD_ROUTINE",
                "1.0");
        when(workflowSnapshotFactory.createSnapshot(pack, "{\"channel\":\"internal\"}")).thenReturn(snapshot);

        workflowService.createRequest(createReq());

        verify(domainPackGateway).getPublishedPackSnapshot(1L);
        verify(workflowSnapshotFactory).createSnapshot(pack, "{\"channel\":\"internal\"}");
        verify(requestMapper).insert(argThat((LimsTestRequestDO request) ->
                "FOOD_ROUTINE".equals(request.getDomainPackCode())
                        && "1.0".equals(request.getDomainPackVersion())
                        && "hash-001".equals(request.getWorkflowSnapshotHash())
                        && request.getScenarioConfig().contains("FOOD_ROUTINE")
                        && "INTERNAL_DEPARTMENT".equals(request.getRequestSourceType())));
    }

    private static LimsWorkflowSaveReqVO createReq() {
        LimsWorkflowSaveReqVO reqVO = new LimsWorkflowSaveReqVO();
        reqVO.setRequestNo("REQ-2026-001");
        reqVO.setRequestName("食品委托检测");
        reqVO.setRequestType("internal");
        reqVO.setDomainCode("FOOD");
        reqVO.setDomainPackId(1L);
        reqVO.setScenarioConfig("{\"channel\":\"internal\"}");
        return reqVO;
    }

    private static LabDomainPackSnapshotDTO publishedPackSnapshot() {
        LabDomainPackSnapshotDTO pack = new LabDomainPackSnapshotDTO();
        pack.setDomainPackId(1L);
        pack.setPackCode("FOOD_ROUTINE");
        pack.setPackName("食品常规检测方案包");
        pack.setPackVersion("1.0");
        pack.setIndustry("食品");
        return pack;
    }

}
