package cn.iocoder.yudao.module.lab.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * Lab 错误码枚举类。
 *
 * lab 系统，使用 1-050-000-000 段。
 */
public interface ErrorCodeConstants {

    // ========== 检测方向包 1-050-001-000 ==========
    ErrorCode DOMAIN_PROFILE_NOT_EXISTS = new ErrorCode(1_050_001_000, "检测方向不存在");
    ErrorCode DOMAIN_PROFILE_CODE_DUPLICATE = new ErrorCode(1_050_001_001, "检测方向编码已存在");
    ErrorCode DOMAIN_PACK_NOT_EXISTS = new ErrorCode(1_050_001_002, "检测方案包不存在");
    ErrorCode DOMAIN_PACK_CODE_DUPLICATE = new ErrorCode(1_050_001_003, "检测方案包编码已存在");
    ErrorCode EVIDENCE_LINK_NOT_EXISTS = new ErrorCode(1_050_001_004, "证据关联不存在");
    ErrorCode TEMPLATE_VERSION_NOT_EXISTS = new ErrorCode(1_050_001_005, "模板版本不存在");
    ErrorCode TEMPLATE_FIELD_BINDING_NOT_EXISTS = new ErrorCode(1_050_001_006, "模板字段绑定不存在");
    ErrorCode REVIEW_BATCH_NOT_EXISTS = new ErrorCode(1_050_001_007, "评审批次不存在");
    ErrorCode STANDARD_NOT_EXISTS = new ErrorCode(1_050_001_008, "实验室标准不存在");
    ErrorCode STANDARD_CODE_DUPLICATE = new ErrorCode(1_050_001_009, "实验室标准编码已存在");
    ErrorCode STANDARD_CLAUSE_NOT_EXISTS = new ErrorCode(1_050_001_010, "实验室标准条款不存在");
    ErrorCode STANDARD_CLAUSE_CODE_DUPLICATE = new ErrorCode(1_050_001_011, "同一标准下条款编码已存在");
    ErrorCode REVIEW_BATCH_CODE_DUPLICATE = new ErrorCode(1_050_001_012, "评审批次编码已存在");
    ErrorCode REVIEW_ITEM_NOT_EXISTS = new ErrorCode(1_050_001_013, "评审材料条目不存在");
    ErrorCode DOMAIN_PACK_PUBLISHED_IMMUTABLE = new ErrorCode(1_050_001_014, "检测方案包已发布或归档，不能原地修改");
    ErrorCode DOMAIN_PACK_VERSION_DUPLICATE = new ErrorCode(1_050_001_015, "检测方案包编码和版本已存在");
    ErrorCode DOMAIN_PACK_STATUS_INVALID = new ErrorCode(1_050_001_016, "检测方案包状态不允许当前操作");
    ErrorCode EQUIPMENT_ASSET_NOT_EXISTS = new ErrorCode(1_050_001_017, "设备主档不存在");
    ErrorCode EQUIPMENT_ASSET_CODE_DUPLICATE = new ErrorCode(1_050_001_018, "设备编码已存在");
    ErrorCode EQUIPMENT_TRACEABILITY_NOT_EXISTS = new ErrorCode(1_050_001_019, "设备溯源证据不存在");

}
