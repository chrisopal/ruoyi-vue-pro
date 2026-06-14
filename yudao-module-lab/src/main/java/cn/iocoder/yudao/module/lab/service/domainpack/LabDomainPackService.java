package cn.iocoder.yudao.module.lab.service.domainpack;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.lab.controller.admin.domainpack.vo.LabDomainPackPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.domainpack.vo.LabDomainPackSaveReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.domainpack.LabDomainPackDO;
import jakarta.validation.Valid;

public interface LabDomainPackService {

    Long createDomainPack(@Valid LabDomainPackSaveReqVO createReqVO);

    void updateDomainPack(@Valid LabDomainPackSaveReqVO updateReqVO);

    void publishDomainPack(Long id);

    Long copyDomainPackVersion(Long id, String targetVersion);

    void archiveDomainPack(Long id);

    void deleteDomainPack(Long id);

    LabDomainPackDO getDomainPack(Long id);

    PageResult<LabDomainPackDO> getDomainPackPage(LabDomainPackPageReqVO pageReqVO);

}
