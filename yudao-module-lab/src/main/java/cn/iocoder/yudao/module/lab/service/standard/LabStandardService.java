package cn.iocoder.yudao.module.lab.service.standard;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.lab.controller.admin.standard.vo.LabStandardClausePageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.standard.vo.LabStandardClauseSaveReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.standard.vo.LabStandardPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.standard.vo.LabStandardSaveReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.standard.LabStandardClauseDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.standard.LabStandardDO;

import java.util.List;

public interface LabStandardService {

    Long createStandard(LabStandardSaveReqVO createReqVO);

    void updateStandard(LabStandardSaveReqVO updateReqVO);

    void updateStandardStatus(Long id, String status);

    void deleteStandard(Long id);

    LabStandardDO getStandard(Long id);

    PageResult<LabStandardDO> getStandardPage(LabStandardPageReqVO pageReqVO);

    Long createStandardClause(LabStandardClauseSaveReqVO createReqVO);

    void updateStandardClause(LabStandardClauseSaveReqVO updateReqVO);

    void deleteStandardClause(Long id);

    LabStandardClauseDO getStandardClause(Long id);

    PageResult<LabStandardClauseDO> getStandardClausePage(LabStandardClausePageReqVO pageReqVO);

    List<LabStandardClauseDO> getStandardClauseListByStandardId(Long standardId);

    Long getFirstClauseIdByCategory(String clauseCategory);

}
