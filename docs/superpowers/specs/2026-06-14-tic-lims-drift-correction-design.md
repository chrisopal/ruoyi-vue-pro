# TIC LIMS 当前实现漂移纠偏设计

日期：2026-06-14
状态：Draft for implementation planning
适用仓库：`ruoyi-vue-pro`

## 1. 目标

当前实现已经具备 TIC LIMS 的基础骨架：

- `yudao-module-lab` 承载检测方向包、方向包配置、标准条款、报告模板、证据链接和部分质量记录。
- `yudao-module-lims` 承载检测需求、样品、任务、结果字段实例和报告。
- 管理端已有方向包设计器、LIMS 执行页面和报告页面。
- 检测需求创建时已经冻结 `WorkflowSnapshot`，报告生成时已经保存 `dataSnapshot` 和哈希。

本设计用于纠偏两个方向：

1. 设备和证据链侧：补齐一期设计要求的 `EquipmentAsset` 主档，把现有设备溯源和期间核查从散点记录收敛到设备聚合下，并跑通设备证据链最小闭环。
2. 方向包和 LIMS 执行侧：把“通用检测方向包设计器 + 不同检测场景配置 + 检测需求到报告闭环”从 MVP 骨架提升到架构约束明确的第一阶段：方向包不可变发布、`WorkflowSnapshotFactory`、`DomainPackGateway`、`ExecutionPlan` 和 `ReportDraftPlan`。

## 2. 非目标

本批不做以下内容：

- 不重建仓库，不重写 LAB/LIMS 模块。
- 不实现 MQTT、Modbus、TCP 等真实物联协议。
- 不实现 AI 标准问答、AI 报告解读或实验数据大模型分析。
- 不实现像素级报告设计器。
- 不完整实现商业订单、合同、报价、收款、发票。
- 不拆微服务。继续按模块化单体演进。

## 3. 当前偏移判断

| 领域 | 当前状态 | 结论 | 纠偏动作 |
| --- | --- | --- | --- |
| 检测方向包 | 已有 `lab_domain_pack`、流程节点、检测项目、结果字段、报告章节配置 | 方向正确，但发布和版本不可变不足 | 增加 `draft -> published -> archived` 约束，发布后不能原地改 |
| WorkflowSnapshot | 已在 `lims_test_request` 保存 JSON、哈希、冻结时间 | 方向正确，但逻辑在 `LimsWorkflowService` 内 | 抽出 `WorkflowSnapshotFactory` |
| LAB/LIMS 边界 | LIMS 直接注入 `LabDomainPackMapper` | 违反边界抽象 | 增加 `DomainPackGateway`，由 LAB 查询服务提供只读快照 |
| ExecutionPlan | 任务生成能读取方向包 `testItems` | 只覆盖任务，未覆盖样品要求、质控、证据、报告计划 | 增加执行计划模型和生成器 |
| ReportPlan | `lims_report` 可生成内容和数据快照 | 报告中心仍是 MVP | 增加 `ReportDraftPlan`，先不做完整输出渲染 |
| 设备管理 | 只有溯源和期间核查记录 | 缺一期核心聚合 `EquipmentAsset` | 新增设备主档和设备能力、状态、证据关系 |
| 证据链 | 已有 `lab_evidence_link` | 偏链接，缺证据对象 | 增加 `lab_evidence_object` 最小模型 |

## 4. 目标架构

### 4.1 边界上下文

本批保持现有物理模块，按逻辑上下文收敛职责：

| 上下文 | 物理模块 | 职责 |
| --- | --- | --- |
| Domain Pack Context | `yudao-module-lab` | 方向包草稿、配置、发布、归档、版本快照 |
| Equipment Management Context | `yudao-module-lab` | 设备主档、校准、期间核查、维护、能力范围、状态 |
| Evidence Chain Context | `yudao-module-lab` | 证据对象、证据链接、条款映射 |
| LIMS Execution Context | `yudao-module-lims` | 检测需求、样品、任务、结果、执行计划 |
| Report Execution Context | `yudao-module-lims` | 报告草稿计划、报告数据快照、签发状态 |

跨模块规则：

- LIMS 可以读取 LAB 的已发布方向包快照。
- LIMS 可以读取 LAB 的可用设备摘要。
- LIMS 不直接访问 LAB Mapper。
- LIMS 不修改 LAB 主数据。
- LAB 不修改 LIMS 执行状态。

### 4.2 关键流程

```text
DomainPack draft
  -> 配置 workflowNodes / sampleRequirements / testItems / resultFields / qcRules / reportSections / evidenceRequirements
  -> publish
  -> DomainPackGateway 查询已发布版本
  -> WorkflowSnapshotFactory 冻结快照
  -> ExecutionPlanFactory 生成执行计划
  -> TestTask + ResultFieldInstance + EvidenceRequirementTodo
  -> ReportDraftPlanFactory 生成报告草稿计划
  -> 结果审核
  -> 报告生成和签发
  -> EvidenceObject / EvidenceLink
```

## 5. 方向包发布与不可变版本

### 5.1 状态机

`lab_domain_pack.status` 使用以下状态：

```text
draft
published
archived
```

规则：

- `draft` 可编辑配置。
- `published` 不允许修改基础信息和配置项。
- `published` 可复制为新 `draft` 版本。
- `archived` 不可用于新检测需求，但历史检测需求继续可追溯。
- 创建检测需求只能选择 `published`，兼容期可允许 `active`，但新实现应统一为 `published`。

### 5.2 唯一性

方向包使用版本行模型：

```text
pack_code + pack_version + tenant_id
```

作为业务唯一键。一个行业方向的不同版本通过多行表达，不覆盖历史版本。

### 5.3 配置范围

已有配置继续保留：

- `lab_pack_workflow_node`
- `lab_pack_test_item`
- `lab_pack_result_field`
- `lab_pack_report_section`

补齐配置：

- `lab_pack_sample_requirement`
- `lab_pack_qc_rule`
- `lab_pack_evidence_requirement`

`workflow_schema` 和 `template_schema` 继续作为兼容字段和缓存字段，不作为唯一事实来源。发布时由结构化配置生成发布快照。

## 6. WorkflowSnapshotFactory 和 DomainPackGateway

### 6.1 DomainPackGateway

在 `yudao-module-lims` 定义网关接口：

```text
DomainPackGateway
  getPublishedPackSnapshot(domainPackId)
```

返回 LIMS 只读 DTO：

```text
DomainPackSnapshotDTO
  domainPackId
  packCode
  packName
  packVersion
  industry
  workflowNodes
  sampleRequirements
  testItems
  resultFields
  qcRules
  reportSections
  evidenceRequirements
  templateRequirements
```

在 `yudao-module-lab` 暴露查询服务：

```text
LabDomainPackQueryService
  getPublishedSnapshot(domainPackId)
```

LIMS 的网关实现调用 LAB 查询服务，不再直接注入 `LabDomainPackMapper`。

### 6.2 WorkflowSnapshotFactory

`WorkflowSnapshotFactory` 只负责一件事：把已发布方向包快照、检测需求来源和可选覆盖配置组装成不可变快照。

输出：

```text
WorkflowSnapshot
  snapshotJson
  snapshotHash
  frozenAt
  domainPackId
  packCode
  packVersion
```

规则：

- 输入必须来自已发布方向包。
- 快照一旦写入检测需求，不允许因方向包后续版本变化而改变。
- 更新检测需求时不得更换 `domainPackId`。
- 快照 JSON 必须包含报告章节、证据要求、质控规则和结果字段。

## 7. ExecutionPlan 和 ReportDraftPlan

### 7.1 ExecutionPlan

`ExecutionPlan` 是从 `WorkflowSnapshot` 派生的执行计划。第一批可以先持久化为 JSON 快照，后续再拆成更细表。

建议表：

```text
lims_execution_plan
  id
  request_id
  workflow_snapshot_hash
  plan_json
  status
```

`plan_json` 必须包含：

- `sampleRequirements`
- `taskPlans`
- `resultFieldPlans`
- `qcCheckPlans`
- `evidenceRequirementPlans`
- `reportDraftPlan`

生成规则：

- `sampleRequirements` 生成样品接收要求。
- `testItems` 生成检测任务。
- `resultFields` 生成结果字段实例计划。
- `qcRules` 生成质控检查点。
- `evidenceRequirements` 生成证据待办。
- `reportSections` 生成报告草稿章节计划。

### 7.2 ReportDraftPlan

`ReportDraftPlan` 是报告中心的第一阶段落点，不要求马上实现完整渲染器。

内容：

```text
ReportDraftPlan
  templateId
  templateVersion
  outputFormats
  sections
  dataBindings
  evidenceRequirements
```

报告生成时必须从 `ReportDraftPlan` 和已审核结果生成 `dataSnapshot`，不能任意读取当前最新配置。

## 8. 设备主档和任务设备绑定

### 8.1 EquipmentAsset

新增设备主档：

```text
lab_equipment_asset
  id
  equipment_code
  equipment_name
  equipment_type
  manufacturer
  model
  serial_no
  lab_area
  domain_code
  capability_scope
  responsible_user_id
  calibration_valid_until
  status
  iot_enabled
  iot_product_id
  iot_device_id
  data_source_type
```

状态：

```text
draft
enabled
disabled
maintenance
expired
scrapped
```

### 8.2 校准、维护和数据导入

现有 `lab_equipment_traceability` 作为兼容入口，后续归入校准记录。

新增或演进：

```text
lab_equipment_calibration_record
lab_equipment_maintenance_record
lab_equipment_data_import
```

第一批必须支持：

- 创建设备主档。
- 登记校准证书。
- 计算证据哈希。
- 根据证书有效期刷新设备状态。
- 查询可用于任务的设备。

### 8.3 LIMS 任务设备绑定

`lims_test_task` 增加设备引用：

```text
equipment_asset_id
equipment_code
equipment_name
equipment_snapshot
```

绑定规则：

- 只能绑定 `enabled` 且未超过校准有效期的设备。
- 设备 `domain_code` 或 `capability_scope` 必须覆盖任务检测方向或检测项目。
- 绑定时冻结设备摘要到 `equipment_snapshot`，避免设备主档后续变化影响历史任务。

## 9. 设备证据链最小闭环

### 9.1 EvidenceObject

新增证据对象：

```text
lab_evidence_object
  id
  evidence_no
  object_type
  source_type
  source_id
  source_no
  file_url
  file_hash
  metadata_json
  archived
  status
```

第一批对象类型：

```text
EQUIPMENT_CERTIFICATE
EQUIPMENT_INTERMEDIATE_CHECK
LIMS_RAW_RECORD
REPORT_OUTPUT
```

### 9.2 EvidenceLink

现有 `lab_evidence_link` 继续承载条款映射，但应补充或对齐：

```text
evidence_object_id
clause_id
capability_scope_id
link_reason
verified_by
verified_at
```

第一批闭环：

```text
EquipmentAsset
  -> CalibrationRecord
  -> EvidenceObject(EQUIPMENT_CERTIFICATE)
  -> EvidenceLink(CNAS equipment clause)
  -> LIMS TestTask equipment_snapshot
  -> Report dataSnapshot includes equipment evidence summary
```

## 10. API 和页面调整

### 10.1 LAB API

新增或调整：

- `POST /lab/domain-pack/{id}/publish`
- `POST /lab/domain-pack/{id}/copy-version`
- `POST /lab/domain-pack/{id}/archive`
- `GET /lab/domain-pack/{id}/published-snapshot`
- `GET /lab/equipment-asset/page`
- `POST /lab/equipment-asset/create`
- `PUT /lab/equipment-asset/update`
- `GET /lab/equipment-asset/available`
- `POST /lab/equipment-calibration/create`
- `POST /lab/equipment-evidence/link-clause`

### 10.2 LIMS API

新增或调整：

- `POST /lims/request/generate-execution-plan`
- `POST /lims/task/{id}/bind-equipment`
- `POST /lims/request/{id}/generate-report-draft-plan`
- `POST /lims/report/{id}/issue`

现有 `generate-tasks` 可保留，但内部应调用 `ExecutionPlanFactory`。

### 10.3 前端页面

第一批页面：

- 方向包管理：增加发布、复制版本、归档；发布后表单只读。
- 方向包设计器：增加样品要求、质控规则、证据要求三个配置区。
- 检测需求：显示冻结快照哈希和执行计划摘要。
- 检测任务：增加设备选择和设备快照展示。
- 设备台账：新增设备主档列表和详情入口。
- 设备证据：显示校准证书证据对象和条款映射。

## 11. 第一批实现范围

第一批只做以下闭环：

1. 设备主档：
   - 表、DO、Mapper、Service、Controller、前端列表和表单。
   - 可创建设备并维护状态、能力范围、校准有效期。
2. 方向包访问防腐层：
   - `LabDomainPackQueryService`。
   - `DomainPackGateway`。
   - `WorkflowSnapshotFactory`。
   - 移除 LIMS 对 `LabDomainPackMapper` 的直接依赖。
3. LIMS 任务设备绑定：
   - 任务表增加设备字段。
   - 绑定时校验设备状态和校准有效期。
   - 冻结设备摘要。
4. 设备证据链最小闭环：
   - 设备校准证书生成 `EvidenceObject`。
   - 可链接到 CNAS 设备条款。
   - 报告数据快照中包含任务设备和设备证据摘要。
5. 方向包不可变发布：
   - `draft -> published -> archived`。
   - 已发布版本不能原地修改。
   - 检测需求只能选择已发布方向包。

## 12. 验收标准

### 12.1 架构验收

- LIMS 不再直接注入 LAB Mapper。
- `WorkflowSnapshotFactory` 独立可测试。
- 已发布方向包不能修改。
- 新版本必须通过复制产生 draft。
- 设备主档是设备相关记录的主入口。

### 12.2 业务验收

- 可创建一个“恒温培养箱”设备。
- 可登记校准证书并生成证据对象。
- 可把证据对象链接到 CNAS 设备条款。
- 可创建检测需求并冻结已发布方向包。
- 可生成执行计划、样品、任务和结果字段计划。
- 可给检测任务绑定可用设备。
- 可录入结果并生成报告数据快照。
- 报告快照包含方向包版本、任务、结果字段、设备摘要和设备证据摘要。

### 12.3 验证验收

- `mvn -q -pl yudao-module-lab -am -DskipTests compile` 通过。
- `mvn -q -pl yudao-module-lims -am -DskipTests compile` 通过。
- `mvn -q -pl yudao-server -am -DskipTests compile` 通过。
- 至少新增以下测试：
  - 发布方向包后不能修改。
  - 创建检测需求冻结快照。
  - LIMS 通过 `DomainPackGateway` 读取方向包。
  - 设备过期不能绑定任务。
  - 校准证书生成证据对象。
- 前端构建通过。
- API 集成脚本跑通设备到报告快照的最小闭环。

## 13. 风险和约束

| 风险 | 处理 |
| --- | --- |
| 一次性补齐所有表会扩大变更面 | 第一批只做设备主档、校准证据、任务绑定和方向包发布 |
| 现有 `lab_equipment_traceability` 命名和目标模型不一致 | 保留兼容入口，新增 `EquipmentAsset` 作为主档 |
| 报告中心完整输出复杂 | 第一批只做 `ReportDraftPlan` 和报告数据快照，不做真实 PDF/Word/Excel 渲染 |
| 方向包配置迁移影响已有数据 | 发布快照从结构化表读取，旧 `workflow_schema` 作为兼容 fallback |
| DDD 分包过度设计 | 先抽工厂和网关两个关键边界，不一次性拆全量服务 |

## 14. 实施顺序建议

1. 加保护测试：冻结当前 LIMS 快照和报告生成行为。
2. 实现方向包发布状态和不可变校验。
3. 抽 `LabDomainPackQueryService`、`DomainPackGateway`、`WorkflowSnapshotFactory`。
4. 新增 `EquipmentAsset` 主档和设备可用性查询。
5. 新增设备证据对象和校准证书证据生成。
6. LIMS 任务增加设备绑定和设备快照。
7. 扩展执行计划和报告快照，使方向包剩余配置进入执行侧。
8. 后端编译、API 集成、前端构建和浏览器中文页面验收。
