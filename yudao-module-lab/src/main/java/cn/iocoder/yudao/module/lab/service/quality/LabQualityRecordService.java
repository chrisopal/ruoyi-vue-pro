package cn.iocoder.yudao.module.lab.service.quality;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.lab.controller.admin.quality.vo.LabQualityRecordPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.quality.vo.LabQualityRecordRespVO;
import cn.iocoder.yudao.module.lab.controller.admin.quality.vo.LabQualityRecordSaveReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.evidencelink.LabEvidenceLinkDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.standard.LabStandardClauseDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.clausemapping.LabClauseFunctionMappingDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.compliancecheck.LabComplianceCheckDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.compliancecheck.LabComplianceCheckItemDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.personnel.LabPersonnelCompetenceDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.personnel.LabPersonnelAuthorizationDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.equipment.LabEquipmentTraceabilityDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.equipment.LabEquipmentIntermediateCheckDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.environment.LabEnvironmentRecordDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.method.LabMethodValidationDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.nonconformity.LabNonconformityDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.nonconformity.LabCorrectiveActionDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.audit.LabInternalAuditDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.audit.LabManagementReviewDO;
import cn.iocoder.yudao.module.lab.dal.mysql.evidencelink.LabEvidenceLinkMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.standard.LabStandardClauseMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.clausemapping.LabClauseFunctionMappingMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.compliancecheck.LabComplianceCheckMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.compliancecheck.LabComplianceCheckItemMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.personnel.LabPersonnelCompetenceMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.personnel.LabPersonnelAuthorizationMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.equipment.LabEquipmentTraceabilityMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.equipment.LabEquipmentIntermediateCheckMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.environment.LabEnvironmentRecordMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.method.LabMethodValidationMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.nonconformity.LabNonconformityMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.nonconformity.LabCorrectiveActionMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.audit.LabInternalAuditMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.audit.LabManagementReviewMapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Validated
public class LabQualityRecordService {

    @Resource
    private LabClauseFunctionMappingMapper clauseMappingMapper;
    @Resource
    private LabComplianceCheckMapper complianceCheckMapper;
    @Resource
    private LabComplianceCheckItemMapper complianceCheckItemMapper;
    @Resource
    private LabPersonnelCompetenceMapper personnelCompetenceMapper;
    @Resource
    private LabPersonnelAuthorizationMapper personnelAuthorizationMapper;
    @Resource
    private LabEquipmentTraceabilityMapper equipmentTraceabilityMapper;
    @Resource
    private LabEquipmentIntermediateCheckMapper equipmentIntermediateCheckMapper;
    @Resource
    private LabEnvironmentRecordMapper environmentRecordMapper;
    @Resource
    private LabMethodValidationMapper methodValidationMapper;
    @Resource
    private LabNonconformityMapper nonconformityMapper;
    @Resource
    private LabCorrectiveActionMapper correctiveActionMapper;
    @Resource
    private LabInternalAuditMapper internalAuditMapper;
    @Resource
    private LabManagementReviewMapper managementReviewMapper;
    @Resource
    private LabStandardClauseMapper standardClauseMapper;
    @Resource
    private LabEvidenceLinkMapper evidenceLinkMapper;

    public Long createClauseMapping(LabQualityRecordSaveReqVO createReqVO) {
        LabClauseFunctionMappingDO record = BeanUtils.toBean(createReqVO, LabClauseFunctionMappingDO.class);
        clauseMappingMapper.insert(record);
        return record.getId();
    }

    public void updateClauseMapping(LabQualityRecordSaveReqVO updateReqVO) {
        clauseMappingMapper.updateById(BeanUtils.toBean(updateReqVO, LabClauseFunctionMappingDO.class));
    }

    public void deleteClauseMapping(Long id) {
        clauseMappingMapper.deleteById(id);
    }

    public LabQualityRecordRespVO getClauseMapping(Long id) {
        return BeanUtils.toBean(clauseMappingMapper.selectById(id), LabQualityRecordRespVO.class);
    }

    public PageResult<LabQualityRecordRespVO> getClauseMappingPage(LabQualityRecordPageReqVO pageReqVO) {
        return BeanUtils.toBean(clauseMappingMapper.selectPage(pageReqVO), LabQualityRecordRespVO.class);
    }

    public void updateClauseMappingStatus(Long id, String status) {
        clauseMappingMapper.update(null, new UpdateWrapper<LabClauseFunctionMappingDO>().eq("id", id).set("status", status));
    }


    public Long createComplianceCheck(LabQualityRecordSaveReqVO createReqVO) {
        LabComplianceCheckDO record = BeanUtils.toBean(createReqVO, LabComplianceCheckDO.class);
        complianceCheckMapper.insert(record);
        return record.getId();
    }

    public void updateComplianceCheck(LabQualityRecordSaveReqVO updateReqVO) {
        complianceCheckMapper.updateById(BeanUtils.toBean(updateReqVO, LabComplianceCheckDO.class));
    }

    public void deleteComplianceCheck(Long id) {
        complianceCheckMapper.deleteById(id);
    }

    public LabQualityRecordRespVO getComplianceCheck(Long id) {
        return BeanUtils.toBean(complianceCheckMapper.selectById(id), LabQualityRecordRespVO.class);
    }

    public PageResult<LabQualityRecordRespVO> getComplianceCheckPage(LabQualityRecordPageReqVO pageReqVO) {
        return BeanUtils.toBean(complianceCheckMapper.selectPage(pageReqVO), LabQualityRecordRespVO.class);
    }

    public void updateComplianceCheckStatus(Long id, String status) {
        complianceCheckMapper.update(null, new UpdateWrapper<LabComplianceCheckDO>().eq("id", id).set("status", status));
    }


    public Long createComplianceCheckItem(LabQualityRecordSaveReqVO createReqVO) {
        LabComplianceCheckItemDO record = BeanUtils.toBean(createReqVO, LabComplianceCheckItemDO.class);
        complianceCheckItemMapper.insert(record);
        return record.getId();
    }

    public void updateComplianceCheckItem(LabQualityRecordSaveReqVO updateReqVO) {
        complianceCheckItemMapper.updateById(BeanUtils.toBean(updateReqVO, LabComplianceCheckItemDO.class));
    }

    public void deleteComplianceCheckItem(Long id) {
        complianceCheckItemMapper.deleteById(id);
    }

    public LabQualityRecordRespVO getComplianceCheckItem(Long id) {
        return BeanUtils.toBean(complianceCheckItemMapper.selectById(id), LabQualityRecordRespVO.class);
    }

    public PageResult<LabQualityRecordRespVO> getComplianceCheckItemPage(LabQualityRecordPageReqVO pageReqVO) {
        return BeanUtils.toBean(complianceCheckItemMapper.selectPage(pageReqVO), LabQualityRecordRespVO.class);
    }

    public void updateComplianceCheckItemStatus(Long id, String status) {
        complianceCheckItemMapper.update(null, new UpdateWrapper<LabComplianceCheckItemDO>().eq("id", id).set("status", status));
    }


    public Long createPersonnelCompetence(LabQualityRecordSaveReqVO createReqVO) {
        LabPersonnelCompetenceDO record = BeanUtils.toBean(createReqVO, LabPersonnelCompetenceDO.class);
        personnelCompetenceMapper.insert(record);
        return record.getId();
    }

    public void updatePersonnelCompetence(LabQualityRecordSaveReqVO updateReqVO) {
        personnelCompetenceMapper.updateById(BeanUtils.toBean(updateReqVO, LabPersonnelCompetenceDO.class));
    }

    public void deletePersonnelCompetence(Long id) {
        personnelCompetenceMapper.deleteById(id);
    }

    public LabQualityRecordRespVO getPersonnelCompetence(Long id) {
        return BeanUtils.toBean(personnelCompetenceMapper.selectById(id), LabQualityRecordRespVO.class);
    }

    public PageResult<LabQualityRecordRespVO> getPersonnelCompetencePage(LabQualityRecordPageReqVO pageReqVO) {
        return BeanUtils.toBean(personnelCompetenceMapper.selectPage(pageReqVO), LabQualityRecordRespVO.class);
    }

    public void updatePersonnelCompetenceStatus(Long id, String status) {
        personnelCompetenceMapper.update(null, new UpdateWrapper<LabPersonnelCompetenceDO>().eq("id", id).set("status", status));
    }


    public Long createPersonnelAuthorization(LabQualityRecordSaveReqVO createReqVO) {
        LabPersonnelAuthorizationDO record = BeanUtils.toBean(createReqVO, LabPersonnelAuthorizationDO.class);
        personnelAuthorizationMapper.insert(record);
        return record.getId();
    }

    public void updatePersonnelAuthorization(LabQualityRecordSaveReqVO updateReqVO) {
        personnelAuthorizationMapper.updateById(BeanUtils.toBean(updateReqVO, LabPersonnelAuthorizationDO.class));
    }

    public void deletePersonnelAuthorization(Long id) {
        personnelAuthorizationMapper.deleteById(id);
    }

    public LabQualityRecordRespVO getPersonnelAuthorization(Long id) {
        return BeanUtils.toBean(personnelAuthorizationMapper.selectById(id), LabQualityRecordRespVO.class);
    }

    public PageResult<LabQualityRecordRespVO> getPersonnelAuthorizationPage(LabQualityRecordPageReqVO pageReqVO) {
        return BeanUtils.toBean(personnelAuthorizationMapper.selectPage(pageReqVO), LabQualityRecordRespVO.class);
    }

    public void updatePersonnelAuthorizationStatus(Long id, String status) {
        personnelAuthorizationMapper.update(null, new UpdateWrapper<LabPersonnelAuthorizationDO>().eq("id", id).set("status", status));
    }


    public Long createEquipmentTraceability(LabQualityRecordSaveReqVO createReqVO) {
        LabEquipmentTraceabilityDO record = BeanUtils.toBean(createReqVO, LabEquipmentTraceabilityDO.class);
        equipmentTraceabilityMapper.insert(record);
        return record.getId();
    }

    public void updateEquipmentTraceability(LabQualityRecordSaveReqVO updateReqVO) {
        equipmentTraceabilityMapper.updateById(BeanUtils.toBean(updateReqVO, LabEquipmentTraceabilityDO.class));
    }

    public void deleteEquipmentTraceability(Long id) {
        equipmentTraceabilityMapper.deleteById(id);
    }

    public LabQualityRecordRespVO getEquipmentTraceability(Long id) {
        return BeanUtils.toBean(equipmentTraceabilityMapper.selectById(id), LabQualityRecordRespVO.class);
    }

    public PageResult<LabQualityRecordRespVO> getEquipmentTraceabilityPage(LabQualityRecordPageReqVO pageReqVO) {
        return BeanUtils.toBean(equipmentTraceabilityMapper.selectPage(pageReqVO), LabQualityRecordRespVO.class);
    }

    public void updateEquipmentTraceabilityStatus(Long id, String status) {
        equipmentTraceabilityMapper.update(null, new UpdateWrapper<LabEquipmentTraceabilityDO>().eq("id", id).set("status", status));
    }


    public Long createEquipmentIntermediateCheck(LabQualityRecordSaveReqVO createReqVO) {
        LabEquipmentIntermediateCheckDO record = BeanUtils.toBean(createReqVO, LabEquipmentIntermediateCheckDO.class);
        equipmentIntermediateCheckMapper.insert(record);
        return record.getId();
    }

    public void updateEquipmentIntermediateCheck(LabQualityRecordSaveReqVO updateReqVO) {
        equipmentIntermediateCheckMapper.updateById(BeanUtils.toBean(updateReqVO, LabEquipmentIntermediateCheckDO.class));
    }

    public void deleteEquipmentIntermediateCheck(Long id) {
        equipmentIntermediateCheckMapper.deleteById(id);
    }

    public LabQualityRecordRespVO getEquipmentIntermediateCheck(Long id) {
        return BeanUtils.toBean(equipmentIntermediateCheckMapper.selectById(id), LabQualityRecordRespVO.class);
    }

    public PageResult<LabQualityRecordRespVO> getEquipmentIntermediateCheckPage(LabQualityRecordPageReqVO pageReqVO) {
        return BeanUtils.toBean(equipmentIntermediateCheckMapper.selectPage(pageReqVO), LabQualityRecordRespVO.class);
    }

    public void updateEquipmentIntermediateCheckStatus(Long id, String status) {
        equipmentIntermediateCheckMapper.update(null, new UpdateWrapper<LabEquipmentIntermediateCheckDO>().eq("id", id).set("status", status));
    }


    public Long createEnvironmentRecord(LabQualityRecordSaveReqVO createReqVO) {
        LabEnvironmentRecordDO record = BeanUtils.toBean(createReqVO, LabEnvironmentRecordDO.class);
        environmentRecordMapper.insert(record);
        return record.getId();
    }

    public void updateEnvironmentRecord(LabQualityRecordSaveReqVO updateReqVO) {
        environmentRecordMapper.updateById(BeanUtils.toBean(updateReqVO, LabEnvironmentRecordDO.class));
    }

    public void deleteEnvironmentRecord(Long id) {
        environmentRecordMapper.deleteById(id);
    }

    public LabQualityRecordRespVO getEnvironmentRecord(Long id) {
        return BeanUtils.toBean(environmentRecordMapper.selectById(id), LabQualityRecordRespVO.class);
    }

    public PageResult<LabQualityRecordRespVO> getEnvironmentRecordPage(LabQualityRecordPageReqVO pageReqVO) {
        return BeanUtils.toBean(environmentRecordMapper.selectPage(pageReqVO), LabQualityRecordRespVO.class);
    }

    public void updateEnvironmentRecordStatus(Long id, String status) {
        environmentRecordMapper.update(null, new UpdateWrapper<LabEnvironmentRecordDO>().eq("id", id).set("status", status));
    }


    public Long createMethodValidation(LabQualityRecordSaveReqVO createReqVO) {
        LabMethodValidationDO record = BeanUtils.toBean(createReqVO, LabMethodValidationDO.class);
        methodValidationMapper.insert(record);
        return record.getId();
    }

    public void updateMethodValidation(LabQualityRecordSaveReqVO updateReqVO) {
        methodValidationMapper.updateById(BeanUtils.toBean(updateReqVO, LabMethodValidationDO.class));
    }

    public void deleteMethodValidation(Long id) {
        methodValidationMapper.deleteById(id);
    }

    public LabQualityRecordRespVO getMethodValidation(Long id) {
        return BeanUtils.toBean(methodValidationMapper.selectById(id), LabQualityRecordRespVO.class);
    }

    public PageResult<LabQualityRecordRespVO> getMethodValidationPage(LabQualityRecordPageReqVO pageReqVO) {
        return BeanUtils.toBean(methodValidationMapper.selectPage(pageReqVO), LabQualityRecordRespVO.class);
    }

    public void updateMethodValidationStatus(Long id, String status) {
        methodValidationMapper.update(null, new UpdateWrapper<LabMethodValidationDO>().eq("id", id).set("status", status));
    }


    public Long createNonconformity(LabQualityRecordSaveReqVO createReqVO) {
        LabNonconformityDO record = BeanUtils.toBean(createReqVO, LabNonconformityDO.class);
        nonconformityMapper.insert(record);
        return record.getId();
    }

    public void updateNonconformity(LabQualityRecordSaveReqVO updateReqVO) {
        nonconformityMapper.updateById(BeanUtils.toBean(updateReqVO, LabNonconformityDO.class));
    }

    public void deleteNonconformity(Long id) {
        nonconformityMapper.deleteById(id);
    }

    public LabQualityRecordRespVO getNonconformity(Long id) {
        return BeanUtils.toBean(nonconformityMapper.selectById(id), LabQualityRecordRespVO.class);
    }

    public PageResult<LabQualityRecordRespVO> getNonconformityPage(LabQualityRecordPageReqVO pageReqVO) {
        return BeanUtils.toBean(nonconformityMapper.selectPage(pageReqVO), LabQualityRecordRespVO.class);
    }

    public void updateNonconformityStatus(Long id, String status) {
        nonconformityMapper.update(null, new UpdateWrapper<LabNonconformityDO>().eq("id", id).set("status", status));
    }


    public Long createCorrectiveAction(LabQualityRecordSaveReqVO createReqVO) {
        LabCorrectiveActionDO record = BeanUtils.toBean(createReqVO, LabCorrectiveActionDO.class);
        correctiveActionMapper.insert(record);
        return record.getId();
    }

    public void updateCorrectiveAction(LabQualityRecordSaveReqVO updateReqVO) {
        correctiveActionMapper.updateById(BeanUtils.toBean(updateReqVO, LabCorrectiveActionDO.class));
    }

    public void deleteCorrectiveAction(Long id) {
        correctiveActionMapper.deleteById(id);
    }

    public LabQualityRecordRespVO getCorrectiveAction(Long id) {
        return BeanUtils.toBean(correctiveActionMapper.selectById(id), LabQualityRecordRespVO.class);
    }

    public PageResult<LabQualityRecordRespVO> getCorrectiveActionPage(LabQualityRecordPageReqVO pageReqVO) {
        return BeanUtils.toBean(correctiveActionMapper.selectPage(pageReqVO), LabQualityRecordRespVO.class);
    }

    public void updateCorrectiveActionStatus(Long id, String status) {
        correctiveActionMapper.update(null, new UpdateWrapper<LabCorrectiveActionDO>().eq("id", id).set("status", status));
    }


    public Long createInternalAudit(LabQualityRecordSaveReqVO createReqVO) {
        LabInternalAuditDO record = BeanUtils.toBean(createReqVO, LabInternalAuditDO.class);
        internalAuditMapper.insert(record);
        return record.getId();
    }

    public void updateInternalAudit(LabQualityRecordSaveReqVO updateReqVO) {
        internalAuditMapper.updateById(BeanUtils.toBean(updateReqVO, LabInternalAuditDO.class));
    }

    public void deleteInternalAudit(Long id) {
        internalAuditMapper.deleteById(id);
    }

    public LabQualityRecordRespVO getInternalAudit(Long id) {
        return BeanUtils.toBean(internalAuditMapper.selectById(id), LabQualityRecordRespVO.class);
    }

    public PageResult<LabQualityRecordRespVO> getInternalAuditPage(LabQualityRecordPageReqVO pageReqVO) {
        return BeanUtils.toBean(internalAuditMapper.selectPage(pageReqVO), LabQualityRecordRespVO.class);
    }

    public void updateInternalAuditStatus(Long id, String status) {
        internalAuditMapper.update(null, new UpdateWrapper<LabInternalAuditDO>().eq("id", id).set("status", status));
    }


    public Long createManagementReview(LabQualityRecordSaveReqVO createReqVO) {
        LabManagementReviewDO record = BeanUtils.toBean(createReqVO, LabManagementReviewDO.class);
        managementReviewMapper.insert(record);
        return record.getId();
    }

    public void updateManagementReview(LabQualityRecordSaveReqVO updateReqVO) {
        managementReviewMapper.updateById(BeanUtils.toBean(updateReqVO, LabManagementReviewDO.class));
    }

    public void deleteManagementReview(Long id) {
        managementReviewMapper.deleteById(id);
    }

    public LabQualityRecordRespVO getManagementReview(Long id) {
        return BeanUtils.toBean(managementReviewMapper.selectById(id), LabQualityRecordRespVO.class);
    }

    public PageResult<LabQualityRecordRespVO> getManagementReviewPage(LabQualityRecordPageReqVO pageReqVO) {
        return BeanUtils.toBean(managementReviewMapper.selectPage(pageReqVO), LabQualityRecordRespVO.class);
    }

    public void updateManagementReviewStatus(Long id, String status) {
        managementReviewMapper.update(null, new UpdateWrapper<LabManagementReviewDO>().eq("id", id).set("status", status));
    }

    public Integer generateComplianceCheckItems(Long checkId) {
        LabComplianceCheckDO check = complianceCheckMapper.selectById(checkId);
        if (check == null || check.getStandardId() == null) {
            return 0;
        }
        List<LabStandardClauseDO> clauses = standardClauseMapper.selectList(LabStandardClauseDO::getStandardId, check.getStandardId());
        int count = 0;
        for (LabStandardClauseDO clause : clauses) {
            LabComplianceCheckItemDO item = new LabComplianceCheckItemDO();
            item.setCheckId(checkId);
            item.setClauseId(clause.getId());
            item.setCheckResult("not_checked");
            item.setEvidenceStatus("missing");
            item.setFindingDescription(clause.getRequirementText());
            item.setSeverity("observation");
            complianceCheckItemMapper.insert(item);
            count++;
        }
        return count;
    }

    public Long linkComplianceCheckItemEvidence(LabQualityRecordSaveReqVO reqVO) {
        LabComplianceCheckItemDO item = complianceCheckItemMapper.selectById(reqVO.getId());
        LabEvidenceLinkDO evidenceLink = new LabEvidenceLinkDO();
        evidenceLink.setEvidenceCode(reqVO.getEvidenceType() == null ? "FILE" : reqVO.getEvidenceType());
        evidenceLink.setSourceObject("lab_compliance_check_item");
        evidenceLink.setSourceObjectId(reqVO.getId());
        evidenceLink.setSourceObjectNo(item == null ? null : String.valueOf(item.getClauseId()));
        evidenceLink.setLinkedBizType("compliance_check");
        evidenceLink.setLinkedBizId(item == null ? null : item.getCheckId());
        evidenceLink.setLinkedBizNo(item == null ? null : String.valueOf(item.getCheckId()));
        evidenceLink.setClauseCategory("compliance");
        evidenceLink.setLinkStatus("linked");
        evidenceLink.setRemark(reqVO.getRemark());
        evidenceLinkMapper.insert(evidenceLink);
        if (item != null) {
            item.setEvidenceStatus("sufficient");
            item.setEvidenceSummary(reqVO.getEvidenceDescription());
            complianceCheckItemMapper.updateById(item);
        }
        return evidenceLink.getId();
    }

    public Long createNonconformityFromCheckItem(Long itemId) {
        LabComplianceCheckItemDO item = complianceCheckItemMapper.selectById(itemId);
        LabNonconformityDO nc = new LabNonconformityDO();
        nc.setNcNo("NC" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        nc.setSourceType("compliance_check");
        nc.setSourceId(item == null ? null : item.getCheckId());
        nc.setClauseId(item == null ? null : item.getClauseId());
        nc.setTitle("符合性检查不符合项");
        nc.setDescription(item == null || item.getFindingDescription() == null ? "检查明细生成" : item.getFindingDescription());
        nc.setSeverity(item == null || item.getSeverity() == null ? "minor" : item.getSeverity());
        nc.setStatus("open");
        nonconformityMapper.insert(nc);
        if (item != null) {
            item.setCheckResult("nonconformity");
            item.setNonconformityId(nc.getId());
            complianceCheckItemMapper.updateById(item);
        }
        return nc.getId();
    }

    public String exportComplianceCheckPackage(Long checkId) {
        return "CHECK-PACKAGE-" + checkId;
    }

}
