# LIMS Experiment Task End-to-End Management Design

## Status

Proposed. This is a design-only specification for `ruoyi-vue-pro`. It does not require changing or reusing the older `iimake-qms-lims` module.

## Context

The current Ruoyi LIMS flow already has a working MVP:

```text
检测需求 -> 样品 -> 检测任务 -> 检测结果 -> 检测报告
```

The current `lims_test_task` table and `/lims/task` page mainly support generated tasks, manual edits, start action, equipment snapshots, and report evidence snapshots. This is enough for a closed-loop demo, but it is not yet a full laboratory task control module.

For ISO/IEC 17025 / CNAS-style laboratory operation, an experiment task should be the controlled execution unit that connects request review, sample identity, method version, personnel authorization, equipment calibration, environment conditions, raw data, quality control, review, nonconforming work, and report eligibility.

This design keeps all implementation inside:

- `ruoyi-vue-pro/yudao-module-lims`
- `ruoyi-vue-pro/yudao-ui/yudao-ui-admin-vue3/src/api/lims`
- `ruoyi-vue-pro/yudao-ui/yudao-ui-admin-vue3/src/views/lims`
- `ruoyi-vue-pro/sql/mysql/lab.sql`

It may read through existing LAB service gateways, but LIMS must not directly import LAB mappers.

## Goals

- Turn `lims_test_task` from a simple work item into the central experiment execution aggregate.
- Add end-to-end task lifecycle management from generated task to approved result.
- Add simple scheduling for task time windows, personnel, equipment, and conflict detection.
- Preserve current request/sample/result/report closed loop.
- Ensure reports can only use reviewed and approved task results.
- Keep the first implementation small enough to ship in one Ruoyi LIMS slice.

## Non-Goals

- No advanced optimization scheduler, capacity simulation, or automatic rescheduling engine.
- No cross-repo implementation in `iimake-qms-lims`.
- No new dependency.
- No full electronic signature platform in the first slice.
- No replacement of LAB configuration modules; LIMS should consume published configuration snapshots.

## Standard-Derived Requirements

| Standard concern | Task module responsibility |
| --- | --- |
| Review of request and contract | Generate execution tasks only from accepted/reviewed requests. |
| Method selection and version control | Freeze method code, method name, standard code, and workflow snapshot hash on each task. |
| Sample handling | Every task must bind exactly one sample identity. |
| Personnel competence | Task assignment and readiness checks must verify personnel authorization when available. |
| Equipment and metrological traceability | Task must bind equipment and freeze calibration/evidence snapshots before execution. |
| Environment conditions | Task start/readiness must check and record required environment conditions when configured. |
| Technical records | Task execution must capture raw records, result values, calculations, attachments, and audit trail. |
| Ensuring validity of results | Task must support QC checks and block approval when QC fails. |
| Reporting results | Report generation must use only approved task results. |
| Nonconforming work | Task must support hold, rework, cancellation, and report blocking. |
| Data control | All task state changes and data changes need immutable event/audit records. |

## Recommended Approach

Use an incremental, aggregate-based design inside `yudao-module-lims`.

`LimsWorkflowService` should keep the existing high-level closed-loop API, but task-specific behavior should move into focused services:

- `LimsTaskService`: task lifecycle, assignment, readiness, start, submit, review.
- `LimsTaskScheduleService`: simple scheduling, conflict checks, schedule updates.
- `LimsTaskRecordService`: raw records, QC records, review records, event logs.
- `LimsReportEligibilityService`: checks whether a request can generate a report.

This avoids a large rewrite while reducing the current `LimsWorkflowService` pressure.

## Alternatives Considered

### Option A: Expand the existing `LimsWorkflowService`

Rejected. It is fastest for a small demo, but it would concentrate task lifecycle, scheduling, records, review, and report gating in one service.

### Option B: Introduce a separate scheduling engine

Rejected for now. It is too broad for the current need and would add operational complexity before the task lifecycle is mature.

### Option C: Add a focused experiment-task subdomain inside Ruoyi LIMS

Selected. It keeps scope inside `ruoyi-vue-pro`, fits current package patterns, and can evolve from MVP to production control without cross-repo drift.

## Domain Model

### Existing Tables To Extend

#### `lims_test_task`

Add lifecycle and scheduling fields:

- `task_status`: canonical lifecycle status.
- `schedule_status`: unscheduled / scheduled / conflict / locked.
- `assigned_user_id`: existing field remains primary executor.
- `reviewer_id`: technical reviewer.
- `planned_start_time`, `planned_end_time`: existing fields should become schedule window.
- `actual_start_time`, `actual_end_time`.
- `duration_minutes`: estimated execution duration.
- `method_snapshot`: frozen method/config snapshot.
- `readiness_snapshot`: readiness gate result JSON.
- `qc_status`: none / pending / pass / fail.
- `review_status`: none / pending / approved / rejected.
- `report_eligible`: boolean.
- `block_reason`: latest blocking reason.

Keep current equipment fields:

- `equipment_id`
- `equipment_code`
- `equipment_name`
- `equipment_snapshot`
- `equipment_evidence_snapshot`

### New Tables

#### `lims_task_schedule`

Stores the user-visible schedule assignment.

Key fields:

- `id`
- `task_id`
- `request_id`
- `sample_id`
- `equipment_id`
- `assigned_user_id`
- `planned_start_time`
- `planned_end_time`
- `schedule_status`
- `conflict_reason`
- `locked`
- tenant/audit fields

Rule: this table owns scheduling conflicts and calendar display. `lims_test_task` stores the latest denormalized schedule fields for list/report convenience.

#### `lims_task_raw_record`

Stores structured raw records and attachments.

Key fields:

- `id`
- `task_id`
- `task_no`
- `record_type`: raw_data / calculation / attachment / observation
- `record_json`
- `attachment_url`
- `version_no`
- `submitted_by`
- `submitted_time`
- `status`
- tenant/audit fields

#### `lims_task_qc_record`

Stores quality-control checks.

Key fields:

- `id`
- `task_id`
- `qc_type`
- `qc_rule_snapshot`
- `qc_data_json`
- `qc_result`: pass / fail
- `review_comment`
- tenant/audit fields

#### `lims_task_review`

Stores technical review.

Key fields:

- `id`
- `task_id`
- `review_type`: technical / qa
- `review_status`: approved / rejected
- `reviewer_id`
- `review_time`
- `comment`
- `snapshot_hash`
- tenant/audit fields

#### `lims_task_event_log`

Stores immutable lifecycle evidence.

Key fields:

- `id`
- `task_id`
- `event_type`
- `from_status`
- `to_status`
- `operator_id`
- `event_time`
- `reason`
- `payload_json`
- tenant fields

## Task State Machine

Use one canonical task lifecycle:

```text
generated
  -> scheduled
  -> assigned
  -> ready
  -> testing
  -> data_submitted
  -> reviewing
  -> approved
  -> completed
  -> reported
```

Exceptional states:

```text
hold
rework
cancelled
```

Allowed transitions:

- `generated -> scheduled`: schedule created.
- `scheduled -> assigned`: executor assigned.
- `assigned -> ready`: readiness check passed.
- `ready -> testing`: executor starts task.
- `testing -> data_submitted`: raw records and result values submitted.
- `data_submitted -> reviewing`: technical review opened.
- `reviewing -> approved`: reviewer approves.
- `reviewing -> rework`: reviewer rejects and returns.
- `rework -> testing`: executor restarts or amends records.
- `approved -> completed`: system marks report-eligible completion.
- `completed -> reported`: report generation consumes task result.
- `any active state -> hold`: nonconforming work or resource issue.
- `hold -> previous active state`: authorized resume.
- `generated/scheduled/assigned -> cancelled`: no execution has started.

Report generation should reject any request with tasks not in `approved` or `completed`.

## Simple Scheduling Design

### Scheduling Scope

The first scheduling version is a deterministic assistant, not an optimizer.

It supports:

- schedule by task estimated duration;
- assign planned start/end;
- bind executor and equipment;
- detect conflicts;
- show calendar/table view;
- allow manual override with reason.

It does not:

- optimize across all resources;
- split one task into multiple sessions;
- model shifts or holidays beyond a simple availability table/config.

### Scheduling Inputs

From request:

- priority;
- due date;
- domain pack;
- workflow snapshot hash.

From task:

- test item;
- method;
- estimated duration;
- equipment requirement;
- executor requirement.

From resources:

- equipment availability;
- equipment calibration validity;
- assigned user existing task windows;
- optional environment requirement.

### Scheduling Algorithm

For MVP:

1. Sort unscheduled tasks by request priority, due date, request id, task id.
2. For each task, resolve candidate equipment and candidate executor.
3. Start from the requested planned start or next business-hour slot.
4. Find the first window where assigned equipment and user have no overlap.
5. Write `lims_task_schedule`.
6. Copy planned window and assignee to `lims_test_task`.
7. Mark conflicts if no slot exists or resource is invalid.

Overlap rule:

```text
new_start < existing_end AND new_end > existing_start
```

Conflict checks:

- same equipment overlapping active schedules;
- same assigned user overlapping active schedules;
- equipment unavailable or calibration expired;
- task due date exceeded;
- task already locked or already started.

### UI

Add a dedicated task workspace instead of relying only on generic `LimsWorkflowPage`:

- `src/views/lims/task/index.vue`: list, filters, status actions.
- `src/views/lims/task/TaskSchedulePanel.vue`: day/week table or timeline.
- `src/views/lims/task/TaskReadinessDrawer.vue`: readiness details.
- `src/views/lims/task/TaskRecordDrawer.vue`: raw records and QC data.
- `src/views/lims/task/TaskReviewDrawer.vue`: technical review.

No visual-heavy Gantt is required in the first slice. A table/calendar panel is enough.

## API Design

Keep existing endpoints:

- `GET /lims/task/page`
- `GET /lims/task/get`
- `POST /lims/task/create`
- `PUT /lims/task/update`
- `DELETE /lims/task/delete`
- `PUT /lims/task/start`

Add task lifecycle endpoints:

- `POST /lims/task/generate-schedules?requestId=`
- `PUT /lims/task/schedule`
- `POST /lims/task/readiness-check?id=`
- `PUT /lims/task/assign`
- `PUT /lims/task/submit-record`
- `PUT /lims/task/submit-review`
- `PUT /lims/task/approve`
- `PUT /lims/task/reject`
- `PUT /lims/task/hold`
- `PUT /lims/task/resume`

Add query endpoints:

- `GET /lims/task/schedule/page`
- `GET /lims/task/records?taskId=`
- `GET /lims/task/reviews?taskId=`
- `GET /lims/task/events?taskId=`

Report generation change:

- `/lims/request/generate-report` must call report eligibility checks before creating a report.

## Integration With Current Design

### Domain Pack

The task module consumes frozen `workflowSnapshot` generated from published domain packs. Add optional fields to pack test items:

- `estimatedDurationMinutes`
- `requiredEquipmentType`
- `requiredPersonnelRole`
- `readinessRules`
- `qcRules`

If absent, default duration is 60 minutes and readiness/QC are best-effort.

### Equipment

Continue using `EquipmentGateway`. Do not import LAB mappers into LIMS. Readiness should reuse:

- available equipment;
- equipment master snapshot;
- calibration evidence snapshot.

### Personnel

Introduce a `PersonnelAuthorizationGateway` only if existing LAB personnel service APIs are ready. Otherwise, first implementation records `assignedUserId` and leaves personnel authorization as a warning-level readiness item.

### Result Values

Keep `lims_test_result` and `lims_test_result_value`, but create or update result rows only when task records are submitted. Result approval should move to task technical review so the task, raw record, result value, and review decision stay together.

### Report

Report content should include:

- approved tasks;
- task raw-record summary;
- result values;
- equipment snapshots;
- equipment evidence snapshots;
- QC summary;
- technical review summary;
- event-log hash or task snapshot hash.

## Permissions

Add permissions:

- `lims:task:schedule`
- `lims:task:assign`
- `lims:task:readiness`
- `lims:task:record`
- `lims:task:review`
- `lims:task:hold`

Keep existing:

- `lims:task:query`
- `lims:task:create`
- `lims:task:update`
- `lims:task:delete`

## Error Handling

Use explicit business errors:

- task not found;
- invalid task status transition;
- schedule conflict;
- equipment unavailable;
- readiness failed;
- raw record missing;
- QC failed;
- review required;
- report blocked by unapproved task.

Every failed lifecycle action should return a user-readable reason and write an event only if the business state changed.

## Verification Strategy

Backend unit tests:

- generate tasks from workflow snapshot with duration/resource defaults;
- schedule tasks without overlap;
- detect user/equipment conflicts;
- readiness passes with valid equipment;
- readiness blocks expired calibration;
- invalid status transitions fail;
- submit records creates result/result values;
- review approval marks task report-eligible;
- report generation rejects unapproved tasks;
- event log records every state transition.

Backend compile:

- `mvn -q -pl yudao-module-lims -am -Dtest=LimsWorkflowServiceTest,LimsTaskServiceTest,LimsTaskScheduleServiceTest -Dsurefire.failIfNoSpecifiedTests=false test`
- `mvn -q -pl yudao-server -am -DskipTests compile`

Frontend verification:

- `pnpm build:local`
- targeted browser preview for task list, scheduling panel, readiness drawer, record drawer, and review drawer.

SQL verification:

- import `sql/mysql/lab.sql` into local verification schema;
- confirm new tables and permissions exist;
- run API closed loop from accepted request to approved tasks to report generation.

## Phasing

### Phase 1: Task Lifecycle and Report Gate

- Add statuses, event log, task service, and report eligibility check.
- Keep current UI mostly intact.
- Make report generation reject unapproved tasks.

### Phase 2: Simple Scheduling

- Add `lims_task_schedule`.
- Add schedule generation and conflict detection.
- Add task schedule panel.

### Phase 3: Raw Records and Review

- Add raw record, QC record, and review tables.
- Move result approval into task review.
- Add record and review drawers.

### Phase 4: Readiness Hardening

- Add readiness rules from domain pack.
- Add personnel authorization gateway when LAB personnel APIs are stable.
- Add environment readiness and nonconforming-work integration.

## ADR-001: Keep Experiment Task Management Inside Ruoyi LIMS

### Status

Proposed

### Context

The user requested the new module to connect with the existing overall design and only change the Ruoyi stack. The current Ruoyi LIMS already owns the active request/sample/task/result/report closed loop.

### Decision

Build the experiment task module under `yudao-module-lims` and the existing Ruoyi Vue admin app. Do not implement against the older `iimake-qms-lims` module.

### Consequences

Positive:

- Avoids two competing task models.
- Keeps report evidence snapshot integration close to the current implementation.
- Can reuse domain-pack snapshots and equipment gateways.

Negative:

- Some mature ideas from `iimake-qms-lims` must be re-modeled rather than reused directly.

## ADR-002: Use Simple Deterministic Scheduling Instead Of An Optimizer

### Status

Proposed

### Context

Laboratory teams need a visible, conflict-aware schedule, but the current request is for simple scheduling.

### Decision

Implement first-available-window scheduling with manual override and conflict detection.

### Consequences

Positive:

- Shippable without a new scheduling dependency.
- Easy for operators to understand and override.

Negative:

- It will not maximize throughput or balance workloads globally.

## Open Risks

- Current live schemas need incremental migrations; `lab.sql` is bootstrap-oriented.
- Personnel authorization depends on available LAB personnel service APIs.
- Existing generic `LimsWorkflowPage` will become limiting once raw records and review drawers are added.
- The current dirty worktree contains related report/template changes; implementation should start after those changes are settled or explicitly incorporated.
