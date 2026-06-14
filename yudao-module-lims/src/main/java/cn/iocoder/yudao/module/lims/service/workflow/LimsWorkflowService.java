package cn.iocoder.yudao.module.lims.service.workflow;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.lab.service.domainpack.dto.LabDomainPackSnapshotDTO;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsWorkflowPageReqVO;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsWorkflowRespVO;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsWorkflowSaveReqVO;
import cn.iocoder.yudao.module.lims.dal.dataobject.resultvalue.LimsTestResultValueDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsExecutionPlanDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsReportDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsSampleDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskScheduleDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestRequestDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestResultDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestTaskDO;
import cn.iocoder.yudao.module.lims.dal.mysql.resultvalue.LimsTestResultValueMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsExecutionPlanMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsReportMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsSampleMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestRequestMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestResultMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestTaskMapper;
import cn.iocoder.yudao.module.lims.service.workflow.gateway.DomainPackGateway;
import cn.iocoder.yudao.module.lims.service.workflow.gateway.EquipmentGateway;
import cn.iocoder.yudao.module.lims.service.workflow.gateway.ReportEvidenceGateway;
import cn.iocoder.yudao.module.lims.service.workflow.model.AvailableEquipment;
import cn.iocoder.yudao.module.lims.service.workflow.model.CalibrationEvidence;
import cn.iocoder.yudao.module.lims.service.workflow.model.IssuedReportEvidence;
import cn.iocoder.yudao.module.lims.service.workflow.model.ReportOutputBundle;
import cn.iocoder.yudao.module.lims.service.workflow.model.ReportOutputRequest;
import cn.iocoder.yudao.module.lims.service.workflow.model.WorkflowSnapshot;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lims.enums.ErrorCodeConstants.*;

@Service
@Validated
public class LimsWorkflowService {

    private static final DateTimeFormatter NO_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Resource
    private LimsTestRequestMapper requestMapper;
    @Resource
    private LimsSampleMapper sampleMapper;
    @Resource
    private LimsTestTaskMapper taskMapper;
    @Resource
    private LimsTestResultMapper resultMapper;
    @Resource
    private LimsTestResultValueMapper resultValueMapper;
    @Resource
    private LimsReportMapper reportMapper;
    @Resource
    private LimsExecutionPlanMapper executionPlanMapper;
    @Resource
    private DomainPackGateway domainPackGateway;
    @Resource
    private EquipmentGateway equipmentGateway;
    @Resource
    private ReportEvidenceGateway reportEvidenceGateway;
    @Resource
    private WorkflowSnapshotFactory workflowSnapshotFactory;
    @Resource
    private ExecutionPlanFactory executionPlanFactory;
    @Resource
    private ReportDraftPlanFactory reportDraftPlanFactory;
    @Resource
    private ReportOutputGenerator reportOutputGenerator;
    @Resource
    private LimsTaskLifecycleService taskLifecycleService;
    @Resource
    private LimsReportEligibilityService reportEligibilityService;
    @Resource
    private LimsTaskScheduleService taskScheduleService;
    @Resource
    private ObjectMapper objectMapper;

    public Long createRequest(LimsWorkflowSaveReqVO createReqVO) {
        validateRequestPayload(createReqVO);
        validateRequestNoUnique(null, createReqVO.getRequestNo());
        LabDomainPackSnapshotDTO pack = validateDomainPack(createReqVO.getDomainPackId());
        LimsTestRequestDO request = BeanUtils.toBean(createReqVO, LimsTestRequestDO.class);
        WorkflowSnapshot workflowSnapshot = workflowSnapshotFactory.createSnapshot(pack, createReqVO.getScenarioConfig());
        request.setDomainPackCode(workflowSnapshot.getPackCode());
        request.setDomainPackVersion(workflowSnapshot.getPackVersion());
        request.setWorkflowSnapshot(workflowSnapshot.getSnapshotJson());
        request.setWorkflowSnapshotHash(workflowSnapshot.getSnapshotHash());
        request.setWorkflowSnapshotTime(workflowSnapshot.getFrozenAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        request.setScenarioConfig(workflowSnapshot.getSnapshotJson());
        if (!StringUtils.hasText(request.getRequestSourceType())) {
            request.setRequestSourceType(resolveDefaultRequestSourceType(request.getRequestType()));
        }
        if (!StringUtils.hasText(request.getStatus())) {
            request.setStatus("draft");
        }
        requestMapper.insert(request);
        return request.getId();
    }

    public void updateRequest(LimsWorkflowSaveReqVO updateReqVO) {
        LimsTestRequestDO existing = validateRequestExists(updateReqVO.getId());
        validateRequestPayload(updateReqVO);
        validateRequestNoUnique(updateReqVO.getId(), updateReqVO.getRequestNo());
        Long requestedDomainPackId = updateReqVO.getDomainPackId() == null ? existing.getDomainPackId() : updateReqVO.getDomainPackId();
        if (existing.getDomainPackId() != null && !Objects.equals(existing.getDomainPackId(), requestedDomainPackId)) {
            throw exception(WORKFLOW_SNAPSHOT_FROZEN);
        }
        LabDomainPackSnapshotDTO pack = validateDomainPack(requestedDomainPackId);
        LimsTestRequestDO request = BeanUtils.toBean(updateReqVO, LimsTestRequestDO.class);
        request.setDomainPackCode(pack.getPackCode());
        request.setDomainPackVersion(pack.getPackVersion());
        preserveWorkflowSnapshot(request, existing, pack);
        if (!StringUtils.hasText(request.getRequestSourceType())) {
            request.setRequestSourceType(resolveDefaultRequestSourceType(request.getRequestType()));
        }
        requestMapper.updateById(request);
    }

    public void deleteRequest(Long id) {
        validateRequestExists(id);
        requestMapper.deleteById(id);
    }

    public LimsWorkflowRespVO getRequest(Long id) {
        return BeanUtils.toBean(requestMapper.selectById(id), LimsWorkflowRespVO.class);
    }

    public PageResult<LimsWorkflowRespVO> getRequestPage(LimsWorkflowPageReqVO pageReqVO) {
        return BeanUtils.toBean(requestMapper.selectPage(pageReqVO), LimsWorkflowRespVO.class);
    }

    public void updateRequestStatus(Long id, String status) {
        validateRequestExists(id);
        requestMapper.update(null, new UpdateWrapper<LimsTestRequestDO>().eq("id", id).set("status", status));
    }

    public Long createSample(LimsWorkflowSaveReqVO createReqVO) {
        LimsTestRequestDO request = validateRequestExists(createReqVO.getRequestId());
        LimsSampleDO sample = BeanUtils.toBean(createReqVO, LimsSampleDO.class);
        sample.setRequestNo(request.getRequestNo());
        if (!StringUtils.hasText(sample.getStatus())) {
            sample.setStatus("received");
        }
        sampleMapper.insert(sample);
        return sample.getId();
    }

    public void updateSample(LimsWorkflowSaveReqVO updateReqVO) {
        validateSampleExists(updateReqVO.getId());
        sampleMapper.updateById(BeanUtils.toBean(updateReqVO, LimsSampleDO.class));
    }

    public void deleteSample(Long id) {
        validateSampleExists(id);
        sampleMapper.deleteById(id);
    }

    public LimsWorkflowRespVO getSample(Long id) {
        return BeanUtils.toBean(sampleMapper.selectById(id), LimsWorkflowRespVO.class);
    }

    public PageResult<LimsWorkflowRespVO> getSamplePage(LimsWorkflowPageReqVO pageReqVO) {
        return BeanUtils.toBean(sampleMapper.selectPage(pageReqVO), LimsWorkflowRespVO.class);
    }

    public Long createTask(LimsWorkflowSaveReqVO createReqVO) {
        LimsTestRequestDO request = validateRequestExists(createReqVO.getRequestId());
        LimsSampleDO sample = validateSampleExists(createReqVO.getSampleId());
        LimsTestTaskDO task = BeanUtils.toBean(createReqVO, LimsTestTaskDO.class);
        task.setRequestNo(request.getRequestNo());
        task.setSampleNo(sample.getSampleNo());
        if (!StringUtils.hasText(task.getStatus())) {
            task.setStatus(LimsTaskStatus.ASSIGNED);
        }
        if (!StringUtils.hasText(task.getTaskStatus())) {
            task.setTaskStatus(task.getStatus());
        }
        if (!StringUtils.hasText(task.getScheduleStatus())) {
            task.setScheduleStatus(LimsTaskScheduleStatus.UNSCHEDULED);
        }
        if (task.getDurationMinutes() == null || task.getDurationMinutes() < 1) {
            task.setDurationMinutes(60L);
        }
        if (!StringUtils.hasText(task.getQcStatus())) {
            task.setQcStatus(LimsTaskReviewStatus.NONE);
        }
        if (!StringUtils.hasText(task.getReviewStatus())) {
            task.setReviewStatus(LimsTaskReviewStatus.NONE);
        }
        if (task.getReportEligible() == null) {
            task.setReportEligible(false);
        }
        if (!StringUtils.hasText(task.getMethodSnapshot())) {
            task.setMethodSnapshot(writeJson(new TestItemConfig(
                    task.getTestItem(), task.getMethodCode(), task.getMethodName(), task.getDurationMinutes())));
        }
        bindEquipmentToTask(task, request, createReqVO.getEquipmentId());
        taskMapper.insert(task);
        return task.getId();
    }

    public void updateTask(LimsWorkflowSaveReqVO updateReqVO) {
        LimsTestTaskDO existing = validateTaskExists(updateReqVO.getId());
        LimsTestTaskDO task = BeanUtils.toBean(updateReqVO, LimsTestTaskDO.class);
        if (updateReqVO.getEquipmentId() != null) {
            Long requestId = task.getRequestId() == null ? existing.getRequestId() : task.getRequestId();
            LimsTestRequestDO request = validateRequestExists(requestId);
            if (!StringUtils.hasText(task.getTestItem())) {
                task.setTestItem(existing.getTestItem());
            }
            bindEquipmentToTask(task, request, updateReqVO.getEquipmentId());
        }
        clearTaskLifecycleOwnedFields(task);
        taskMapper.updateById(task);
    }

    public void deleteTask(Long id) {
        validateTaskExists(id);
        taskMapper.deleteById(id);
    }

    public LimsWorkflowRespVO getTask(Long id) {
        return BeanUtils.toBean(taskMapper.selectById(id), LimsWorkflowRespVO.class);
    }

    public PageResult<LimsWorkflowRespVO> getTaskPage(LimsWorkflowPageReqVO pageReqVO) {
        return BeanUtils.toBean(taskMapper.selectPage(pageReqVO), LimsWorkflowRespVO.class);
    }

    public void updateTaskStatus(Long id, String status) {
        validateTaskExists(id);
        taskLifecycleService.transition(id, status, resolveTaskEventType(status), "手动更新任务状态", null);
    }

    public void startTask(Long id) {
        taskLifecycleService.start(id);
    }

    public Long scheduleTask(LimsWorkflowSaveReqVO reqVO) {
        return taskScheduleService.schedule(reqVO);
    }

    public Long scheduleTaskDefault(Long id) {
        return taskScheduleService.scheduleDefault(id);
    }

    public void markTaskReady(Long id) {
        taskScheduleService.markReady(id);
    }

    public PageResult<LimsWorkflowRespVO> getTaskSchedulePage(LimsWorkflowPageReqVO pageReqVO) {
        PageResult<LimsTaskScheduleDO> page = taskScheduleService.getSchedulePage(pageReqVO);
        return BeanUtils.toBean(page, LimsWorkflowRespVO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public Long createResult(LimsWorkflowSaveReqVO createReqVO) {
        LimsTestTaskDO task = validateTaskExists(createReqVO.getTaskId());
        LimsTestResultDO result = BeanUtils.toBean(createReqVO, LimsTestResultDO.class);
        fillResultFromTask(result, task);
        if (!StringUtils.hasText(result.getStatus())) {
            result.setStatus("recorded");
        }
        resultMapper.insert(result);
        saveResultValues(result, task);
        taskLifecycleService.transition(task.getId(), LimsTaskStatus.DATA_SUBMITTED,
                LimsTaskEventType.RECORD_SUBMITTED, "检测结果已录入", null);
        taskLifecycleService.transition(task.getId(), LimsTaskStatus.REVIEWING,
                LimsTaskEventType.REVIEW_SUBMITTED, "检测结果待复核", null);
        taskMapper.update(null, new UpdateWrapper<LimsTestTaskDO>()
                .eq("id", task.getId())
                .set("review_status", LimsTaskReviewStatus.PENDING)
                .set("report_eligible", false));
        refreshRequestAfterResults(task.getRequestId());
        return result.getId();
    }

    public void updateResult(LimsWorkflowSaveReqVO updateReqVO) {
        validateResultExists(updateReqVO.getId());
        LimsTestResultDO result = BeanUtils.toBean(updateReqVO, LimsTestResultDO.class);
        clearResultLifecycleOwnedFields(result);
        resultMapper.updateById(result);
    }

    public void deleteResult(Long id) {
        validateResultExists(id);
        resultMapper.deleteById(id);
    }

    public LimsWorkflowRespVO getResult(Long id) {
        return BeanUtils.toBean(resultMapper.selectById(id), LimsWorkflowRespVO.class);
    }

    public PageResult<LimsWorkflowRespVO> getResultPage(LimsWorkflowPageReqVO pageReqVO) {
        return BeanUtils.toBean(resultMapper.selectPage(pageReqVO), LimsWorkflowRespVO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void approveResult(Long id) {
        LimsTestResultDO result = validateResultExists(id);
        resultMapper.update(null, new UpdateWrapper<LimsTestResultDO>()
                .eq("id", id)
                .set("status", "approved")
                .set("reviewed_time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        markTaskReportEligible(result);
        refreshRequestAfterResults(result.getRequestId());
    }

    public Long createReport(LimsWorkflowSaveReqVO createReqVO) {
        LimsTestRequestDO request = validateRequestExists(createReqVO.getRequestId());
        LimsReportDO report = BeanUtils.toBean(createReqVO, LimsReportDO.class);
        report.setRequestNo(request.getRequestNo());
        if (!StringUtils.hasText(report.getStatus())) {
            report.setStatus("draft");
        }
        reportMapper.insert(report);
        return report.getId();
    }

    public void updateReport(LimsWorkflowSaveReqVO updateReqVO) {
        validateReportExists(updateReqVO.getId());
        reportMapper.updateById(BeanUtils.toBean(updateReqVO, LimsReportDO.class));
    }

    public void deleteReport(Long id) {
        validateReportExists(id);
        reportMapper.deleteById(id);
    }

    public LimsWorkflowRespVO getReport(Long id) {
        return BeanUtils.toBean(reportMapper.selectById(id), LimsWorkflowRespVO.class);
    }

    public PageResult<LimsWorkflowRespVO> getReportPage(LimsWorkflowPageReqVO pageReqVO) {
        return BeanUtils.toBean(reportMapper.selectPage(pageReqVO), LimsWorkflowRespVO.class);
    }

    public Long generateTasks(Long requestId) {
        LimsTestRequestDO request = validateRequestExists(requestId);
        persistExecutionPlan(request);
        List<LimsSampleDO> samples = sampleMapper.selectListByRequestId(requestId);
        if (samples.isEmpty()) {
            samples = List.of(createDefaultSample(request));
        }
        List<TestItemConfig> items = getTestItemConfigs(request);
        int created = 0;
        for (LimsSampleDO sample : samples) {
            for (int i = 0; i < items.size(); i++) {
                TestItemConfig item = items.get(i);
                LimsTestTaskDO task = new LimsTestTaskDO();
                task.setRequestId(request.getId());
                task.setRequestNo(request.getRequestNo());
                task.setSampleId(sample.getId());
                task.setSampleNo(sample.getSampleNo());
                task.setTaskNo(request.getRequestNo() + "-T" + String.format("%02d", created + 1));
                task.setTaskName(item.itemName());
                task.setTestItem(item.itemName());
                task.setMethodCode(item.methodCode());
                task.setMethodName(item.methodName());
                task.setStatus(LimsTaskStatus.GENERATED);
                task.setTaskStatus(LimsTaskStatus.GENERATED);
                task.setScheduleStatus(LimsTaskScheduleStatus.UNSCHEDULED);
                task.setDurationMinutes(item.durationMinutes());
                task.setMethodSnapshot(writeJson(item));
                task.setQcStatus(LimsTaskReviewStatus.NONE);
                task.setReviewStatus(LimsTaskReviewStatus.NONE);
                task.setReportEligible(false);
                bindEquipmentToTask(task, request, null);
                taskMapper.insert(task);
                created++;
            }
        }
        requestMapper.update(null, new UpdateWrapper<LimsTestRequestDO>().eq("id", requestId).set("status", "task_generated"));
        return (long) created;
    }

    private void persistExecutionPlan(LimsTestRequestDO request) {
        String planJson = executionPlanFactory.createPlanJson(resolveWorkflowSnapshot(request));
        LimsExecutionPlanDO existing = executionPlanMapper.selectByRequestId(request.getId());
        LimsExecutionPlanDO executionPlan = new LimsExecutionPlanDO();
        executionPlan.setRequestId(request.getId());
        executionPlan.setWorkflowSnapshotHash(request.getWorkflowSnapshotHash());
        executionPlan.setPlanJson(planJson);
        executionPlan.setStatus("generated");
        if (existing == null) {
            executionPlanMapper.insert(executionPlan);
        } else {
            executionPlan.setId(existing.getId());
            executionPlanMapper.updateById(executionPlan);
        }
    }

    public Long generateReport(Long requestId) {
        LimsTestRequestDO request = validateRequestExists(requestId);
        reportEligibilityService.assertRequestReportable(requestId);
        LimsReportDO existingReport = reportMapper.selectByRequestId(requestId);
        if (existingReport != null) {
            return existingReport.getId();
        }
        List<LimsTestResultDO> results = resultMapper.selectListByRequestId(requestId);
        LimsReportDO report = new LimsReportDO();
        report.setRequestId(requestId);
        report.setRequestNo(request.getRequestNo());
        report.setReportNo("RPT-" + request.getRequestNo());
        report.setReportName(request.getRequestName() + "检测报告");
        report.setConclusion(resolveReportConclusion(results));
        String reportContent = buildReportContent(request, results);
        attachReportOutputs(report, request, reportContent);
        report.setReportContent(reportContent);
        report.setDataSnapshot(reportContent);
        report.setDataSnapshotHash(sha256(reportContent));
        report.setWorkflowSnapshotHash(request.getWorkflowSnapshotHash());
        report.setStatus("generated");
        reportMapper.insert(report);
        requestMapper.update(null, new UpdateWrapper<LimsTestRequestDO>().eq("id", requestId).set("status", "report_generated"));
        return report.getId();
    }

    private void attachReportOutputs(LimsReportDO report, LimsTestRequestDO request, String reportContent) {
        JsonNode workflowSnapshot = readObject(resolveWorkflowSnapshot(request));
        ObjectNode reportDraftPlan = reportDraftPlanFactory.createReportDraftPlan(workflowSnapshot);
        String generatedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        ReportOutputBundle outputBundle = reportOutputGenerator.generate(new ReportOutputRequest(
                report.getReportNo(),
                report.getReportName(),
                report.getConclusion(),
                reportContent,
                reportDraftPlan,
                generatedAt));
        report.setTemplateVersion(reportDraftPlan.path("templateVersion").asText(report.getTemplateVersion()));
        report.setReportOutput(writeJson(outputBundle));
        report.setFileUrl(outputBundle.primaryFileUrl());
    }

    public void issueReport(Long id) {
        LimsReportDO report = validateReportExists(id);
        LimsTestRequestDO request = validateRequestExists(report.getRequestId());
        String issuedTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        reportMapper.update(null, new UpdateWrapper<LimsReportDO>()
                .eq("id", id)
                .set("status", "issued")
                .set("issued_time", issuedTime));
        reportEvidenceGateway.registerIssuedReportEvidence(new IssuedReportEvidence(
                report.getId(),
                report.getReportNo(),
                report.getReportName(),
                request.getId(),
                request.getRequestNo(),
                report.getFileUrl(),
                StringUtils.hasText(report.getDataSnapshotHash()) ? report.getDataSnapshotHash() : sha256(report.getDataSnapshot()),
                issuedTime,
                "检测报告已签发：" + report.getReportNo()));
        requestMapper.update(null, new UpdateWrapper<LimsTestRequestDO>().eq("id", report.getRequestId()).set("status", "completed"));
    }

    private LimsSampleDO createDefaultSample(LimsTestRequestDO request) {
        LimsSampleDO sample = new LimsSampleDO();
        sample.setRequestId(request.getId());
        sample.setRequestNo(request.getRequestNo());
        sample.setSampleNo(request.getRequestNo() + "-S01");
        sample.setSampleName(request.getRequestName() + "样品");
        sample.setSampleType(request.getDomainCode());
        sample.setSampleQty("1");
        sample.setStatus("received");
        sampleMapper.insert(sample);
        return sample;
    }

    private void fillResultFromTask(LimsTestResultDO result, LimsTestTaskDO task) {
        result.setRequestId(task.getRequestId());
        result.setRequestNo(task.getRequestNo());
        result.setSampleId(task.getSampleId());
        result.setSampleNo(task.getSampleNo());
        result.setTaskNo(task.getTaskNo());
        result.setTestItem(task.getTestItem());
        if (!StringUtils.hasText(result.getResultNo())) {
            result.setResultNo(task.getTaskNo() + "-R");
        }
    }

    private void markTaskReportEligible(LimsTestResultDO result) {
        LimsTestTaskDO task = taskMapper.selectById(result.getTaskId());
        if (task == null) {
            return;
        }
        taskLifecycleService.transition(task.getId(), LimsTaskStatus.APPROVED,
                LimsTaskEventType.APPROVED, "检测结果审核通过", null);
        LimsTestTaskDO updateTask = new LimsTestTaskDO();
        updateTask.setId(task.getId());
        updateTask.setReviewStatus(LimsTaskReviewStatus.APPROVED);
        updateTask.setReportEligible(true);
        updateTask.setBlockReason(null);
        taskMapper.updateById(updateTask);
    }

    private void clearTaskLifecycleOwnedFields(LimsTestTaskDO task) {
        task.setStatus(null);
        task.setTaskStatus(null);
        task.setScheduleStatus(null);
        task.setActualStartTime(null);
        task.setActualEndTime(null);
        task.setReadinessSnapshot(null);
        task.setReviewStatus(null);
        task.setQcStatus(null);
        task.setReportEligible(null);
        task.setBlockReason(null);
        task.setMethodSnapshot(null);
    }

    private void clearResultLifecycleOwnedFields(LimsTestResultDO result) {
        result.setStatus(null);
        result.setReviewerId(null);
        result.setReviewedTime(null);
    }

    private String resolveTaskEventType(String status) {
        if (LimsTaskStatus.SCHEDULED.equals(status)) {
            return LimsTaskEventType.SCHEDULED;
        }
        if (LimsTaskStatus.ASSIGNED.equals(status)) {
            return LimsTaskEventType.ASSIGNED;
        }
        if (LimsTaskStatus.READY.equals(status)) {
            return LimsTaskEventType.READINESS_PASSED;
        }
        if (LimsTaskStatus.TESTING.equals(status)) {
            return LimsTaskEventType.STARTED;
        }
        if (LimsTaskStatus.DATA_SUBMITTED.equals(status)) {
            return LimsTaskEventType.RECORD_SUBMITTED;
        }
        if (LimsTaskStatus.REVIEWING.equals(status)) {
            return LimsTaskEventType.REVIEW_SUBMITTED;
        }
        if (LimsTaskStatus.APPROVED.equals(status)) {
            return LimsTaskEventType.APPROVED;
        }
        if (LimsTaskStatus.COMPLETED.equals(status)) {
            return LimsTaskEventType.COMPLETED;
        }
        if (LimsTaskStatus.REPORTED.equals(status)) {
            return LimsTaskEventType.REPORTED;
        }
        if (LimsTaskStatus.HOLD.equals(status)) {
            return LimsTaskEventType.HOLD;
        }
        if (LimsTaskStatus.CANCELLED.equals(status) || LimsTaskStatus.REWORK.equals(status)) {
            return LimsTaskEventType.REJECTED;
        }
        return LimsTaskEventType.RESUMED;
    }

    private void bindEquipmentToTask(LimsTestTaskDO task, LimsTestRequestDO request, Long requestedEquipmentId) {
        List<AvailableEquipment> availableEquipment = equipmentGateway.getAvailableEquipment(request.getDomainCode(), task.getTestItem());
        if (availableEquipment.isEmpty()) {
            if (requestedEquipmentId == null) {
                return;
            }
            throw exception(TEST_TASK_EQUIPMENT_UNAVAILABLE);
        }
        AvailableEquipment selected = requestedEquipmentId == null ? availableEquipment.get(0)
                : availableEquipment.stream()
                .filter(equipment -> requestedEquipmentId.equals(equipment.equipmentId()))
                .findFirst()
                .orElseThrow(() -> exception(TEST_TASK_EQUIPMENT_UNAVAILABLE));
        task.setEquipmentId(selected.equipmentId());
        task.setEquipmentCode(selected.equipmentCode());
        task.setEquipmentName(selected.equipmentName());
        task.setEquipmentSnapshot(writeJson(selected));
        List<CalibrationEvidence> evidence = equipmentGateway.getCurrentCalibrationEvidence(selected.equipmentId());
        task.setEquipmentEvidenceSnapshot(writeJson(evidence));
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to serialize LIMS snapshot", ex);
        }
    }

    private void saveResultValues(LimsTestResultDO result, LimsTestTaskDO task) {
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

    private void refreshRequestAfterResults(Long requestId) {
        List<LimsTestTaskDO> tasks = taskMapper.selectListByRequestId(requestId);
        if (!tasks.isEmpty() && tasks.stream().allMatch(task -> Boolean.TRUE.equals(task.getReportEligible())
                || LimsTaskStatus.REPORT_ALLOWED.contains(StringUtils.hasText(task.getTaskStatus()) ? task.getTaskStatus() : task.getStatus()))) {
            requestMapper.update(null, new UpdateWrapper<LimsTestRequestDO>().eq("id", requestId).set("status", "result_recorded"));
        }
    }

    private void preserveWorkflowSnapshot(LimsTestRequestDO request, LimsTestRequestDO existing, LabDomainPackSnapshotDTO pack) {
        String workflowSnapshot = existing.getWorkflowSnapshot();
        WorkflowSnapshot fallbackSnapshot = null;
        if (!StringUtils.hasText(workflowSnapshot)) {
            if (StringUtils.hasText(existing.getScenarioConfig())) {
                workflowSnapshot = existing.getScenarioConfig();
            } else {
                fallbackSnapshot = workflowSnapshotFactory.createSnapshot(pack, null);
                workflowSnapshot = fallbackSnapshot.getSnapshotJson();
            }
        }
        request.setDomainPackId(existing.getDomainPackId());
        request.setDomainPackCode(StringUtils.hasText(existing.getDomainPackCode()) ? existing.getDomainPackCode() : pack.getPackCode());
        request.setDomainPackVersion(StringUtils.hasText(existing.getDomainPackVersion()) ? existing.getDomainPackVersion() : pack.getPackVersion());
        request.setWorkflowSnapshot(workflowSnapshot);
        request.setWorkflowSnapshotHash(StringUtils.hasText(existing.getWorkflowSnapshotHash()) ? existing.getWorkflowSnapshotHash()
                : fallbackSnapshot != null ? fallbackSnapshot.getSnapshotHash() : sha256(workflowSnapshot));
        request.setWorkflowSnapshotTime(StringUtils.hasText(existing.getWorkflowSnapshotTime()) ? existing.getWorkflowSnapshotTime()
                : fallbackSnapshot != null ? fallbackSnapshot.getFrozenAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                : LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        request.setScenarioConfig(workflowSnapshot);
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

    private List<TestItemConfig> getTestItemConfigs(LimsTestRequestDO request) {
        JsonNode snapshot = readObject(resolveWorkflowSnapshot(request));
        JsonNode configuredItems = snapshot.path("testItems");
        if (!configuredItems.isArray() || configuredItems.isEmpty()) {
            configuredItems = snapshot.path("workflow").path("testItems");
        }
        List<TestItemConfig> items = new ArrayList<>();
        JsonNode workflowItems = snapshot.path("workflow").path("testItems");
        if (configuredItems.isArray()) {
            configuredItems.forEach(item -> items.add(new TestItemConfig(
                    item.path("itemName").asText(item.path("name").asText("常规检测")),
                    item.path("methodCode").asText("METHOD"),
                    item.path("methodName").asText("配置方法"),
                    resolveDurationMinutes(item, workflowItems))));
        }
        if (!items.isEmpty()) {
            return items;
        }
        if ("ENVIRONMENT".equalsIgnoreCase(request.getDomainCode())) {
            return List.of(new TestItemConfig("pH", "HJ-1147", "水质 pH 测定", 60L),
                    new TestItemConfig("COD", "HJ-828", "化学需氧量测定", 120L));
        }
        if ("INDUSTRIAL".equalsIgnoreCase(request.getDomainCode())) {
            return List.of(new TestItemConfig("尺寸检查", "DIM", "尺寸测量", 60L),
                    new TestItemConfig("可靠性试验", "REL", "可靠性试验方法", 240L));
        }
        return List.of(new TestItemConfig("感官检查", "FOOD-SENSE", "食品感官检查", 60L),
                new TestItemConfig("水分", "GB5009.3", "食品中水分测定", 90L));
    }

    private Long resolveDurationMinutes(JsonNode item, JsonNode workflowItems) {
        JsonNode durationSource = item;
        if (!item.hasNonNull("estimatedDurationMinutes") && workflowItems.isArray()) {
            String itemCode = item.path("itemCode").asText("");
            String itemName = item.path("itemName").asText(item.path("name").asText(""));
            for (JsonNode workflowItem : workflowItems) {
                boolean sameCode = StringUtils.hasText(itemCode) && itemCode.equals(workflowItem.path("itemCode").asText(""));
                boolean sameName = StringUtils.hasText(itemName)
                        && (itemName.equals(workflowItem.path("itemName").asText(""))
                        || itemName.equals(workflowItem.path("name").asText("")));
                if (sameCode || sameName) {
                    durationSource = workflowItem;
                    break;
                }
            }
        }
        long durationMinutes = durationSource.path("estimatedDurationMinutes").asLong(60L);
        return Math.max(durationMinutes, 1L);
    }

    private String buildReportContent(LimsTestRequestDO request, List<LimsTestResultDO> results) {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("requestNo", request.getRequestNo());
        root.put("requestName", request.getRequestName());
        root.put("domainPackCode", request.getDomainPackCode());
        root.put("domainPackVersion", request.getDomainPackVersion());
        root.put("workflowSnapshotHash", request.getWorkflowSnapshotHash());
        root.set("workflowSnapshot", readObject(resolveWorkflowSnapshot(request)));
        ArrayNode resultArray = root.putArray("results");
        for (LimsTestResultDO result : results) {
            ObjectNode node = resultArray.addObject();
            node.put("sampleNo", result.getSampleNo());
            node.put("testItem", result.getTestItem());
            node.put("resultValue", result.getResultValue());
            node.put("resultUnit", result.getResultUnit());
            node.put("conclusion", result.getResultConclusion());
        }
        ArrayNode taskArray = root.putArray("tasks");
        ArrayNode equipmentEvidenceArray = root.putArray("equipmentEvidenceSnapshots");
        for (LimsTestTaskDO task : taskMapper.selectListByRequestId(request.getId())) {
            ObjectNode node = taskArray.addObject();
            node.put("taskNo", task.getTaskNo());
            node.put("taskName", task.getTaskName());
            node.put("testItem", task.getTestItem());
            node.put("equipmentId", task.getEquipmentId());
            node.put("equipmentCode", task.getEquipmentCode());
            node.put("equipmentName", task.getEquipmentName());
            node.set("equipmentSnapshot", readObject(task.getEquipmentSnapshot()));
            JsonNode evidenceSnapshot = readObject(task.getEquipmentEvidenceSnapshot());
            node.set("equipmentEvidenceSnapshot", evidenceSnapshot);
            if (task.getEquipmentId() != null && !evidenceSnapshot.isMissingNode() && !evidenceSnapshot.isNull()) {
                ObjectNode evidenceNode = equipmentEvidenceArray.addObject();
                evidenceNode.put("taskNo", task.getTaskNo());
                evidenceNode.put("equipmentId", task.getEquipmentId());
                evidenceNode.put("equipmentCode", task.getEquipmentCode());
                evidenceNode.set("evidence", evidenceSnapshot);
            }
        }
        ArrayNode resultValueArray = root.putArray("resultValues");
        for (LimsTestResultValueDO value : resultValueMapper.selectListByRequestId(request.getId())) {
            ObjectNode node = resultValueArray.addObject();
            node.put("taskNo", value.getTaskNo());
            node.put("testItem", value.getTestItem());
            node.put("fieldCode", value.getFieldCode());
            node.put("fieldName", value.getFieldName());
            node.put("displayValue", value.getDisplayValue());
            node.put("conclusion", value.getConclusion());
        }
        return root.toString();
    }

    private String resolveReportConclusion(List<LimsTestResultDO> results) {
        if (results.isEmpty()) {
            return "待补充检测结果";
        }
        boolean allPass = results.stream().allMatch(result -> !StringUtils.hasText(result.getResultConclusion())
                || "pass".equalsIgnoreCase(result.getResultConclusion())
                || "合格".equals(result.getResultConclusion()));
        return allPass ? "合格" : "需复核";
    }

    private LabDomainPackSnapshotDTO validateDomainPack(Long id) {
        if (id == null) {
            throw exception(DOMAIN_PACK_REQUIRED);
        }
        return domainPackGateway.getPublishedPackSnapshot(id);
    }

    private String resolveWorkflowSnapshot(LimsTestRequestDO request) {
        return StringUtils.hasText(request.getWorkflowSnapshot()) ? request.getWorkflowSnapshot() : request.getScenarioConfig();
    }

    private String resolveDefaultRequestSourceType(String requestType) {
        if ("third_party".equalsIgnoreCase(requestType) || "external".equalsIgnoreCase(requestType)) {
            return "THIRD_PARTY_ORDER";
        }
        return "INTERNAL_DEPARTMENT";
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest((value == null ? "" : value).getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm is unavailable", ex);
        }
    }

    private void validateRequestPayload(LimsWorkflowSaveReqVO reqVO) {
        if (!StringUtils.hasText(reqVO.getRequestNo())) {
            throw exception(TEST_REQUEST_NO_REQUIRED);
        }
    }

    private LimsTestRequestDO validateRequestExists(Long id) {
        LimsTestRequestDO request = id == null ? null : requestMapper.selectById(id);
        if (request == null) {
            throw exception(TEST_REQUEST_NOT_EXISTS);
        }
        return request;
    }

    private void validateRequestNoUnique(Long id, String requestNo) {
        LimsTestRequestDO request = requestMapper.selectByRequestNo(requestNo);
        if (request == null) {
            return;
        }
        if (id == null || !request.getId().equals(id)) {
            throw exception(TEST_REQUEST_NO_DUPLICATE);
        }
    }

    private LimsSampleDO validateSampleExists(Long id) {
        LimsSampleDO sample = id == null ? null : sampleMapper.selectById(id);
        if (sample == null) {
            throw exception(SAMPLE_NOT_EXISTS);
        }
        return sample;
    }

    private LimsTestTaskDO validateTaskExists(Long id) {
        LimsTestTaskDO task = id == null ? null : taskMapper.selectById(id);
        if (task == null) {
            throw exception(TEST_TASK_NOT_EXISTS);
        }
        return task;
    }

    private LimsTestResultDO validateResultExists(Long id) {
        LimsTestResultDO result = id == null ? null : resultMapper.selectById(id);
        if (result == null) {
            throw exception(TEST_RESULT_NOT_EXISTS);
        }
        return result;
    }

    private LimsReportDO validateReportExists(Long id) {
        LimsReportDO report = id == null ? null : reportMapper.selectById(id);
        if (report == null) {
            throw exception(TEST_REPORT_NOT_EXISTS);
        }
        return report;
    }

    private record TestItemConfig(String itemName, String methodCode, String methodName, Long durationMinutes) {
    }

}
