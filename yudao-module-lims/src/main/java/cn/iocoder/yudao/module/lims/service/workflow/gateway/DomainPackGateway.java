package cn.iocoder.yudao.module.lims.service.workflow.gateway;

import cn.iocoder.yudao.module.lab.service.domainpack.dto.LabDomainPackSnapshotDTO;

public interface DomainPackGateway {

    LabDomainPackSnapshotDTO getPublishedPackSnapshot(Long domainPackId);

}
