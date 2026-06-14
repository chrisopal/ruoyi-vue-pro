package cn.iocoder.yudao.module.lims.service.workflow.gateway;

import cn.iocoder.yudao.module.lims.service.workflow.model.AvailablePersonnel;
import cn.iocoder.yudao.module.lims.service.workflow.model.PersonnelAuthorizationEvidence;

import java.util.List;

public interface PersonnelGateway {

    List<AvailablePersonnel> getAvailablePersonnel(String testItem, Long methodId, Long equipmentId);

    List<PersonnelAuthorizationEvidence> getCurrentAuthorizationEvidence(Long userId, String testItem,
                                                                         Long methodId, Long equipmentId);

}
