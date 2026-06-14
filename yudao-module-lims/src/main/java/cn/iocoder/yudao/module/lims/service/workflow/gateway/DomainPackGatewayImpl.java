package cn.iocoder.yudao.module.lims.service.workflow.gateway;

import cn.iocoder.yudao.module.lab.service.domainpack.LabDomainPackQueryService;
import cn.iocoder.yudao.module.lab.service.domainpack.dto.LabDomainPackSnapshotDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class DomainPackGatewayImpl implements DomainPackGateway {

    @Resource
    private LabDomainPackQueryService labDomainPackQueryService;

    @Override
    public LabDomainPackSnapshotDTO getPublishedPackSnapshot(Long domainPackId) {
        return labDomainPackQueryService.getPublishedSnapshot(domainPackId);
    }

}
