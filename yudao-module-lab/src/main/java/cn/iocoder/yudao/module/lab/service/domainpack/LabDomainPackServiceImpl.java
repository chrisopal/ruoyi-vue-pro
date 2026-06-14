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
import org.springframework.util.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lab.enums.ErrorCodeConstants.*;

@Service
@Validated
public class LabDomainPackServiceImpl implements LabDomainPackService {

    private static final String STATUS_DRAFT = "draft";
    private static final String STATUS_PUBLISHED = "published";
    private static final String STATUS_ARCHIVED = "archived";

    @Resource
    private LabDomainPackMapper domainPackMapper;
    @Resource
    private LabDomainProfileService domainProfileService;

    @Override
    public Long createDomainPack(LabDomainPackSaveReqVO createReqVO) {
        validateDomainProfileExists(createReqVO.getDomainId());
        validateDomainPackVersionUnique(null, createReqVO.getPackCode(), createReqVO.getPackVersion());

        LabDomainPackDO domainPack = BeanUtils.toBean(createReqVO, LabDomainPackDO.class);
        if (!StringUtils.hasText(domainPack.getStatus())) {
            domainPack.setStatus(STATUS_DRAFT);
        }
        domainPackMapper.insert(domainPack);
        return domainPack.getId();
    }

    @Override
    public void updateDomainPack(LabDomainPackSaveReqVO updateReqVO) {
        LabDomainPackDO existing = validateDomainPackExists(updateReqVO.getId());
        validateDomainPackEditable(existing);
        validateDomainProfileExists(updateReqVO.getDomainId());
        validateDomainPackVersionUnique(updateReqVO.getId(), updateReqVO.getPackCode(), updateReqVO.getPackVersion());

        LabDomainPackDO updateObj = BeanUtils.toBean(updateReqVO, LabDomainPackDO.class);
        domainPackMapper.updateById(updateObj);
    }

    @Override
    public void publishDomainPack(Long id) {
        LabDomainPackDO domainPack = validateDomainPackExists(id);
        if (!STATUS_DRAFT.equalsIgnoreCase(domainPack.getStatus())) {
            throw exception(DOMAIN_PACK_STATUS_INVALID);
        }
        LabDomainPackDO updateObj = new LabDomainPackDO();
        updateObj.setId(id);
        updateObj.setStatus(STATUS_PUBLISHED);
        domainPackMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long copyDomainPackVersion(Long id, String targetVersion) {
        LabDomainPackDO source = validateDomainPackExists(id);
        if (!StringUtils.hasText(targetVersion)) {
            throw exception(DOMAIN_PACK_STATUS_INVALID);
        }
        validateDomainPackVersionUnique(null, source.getPackCode(), targetVersion);

        LabDomainPackDO target = BeanUtils.toBean(source, LabDomainPackDO.class);
        target.setId(null);
        target.setPackVersion(targetVersion);
        target.setStatus(STATUS_DRAFT);
        domainPackMapper.insert(target);
        return target.getId();
    }

    @Override
    public void archiveDomainPack(Long id) {
        LabDomainPackDO domainPack = validateDomainPackExists(id);
        if (!STATUS_PUBLISHED.equalsIgnoreCase(domainPack.getStatus())) {
            throw exception(DOMAIN_PACK_STATUS_INVALID);
        }
        LabDomainPackDO updateObj = new LabDomainPackDO();
        updateObj.setId(id);
        updateObj.setStatus(STATUS_ARCHIVED);
        domainPackMapper.updateById(updateObj);
    }

    @Override
    public void deleteDomainPack(Long id) {
        LabDomainPackDO existing = validateDomainPackExists(id);
        validateDomainPackEditable(existing);
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

    private LabDomainPackDO validateDomainPackExists(Long id) {
        LabDomainPackDO domainPack = id == null ? null : domainPackMapper.selectById(id);
        if (domainPack == null) {
            throw exception(DOMAIN_PACK_NOT_EXISTS);
        }
        return domainPack;
    }

    private void validateDomainProfileExists(Long domainId) {
        if (domainProfileService.getDomainProfile(domainId) == null) {
            throw exception(DOMAIN_PROFILE_NOT_EXISTS);
        }
    }

    private void validateDomainPackVersionUnique(Long id, String packCode, String packVersion) {
        LabDomainPackDO domainPack = domainPackMapper.selectByPackCodeAndVersion(packCode, packVersion);
        if (domainPack == null) {
            return;
        }
        if (id == null || !domainPack.getId().equals(id)) {
            throw exception(DOMAIN_PACK_VERSION_DUPLICATE);
        }
    }

    private void validateDomainPackEditable(LabDomainPackDO domainPack) {
        if (STATUS_PUBLISHED.equalsIgnoreCase(domainPack.getStatus())
                || STATUS_ARCHIVED.equalsIgnoreCase(domainPack.getStatus())) {
            throw exception(DOMAIN_PACK_PUBLISHED_IMMUTABLE);
        }
    }

}
