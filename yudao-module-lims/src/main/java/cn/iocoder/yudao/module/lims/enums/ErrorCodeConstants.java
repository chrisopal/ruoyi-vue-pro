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
    ErrorCode TEST_TASK_PERSONNEL_UNAUTHORIZED = new ErrorCode(1_051_001_020, "检测任务执行人未获得当前项目授权或能力已过期");
    ErrorCode TEST_TASK_INVALID_STATUS_TRANSITION = new ErrorCode(1_051_001_011, "检测任务状态流转不允许");
    ErrorCode TEST_TASK_SCHEDULE_CONFLICT = new ErrorCode(1_051_001_012, "检测任务排程冲突");
    ErrorCode TEST_TASK_READINESS_FAILED = new ErrorCode(1_051_001_013, "检测任务就绪检查未通过");
    ErrorCode TEST_TASK_RAW_RECORD_REQUIRED = new ErrorCode(1_051_001_014, "检测任务原始记录不能为空");
    ErrorCode TEST_TASK_REVIEW_REQUIRED = new ErrorCode(1_051_001_015, "检测任务需要技术复核通过后才能进入报告");
    ErrorCode TEST_TASK_REPORT_BLOCKED = new ErrorCode(1_051_001_016, "存在未批准的检测任务，不能生成报告");
    ErrorCode TEST_RESULT_FIELD_INVALID = new ErrorCode(1_051_001_017, "检测结果字段不符合方向包配置");
    ErrorCode TEST_QC_RULE_UNSATISFIED = new ErrorCode(1_051_001_018, "检测任务质控规则未满足，不能生成报告");
    ErrorCode TEST_EVIDENCE_INCOMPLETE = new ErrorCode(1_051_001_019, "检测任务证据链不完整，不能生成报告");

}
