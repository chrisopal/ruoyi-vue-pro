package cn.iocoder.yudao.module.lab.dal.mysql.standard;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lab.controller.admin.standard.vo.LabStandardClausePageReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.standard.LabStandardClauseDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LabStandardClauseMapper extends BaseMapperX<LabStandardClauseDO> {

    default LabStandardClauseDO selectByStandardIdAndClauseCode(Long standardId, String clauseCode) {
        return selectOne(new LambdaQueryWrapperX<LabStandardClauseDO>()
                .eq(LabStandardClauseDO::getStandardId, standardId)
                .eq(LabStandardClauseDO::getClauseCode, clauseCode));
    }

    default PageResult<LabStandardClauseDO> selectPage(LabStandardClausePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LabStandardClauseDO>()
                .eqIfPresent(LabStandardClauseDO::getStandardId, reqVO.getStandardId())
                .likeIfPresent(LabStandardClauseDO::getClauseCode, reqVO.getClauseCode())
                .likeIfPresent(LabStandardClauseDO::getClauseTitle, reqVO.getClauseTitle())
                .eqIfPresent(LabStandardClauseDO::getClauseCategory, reqVO.getClauseCategory())
                .eqIfPresent(LabStandardClauseDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(LabStandardClauseDO::getCreateTime, reqVO.getCreateTime())
                .orderByAsc(LabStandardClauseDO::getStandardId)
                .orderByAsc(LabStandardClauseDO::getClauseCode));
    }

    default List<LabStandardClauseDO> selectListByStandardId(Long standardId) {
        return selectList(new LambdaQueryWrapperX<LabStandardClauseDO>()
                .eq(LabStandardClauseDO::getStandardId, standardId)
                .orderByAsc(LabStandardClauseDO::getClauseCode));
    }

    default LabStandardClauseDO selectFirstEquipmentClause() {
        return selectOne(new LambdaQueryWrapperX<LabStandardClauseDO>()
                .eq(LabStandardClauseDO::getClauseCategory, "equipment")
                .orderByAsc(LabStandardClauseDO::getClauseCode)
                .last("LIMIT 1"));
    }

}
