# TIC LIMS 当前实现漂移纠偏设计确认版

日期：2026-06-15
状态：Accepted for implementation planning
适用仓库：`ruoyi-vue-pro`
关联基线：`docs/superpowers/specs/2026-06-14-tic-lims-drift-correction-design.md`

## 1. 背景和目标

当前 TIC LIMS 已经从原 LAB 需求迁移到 RuoYi-Vue-Pro 体系，并形成 `yudao-module-lab` 和 `yudao-module-lims` 两个主要模块。上一版纠偏设计已经定义了六层产品架构中的第一阶段落点：检测方向包、LIMS 执行闭环、设备主档、设备证据链、报告草稿计划和跨模块防腐层。

本确认版只解决一个问题：在继续实现前，把当前实现重新钉回设计边界，防止后续开发漂移成“直接读配置、直接拼报告、直接访问 LAB 表”的快捷路径。

纠偏目标：

- 第一批实现只收敛在设备主档、方向包访问防腐层、LIMS 任务设备绑定、设备证据链最小闭环。
- 方向包必须发布为不可变版本，生命周期固定为 `draft -> published -> archived`。
- LIMS 执行侧只能读取已发布方向包快照，不能直接读取 LAB Mapper。
- `WorkflowSnapshot` 必须由明确工厂生成，至少形成 `WorkflowSnapshotFactory` 和 `DomainPackGateway`。
- 方向包剩余能力必须进入执行侧：样品要求、质控规则、证据要求、报告模板/章节规则进入 `ExecutionPlan` 和 `ReportDraftPlan`。

## 2. 当前实现校准

| 设计项 | 当前实现判断 | 纠偏结论 |
| --- | --- | --- |
| 方向包生命周期 | 已有方向包发布、复制版本、归档能力 | 保持 `draft/published/archived`，后续任何配置编辑必须复用不可变校验 |
| 方向包访问防腐层 | LIMS 已出现 `DomainPackGateway` 和 `WorkflowSnapshotFactory` | 继续强化：LIMS 允许依赖网关和服务 DTO，不允许依赖 LAB Mapper |
| WorkflowSnapshot | 检测需求已保存快照 JSON、哈希和冻结时间 | 快照创建逻辑必须留在工厂，不回流到 `LimsWorkflowService` |
| ExecutionPlan | 已有执行计划工厂和持久化模型 | 必须包含样品要求、任务计划、结果字段计划、质控检查计划、证据要求计划、报告草稿计划 |
| ReportDraftPlan | 已有报告草稿计划工厂 | 必须从冻结快照的报告章节、模板要求、输出格式、证据要求生成 |
| 设备主档 | 已有 `LabEquipmentAsset` 设备主档和可用设备查询 | 第一批保持主档和可用性，不扩展真实 IoT 协议 |
| 任务设备绑定 | LIMS 任务已有设备字段和冻结快照能力 | 绑定时必须校验设备可用性，并冻结设备摘要和校准证据 |
| 设备证据链 | 已有证据对象、证据链接和校准证据摘要 | 第一批只跑通设备证书到 CNAS/CMA 条款和报告快照的最小闭环 |

当前未提交的质量门实现属于“执行侧补齐”的后续扩展，只能在本设计提交后进入实现计划；不能混入设计提交。

## 3. 目标架构边界

### 3.1 领域分层

```text
LAB / Domain Pack Context
  owns: domain pack draft, structured config, publication, archive

LAB / Equipment Context
  owns: equipment asset, calibration summary, available equipment query

LAB / Evidence Chain Context
  owns: evidence object, evidence link, CNAS/CMA clause association

LIMS / Execution Context
  owns: test request, sample, execution plan, task, result, task equipment binding

LIMS / Report Context
  owns: report draft plan, report data snapshot, output artifacts, issue state
```

跨域规则：

- LIMS 可以通过 `DomainPackGateway` 读取已发布方向包快照。
- LIMS 可以通过 `EquipmentGateway` 读取可用设备摘要和校准证据摘要。
- LIMS 不直接注入或调用 `cn.iocoder.yudao.module.lab.dal.mysql.*`。
- LAB 不修改 LIMS 执行状态。
- 历史检测需求、执行计划、报告快照都以冻结快照为准，不读取当前最新方向包配置。

### 3.2 主流程

```text
DomainPack draft
  -> configure sampleRequirements / workflowNodes / testItems / resultFields
  -> configure qcRules / evidenceRequirements / reportSections / template rules
  -> publish immutable version
  -> LIMS DomainPackGateway reads published snapshot
  -> WorkflowSnapshotFactory freezes snapshot and hash
  -> ExecutionPlanFactory creates sample/task/result/QC/evidence/report plans
  -> LIMS executes tasks and binds available equipment
  -> equipment snapshot + calibration evidence snapshot are frozen on task
  -> ReportDraftPlan drives report data snapshot and output artifacts
  -> EvidenceObject and EvidenceLink preserve equipment/report traceability
```

## 4. 方向包不可变发布设计

状态机固定为：

```text
draft -> published -> archived
```

规则：

- `draft` 可编辑基础信息和结构化配置。
- `published` 不能原地修改基础信息、流程节点、样品要求、检测项目、结果字段、质控规则、证据要求、报告章节和模板规则。
- `published` 如需调整，必须复制为新的 `draft` 版本。
- `archived` 不可用于新检测需求，但历史检测需求继续可追溯。
- 新检测需求只能选择 `published` 方向包。
- 发布时形成结构化快照；`workflow_schema`、`template_schema` 只能作为兼容字段或缓存字段，不作为唯一事实来源。

## 5. WorkflowSnapshotFactory 和 DomainPackGateway

`DomainPackGateway` 是 LIMS 对 LAB 方向包的防腐入口：

```text
DomainPackGateway.getPublishedPackSnapshot(domainPackId)
```

`WorkflowSnapshotFactory` 的职责只有一个：把已发布方向包快照和检测需求上下文转换成不可变执行快照。

输出必须包含：

- `domainPackId`
- `packCode`
- `packVersion`
- `workflowNodes`
- `sampleRequirements`
- `testItems`
- `resultFields`
- `qcRules`
- `evidenceRequirements`
- `reportSections`
- `templateRequirements`
- `snapshotHash`
- `frozenAt`

禁止事项：

- 禁止在 `LimsWorkflowService` 里重新手写快照拼装逻辑。
- 禁止 LIMS 直接注入 `LabDomainPackMapper` 或其他 LAB Mapper。
- 禁止报告生成时绕过冻结快照读取最新方向包配置。

## 6. ExecutionPlan 和 ReportDraftPlan

`ExecutionPlan` 是 LIMS 执行闭环的计划层，必须从冻结 `WorkflowSnapshot` 派生。

`plan_json` 至少包含：

- `sampleRequirements`
- `taskPlans`
- `resultFieldPlans`
- `qcCheckPlans`
- `evidenceRequirementPlans`
- `reportDraftPlan`

`ReportDraftPlan` 是报告中心的第一阶段抽象，先解决“报告由什么章节、什么字段、什么证据、什么格式输出”。

`reportDraftPlan` 至少包含：

- `templateId`
- `templateVersion`
- `outputFormats`
- `sections`
- `sectionRules`
- `dataBindings`
- `evidenceRequirements`

报告生成时只能使用：

- 检测需求冻结快照。
- 已审核结果。
- 任务设备冻结快照。
- 已收集证据对象和链接摘要。
- 报告草稿计划。

## 7. 第一批实现范围锁定

第一批只做以下四个闭环，不扩张到 AI、真实 IoT、完整报告设计器或商业订单：

1. 设备主档：
   - 创建设备台账。
   - 维护设备状态、能力范围、校准有效期、IoT 预留字段。
   - 查询可用设备。
2. 方向包访问防腐层：
   - 保持 `LabDomainPackQueryService`。
   - 保持 `DomainPackGateway`。
   - 保持 `WorkflowSnapshotFactory`。
   - 移除并防止 LIMS 直接访问 LAB Mapper。
3. LIMS 任务设备绑定：
   - 任务绑定可用设备。
   - 绑定时冻结设备摘要和校准证据摘要。
   - 设备过期、停用、能力范围不匹配时拒绝绑定。
4. 设备证据链最小闭环：
   - 校准证书形成证据对象。
   - 证据对象可链接 CNAS/CMA 设备条款。
   - 报告数据快照包含任务设备和设备证据摘要。

方向包发布不可变、`ExecutionPlan`、`ReportDraftPlan` 是这四个闭环的前置架构约束，不另行扩张为独立大批次。

## 8. 实施计划入口

设计提交后进入实现计划，按下面顺序推进：

1. 架构检查：确认 LIMS 无 LAB Mapper 依赖，确认快照逻辑只在工厂和网关边界。
2. 当前代码对齐：检查已实现第一批是否完全满足本确认版，不满足处只做小补丁。
3. 方向包执行侧补齐：确认样品要求、质控规则、证据要求、报告章节规则进入 `ExecutionPlan` 和 `ReportDraftPlan`。
4. 质量门最小实现：在不扩大领域边界的前提下，使用冻结快照校验结果字段、质控覆盖、证据完整性。
5. 验证：运行 LIMS/LAB 相关单测、`yudao-server` 编译、架构 grep、必要的前端构建或浏览器验收。
6. 提交：每个完成批次按 Lore 协议提交并推送。

## 9. 验收标准

架构验收：

- `rg "cn\\.iocoder\\.yudao\\.module\\.lab\\.dal\\.mysql|Lab[A-Za-z0-9]+Mapper" yudao-module-lims/src/main/java yudao-module-lims/src/test/java` 无命中。
- `WorkflowSnapshotFactory`、`DomainPackGateway` 有单元测试或服务测试覆盖。
- 已发布方向包不能通过基础信息或配置接口原地修改。
- `ExecutionPlan` 和 `ReportDraftPlan` 能从冻结快照还原关键配置。

业务验收：

- 可创建方向包草稿、发布、复制新版本、归档。
- 可创建检测需求并冻结已发布方向包。
- 可生成执行计划，计划包含样品、任务、结果字段、质控、证据和报告草稿。
- 可创建设备主档并查询可用设备。
- 可将设备绑定到 LIMS 任务并冻结设备证据。
- 报告快照包含方向包版本、任务结果、设备摘要和设备证据摘要。

范围验收：

- 不新增真实 IoT 协议接入。
- 不新增完整 AI 标准/报告解读能力。
- 不新增像素级报告设计器。
- 不引入新的跨模块 Mapper 依赖。

## 10. ADR

### ADR-001: LIMS 通过网关读取方向包快照

状态：Accepted

上下文：LIMS 需要消费 LAB 的检测方向包配置，但直接注入 LAB Mapper 会让执行域依赖配置域的持久化细节，后续发布不可变和版本追溯都会失控。

决策：LIMS 定义 `DomainPackGateway`，实现层调用 LAB 的只读查询服务；检测需求创建时由 `WorkflowSnapshotFactory` 生成冻结快照。

后果：

- 正面：LIMS 执行闭环稳定依赖已发布版本，历史报告可追溯。
- 负面：需要维护服务 DTO 和网关测试。
- 中性：当前仍处于模块化单体，服务调用是进程内调用，不引入网络复杂度。

替代方案：

- 直接读取 LAB Mapper：拒绝，破坏 DDD 边界。
- 把方向包复制到 LIMS 表：暂不采用，第一批会增加迁移和同步复杂度。

### ADR-002: 第一批设备能力以主档和证据闭环为界

状态：Accepted

上下文：设备管理既包含台账、校准、维护、期间核查，也包含物联采集和数据分析。一次性实现全部会拖慢 LIMS 主闭环。

决策：第一批只做设备主档、可用设备查询、任务绑定、校准证据快照和 CNAS/CMA 条款链接；真实物联复用后续芋道 IoT 模块。

后果：

- 正面：设备能进入报告证据链，满足 CNAS/CMA 最小追溯。
- 负面：实时采集、协议网关和设备数据分析暂时不可用。
- 中性：IoT 字段先预留，避免未来表结构大改。

替代方案：

- 同期接入 IoT：拒绝，超出第一批边界。
- 只在任务上填设备文本：拒绝，无法形成可审计证据链。
