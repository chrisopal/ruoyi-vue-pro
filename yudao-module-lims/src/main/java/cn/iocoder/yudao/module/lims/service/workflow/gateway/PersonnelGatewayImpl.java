package cn.iocoder.yudao.module.lims.service.workflow.gateway;

import cn.iocoder.yudao.module.lab.service.quality.LabQualityRecordService;
import cn.iocoder.yudao.module.lab.service.quality.dto.LabPersonnelAuthorizationSummaryDTO;
import cn.iocoder.yudao.module.lims.service.workflow.model.AvailablePersonnel;
import cn.iocoder.yudao.module.lims.service.workflow.model.PersonnelAuthorizationEvidence;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonnelGatewayImpl implements PersonnelGateway {

    @Resource
    private LabQualityRecordService qualityRecordService;

    @Override
    public List<AvailablePersonnel> getAvailablePersonnel(String testItem, Long methodId, Long equipmentId) {
        return qualityRecordService.getAvailablePersonnel(testItem, methodId, equipmentId).stream()
                .map(this::toAvailablePersonnel)
                .toList();
    }

    @Override
    public List<PersonnelAuthorizationEvidence> getCurrentAuthorizationEvidence(Long userId, String testItem,
                                                                               Long methodId, Long equipmentId) {
        return qualityRecordService.getCurrentPersonnelAuthorizationEvidence(userId, testItem, methodId, equipmentId).stream()
                .map(this::toEvidence)
                .toList();
    }

    private AvailablePersonnel toAvailablePersonnel(LabPersonnelAuthorizationSummaryDTO person) {
        return new AvailablePersonnel(
                person.getUserId(),
                person.getUserName(),
                person.getAuthType(),
                person.getAuthScope(),
                person.getAuthorizationId(),
                person.getValidTo(),
                person.getCompetenceType(),
                person.getCompetenceItem(),
                person.getCertificateNo(),
                person.isEffective());
    }

    private PersonnelAuthorizationEvidence toEvidence(LabPersonnelAuthorizationSummaryDTO evidence) {
        return new PersonnelAuthorizationEvidence(
                evidence.getAuthorizationId(),
                evidence.getUserId(),
                evidence.getUserName(),
                evidence.getAuthType(),
                evidence.getAuthScope(),
                evidence.getValidFrom(),
                evidence.getValidTo(),
                evidence.getStatus(),
                evidence.getFileUrl(),
                evidence.getCompetenceId(),
                evidence.getCompetenceType(),
                evidence.getCompetenceItem(),
                evidence.getCertificateNo(),
                evidence.getCertificateFileUrl(),
                evidence.getAssessmentResult(),
                evidence.isEffective());
    }

}
