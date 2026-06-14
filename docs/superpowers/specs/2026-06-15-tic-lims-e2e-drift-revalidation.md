# TIC LIMS 当前实现漂移复核记录

日期：2026-06-15

仓库：`ruoyi-vue-pro`

分支：`master-jdk17`

复核提交：`931cf79 Preserve frozen LIMS requests after domain-pack archival`

## 结论

当前实现与“通用检测方向包设计器 + 不同检测场景配置 + 检测需求到报告闭环”的总体架构保持一致，没有发现需要推翻现有抽象的漂移。

已经验证成立的关键点：

- 方向包已按不可变版本治理：`draft -> published -> archived`，发布或归档后不能原地编辑，已发布包通过复制版本形成新草稿。
- LIMS 执行侧通过 `DomainPackGateway` 和 `WorkflowSnapshotFactory` 消费方向包快照，没有直接读取 LAB Mapper。
- 已发布方向包会冻结为 `WorkflowSnapshot`，并生成 `ExecutionPlan` 与 `ReportDraftPlan`。
- 样品要求、结果字段、QC 规则、证据要求、报告章节和输出格式已经进入执行侧质量门禁和报告计划。
- 设备主档 `FOOD-PH-001` 能绑定到 LIMS 任务，并冻结设备快照和校准证据快照。
- 原始记录、QC、技术复核、设备证据和报告签发能形成最小证据链。
- 报告中心能基于执行结果生成并签发 Word、PDF、Excel 三类输出。

## 真实链路验证

验证环境：

- MySQL：Docker `lims-verify-mysql`，数据库 `yudao_lims_e2e`
- Backend：`http://127.0.0.1:48082`
- Frontend：`http://127.0.0.1:4196`
- 前端 API：`VITE_BASE_URL=http://127.0.0.1:48082`

验证业务数据：

- 方向包：`FOOD_ROUTINE_V1 / 1.0 / published`
- 检测需求：`E2E-FOOD-20260614232345`
- 检测任务：
  - `E2E-FOOD-20260614232345-T01`，感官检查，设备 `FOOD-PH-001`
  - `E2E-FOOD-20260614232345-T02`，水分，设备 `FOOD-PH-001`
- 任务状态：两条任务均为 `approved`，QC `approved`，复核 `approved`，`reportEligible=true`
- 报告：`RPT-E2E-FOOD-20260614232345`，状态 `issued`
- 报告输出：
  - `/lims/report-output/RPT-E2E-FOOD-20260614232345/RPT-E2E-FOOD-20260614232345.docx`
  - `/lims/report-output/RPT-E2E-FOOD-20260614232345/RPT-E2E-FOOD-20260614232345.pdf`
  - `/lims/report-output/RPT-E2E-FOOD-20260614232345/RPT-E2E-FOOD-20260614232345.xlsx`

数据库核对：

```text
requests          1
tasks             2
raw_records       2
qc_records        2
reviews           2
results           2
reports           1
evidence_objects  8
evidence_links    8
```

执行计划核对：

- `sampleRequirements` 已进入 `ExecutionPlan`
- `qcRules` 已进入任务质量门禁
- `evidenceRequirements` 已进入任务质量门禁
- `reportDraftPlan` 已生成，模板版本为 `1.0`

## 浏览器证据

截图保存在本地验证目录：

- `target/lims-e2e-screenshots/01-pack-designer.png`
- `target/lims-e2e-screenshots/02-domain-pack-published.png`
- `target/lims-e2e-screenshots/03-equipment-asset.png`
- `target/lims-e2e-screenshots/04-lims-task.png`
- `target/lims-e2e-screenshots/04b-lims-task-quality-gate.png`
- `target/lims-e2e-screenshots/05-lims-report.png`

其中 `04b-lims-task-quality-gate.png` 是最关键证据：页面显示方向包 `FOOD_ROUTINE_V1 / 1.0`、执行计划 `generated`、QC 通过、复核通过、可报告、总门禁已满足，并展示样品要求、结果字段、QC 规则和证据要求。

## 发现的语义边界

这些不是架构漂移，但需要在后续实现中继续收紧：

1. `raw-record` 与 `result/create` 不是串联动作。
   当前后端语义是原始记录提交可以携带结果载荷并同步 `lims_test_result`。如果先提交原始记录，再调用 `result/create`，生命周期会拒绝从 `data_submitted` 回跳。后续应把执行侧 UI 的主路径固定为“任务记录抽屉提交原始记录和结果载荷”，或把 `result/create` 改成可幂等补录。

2. 同一设备不能用默认排程同时绑定多个任务。
   两个食品任务都自动匹配 `FOOD-PH-001`，默认排程会产生时间窗口冲突。真实流程必须显式错峰排程或选择其他可用设备。这是正确的资源约束。

3. 报告输出目录读取 JVM system property。
   `ReportOutputGenerator` 使用 `System.getProperty("lims.report.output.dir")`，因此 Spring 启动参数 `--lims.report.output.dir=...` 不会生效。本次验证文件落在默认 `target/lims-report-output`。后续可改为 Spring 配置属性，或者在运行文档中明确使用 `-Dlims.report.output.dir=...`。

4. 宽表截图可读性仍需优化。
   检测方案包、检测任务、检测报告表格字段较宽，部分关键列依赖横向滚动或抽屉查看。任务质量门禁抽屉已经能承载关键验收信息，后续可继续改善表格默认列和详情入口。

## 下一步

进入下一实现计划时，不需要推翻现有 DDD 分层和领域边界，建议沿当前实现继续推进：

1. 固化结果录入语义：统一 `TaskRecordDrawer`、结果页和后端 `result/create` 的职责，避免同一任务出现两条结果路径。
2. 把本次 API + 浏览器 E2E 脚本沉淀为可复跑验收脚本，覆盖方向包冻结、任务设备绑定、质量门禁、报告签发和证据链。
3. 改善报告列表和任务列表的关键列展示，让报告输出、设备证据和门禁状态默认更容易被实施人员看见。
4. 进入 CNAS/CMA 能力范围、证据缺口诊断和评审运营看板的下一批闭环。

