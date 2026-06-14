package cn.iocoder.yudao.module.lab.service.packconfig;

import cn.iocoder.yudao.module.lab.controller.admin.packconfig.vo.LabPackConfigRespVO;
import cn.iocoder.yudao.module.lab.controller.admin.packconfig.vo.LabPackConfigSaveReqVO;
import jakarta.validation.Valid;

public interface LabPackConfigService {

    LabPackConfigRespVO getPackConfig(Long domainPackId);

    void savePackConfig(@Valid LabPackConfigSaveReqVO saveReqVO);

}
