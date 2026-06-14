package cn.iocoder.yudao.module.lab.service.domain;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.lab.controller.admin.domain.vo.LabDomainProfilePageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.domain.vo.LabDomainProfileSaveReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.domain.LabDomainProfileDO;
import cn.iocoder.yudao.module.lab.dal.mysql.domain.LabDomainProfileMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lab.enums.ErrorCodeConstants.DOMAIN_PROFILE_CODE_DUPLICATE;
import static cn.iocoder.yudao.module.lab.enums.ErrorCodeConstants.DOMAIN_PROFILE_NOT_EXISTS;

/**
 * 实验室检测方向 Service 实现类。
 */
@Service
@Validated
public class LabDomainProfileServiceImpl implements LabDomainProfileService {

    @Resource
    private LabDomainProfileMapper domainProfileMapper;

    @Override
    public Long createDomainProfile(LabDomainProfileSaveReqVO createReqVO) {
        validateDomainCodeUnique(createReqVO.getDomainCode(), null);
        LabDomainProfileDO domainProfile = BeanUtils.toBean(createReqVO, LabDomainProfileDO.class);
        domainProfileMapper.insert(domainProfile);
        return domainProfile.getId();
    }

    @Override
    public void updateDomainProfile(LabDomainProfileSaveReqVO updateReqVO) {
        validateDomainProfileExists(updateReqVO.getId());
        validateDomainCodeUnique(updateReqVO.getDomainCode(), updateReqVO.getId());
        LabDomainProfileDO updateObj = BeanUtils.toBean(updateReqVO, LabDomainProfileDO.class);
        domainProfileMapper.updateById(updateObj);
    }

    @Override
    public void deleteDomainProfile(Long id) {
        validateDomainProfileExists(id);
        domainProfileMapper.deleteById(id);
    }

    @Override
    public LabDomainProfileDO getDomainProfile(Long id) {
        return domainProfileMapper.selectById(id);
    }

    @Override
    public PageResult<LabDomainProfileDO> getDomainProfilePage(LabDomainProfilePageReqVO pageReqVO) {
        return domainProfileMapper.selectPage(pageReqVO);
    }

    private void validateDomainProfileExists(Long id) {
        if (id == null || domainProfileMapper.selectById(id) == null) {
            throw exception(DOMAIN_PROFILE_NOT_EXISTS);
        }
    }

    private void validateDomainCodeUnique(String domainCode, Long id) {
        LabDomainProfileDO domainProfile = domainProfileMapper.selectByDomainCode(domainCode);
        if (domainProfile == null) {
            return;
        }
        if (id == null || !domainProfile.getId().equals(id)) {
            throw exception(DOMAIN_PROFILE_CODE_DUPLICATE);
        }
    }

}
