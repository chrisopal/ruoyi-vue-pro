package cn.iocoder.yudao.module.lims.controller.admin.workflow;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsExecutionPlanRespVO;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsTaskQualityGateRespVO;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsWorkflowPageReqVO;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsWorkflowRespVO;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsWorkflowSaveReqVO;
import cn.iocoder.yudao.module.lims.service.workflow.LimsWorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - LIMS 检测业务闭环")
@RestController
@Validated
public class LimsWorkflowController {

    @Resource
    private LimsWorkflowService workflowService;

    @GetMapping("/lims/request/page")
    @Operation(summary = "获得检测需求分页")
    @PreAuthorize("@ss.hasPermission('lims:request:query')")
    public CommonResult<PageResult<LimsWorkflowRespVO>> getRequestPage(@Valid LimsWorkflowPageReqVO pageReqVO) {
        return success(workflowService.getRequestPage(pageReqVO));
    }

    @GetMapping("/lims/request/get")
    @Operation(summary = "获得检测需求")
    @PreAuthorize("@ss.hasPermission('lims:request:query')")
    public CommonResult<LimsWorkflowRespVO> getRequest(@RequestParam("id") Long id) {
        return success(workflowService.getRequest(id));
    }

    @PostMapping("/lims/request/create")
    @Operation(summary = "创建检测需求")
    @PreAuthorize("@ss.hasPermission('lims:request:create')")
    public CommonResult<Long> createRequest(@Valid @RequestBody LimsWorkflowSaveReqVO createReqVO) {
        return success(workflowService.createRequest(createReqVO));
    }

    @PutMapping("/lims/request/update")
    @Operation(summary = "更新检测需求")
    @PreAuthorize("@ss.hasPermission('lims:request:update')")
    public CommonResult<Boolean> updateRequest(@Valid @RequestBody LimsWorkflowSaveReqVO updateReqVO) {
        workflowService.updateRequest(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/lims/request/delete")
    @Operation(summary = "删除检测需求")
    @PreAuthorize("@ss.hasPermission('lims:request:delete')")
    public CommonResult<Boolean> deleteRequest(@RequestParam("id") Long id) {
        workflowService.deleteRequest(id);
        return success(true);
    }

    @PutMapping("/lims/request/submit")
    @Operation(summary = "提交检测需求")
    @PreAuthorize("@ss.hasPermission('lims:request:update')")
    public CommonResult<Boolean> submitRequest(@RequestParam("id") Long id) {
        workflowService.updateRequestStatus(id, "submitted");
        return success(true);
    }

    @PutMapping("/lims/request/accept")
    @Operation(summary = "受理检测需求")
    @PreAuthorize("@ss.hasPermission('lims:request:update')")
    public CommonResult<Boolean> acceptRequest(@RequestParam("id") Long id) {
        workflowService.updateRequestStatus(id, "accepted");
        return success(true);
    }

    @PostMapping("/lims/request/generate-tasks")
    @Operation(summary = "按检测场景方案包生成样品与检测任务")
    @PreAuthorize("@ss.hasPermission('lims:request:update')")
    public CommonResult<Long> generateTasks(@RequestParam("id") Long id) {
        return success(workflowService.generateTasks(id));
    }

    @PostMapping("/lims/request/generate-execution-plan")
    @Operation(summary = "生成执行计划、样品要求和检测任务")
    @PreAuthorize("@ss.hasPermission('lims:request:update')")
    public CommonResult<Long> generateExecutionPlan(@RequestParam("id") Long id) {
        return success(workflowService.generateTasks(id));
    }

    @GetMapping("/lims/request/execution-plan")
    @Operation(summary = "获得检测需求执行计划")
    @PreAuthorize("@ss.hasPermission('lims:request:query')")
    public CommonResult<LimsExecutionPlanRespVO> getExecutionPlan(@RequestParam("id") Long id) {
        return success(workflowService.getExecutionPlan(id));
    }

    @PostMapping("/lims/request/generate-report")
    @Operation(summary = "生成检测报告")
    @PreAuthorize("@ss.hasPermission('lims:report:create')")
    public CommonResult<Long> generateReport(@RequestParam("id") Long id) {
        return success(workflowService.generateReport(id));
    }

    @GetMapping("/lims/sample/page")
    @Operation(summary = "获得样品分页")
    @PreAuthorize("@ss.hasPermission('lims:sample:query')")
    public CommonResult<PageResult<LimsWorkflowRespVO>> getSamplePage(@Valid LimsWorkflowPageReqVO pageReqVO) {
        return success(workflowService.getSamplePage(pageReqVO));
    }

    @GetMapping("/lims/sample/get")
    @Operation(summary = "获得样品")
    @PreAuthorize("@ss.hasPermission('lims:sample:query')")
    public CommonResult<LimsWorkflowRespVO> getSample(@RequestParam("id") Long id) {
        return success(workflowService.getSample(id));
    }

    @PostMapping("/lims/sample/create")
    @Operation(summary = "创建样品")
    @PreAuthorize("@ss.hasPermission('lims:sample:create')")
    public CommonResult<Long> createSample(@Valid @RequestBody LimsWorkflowSaveReqVO createReqVO) {
        return success(workflowService.createSample(createReqVO));
    }

    @PutMapping("/lims/sample/update")
    @Operation(summary = "更新样品")
    @PreAuthorize("@ss.hasPermission('lims:sample:update')")
    public CommonResult<Boolean> updateSample(@Valid @RequestBody LimsWorkflowSaveReqVO updateReqVO) {
        workflowService.updateSample(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/lims/sample/delete")
    @Operation(summary = "删除样品")
    @PreAuthorize("@ss.hasPermission('lims:sample:delete')")
    public CommonResult<Boolean> deleteSample(@RequestParam("id") Long id) {
        workflowService.deleteSample(id);
        return success(true);
    }

    @GetMapping("/lims/task/page")
    @Operation(summary = "获得检测任务分页")
    @PreAuthorize("@ss.hasPermission('lims:task:query')")
    public CommonResult<PageResult<LimsWorkflowRespVO>> getTaskPage(@Valid LimsWorkflowPageReqVO pageReqVO) {
        return success(workflowService.getTaskPage(pageReqVO));
    }

    @GetMapping("/lims/task/get")
    @Operation(summary = "获得检测任务")
    @PreAuthorize("@ss.hasPermission('lims:task:query')")
    public CommonResult<LimsWorkflowRespVO> getTask(@RequestParam("id") Long id) {
        return success(workflowService.getTask(id));
    }

    @GetMapping("/lims/task/quality-gate")
    @Operation(summary = "获得检测任务质量门禁")
    @PreAuthorize("@ss.hasPermission('lims:task:query')")
    public CommonResult<LimsTaskQualityGateRespVO> getTaskQualityGate(@RequestParam("id") Long id) {
        return success(workflowService.getTaskQualityGate(id));
    }

    @PostMapping("/lims/task/create")
    @Operation(summary = "创建检测任务")
    @PreAuthorize("@ss.hasPermission('lims:task:create')")
    public CommonResult<Long> createTask(@Valid @RequestBody LimsWorkflowSaveReqVO createReqVO) {
        return success(workflowService.createTask(createReqVO));
    }

    @PutMapping("/lims/task/update")
    @Operation(summary = "更新检测任务")
    @PreAuthorize("@ss.hasPermission('lims:task:update')")
    public CommonResult<Boolean> updateTask(@Valid @RequestBody LimsWorkflowSaveReqVO updateReqVO) {
        workflowService.updateTask(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/lims/task/delete")
    @Operation(summary = "删除检测任务")
    @PreAuthorize("@ss.hasPermission('lims:task:delete')")
    public CommonResult<Boolean> deleteTask(@RequestParam("id") Long id) {
        workflowService.deleteTask(id);
        return success(true);
    }

    @PutMapping("/lims/task/start")
    @Operation(summary = "开始检测任务")
    @PreAuthorize("@ss.hasPermission('lims:task:readiness')")
    public CommonResult<Boolean> startTask(@RequestParam("id") Long id) {
        workflowService.startTask(id);
        return success(true);
    }

    @PutMapping("/lims/task/hold")
    @Operation(summary = "挂起检测任务")
    @PreAuthorize("@ss.hasPermission('lims:task:hold')")
    public CommonResult<Boolean> holdTask(@RequestParam("id") Long id,
                                          @RequestParam(value = "reason", required = false) String reason) {
        workflowService.holdTask(id, reason);
        return success(true);
    }

    @PostMapping("/lims/task/schedule")
    @Operation(summary = "检测任务排程")
    @PreAuthorize("@ss.hasPermission('lims:task:schedule')")
    public CommonResult<Long> scheduleTask(@Valid @RequestBody LimsWorkflowSaveReqVO reqVO) {
        return success(workflowService.scheduleTask(reqVO));
    }

    @PostMapping("/lims/task/schedule-default")
    @Operation(summary = "检测任务快速排程")
    @PreAuthorize("@ss.hasPermission('lims:task:schedule')")
    public CommonResult<Long> scheduleTaskDefault(@RequestParam("id") Long id) {
        return success(workflowService.scheduleTaskDefault(id));
    }

    @PutMapping("/lims/task/ready")
    @Operation(summary = "确认检测任务就绪")
    @PreAuthorize("@ss.hasPermission('lims:task:readiness')")
    public CommonResult<Boolean> markTaskReady(@RequestParam("id") Long id) {
        workflowService.markTaskReady(id);
        return success(true);
    }

    @PostMapping("/lims/task/raw-record")
    @Operation(summary = "提交检测任务原始记录")
    @PreAuthorize("@ss.hasPermission('lims:task:record')")
    public CommonResult<Long> submitRawRecord(@Valid @RequestBody LimsWorkflowSaveReqVO reqVO) {
        return success(workflowService.submitRawRecord(reqVO));
    }

    @PostMapping("/lims/task/qc-record")
    @Operation(summary = "提交检测任务质控记录")
    @PreAuthorize("@ss.hasPermission('lims:task:record')")
    public CommonResult<Long> submitQcRecord(@Valid @RequestBody LimsWorkflowSaveReqVO reqVO) {
        return success(workflowService.submitQcRecord(reqVO));
    }

    @PutMapping("/lims/task/approve")
    @Operation(summary = "技术复核通过检测任务")
    @PreAuthorize("@ss.hasPermission('lims:task:review')")
    public CommonResult<Boolean> approveTaskReview(@Valid @RequestBody LimsWorkflowSaveReqVO reqVO) {
        workflowService.approveTaskReview(reqVO);
        return success(true);
    }

    @PutMapping("/lims/task/reject")
    @Operation(summary = "技术复核驳回检测任务")
    @PreAuthorize("@ss.hasPermission('lims:task:review')")
    public CommonResult<Boolean> rejectTaskReview(@Valid @RequestBody LimsWorkflowSaveReqVO reqVO) {
        workflowService.rejectTaskReview(reqVO);
        return success(true);
    }

    @GetMapping("/lims/task/reviews")
    @Operation(summary = "获得检测任务技术复核记录")
    @PreAuthorize("@ss.hasPermission('lims:task:query')")
    public CommonResult<List<LimsWorkflowRespVO>> getTaskReviews(@RequestParam("taskId") Long taskId) {
        return success(workflowService.getTaskReviews(taskId));
    }

    @GetMapping("/lims/task/schedule/page")
    @Operation(summary = "获得检测任务排程分页")
    @PreAuthorize("@ss.hasPermission('lims:task:query')")
    public CommonResult<PageResult<LimsWorkflowRespVO>> getTaskSchedulePage(@Valid LimsWorkflowPageReqVO pageReqVO) {
        return success(workflowService.getTaskSchedulePage(pageReqVO));
    }

    @GetMapping("/lims/result/page")
    @Operation(summary = "获得检测结果分页")
    @PreAuthorize("@ss.hasPermission('lims:result:query')")
    public CommonResult<PageResult<LimsWorkflowRespVO>> getResultPage(@Valid LimsWorkflowPageReqVO pageReqVO) {
        return success(workflowService.getResultPage(pageReqVO));
    }

    @GetMapping("/lims/result/get")
    @Operation(summary = "获得检测结果")
    @PreAuthorize("@ss.hasPermission('lims:result:query')")
    public CommonResult<LimsWorkflowRespVO> getResult(@RequestParam("id") Long id) {
        return success(workflowService.getResult(id));
    }

    @PostMapping("/lims/result/create")
    @Operation(summary = "录入检测结果")
    @PreAuthorize("@ss.hasPermission('lims:result:create')")
    public CommonResult<Long> createResult(@Valid @RequestBody LimsWorkflowSaveReqVO createReqVO) {
        return success(workflowService.createResult(createReqVO));
    }

    @PutMapping("/lims/result/update")
    @Operation(summary = "更新检测结果")
    @PreAuthorize("@ss.hasPermission('lims:result:update')")
    public CommonResult<Boolean> updateResult(@Valid @RequestBody LimsWorkflowSaveReqVO updateReqVO) {
        workflowService.updateResult(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/lims/result/delete")
    @Operation(summary = "删除检测结果")
    @PreAuthorize("@ss.hasPermission('lims:result:delete')")
    public CommonResult<Boolean> deleteResult(@RequestParam("id") Long id) {
        workflowService.deleteResult(id);
        return success(true);
    }

    @PutMapping("/lims/result/approve")
    @Operation(summary = "审核检测结果")
    @PreAuthorize("@ss.hasPermission('lims:result:update')")
    public CommonResult<Boolean> approveResult(@RequestParam("id") Long id) {
        workflowService.approveResult(id);
        return success(true);
    }

    @GetMapping("/lims/report/page")
    @Operation(summary = "获得检测报告分页")
    @PreAuthorize("@ss.hasPermission('lims:report:query')")
    public CommonResult<PageResult<LimsWorkflowRespVO>> getReportPage(@Valid LimsWorkflowPageReqVO pageReqVO) {
        return success(workflowService.getReportPage(pageReqVO));
    }

    @GetMapping("/lims/report/get")
    @Operation(summary = "获得检测报告")
    @PreAuthorize("@ss.hasPermission('lims:report:query')")
    public CommonResult<LimsWorkflowRespVO> getReport(@RequestParam("id") Long id) {
        return success(workflowService.getReport(id));
    }

    @PostMapping("/lims/report/create")
    @Operation(summary = "创建检测报告")
    @PreAuthorize("@ss.hasPermission('lims:report:create')")
    public CommonResult<Long> createReport(@Valid @RequestBody LimsWorkflowSaveReqVO createReqVO) {
        return success(workflowService.createReport(createReqVO));
    }

    @PutMapping("/lims/report/update")
    @Operation(summary = "更新检测报告")
    @PreAuthorize("@ss.hasPermission('lims:report:update')")
    public CommonResult<Boolean> updateReport(@Valid @RequestBody LimsWorkflowSaveReqVO updateReqVO) {
        workflowService.updateReport(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/lims/report/delete")
    @Operation(summary = "删除检测报告")
    @PreAuthorize("@ss.hasPermission('lims:report:delete')")
    public CommonResult<Boolean> deleteReport(@RequestParam("id") Long id) {
        workflowService.deleteReport(id);
        return success(true);
    }

    @PutMapping("/lims/report/issue")
    @Operation(summary = "签发检测报告")
    @PreAuthorize("@ss.hasPermission('lims:report:update')")
    public CommonResult<Boolean> issueReport(@RequestParam("id") Long id) {
        workflowService.issueReport(id);
        return success(true);
    }

}
