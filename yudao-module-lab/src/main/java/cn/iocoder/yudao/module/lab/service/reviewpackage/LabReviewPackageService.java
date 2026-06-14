package cn.iocoder.yudao.module.lab.service.reviewpackage;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
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

import java.util.List;

public interface LabReviewPackageService {

    Long createReviewBatch(LabReviewBatchSaveReqVO createReqVO);

    void updateReviewBatch(LabReviewBatchSaveReqVO updateReqVO);

    void deleteReviewBatch(Long id);

    LabReviewBatchDO getReviewBatch(Long id);

    PageResult<LabReviewBatchDO> getReviewBatchPage(LabReviewBatchPageReqVO pageReqVO);

    Long createReviewItem(LabReviewItemSaveReqVO createReqVO);

    void updateReviewItem(LabReviewItemSaveReqVO updateReqVO);

    void deleteReviewItem(Long id);

    LabReviewItemDO getReviewItem(Long id);

    PageResult<LabReviewItemDO> getReviewItemPage(LabReviewItemPageReqVO pageReqVO);

    List<LabReviewChecklistExcelVO> getChecklistRows(String batchCode);

    List<LabReviewEvidenceExcelVO> getEvidenceRows(String batchCode);

    List<LabReviewNonconformityExcelVO> getNonconformityRows(String batchCode);

    List<LabReviewCapaExcelVO> getCapaRows(String batchCode);

}
