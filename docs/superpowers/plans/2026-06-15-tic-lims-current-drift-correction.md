# TIC LIMS Current Drift Correction Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Verify and complete the current TIC LIMS implementation against the 2026-06-15 drift-correction design, then commit only the aligned implementation slice.

**Architecture:** Keep the RuoYi modular monolith. LAB owns direction-pack, equipment, and evidence master data; LIMS consumes published direction-pack snapshots and equipment summaries through gateway/factory boundaries, then freezes execution/report snapshots. The implementation must not introduce direct LIMS dependencies on LAB Mapper classes.

**Tech Stack:** Java 17, Spring Boot 3, MyBatis Plus, Jackson, JUnit 5/Mockito, MySQL bootstrap SQL in `sql/mysql/lab.sql`, Vue3 source under `yudao-ui/yudao-ui-admin-vue3` when frontend changes are needed.

---

## Scope Lock

Implement or verify only these items:

- Equipment master data and available equipment query.
- Direction-pack access anti-corruption layer.
- LIMS task-equipment binding with frozen equipment/calibration evidence snapshots.
- Minimal equipment evidence chain into report snapshots.
- Immutable direction-pack lifecycle `draft -> published -> archived`.
- `WorkflowSnapshotFactory` + `DomainPackGateway` boundary.
- Direction-pack sample requirements, QC rules, evidence requirements, and report template/section rules entering `ExecutionPlan` and `ReportDraftPlan`.
- Minimal execution-side quality/evidence gate using the frozen snapshot.

Do not implement:

- Real IoT protocol integration.
- AI standard Q&A, AI report interpretation, or large-model data analysis.
- Pixel-level report designer.
- Commercial order/contract/billing/invoice flows.
- A new microservice split.

## Current Code Map

Design and plan:

- `docs/superpowers/specs/2026-06-15-tic-lims-current-drift-correction-design.md`
- `docs/superpowers/specs/2026-06-14-tic-lims-drift-correction-design.md`
- `docs/superpowers/plans/2026-06-14-tic-lims-first-batch.md`

LAB direction pack:

- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/service/domainpack/LabDomainPackServiceImpl.java`
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/service/domainpack/LabDomainPackQueryServiceImpl.java`
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/service/domainpack/dto/LabDomainPackSnapshotDTO.java`
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/service/packconfig/LabPackConfigServiceImpl.java`

LIMS snapshot and execution:

- `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/gateway/DomainPackGateway.java`
- `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/gateway/DomainPackGatewayImpl.java`
- `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/WorkflowSnapshotFactory.java`
- `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/ExecutionPlanFactory.java`
- `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/ReportDraftPlanFactory.java`
- `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/LimsWorkflowService.java`
- `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/LimsReportEligibilityService.java`
- `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/LimsQualityGateService.java`

Equipment and evidence:

- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/dal/dataobject/equipment/LabEquipmentAssetDO.java`
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/service/equipment/LabEquipmentAssetServiceImpl.java`
- `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/gateway/EquipmentGateway.java`
- `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/gateway/EquipmentGatewayImpl.java`
- `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/gateway/ReportEvidenceGateway.java`
- `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/gateway/ReportEvidenceGatewayImpl.java`

Tests:

- `yudao-module-lab/src/test/java/cn/iocoder/yudao/module/lab/service/domainpack/LabDomainPackServiceImplTest.java`
- `yudao-module-lab/src/test/java/cn/iocoder/yudao/module/lab/service/packconfig/LabPackConfigServiceImplTest.java`
- `yudao-module-lab/src/test/java/cn/iocoder/yudao/module/lab/service/domainpack/LabDomainPackQueryServiceImplTest.java`
- `yudao-module-lab/src/test/java/cn/iocoder/yudao/module/lab/service/equipment/LabEquipmentAssetServiceImplTest.java`
- `yudao-module-lims/src/test/java/cn/iocoder/yudao/module/lims/service/workflow/WorkflowSnapshotFactoryTest.java`
- `yudao-module-lims/src/test/java/cn/iocoder/yudao/module/lims/service/workflow/LimsWorkflowServiceTest.java`
- `yudao-module-lims/src/test/java/cn/iocoder/yudao/module/lims/service/workflow/LimsReportEligibilityServiceTest.java`
- `yudao-module-lims/src/test/java/cn/iocoder/yudao/module/lims/service/workflow/LimsQualityGateServiceTest.java`
- `yudao-module-lims/src/test/java/cn/iocoder/yudao/module/lims/service/workflow/LimsTaskRecordServiceTest.java`

## Task 1: Audit Boundary Drift Before Editing

**Files:**

- Read: all files in "Current Code Map"
- Modify: none unless the checks fail

- [ ] **Step 1: Confirm branch and uncommitted scope**

Run:

```bash
git status --short --branch
git diff --stat
```

Expected:

- Branch is `feature/lims-quality-evidence-gate`.
- Design commit `8cae131` is present on the branch.
- Remaining uncommitted changes are limited to LIMS quality/evidence gate and supporting tests.

- [ ] **Step 2: Check LIMS has no direct LAB Mapper dependency**

Run:

```bash
rg -n "cn\\.iocoder\\.yudao\\.module\\.lab\\.dal\\.mysql|Lab[A-Za-z0-9]+Mapper" \
  yudao-module-lims/src/main/java \
  yudao-module-lims/src/test/java
```

Expected:

- No matches.

If this finds a production dependency, remove it and route the call through `DomainPackGateway`, `EquipmentGateway`, or `ReportEvidenceGateway`.

- [ ] **Step 3: Check snapshot and plan factories own configuration propagation**

Run:

```bash
rg -n "sampleRequirements|qcRules|evidenceRequirements|reportSections|reportDraftPlan" \
  yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/WorkflowSnapshotFactory.java \
  yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/ExecutionPlanFactory.java \
  yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/ReportDraftPlanFactory.java
```

Expected:

- `WorkflowSnapshotFactory` writes `sampleRequirements`, `qcRules`, `evidenceRequirements`, and `reportSections`.
- `ExecutionPlanFactory` writes `sampleRequirements`, `qcCheckPlans`, `evidenceRequirementPlans`, and `reportDraftPlan`.
- `ReportDraftPlanFactory` writes `sections`, `sectionRules`, `dataBindings`, `outputFormats`, and `evidenceRequirements`.

## Task 2: Verify Direction-Pack Lifecycle and Published Immutability

**Files:**

- Modify if needed: `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/service/domainpack/LabDomainPackServiceImpl.java`
- Modify if needed: `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/service/packconfig/LabPackConfigServiceImpl.java`
- Test: `yudao-module-lab/src/test/java/cn/iocoder/yudao/module/lab/service/domainpack/LabDomainPackServiceImplTest.java`
- Test: `yudao-module-lab/src/test/java/cn/iocoder/yudao/module/lab/service/packconfig/LabPackConfigServiceImplTest.java`

- [ ] **Step 1: Run lifecycle tests**

Run:

```bash
mvn -q -pl yudao-module-lab -am \
  -Dtest=LabDomainPackServiceImplTest,LabPackConfigServiceImplTest \
  -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected:

- Published and archived packs reject direct update/delete.
- Published and archived pack configs reject save.
- Draft packs allow configuration save.
- Publish, copy version, and archive transitions pass.

- [ ] **Step 2: Patch only if immutable checks are missing**

If the tests fail because published or archived config can be edited, make `LabPackConfigServiceImpl.validateDomainPackEditable` enforce this logic:

```java
private void validateDomainPackEditable(Long domainPackId) {
    LabDomainPackDO domainPack = domainPackId == null ? null : domainPackMapper.selectById(domainPackId);
    if (domainPack == null) {
        throw exception(DOMAIN_PACK_NOT_EXISTS);
    }
    if (!"draft".equalsIgnoreCase(domainPack.getStatus())) {
        throw exception(DOMAIN_PACK_PUBLISHED_IMMUTABLE);
    }
}
```

Then rerun Step 1.

## Task 3: Verify WorkflowSnapshot, ExecutionPlan, and ReportDraftPlan Coverage

**Files:**

- Modify if needed: `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/WorkflowSnapshotFactory.java`
- Modify if needed: `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/ExecutionPlanFactory.java`
- Modify if needed: `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/ReportDraftPlanFactory.java`
- Test: `yudao-module-lims/src/test/java/cn/iocoder/yudao/module/lims/service/workflow/WorkflowSnapshotFactoryTest.java`
- Test: `yudao-module-lims/src/test/java/cn/iocoder/yudao/module/lims/service/workflow/LimsWorkflowServiceTest.java`

- [ ] **Step 1: Run snapshot and execution-plan tests**

Run:

```bash
mvn -q -pl yudao-module-lims -am \
  -Dtest=WorkflowSnapshotFactoryTest,LimsWorkflowServiceTest \
  -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected:

- Request creation freezes the published direction-pack snapshot from `DomainPackGateway`.
- Generated execution plan contains `sampleRequirements`, `taskPlans`, `resultFieldPlans`, `qcCheckPlans`, `evidenceRequirementPlans`, and `reportDraftPlan`.
- Report output uses `ReportDraftPlanFactory`.

- [ ] **Step 2: Patch factory propagation only if required fields are absent**

If `ExecutionPlanFactory` misses any required plan fields, set them explicitly in `createPlanJson`:

```java
plan.set("sampleRequirements", copyArray(snapshot.path("sampleRequirements")));
plan.set("taskPlans", createTaskPlans(snapshot.path("testItems")));
plan.set("resultFieldPlans", copyArray(snapshot.path("resultFields")));
plan.set("qcCheckPlans", copyArray(snapshot.path("qcRules")));
plan.set("evidenceRequirementPlans", copyArray(snapshot.path("evidenceRequirements")));
plan.set("reportDraftPlan", reportDraftPlanFactory.createReportDraftPlan(snapshot));
```

If `ReportDraftPlanFactory` misses report or evidence fields, set them explicitly in `createReportDraftPlan`:

```java
plan.set("outputFormats", createOutputFormats(templateSchema));
plan.set("sections", copyArray(workflowSnapshot.path("reportSections")));
plan.set("sectionRules", copyArray(templateSchema.path("reportSections")));
plan.set("dataBindings", createDataBindings(workflowSnapshot.path("resultFields")));
plan.set("evidenceRequirements", copyArray(workflowSnapshot.path("evidenceRequirements")));
```

Then rerun Step 1.

## Task 4: Finish the Minimal Quality and Evidence Gate

**Files:**

- Create or modify: `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/LimsQualityGateService.java`
- Modify: `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/LimsWorkflowService.java`
- Modify: `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/LimsReportEligibilityService.java`
- Modify: `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/LimsTaskRecordService.java`
- Modify: `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/dal/mysql/workflow/LimsTaskRawRecordMapper.java`
- Modify: `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/dal/mysql/workflow/LimsTaskQcRecordMapper.java`
- Modify: `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/dal/mysql/workflow/LimsTestResultMapper.java`
- Test: `yudao-module-lims/src/test/java/cn/iocoder/yudao/module/lims/service/workflow/LimsQualityGateServiceTest.java`
- Test: `yudao-module-lims/src/test/java/cn/iocoder/yudao/module/lims/service/workflow/LimsReportEligibilityServiceTest.java`
- Test: `yudao-module-lims/src/test/java/cn/iocoder/yudao/module/lims/service/workflow/LimsTaskRecordServiceTest.java`
- Test: `yudao-module-lims/src/test/java/cn/iocoder/yudao/module/lims/service/workflow/LimsWorkflowServiceTest.java`

- [ ] **Step 1: Validate result fields from the frozen snapshot before result creation**

Implementation contract:

```java
LimsTestTaskDO task = validateTaskExists(createReqVO.getTaskId());
LimsTestRequestDO request = validateRequestExists(task.getRequestId());
qualityGateService.validateResultValues(request, task, createReqVO.getRawData());
```

Expected behavior:

- Required result fields must be present in `rawData.resultValues`.
- Numeric fields must parse and satisfy configured min/max.
- Enum fields must match configured options.

- [ ] **Step 2: Validate QC and evidence before reports**

Implementation contract:

```java
qualityGateService.assertQcAndEvidenceComplete(
        request,
        tasks,
        rawRecordsByTaskId(tasks),
        qcRecordsByTaskId(tasks),
        reviewsByTaskId(tasks));
```

Expected behavior:

- Each active frozen QC rule needs at least one approved QC record carrying the rule code.
- Required raw-record evidence needs a raw record.
- Required equipment/calibration evidence needs a task equipment evidence snapshot.
- Required review evidence needs an approved technical review.

- [ ] **Step 3: Validate quality gate during technical review approval**

Implementation contract:

```java
qualityGateService.assertQcAndEvidenceComplete(request, List.of(task),
        Map.of(task.getId(), rawRecordMapper.selectListByTaskId(task.getId())),
        Map.of(task.getId(), qcRecordMapper.selectListByTaskId(task.getId())),
        Map.of(task.getId(), reviews));
```

Expected behavior:

- A task cannot be marked `reportEligible = true` unless the frozen snapshot evidence requirements are satisfied.
- Related result rows are synchronized to `approved` during technical review approval.

- [ ] **Step 4: Run quality-gate tests**

Run:

```bash
mvn -q -pl yudao-module-lims -am \
  -Dtest=LimsQualityGateServiceTest,LimsReportEligibilityServiceTest,LimsTaskRecordServiceTest,LimsWorkflowServiceTest \
  -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected:

- All listed tests pass.

## Task 5: Full Verification and Commit

**Files:**

- Modify: `/Users/guojiexie/Development/lims/STATUS.md`
- Commit all aligned implementation files in `ruoyi-vue-pro`

- [ ] **Step 1: Run focused module tests**

Run:

```bash
mvn -q -pl yudao-module-lims -am \
  -Dtest=LimsQualityGateServiceTest,LimsTaskRecordServiceTest,LimsWorkflowServiceTest,LimsTaskLifecycleServiceTest,LimsTaskScheduleServiceTest,LimsReportEligibilityServiceTest,WorkflowSnapshotFactoryTest \
  -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected:

- All tests pass.

- [ ] **Step 2: Compile server**

Run:

```bash
mvn -q -pl yudao-server -am -DskipTests compile
```

Expected:

- Compile passes.

- [ ] **Step 3: Re-run architecture grep**

Run:

```bash
rg -n "cn\\.iocoder\\.yudao\\.module\\.lab\\.dal\\.mysql|Lab[A-Za-z0-9]+Mapper" \
  yudao-module-lims/src/main/java \
  yudao-module-lims/src/test/java
```

Expected:

- No matches.

- [ ] **Step 4: Check diff hygiene**

Run:

```bash
git diff --check
git status --short --branch
```

Expected:

- `git diff --check` exits successfully.
- Only intended implementation files are modified.

- [ ] **Step 5: Commit and push**

Use Lore protocol. Suggested intent line:

```text
Gate reports on frozen result rules and evidence coverage
```

Required verification trailers:

```text
Constraint: LIMS must use gateway/factory boundaries and frozen snapshots instead of LAB Mapper reads
Rejected: Generate reports from task/result status alone | misses direction-pack QC and evidence requirements
Confidence: high
Scope-risk: moderate
Directive: Do not bypass LimsQualityGateService when marking a task or request reportable
Tested: <exact Maven test commands that passed>
Tested: mvn -q -pl yudao-server -am -DskipTests compile
Tested: architecture grep found no LIMS LAB Mapper dependency
Not-tested: Authenticated browser flow if no frontend files changed
```

Push:

```bash
git push
```

Expected:

- Remote branch `origin/feature/lims-quality-evidence-gate` contains the implementation commit.

## Self-Review

Spec coverage:

- Equipment first-batch scope is audited and kept out of IoT expansion.
- Direction-pack immutable lifecycle is verified through LAB service/config tests.
- `DomainPackGateway` and `WorkflowSnapshotFactory` boundaries are verified by grep and service tests.
- `ExecutionPlan` and `ReportDraftPlan` propagation is verified by LIMS tests.
- Frozen snapshot quality/evidence gate is implemented as the minimal execution-side completion step.

Placeholder scan:

- This plan intentionally contains no unresolved placeholder markers.

Type consistency:

- The plan uses existing class and method names observed in the current codebase: `DomainPackGateway`, `WorkflowSnapshotFactory`, `ExecutionPlanFactory`, `ReportDraftPlanFactory`, `LimsQualityGateService`, and `assertQcAndEvidenceComplete`.
