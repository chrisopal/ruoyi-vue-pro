package cn.iocoder.yudao.module.lab.service.domainpack;

import cn.iocoder.yudao.module.lab.service.domainpack.dto.LabDomainPackSnapshotDTO;

public interface LabDomainPackQueryService {

    LabDomainPackSnapshotDTO getPublishedSnapshot(Long domainPackId);

}
