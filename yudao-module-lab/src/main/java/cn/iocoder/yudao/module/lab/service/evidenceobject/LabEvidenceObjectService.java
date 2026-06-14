package cn.iocoder.yudao.module.lab.service.evidenceobject;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.lab.controller.admin.evidenceobject.vo.LabEvidenceObjectPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.evidenceobject.vo.LabEvidenceObjectSaveReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.evidenceobject.LabEvidenceObjectDO;

public interface LabEvidenceObjectService {

    Long createEvidenceObject(LabEvidenceObjectSaveReqVO createReqVO);

    void updateEvidenceObject(LabEvidenceObjectSaveReqVO updateReqVO);

    void deleteEvidenceObject(Long id);

    LabEvidenceObjectDO getEvidenceObject(Long id);

    PageResult<LabEvidenceObjectDO> getEvidenceObjectPage(LabEvidenceObjectPageReqVO pageReqVO);

    LabEvidenceObjectDO getEvidenceObjectOrThrow(Long id);

    Long createEvidenceObject(LabEvidenceObjectDO evidenceObject);

}
