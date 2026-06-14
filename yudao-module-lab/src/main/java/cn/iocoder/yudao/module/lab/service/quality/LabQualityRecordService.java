package cn.iocoder.yudao.module.lab.service.quality;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.lab.controller.admin.evidencelink.vo.LabEvidenceLinkSaveReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.quality.vo.LabQualityRecordPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.quality.vo.LabQualityRecordRespVO;
import cn.iocoder.yudao.module.lab.controller.admin.quality.vo.LabQualityRecordSaveReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.evidenceobject.LabEvidenceObjectDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.evidencelink.LabEvidenceLinkDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.standard.LabStandardClauseDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.clausemapping.LabClauseFunctionMappingDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.compliancecheck.LabComplianceCheckDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.compliancecheck.LabComplianceCheckItemDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.personnel.LabPersonnelCompetenceDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.personnel.LabPersonnelAuthorizationDO;
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
import cn.iocoder.yudao.module.lab.dal.mysql.equipment.LabEquipmentIntermediateCheckMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.environment.LabEnvironmentRecordMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.method.LabMethodValidationMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.nonconformity.LabNonconformityMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.nonconformity.LabCorrectiveActionMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.audit.LabInternalAuditMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.audit.LabManagementReviewMapper;
import cn.iocoder.yudao.module.lab.service.equipment.LabEquipmentTraceabilityService;
import cn.iocoder.yudao.module.lab.service.equipment.dto.LabEquipmentCalibrationEvidenceDTO;
import cn.iocoder.yudao.module.lab.service.evidencelink.LabEvidenceLinkService;
import cn.iocoder.yudao.module.lab.service.evidenceobject.LabEvidenceObjectService;
import cn.iocoder.yudao.module.lab.service.quality.dto.LabPersonnelAuthorizationSummaryDTO;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Validated
public class LabQualityRecordService {

    private static final String STATUS_ACTIVE = "active";
    private static final String STATUS_VALID = "valid";
    private static final String STATUS_EFFECTIVE = "effective";
    private static final String STATUS_APPROVED = "approved";
    private static final String STATUS_DRAFT = "draft";
    private static final String EVIDENCE_TYPE_PERSON_AUTH = "PERSON_AUTH";
    private static final String EVIDENCE_SOURCE_PERSONNEL_AUTHORIZATION = "lab_personnel_authorization";
    private static final String BUSINESS_DOMAIN_PERSONNEL = "personnel";
    private static final String LINKED_BIZ_TYPE_PERSONNEL_USER = "personnel_user";

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
    private LabEquipmentTraceabilityService equipmentTraceabilityService;
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
    @Resource
    private LabEvidenceObjectService evidenceObjectService;
    @Resource
    private LabEvidenceLinkService evidenceLinkService;

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
        normalizePersonnelAuthorization(record);
        personnelAuthorizationMapper.insert(record);
        createPersonnelAuthorizationEvidenceChain(record);
        return record.getId();
    }

    public void updatePersonnelAuthorization(LabQualityRecordSaveReqVO updateReqVO) {
        LabPersonnelAuthorizationDO record = BeanUtils.toBean(updateReqVO, LabPersonnelAuthorizationDO.class);
        normalizePersonnelAuthorization(record);
        personnelAuthorizationMapper.updateById(record);
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

    public List<LabPersonnelAuthorizationSummaryDTO> getAvailablePersonnel(String testItem, Long methodId, Long equipmentId) {
        List<LabPersonnelAuthorizationDO> authorizations = personnelAuthorizationMapper.selectListForAvailability();
        return buildPersonnelAuthorizationSummaries(authorizations, testItem, methodId, equipmentId).stream()
                .collect(Collectors.toMap(LabPersonnelAuthorizationSummaryDTO::getUserId,
                        Function.identity(), this::preferLaterValidTo))
                .values().stream()
                .sorted(Comparator.comparing(LabPersonnelAuthorizationSummaryDTO::getUserName,
                                Comparator.nullsLast(String::compareToIgnoreCase))
                        .thenComparing(LabPersonnelAuthorizationSummaryDTO::getUserId,
                                Comparator.nullsLast(Long::compareTo)))
                .toList();
    }

    public List<LabPersonnelAuthorizationSummaryDTO> getCurrentPersonnelAuthorizationEvidence(Long userId, String testItem,
                                                                                              Long methodId, Long equipmentId) {
        if (userId == null) {
            return List.of();
        }
        return buildPersonnelAuthorizationSummaries(personnelAuthorizationMapper.selectListByUserId(userId),
                testItem, methodId, equipmentId);
    }

    private List<LabPersonnelAuthorizationSummaryDTO> buildPersonnelAuthorizationSummaries(
            List<LabPersonnelAuthorizationDO> authorizations, String testItem, Long methodId, Long equipmentId) {
        List<LabPersonnelAuthorizationDO> effectiveAuthorizations = authorizations.stream()
                .filter(this::isUsableAuthorization)
                .filter(authorization -> matchesMethod(authorization, methodId))
                .filter(authorization -> matchesEquipment(authorization, equipmentId))
                .filter(authorization -> matchesText(authorization.getAuthScope(), testItem))
                .toList();
        List<Long> userIds = effectiveAuthorizations.stream()
                .map(LabPersonnelAuthorizationDO::getUserId)
                .filter(userId -> userId != null)
                .distinct()
                .toList();
        if (userIds.isEmpty()) {
            return List.of();
        }
        Map<Long, List<LabPersonnelCompetenceDO>> competenceByUser = personnelCompetenceMapper.selectListByUserIds(userIds).stream()
                .filter(this::isUsableCompetence)
                .filter(competence -> matchesText(competence.getCompetenceItem(), testItem))
                .collect(Collectors.groupingBy(LabPersonnelCompetenceDO::getUserId));
        return effectiveAuthorizations.stream()
                .map(authorization -> toPersonnelSummary(authorization, selectCompetence(competenceByUser.get(authorization.getUserId()))))
                .filter(summary -> summary.getCompetenceId() != null)
                .toList();
    }

    private LabPersonnelCompetenceDO selectCompetence(List<LabPersonnelCompetenceDO> competences) {
        if (competences == null || competences.isEmpty()) {
            return null;
        }
        return competences.stream()
                .max(Comparator.comparing(LabPersonnelCompetenceDO::getValidTo, Comparator.nullsFirst(String::compareTo))
                        .thenComparing(LabPersonnelCompetenceDO::getId, Comparator.nullsFirst(Long::compareTo)))
                .orElse(null);
    }

    private LabPersonnelAuthorizationSummaryDTO toPersonnelSummary(LabPersonnelAuthorizationDO authorization,
                                                                   LabPersonnelCompetenceDO competence) {
        LabPersonnelAuthorizationSummaryDTO dto = new LabPersonnelAuthorizationSummaryDTO();
        dto.setAuthorizationId(authorization.getId());
        dto.setUserId(authorization.getUserId());
        dto.setAuthType(authorization.getAuthType());
        dto.setAuthScope(authorization.getAuthScope());
        dto.setMethodId(authorization.getMethodId());
        dto.setEquipmentId(authorization.getEquipmentId());
        dto.setAuthorizedTime(authorization.getAuthorizedTime());
        dto.setValidFrom(authorization.getValidFrom());
        dto.setValidTo(authorization.getValidTo());
        dto.setStatus(authorization.getStatus());
        dto.setFileUrl(authorization.getFileUrl());
        dto.setEffective(true);
        if (competence != null) {
            dto.setCompetenceId(competence.getId());
            dto.setUserName(competence.getUserName());
            dto.setCompetenceType(competence.getCompetenceType());
            dto.setCompetenceItem(competence.getCompetenceItem());
            dto.setCertificateNo(competence.getCertificateNo());
            dto.setCertificateFileUrl(competence.getCertificateFileUrl());
            dto.setAssessmentResult(competence.getAssessmentResult());
        } else {
            dto.setUserName(authorization.getUserId() == null ? null : "用户#" + authorization.getUserId());
        }
        return dto;
    }

    private LabPersonnelAuthorizationSummaryDTO preferLaterValidTo(LabPersonnelAuthorizationSummaryDTO left,
                                                                   LabPersonnelAuthorizationSummaryDTO right) {
        String leftValidTo = left.getValidTo() == null ? "" : left.getValidTo();
        String rightValidTo = right.getValidTo() == null ? "" : right.getValidTo();
        return rightValidTo.compareTo(leftValidTo) >= 0 ? right : left;
    }

    private boolean isUsableAuthorization(LabPersonnelAuthorizationDO authorization) {
        return authorization != null
                && authorization.getUserId() != null
                && isActiveStatus(authorization.getStatus())
                && isCurrentlyEffective(authorization.getValidFrom(), authorization.getValidTo());
    }

    private boolean isUsableCompetence(LabPersonnelCompetenceDO competence) {
        return competence != null
                && competence.getUserId() != null
                && isActiveStatus(competence.getStatus())
                && isCurrentlyEffective(competence.getValidFrom(), competence.getValidTo())
                && isPositiveOrBlank(competence.getAssessmentResult());
    }

    private boolean isActiveStatus(String status) {
        if (!StringUtils.hasText(status)) {
            return false;
        }
        String normalized = status.trim().toLowerCase(Locale.ROOT);
        return STATUS_ACTIVE.equals(normalized)
                || STATUS_VALID.equals(normalized)
                || STATUS_EFFECTIVE.equals(normalized)
                || STATUS_APPROVED.equals(normalized);
    }

    private boolean isCurrentlyEffective(String validFrom, String validTo) {
        LocalDate today = LocalDate.now();
        LocalDate from = parseLocalDate(validFrom);
        LocalDate to = parseLocalDate(validTo);
        return (from == null || !from.isAfter(today)) && to != null && !to.isBefore(today);
    }

    private boolean isPositiveOrBlank(String value) {
        if (!StringUtils.hasText(value)) {
            return true;
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        return "pass".equals(normalized) || "passed".equals(normalized)
                || "approved".equals(normalized) || "qualified".equals(normalized)
                || "合格".equals(value.trim()) || "通过".equals(value.trim());
    }

    private boolean matchesMethod(LabPersonnelAuthorizationDO authorization, Long methodId) {
        return methodId == null || authorization.getMethodId() == null || methodId.equals(authorization.getMethodId());
    }

    private boolean matchesEquipment(LabPersonnelAuthorizationDO authorization, Long equipmentId) {
        return equipmentId == null || authorization.getEquipmentId() == null || equipmentId.equals(authorization.getEquipmentId());
    }

    private boolean matchesText(String text, String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return true;
        }
        return StringUtils.hasText(text)
                && text.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    private void normalizePersonnelAuthorization(LabPersonnelAuthorizationDO authorization) {
        if (!StringUtils.hasText(authorization.getStatus()) || STATUS_DRAFT.equalsIgnoreCase(authorization.getStatus())) {
            authorization.setStatus(STATUS_ACTIVE);
        }
    }

    private void createPersonnelAuthorizationEvidenceChain(LabPersonnelAuthorizationDO authorization) {
        if (authorization.getUserId() == null) {
            return;
        }
        Long evidenceObjectId = evidenceObjectService.createEvidenceObject(buildPersonnelAuthorizationEvidenceObject(authorization));
        evidenceLinkService.createEvidenceLink(buildPersonnelAuthorizationEvidenceLink(authorization, evidenceObjectId));
    }

    private LabEvidenceObjectDO buildPersonnelAuthorizationEvidenceObject(LabPersonnelAuthorizationDO authorization) {
        LabEvidenceObjectDO evidenceObject = new LabEvidenceObjectDO();
        evidenceObject.setEvidenceCode("PERSON-AUTH-" + authorization.getUserId() + "-" + authorization.getId());
        evidenceObject.setEvidenceName("人员授权-" + authorization.getUserId() + "-" + nullToEmpty(authorization.getAuthScope()));
        evidenceObject.setEvidenceType(EVIDENCE_TYPE_PERSON_AUTH);
        evidenceObject.setSourceObject(EVIDENCE_SOURCE_PERSONNEL_AUTHORIZATION);
        evidenceObject.setSourceObjectId(authorization.getId());
        evidenceObject.setSourceObjectNo(String.valueOf(authorization.getUserId()));
        evidenceObject.setBusinessDomain(BUSINESS_DOMAIN_PERSONNEL);
        evidenceObject.setFileUrl(authorization.getFileUrl());
        evidenceObject.setFileName("PERSON-AUTH-" + authorization.getId());
        evidenceObject.setFileFormat(resolveFileFormat(authorization.getFileUrl()));
        evidenceObject.setIssuedBy(authorization.getAuthorizedBy() == null ? null : String.valueOf(authorization.getAuthorizedBy()));
        evidenceObject.setIssuedAt(parseLocalDate(authorization.getAuthorizedTime()));
        evidenceObject.setValidFrom(parseLocalDate(authorization.getValidFrom()));
        evidenceObject.setValidTo(parseLocalDate(authorization.getValidTo()));
        evidenceObject.setStatus(STATUS_ACTIVE.equalsIgnoreCase(authorization.getStatus()) ? STATUS_EFFECTIVE : authorization.getStatus());
        evidenceObject.setSummary(String.format("用户%s的人员授权，范围%s，有效期至%s",
                authorization.getUserId(),
                nullToEmpty(authorization.getAuthScope()),
                nullToEmpty(authorization.getValidTo())));
        return evidenceObject;
    }

    private LabEvidenceLinkSaveReqVO buildPersonnelAuthorizationEvidenceLink(LabPersonnelAuthorizationDO authorization,
                                                                             Long evidenceObjectId) {
        LabEvidenceLinkSaveReqVO link = new LabEvidenceLinkSaveReqVO();
        link.setEvidenceObjectId(evidenceObjectId);
        link.setLinkedBizType(LINKED_BIZ_TYPE_PERSONNEL_USER);
        link.setLinkedBizId(authorization.getUserId());
        link.setLinkedBizNo(String.valueOf(authorization.getUserId()));
        LabStandardClauseDO personnelClause = standardClauseMapper.selectFirstByClauseCategory(BUSINESS_DOMAIN_PERSONNEL);
        link.setClauseId(personnelClause == null ? null : personnelClause.getId());
        link.setClauseCategory(BUSINESS_DOMAIN_PERSONNEL);
        link.setLinkStatus("linked");
        link.setLinkReason("人员授权支撑 CNAS/CMA 人员能力与授权条款");
        link.setRemark("由人员授权记录自动生成");
        return link;
    }

    private String resolveFileFormat(String fileUrl) {
        if (!StringUtils.hasText(fileUrl) || !fileUrl.contains(".")) {
            return null;
        }
        String suffix = fileUrl.substring(fileUrl.lastIndexOf('.') + 1);
        return suffix.length() > 32 ? null : suffix.toLowerCase(Locale.ROOT);
    }

    private LocalDate parseLocalDate(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            String dateValue = value.length() > 10 ? value.substring(0, 10) : value;
            return LocalDate.parse(dateValue);
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }


    public Long createEquipmentTraceability(LabQualityRecordSaveReqVO createReqVO) {
        return equipmentTraceabilityService.createEquipmentTraceability(createReqVO);
    }

    public void updateEquipmentTraceability(LabQualityRecordSaveReqVO updateReqVO) {
        equipmentTraceabilityService.updateEquipmentTraceability(updateReqVO);
    }

    public void deleteEquipmentTraceability(Long id) {
        equipmentTraceabilityService.deleteEquipmentTraceability(id);
    }

    public LabQualityRecordRespVO getEquipmentTraceability(Long id) {
        return equipmentTraceabilityService.getEquipmentTraceability(id);
    }

    public PageResult<LabQualityRecordRespVO> getEquipmentTraceabilityPage(LabQualityRecordPageReqVO pageReqVO) {
        return equipmentTraceabilityService.getEquipmentTraceabilityPage(pageReqVO);
    }

    public void updateEquipmentTraceabilityStatus(Long id, String status) {
        equipmentTraceabilityService.updateEquipmentTraceabilityStatus(id, status);
    }

    public List<LabEquipmentCalibrationEvidenceDTO> getCurrentEquipmentCalibrationEvidence(Long equipmentId) {
        return equipmentTraceabilityService.getCurrentCalibrationEvidence(equipmentId);
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
