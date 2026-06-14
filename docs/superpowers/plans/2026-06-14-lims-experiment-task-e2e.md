# LIMS Experiment Task End-to-End Management Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a Ruoyi-only LIMS experiment task module that manages task lifecycle, simple scheduling, raw records, QC, review, and report eligibility.

**Architecture:** Keep `LimsWorkflowService` as the existing closed-loop facade, but extract task-specific behavior into focused services under `yudao-module-lims/service/workflow`. Persist scheduling, raw records, QC, reviews, and event logs in LIMS tables, and gate report generation on approved task results.

**Tech Stack:** Java 17, Spring Boot, MyBatis Plus, Ruoyi tenant/audit base classes, Vue 3, Element Plus, TypeScript, existing `request` axios wrapper, Maven, pnpm.

---

## Current Constraints

- Work only inside `ruoyi-vue-pro`.
- Do not use or edit `iimake-qms-lims`.
- Do not add dependencies.
- Do not import LAB mappers inside `yudao-module-lims`; use gateway/service boundaries.
- `sql/mysql/lab.sql` is bootstrap-oriented. Live environments need separate incremental migration later.
- Current worktree contains unrelated LAB/report governance changes. Implementation should either start after those are committed or deliberately include them in the branch.

## File Structure

### Backend Files

- Modify: `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/enums/ErrorCodeConstants.java`
  - Add task lifecycle, scheduling, readiness, review, and report gate error codes.

- Modify: `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/dal/dataobject/workflow/LimsTestTaskDO.java`
  - Add lifecycle, scheduling, readiness, QC, review, and report eligibility fields.

- Create: `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/dal/dataobject/workflow/LimsTaskScheduleDO.java`
  - Stores user-visible schedule rows and conflict state.

- Create: `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/dal/dataobject/workflow/LimsTaskRawRecordDO.java`
  - Stores raw records, calculations, observations, and attachments.

- Create: `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/dal/dataobject/workflow/LimsTaskQcRecordDO.java`
  - Stores task-level QC records.

- Create: `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/dal/dataobject/workflow/LimsTaskReviewDO.java`
  - Stores technical review decisions.

- Create: `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/dal/dataobject/workflow/LimsTaskEventLogDO.java`
  - Stores immutable lifecycle event trail.

- Create mapper files under `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/dal/mysql/workflow/`
  - `LimsTaskScheduleMapper.java`
  - `LimsTaskRawRecordMapper.java`
  - `LimsTaskQcRecordMapper.java`
  - `LimsTaskReviewMapper.java`
  - `LimsTaskEventLogMapper.java`

- Create service files under `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/`
  - `LimsTaskStatus.java`
  - `LimsTaskScheduleStatus.java`
  - `LimsTaskReviewStatus.java`
  - `LimsTaskEventType.java`
  - `LimsTaskLifecycleService.java`
  - `LimsTaskScheduleService.java`
  - `LimsTaskRecordService.java`
  - `LimsReportEligibilityService.java`

- Modify: `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/LimsWorkflowService.java`
  - Delegate task status actions, report gate, task creation defaults, and report snapshot additions.

- Modify: `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/controller/admin/workflow/LimsWorkflowController.java`
  - Add lifecycle, schedule, record, review, and event endpoints.

- Modify: `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/controller/admin/workflow/vo/LimsWorkflowSaveReqVO.java`
  - Add command fields for task schedule, records, QC, reviews, and hold/reject reasons.

- Modify: `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/controller/admin/workflow/vo/LimsWorkflowRespVO.java`
  - Add response fields and nested JSON fields used by the task UI.

- Modify: `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/controller/admin/workflow/vo/LimsWorkflowPageReqVO.java`
  - Add task status, schedule status, assigned user, equipment, planned time range filters.

- Modify: `yudao-module-lims/src/test/java/cn/iocoder/yudao/module/lims/service/workflow/LimsWorkflowServiceTest.java`
  - Adjust existing task/report assertions to new status and report gate behavior.

- Create tests:
  - `yudao-module-lims/src/test/java/cn/iocoder/yudao/module/lims/service/workflow/LimsTaskLifecycleServiceTest.java`
  - `yudao-module-lims/src/test/java/cn/iocoder/yudao/module/lims/service/workflow/LimsTaskScheduleServiceTest.java`
  - `yudao-module-lims/src/test/java/cn/iocoder/yudao/module/lims/service/workflow/LimsTaskRecordServiceTest.java`
  - `yudao-module-lims/src/test/java/cn/iocoder/yudao/module/lims/service/workflow/LimsReportEligibilityServiceTest.java`

### SQL Files

- Modify: `sql/mysql/lab.sql`
  - Extend `lims_test_task`.
  - Add `lims_task_schedule`, `lims_task_raw_record`, `lims_task_qc_record`, `lims_task_review`, `lims_task_event_log`.
  - Add task permissions and indexes.

### Frontend Files

- Modify: `yudao-ui/yudao-ui-admin-vue3/src/api/lims/workflow/index.ts`
  - Add task lifecycle/schedule/record/review/event API methods and fields.

- Replace or expand: `yudao-ui/yudao-ui-admin-vue3/src/views/lims/task/index.vue`
  - Dedicated task workspace, replacing generic `LimsWorkflowPage` usage for tasks.

- Create:
  - `yudao-ui/yudao-ui-admin-vue3/src/views/lims/task/TaskSchedulePanel.vue`
  - `yudao-ui/yudao-ui-admin-vue3/src/views/lims/task/TaskReadinessDrawer.vue`
  - `yudao-ui/yudao-ui-admin-vue3/src/views/lims/task/TaskRecordDrawer.vue`
  - `yudao-ui/yudao-ui-admin-vue3/src/views/lims/task/TaskReviewDrawer.vue`
  - `yudao-ui/yudao-ui-admin-vue3/src/views/lims/task/taskStatus.ts`

### Docs

- Modify: `STATUS.md` at workspace root after each major milestone.

---

## Task 1: Add Task Lifecycle Constants And Persistence Fields

**Files:**
- Modify: `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/dal/dataobject/workflow/LimsTestTaskDO.java`
- Modify: `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/enums/ErrorCodeConstants.java`
- Create: `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/LimsTaskStatus.java`
- Create: `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/LimsTaskScheduleStatus.java`
- Create: `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/LimsTaskReviewStatus.java`
- Create: `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/LimsTaskEventType.java`
- Modify: `sql/mysql/lab.sql`
- Test: `yudao-module-lims/src/test/java/cn/iocoder/yudao/module/lims/service/workflow/LimsWorkflowServiceTest.java`

- [ ] **Step 1: Write failing assertions for generated task lifecycle defaults**

Add a test to `LimsWorkflowServiceTest`:

```java
@Test
void generateTasks_shouldInitializeLifecycleSchedulingAndReportGateFields() {
    LimsTestRequestDO request = requestWithWorkflowSnapshot(snapshotWithAllSections());
    when(requestMapper.selectById(1L)).thenReturn(request);
    when(sampleMapper.selectListByRequestId(1L)).thenReturn(List.of(sample()));

    workflowService.generateTasks(1L);

    verify(taskMapper).insert(argThat((LimsTestTaskDO task) ->
            "generated".equals(task.getTaskStatus())
                    && "unscheduled".equals(task.getScheduleStatus())
                    && "none".equals(task.getQcStatus())
                    && "none".equals(task.getReviewStatus())
                    && Boolean.FALSE.equals(task.getReportEligible())
                    && task.getDurationMinutes() != null
                    && task.getDurationMinutes() >= 1));
}
```

- [ ] **Step 2: Run test and verify it fails**

Run:

```bash
mvn -q -pl yudao-module-lims -am -Dtest=LimsWorkflowServiceTest#generateTasks_shouldInitializeLifecycleSchedulingAndReportGateFields -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: compile fails because new fields do not exist on `LimsTestTaskDO`.

- [ ] **Step 3: Add enum-like constants**

Create `LimsTaskStatus.java`:

```java
package cn.iocoder.yudao.module.lims.service.workflow;

import java.util.Set;

public final class LimsTaskStatus {

    public static final String GENERATED = "generated";
    public static final String SCHEDULED = "scheduled";
    public static final String ASSIGNED = "assigned";
    public static final String READY = "ready";
    public static final String TESTING = "testing";
    public static final String DATA_SUBMITTED = "data_submitted";
    public static final String REVIEWING = "reviewing";
    public static final String APPROVED = "approved";
    public static final String COMPLETED = "completed";
    public static final String REPORTED = "reported";
    public static final String HOLD = "hold";
    public static final String REWORK = "rework";
    public static final String CANCELLED = "cancelled";

    public static final Set<String> REPORT_ALLOWED = Set.of(APPROVED, COMPLETED, REPORTED);

    private LimsTaskStatus() {
    }
}
```

Create `LimsTaskScheduleStatus.java`:

```java
package cn.iocoder.yudao.module.lims.service.workflow;

public final class LimsTaskScheduleStatus {

    public static final String UNSCHEDULED = "unscheduled";
    public static final String SCHEDULED = "scheduled";
    public static final String CONFLICT = "conflict";
    public static final String LOCKED = "locked";

    private LimsTaskScheduleStatus() {
    }
}
```

Create `LimsTaskReviewStatus.java`:

```java
package cn.iocoder.yudao.module.lims.service.workflow;

public final class LimsTaskReviewStatus {

    public static final String NONE = "none";
    public static final String PENDING = "pending";
    public static final String APPROVED = "approved";
    public static final String REJECTED = "rejected";

    private LimsTaskReviewStatus() {
    }
}
```

Create `LimsTaskEventType.java`:

```java
package cn.iocoder.yudao.module.lims.service.workflow;

public final class LimsTaskEventType {

    public static final String CREATED = "created";
    public static final String SCHEDULED = "scheduled";
    public static final String ASSIGNED = "assigned";
    public static final String READINESS_PASSED = "readiness_passed";
    public static final String STARTED = "started";
    public static final String RECORD_SUBMITTED = "record_submitted";
    public static final String REVIEW_SUBMITTED = "review_submitted";
    public static final String APPROVED = "approved";
    public static final String REJECTED = "rejected";
    public static final String HOLD = "hold";
    public static final String RESUMED = "resumed";
    public static final String COMPLETED = "completed";
    public static final String REPORTED = "reported";

    private LimsTaskEventType() {
    }
}
```

- [ ] **Step 4: Extend `LimsTestTaskDO`**

Add fields after `assignedUserId` and existing schedule fields:

```java
    private Long reviewerId;
    private Long durationMinutes;
    private String taskStatus;
    private String scheduleStatus;
    private String actualStartTime;
    private String actualEndTime;
    private String methodSnapshot;
    private String readinessSnapshot;
    private String qcStatus;
    private String reviewStatus;
    private Boolean reportEligible;
    private String blockReason;
```

Keep existing `status` field for backward compatibility during the first migration. During implementation, write both `status` and `taskStatus` until frontend and existing APIs are fully switched.

- [ ] **Step 5: Add SQL columns**

In `sql/mysql/lab.sql`, extend `lims_test_task` with:

```sql
  `reviewer_id` bigint NULL DEFAULT NULL COMMENT '技术复核人',
  `duration_minutes` bigint NULL DEFAULT NULL COMMENT '预计耗时分钟',
  `task_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'generated' COMMENT '任务生命周期状态',
  `schedule_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'unscheduled' COMMENT '排程状态',
  `actual_start_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '实际开始时间',
  `actual_end_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '实际结束时间',
  `method_snapshot` json NULL COMMENT '方法配置快照',
  `readiness_snapshot` json NULL COMMENT '任务就绪检查快照',
  `qc_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'none' COMMENT '质控状态',
  `review_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'none' COMMENT '复核状态',
  `report_eligible` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否可进入报告',
  `block_reason` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '阻断原因',
```

Add indexes:

```sql
  KEY `idx_lims_task_lifecycle` (`task_status`, `tenant_id`, `deleted`) USING BTREE,
  KEY `idx_lims_task_schedule_status` (`schedule_status`, `tenant_id`, `deleted`) USING BTREE,
  KEY `idx_lims_task_assignee_window` (`assigned_user_id`, `planned_start_time`, `planned_end_time`, `tenant_id`, `deleted`) USING BTREE,
```

- [ ] **Step 6: Add error codes**

Add to `ErrorCodeConstants.java`:

```java
    ErrorCode TEST_TASK_INVALID_STATUS_TRANSITION = new ErrorCode(1_049_001_001, "检测任务状态流转不允许");
    ErrorCode TEST_TASK_SCHEDULE_CONFLICT = new ErrorCode(1_049_001_002, "检测任务排程冲突");
    ErrorCode TEST_TASK_READINESS_FAILED = new ErrorCode(1_049_001_003, "检测任务就绪检查未通过");
    ErrorCode TEST_TASK_RAW_RECORD_REQUIRED = new ErrorCode(1_049_001_004, "检测任务原始记录不能为空");
    ErrorCode TEST_TASK_REVIEW_REQUIRED = new ErrorCode(1_049_001_005, "检测任务需要技术复核通过后才能进入报告");
    ErrorCode TEST_TASK_REPORT_BLOCKED = new ErrorCode(1_049_001_006, "存在未批准的检测任务，不能生成报告");
```

- [ ] **Step 7: Initialize defaults in `generateTasks`**

In `LimsWorkflowService.generateTasks`, when creating `LimsTestTaskDO`, replace status-only defaults with:

```java
                task.setStatus(LimsTaskStatus.GENERATED);
                task.setTaskStatus(LimsTaskStatus.GENERATED);
                task.setScheduleStatus(LimsTaskScheduleStatus.UNSCHEDULED);
                task.setDurationMinutes(item.durationMinutes());
                task.setQcStatus(LimsTaskReviewStatus.NONE);
                task.setReviewStatus(LimsTaskReviewStatus.NONE);
                task.setReportEligible(false);
                task.setMethodSnapshot(writeJson(item));
```

Extend the local record:

```java
    private record TestItemConfig(String itemName, String methodCode, String methodName, Long durationMinutes) {
    }
```

Update `getTestItemConfigs` to parse duration:

```java
                    item.path("methodName").asText("配置方法"),
                    Math.max(item.path("estimatedDurationMinutes").asLong(60L), 1L))));
```

Update fallback items to include `60L`, `90L`, or domain-appropriate values.

- [ ] **Step 8: Run focused test**

Run:

```bash
mvn -q -pl yudao-module-lims -am -Dtest=LimsWorkflowServiceTest#generateTasks_shouldInitializeLifecycleSchedulingAndReportGateFields -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: PASS.

- [ ] **Step 9: Run existing LIMS workflow tests**

Run:

```bash
mvn -q -pl yudao-module-lims -am -Dtest=LimsWorkflowServiceTest,WorkflowSnapshotFactoryTest -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: PASS.

---

## Task 2: Add Event Log And Lifecycle Service

**Files:**
- Create: `LimsTaskEventLogDO.java`
- Create: `LimsTaskEventLogMapper.java`
- Create: `LimsTaskLifecycleService.java`
- Modify: `LimsWorkflowController.java`
- Modify: `LimsWorkflowService.java`
- Modify: `LimsWorkflowSaveReqVO.java`
- Modify: `LimsWorkflowRespVO.java`
- Modify: `sql/mysql/lab.sql`
- Test: `LimsTaskLifecycleServiceTest.java`

- [ ] **Step 1: Write failing lifecycle service tests**

Create `LimsTaskLifecycleServiceTest.java`:

```java
package cn.iocoder.yudao.module.lims.service.workflow;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskEventLogDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestTaskDO;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTaskEventLogMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestTaskMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LimsTaskLifecycleServiceTest extends BaseMockitoUnitTest {

    @InjectMocks
    private LimsTaskLifecycleService service;

    @Mock
    private LimsTestTaskMapper taskMapper;
    @Mock
    private LimsTaskEventLogMapper eventLogMapper;

    @Test
    void start_shouldMoveReadyTaskToTestingAndWriteEvent() {
        LimsTestTaskDO task = task(10L, LimsTaskStatus.READY);
        when(taskMapper.selectById(10L)).thenReturn(task);

        service.start(10L);

        verify(taskMapper).updateById(argThat(updated ->
                LimsTaskStatus.TESTING.equals(updated.getTaskStatus())
                        && LimsTaskStatus.TESTING.equals(updated.getStatus())
                        && updated.getActualStartTime() != null));
        verify(eventLogMapper).insert(argThat((LimsTaskEventLogDO event) ->
                Long.valueOf(10L).equals(event.getTaskId())
                        && LimsTaskEventType.STARTED.equals(event.getEventType())
                        && LimsTaskStatus.READY.equals(event.getFromStatus())
                        && LimsTaskStatus.TESTING.equals(event.getToStatus())));
    }

    @Test
    void start_shouldRejectGeneratedTask() {
        when(taskMapper.selectById(10L)).thenReturn(task(10L, LimsTaskStatus.GENERATED));

        assertThrows(Exception.class, () -> service.start(10L));
    }

    private static LimsTestTaskDO task(Long id, String status) {
        LimsTestTaskDO task = new LimsTestTaskDO();
        task.setId(id);
        task.setTaskNo("REQ-001-T01");
        task.setTaskStatus(status);
        task.setStatus(status);
        return task;
    }
}
```

- [ ] **Step 2: Run test and verify it fails**

Run:

```bash
mvn -q -pl yudao-module-lims -am -Dtest=LimsTaskLifecycleServiceTest -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: compile fails because event log and lifecycle service do not exist.

- [ ] **Step 3: Create event log DO**

Create `LimsTaskEventLogDO.java`:

```java
package cn.iocoder.yudao.module.lims.dal.dataobject.workflow;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lims_task_event_log")
@KeySequence("lims_task_event_log_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LimsTaskEventLogDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long taskId;
    private String taskNo;
    private String eventType;
    private String fromStatus;
    private String toStatus;
    private Long operatorId;
    private String eventTime;
    private String reason;
    private String payloadJson;

}
```

- [ ] **Step 4: Create event log mapper**

Create `LimsTaskEventLogMapper.java`:

```java
package cn.iocoder.yudao.module.lims.dal.mysql.workflow;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskEventLogDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LimsTaskEventLogMapper extends BaseMapperX<LimsTaskEventLogDO> {

    default List<LimsTaskEventLogDO> selectListByTaskId(Long taskId) {
        return selectList(new LambdaQueryWrapperX<LimsTaskEventLogDO>()
                .eq(LimsTaskEventLogDO::getTaskId, taskId)
                .orderByAsc(LimsTaskEventLogDO::getId));
    }
}
```

- [ ] **Step 5: Add event log SQL**

Add to `lab.sql` after `lims_test_task` or near task tables:

```sql
CREATE TABLE IF NOT EXISTS `lims_task_event_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `task_id` bigint NOT NULL COMMENT '检测任务编号',
  `task_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务编号',
  `event_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '事件类型',
  `from_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '原状态',
  `to_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '目标状态',
  `operator_id` bigint NULL DEFAULT NULL COMMENT '操作人',
  `event_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '事件时间',
  `reason` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '原因',
  `payload_json` json NULL COMMENT '事件载荷',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lims_task_event_task` (`task_id`, `tenant_id`, `deleted`) USING BTREE,
  KEY `idx_lims_task_event_type` (`event_type`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'LIMS检测任务事件日志';
```

- [ ] **Step 6: Implement lifecycle service**

Create `LimsTaskLifecycleService.java`:

```java
package cn.iocoder.yudao.module.lims.service.workflow;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskEventLogDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestTaskDO;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTaskEventLogMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestTaskMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lims.enums.ErrorCodeConstants.TEST_TASK_INVALID_STATUS_TRANSITION;
import static cn.iocoder.yudao.module.lims.enums.ErrorCodeConstants.TEST_TASK_NOT_EXISTS;

@Service
public class LimsTaskLifecycleService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final Map<String, Set<String>> ALLOWED = Map.of(
            LimsTaskStatus.GENERATED, Set.of(LimsTaskStatus.SCHEDULED, LimsTaskStatus.ASSIGNED, LimsTaskStatus.CANCELLED, LimsTaskStatus.HOLD),
            LimsTaskStatus.SCHEDULED, Set.of(LimsTaskStatus.ASSIGNED, LimsTaskStatus.CANCELLED, LimsTaskStatus.HOLD),
            LimsTaskStatus.ASSIGNED, Set.of(LimsTaskStatus.READY, LimsTaskStatus.CANCELLED, LimsTaskStatus.HOLD),
            LimsTaskStatus.READY, Set.of(LimsTaskStatus.TESTING, LimsTaskStatus.HOLD),
            LimsTaskStatus.TESTING, Set.of(LimsTaskStatus.DATA_SUBMITTED, LimsTaskStatus.HOLD),
            LimsTaskStatus.DATA_SUBMITTED, Set.of(LimsTaskStatus.REVIEWING, LimsTaskStatus.HOLD),
            LimsTaskStatus.REVIEWING, Set.of(LimsTaskStatus.APPROVED, LimsTaskStatus.REWORK, LimsTaskStatus.HOLD),
            LimsTaskStatus.REWORK, Set.of(LimsTaskStatus.TESTING, LimsTaskStatus.HOLD),
            LimsTaskStatus.APPROVED, Set.of(LimsTaskStatus.COMPLETED, LimsTaskStatus.HOLD),
            LimsTaskStatus.COMPLETED, Set.of(LimsTaskStatus.REPORTED),
            LimsTaskStatus.HOLD, Set.of(LimsTaskStatus.GENERATED, LimsTaskStatus.SCHEDULED, LimsTaskStatus.ASSIGNED,
                    LimsTaskStatus.READY, LimsTaskStatus.TESTING, LimsTaskStatus.DATA_SUBMITTED, LimsTaskStatus.REVIEWING,
                    LimsTaskStatus.REWORK)
    );

    @Resource
    private LimsTestTaskMapper taskMapper;
    @Resource
    private LimsTaskEventLogMapper eventLogMapper;

    @Transactional(rollbackFor = Exception.class)
    public void start(Long taskId) {
        LimsTestTaskDO task = validateTask(taskId);
        transition(task, LimsTaskStatus.TESTING, LimsTaskEventType.STARTED, null, null);
        task.setActualStartTime(now());
        taskMapper.updateById(task);
    }

    @Transactional(rollbackFor = Exception.class)
    public void transition(Long taskId, String toStatus, String eventType, String reason, String payloadJson) {
        transition(validateTask(taskId), toStatus, eventType, reason, payloadJson);
    }

    public void writeEvent(Long taskId, String taskNo, String eventType, String fromStatus, String toStatus, String reason, String payloadJson) {
        LimsTaskEventLogDO event = new LimsTaskEventLogDO();
        event.setTaskId(taskId);
        event.setTaskNo(taskNo);
        event.setEventType(eventType);
        event.setFromStatus(fromStatus);
        event.setToStatus(toStatus);
        event.setEventTime(now());
        event.setReason(reason);
        event.setPayloadJson(payloadJson);
        eventLogMapper.insert(event);
    }

    private void transition(LimsTestTaskDO task, String toStatus, String eventType, String reason, String payloadJson) {
        String fromStatus = StringUtils.hasText(task.getTaskStatus()) ? task.getTaskStatus() : task.getStatus();
        if (!ALLOWED.getOrDefault(fromStatus, Set.of()).contains(toStatus)) {
            throw exception(TEST_TASK_INVALID_STATUS_TRANSITION);
        }
        task.setTaskStatus(toStatus);
        task.setStatus(toStatus);
        taskMapper.updateById(task);
        writeEvent(task.getId(), task.getTaskNo(), eventType, fromStatus, toStatus, reason, payloadJson);
    }

    private LimsTestTaskDO validateTask(Long taskId) {
        LimsTestTaskDO task = taskId == null ? null : taskMapper.selectById(taskId);
        if (task == null) {
            throw exception(TEST_TASK_NOT_EXISTS);
        }
        return task;
    }

    private static String now() {
        return LocalDateTime.now().format(TIME_FORMATTER);
    }
}
```

If `ErrorCodeConstants.TEST_TASK_NOT_EXISTS` has a different existing name, use the existing task-not-found constant instead of adding a duplicate.

- [ ] **Step 7: Wire start endpoint through lifecycle service**

Modify `LimsWorkflowService.updateTaskStatus` or `LimsWorkflowController.startTask` so `/lims/task/start` calls:

```java
workflowService.startTask(id);
```

Add to `LimsWorkflowService`:

```java
    @Resource
    private LimsTaskLifecycleService taskLifecycleService;

    public void startTask(Long id) {
        taskLifecycleService.start(id);
    }
```

- [ ] **Step 8: Run lifecycle tests**

Run:

```bash
mvn -q -pl yudao-module-lims -am -Dtest=LimsTaskLifecycleServiceTest -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: PASS.

---

## Task 3: Add Report Eligibility Gate

**Files:**
- Create: `LimsReportEligibilityService.java`
- Modify: `LimsWorkflowService.java`
- Test: `LimsReportEligibilityServiceTest.java`
- Test: `LimsWorkflowServiceTest.java`

- [ ] **Step 1: Write failing eligibility tests**

Create `LimsReportEligibilityServiceTest.java`:

```java
package cn.iocoder.yudao.module.lims.service.workflow;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestTaskDO;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestTaskMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class LimsReportEligibilityServiceTest extends BaseMockitoUnitTest {

    @InjectMocks
    private LimsReportEligibilityService service;

    @Mock
    private LimsTestTaskMapper taskMapper;

    @Test
    void assertRequestReportable_shouldPassWhenAllTasksApprovedOrCompleted() {
        when(taskMapper.selectListByRequestId(1L)).thenReturn(List.of(
                task(LimsTaskStatus.APPROVED, true),
                task(LimsTaskStatus.COMPLETED, true)));

        assertDoesNotThrow(() -> service.assertRequestReportable(1L));
    }

    @Test
    void assertRequestReportable_shouldRejectTestingTask() {
        when(taskMapper.selectListByRequestId(1L)).thenReturn(List.of(task(LimsTaskStatus.TESTING, false)));

        assertThrows(Exception.class, () -> service.assertRequestReportable(1L));
    }

    private static LimsTestTaskDO task(String status, boolean reportEligible) {
        LimsTestTaskDO task = new LimsTestTaskDO();
        task.setTaskStatus(status);
        task.setReportEligible(reportEligible);
        return task;
    }
}
```

- [ ] **Step 2: Implement eligibility service**

Create `LimsReportEligibilityService.java`:

```java
package cn.iocoder.yudao.module.lims.service.workflow;

import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestTaskDO;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestTaskMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lims.enums.ErrorCodeConstants.TEST_TASK_REPORT_BLOCKED;

@Service
public class LimsReportEligibilityService {

    @Resource
    private LimsTestTaskMapper taskMapper;

    public void assertRequestReportable(Long requestId) {
        List<LimsTestTaskDO> tasks = taskMapper.selectListByRequestId(requestId);
        if (tasks.isEmpty()) {
            throw exception(TEST_TASK_REPORT_BLOCKED);
        }
        boolean allAllowed = tasks.stream().allMatch(task ->
                Boolean.TRUE.equals(task.getReportEligible())
                        && LimsTaskStatus.REPORT_ALLOWED.contains(task.getTaskStatus()));
        if (!allAllowed) {
            throw exception(TEST_TASK_REPORT_BLOCKED);
        }
    }
}
```

- [ ] **Step 3: Call gate before report creation**

In `LimsWorkflowService`, inject service:

```java
    @Resource
    private LimsReportEligibilityService reportEligibilityService;
```

At the start of `generateReport(Long requestId)` after validating request and before `selectByRequestId`:

```java
        reportEligibilityService.assertRequestReportable(requestId);
```

- [ ] **Step 4: Update existing report generation tests**

In `LimsWorkflowServiceTest.generateReport_shouldIncludeEquipmentEvidenceSnapshots`, set:

```java
        LimsTestTaskDO task = taskWithEquipmentEvidence();
        task.setTaskStatus(LimsTaskStatus.APPROVED);
        task.setReportEligible(true);
        when(taskMapper.selectListByRequestId(1L)).thenReturn(List.of(task));
```

If `selectListByRequestId` is used twice, Mockito can return the same list both times by default.

- [ ] **Step 5: Run tests**

Run:

```bash
mvn -q -pl yudao-module-lims -am -Dtest=LimsReportEligibilityServiceTest,LimsWorkflowServiceTest -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: PASS.

---

## Task 4: Add Simple Scheduling Persistence And Conflict Detection

**Files:**
- Create: `LimsTaskScheduleDO.java`
- Create: `LimsTaskScheduleMapper.java`
- Create: `LimsTaskScheduleService.java`
- Modify: `LimsWorkflowController.java`
- Modify: `LimsWorkflowSaveReqVO.java`
- Modify: `LimsWorkflowRespVO.java`
- Modify: `sql/mysql/lab.sql`
- Test: `LimsTaskScheduleServiceTest.java`

- [ ] **Step 1: Write failing schedule tests**

Create `LimsTaskScheduleServiceTest.java`:

```java
package cn.iocoder.yudao.module.lims.service.workflow;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsWorkflowSaveReqVO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskScheduleDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestTaskDO;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTaskScheduleMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestTaskMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LimsTaskScheduleServiceTest extends BaseMockitoUnitTest {

    @InjectMocks
    private LimsTaskScheduleService service;

    @Mock
    private LimsTestTaskMapper taskMapper;
    @Mock
    private LimsTaskScheduleMapper scheduleMapper;
    @Mock
    private LimsTaskLifecycleService lifecycleService;

    @Test
    void schedule_shouldPersistWindowAndMoveTaskToScheduled() {
        LimsTestTaskDO task = task(10L);
        when(taskMapper.selectById(10L)).thenReturn(task);
        when(scheduleMapper.selectActiveByEquipmentId(88L)).thenReturn(List.of());
        when(scheduleMapper.selectActiveByAssignedUserId(99L)).thenReturn(List.of());

        service.schedule(command());

        verify(scheduleMapper).insert(argThat((LimsTaskScheduleDO schedule) ->
                Long.valueOf(10L).equals(schedule.getTaskId())
                        && Long.valueOf(88L).equals(schedule.getEquipmentId())
                        && Long.valueOf(99L).equals(schedule.getAssignedUserId())
                        && LimsTaskScheduleStatus.SCHEDULED.equals(schedule.getScheduleStatus())));
        verify(taskMapper).updateById(argThat(updated ->
                LimsTaskStatus.SCHEDULED.equals(updated.getTaskStatus())
                        && LimsTaskScheduleStatus.SCHEDULED.equals(updated.getScheduleStatus())));
    }

    @Test
    void schedule_shouldRejectOverlappingEquipmentWindow() {
        when(taskMapper.selectById(10L)).thenReturn(task(10L));
        when(scheduleMapper.selectActiveByEquipmentId(88L)).thenReturn(List.of(existing()));

        assertThrows(Exception.class, () -> service.schedule(command()));
    }

    private static LimsWorkflowSaveReqVO command() {
        LimsWorkflowSaveReqVO req = new LimsWorkflowSaveReqVO();
        req.setTaskId(10L);
        req.setEquipmentId(88L);
        req.setAssignedUserId(99L);
        req.setPlannedStartTime("2026-06-15 09:00:00");
        req.setPlannedEndTime("2026-06-15 10:00:00");
        return req;
    }

    private static LimsTestTaskDO task(Long id) {
        LimsTestTaskDO task = new LimsTestTaskDO();
        task.setId(id);
        task.setRequestId(1L);
        task.setSampleId(2L);
        task.setTaskNo("REQ-001-T01");
        task.setTaskStatus(LimsTaskStatus.GENERATED);
        return task;
    }

    private static LimsTaskScheduleDO existing() {
        LimsTaskScheduleDO schedule = new LimsTaskScheduleDO();
        schedule.setPlannedStartTime("2026-06-15 09:30:00");
        schedule.setPlannedEndTime("2026-06-15 10:30:00");
        schedule.setScheduleStatus(LimsTaskScheduleStatus.SCHEDULED);
        return schedule;
    }
}
```

- [ ] **Step 2: Create schedule DO**

Create `LimsTaskScheduleDO.java`:

```java
package cn.iocoder.yudao.module.lims.dal.dataobject.workflow;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lims_task_schedule")
@KeySequence("lims_task_schedule_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LimsTaskScheduleDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long taskId;
    private Long requestId;
    private Long sampleId;
    private Long equipmentId;
    private Long assignedUserId;
    private String plannedStartTime;
    private String plannedEndTime;
    private String scheduleStatus;
    private String conflictReason;
    private Boolean locked;

}
```

- [ ] **Step 3: Create schedule mapper**

Create `LimsTaskScheduleMapper.java`:

```java
package cn.iocoder.yudao.module.lims.dal.mysql.workflow;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsWorkflowPageReqVO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskScheduleDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LimsTaskScheduleMapper extends BaseMapperX<LimsTaskScheduleDO> {

    default PageResult<LimsTaskScheduleDO> selectPage(LimsWorkflowPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LimsTaskScheduleDO>()
                .eqIfPresent(LimsTaskScheduleDO::getTaskId, reqVO.getTaskId())
                .eqIfPresent(LimsTaskScheduleDO::getAssignedUserId, reqVO.getAssignedUserId())
                .eqIfPresent(LimsTaskScheduleDO::getEquipmentId, reqVO.getEquipmentId())
                .eqIfPresent(LimsTaskScheduleDO::getScheduleStatus, reqVO.getScheduleStatus())
                .orderByAsc(LimsTaskScheduleDO::getPlannedStartTime));
    }

    default List<LimsTaskScheduleDO> selectActiveByEquipmentId(Long equipmentId) {
        return selectList(new LambdaQueryWrapperX<LimsTaskScheduleDO>()
                .eq(LimsTaskScheduleDO::getEquipmentId, equipmentId)
                .in(LimsTaskScheduleDO::getScheduleStatus, List.of(LimsTaskScheduleStatus.SCHEDULED, LimsTaskScheduleStatus.LOCKED)));
    }

    default List<LimsTaskScheduleDO> selectActiveByAssignedUserId(Long assignedUserId) {
        return selectList(new LambdaQueryWrapperX<LimsTaskScheduleDO>()
                .eq(LimsTaskScheduleDO::getAssignedUserId, assignedUserId)
                .in(LimsTaskScheduleDO::getScheduleStatus, List.of(LimsTaskScheduleStatus.SCHEDULED, LimsTaskScheduleStatus.LOCKED)));
    }
}
```

- [ ] **Step 4: Implement schedule service**

Create `LimsTaskScheduleService.java`:

```java
package cn.iocoder.yudao.module.lims.service.workflow;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsWorkflowPageReqVO;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsWorkflowSaveReqVO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskScheduleDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestTaskDO;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTaskScheduleMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestTaskMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lims.enums.ErrorCodeConstants.TEST_TASK_NOT_EXISTS;
import static cn.iocoder.yudao.module.lims.enums.ErrorCodeConstants.TEST_TASK_SCHEDULE_CONFLICT;

@Service
public class LimsTaskScheduleService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Resource
    private LimsTestTaskMapper taskMapper;
    @Resource
    private LimsTaskScheduleMapper scheduleMapper;
    @Resource
    private LimsTaskLifecycleService lifecycleService;

    @Transactional(rollbackFor = Exception.class)
    public Long schedule(LimsWorkflowSaveReqVO reqVO) {
        LimsTestTaskDO task = validateTask(reqVO.getTaskId());
        assertNoConflicts(reqVO);
        LimsTaskScheduleDO schedule = new LimsTaskScheduleDO();
        schedule.setTaskId(task.getId());
        schedule.setRequestId(task.getRequestId());
        schedule.setSampleId(task.getSampleId());
        schedule.setEquipmentId(reqVO.getEquipmentId());
        schedule.setAssignedUserId(reqVO.getAssignedUserId());
        schedule.setPlannedStartTime(reqVO.getPlannedStartTime());
        schedule.setPlannedEndTime(reqVO.getPlannedEndTime());
        schedule.setScheduleStatus(LimsTaskScheduleStatus.SCHEDULED);
        schedule.setLocked(false);
        scheduleMapper.insert(schedule);

        task.setEquipmentId(reqVO.getEquipmentId());
        task.setAssignedUserId(reqVO.getAssignedUserId());
        task.setPlannedStartTime(reqVO.getPlannedStartTime());
        task.setPlannedEndTime(reqVO.getPlannedEndTime());
        task.setScheduleStatus(LimsTaskScheduleStatus.SCHEDULED);
        task.setTaskStatus(LimsTaskStatus.SCHEDULED);
        task.setStatus(LimsTaskStatus.SCHEDULED);
        taskMapper.updateById(task);
        lifecycleService.writeEvent(task.getId(), task.getTaskNo(), LimsTaskEventType.SCHEDULED,
                LimsTaskStatus.GENERATED, LimsTaskStatus.SCHEDULED, null, null);
        return schedule.getId();
    }

    public PageResult<LimsTaskScheduleDO> getSchedulePage(LimsWorkflowPageReqVO reqVO) {
        return scheduleMapper.selectPage(reqVO);
    }

    private void assertNoConflicts(LimsWorkflowSaveReqVO reqVO) {
        if (hasOverlap(scheduleMapper.selectActiveByEquipmentId(reqVO.getEquipmentId()), reqVO)) {
            throw exception(TEST_TASK_SCHEDULE_CONFLICT);
        }
        if (hasOverlap(scheduleMapper.selectActiveByAssignedUserId(reqVO.getAssignedUserId()), reqVO)) {
            throw exception(TEST_TASK_SCHEDULE_CONFLICT);
        }
    }

    private boolean hasOverlap(List<LimsTaskScheduleDO> existing, LimsWorkflowSaveReqVO reqVO) {
        LocalDateTime start = parse(reqVO.getPlannedStartTime());
        LocalDateTime end = parse(reqVO.getPlannedEndTime());
        return existing.stream().anyMatch(item -> start.isBefore(parse(item.getPlannedEndTime()))
                && end.isAfter(parse(item.getPlannedStartTime())));
    }

    private static LocalDateTime parse(String value) {
        return LocalDateTime.parse(value, FORMATTER);
    }

    private LimsTestTaskDO validateTask(Long id) {
        LimsTestTaskDO task = id == null ? null : taskMapper.selectById(id);
        if (task == null) {
            throw exception(TEST_TASK_NOT_EXISTS);
        }
        return task;
    }
}
```

- [ ] **Step 5: Add schedule SQL**

Add `lims_task_schedule` table to `lab.sql`:

```sql
CREATE TABLE IF NOT EXISTS `lims_task_schedule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `task_id` bigint NOT NULL COMMENT '检测任务编号',
  `request_id` bigint NOT NULL COMMENT '检测需求编号',
  `sample_id` bigint NOT NULL COMMENT '样品编号',
  `equipment_id` bigint NULL DEFAULT NULL COMMENT '设备编号',
  `assigned_user_id` bigint NULL DEFAULT NULL COMMENT '执行人',
  `planned_start_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '计划开始时间',
  `planned_end_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '计划结束时间',
  `schedule_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'scheduled' COMMENT '排程状态',
  `conflict_reason` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '冲突原因',
  `locked` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否锁定',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lims_task_schedule_task` (`task_id`, `tenant_id`, `deleted`) USING BTREE,
  KEY `idx_lims_task_schedule_equipment` (`equipment_id`, `planned_start_time`, `planned_end_time`, `tenant_id`, `deleted`) USING BTREE,
  KEY `idx_lims_task_schedule_user` (`assigned_user_id`, `planned_start_time`, `planned_end_time`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'LIMS检测任务排程';
```

- [ ] **Step 6: Expose schedule endpoints**

In `LimsWorkflowController`, add:

```java
    @PostMapping("/lims/task/schedule")
    @Operation(summary = "检测任务排程")
    @PreAuthorize("@ss.hasPermission('lims:task:schedule')")
    public CommonResult<Long> scheduleTask(@Valid @RequestBody LimsWorkflowSaveReqVO reqVO) {
        return success(workflowService.scheduleTask(reqVO));
    }

    @GetMapping("/lims/task/schedule/page")
    @Operation(summary = "检测任务排程分页")
    @PreAuthorize("@ss.hasPermission('lims:task:query')")
    public CommonResult<PageResult<LimsWorkflowRespVO>> getTaskSchedulePage(@Valid LimsWorkflowPageReqVO pageReqVO) {
        return success(workflowService.getTaskSchedulePage(pageReqVO));
    }
```

In `LimsWorkflowService`, delegate to `LimsTaskScheduleService` and convert DOs with `BeanUtils.toBean`.

- [ ] **Step 7: Run schedule tests**

Run:

```bash
mvn -q -pl yudao-module-lims -am -Dtest=LimsTaskScheduleServiceTest -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: PASS.

---

## Task 5: Add Raw Record, QC, And Review Persistence

**Files:**
- Create: `LimsTaskRawRecordDO.java`
- Create: `LimsTaskQcRecordDO.java`
- Create: `LimsTaskReviewDO.java`
- Create corresponding mappers.
- Create: `LimsTaskRecordService.java`
- Modify: `LimsWorkflowController.java`
- Modify: `LimsWorkflowService.java`
- Modify: `sql/mysql/lab.sql`
- Test: `LimsTaskRecordServiceTest.java`

- [ ] **Step 1: Write failing record/review test**

Create `LimsTaskRecordServiceTest.java`:

```java
package cn.iocoder.yudao.module.lims.service.workflow;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsWorkflowSaveReqVO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskReviewDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestTaskDO;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTaskReviewMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestTaskMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LimsTaskRecordServiceTest extends BaseMockitoUnitTest {

    @InjectMocks
    private LimsTaskRecordService service;

    @Mock
    private LimsTestTaskMapper taskMapper;
    @Mock
    private LimsTaskReviewMapper reviewMapper;
    @Mock
    private LimsTaskLifecycleService lifecycleService;

    @Test
    void approveReview_shouldMarkTaskReportEligible() {
        when(taskMapper.selectById(10L)).thenReturn(task());

        service.approveReview(reviewCommand());

        verify(reviewMapper).insert(argThat((LimsTaskReviewDO review) ->
                Long.valueOf(10L).equals(review.getTaskId())
                        && LimsTaskReviewStatus.APPROVED.equals(review.getReviewStatus())));
        verify(taskMapper).updateById(argThat(updated ->
                LimsTaskStatus.COMPLETED.equals(updated.getTaskStatus())
                        && LimsTaskReviewStatus.APPROVED.equals(updated.getReviewStatus())
                        && Boolean.TRUE.equals(updated.getReportEligible())));
    }

    private static LimsWorkflowSaveReqVO reviewCommand() {
        LimsWorkflowSaveReqVO req = new LimsWorkflowSaveReqVO();
        req.setTaskId(10L);
        req.setReviewerId(66L);
        req.setRemark("数据和质控记录符合要求");
        return req;
    }

    private static LimsTestTaskDO task() {
        LimsTestTaskDO task = new LimsTestTaskDO();
        task.setId(10L);
        task.setTaskNo("REQ-001-T01");
        task.setTaskStatus(LimsTaskStatus.REVIEWING);
        task.setReviewStatus(LimsTaskReviewStatus.PENDING);
        return task;
    }
}
```

- [ ] **Step 2: Create DOs and mappers**

Use the same TenantBaseDO pattern as prior tasks.

`LimsTaskReviewDO.java`:

```java
package cn.iocoder.yudao.module.lims.dal.dataobject.workflow;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("lims_task_review")
@KeySequence("lims_task_review_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LimsTaskReviewDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long taskId;
    private String taskNo;
    private String reviewType;
    private String reviewStatus;
    private Long reviewerId;
    private String reviewTime;
    private String comment;
    private String snapshotHash;

}
```

Create mapper:

```java
package cn.iocoder.yudao.module.lims.dal.mysql.workflow;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskReviewDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LimsTaskReviewMapper extends BaseMapperX<LimsTaskReviewDO> {

    default List<LimsTaskReviewDO> selectListByTaskId(Long taskId) {
        return selectList(new LambdaQueryWrapperX<LimsTaskReviewDO>()
                .eq(LimsTaskReviewDO::getTaskId, taskId)
                .orderByDesc(LimsTaskReviewDO::getId));
    }
}
```

Implement raw record and QC DOs similarly:

```java
// LimsTaskRawRecordDO fields:
private Long id;
private Long taskId;
private String taskNo;
private String recordType;
private String recordJson;
private String attachmentUrl;
private Long versionNo;
private Long submittedBy;
private String submittedTime;
private String status;
```

```java
// LimsTaskQcRecordDO fields:
private Long id;
private Long taskId;
private String taskNo;
private String qcType;
private String qcRuleSnapshot;
private String qcDataJson;
private String qcResult;
private String reviewComment;
```

- [ ] **Step 3: Implement review approval**

Create `LimsTaskRecordService.java`:

```java
package cn.iocoder.yudao.module.lims.service.workflow;

import cn.iocoder.yudao.module.lims.controller.admin.workflow.vo.LimsWorkflowSaveReqVO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTaskReviewDO;
import cn.iocoder.yudao.module.lims.dal.dataobject.workflow.LimsTestTaskDO;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTaskReviewMapper;
import cn.iocoder.yudao.module.lims.dal.mysql.workflow.LimsTestTaskMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lims.enums.ErrorCodeConstants.TEST_TASK_NOT_EXISTS;

@Service
public class LimsTaskRecordService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Resource
    private LimsTestTaskMapper taskMapper;
    @Resource
    private LimsTaskReviewMapper reviewMapper;
    @Resource
    private LimsTaskLifecycleService lifecycleService;

    @Transactional(rollbackFor = Exception.class)
    public void approveReview(LimsWorkflowSaveReqVO reqVO) {
        LimsTestTaskDO task = validateTask(reqVO.getTaskId());
        LimsTaskReviewDO review = new LimsTaskReviewDO();
        review.setTaskId(task.getId());
        review.setTaskNo(task.getTaskNo());
        review.setReviewType("technical");
        review.setReviewStatus(LimsTaskReviewStatus.APPROVED);
        review.setReviewerId(reqVO.getReviewerId());
        review.setReviewTime(now());
        review.setComment(reqVO.getRemark());
        reviewMapper.insert(review);

        task.setReviewerId(reqVO.getReviewerId());
        task.setReviewStatus(LimsTaskReviewStatus.APPROVED);
        task.setTaskStatus(LimsTaskStatus.COMPLETED);
        task.setStatus(LimsTaskStatus.COMPLETED);
        task.setReportEligible(true);
        task.setActualEndTime(now());
        taskMapper.updateById(task);
        lifecycleService.writeEvent(task.getId(), task.getTaskNo(), LimsTaskEventType.APPROVED,
                LimsTaskStatus.REVIEWING, LimsTaskStatus.COMPLETED, reqVO.getRemark(), null);
    }

    private LimsTestTaskDO validateTask(Long id) {
        LimsTestTaskDO task = id == null ? null : taskMapper.selectById(id);
        if (task == null) {
            throw exception(TEST_TASK_NOT_EXISTS);
        }
        return task;
    }

    private static String now() {
        return LocalDateTime.now().format(TIME_FORMATTER);
    }
}
```

- [ ] **Step 4: Add SQL tables**

Add `lims_task_raw_record`, `lims_task_qc_record`, and `lims_task_review` to `lab.sql`. Use the fields listed in the design spec and index each table by `task_id`.

- [ ] **Step 5: Expose approve/reject endpoints**

In `LimsWorkflowController`, add:

```java
    @PutMapping("/lims/task/approve")
    @Operation(summary = "检测任务技术复核通过")
    @PreAuthorize("@ss.hasPermission('lims:task:review')")
    public CommonResult<Boolean> approveTask(@Valid @RequestBody LimsWorkflowSaveReqVO reqVO) {
        workflowService.approveTask(reqVO);
        return success(true);
    }
```

Add matching `rejectTask`, `submitTaskRecord`, and `submitTaskReview` endpoints in the same style.

- [ ] **Step 6: Run record tests**

Run:

```bash
mvn -q -pl yudao-module-lims -am -Dtest=LimsTaskRecordServiceTest -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: PASS.

---

## Task 6: Add Backend Controller And VO Fields

**Files:**
- Modify: `LimsWorkflowController.java`
- Modify: `LimsWorkflowSaveReqVO.java`
- Modify: `LimsWorkflowRespVO.java`
- Modify: `LimsWorkflowPageReqVO.java`
- Test: existing compile and controller coverage through service tests.

- [ ] **Step 1: Add command fields to SaveReqVO**

Add:

```java
    private String taskStatus;
    private String scheduleStatus;
    private Long reviewerId;
    private Long durationMinutes;
    private String actualStartTime;
    private String actualEndTime;
    private String methodSnapshot;
    private String readinessSnapshot;
    private String qcStatus;
    private String reviewStatus;
    private Boolean reportEligible;
    private String blockReason;
    private String recordType;
    private String recordJson;
    private String attachmentUrl;
    private String qcType;
    private String qcDataJson;
    private String qcResult;
    private String reviewComment;
```

- [ ] **Step 2: Add response fields to RespVO**

Add the same task fields, plus optional list JSON strings for the first UI pass:

```java
    private String taskStatus;
    private String scheduleStatus;
    private Long durationMinutes;
    private String actualStartTime;
    private String actualEndTime;
    private String methodSnapshot;
    private String readinessSnapshot;
    private String qcStatus;
    private String reviewStatus;
    private Boolean reportEligible;
    private String blockReason;
    private String eventLogJson;
    private String reviewJson;
    private String recordJson;
```

- [ ] **Step 3: Add page filters**

Add to `LimsWorkflowPageReqVO`:

```java
    private Long assignedUserId;
    private Long equipmentId;
    private String taskStatus;
    private String scheduleStatus;
    private String plannedStartTimeBegin;
    private String plannedStartTimeEnd;
```

- [ ] **Step 4: Update task mapper filters**

In `LimsTestTaskMapper.selectPage`, add:

```java
                .eqIfPresent(LimsTestTaskDO::getAssignedUserId, reqVO.getAssignedUserId())
                .eqIfPresent(LimsTestTaskDO::getEquipmentId, reqVO.getEquipmentId())
                .eqIfPresent(LimsTestTaskDO::getTaskStatus, reqVO.getTaskStatus())
                .eqIfPresent(LimsTestTaskDO::getScheduleStatus, reqVO.getScheduleStatus())
                .geIfPresent(LimsTestTaskDO::getPlannedStartTime, reqVO.getPlannedStartTimeBegin())
                .leIfPresent(LimsTestTaskDO::getPlannedStartTime, reqVO.getPlannedStartTimeEnd())
```

If `LambdaQueryWrapperX` in this repo does not have `geIfPresent` or `leIfPresent`, use explicit `if` blocks before `orderByDesc`.

- [ ] **Step 5: Run compile**

Run:

```bash
mvn -q -pl yudao-module-lims -am -DskipTests compile
```

Expected: PASS.

---

## Task 7: Add SQL Permissions And Menu Actions

**Files:**
- Modify: `sql/mysql/lab.sql`

- [ ] **Step 1: Add permissions under existing task menu**

After existing `检测任务删除`, add:

```sql
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测任务排程', 'lims:task:schedule', 3, 4, @lims_task_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:task:schedule' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测任务分派', 'lims:task:assign', 3, 5, @lims_task_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:task:assign' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测任务就绪检查', 'lims:task:readiness', 3, 6, @lims_task_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:task:readiness' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测任务记录', 'lims:task:record', 3, 7, @lims_task_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:task:record' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测任务复核', 'lims:task:review', 3, 8, @lims_task_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:task:review' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测任务暂停', 'lims:task:hold', 3, 9, @lims_task_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:task:hold' AND `deleted` = b'0');
```

- [ ] **Step 2: Validate SQL syntax with bootstrap import**

Use the local verification MariaDB path used in previous LIMS tasks. If not running, start the known local DB before this step.

Run:

```bash
mysql -h127.0.0.1 -P33306 -uroot -p123456 yudao_lims_verify < sql/mysql/lab.sql
```

Expected: import succeeds. If the local DB is unavailable, record the exact connection failure in `STATUS.md`.

---

## Task 8: Add Frontend API

**Files:**
- Modify: `yudao-ui/yudao-ui-admin-vue3/src/api/lims/workflow/index.ts`

- [ ] **Step 1: Add fields to `LimsWorkflowVO`**

Add:

```ts
  taskStatus?: string
  scheduleStatus?: string
  reviewerId?: number
  durationMinutes?: number
  actualStartTime?: string
  actualEndTime?: string
  methodSnapshot?: string
  readinessSnapshot?: string
  qcStatus?: string
  reviewStatus?: string
  reportEligible?: boolean
  blockReason?: string
  recordType?: string
  recordJson?: string
  attachmentUrl?: string
  qcType?: string
  qcDataJson?: string
  qcResult?: string
  reviewComment?: string
```

- [ ] **Step 2: Add API methods**

Add methods:

```ts
  scheduleTask: async (data: LimsWorkflowVO) => request.post({ url: '/lims/task/schedule', data }),
  getTaskSchedulePage: async (params: any) => request.get({ url: '/lims/task/schedule/page', params }),
  readinessCheck: async (id: number) => request.post({ url: '/lims/task/readiness-check', params: { id } }),
  assignTask: async (data: LimsWorkflowVO) => request.put({ url: '/lims/task/assign', data }),
  submitTaskRecord: async (data: LimsWorkflowVO) => request.put({ url: '/lims/task/submit-record', data }),
  submitTaskReview: async (data: LimsWorkflowVO) => request.put({ url: '/lims/task/submit-review', data }),
  approveTask: async (data: LimsWorkflowVO) => request.put({ url: '/lims/task/approve', data }),
  rejectTask: async (data: LimsWorkflowVO) => request.put({ url: '/lims/task/reject', data }),
  holdTask: async (data: LimsWorkflowVO) => request.put({ url: '/lims/task/hold', data }),
  resumeTask: async (data: LimsWorkflowVO) => request.put({ url: '/lims/task/resume', data }),
  getTaskEvents: async (taskId: number) => request.get({ url: '/lims/task/events', params: { taskId } }),
  getTaskRecords: async (taskId: number) => request.get({ url: '/lims/task/records', params: { taskId } }),
  getTaskReviews: async (taskId: number) => request.get({ url: '/lims/task/reviews', params: { taskId } })
```

- [ ] **Step 3: Run TypeScript check for API file**

Run:

```bash
cd yudao-ui/yudao-ui-admin-vue3 && pnpm exec vue-tsc --noEmit --skipLibCheck
```

Expected: current project may have unrelated type debt. If it fails, grep output for `src/api/lims/workflow/index.ts`; expected no matches for this file.

---

## Task 9: Build Dedicated Task Workspace UI

**Files:**
- Modify: `yudao-ui/yudao-ui-admin-vue3/src/views/lims/task/index.vue`
- Create: `TaskSchedulePanel.vue`
- Create: `TaskReadinessDrawer.vue`
- Create: `TaskRecordDrawer.vue`
- Create: `TaskReviewDrawer.vue`
- Create: `taskStatus.ts`

- [ ] **Step 1: Create status helper**

Create `taskStatus.ts`:

```ts
export const taskStatusOptions = [
  { label: '已生成', value: 'generated', type: 'info' },
  { label: '已排程', value: 'scheduled', type: 'primary' },
  { label: '已分派', value: 'assigned', type: 'primary' },
  { label: '已就绪', value: 'ready', type: 'success' },
  { label: '检测中', value: 'testing', type: 'warning' },
  { label: '数据已提交', value: 'data_submitted', type: 'warning' },
  { label: '复核中', value: 'reviewing', type: 'warning' },
  { label: '已批准', value: 'approved', type: 'success' },
  { label: '已完成', value: 'completed', type: 'success' },
  { label: '已入报告', value: 'reported', type: 'success' },
  { label: '暂停', value: 'hold', type: 'danger' },
  { label: '退回重做', value: 'rework', type: 'danger' },
  { label: '已取消', value: 'cancelled', type: 'info' }
] as const

export const statusLabel = (value?: string) => taskStatusOptions.find((item) => item.value === value)?.label || value || '-'
export const statusType = (value?: string) => taskStatusOptions.find((item) => item.value === value)?.type || 'info'
```

- [ ] **Step 2: Replace task index with dedicated table shell**

Replace `index.vue` with a dedicated page that:

- uses `ContentWrap`, `el-form`, `el-table`, `Pagination`;
- loads `/lims/task/page`;
- shows task no, task name, request no, sample no, test item, assignee, equipment, planned window, task status, schedule status, report eligibility;
- actions: 排程, 就绪检查, 开始检测, 记录, 复核, 暂停.

Minimum script structure:

```ts
import { LimsWorkflowApi, LimsWorkflowVO } from '@/api/lims/workflow'
import TaskSchedulePanel from './TaskSchedulePanel.vue'
import TaskReadinessDrawer from './TaskReadinessDrawer.vue'
import TaskRecordDrawer from './TaskRecordDrawer.vue'
import TaskReviewDrawer from './TaskReviewDrawer.vue'
import { statusLabel, statusType } from './taskStatus'

const loading = ref(false)
const list = ref<LimsWorkflowVO[]>([])
const total = ref(0)
const queryParams = reactive({ pageNo: 1, pageSize: 10, keyword: undefined, taskStatus: undefined, scheduleStatus: undefined })
const currentRow = ref<LimsWorkflowVO>()
const scheduleVisible = ref(false)
const readinessVisible = ref(false)
const recordVisible = ref(false)
const reviewVisible = ref(false)

const getList = async () => {
  loading.value = true
  try {
    const data = await LimsWorkflowApi.page('/lims/task', queryParams)
    list.value = data.list || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}
```

- [ ] **Step 3: Create schedule panel**

Create `TaskSchedulePanel.vue` with props `modelValue` and `task`, and form fields:

- `assignedUserId`
- `equipmentId`
- `plannedStartTime`
- `plannedEndTime`

On submit:

```ts
await LimsWorkflowApi.scheduleTask({
  taskId: props.task?.id,
  assignedUserId: formData.value.assignedUserId,
  equipmentId: formData.value.equipmentId,
  plannedStartTime: formData.value.plannedStartTime,
  plannedEndTime: formData.value.plannedEndTime
})
```

- [ ] **Step 4: Create readiness drawer**

Create `TaskReadinessDrawer.vue` that calls:

```ts
await LimsWorkflowApi.readinessCheck(props.task.id!)
```

Display `task.readinessSnapshot`, `equipmentSnapshot`, and `equipmentEvidenceSnapshot` as preformatted JSON blocks when present.

- [ ] **Step 5: Create record drawer**

Create `TaskRecordDrawer.vue` with fields:

- `recordJson`
- `resultValue`
- `resultUnit`
- `resultConclusion`
- `rawData`

Submit via:

```ts
await LimsWorkflowApi.submitTaskRecord({ taskId: props.task?.id, ...formData.value })
```

- [ ] **Step 6: Create review drawer**

Create `TaskReviewDrawer.vue` with fields:

- `reviewerId`
- `remark`

Approve via:

```ts
await LimsWorkflowApi.approveTask({ taskId: props.task?.id, reviewerId: formData.value.reviewerId, remark: formData.value.remark })
```

Reject via:

```ts
await LimsWorkflowApi.rejectTask({ taskId: props.task?.id, reviewerId: formData.value.reviewerId, remark: formData.value.remark })
```

- [ ] **Step 7: Run frontend build**

Run:

```bash
cd yudao-ui/yudao-ui-admin-vue3 && pnpm build:local
```

Expected: PASS.

---

## Task 10: End-to-End Verification

**Files:**
- Modify: `STATUS.md`

- [ ] **Step 1: Run backend focused tests**

Run:

```bash
mvn -q -pl yudao-module-lims -am -Dtest=LimsWorkflowServiceTest,LimsTaskLifecycleServiceTest,LimsTaskScheduleServiceTest,LimsTaskRecordServiceTest,LimsReportEligibilityServiceTest,WorkflowSnapshotFactoryTest -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: PASS.

- [ ] **Step 2: Run backend compile**

Run:

```bash
mvn -q -pl yudao-server -am -DskipTests compile
```

Expected: PASS.

- [ ] **Step 3: Run frontend build**

Run:

```bash
cd yudao-ui/yudao-ui-admin-vue3 && pnpm build:local
```

Expected: PASS.

- [ ] **Step 4: Run architecture guard**

Run:

```bash
rg -n "module\\.lab\\.dal\\.mysql|Lab[A-Za-z0-9]+Mapper" yudao-module-lims/src/main/java yudao-module-lims/src/test/java
```

Expected: no output.

- [ ] **Step 5: Run API closed loop**

Against local backend and verification DB:

1. Create accepted request with published domain pack.
2. Generate tasks.
3. Schedule first task.
4. Run readiness check.
5. Start task.
6. Submit raw record/result.
7. Approve task review.
8. Generate report.
9. Verify report `dataSnapshot` includes task, equipment evidence, QC/review summary.

Expected final states:

```text
taskStatus=completed
reviewStatus=approved
reportEligible=true
report.status=generated
```

- [ ] **Step 6: Browser verification**

Open local preview at:

```text
http://127.0.0.1:4173/lims/task
```

Verify:

- page title `检测任务`;
- filters render;
- task rows render;
- status tags render;
- schedule panel opens and saves;
- readiness drawer opens;
- record drawer opens;
- review drawer opens;
- no console errors.

- [ ] **Step 7: Update STATUS**

Append a new section to workspace root `STATUS.md`:

```markdown
## Ruoyi LIMS Experiment Task End-to-End Management Implementation

- Date: 2026-06-14
- Scope: implemented experiment task lifecycle, simple scheduling, task records/QC/review, and report eligibility gate inside `ruoyi-vue-pro`.
- Backend files updated:
  - `yudao-module-lims/...`
- Frontend files updated:
  - `yudao-ui/yudao-ui-admin-vue3/src/api/lims/workflow/index.ts`
  - `yudao-ui/yudao-ui-admin-vue3/src/views/lims/task/*`
- SQL updated:
  - `sql/mysql/lab.sql`
- Verification:
  - `<paste exact commands and outcomes>`
- Remaining risk:
  - `<state live schema migration and any skipped browser/API checks>`
```

---

## Self-Review Checklist

- Spec coverage:
  - Lifecycle: Tasks 1, 2, 5.
  - Simple scheduling: Task 4 and frontend Task 9.
  - Raw records/QC/review: Task 5 and frontend Task 9.
  - Report eligibility: Task 3.
  - Ruoyi-only scope: all tasks are under `ruoyi-vue-pro`.

- Placeholder scan:
  - This plan intentionally avoids unresolved placeholder markers and uses exact file paths and commands.

- Type consistency:
  - Task id command field uses existing `taskId`.
  - Lifecycle status field is `taskStatus`.
  - Schedule status field is `scheduleStatus`.
  - Report gate field is `reportEligible`.

## Execution Options

Recommended execution is subagent-driven because the work has independent backend, SQL, and frontend slices.

1. **Subagent-Driven**: one fresh subagent per task, review after each task, lower risk in dirty worktree.
2. **Inline Execution**: execute this plan in this session, with checkpoints after Tasks 3, 7, and 10.
