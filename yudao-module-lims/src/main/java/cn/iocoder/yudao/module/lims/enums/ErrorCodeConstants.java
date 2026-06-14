package cn.iocoder.yudao.module.lims.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * LIMS 错误码枚举类。
 *
 * lims 系统，使用 1-051-000-000 段。
 */
public interface ErrorCodeConstants {

    ErrorCode TEST_REQUEST_NOT_EXISTS = new ErrorCode(1_051_001_000, "检测需求不存在");
    ErrorCode TEST_REQUEST_NO_DUPLICATE = new ErrorCode(1_051_001_001, "检测需求编号已存在");
    ErrorCode TEST_REQUEST_NO_REQUIRED = new ErrorCode(1_051_001_007, "检测需求编号不能为空");
    ErrorCode SAMPLE_NOT_EXISTS = new ErrorCode(1_051_001_002, "样品不存在");
    ErrorCode TEST_TASK_NOT_EXISTS = new ErrorCode(1_051_001_003, "检测任务不存在");
    ErrorCode TEST_RESULT_NOT_EXISTS = new ErrorCode(1_051_001_004, "检测结果不存在");
    ErrorCode TEST_REPORT_NOT_EXISTS = new ErrorCode(1_051_001_005, "检测报告不存在");
    ErrorCode DOMAIN_PACK_REQUIRED = new ErrorCode(1_051_001_006, "检测场景方案包不能为空");
    ErrorCode DOMAIN_PACK_NOT_PUBLISHED = new ErrorCode(1_051_001_008, "检测场景方案包未发布，不能创建检测需求");
    ErrorCode WORKFLOW_SNAPSHOT_FROZEN = new ErrorCode(1_051_001_009, "检测需求已冻结方向包快照，不能更换检测方案包");
    ErrorCode TEST_TASK_EQUIPMENT_UNAVAILABLE = new ErrorCode(1_051_001_010, "检测任务设备不可用或校准已过期");

}
