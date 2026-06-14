package cn.iocoder.yudao.module.lab.service.evidencelink;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.lab.controller.admin.evidencelink.vo.LabEvidenceLinkPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.evidencelink.vo.LabEvidenceLinkSaveReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.evidenceobject.LabEvidenceObjectDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.evidencelink.LabEvidenceLinkDO;
import cn.iocoder.yudao.module.lab.dal.mysql.evidencelink.LabEvidenceLinkMapper;
import cn.iocoder.yudao.module.lab.service.evidenceobject.LabEvidenceObjectService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lab.enums.ErrorCodeConstants.EVIDENCE_LINK_EVIDENCE_REQUIRED;
import static cn.iocoder.yudao.module.lab.enums.ErrorCodeConstants.EVIDENCE_LINK_NOT_EXISTS;
import static cn.iocoder.yudao.module.lab.enums.ErrorCodeConstants.EVIDENCE_LINK_SOURCE_REQUIRED;

@Service
@Validated
public class LabEvidenceLinkServiceImpl implements LabEvidenceLinkService {

    @Resource
    private LabEvidenceLinkMapper evidenceLinkMapper;
    @Resource
    private LabEvidenceObjectService evidenceObjectService;

    @Override
    public Long createEvidenceLink(LabEvidenceLinkSaveReqVO createReqVO) {
        LabEvidenceLinkDO evidenceLink = BeanUtils.toBean(createReqVO, LabEvidenceLinkDO.class);
        fillFromEvidenceObjectIfPresent(evidenceLink);
        validateEvidenceReference(evidenceLink);
        fillEvidenceHashIfAbsent(evidenceLink);
        evidenceLinkMapper.insert(evidenceLink);
        return evidenceLink.getId();
    }

    @Override
    public void updateEvidenceLink(LabEvidenceLinkSaveReqVO updateReqVO) {
        validateEvidenceLinkExists(updateReqVO.getId());
        LabEvidenceLinkDO evidenceLink = BeanUtils.toBean(updateReqVO, LabEvidenceLinkDO.class);
        fillFromEvidenceObjectIfPresent(evidenceLink);
        validateEvidenceReference(evidenceLink);
        fillEvidenceHashIfAbsent(evidenceLink);
        evidenceLinkMapper.updateById(evidenceLink);
    }

    @Override
    public void deleteEvidenceLink(Long id) {
        validateEvidenceLinkExists(id);
        evidenceLinkMapper.deleteById(id);
    }

    @Override
    public LabEvidenceLinkDO getEvidenceLink(Long id) {
        return evidenceLinkMapper.selectById(id);
    }

    @Override
    public PageResult<LabEvidenceLinkDO> getEvidenceLinkPage(LabEvidenceLinkPageReqVO pageReqVO) {
        return evidenceLinkMapper.selectPage(pageReqVO);
    }

    @Override
    public List<LabEvidenceLinkDO> getEvidenceLinksBySource(String sourceType, Long sourceId) {
        return evidenceLinkMapper.selectListBySource(sourceType, sourceId);
    }

    @Override
    public Long uploadEvidenceLink(LabEvidenceLinkSaveReqVO uploadReqVO) {
        return createEvidenceLink(uploadReqVO);
    }

    private void validateEvidenceLinkExists(Long id) {
        if (id == null || evidenceLinkMapper.selectById(id) == null) {
            throw exception(EVIDENCE_LINK_NOT_EXISTS);
        }
    }

    private void fillFromEvidenceObjectIfPresent(LabEvidenceLinkDO evidenceLink) {
        if (evidenceLink.getEvidenceObjectId() == null) {
            return;
        }
        LabEvidenceObjectDO evidenceObject = evidenceObjectService.getEvidenceObjectOrThrow(evidenceLink.getEvidenceObjectId());
        if (!StringUtils.hasText(evidenceLink.getEvidenceCode())) {
            evidenceLink.setEvidenceCode(evidenceObject.getEvidenceCode());
        }
        if (!StringUtils.hasText(evidenceLink.getEvidenceName())) {
            evidenceLink.setEvidenceName(evidenceObject.getEvidenceName());
        }
        if (!StringUtils.hasText(evidenceLink.getEvidenceUrl())) {
            evidenceLink.setEvidenceUrl(evidenceObject.getFileUrl());
        }
        if (!StringUtils.hasText(evidenceLink.getEvidenceHash())) {
            evidenceLink.setEvidenceHash(evidenceObject.getEvidenceHash());
        }
        if (!StringUtils.hasText(evidenceLink.getSourceObject())) {
            evidenceLink.setSourceObject(evidenceObject.getSourceObject());
        }
        if (evidenceLink.getSourceObjectId() == null) {
            evidenceLink.setSourceObjectId(evidenceObject.getSourceObjectId());
        }
        if (!StringUtils.hasText(evidenceLink.getSourceObjectNo())) {
            evidenceLink.setSourceObjectNo(evidenceObject.getSourceObjectNo());
        }
    }

    private void validateEvidenceReference(LabEvidenceLinkDO evidenceLink) {
        if (evidenceLink.getEvidenceObjectId() == null && !StringUtils.hasText(evidenceLink.getEvidenceCode())) {
            throw exception(EVIDENCE_LINK_EVIDENCE_REQUIRED);
        }
        if (evidenceLink.getEvidenceObjectId() == null && !StringUtils.hasText(evidenceLink.getSourceObject())) {
            throw exception(EVIDENCE_LINK_SOURCE_REQUIRED);
        }
    }

    private void fillEvidenceHashIfAbsent(LabEvidenceLinkDO evidenceLink) {
        if (StringUtils.hasText(evidenceLink.getEvidenceHash())) {
            return;
        }
        String seed = String.join("|",
                nullToEmpty(evidenceLink.getEvidenceCode()),
                nullToEmpty(evidenceLink.getEvidenceName()),
                nullToEmpty(evidenceLink.getEvidenceUrl()),
                nullToEmpty(evidenceLink.getSourceObject()),
                nullToEmpty(evidenceLink.getSourceObjectNo()),
                nullToEmpty(evidenceLink.getLinkedBizType()),
                nullToEmpty(evidenceLink.getLinkedBizNo()));
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            evidenceLink.setEvidenceHash(HexFormat.of().formatHex(digest.digest(seed.getBytes(StandardCharsets.UTF_8))));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm is unavailable", ex);
        }
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

}
