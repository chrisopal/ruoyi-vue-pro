package cn.iocoder.yudao.module.lab.service.domain;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.lab.controller.admin.domain.vo.LabDomainProfilePageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.domain.vo.LabDomainProfileSaveReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.domain.LabDomainProfileDO;

/**
 * 实验室检测方向 Service 接口。
 */
public interface LabDomainProfileService {

    Long createDomainProfile(LabDomainProfileSaveReqVO createReqVO);

    void updateDomainProfile(LabDomainProfileSaveReqVO updateReqVO);

    void deleteDomainProfile(Long id);

    LabDomainProfileDO getDomainProfile(Long id);

    PageResult<LabDomainProfileDO> getDomainProfilePage(LabDomainProfilePageReqVO pageReqVO);

}
