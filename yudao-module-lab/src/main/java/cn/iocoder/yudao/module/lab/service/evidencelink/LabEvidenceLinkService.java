package cn.iocoder.yudao.module.lab.service.evidencelink;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.lab.controller.admin.evidencelink.vo.LabEvidenceLinkPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.evidencelink.vo.LabEvidenceLinkSaveReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.evidencelink.LabEvidenceLinkDO;
import jakarta.validation.Valid;

import java.util.List;

public interface LabEvidenceLinkService {

    Long createEvidenceLink(@Valid LabEvidenceLinkSaveReqVO createReqVO);

    void updateEvidenceLink(@Valid LabEvidenceLinkSaveReqVO updateReqVO);

    void deleteEvidenceLink(Long id);

    LabEvidenceLinkDO getEvidenceLink(Long id);

    PageResult<LabEvidenceLinkDO> getEvidenceLinkPage(LabEvidenceLinkPageReqVO pageReqVO);

    List<LabEvidenceLinkDO> getEvidenceLinksBySource(String sourceType, Long sourceId);

    Long uploadEvidenceLink(@Valid LabEvidenceLinkSaveReqVO uploadReqVO);

}
