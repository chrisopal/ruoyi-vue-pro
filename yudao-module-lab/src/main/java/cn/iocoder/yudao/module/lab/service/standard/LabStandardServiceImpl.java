package cn.iocoder.yudao.module.lab.service.standard;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.lab.controller.admin.standard.vo.LabStandardClausePageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.standard.vo.LabStandardClauseSaveReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.standard.vo.LabStandardPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.standard.vo.LabStandardSaveReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.standard.LabStandardClauseDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.standard.LabStandardDO;
import cn.iocoder.yudao.module.lab.dal.mysql.standard.LabStandardClauseMapper;
import cn.iocoder.yudao.module.lab.dal.mysql.standard.LabStandardMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lab.enums.ErrorCodeConstants.*;

@Service
@Validated
public class LabStandardServiceImpl implements LabStandardService {

    @Resource
    private LabStandardMapper standardMapper;
    @Resource
    private LabStandardClauseMapper standardClauseMapper;

    @Override
    public Long createStandard(LabStandardSaveReqVO createReqVO) {
        validateStandardCodeUnique(null, createReqVO.getStandardCode());
        LabStandardDO standard = BeanUtils.toBean(createReqVO, LabStandardDO.class);
        standardMapper.insert(standard);
        return standard.getId();
    }

    @Override
    public void updateStandard(LabStandardSaveReqVO updateReqVO) {
        validateStandardExists(updateReqVO.getId());
        validateStandardCodeUnique(updateReqVO.getId(), updateReqVO.getStandardCode());
        standardMapper.updateById(BeanUtils.toBean(updateReqVO, LabStandardDO.class));
    }

    @Override
    public void updateStandardStatus(Long id, String status) {
        validateStandardExists(id);
        LabStandardDO standard = new LabStandardDO();
        standard.setId(id);
        standard.setStatus(status);
        standardMapper.updateById(standard);
    }

    @Override
    public void deleteStandard(Long id) {
        validateStandardExists(id);
        standardMapper.deleteById(id);
    }

    @Override
    public LabStandardDO getStandard(Long id) {
        return standardMapper.selectById(id);
    }

    @Override
    public PageResult<LabStandardDO> getStandardPage(LabStandardPageReqVO pageReqVO) {
        return standardMapper.selectPage(pageReqVO);
    }

    @Override
    public Long createStandardClause(LabStandardClauseSaveReqVO createReqVO) {
        validateStandardExists(createReqVO.getStandardId());
        validateStandardClauseCodeUnique(null, createReqVO.getStandardId(), createReqVO.getClauseCode());
        LabStandardClauseDO clause = BeanUtils.toBean(createReqVO, LabStandardClauseDO.class);
        standardClauseMapper.insert(clause);
        return clause.getId();
    }

    @Override
    public void updateStandardClause(LabStandardClauseSaveReqVO updateReqVO) {
        validateStandardClauseExists(updateReqVO.getId());
        validateStandardExists(updateReqVO.getStandardId());
        validateStandardClauseCodeUnique(updateReqVO.getId(), updateReqVO.getStandardId(), updateReqVO.getClauseCode());
        standardClauseMapper.updateById(BeanUtils.toBean(updateReqVO, LabStandardClauseDO.class));
    }

    @Override
    public void deleteStandardClause(Long id) {
        validateStandardClauseExists(id);
        standardClauseMapper.deleteById(id);
    }

    @Override
    public LabStandardClauseDO getStandardClause(Long id) {
        return standardClauseMapper.selectById(id);
    }

    @Override
    public PageResult<LabStandardClauseDO> getStandardClausePage(LabStandardClausePageReqVO pageReqVO) {
        return standardClauseMapper.selectPage(pageReqVO);
    }

    @Override
    public List<LabStandardClauseDO> getStandardClauseListByStandardId(Long standardId) {
        validateStandardExists(standardId);
        return standardClauseMapper.selectListByStandardId(standardId);
    }

    private void validateStandardExists(Long id) {
        if (id == null || standardMapper.selectById(id) == null) {
            throw exception(STANDARD_NOT_EXISTS);
        }
    }

    private void validateStandardCodeUnique(Long id, String standardCode) {
        LabStandardDO standard = standardMapper.selectByStandardCode(standardCode);
        if (standard == null) {
            return;
        }
        if (id == null || !standard.getId().equals(id)) {
            throw exception(STANDARD_CODE_DUPLICATE);
        }
    }

    private void validateStandardClauseExists(Long id) {
        if (id == null || standardClauseMapper.selectById(id) == null) {
            throw exception(STANDARD_CLAUSE_NOT_EXISTS);
        }
    }

    private void validateStandardClauseCodeUnique(Long id, Long standardId, String clauseCode) {
        LabStandardClauseDO clause = standardClauseMapper.selectByStandardIdAndClauseCode(standardId, clauseCode);
        if (clause == null) {
            return;
        }
        if (id == null || !clause.getId().equals(id)) {
            throw exception(STANDARD_CLAUSE_CODE_DUPLICATE);
        }
    }

}
