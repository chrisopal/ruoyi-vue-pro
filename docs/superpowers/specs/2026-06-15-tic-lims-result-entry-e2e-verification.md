# TIC LIMS 结果录入闭环 E2E 复验记录

日期：2026-06-15

仓库：`ruoyi-vue-pro`

分支：`master-jdk17`

复验基线提交：`7a87767 Unify LIMS result entry around dynamic result values`

## 复验目标

本轮复验针对上一轮漂移复核发现的第一条语义边界：`raw-record` 与 `/lims/result/create` 结果录入路径需要统一。

本轮基线已经把任务原始记录里的结果载荷同步到 `lims_test_result` 和 `lims_test_result_value`，因此需要用真实后端、真实数据库和前端页面重新确认：

- 通用检测方向包配置没有漂移，仍通过已发布不可变版本进入 LIMS。
- `WorkflowSnapshotFactory` 和 `DomainPackGateway` 仍是 LIMS 访问方向包的边界。
- 样品要求、结果字段、QC 规则、证据要求、报告章节/模板规则进入 `ExecutionPlan` 与 `ReportDraftPlan`。
- 从检测需求到报告的闭环能通过任务记录路径沉淀动态结果字段。
- 报告内容和报告输出能读取到动态结果值，并形成报告证据链。

## 复验环境

- MySQL：Docker `lims-verify-mysql`，端口 `127.0.0.1:33307`
- 数据库：`yudao_lims_e2e_result`
- Backend：`http://127.0.0.1:48083`
- Frontend：`http://127.0.0.1:4197`
- Report output dir：`target/lims-result-e2e-report-output`
- Browser screenshots：`target/lims-result-e2e-screenshots`

启动参数中使用 JVM system property：

```text
-Dlims.report.output.dir=/Users/guojiexie/Development/lims/ruoyi-vue-pro/target/lims-result-e2e-report-output
```

## 业务数据

- 方向包：`FOOD_ROUTINE_V1 / 1.0 / published`
- 检测需求：`TIC-E2E-20260615004400`
- 检测任务：
  - `TIC-E2E-20260615004400-T01`，感官检查，设备 `FOOD-PH-001`
  - `TIC-E2E-20260615004400-T02`，水分，设备 `FOOD-PH-001`
- 报告：`RPT-TIC-E2E-20260615004400`
- 报告状态：`issued`
- 报告结论：`合格`

两条任务均按状态机完成：

```text
generated -> scheduled -> ready -> testing -> data_submitted -> reviewing -> approved
```

排程采用显式错峰窗口，避免同一设备 `FOOD-PH-001` 的时间冲突：

- `T01`：`2026-06-16 09:00:00 ~ 2026-06-16 10:00:00`
- `T02`：`2026-06-16 11:00:00 ~ 2026-06-16 12:00:00`

## 执行计划核对

`/admin-api/lims/request/execution-plan?id=1` 返回的执行计划包含：

```text
workflowNodes              5
sampleRequirements         2
taskPlans                  2
resultFieldPlans           SENSE_RESULT, MOISTURE_VALUE
qcCheckPlans               FOOD_BATCH_QC
evidenceRequirementPlans   FOOD_RAW_RECORD, FOOD_EQUIPMENT_CAL
reportSections             basicInfo, sampleInfo, resultTable, equipmentTrace, conclusion
reportDataBindings         SENSE_RESULT, MOISTURE_VALUE
```

结论：方向包剩余能力已经进入执行侧，不只是停留在 LAB 配置侧。

## 结果录入核对

任务原始记录通过 `rawData.resultValues` 提交结果字段：

- `SENSE_RESULT / 感官结论 / 符合`
- `MOISTURE_VALUE / 水分含量 / 12.5%`

数据库核对：

```text
result_value_count
2

task_no                         field_code       field_name   display_value   conclusion
TIC-E2E-20260615004400-T01      SENSE_RESULT     感官结论       符合             pass
TIC-E2E-20260615004400-T02      MOISTURE_VALUE   水分含量       12.5%           pass
```

质量门禁核对：

- 技术复核前：原始记录、QC、设备证据均满足；缺口为技术复核。
- 技术复核后：两条任务 `qualityGateSatisfied=true`，`missingRequirementCount=0`。

## 报告核对

`lims_report.report_content` 包含 `resultValues`，并能读取两条动态结果字段：

```text
TIC-E2E-20260615004400-T01:SENSE_RESULT=符合
TIC-E2E-20260615004400-T02:MOISTURE_VALUE=12.5%
```

报告输出清单：

```text
WORD   /lims/report-output/RPT-TIC-E2E-20260615004400/RPT-TIC-E2E-20260615004400.docx
PDF    /lims/report-output/RPT-TIC-E2E-20260615004400/RPT-TIC-E2E-20260615004400.pdf
EXCEL  /lims/report-output/RPT-TIC-E2E-20260615004400/RPT-TIC-E2E-20260615004400.xlsx
```

本地文件大小：

```text
RPT-TIC-E2E-20260615004400.xlsx  2284 bytes
RPT-TIC-E2E-20260615004400.docx  1456 bytes
RPT-TIC-E2E-20260615004400.pdf   1440 bytes
```

带登录态访问 PDF 返回 `Content-Type: application/pdf`，文件头为 `%PDF-1.4`。

报告签发后证据链核对：

```text
linked_biz_type   linked_biz_no                  evidence_name                       link_status
lims_report       RPT-TIC-E2E-20260615004400     食品常规检测结果值复验检测报告       linked
```

## 浏览器证据

截图文件：

- `target/lims-result-e2e-screenshots/01-task-quality-gate-result-values.png`
- `target/lims-result-e2e-screenshots/02-report-issued-result-values.png`
- `target/lims-result-e2e-screenshots/03-report-output-links.png`

关键页面核对：

- 任务质量门禁抽屉显示 `FOOD_ROUTINE_V1 / 1.0`、总门禁已满足、`SENSE_RESULT`、样品要求、QC 规则和证据要求。
- 报告列表显示 `RPT-TIC-E2E-20260615004400`、结论 `合格`。
- 报告输出横向列显示 `WORD / PDF / EXCEL` 输出标签、文件地址和 `issued` 状态。

## 漂移判断

本轮复验没有发现相对总体设计的新漂移：

- LIMS 仍通过方向包防腐层消费 LAB 配置。
- 方向包发布版本进入冻结快照，冻结快照生成执行计划和报告草稿计划。
- 执行闭环仍能同时支持第三方订单和内部检测需求的统一 `TestRequest` 抽象。
- 检测方向差异通过方向包配置驱动任务、结果字段、质控、证据和报告规则。
- 设备主档、任务设备绑定、设备校准证据和报告签发证据链已形成最小闭环。

## 后续收敛点

1. 将本轮临时 API + CDP 浏览器脚本沉淀为可复跑 E2E 验收脚本。
2. 报告列表当前通用状态列显示 `issued`，后续可补状态字典/中文化显示。
3. 报告输出列需要横向滚动才能完整查看，后续可增加报告详情抽屉或固定输出入口。
4. 下一批建议进入报告详情/报告设计器深化，或进入 CNAS/CMA 能力范围与证据缺口诊断闭环。
