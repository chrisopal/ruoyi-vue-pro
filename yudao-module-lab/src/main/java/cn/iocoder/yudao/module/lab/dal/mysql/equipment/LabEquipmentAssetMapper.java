package cn.iocoder.yudao.module.lab.dal.mysql.equipment;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lab.controller.admin.equipmentasset.vo.LabEquipmentAssetPageReqVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.equipment.LabEquipmentAssetDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LabEquipmentAssetMapper extends BaseMapperX<LabEquipmentAssetDO> {

    default PageResult<LabEquipmentAssetDO> selectPage(LabEquipmentAssetPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LabEquipmentAssetDO>()
                .likeIfPresent(LabEquipmentAssetDO::getEquipmentCode, reqVO.getEquipmentCode())
                .likeIfPresent(LabEquipmentAssetDO::getEquipmentName, reqVO.getEquipmentName())
                .eqIfPresent(LabEquipmentAssetDO::getEquipmentType, reqVO.getEquipmentType())
                .eqIfPresent(LabEquipmentAssetDO::getDomainCode, reqVO.getDomainCode())
                .eqIfPresent(LabEquipmentAssetDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(LabEquipmentAssetDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(LabEquipmentAssetDO::getId));
    }

    default LabEquipmentAssetDO selectByEquipmentCode(String equipmentCode) {
        return selectOne(LabEquipmentAssetDO::getEquipmentCode, equipmentCode);
    }

    default List<LabEquipmentAssetDO> selectEnabledCandidates() {
        return selectList(new LambdaQueryWrapperX<LabEquipmentAssetDO>()
                .eq(LabEquipmentAssetDO::getStatus, "enabled")
                .orderByAsc(LabEquipmentAssetDO::getEquipmentCode));
    }

}
