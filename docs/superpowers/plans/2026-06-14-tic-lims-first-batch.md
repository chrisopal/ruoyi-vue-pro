# TIC LIMS First Batch Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Deliver the first corrected TIC LIMS batch: immutable DomainPack publication, LAB/LIMS anti-corruption access, workflow/execution/report draft planning, equipment master data, task-equipment binding, and the minimal equipment evidence chain.

**Architecture:** Keep the current RuoYi modular monolith and enforce DDD boundaries by adding explicit services at module boundaries. LAB owns DomainPack, equipment, and evidence master data; LIMS reads published snapshots and equipment summaries through gateway interfaces and freezes snapshots into execution records.

**Tech Stack:** Java 17, Spring Boot 3, MyBatis Plus, Jackson, JUnit 5/Mockito, MySQL DDL in `sql/mysql/lab.sql`, Vue 3 + Element Plus in `yudao-ui/yudao-ui-admin-vue3`.

---

## Scope Lock

This plan implements the first batch from `docs/superpowers/specs/2026-06-14-tic-lims-drift-correction-design.md`.

Included:

- DomainPack lifecycle: `draft -> published -> archived`.
- Published DomainPack versions are immutable.
- LIMS no longer injects LAB Mapper classes.
- `DomainPackGateway`, `LabDomainPackQueryService`, and `WorkflowSnapshotFactory`.
- Direction pack sample requirements, QC rules, evidence requirements, and report sections included in workflow, execution, and report draft plans.
- Equipment asset master data and available-equipment query.
- Calibration certificate evidence object and clause link.
- LIMS task equipment binding with frozen equipment snapshot.
- Report data snapshot includes equipment and equipment evidence summary.
- Vue3 admin changes for Chinese-visible first-batch workflows.

Excluded:

- MQTT/Modbus/TCP IoT protocol integration.
- AI standard Q&A and report interpretation.
- Pixel-level Word/PDF/Excel report designer.
- Commercial order, contract, billing, and invoice flows.

## Current Code Map

Backend:

- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/service/domainpack/LabDomainPackServiceImpl.java` owns DomainPack CRUD.
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/service/packconfig/LabPackConfigServiceImpl.java` owns existing workflow/test item/result field/report section config.
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/dal/dataobject/equipment/LabEquipmentTraceabilityDO.java` and `LabEquipmentIntermediateCheckDO.java` are existing equipment-adjacent records without an asset aggregate.
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/dal/dataobject/evidencelink/LabEvidenceLinkDO.java` links evidence-like records but does not define evidence objects.
- `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/LimsWorkflowService.java` currently injects `LabDomainPackMapper`; this must be removed.
- `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/dal/dataobject/workflow/LimsTestTaskDO.java` has no equipment fields.
- `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/dal/dataobject/workflow/LimsReportDO.java` already stores `dataSnapshot` and `workflowSnapshotHash`.

SQL:

- `sql/mysql/lab.sql` is the additive bootstrap/migration script and already contains LAB/LIMS base tables, menus, and seed data.

Frontend:

- `yudao-ui/yudao-ui-admin-vue3/src/api/lab/domain-pack/index.ts`
- `yudao-ui/yudao-ui-admin-vue3/src/api/lab/pack-config/index.ts`
- `yudao-ui/yudao-ui-admin-vue3/src/api/lims/workflow/index.ts`
- `yudao-ui/yudao-ui-admin-vue3/src/views/lab/domain-pack/index.vue`
- `yudao-ui/yudao-ui-admin-vue3/src/views/lab/pack-designer/index.vue`
- `yudao-ui/yudao-ui-admin-vue3/src/views/lims/_components/LimsWorkflowPage.vue`
- `yudao-ui/yudao-ui-admin-vue3/src/views/lims/task/index.vue`
- `yudao-ui/yudao-ui-admin-vue3/src/views/lims/report/index.vue`

## Target File Structure

Create backend LAB files:

- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/controller/admin/equipmentasset/LabEquipmentAssetController.java`
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/controller/admin/equipmentasset/vo/LabEquipmentAssetPageReqVO.java`
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/controller/admin/equipmentasset/vo/LabEquipmentAssetRespVO.java`
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/controller/admin/equipmentasset/vo/LabEquipmentAssetSaveReqVO.java`
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/controller/admin/equipmentasset/vo/LabEquipmentAssetAvailableReqVO.java`
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/controller/admin/equipmentcalibration/LabEquipmentCalibrationController.java`
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/controller/admin/equipmentcalibration/vo/LabEquipmentCalibrationSaveReqVO.java`
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/controller/admin/evidenceobject/vo/LabEvidenceObjectRespVO.java`
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/dal/dataobject/equipment/LabEquipmentAssetDO.java`
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/dal/dataobject/equipment/LabEquipmentCalibrationRecordDO.java`
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/dal/dataobject/evidenceobject/LabEvidenceObjectDO.java`
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/dal/mysql/equipment/LabEquipmentAssetMapper.java`
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/dal/mysql/equipment/LabEquipmentCalibrationRecordMapper.java`
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/dal/mysql/evidenceobject/LabEvidenceObjectMapper.java`
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/service/domainpack/LabDomainPackQueryService.java`
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/service/domainpack/LabDomainPackQueryServiceImpl.java`
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/service/domainpack/dto/LabDomainPackSnapshotDTO.java`
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/service/equipment/LabEquipmentAssetService.java`
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/service/equipment/LabEquipmentAssetServiceImpl.java`
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/service/equipment/dto/LabEquipmentAssetSummaryDTO.java`
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/service/evidenceobject/LabEvidenceObjectService.java`
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/service/evidenceobject/LabEvidenceObjectServiceImpl.java`

Create backend LIMS files:

- `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/gateway/DomainPackGateway.java`
- `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/gateway/DomainPackGatewayImpl.java`
- `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/gateway/EquipmentAssetGateway.java`
- `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/gateway/EquipmentAssetGatewayImpl.java`
- `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/model/WorkflowSnapshot.java`
- `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/WorkflowSnapshotFactory.java`
- `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/ExecutionPlanFactory.java`
- `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/ReportDraftPlanFactory.java`
- `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/dal/dataobject/workflow/LimsExecutionPlanDO.java`
- `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/dal/mysql/workflow/LimsExecutionPlanMapper.java`
- `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/controller/admin/workflow/vo/LimsTaskEquipmentBindReqVO.java`

Modify backend files:

- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/controller/admin/domainpack/LabDomainPackController.java`
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/service/domainpack/LabDomainPackService.java`
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/service/domainpack/LabDomainPackServiceImpl.java`
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/dal/mysql/domainpack/LabDomainPackMapper.java`
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/controller/admin/packconfig/vo/LabPackConfigSaveReqVO.java`
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/controller/admin/packconfig/vo/LabPackConfigRespVO.java`
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/service/packconfig/LabPackConfigServiceImpl.java`
- `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/enums/ErrorCodeConstants.java`
- `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/service/workflow/LimsWorkflowService.java`
- `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/controller/admin/workflow/LimsWorkflowController.java`
- `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/controller/admin/workflow/vo/LimsWorkflowSaveReqVO.java`
- `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/controller/admin/workflow/vo/LimsWorkflowRespVO.java`
- `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/dal/dataobject/workflow/LimsTestTaskDO.java`
- `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/dal/dataobject/workflow/LimsReportDO.java`
- `yudao-module-lims/src/main/java/cn/iocoder/yudao/module/lims/enums/ErrorCodeConstants.java`
- `sql/mysql/lab.sql`

Create/modify frontend files:

- `yudao-ui/yudao-ui-admin-vue3/src/api/lab/equipment-asset/index.ts`
- `yudao-ui/yudao-ui-admin-vue3/src/api/lab/equipment-calibration/index.ts`
- `yudao-ui/yudao-ui-admin-vue3/src/views/lab/equipment-asset/index.vue`
- `yudao-ui/yudao-ui-admin-vue3/src/api/lab/domain-pack/index.ts`
- `yudao-ui/yudao-ui-admin-vue3/src/api/lab/pack-config/index.ts`
- `yudao-ui/yudao-ui-admin-vue3/src/api/lims/workflow/index.ts`
- `yudao-ui/yudao-ui-admin-vue3/src/views/lab/domain-pack/index.vue`
- `yudao-ui/yudao-ui-admin-vue3/src/views/lab/pack-designer/index.vue`
- `yudao-ui/yudao-ui-admin-vue3/src/views/lims/task/index.vue`
- `yudao-ui/yudao-ui-admin-vue3/src/views/lims/report/index.vue`

Create tests:

- `yudao-module-lab/src/test/java/cn/iocoder/yudao/module/lab/service/domainpack/LabDomainPackServiceImplTest.java`
- `yudao-module-lab/src/test/java/cn/iocoder/yudao/module/lab/service/domainpack/LabDomainPackQueryServiceImplTest.java`
- `yudao-module-lab/src/test/java/cn/iocoder/yudao/module/lab/service/equipment/LabEquipmentAssetServiceImplTest.java`
- `yudao-module-lims/src/test/java/cn/iocoder/yudao/module/lims/service/workflow/WorkflowSnapshotFactoryTest.java`
- `yudao-module-lims/src/test/java/cn/iocoder/yudao/module/lims/service/workflow/LimsWorkflowServiceTest.java`

## Task 1: Lock DomainPack Publication Rules

**Files:**

- Create: `yudao-module-lab/src/test/java/cn/iocoder/yudao/module/lab/service/domainpack/LabDomainPackServiceImplTest.java`
- Modify: `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/service/domainpack/LabDomainPackService.java`
- Modify: `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/service/domainpack/LabDomainPackServiceImpl.java`
- Modify: `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/controller/admin/domainpack/LabDomainPackController.java`
- Modify: `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/dal/mysql/domainpack/LabDomainPackMapper.java`
- Modify: `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/enums/ErrorCodeConstants.java`

- [ ] **Step 1: Write failing tests for immutable published packs**

Test cases:

```java
@Test
void updateDomainPack_shouldRejectPublishedPack() {
    LabDomainPackDO existing = domainPack(1L, "FOOD_ROUTINE", "1.0", "published");
    when(domainPackMapper.selectById(1L)).thenReturn(existing);

    LabDomainPackSaveReqVO reqVO = new LabDomainPackSaveReqVO();
    reqVO.setId(1L);
    reqVO.setDomainId(10L);
    reqVO.setPackCode("FOOD_ROUTINE");
    reqVO.setPackName("食品常规检测方案包");
    reqVO.setPackVersion("1.0");
    reqVO.setStatus("published");

    ServiceException ex = assertThrows(ServiceException.class, () -> service.updateDomainPack(reqVO));
    assertEquals(DOMAIN_PACK_PUBLISHED_IMMUTABLE.getCode(), ex.getCode());
}

@Test
void publishDomainPack_shouldMoveDraftToPublished() {
    LabDomainPackDO existing = domainPack(1L, "FOOD_ROUTINE", "1.0", "draft");
    when(domainPackMapper.selectById(1L)).thenReturn(existing);

    service.publishDomainPack(1L);

    verify(domainPackMapper).updateById(argThat(pack -> "published".equals(pack.getStatus())));
}

@Test
void copyDomainPackVersion_shouldCreateDraftVersion() {
    LabDomainPackDO existing = domainPack(1L, "FOOD_ROUTINE", "1.0", "published");
    when(domainPackMapper.selectById(1L)).thenReturn(existing);
    when(domainPackMapper.selectByPackCodeAndVersion("FOOD_ROUTINE", "1.1")).thenReturn(null);

    Long copiedId = service.copyDomainPackVersion(1L, "1.1");

    assertNotNull(copiedId);
    verify(domainPackMapper).insert(argThat(pack ->
            "FOOD_ROUTINE".equals(pack.getPackCode())
                    && "1.1".equals(pack.getPackVersion())
                    && "draft".equals(pack.getStatus())));
}
```

- [ ] **Step 2: Run test to confirm it fails**

Run:

```bash
mvn -q -pl yudao-module-lab -Dtest=LabDomainPackServiceImplTest test
```

Expected: compile fails because lifecycle methods and error codes do not exist.

- [ ] **Step 3: Add lifecycle methods and guards**

Add service methods:

```java
void publishDomainPack(Long id);

Long copyDomainPackVersion(Long id, String targetVersion);

void archiveDomainPack(Long id);
```

Add controller endpoints:

```java
@PostMapping("/{id}/publish")
public CommonResult<Boolean> publishDomainPack(@PathVariable("id") Long id) {
    domainPackService.publishDomainPack(id);
    return success(true);
}

@PostMapping("/{id}/copy-version")
public CommonResult<Long> copyDomainPackVersion(@PathVariable("id") Long id,
                                                @RequestParam("targetVersion") String targetVersion) {
    return success(domainPackService.copyDomainPackVersion(id, targetVersion));
}

@PostMapping("/{id}/archive")
public CommonResult<Boolean> archiveDomainPack(@PathVariable("id") Long id) {
    domainPackService.archiveDomainPack(id);
    return success(true);
}
```

Add mapper method:

```java
default LabDomainPackDO selectByPackCodeAndVersion(String packCode, String packVersion) {
    return selectOne(new LambdaQueryWrapperX<LabDomainPackDO>()
            .eq(LabDomainPackDO::getPackCode, packCode)
            .eq(LabDomainPackDO::getPackVersion, packVersion));
}
```

Update uniqueness to use `packCode + packVersion`, not only `packCode`.

- [ ] **Step 4: Run tests**

Run:

```bash
mvn -q -pl yudao-module-lab -Dtest=LabDomainPackServiceImplTest test
```

Expected: pass.

- [ ] **Step 5: Commit**

```bash
git add yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/controller/admin/domainpack \
        yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/service/domainpack \
        yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/dal/mysql/domainpack \
        yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/enums/ErrorCodeConstants.java \
        yudao-module-lab/src/test/java/cn/iocoder/yudao/module/lab/service/domainpack/LabDomainPackServiceImplTest.java
git commit
```

Commit intent line: `Protect published DomainPack versions from drift`

## Task 2: Add Remaining Direction Pack Configuration Types

**Files:**

- Create: `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/dal/dataobject/packconfig/LabPackSampleRequirementDO.java`
- Create: `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/dal/dataobject/packconfig/LabPackQcRuleDO.java`
- Create: `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/dal/dataobject/packconfig/LabPackEvidenceRequirementDO.java`
- Create: `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/dal/mysql/packconfig/LabPackSampleRequirementMapper.java`
- Create: `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/dal/mysql/packconfig/LabPackQcRuleMapper.java`
- Create: `yudao-module-lab/src/main/java/cn/iocoder/yudao/module/lab/dal/mysql/packconfig/LabPackEvidenceRequirementMapper.java`
- Modify: `LabPackConfigSaveReqVO.java`, `LabPackConfigRespVO.java`, `LabPackConfigServiceImpl.java`

- [ ] **Step 1: Write failing service test**

Add to `LabPackConfigServiceImplTest` or create the test if it does not exist:

```java
@Test
void savePackConfig_shouldPersistSampleQcEvidenceConfig() {
    LabPackConfigSaveReqVO reqVO = new LabPackConfigSaveReqVO();
    reqVO.setDomainPackId(1L);
    reqVO.setSampleRequirements(List.of(sampleRequirement("SAMPLE_QTY", "样品量", ">= 500g")));
    reqVO.setQcRules(List.of(qcRule("BLANK", "空白样", "每批至少 1 个")));
    reqVO.setEvidenceRequirements(List.of(evidenceRequirement("EQUIPMENT_CERT", "设备校准证书", "equipment")));

    service.savePackConfig(reqVO);

    verify(sampleRequirementMapper).insert(argThat(item -> "SAMPLE_QTY".equals(item.getRequirementCode())));
    verify(qcRuleMapper).insert(argThat(item -> "BLANK".equals(item.getRuleCode())));
    verify(evidenceRequirementMapper).insert(argThat(item -> "EQUIPMENT_CERT".equals(item.getRequirementCode())));
}
```

- [ ] **Step 2: Run test to confirm it fails**

Run:

```bash
mvn -q -pl yudao-module-lab -Dtest=LabPackConfigServiceImplTest test
```

Expected: compile fails because the new DO/Mapper/VO fields do not exist.

- [ ] **Step 3: Implement DO and Mapper classes**

Use the existing pack config shape:

```java
@TableName("lab_pack_sample_requirement")
@KeySequence("lab_pack_sample_requirement_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class LabPackSampleRequirementDO extends TenantBaseDO {
    @TableId
    private Long id;
    private Long domainPackId;
    private String requirementCode;
    private String requirementName;
    private String requirementType;
    private String requirementText;
    private Integer sort;
    private String status;
    private String remark;
}
```

Mapper pattern:

```java
default List<LabPackSampleRequirementDO> selectListByDomainPackId(Long domainPackId) {
    return selectList(new LambdaQueryWrapperX<LabPackSampleRequirementDO>()
            .eq(LabPackSampleRequirementDO::getDomainPackId, domainPackId)
            .orderByAsc(LabPackSampleRequirementDO::getSort));
}

default void deleteByDomainPackId(Long domainPackId) {
    delete(new LambdaQueryWrapperX<LabPackSampleRequirementDO>().eq(LabPackSampleRequirementDO::getDomainPackId, domainPackId));
}
```

- [ ] **Step 4: Wire VO and service save/get paths**

Add VO lists:

```java
private List<SampleRequirement> sampleRequirements;
private List<QcRule> qcRules;
private List<EvidenceRequirement> evidenceRequirements;
```

Ensure `savePackConfig()` deletes and reinserts all seven config categories in one transaction.

- [ ] **Step 5: Guard published packs**

Before saving config, fetch the DomainPack and reject non-draft:

```java
if (!"draft".equalsIgnoreCase(domainPack.getStatus())) {
    throw exception(DOMAIN_PACK_PUBLISHED_IMMUTABLE);
}
```

- [ ] **Step 6: Run tests and commit**

Run:

```bash
mvn -q -pl yudao-module-lab -Dtest=LabPackConfigServiceImplTest test
```

Commit intent line: `Complete DomainPack configuration before publication`

## Task 3: Add LAB Query Services for Published Pack Snapshots

**Files:**

- Create: `LabDomainPackSnapshotDTO.java`
- Create: `LabDomainPackQueryService.java`
- Create: `LabDomainPackQueryServiceImpl.java`
- Create: `LabDomainPackQueryServiceImplTest.java`
- Modify: `LabDomainPackController.java`

- [ ] **Step 1: Write failing snapshot query test**

```java
@Test
void getPublishedSnapshot_shouldReturnAllConfigSections() {
    when(domainPackMapper.selectById(1L)).thenReturn(domainPack(1L, "FOOD", "1.0", "published"));
    when(workflowNodeMapper.selectListByDomainPackId(1L)).thenReturn(List.of(workflowNode("receive", "样品接收")));
    when(sampleRequirementMapper.selectListByDomainPackId(1L)).thenReturn(List.of(sampleRequirement("MIN_QTY", "最小样品量")));
    when(testItemMapper.selectListByDomainPackId(1L)).thenReturn(List.of(testItem("PH", "pH")));
    when(resultFieldMapper.selectListByDomainPackId(1L)).thenReturn(List.of(resultField("PH_VALUE", "pH值")));
    when(qcRuleMapper.selectListByDomainPackId(1L)).thenReturn(List.of(qcRule("BLANK", "空白样")));
    when(reportSectionMapper.selectListByDomainPackId(1L)).thenReturn(List.of(reportSection("RESULTS", "检测结果")));
    when(evidenceRequirementMapper.selectListByDomainPackId(1L)).thenReturn(List.of(evidenceRequirement("EQUIPMENT_CERT", "设备证书")));

    LabDomainPackSnapshotDTO snapshot = queryService.getPublishedSnapshot(1L);

    assertEquals("FOOD", snapshot.getPackCode());
    assertEquals(1, snapshot.getSampleRequirements().size());
    assertEquals(1, snapshot.getQcRules().size());
    assertEquals(1, snapshot.getEvidenceRequirements().size());
}
```

- [ ] **Step 2: Implement query service**

Rules:

- Only `published` packs are accepted.
- Return a DTO with no Mapper or DO types exposed to LIMS.
- Include `workflowSchema` and `templateSchema` as compatibility fields.
- Include structured config lists as source of truth.

- [ ] **Step 3: Add published snapshot controller endpoint**

```java
@GetMapping("/{id}/published-snapshot")
public CommonResult<LabDomainPackSnapshotDTO> getPublishedSnapshot(@PathVariable("id") Long id) {
    return success(domainPackQueryService.getPublishedSnapshot(id));
}
```

- [ ] **Step 4: Run tests and commit**

Run:

```bash
mvn -q -pl yudao-module-lab -Dtest=LabDomainPackQueryServiceImplTest test
```

Commit intent line: `Expose published DomainPack snapshots through LAB services`

## Task 4: Introduce DomainPackGateway and WorkflowSnapshotFactory in LIMS

**Files:**

- Create: `DomainPackGateway.java`
- Create: `DomainPackGatewayImpl.java`
- Create: `WorkflowSnapshot.java`
- Create: `WorkflowSnapshotFactory.java`
- Create: `WorkflowSnapshotFactoryTest.java`
- Modify: `LimsWorkflowService.java`
- Modify: `LimsWorkflowSaveReqVO.java`, `LimsWorkflowRespVO.java`

- [ ] **Step 1: Write failing factory test**

```java
@Test
void createSnapshot_shouldContainPackVersionAndAllConfigSections() {
    LabDomainPackSnapshotDTO pack = publishedPackSnapshot();

    WorkflowSnapshot snapshot = factory.createSnapshot(pack, "{\"channel\":\"third_party_order\"}");

    JsonNode json = objectMapper.readTree(snapshot.getSnapshotJson());
    assertEquals("FOOD_ROUTINE", json.path("packCode").asText());
    assertEquals("1.0", json.path("packVersion").asText());
    assertTrue(json.path("sampleRequirements").isArray());
    assertTrue(json.path("qcRules").isArray());
    assertTrue(json.path("evidenceRequirements").isArray());
    assertTrue(json.path("reportSections").isArray());
    assertEquals(snapshot.getSnapshotHash(), sha256(snapshot.getSnapshotJson()));
}
```

- [ ] **Step 2: Implement gateway and factory**

Gateway:

```java
public interface DomainPackGateway {
    LabDomainPackSnapshotDTO getPublishedPackSnapshot(Long domainPackId);
}
```

Implementation:

```java
@Service
public class DomainPackGatewayImpl implements DomainPackGateway {
    @Resource
    private LabDomainPackQueryService labDomainPackQueryService;

    @Override
    public LabDomainPackSnapshotDTO getPublishedPackSnapshot(Long domainPackId) {
        return labDomainPackQueryService.getPublishedSnapshot(domainPackId);
    }
}
```

Factory:

```java
public WorkflowSnapshot createSnapshot(LabDomainPackSnapshotDTO pack, String overrideConfig) {
    ObjectNode root = objectMapper.createObjectNode();
    root.put("domainPackId", pack.getDomainPackId());
    root.put("packCode", pack.getPackCode());
    root.put("packName", pack.getPackName());
    root.put("packVersion", pack.getPackVersion());
    root.put("industry", pack.getIndustry());
    root.put("frozenAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    root.set("workflowNodes", objectMapper.valueToTree(pack.getWorkflowNodes()));
    root.set("sampleRequirements", objectMapper.valueToTree(pack.getSampleRequirements()));
    root.set("testItems", objectMapper.valueToTree(pack.getTestItems()));
    root.set("resultFields", objectMapper.valueToTree(pack.getResultFields()));
    root.set("qcRules", objectMapper.valueToTree(pack.getQcRules()));
    root.set("reportSections", objectMapper.valueToTree(pack.getReportSections()));
    root.set("evidenceRequirements", objectMapper.valueToTree(pack.getEvidenceRequirements()));
    if (StringUtils.hasText(overrideConfig)) {
        root.set("override", readObject(overrideConfig));
    }
    String json = root.toString();
    return new WorkflowSnapshot(json, sha256(json), LocalDateTime.now(), pack.getDomainPackId(), pack.getPackCode(), pack.getPackVersion());
}
```

- [ ] **Step 3: Refactor `LimsWorkflowService`**

Replace:

```java
@Resource
private LabDomainPackMapper domainPackMapper;
```

with:

```java
@Resource
private DomainPackGateway domainPackGateway;
@Resource
private WorkflowSnapshotFactory workflowSnapshotFactory;
```

Create requests by reading `domainPackGateway.getPublishedPackSnapshot(domainPackId)` and freezing the returned `WorkflowSnapshot`.

- [ ] **Step 4: Architecture guard**

Run:

```bash
rg -n "LabDomainPackMapper|dal\\.mysql\\.domainpack" yudao-module-lims/src/main/java
```

Expected: no matches.

- [ ] **Step 5: Run tests and commit**

Run:

```bash
mvn -q -pl yudao-module-lims -Dtest=WorkflowSnapshotFactoryTest test
```

Commit intent line: `Route LIMS DomainPack access through a gateway`

## Task 5: Generate ExecutionPlan and ReportDraftPlan from Frozen Snapshot

**Files:**

- Create: `ExecutionPlanFactory.java`
- Create: `ReportDraftPlanFactory.java`
- Create: `LimsExecutionPlanDO.java`
- Create: `LimsExecutionPlanMapper.java`
- Modify: `LimsWorkflowService.java`
- Modify: `LimsWorkflowController.java`
- Modify: `LimsWorkflowRespVO.java`

- [ ] **Step 1: Write failing execution plan test**

```java
@Test
void generateTasks_shouldPersistExecutionPlanWithSampleQcEvidenceAndReportDraft() {
    LimsTestRequestDO request = requestWithWorkflowSnapshot(snapshotWithAllSections());
    when(requestMapper.selectById(1L)).thenReturn(request);
    when(sampleMapper.selectListByRequestId(1L)).thenReturn(List.of(sample("REQ-1-S01")));

    Long created = workflowService.generateTasks(1L);

    assertEquals(1L, created);
    verify(executionPlanMapper).insert(argThat(plan ->
            plan.getPlanJson().contains("sampleRequirements")
                    && plan.getPlanJson().contains("qcCheckPlans")
                    && plan.getPlanJson().contains("evidenceRequirementPlans")
                    && plan.getPlanJson().contains("reportDraftPlan")));
}
```

- [ ] **Step 2: Implement factories**

`ExecutionPlanFactory` input is `workflowSnapshotJson`; output is JSON with:

```json
{
  "sampleRequirements": [],
  "taskPlans": [],
  "resultFieldPlans": [],
  "qcCheckPlans": [],
  "evidenceRequirementPlans": [],
  "reportDraftPlan": {}
}
```

`ReportDraftPlanFactory` builds:

```json
{
  "templateId": null,
  "templateVersion": "",
  "outputFormats": ["WORD", "PDF", "EXCEL"],
  "sections": [],
  "dataBindings": [],
  "evidenceRequirements": []
}
```

- [ ] **Step 3: Persist plan and keep existing task creation**

`generateTasks()` must:

1. Read frozen snapshot from the request.
2. Build execution plan.
3. Insert or update `lims_execution_plan`.
4. Generate tasks from `taskPlans`.
5. Preserve existing fallback items when old data lacks structured config.

- [ ] **Step 4: Add endpoint alias**

Add:

```java
@PostMapping("/lims/request/generate-execution-plan")
public CommonResult<Long> generateExecutionPlan(@RequestParam("id") Long id) {
    return success(workflowService.generateTasks(id));
}
```

- [ ] **Step 5: Run tests and commit**

Run:

```bash
mvn -q -pl yudao-module-lims -Dtest=LimsWorkflowServiceTest test
```

Commit intent line: `Derive LIMS execution plans from frozen DomainPack snapshots`

## Task 6: Add EquipmentAsset Master Data and Available Query

**Files:**

- Create equipment asset controller, VOs, DO, mapper, service, tests listed in Target File Structure.
- Modify: `LabEquipmentTraceabilityDO.java` only if a relationship field must be aligned.
- Modify: `ErrorCodeConstants.java`

- [ ] **Step 1: Write failing available-equipment tests**

```java
@Test
void getAvailableEquipment_shouldExcludeExpiredCalibration() {
    LabEquipmentAssetDO expired = equipment("EQ-OLD", "expired", LocalDate.now().minusDays(1).toString(), "FOOD");
    when(equipmentAssetMapper.selectListByStatus("enabled")).thenReturn(List.of(expired));

    List<LabEquipmentAssetSummaryDTO> available = service.getAvailableEquipment("FOOD", "pH");

    assertTrue(available.isEmpty());
}

@Test
void createEquipmentAsset_shouldDefaultDraftToEnabledWhenCalibrationValid() {
    LabEquipmentAssetSaveReqVO reqVO = validEquipmentSaveReq();

    Long id = service.createEquipmentAsset(reqVO);

    assertNotNull(id);
    verify(equipmentAssetMapper).insert(argThat(asset ->
            "恒温培养箱".equals(asset.getEquipmentName())
                    && "enabled".equals(asset.getStatus())));
}
```

- [ ] **Step 2: Implement EquipmentAsset model**

Required fields:

```java
private String equipmentCode;
private String equipmentName;
private String equipmentType;
private String manufacturer;
private String model;
private String serialNo;
private String labArea;
private String domainCode;
private String capabilityScope;
private Long responsibleUserId;
private String calibrationValidUntil;
private String status;
private Boolean iotEnabled;
private Long iotProductId;
private Long iotDeviceId;
private String dataSourceType;
private String remark;
```

- [ ] **Step 3: Implement controller**

Endpoints:

```text
GET /lab/equipment-asset/page
GET /lab/equipment-asset/get
POST /lab/equipment-asset/create
PUT /lab/equipment-asset/update
DELETE /lab/equipment-asset/delete
GET /lab/equipment-asset/available
```

- [ ] **Step 4: Run tests and commit**

Run:

```bash
mvn -q -pl yudao-module-lab -Dtest=LabEquipmentAssetServiceImplTest test
```

Commit intent line: `Make equipment assets the master record`

## Task 7: Create Equipment Calibration Evidence Objects

**Files:**

- Create calibration controller/VO/DO/mapper/service methods.
- Create evidence object DO/mapper/service.
- Modify `LabEquipmentAssetServiceImpl.java`.
- Modify `LabEvidenceLinkDO.java` and `LabEvidenceLinkSaveReqVO.java` to include `evidenceObjectId`, `clauseId`, `capabilityScopeId`, `linkReason`, `verifiedBy`, `verifiedAt` when supported by SQL.

- [ ] **Step 1: Write failing evidence test**

```java
@Test
void createCalibration_shouldCreateEvidenceObjectAndRefreshEquipmentStatus() {
    LabEquipmentCalibrationSaveReqVO reqVO = calibrationReq("EQ-INC-001", "CAL-2026-001", "2027-06-30");

    Long calibrationId = service.createCalibrationRecord(reqVO);

    assertNotNull(calibrationId);
    verify(evidenceObjectMapper).insert(argThat(evidence ->
            "EQUIPMENT_CERTIFICATE".equals(evidence.getObjectType())
                    && "CAL-2026-001".equals(evidence.getSourceNo())
                    && StringUtils.hasText(evidence.getFileHash())));
    verify(equipmentAssetMapper).updateById(argThat(asset -> "enabled".equals(asset.getStatus())));
}
```

- [ ] **Step 2: Implement evidence object creation**

Hash input:

```text
equipmentCode + "|" + certificateNo + "|" + validTo + "|" + fileUrl
```

Evidence object fields:

```java
private String evidenceNo;
private String objectType;
private String sourceType;
private Long sourceId;
private String sourceNo;
private String fileUrl;
private String fileHash;
private String metadataJson;
private Boolean archived;
private String status;
```

- [ ] **Step 3: Add clause link endpoint**

Endpoint:

```text
POST /lab/equipment-evidence/link-clause
```

Use existing `LabEvidenceLinkService` where possible; create a link from `EvidenceObject` to CNAS equipment clause with `clauseCategory = equipment`.

- [ ] **Step 4: Run tests and commit**

Run:

```bash
mvn -q -pl yudao-module-lab -Dtest=LabEquipmentAssetServiceImplTest test
```

Commit intent line: `Turn equipment certificates into evidence objects`

## Task 8: Bind Equipment to LIMS Tasks

**Files:**

- Create: `LimsTaskEquipmentBindReqVO.java`
- Create: `EquipmentAssetGateway.java`, `EquipmentAssetGatewayImpl.java`
- Modify: `LimsTestTaskDO.java`
- Modify: `LimsWorkflowSaveReqVO.java`, `LimsWorkflowRespVO.java`
- Modify: `LimsWorkflowService.java`
- Modify: `LimsWorkflowController.java`
- Modify: `ErrorCodeConstants.java`

- [ ] **Step 1: Write failing task binding tests**

```java
@Test
void bindEquipmentToTask_shouldRejectExpiredEquipment() {
    LimsTestTaskDO task = task("FOOD", "pH");
    when(taskMapper.selectById(10L)).thenReturn(task);
    when(equipmentAssetGateway.getAvailableEquipment("FOOD", "pH")).thenReturn(List.of());

    LimsTaskEquipmentBindReqVO reqVO = new LimsTaskEquipmentBindReqVO();
    reqVO.setTaskId(10L);
    reqVO.setEquipmentAssetId(99L);

    ServiceException ex = assertThrows(ServiceException.class, () -> workflowService.bindTaskEquipment(reqVO));
    assertEquals(EQUIPMENT_NOT_AVAILABLE.getCode(), ex.getCode());
}

@Test
void bindEquipmentToTask_shouldFreezeEquipmentSnapshot() {
    LimsTestTaskDO task = task("FOOD", "pH");
    LabEquipmentAssetSummaryDTO equipment = equipmentSummary(99L, "EQ-INC-001", "恒温培养箱");
    when(taskMapper.selectById(10L)).thenReturn(task);
    when(equipmentAssetGateway.getEquipmentSummary(99L)).thenReturn(equipment);
    when(equipmentAssetGateway.isEquipmentAvailableForTask(99L, "FOOD", "pH")).thenReturn(true);

    workflowService.bindTaskEquipment(bindReq(10L, 99L));

    verify(taskMapper).updateById(argThat(updated ->
            Long.valueOf(99L).equals(updated.getEquipmentAssetId())
                    && updated.getEquipmentSnapshot().contains("EQ-INC-001")));
}
```

- [ ] **Step 2: Implement fields and service**

Add task fields:

```java
private Long equipmentAssetId;
private String equipmentCode;
private String equipmentName;
private String equipmentSnapshot;
```

Add endpoint:

```java
@PostMapping("/lims/task/bind-equipment")
public CommonResult<Boolean> bindTaskEquipment(@Valid @RequestBody LimsTaskEquipmentBindReqVO reqVO) {
    workflowService.bindTaskEquipment(reqVO);
    return success(true);
}
```

- [ ] **Step 3: Run tests and commit**

Run:

```bash
mvn -q -pl yudao-module-lims -Dtest=LimsWorkflowServiceTest test
```

Commit intent line: `Freeze equipment snapshots on LIMS tasks`

## Task 9: Include Equipment Evidence in Report Snapshot

**Files:**

- Modify: `LimsWorkflowService.java`
- Modify: `LimsReportDO.java` if `reportDraftPlan` needs a column.
- Modify: `LimsWorkflowRespVO.java`
- Modify: `LimsWorkflowServiceTest.java`

- [ ] **Step 1: Write failing report snapshot test**

```java
@Test
void generateReport_shouldIncludeEquipmentAndEvidenceSummary() {
    when(requestMapper.selectById(1L)).thenReturn(requestWithWorkflowSnapshot(snapshotWithEvidenceRequirements()));
    when(reportMapper.selectByRequestId(1L)).thenReturn(null);
    when(resultMapper.selectListByRequestId(1L)).thenReturn(List.of(result("pH", "合格")));
    when(taskMapper.selectListByRequestId(1L)).thenReturn(List.of(taskWithEquipmentSnapshot()));

    Long reportId = workflowService.generateReport(1L);

    assertNotNull(reportId);
    verify(reportMapper).insert(argThat(report ->
            report.getDataSnapshot().contains("equipmentSummary")
                    && report.getDataSnapshot().contains("equipmentEvidenceSummary")
                    && report.getDataSnapshot().contains("reportDraftPlan")));
}
```

- [ ] **Step 2: Extend report snapshot**

`buildReportContent()` must include:

```json
{
  "reportDraftPlan": {},
  "equipmentSummary": [],
  "equipmentEvidenceSummary": []
}
```

Evidence summary comes from the frozen task `equipmentSnapshot` first. If evidence objects are present in gateway summary, include them; otherwise include an empty array with the task and equipment identifiers.

- [ ] **Step 3: Run tests and commit**

Run:

```bash
mvn -q -pl yudao-module-lims -Dtest=LimsWorkflowServiceTest test
```

Commit intent line: `Carry equipment evidence into report snapshots`

## Task 10: Add SQL Migration Coverage

**Files:**

- Modify: `sql/mysql/lab.sql`

- [ ] **Step 1: Add additive DDL**

Add tables:

```sql
CREATE TABLE IF NOT EXISTS `lab_equipment_asset` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `equipment_code` varchar(64) NOT NULL COMMENT '设备编码',
  `equipment_name` varchar(128) NOT NULL COMMENT '设备名称',
  `equipment_type` varchar(64) NULL DEFAULT NULL COMMENT '设备类型',
  `manufacturer` varchar(128) NULL DEFAULT NULL COMMENT '制造商',
  `model` varchar(128) NULL DEFAULT NULL COMMENT '型号',
  `serial_no` varchar(128) NULL DEFAULT NULL COMMENT '出厂编号',
  `lab_area` varchar(128) NULL DEFAULT NULL COMMENT '实验室区域',
  `domain_code` varchar(64) NULL DEFAULT NULL COMMENT '检测方向编码',
  `capability_scope` varchar(512) NULL DEFAULT NULL COMMENT '能力范围',
  `responsible_user_id` bigint NULL DEFAULT NULL COMMENT '负责人',
  `calibration_valid_until` varchar(32) NULL DEFAULT NULL COMMENT '校准有效期',
  `status` varchar(32) NOT NULL DEFAULT 'draft' COMMENT '状态',
  `iot_enabled` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否启用物联',
  `iot_product_id` bigint NULL DEFAULT NULL COMMENT 'IoT 产品编号',
  `iot_device_id` bigint NULL DEFAULT NULL COMMENT 'IoT 设备编号',
  `data_source_type` varchar(64) NULL DEFAULT NULL COMMENT '数据来源类型',
  `remark` varchar(512) NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_lab_equipment_asset_code_tenant_deleted` (`equipment_code`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '设备主档';

CREATE TABLE IF NOT EXISTS `lab_equipment_calibration_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `equipment_asset_id` bigint NOT NULL COMMENT '设备主档编号',
  `equipment_code` varchar(64) NOT NULL COMMENT '设备编码',
  `certificate_no` varchar(128) NOT NULL COMMENT '证书编号',
  `calibration_org` varchar(128) NULL DEFAULT NULL COMMENT '校准机构',
  `calibration_date` varchar(32) NULL DEFAULT NULL COMMENT '校准日期',
  `valid_to` varchar(32) NULL DEFAULT NULL COMMENT '有效期至',
  `result` varchar(64) NULL DEFAULT NULL COMMENT '校准结果',
  `certificate_file_url` varchar(512) NULL DEFAULT NULL COMMENT '证书文件地址',
  `evidence_object_id` bigint NULL DEFAULT NULL COMMENT '证据对象编号',
  `status` varchar(32) NOT NULL DEFAULT 'valid' COMMENT '状态',
  `creator` varchar(64) NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lab_equipment_calibration_asset` (`equipment_asset_id`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '设备校准记录';

CREATE TABLE IF NOT EXISTS `lab_evidence_object` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `evidence_no` varchar(64) NOT NULL COMMENT '证据编号',
  `object_type` varchar(64) NOT NULL COMMENT '证据对象类型',
  `source_type` varchar(128) NOT NULL COMMENT '来源类型',
  `source_id` bigint NULL DEFAULT NULL COMMENT '来源编号',
  `source_no` varchar(128) NULL DEFAULT NULL COMMENT '来源单号',
  `file_url` varchar(512) NULL DEFAULT NULL COMMENT '文件地址',
  `file_hash` varchar(128) NULL DEFAULT NULL COMMENT '文件哈希',
  `metadata_json` json NULL COMMENT '元数据',
  `archived` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否归档',
  `status` varchar(32) NOT NULL DEFAULT 'active' COMMENT '状态',
  `creator` varchar(64) NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_lab_evidence_object_no_tenant_deleted` (`evidence_no`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '证据对象';

CREATE TABLE IF NOT EXISTS `lims_execution_plan` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `request_id` bigint NOT NULL COMMENT '检测需求编号',
  `workflow_snapshot_hash` varchar(128) NOT NULL COMMENT '流程快照哈希',
  `plan_json` json NOT NULL COMMENT '执行计划 JSON',
  `status` varchar(32) NOT NULL DEFAULT 'generated' COMMENT '状态',
  `creator` varchar(64) NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lims_execution_plan_request` (`request_id`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'LIMS执行计划';
```

Add idempotent column migration:

```sql
ALTER TABLE `lims_test_task`
  ADD COLUMN `equipment_asset_id` bigint NULL DEFAULT NULL COMMENT '设备主档编号',
  ADD COLUMN `equipment_code` varchar(64) NULL DEFAULT NULL COMMENT '设备编码',
  ADD COLUMN `equipment_name` varchar(128) NULL DEFAULT NULL COMMENT '设备名称',
  ADD COLUMN `equipment_snapshot` json NULL COMMENT '设备绑定快照';
```

For MySQL idempotence, wrap column additions using `information_schema.columns` and prepared statements if the current script style requires re-runnability.

- [ ] **Step 2: Add menu and permission data**

Add:

```text
设备台账 -> lab/equipment-asset/index -> lab:equipment-asset:query
设备台账新增 -> lab:equipment-asset:create
设备台账修改 -> lab:equipment-asset:update
设备台账删除 -> lab:equipment-asset:delete
设备校准登记 -> lab:equipment-calibration:create
任务绑定设备 -> lims:task:update
```

- [ ] **Step 3: Static SQL check**

Run:

```bash
rg -n "lab_equipment_asset|lab_equipment_calibration_record|lab_evidence_object|lims_execution_plan|equipment_snapshot" sql/mysql/lab.sql
```

Expected: each keyword appears in DDL and menu/permission or column areas.

- [ ] **Step 4: Commit**

Commit intent line: `Add first-batch TIC LIMS schema changes`

## Task 11: Update Vue3 Admin UI

**Files:**

- Create: `src/api/lab/equipment-asset/index.ts`
- Create: `src/api/lab/equipment-calibration/index.ts`
- Create: `src/views/lab/equipment-asset/index.vue`
- Modify: `src/api/lab/domain-pack/index.ts`
- Modify: `src/api/lab/pack-config/index.ts`
- Modify: `src/api/lims/workflow/index.ts`
- Modify: `src/views/lab/domain-pack/index.vue`
- Modify: `src/views/lab/pack-designer/index.vue`
- Modify: `src/views/lims/task/index.vue`
- Modify: `src/views/lims/report/index.vue`

All paths above are under `yudao-ui/yudao-ui-admin-vue3`.

- [ ] **Step 1: Add frontend API methods**

DomainPack:

```ts
publishDomainPack: async (id: number) => request.post({ url: `/lab/domain-pack/${id}/publish` }),
copyDomainPackVersion: async (id: number, targetVersion: string) =>
  request.post({ url: `/lab/domain-pack/${id}/copy-version`, params: { targetVersion } }),
archiveDomainPack: async (id: number) => request.post({ url: `/lab/domain-pack/${id}/archive` }),
getPublishedSnapshot: async (id: number) =>
  request.get({ url: `/lab/domain-pack/${id}/published-snapshot` })
```

Equipment:

```ts
getEquipmentAssetPage(params)
getEquipmentAsset(id)
createEquipmentAsset(data)
updateEquipmentAsset(data)
deleteEquipmentAsset(id)
getAvailableEquipment(params)
createCalibration(data)
```

LIMS:

```ts
bindEquipment: async (data) => request.post({ url: '/lims/task/bind-equipment', data })
generateExecutionPlan: async (id: number) =>
  request.post({ url: '/lims/request/generate-execution-plan', params: { id } })
```

- [ ] **Step 2: DomainPack page**

Chinese visible labels:

- `发布`
- `复制版本`
- `归档`
- `已发布不可编辑`
- status labels `草稿`, `已发布`, `已归档`

Disable edit form fields when `status === 'published' || status === 'archived'`.

- [ ] **Step 3: Pack designer**

Add tabs:

- `样品要求`
- `质控规则`
- `证据要求`

Save payload must include `sampleRequirements`, `qcRules`, and `evidenceRequirements`.

- [ ] **Step 4: Equipment ledger page**

Page title: `设备台账`

Table columns:

- `设备编码`
- `设备名称`
- `设备类型`
- `能力范围`
- `校准有效期`
- `状态`
- `物联设备`

Actions:

- `新增`
- `编辑`
- `登记校准`
- `刷新`

- [ ] **Step 5: LIMS task page**

Show fields:

- `设备编码`
- `设备名称`
- `设备快照`

Action:

- `绑定设备`

- [ ] **Step 6: Report page**

Show:

- `报告草稿计划`
- `设备证据摘要`

- [ ] **Step 7: Frontend typecheck**

Run:

```bash
cd yudao-ui/yudao-ui-admin-vue3
pnpm ts:check
```

Expected: pass.

- [ ] **Step 8: Commit**

Commit intent line: `Expose first-batch TIC LIMS workflows in the admin UI`

## Task 12: End-to-End Verification and Drift Review

**Files:**

- Create: `docs/superpowers/verification/2026-06-14-tic-lims-first-batch.md`
- Modify: `STATUS.md` if it exists in this checkout.

- [ ] **Step 1: Backend compile**

Run:

```bash
mvn -q -pl yudao-module-lab -am -DskipTests compile
mvn -q -pl yudao-module-lims -am -DskipTests compile
mvn -q -pl yudao-server -am -DskipTests compile
```

Expected: all pass.

- [ ] **Step 2: Unit tests**

Run:

```bash
mvn -q -pl yudao-module-lab -Dtest=LabDomainPackServiceImplTest,LabDomainPackQueryServiceImplTest,LabEquipmentAssetServiceImplTest test
mvn -q -pl yudao-module-lims -Dtest=WorkflowSnapshotFactoryTest,LimsWorkflowServiceTest test
```

Expected: all pass.

- [ ] **Step 3: Architecture drift checks**

Run:

```bash
rg -n "LabDomainPackMapper|dal\\.mysql\\.domainpack" yudao-module-lims/src/main/java
rg -n "buildWorkflowSnapshot\\(|private String buildWorkflowSnapshot" yudao-module-lims/src/main/java
rg -n "sampleRequirements|qcRules|evidenceRequirements|reportDraftPlan" yudao-module-lims/src/main/java yudao-module-lab/src/main/java
```

Expected:

- First command returns no matches.
- Second command returns no old private snapshot builder in `LimsWorkflowService`.
- Third command finds snapshot/execution/report plan code in factory and service paths.

- [ ] **Step 4: Frontend build or typecheck**

Run:

```bash
cd yudao-ui/yudao-ui-admin-vue3
pnpm ts:check
```

Expected: pass.

- [ ] **Step 5: Browser verification**

Start backend and frontend using the existing project run path. Use the browser to capture:

- `设备台账` page showing at least one `恒温培养箱`.
- `检测方案包` page showing `草稿/已发布/已归档` lifecycle actions.
- `检测方向包设计器` showing `样品要求/质控规则/证据要求`.
- `检测任务` page showing `绑定设备` and frozen equipment fields.
- `检测报告` page showing `设备证据摘要`.

Save screenshots under:

```text
docs/superpowers/verification/screenshots/2026-06-14-first-batch/
```

- [ ] **Step 6: Verification report**

Write:

```markdown
# TIC LIMS First Batch Verification

## Scope

## Commands

## Browser Screenshots

## Architecture Drift Review

## Remaining Risks
```

- [ ] **Step 7: Commit and push**

Commit intent line: `Verify the TIC LIMS first-batch closed loop`

Then:

```bash
git push origin master-jdk17
```

## Final Acceptance Checklist

- [ ] Published DomainPack cannot be updated in place.
- [ ] New DomainPack version is created by copy into draft.
- [ ] LIMS has no direct `LabDomainPackMapper` dependency.
- [ ] `WorkflowSnapshotFactory` creates stable hashes.
- [ ] `ExecutionPlan` includes sample requirements, task plans, result fields, QC checks, evidence requirements, and report draft plan.
- [ ] EquipmentAsset exists and has available-equipment filtering.
- [ ] Calibration certificate creates an EvidenceObject.
- [ ] LIMS task can bind an available equipment asset and freeze its snapshot.
- [ ] Report data snapshot includes DomainPack version, task/result data, equipment summary, and equipment evidence summary.
- [ ] Vue3 pages expose Chinese-visible flows for DomainPack lifecycle, pack designer remaining config, equipment ledger, task binding, and report evidence summary.
- [ ] Backend compile and focused tests pass.
- [ ] Frontend typecheck or build passes.
- [ ] Browser screenshots prove the end-to-end first-batch product flow.
