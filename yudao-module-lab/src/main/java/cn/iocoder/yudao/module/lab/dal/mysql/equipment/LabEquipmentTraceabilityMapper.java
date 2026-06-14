package cn.iocoder.yudao.module.lab.dal.mysql.equipment;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lab.controller.admin.quality.vo.LabQualityRecordPageReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.equipment.LabEquipmentTraceabilityDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LabEquipmentTraceabilityMapper extends BaseMapperX<LabEquipmentTraceabilityDO> {

    default PageResult<LabEquipmentTraceabilityDO> selectPage(LabQualityRecordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LabEquipmentTraceabilityDO>()
                .likeIfPresent(LabEquipmentTraceabilityDO::getCertificateNo, reqVO.getRecordNo())
                .likeIfPresent(LabEquipmentTraceabilityDO::getCalibrationOrg, reqVO.getRecordName())
                .eqIfPresent(LabEquipmentTraceabilityDO::getStatus, reqVO.getStatus())
                .orderByDesc(LabEquipmentTraceabilityDO::getId));
    }

    default List<LabEquipmentTraceabilityDO> selectByEquipmentId(Long equipmentId) {
        return selectList(new LambdaQueryWrapperX<LabEquipmentTraceabilityDO>()
                .eq(LabEquipmentTraceabilityDO::getEquipmentId, equipmentId)
                .orderByDesc(LabEquipmentTraceabilityDO::getValidTo)
                .orderByDesc(LabEquipmentTraceabilityDO::getId));
    }

}
