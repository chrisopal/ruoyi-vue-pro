package cn.iocoder.yudao.module.lab.service.reviewpackage;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo.LabReviewBatchPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo.LabReviewBatchSaveReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo.LabReviewCapaExcelVO;
import cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo.LabReviewChecklistExcelVO;
import cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo.LabReviewEvidenceExcelVO;
import cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo.LabReviewItemPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo.LabReviewItemSaveReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo.LabReviewNonconformityExcelVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.reviewpackage.LabReviewBatchDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.reviewpackage.LabReviewItemDO;
import cn.iocoder.yudao.module.lab.dal.mysql.reviewpackage.LabReviewBatchMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.reviewpackage.LabReviewItemMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lab.enums.ErrorCodeConstants.*;

@Service
@Validated
public class LabReviewPackageServiceImpl implements LabReviewPackageService {

    public static final String DEFAULT_BATCH_CODE = "RP-MVP-001";

    private static final String ITEM_TYPE_CHECKLIST = "checklist";
    private static final String ITEM_TYPE_EVIDENCE = "evidence";
    private static final String ITEM_TYPE_NC = "nc";
    private static final String ITEM_TYPE_CAPA = "capa";

    @Resource
    private LabReviewBatchMapper reviewBatchMapper;
    @Resource
    private LabReviewItemMapper reviewItemMapper;

    @Override
    public Long createReviewBatch(LabReviewBatchSaveReqVO createReqVO) {
        validateReviewBatchCodeUnique(null, createReqVO.getBatchCode());
        LabReviewBatchDO batch = BeanUtils.toBean(createReqVO, LabReviewBatchDO.class);
        reviewBatchMapper.insert(batch);
        return batch.getId();
    }

    @Override
    public void updateReviewBatch(LabReviewBatchSaveReqVO updateReqVO) {
        validateReviewBatchExists(updateReqVO.getId());
        validateReviewBatchCodeUnique(updateReqVO.getId(), updateReqVO.getBatchCode());
        reviewBatchMapper.updateById(BeanUtils.toBean(updateReqVO, LabReviewBatchDO.class));
    }

    @Override
    public void deleteReviewBatch(Long id) {
        validateReviewBatchExists(id);
        reviewBatchMapper.deleteById(id);
    }

    @Override
    public LabReviewBatchDO getReviewBatch(Long id) {
        return reviewBatchMapper.selectById(id);
    }

    @Override
    public PageResult<LabReviewBatchDO> getReviewBatchPage(LabReviewBatchPageReqVO pageReqVO) {
        return reviewBatchMapper.selectPage(pageReqVO);
    }

    @Override
    public Long createReviewItem(LabReviewItemSaveReqVO createReqVO) {
        validateReviewBatchExists(createReqVO.getBatchId());
        LabReviewItemDO item = BeanUtils.toBean(createReqVO, LabReviewItemDO.class);
        reviewItemMapper.insert(item);
        return item.getId();
    }

    @Override
    public void updateReviewItem(LabReviewItemSaveReqVO updateReqVO) {
        validateReviewItemExists(updateReqVO.getId());
        validateReviewBatchExists(updateReqVO.getBatchId());
        reviewItemMapper.updateById(BeanUtils.toBean(updateReqVO, LabReviewItemDO.class));
    }

    @Override
    public void deleteReviewItem(Long id) {
        validateReviewItemExists(id);
        reviewItemMapper.deleteById(id);
    }

    @Override
    public LabReviewItemDO getReviewItem(Long id) {
        return reviewItemMapper.selectById(id);
    }

    @Override
    public PageResult<LabReviewItemDO> getReviewItemPage(LabReviewItemPageReqVO pageReqVO) {
        return reviewItemMapper.selectPage(pageReqVO);
    }

    @Override
    public List<LabReviewChecklistExcelVO> getChecklistRows(String batchCode) {
        return getItems(batchCode, ITEM_TYPE_CHECKLIST).stream()
                .map(item -> new LabReviewChecklistExcelVO(item.getStandardName(), item.getClauseCode(),
                        item.getClauseCategory(), item.getCheckPoint(), item.getExpectedEvidence(), item.getOwnerRole()))
                .toList();
    }

    @Override
    public List<LabReviewEvidenceExcelVO> getEvidenceRows(String batchCode) {
        return getItems(batchCode, ITEM_TYPE_EVIDENCE).stream()
                .map(item -> new LabReviewEvidenceExcelVO(item.getEvidenceName(), item.getSourceObject(),
                        item.getLinkedObjectNo(), item.getClauseCategory(), item.getLinkStatus(), item.getRemark()))
                .toList();
    }

    @Override
    public List<LabReviewNonconformityExcelVO> getNonconformityRows(String batchCode) {
        return getItems(batchCode, ITEM_TYPE_NC).stream()
                .map(item -> new LabReviewNonconformityExcelVO(item.getNcNo(), item.getClauseCode(),
                        item.getSeverity(), item.getDescription(), item.getOwner(), item.getDueDate()))
                .toList();
    }

    @Override
    public List<LabReviewCapaExcelVO> getCapaRows(String batchCode) {
        return getItems(batchCode, ITEM_TYPE_CAPA).stream()
                .map(item -> new LabReviewCapaExcelVO(item.getCapaNo(), item.getNcNo(),
                        item.getRootCause(), item.getAction(), item.getOwner(), item.getStatus()))
                .toList();
    }

    private List<LabReviewItemDO> getItems(String batchCode, String itemType) {
        LabReviewBatchDO batch = reviewBatchMapper.selectByBatchCode(normalizeBatchCode(batchCode));
        if (batch == null) {
            throw exception(REVIEW_BATCH_NOT_EXISTS);
        }
        return reviewItemMapper.selectListByBatchIdAndType(batch.getId(), itemType);
    }

    private String normalizeBatchCode(String batchCode) {
        if (batchCode == null || batchCode.isBlank()) {
            return DEFAULT_BATCH_CODE;
        }
        return batchCode;
    }

    private void validateReviewBatchExists(Long id) {
        if (id == null || reviewBatchMapper.selectById(id) == null) {
            throw exception(REVIEW_BATCH_NOT_EXISTS);
        }
    }

    private void validateReviewBatchCodeUnique(Long id, String batchCode) {
        LabReviewBatchDO batch = reviewBatchMapper.selectByBatchCode(batchCode);
        if (batch == null) {
            return;
        }
        if (id == null || !batch.getId().equals(id)) {
            throw exception(REVIEW_BATCH_CODE_DUPLICATE);
        }
    }

    private void validateReviewItemExists(Long id) {
        if (id == null || reviewItemMapper.selectById(id) == null) {
            throw exception(REVIEW_ITEM_NOT_EXISTS);
        }
    }

}
