package cn.iocoder.yudao.module.lab.controller.admin.quality;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.lab.controller.admin.quality.vo.LabQualityRecordPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.quality.vo.LabQualityRecordRespVO;
import cn.iocoder.yudao.module.lab.controller.admin.quality.vo.LabQualityRecordSaveReqVO;
import cn.iocoder.yudao.module.lab.service.equipment.dto.LabEquipmentCalibrationEvidenceDTO;
import cn.iocoder.yudao.module.lab.service.quality.dto.LabPersonnelAuthorizationSummaryDTO;
import cn.iocoder.yudao.module.lab.service.quality.LabQualityRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 实验室符合性管理")
@RestController
@Validated
public class LabQualityRecordController {

    @Resource
    private LabQualityRecordService qualityRecordService;

    @GetMapping("/lab/clause-mapping/page")
    @Operation(summary = "获得条款功能映射分页")
    @PreAuthorize("@ss.hasPermission('lab:clause-mapping:query')")
    public CommonResult<PageResult<LabQualityRecordRespVO>> getClauseMappingPage(@Valid LabQualityRecordPageReqVO pageReqVO) {
        return success(qualityRecordService.getClauseMappingPage(pageReqVO));
    }

    @GetMapping("/lab/clause-mapping/get")
    @Operation(summary = "获得条款功能映射")
    @PreAuthorize("@ss.hasPermission('lab:clause-mapping:query')")
    public CommonResult<LabQualityRecordRespVO> getClauseMapping(@RequestParam("id") Long id) {
        return success(qualityRecordService.getClauseMapping(id));
    }

    @PostMapping("/lab/clause-mapping/create")
    @Operation(summary = "创建条款功能映射")
    @PreAuthorize("@ss.hasPermission('lab:clause-mapping:create')")
    public CommonResult<Long> createClauseMapping(@Valid @RequestBody LabQualityRecordSaveReqVO createReqVO) {
        return success(qualityRecordService.createClauseMapping(createReqVO));
    }

    @PutMapping("/lab/clause-mapping/update")
    @Operation(summary = "更新条款功能映射")
    @PreAuthorize("@ss.hasPermission('lab:clause-mapping:update')")
    public CommonResult<Boolean> updateClauseMapping(@Valid @RequestBody LabQualityRecordSaveReqVO updateReqVO) {
        qualityRecordService.updateClauseMapping(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/lab/clause-mapping/delete")
    @Operation(summary = "删除条款功能映射")
    @PreAuthorize("@ss.hasPermission('lab:clause-mapping:delete')")
    public CommonResult<Boolean> deleteClauseMapping(@RequestParam("id") Long id) {
        qualityRecordService.deleteClauseMapping(id);
        return success(true);
    }

    @GetMapping({
            "/lab/clause-mapping/export-excel",
            "/lab/compliance-check/export-excel",
            "/lab/compliance-check-item/export-excel",
            "/lab/personnel-competence/export-excel",
            "/lab/personnel-authorization/export-excel",
            "/lab/equipment-traceability/export-excel",
            "/lab/equipment-intermediate-check/export-excel",
            "/lab/environment-record/export-excel",
            "/lab/method-validation/export-excel",
            "/lab/nonconformity/export-excel",
            "/lab/corrective-action/export-excel",
            "/lab/internal-audit/export-excel",
            "/lab/management-review/export-excel"
    })
    @Operation(summary = "导出实验室质量记录")
    @PreAuthorize("@ss.hasPermission('lab:clause-mapping:export') or @ss.hasPermission('lab:compliance-check:export') "
            + "or @ss.hasPermission('lab:compliance-check-item:export') or @ss.hasPermission('lab:personnel-competence:export') "
            + "or @ss.hasPermission('lab:personnel-authorization:export') or @ss.hasPermission('lab:equipment-traceability:export') "
            + "or @ss.hasPermission('lab:equipment-intermediate-check:export') or @ss.hasPermission('lab:environment-record:export') "
            + "or @ss.hasPermission('lab:method-validation:export') or @ss.hasPermission('lab:nonconformity:export') "
            + "or @ss.hasPermission('lab:corrective-action:export') or @ss.hasPermission('lab:internal-audit:export') "
            + "or @ss.hasPermission('lab:management-review:export')")
    public CommonResult<String> exportQualityRecordExcel(HttpServletRequest request) {
        return success("QUALITY-EXPORT-" + request.getRequestURI().replace("/admin-api/lab/", "").replace("/export-excel", ""));
    }


    @GetMapping("/lab/compliance-check/page")
    @Operation(summary = "获得符合性检查分页")
    @PreAuthorize("@ss.hasPermission('lab:compliance-check:query')")
    public CommonResult<PageResult<LabQualityRecordRespVO>> getComplianceCheckPage(@Valid LabQualityRecordPageReqVO pageReqVO) {
        return success(qualityRecordService.getComplianceCheckPage(pageReqVO));
    }

    @GetMapping("/lab/compliance-check/get")
    @Operation(summary = "获得符合性检查")
    @PreAuthorize("@ss.hasPermission('lab:compliance-check:query')")
    public CommonResult<LabQualityRecordRespVO> getComplianceCheck(@RequestParam("id") Long id) {
        return success(qualityRecordService.getComplianceCheck(id));
    }

    @PostMapping("/lab/compliance-check/create")
    @Operation(summary = "创建符合性检查")
    @PreAuthorize("@ss.hasPermission('lab:compliance-check:create')")
    public CommonResult<Long> createComplianceCheck(@Valid @RequestBody LabQualityRecordSaveReqVO createReqVO) {
        return success(qualityRecordService.createComplianceCheck(createReqVO));
    }

    @PutMapping("/lab/compliance-check/update")
    @Operation(summary = "更新符合性检查")
    @PreAuthorize("@ss.hasPermission('lab:compliance-check:update')")
    public CommonResult<Boolean> updateComplianceCheck(@Valid @RequestBody LabQualityRecordSaveReqVO updateReqVO) {
        qualityRecordService.updateComplianceCheck(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/lab/compliance-check/delete")
    @Operation(summary = "删除符合性检查")
    @PreAuthorize("@ss.hasPermission('lab:compliance-check:delete')")
    public CommonResult<Boolean> deleteComplianceCheck(@RequestParam("id") Long id) {
        qualityRecordService.deleteComplianceCheck(id);
        return success(true);
    }


    @GetMapping("/lab/compliance-check-item/page")
    @Operation(summary = "获得符合性检查明细分页")
    @PreAuthorize("@ss.hasPermission('lab:compliance-check-item:query')")
    public CommonResult<PageResult<LabQualityRecordRespVO>> getComplianceCheckItemPage(@Valid LabQualityRecordPageReqVO pageReqVO) {
        return success(qualityRecordService.getComplianceCheckItemPage(pageReqVO));
    }

    @GetMapping("/lab/compliance-check-item/get")
    @Operation(summary = "获得符合性检查明细")
    @PreAuthorize("@ss.hasPermission('lab:compliance-check-item:query')")
    public CommonResult<LabQualityRecordRespVO> getComplianceCheckItem(@RequestParam("id") Long id) {
        return success(qualityRecordService.getComplianceCheckItem(id));
    }

    @PostMapping("/lab/compliance-check-item/create")
    @Operation(summary = "创建符合性检查明细")
    @PreAuthorize("@ss.hasPermission('lab:compliance-check-item:create')")
    public CommonResult<Long> createComplianceCheckItem(@Valid @RequestBody LabQualityRecordSaveReqVO createReqVO) {
        return success(qualityRecordService.createComplianceCheckItem(createReqVO));
    }

    @PutMapping("/lab/compliance-check-item/update")
    @Operation(summary = "更新符合性检查明细")
    @PreAuthorize("@ss.hasPermission('lab:compliance-check-item:update')")
    public CommonResult<Boolean> updateComplianceCheckItem(@Valid @RequestBody LabQualityRecordSaveReqVO updateReqVO) {
        qualityRecordService.updateComplianceCheckItem(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/lab/compliance-check-item/delete")
    @Operation(summary = "删除符合性检查明细")
    @PreAuthorize("@ss.hasPermission('lab:compliance-check-item:delete')")
    public CommonResult<Boolean> deleteComplianceCheckItem(@RequestParam("id") Long id) {
        qualityRecordService.deleteComplianceCheckItem(id);
        return success(true);
    }


    @GetMapping("/lab/personnel-competence/page")
    @Operation(summary = "获得人员能力分页")
    @PreAuthorize("@ss.hasPermission('lab:personnel-competence:query')")
    public CommonResult<PageResult<LabQualityRecordRespVO>> getPersonnelCompetencePage(@Valid LabQualityRecordPageReqVO pageReqVO) {
        return success(qualityRecordService.getPersonnelCompetencePage(pageReqVO));
    }

    @GetMapping("/lab/personnel-competence/get")
    @Operation(summary = "获得人员能力")
    @PreAuthorize("@ss.hasPermission('lab:personnel-competence:query')")
    public CommonResult<LabQualityRecordRespVO> getPersonnelCompetence(@RequestParam("id") Long id) {
        return success(qualityRecordService.getPersonnelCompetence(id));
    }

    @PostMapping("/lab/personnel-competence/create")
    @Operation(summary = "创建人员能力")
    @PreAuthorize("@ss.hasPermission('lab:personnel-competence:create')")
    public CommonResult<Long> createPersonnelCompetence(@Valid @RequestBody LabQualityRecordSaveReqVO createReqVO) {
        return success(qualityRecordService.createPersonnelCompetence(createReqVO));
    }

    @PutMapping("/lab/personnel-competence/update")
    @Operation(summary = "更新人员能力")
    @PreAuthorize("@ss.hasPermission('lab:personnel-competence:update')")
    public CommonResult<Boolean> updatePersonnelCompetence(@Valid @RequestBody LabQualityRecordSaveReqVO updateReqVO) {
        qualityRecordService.updatePersonnelCompetence(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/lab/personnel-competence/delete")
    @Operation(summary = "删除人员能力")
    @PreAuthorize("@ss.hasPermission('lab:personnel-competence:delete')")
    public CommonResult<Boolean> deletePersonnelCompetence(@RequestParam("id") Long id) {
        qualityRecordService.deletePersonnelCompetence(id);
        return success(true);
    }


    @GetMapping("/lab/personnel-authorization/page")
    @Operation(summary = "获得人员授权分页")
    @PreAuthorize("@ss.hasPermission('lab:personnel-authorization:query')")
    public CommonResult<PageResult<LabQualityRecordRespVO>> getPersonnelAuthorizationPage(@Valid LabQualityRecordPageReqVO pageReqVO) {
        return success(qualityRecordService.getPersonnelAuthorizationPage(pageReqVO));
    }

    @GetMapping("/lab/personnel-authorization/get")
    @Operation(summary = "获得人员授权")
    @PreAuthorize("@ss.hasPermission('lab:personnel-authorization:query')")
    public CommonResult<LabQualityRecordRespVO> getPersonnelAuthorization(@RequestParam("id") Long id) {
        return success(qualityRecordService.getPersonnelAuthorization(id));
    }

    @GetMapping("/lab/personnel-authorization/available")
    @Operation(summary = "获得可执行检测任务的授权人员")
    @PreAuthorize("@ss.hasPermission('lab:personnel-authorization:query')")
    public CommonResult<List<LabPersonnelAuthorizationSummaryDTO>> getAvailablePersonnel(
            @RequestParam(value = "testItem", required = false) String testItem,
            @RequestParam(value = "methodId", required = false) Long methodId,
            @RequestParam(value = "equipmentId", required = false) Long equipmentId) {
        return success(qualityRecordService.getAvailablePersonnel(testItem, methodId, equipmentId));
    }

    @GetMapping("/lab/personnel-authorization/current")
    @Operation(summary = "获得人员当前有效授权证据")
    @PreAuthorize("@ss.hasPermission('lab:personnel-authorization:query')")
    public CommonResult<List<LabPersonnelAuthorizationSummaryDTO>> getCurrentPersonnelAuthorizationEvidence(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "testItem", required = false) String testItem,
            @RequestParam(value = "methodId", required = false) Long methodId,
            @RequestParam(value = "equipmentId", required = false) Long equipmentId) {
        return success(qualityRecordService.getCurrentPersonnelAuthorizationEvidence(userId, testItem, methodId, equipmentId));
    }

    @PostMapping("/lab/personnel-authorization/create")
    @Operation(summary = "创建人员授权")
    @PreAuthorize("@ss.hasPermission('lab:personnel-authorization:create')")
    public CommonResult<Long> createPersonnelAuthorization(@Valid @RequestBody LabQualityRecordSaveReqVO createReqVO) {
        return success(qualityRecordService.createPersonnelAuthorization(createReqVO));
    }

    @PutMapping("/lab/personnel-authorization/update")
    @Operation(summary = "更新人员授权")
    @PreAuthorize("@ss.hasPermission('lab:personnel-authorization:update')")
    public CommonResult<Boolean> updatePersonnelAuthorization(@Valid @RequestBody LabQualityRecordSaveReqVO updateReqVO) {
        qualityRecordService.updatePersonnelAuthorization(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/lab/personnel-authorization/delete")
    @Operation(summary = "删除人员授权")
    @PreAuthorize("@ss.hasPermission('lab:personnel-authorization:delete')")
    public CommonResult<Boolean> deletePersonnelAuthorization(@RequestParam("id") Long id) {
        qualityRecordService.deletePersonnelAuthorization(id);
        return success(true);
    }


    @GetMapping("/lab/equipment-traceability/page")
    @Operation(summary = "获得设备计量溯源分页")
    @PreAuthorize("@ss.hasPermission('lab:equipment-traceability:query')")
    public CommonResult<PageResult<LabQualityRecordRespVO>> getEquipmentTraceabilityPage(@Valid LabQualityRecordPageReqVO pageReqVO) {
        return success(qualityRecordService.getEquipmentTraceabilityPage(pageReqVO));
    }

    @GetMapping("/lab/equipment-traceability/get")
    @Operation(summary = "获得设备计量溯源")
    @PreAuthorize("@ss.hasPermission('lab:equipment-traceability:query')")
    public CommonResult<LabQualityRecordRespVO> getEquipmentTraceability(@RequestParam("id") Long id) {
        return success(qualityRecordService.getEquipmentTraceability(id));
    }

    @GetMapping("/lab/equipment-traceability/current")
    @Operation(summary = "获得设备当前有效校准证据")
    @PreAuthorize("@ss.hasPermission('lab:equipment-traceability:query')")
    public CommonResult<List<LabEquipmentCalibrationEvidenceDTO>> getCurrentEquipmentCalibrationEvidence(
            @RequestParam("equipmentId") Long equipmentId) {
        return success(qualityRecordService.getCurrentEquipmentCalibrationEvidence(equipmentId));
    }

    @PostMapping("/lab/equipment-traceability/create")
    @Operation(summary = "创建设备计量溯源")
    @PreAuthorize("@ss.hasPermission('lab:equipment-traceability:create')")
    public CommonResult<Long> createEquipmentTraceability(@Valid @RequestBody LabQualityRecordSaveReqVO createReqVO) {
        return success(qualityRecordService.createEquipmentTraceability(createReqVO));
    }

    @PutMapping("/lab/equipment-traceability/update")
    @Operation(summary = "更新设备计量溯源")
    @PreAuthorize("@ss.hasPermission('lab:equipment-traceability:update')")
    public CommonResult<Boolean> updateEquipmentTraceability(@Valid @RequestBody LabQualityRecordSaveReqVO updateReqVO) {
        qualityRecordService.updateEquipmentTraceability(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/lab/equipment-traceability/delete")
    @Operation(summary = "删除设备计量溯源")
    @PreAuthorize("@ss.hasPermission('lab:equipment-traceability:delete')")
    public CommonResult<Boolean> deleteEquipmentTraceability(@RequestParam("id") Long id) {
        qualityRecordService.deleteEquipmentTraceability(id);
        return success(true);
    }


    @GetMapping("/lab/equipment-intermediate-check/page")
    @Operation(summary = "获得设备期间核查分页")
    @PreAuthorize("@ss.hasPermission('lab:equipment-intermediate-check:query')")
    public CommonResult<PageResult<LabQualityRecordRespVO>> getEquipmentIntermediateCheckPage(@Valid LabQualityRecordPageReqVO pageReqVO) {
        return success(qualityRecordService.getEquipmentIntermediateCheckPage(pageReqVO));
    }

    @GetMapping("/lab/equipment-intermediate-check/get")
    @Operation(summary = "获得设备期间核查")
    @PreAuthorize("@ss.hasPermission('lab:equipment-intermediate-check:query')")
    public CommonResult<LabQualityRecordRespVO> getEquipmentIntermediateCheck(@RequestParam("id") Long id) {
        return success(qualityRecordService.getEquipmentIntermediateCheck(id));
    }

    @PostMapping("/lab/equipment-intermediate-check/create")
    @Operation(summary = "创建设备期间核查")
    @PreAuthorize("@ss.hasPermission('lab:equipment-intermediate-check:create')")
    public CommonResult<Long> createEquipmentIntermediateCheck(@Valid @RequestBody LabQualityRecordSaveReqVO createReqVO) {
        return success(qualityRecordService.createEquipmentIntermediateCheck(createReqVO));
    }

    @PutMapping("/lab/equipment-intermediate-check/update")
    @Operation(summary = "更新设备期间核查")
    @PreAuthorize("@ss.hasPermission('lab:equipment-intermediate-check:update')")
    public CommonResult<Boolean> updateEquipmentIntermediateCheck(@Valid @RequestBody LabQualityRecordSaveReqVO updateReqVO) {
        qualityRecordService.updateEquipmentIntermediateCheck(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/lab/equipment-intermediate-check/delete")
    @Operation(summary = "删除设备期间核查")
    @PreAuthorize("@ss.hasPermission('lab:equipment-intermediate-check:delete')")
    public CommonResult<Boolean> deleteEquipmentIntermediateCheck(@RequestParam("id") Long id) {
        qualityRecordService.deleteEquipmentIntermediateCheck(id);
        return success(true);
    }


    @GetMapping("/lab/environment-record/page")
    @Operation(summary = "获得环境记录分页")
    @PreAuthorize("@ss.hasPermission('lab:environment-record:query')")
    public CommonResult<PageResult<LabQualityRecordRespVO>> getEnvironmentRecordPage(@Valid LabQualityRecordPageReqVO pageReqVO) {
        return success(qualityRecordService.getEnvironmentRecordPage(pageReqVO));
    }

    @GetMapping("/lab/environment-record/get")
    @Operation(summary = "获得环境记录")
    @PreAuthorize("@ss.hasPermission('lab:environment-record:query')")
    public CommonResult<LabQualityRecordRespVO> getEnvironmentRecord(@RequestParam("id") Long id) {
        return success(qualityRecordService.getEnvironmentRecord(id));
    }

    @PostMapping("/lab/environment-record/create")
    @Operation(summary = "创建环境记录")
    @PreAuthorize("@ss.hasPermission('lab:environment-record:create')")
    public CommonResult<Long> createEnvironmentRecord(@Valid @RequestBody LabQualityRecordSaveReqVO createReqVO) {
        return success(qualityRecordService.createEnvironmentRecord(createReqVO));
    }

    @PutMapping("/lab/environment-record/update")
    @Operation(summary = "更新环境记录")
    @PreAuthorize("@ss.hasPermission('lab:environment-record:update')")
    public CommonResult<Boolean> updateEnvironmentRecord(@Valid @RequestBody LabQualityRecordSaveReqVO updateReqVO) {
        qualityRecordService.updateEnvironmentRecord(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/lab/environment-record/delete")
    @Operation(summary = "删除环境记录")
    @PreAuthorize("@ss.hasPermission('lab:environment-record:delete')")
    public CommonResult<Boolean> deleteEnvironmentRecord(@RequestParam("id") Long id) {
        qualityRecordService.deleteEnvironmentRecord(id);
        return success(true);
    }


    @GetMapping("/lab/method-validation/page")
    @Operation(summary = "获得方法验证确认分页")
    @PreAuthorize("@ss.hasPermission('lab:method-validation:query')")
    public CommonResult<PageResult<LabQualityRecordRespVO>> getMethodValidationPage(@Valid LabQualityRecordPageReqVO pageReqVO) {
        return success(qualityRecordService.getMethodValidationPage(pageReqVO));
    }

    @GetMapping("/lab/method-validation/get")
    @Operation(summary = "获得方法验证确认")
    @PreAuthorize("@ss.hasPermission('lab:method-validation:query')")
    public CommonResult<LabQualityRecordRespVO> getMethodValidation(@RequestParam("id") Long id) {
        return success(qualityRecordService.getMethodValidation(id));
    }

    @PostMapping("/lab/method-validation/create")
    @Operation(summary = "创建方法验证确认")
    @PreAuthorize("@ss.hasPermission('lab:method-validation:create')")
    public CommonResult<Long> createMethodValidation(@Valid @RequestBody LabQualityRecordSaveReqVO createReqVO) {
        return success(qualityRecordService.createMethodValidation(createReqVO));
    }

    @PutMapping("/lab/method-validation/update")
    @Operation(summary = "更新方法验证确认")
    @PreAuthorize("@ss.hasPermission('lab:method-validation:update')")
    public CommonResult<Boolean> updateMethodValidation(@Valid @RequestBody LabQualityRecordSaveReqVO updateReqVO) {
        qualityRecordService.updateMethodValidation(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/lab/method-validation/delete")
    @Operation(summary = "删除方法验证确认")
    @PreAuthorize("@ss.hasPermission('lab:method-validation:delete')")
    public CommonResult<Boolean> deleteMethodValidation(@RequestParam("id") Long id) {
        qualityRecordService.deleteMethodValidation(id);
        return success(true);
    }


    @GetMapping("/lab/nonconformity/page")
    @Operation(summary = "获得不符合项分页")
    @PreAuthorize("@ss.hasPermission('lab:nonconformity:query')")
    public CommonResult<PageResult<LabQualityRecordRespVO>> getNonconformityPage(@Valid LabQualityRecordPageReqVO pageReqVO) {
        return success(qualityRecordService.getNonconformityPage(pageReqVO));
    }

    @GetMapping("/lab/nonconformity/get")
    @Operation(summary = "获得不符合项")
    @PreAuthorize("@ss.hasPermission('lab:nonconformity:query')")
    public CommonResult<LabQualityRecordRespVO> getNonconformity(@RequestParam("id") Long id) {
        return success(qualityRecordService.getNonconformity(id));
    }

    @PostMapping("/lab/nonconformity/create")
    @Operation(summary = "创建不符合项")
    @PreAuthorize("@ss.hasPermission('lab:nonconformity:create')")
    public CommonResult<Long> createNonconformity(@Valid @RequestBody LabQualityRecordSaveReqVO createReqVO) {
        return success(qualityRecordService.createNonconformity(createReqVO));
    }

    @PutMapping("/lab/nonconformity/update")
    @Operation(summary = "更新不符合项")
    @PreAuthorize("@ss.hasPermission('lab:nonconformity:update')")
    public CommonResult<Boolean> updateNonconformity(@Valid @RequestBody LabQualityRecordSaveReqVO updateReqVO) {
        qualityRecordService.updateNonconformity(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/lab/nonconformity/delete")
    @Operation(summary = "删除不符合项")
    @PreAuthorize("@ss.hasPermission('lab:nonconformity:delete')")
    public CommonResult<Boolean> deleteNonconformity(@RequestParam("id") Long id) {
        qualityRecordService.deleteNonconformity(id);
        return success(true);
    }


    @GetMapping("/lab/corrective-action/page")
    @Operation(summary = "获得纠正措施分页")
    @PreAuthorize("@ss.hasPermission('lab:corrective-action:query')")
    public CommonResult<PageResult<LabQualityRecordRespVO>> getCorrectiveActionPage(@Valid LabQualityRecordPageReqVO pageReqVO) {
        return success(qualityRecordService.getCorrectiveActionPage(pageReqVO));
    }

    @GetMapping("/lab/corrective-action/get")
    @Operation(summary = "获得纠正措施")
    @PreAuthorize("@ss.hasPermission('lab:corrective-action:query')")
    public CommonResult<LabQualityRecordRespVO> getCorrectiveAction(@RequestParam("id") Long id) {
        return success(qualityRecordService.getCorrectiveAction(id));
    }

    @PostMapping("/lab/corrective-action/create")
    @Operation(summary = "创建纠正措施")
    @PreAuthorize("@ss.hasPermission('lab:corrective-action:create')")
    public CommonResult<Long> createCorrectiveAction(@Valid @RequestBody LabQualityRecordSaveReqVO createReqVO) {
        return success(qualityRecordService.createCorrectiveAction(createReqVO));
    }

    @PutMapping("/lab/corrective-action/update")
    @Operation(summary = "更新纠正措施")
    @PreAuthorize("@ss.hasPermission('lab:corrective-action:update')")
    public CommonResult<Boolean> updateCorrectiveAction(@Valid @RequestBody LabQualityRecordSaveReqVO updateReqVO) {
        qualityRecordService.updateCorrectiveAction(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/lab/corrective-action/delete")
    @Operation(summary = "删除纠正措施")
    @PreAuthorize("@ss.hasPermission('lab:corrective-action:delete')")
    public CommonResult<Boolean> deleteCorrectiveAction(@RequestParam("id") Long id) {
        qualityRecordService.deleteCorrectiveAction(id);
        return success(true);
    }


    @GetMapping("/lab/internal-audit/page")
    @Operation(summary = "获得内部审核分页")
    @PreAuthorize("@ss.hasPermission('lab:internal-audit:query')")
    public CommonResult<PageResult<LabQualityRecordRespVO>> getInternalAuditPage(@Valid LabQualityRecordPageReqVO pageReqVO) {
        return success(qualityRecordService.getInternalAuditPage(pageReqVO));
    }

    @GetMapping("/lab/internal-audit/get")
    @Operation(summary = "获得内部审核")
    @PreAuthorize("@ss.hasPermission('lab:internal-audit:query')")
    public CommonResult<LabQualityRecordRespVO> getInternalAudit(@RequestParam("id") Long id) {
        return success(qualityRecordService.getInternalAudit(id));
    }

    @PostMapping("/lab/internal-audit/create")
    @Operation(summary = "创建内部审核")
    @PreAuthorize("@ss.hasPermission('lab:internal-audit:create')")
    public CommonResult<Long> createInternalAudit(@Valid @RequestBody LabQualityRecordSaveReqVO createReqVO) {
        return success(qualityRecordService.createInternalAudit(createReqVO));
    }

    @PutMapping("/lab/internal-audit/update")
    @Operation(summary = "更新内部审核")
    @PreAuthorize("@ss.hasPermission('lab:internal-audit:update')")
    public CommonResult<Boolean> updateInternalAudit(@Valid @RequestBody LabQualityRecordSaveReqVO updateReqVO) {
        qualityRecordService.updateInternalAudit(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/lab/internal-audit/delete")
    @Operation(summary = "删除内部审核")
    @PreAuthorize("@ss.hasPermission('lab:internal-audit:delete')")
    public CommonResult<Boolean> deleteInternalAudit(@RequestParam("id") Long id) {
        qualityRecordService.deleteInternalAudit(id);
        return success(true);
    }


    @GetMapping("/lab/management-review/page")
    @Operation(summary = "获得管理评审分页")
    @PreAuthorize("@ss.hasPermission('lab:management-review:query')")
    public CommonResult<PageResult<LabQualityRecordRespVO>> getManagementReviewPage(@Valid LabQualityRecordPageReqVO pageReqVO) {
        return success(qualityRecordService.getManagementReviewPage(pageReqVO));
    }

    @GetMapping("/lab/management-review/get")
    @Operation(summary = "获得管理评审")
    @PreAuthorize("@ss.hasPermission('lab:management-review:query')")
    public CommonResult<LabQualityRecordRespVO> getManagementReview(@RequestParam("id") Long id) {
        return success(qualityRecordService.getManagementReview(id));
    }

    @PostMapping("/lab/management-review/create")
    @Operation(summary = "创建管理评审")
    @PreAuthorize("@ss.hasPermission('lab:management-review:create')")
    public CommonResult<Long> createManagementReview(@Valid @RequestBody LabQualityRecordSaveReqVO createReqVO) {
        return success(qualityRecordService.createManagementReview(createReqVO));
    }

    @PutMapping("/lab/management-review/update")
    @Operation(summary = "更新管理评审")
    @PreAuthorize("@ss.hasPermission('lab:management-review:update')")
    public CommonResult<Boolean> updateManagementReview(@Valid @RequestBody LabQualityRecordSaveReqVO updateReqVO) {
        qualityRecordService.updateManagementReview(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/lab/management-review/delete")
    @Operation(summary = "删除管理评审")
    @PreAuthorize("@ss.hasPermission('lab:management-review:delete')")
    public CommonResult<Boolean> deleteManagementReview(@RequestParam("id") Long id) {
        qualityRecordService.deleteManagementReview(id);
        return success(true);
    }

    @PostMapping("/lab/compliance-check/generate-items")
    @Operation(summary = "根据标准生成符合性检查明细")
    @PreAuthorize("@ss.hasPermission('lab:compliance-check:update')")
    public CommonResult<Integer> generateComplianceCheckItems(@RequestParam("id") Long id) {
        return success(qualityRecordService.generateComplianceCheckItems(id));
    }

    @PutMapping("/lab/compliance-check/start")
    @Operation(summary = "启动符合性检查")
    @PreAuthorize("@ss.hasPermission('lab:compliance-check:update')")
    public CommonResult<Boolean> startComplianceCheck(@RequestParam("id") Long id) {
        qualityRecordService.updateComplianceCheckStatus(id, "running");
        return success(true);
    }

    @PutMapping("/lab/compliance-check/complete")
    @Operation(summary = "完成符合性检查")
    @PreAuthorize("@ss.hasPermission('lab:compliance-check:update')")
    public CommonResult<Boolean> completeComplianceCheck(@RequestParam("id") Long id) {
        qualityRecordService.updateComplianceCheckStatus(id, "completed");
        return success(true);
    }

    @PutMapping("/lab/compliance-check/close")
    @Operation(summary = "关闭符合性检查")
    @PreAuthorize("@ss.hasPermission('lab:compliance-check:update')")
    public CommonResult<Boolean> closeComplianceCheck(@RequestParam("id") Long id) {
        qualityRecordService.updateComplianceCheckStatus(id, "closed");
        return success(true);
    }

    @GetMapping("/lab/compliance-check/export-package")
    @Operation(summary = "导出符合性检查材料包")
    @PreAuthorize("@ss.hasPermission('lab:compliance-check:export')")
    public CommonResult<String> exportComplianceCheckPackage(@RequestParam("id") Long id) {
        return success(qualityRecordService.exportComplianceCheckPackage(id));
    }

    @GetMapping("/lab/compliance-check-item/list")
    @Operation(summary = "获得符合性检查明细列表")
    @PreAuthorize("@ss.hasPermission('lab:compliance-check-item:query')")
    public CommonResult<PageResult<LabQualityRecordRespVO>> getComplianceCheckItemList(@Valid LabQualityRecordPageReqVO pageReqVO) {
        pageReqVO.setPageSize(100);
        return success(qualityRecordService.getComplianceCheckItemPage(pageReqVO));
    }

    @PutMapping("/lab/compliance-check-item/update-result")
    @Operation(summary = "更新符合性检查明细结果")
    @PreAuthorize("@ss.hasPermission('lab:compliance-check-item:update')")
    public CommonResult<Boolean> updateComplianceCheckItemResult(@Valid @RequestBody LabQualityRecordSaveReqVO updateReqVO) {
        qualityRecordService.updateComplianceCheckItem(updateReqVO);
        return success(true);
    }

    @PostMapping("/lab/compliance-check-item/link-evidence")
    @Operation(summary = "关联符合性检查明细证据")
    @PreAuthorize("@ss.hasPermission('lab:compliance-check-item:update')")
    public CommonResult<Long> linkComplianceCheckItemEvidence(@Valid @RequestBody LabQualityRecordSaveReqVO reqVO) {
        return success(qualityRecordService.linkComplianceCheckItemEvidence(reqVO));
    }

    @PostMapping("/lab/compliance-check-item/create-nonconformity")
    @Operation(summary = "从检查明细生成不符合项")
    @PreAuthorize("@ss.hasPermission('lab:nonconformity:create')")
    public CommonResult<Long> createNonconformityFromCheckItem(@RequestParam("id") Long id) {
        return success(qualityRecordService.createNonconformityFromCheckItem(id));
    }

    @GetMapping("/lab/personnel-competence/expiring")
    @Operation(summary = "获得即将到期人员能力")
    @PreAuthorize("@ss.hasPermission('lab:personnel-competence:query')")
    public CommonResult<PageResult<LabQualityRecordRespVO>> getExpiringPersonnelCompetence(@Valid LabQualityRecordPageReqVO pageReqVO) {
        return success(qualityRecordService.getPersonnelCompetencePage(pageReqVO));
    }

    @GetMapping("/lab/personnel-authorization/expiring")
    @Operation(summary = "获得即将到期人员授权")
    @PreAuthorize("@ss.hasPermission('lab:personnel-authorization:query')")
    public CommonResult<PageResult<LabQualityRecordRespVO>> getExpiringPersonnelAuthorization(@Valid LabQualityRecordPageReqVO pageReqVO) {
        return success(qualityRecordService.getPersonnelAuthorizationPage(pageReqVO));
    }

    @PutMapping("/lab/personnel-authorization/suspend")
    @Operation(summary = "暂停人员授权")
    @PreAuthorize("@ss.hasPermission('lab:personnel-authorization:update')")
    public CommonResult<Boolean> suspendPersonnelAuthorization(@RequestParam("id") Long id) {
        qualityRecordService.updatePersonnelAuthorizationStatus(id, "suspended");
        return success(true);
    }

    @PutMapping("/lab/personnel-authorization/revoke")
    @Operation(summary = "撤销人员授权")
    @PreAuthorize("@ss.hasPermission('lab:personnel-authorization:update')")
    public CommonResult<Boolean> revokePersonnelAuthorization(@RequestParam("id") Long id) {
        qualityRecordService.updatePersonnelAuthorizationStatus(id, "revoked");
        return success(true);
    }

    @GetMapping("/lab/equipment-traceability/expiring")
    @Operation(summary = "获得即将到期设备溯源")
    @PreAuthorize("@ss.hasPermission('lab:equipment-traceability:query')")
    public CommonResult<PageResult<LabQualityRecordRespVO>> getExpiringEquipmentTraceability(@Valid LabQualityRecordPageReqVO pageReqVO) {
        return success(qualityRecordService.getEquipmentTraceabilityPage(pageReqVO));
    }

    @PutMapping("/lab/equipment-intermediate-check/approve")
    @Operation(summary = "期间核查复核通过")
    @PreAuthorize("@ss.hasPermission('lab:equipment-intermediate-check:update')")
    public CommonResult<Boolean> approveEquipmentIntermediateCheck(@RequestParam("id") Long id) {
        qualityRecordService.updateEquipmentIntermediateCheckStatus(id, "approved");
        return success(true);
    }

    @PutMapping("/lab/equipment-intermediate-check/reject")
    @Operation(summary = "期间核查驳回")
    @PreAuthorize("@ss.hasPermission('lab:equipment-intermediate-check:update')")
    public CommonResult<Boolean> rejectEquipmentIntermediateCheck(@RequestParam("id") Long id) {
        qualityRecordService.updateEquipmentIntermediateCheckStatus(id, "rejected");
        return success(true);
    }

    @GetMapping("/lab/environment-record/abnormal-list")
    @Operation(summary = "获得异常环境记录")
    @PreAuthorize("@ss.hasPermission('lab:environment-record:query')")
    public CommonResult<PageResult<LabQualityRecordRespVO>> getAbnormalEnvironmentRecords(@Valid LabQualityRecordPageReqVO pageReqVO) {
        return success(qualityRecordService.getEnvironmentRecordPage(pageReqVO));
    }

    @PutMapping("/lab/method-validation/submit-review")
    @Operation(summary = "提交方法验证复核")
    @PreAuthorize("@ss.hasPermission('lab:method-validation:update')")
    public CommonResult<Boolean> submitMethodValidationReview(@RequestParam("id") Long id) {
        qualityRecordService.updateMethodValidationStatus(id, "reviewing");
        return success(true);
    }

    @PutMapping("/lab/method-validation/approve")
    @Operation(summary = "批准方法验证")
    @PreAuthorize("@ss.hasPermission('lab:method-validation:update')")
    public CommonResult<Boolean> approveMethodValidation(@RequestParam("id") Long id) {
        qualityRecordService.updateMethodValidationStatus(id, "approved");
        return success(true);
    }

    @PutMapping("/lab/method-validation/reject")
    @Operation(summary = "驳回方法验证")
    @PreAuthorize("@ss.hasPermission('lab:method-validation:update')")
    public CommonResult<Boolean> rejectMethodValidation(@RequestParam("id") Long id) {
        qualityRecordService.updateMethodValidationStatus(id, "rejected");
        return success(true);
    }

    @PutMapping("/lab/nonconformity/close")
    @Operation(summary = "关闭不符合项")
    @PreAuthorize("@ss.hasPermission('lab:nonconformity:update')")
    public CommonResult<Boolean> closeNonconformity(@RequestParam("id") Long id) {
        qualityRecordService.updateNonconformityStatus(id, "closed");
        return success(true);
    }

    @PutMapping("/lab/corrective-action/submit")
    @Operation(summary = "提交纠正措施")
    @PreAuthorize("@ss.hasPermission('lab:corrective-action:update')")
    public CommonResult<Boolean> submitCorrectiveAction(@RequestParam("id") Long id) {
        qualityRecordService.updateCorrectiveActionStatus(id, "running");
        return success(true);
    }

    @PutMapping("/lab/corrective-action/verify")
    @Operation(summary = "验证纠正措施")
    @PreAuthorize("@ss.hasPermission('lab:corrective-action:update')")
    public CommonResult<Boolean> verifyCorrectiveAction(@RequestParam("id") Long id) {
        qualityRecordService.updateCorrectiveActionStatus(id, "verified");
        return success(true);
    }

    @PutMapping("/lab/corrective-action/reject")
    @Operation(summary = "驳回纠正措施")
    @PreAuthorize("@ss.hasPermission('lab:corrective-action:update')")
    public CommonResult<Boolean> rejectCorrectiveAction(@RequestParam("id") Long id) {
        qualityRecordService.updateCorrectiveActionStatus(id, "rejected");
        return success(true);
    }

    @PutMapping("/lab/internal-audit/start")
    @Operation(summary = "启动内审")
    @PreAuthorize("@ss.hasPermission('lab:internal-audit:update')")
    public CommonResult<Boolean> startInternalAudit(@RequestParam("id") Long id) {
        qualityRecordService.updateInternalAuditStatus(id, "running");
        return success(true);
    }

    @PutMapping("/lab/internal-audit/complete")
    @Operation(summary = "完成内审")
    @PreAuthorize("@ss.hasPermission('lab:internal-audit:update')")
    public CommonResult<Boolean> completeInternalAudit(@RequestParam("id") Long id) {
        qualityRecordService.updateInternalAuditStatus(id, "completed");
        return success(true);
    }

    @PutMapping("/lab/internal-audit/close")
    @Operation(summary = "关闭内审")
    @PreAuthorize("@ss.hasPermission('lab:internal-audit:update')")
    public CommonResult<Boolean> closeInternalAudit(@RequestParam("id") Long id) {
        qualityRecordService.updateInternalAuditStatus(id, "closed");
        return success(true);
    }

    @PutMapping("/lab/management-review/complete")
    @Operation(summary = "完成管理评审")
    @PreAuthorize("@ss.hasPermission('lab:management-review:update')")
    public CommonResult<Boolean> completeManagementReview(@RequestParam("id") Long id) {
        qualityRecordService.updateManagementReviewStatus(id, "completed");
        return success(true);
    }

    @PutMapping("/lab/management-review/close")
    @Operation(summary = "关闭管理评审")
    @PreAuthorize("@ss.hasPermission('lab:management-review:update')")
    public CommonResult<Boolean> closeManagementReview(@RequestParam("id") Long id) {
        qualityRecordService.updateManagementReviewStatus(id, "closed");
        return success(true);
    }

}
