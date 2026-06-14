package cn.iocoder.yudao.module.lab.service.domainpack;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.lab.controller.admin.domainpack.vo.LabDomainPackPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.domainpack.vo.LabDomainPackSaveReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.domainpack.LabDomainPackDO;
import cn.iocoder.yudao.module.lab.dal.mysql.domainpack.LabDomainPackMapper;
import cn.iocoder.yudao.module.lab.service.domain.LabDomainProfileService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lab.enums.ErrorCodeConstants.*;

@Service
@Validated
public class LabDomainPackServiceImpl implements LabDomainPackService {

    @Resource
    private LabDomainPackMapper domainPackMapper;
    @Resource
    private LabDomainProfileService domainProfileService;

    @Override
    public Long createDomainPack(LabDomainPackSaveReqVO createReqVO) {
        validateDomainProfileExists(createReqVO.getDomainId());
        validateDomainPackCodeUnique(null, createReqVO.getPackCode());

        LabDomainPackDO domainPack = BeanUtils.toBean(createReqVO, LabDomainPackDO.class);
        domainPackMapper.insert(domainPack);
        return domainPack.getId();
    }

    @Override
    public void updateDomainPack(LabDomainPackSaveReqVO updateReqVO) {
        validateDomainPackExists(updateReqVO.getId());
        validateDomainProfileExists(updateReqVO.getDomainId());
        validateDomainPackCodeUnique(updateReqVO.getId(), updateReqVO.getPackCode());

        LabDomainPackDO updateObj = BeanUtils.toBean(updateReqVO, LabDomainPackDO.class);
        domainPackMapper.updateById(updateObj);
    }

    @Override
    public void deleteDomainPack(Long id) {
        validateDomainPackExists(id);
        domainPackMapper.deleteById(id);
    }

    @Override
    public LabDomainPackDO getDomainPack(Long id) {
        return domainPackMapper.selectById(id);
    }

    @Override
    public PageResult<LabDomainPackDO> getDomainPackPage(LabDomainPackPageReqVO pageReqVO) {
        return domainPackMapper.selectPage(pageReqVO);
    }

    private void validateDomainPackExists(Long id) {
        if (id == null || domainPackMapper.selectById(id) == null) {
            throw exception(DOMAIN_PACK_NOT_EXISTS);
        }
    }

    private void validateDomainProfileExists(Long domainId) {
        if (domainProfileService.getDomainProfile(domainId) == null) {
            throw exception(DOMAIN_PROFILE_NOT_EXISTS);
        }
    }

    private void validateDomainPackCodeUnique(Long id, String packCode) {
        LabDomainPackDO domainPack = domainPackMapper.selectByPackCode(packCode);
        if (domainPack == null) {
            return;
        }
        if (id == null || !domainPack.getId().equals(id)) {
            throw exception(DOMAIN_PACK_CODE_DUPLICATE);
        }
    }

}
