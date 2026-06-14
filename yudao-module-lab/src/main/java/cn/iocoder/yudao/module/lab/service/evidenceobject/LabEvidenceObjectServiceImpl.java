package cn.iocoder.yudao.module.lab.service.evidenceobject;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.lab.controller.admin.evidenceobject.vo.LabEvidenceObjectPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.evidenceobject.vo.LabEvidenceObjectSaveReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.evidenceobject.LabEvidenceObjectDO;
import cn.iocoder.yudao.module.lab.dal.mysql.evidenceobject.LabEvidenceObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lab.enums.ErrorCodeConstants.EVIDENCE_OBJECT_CODE_DUPLICATE;
import static cn.iocoder.yudao.module.lab.enums.ErrorCodeConstants.EVIDENCE_OBJECT_NOT_EXISTS;

@Service
@Validated
public class LabEvidenceObjectServiceImpl implements LabEvidenceObjectService {

    private static final String STATUS_DRAFT = "draft";
    private static final String STATUS_EFFECTIVE = "effective";

    @Resource
    private LabEvidenceObjectMapper evidenceObjectMapper;

    @Override
    public Long createEvidenceObject(LabEvidenceObjectSaveReqVO createReqVO) {
        validateEvidenceCodeUnique(null, createReqVO.getEvidenceCode());

        LabEvidenceObjectDO evidenceObject = BeanUtils.toBean(createReqVO, LabEvidenceObjectDO.class);
        return createEvidenceObject(evidenceObject);
    }

    @Override
    public void updateEvidenceObject(LabEvidenceObjectSaveReqVO updateReqVO) {
        validateEvidenceObjectExists(updateReqVO.getId());
        validateEvidenceCodeUnique(updateReqVO.getId(), updateReqVO.getEvidenceCode());

        LabEvidenceObjectDO evidenceObject = BeanUtils.toBean(updateReqVO, LabEvidenceObjectDO.class);
        normalizeEvidenceObject(evidenceObject);
        evidenceObjectMapper.updateById(evidenceObject);
    }

    @Override
    public void deleteEvidenceObject(Long id) {
        validateEvidenceObjectExists(id);
        evidenceObjectMapper.deleteById(id);
    }

    @Override
    public LabEvidenceObjectDO getEvidenceObject(Long id) {
        return evidenceObjectMapper.selectById(id);
    }

    @Override
    public PageResult<LabEvidenceObjectDO> getEvidenceObjectPage(LabEvidenceObjectPageReqVO pageReqVO) {
        return evidenceObjectMapper.selectPage(pageReqVO);
    }

    @Override
    public LabEvidenceObjectDO getEvidenceObjectOrThrow(Long id) {
        LabEvidenceObjectDO evidenceObject = id == null ? null : evidenceObjectMapper.selectById(id);
        if (evidenceObject == null) {
            throw exception(EVIDENCE_OBJECT_NOT_EXISTS);
        }
        return evidenceObject;
    }

    @Override
    public Long createEvidenceObject(LabEvidenceObjectDO evidenceObject) {
        validateEvidenceCodeUnique(null, evidenceObject.getEvidenceCode());
        normalizeEvidenceObject(evidenceObject);
        evidenceObjectMapper.insert(evidenceObject);
        return evidenceObject.getId();
    }

    private void validateEvidenceObjectExists(Long id) {
        getEvidenceObjectOrThrow(id);
    }

    private void validateEvidenceCodeUnique(Long id, String evidenceCode) {
        LabEvidenceObjectDO evidenceObject = evidenceObjectMapper.selectByEvidenceCode(evidenceCode);
        if (evidenceObject == null) {
            return;
        }
        if (id == null || !evidenceObject.getId().equals(id)) {
            throw exception(EVIDENCE_OBJECT_CODE_DUPLICATE);
        }
    }

    private void normalizeEvidenceObject(LabEvidenceObjectDO evidenceObject) {
        if (!StringUtils.hasText(evidenceObject.getStatus()) || STATUS_DRAFT.equalsIgnoreCase(evidenceObject.getStatus())) {
            evidenceObject.setStatus(STATUS_EFFECTIVE);
        }
        fillEvidenceHashIfAbsent(evidenceObject);
    }

    private void fillEvidenceHashIfAbsent(LabEvidenceObjectDO evidenceObject) {
        if (StringUtils.hasText(evidenceObject.getEvidenceHash())) {
            return;
        }
        String seed = String.join("|",
                nullToEmpty(evidenceObject.getEvidenceCode()),
                nullToEmpty(evidenceObject.getEvidenceName()),
                nullToEmpty(evidenceObject.getEvidenceType()),
                nullToEmpty(evidenceObject.getSourceObject()),
                nullToEmpty(evidenceObject.getSourceObjectNo()),
                nullToEmpty(evidenceObject.getFileUrl()),
                nullToEmpty(evidenceObject.getValidTo() == null ? null : evidenceObject.getValidTo().toString()));
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            evidenceObject.setEvidenceHash(HexFormat.of().formatHex(digest.digest(seed.getBytes(StandardCharsets.UTF_8))));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm is unavailable", ex);
        }
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

}
