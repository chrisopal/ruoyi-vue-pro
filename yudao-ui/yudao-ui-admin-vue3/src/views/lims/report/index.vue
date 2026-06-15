<template>
  <ContentWrap>
    <div class="report-center-header">
      <div>
        <div class="report-title">报告中心</div>
        <div class="report-subtitle">
          管理检测报告的草稿、生成、签发与多格式输出，支撑第三方检测交付和内部实验室归档
        </div>
      </div>
      <el-button :loading="overviewLoading" plain type="primary" @click="loadReportOverview">
        <Icon class="mr-5px" icon="ep:refresh" />刷新概览
      </el-button>
    </div>

    <div class="report-overview-grid">
      <div class="report-overview-card">
        <span>报告总数</span>
        <strong>{{ reportOverview.total }}</strong>
        <small>当前列表可见报告</small>
      </div>
      <div class="report-overview-card">
        <span>已签发</span>
        <strong>{{ reportOverview.issued }}</strong>
        <small>可作为正式交付件</small>
      </div>
      <div class="report-overview-card">
        <span>签发率</span>
        <strong>{{ reportOverview.issueRate }}<em>%</em></strong>
        <el-progress :percentage="reportOverview.issueRate" :show-text="false" :stroke-width="8" />
      </div>
      <div class="report-overview-card">
        <span>输出格式</span>
        <strong>{{ reportOverview.outputFormats.length || 0 }}</strong>
        <small>{{ reportOverview.outputFormats.join(' / ') || '待生成' }}</small>
      </div>
    </div>

    <div class="report-preview-workspace">
      <div class="report-preview-side">
        <span>最终报告预览</span>
        <strong>{{ previewReport.reportNo }}</strong>
        <small>{{ previewReport.reportName }}</small>
        <div class="report-preview-status">
          <el-tag :type="previewReport.status === 'issued' ? 'success' : 'warning'" effect="light">
            {{ previewReport.status === 'issued' ? '已签发' : '预览样式' }}
          </el-tag>
          <el-tag effect="plain">模板 v{{ previewReport.templateVersion }}</el-tag>
        </div>
        <el-tabs v-model="activePreviewFormat" class="report-format-tabs">
          <el-tab-pane
            v-for="format in previewFormats"
            :key="format"
            :label="formatLabel(format)"
            :name="format"
          />
        </el-tabs>
        <div class="report-output-list">
          <div v-for="output in previewOutputs" :key="output.format || output.fileName" class="report-output-row">
            <span>{{ formatLabel(output.format || '-') }}</span>
            <strong>{{ output.fileName || '待生成文件' }}</strong>
          </div>
          <div v-if="!previewOutputs.length" class="report-output-row">
            <span>输出</span>
            <strong>Word / PDF / Excel 交付件待生成</strong>
          </div>
        </div>
      </div>

      <div class="report-paper-shell">
        <div class="report-paper" :class="`report-paper--${activePreviewFormat.toLowerCase()}`">
          <div class="report-paper-topline"></div>
          <div class="report-paper-header">
            <div>
              <div class="report-paper-org">TIC 智慧检测实验室</div>
              <h2>{{ previewReport.reportName }}</h2>
              <p>报告编号：{{ previewReport.reportNo }} / 需求编号：{{ previewReport.requestNo }}</p>
            </div>
            <div class="report-paper-seal">
              <span>{{ formatLabel(activePreviewFormat) }}</span>
              <strong>{{ previewReport.status === 'issued' ? 'ISSUED' : 'DRAFT' }}</strong>
            </div>
          </div>

          <div class="report-paper-meta">
            <div>
              <span>模板版本</span>
              <strong>{{ previewReport.templateVersion }}</strong>
            </div>
            <div>
              <span>签发时间</span>
              <strong>{{ previewReport.issuedTime || '待签发' }}</strong>
            </div>
            <div>
              <span>报告结论</span>
              <strong>{{ previewReport.conclusion }}</strong>
            </div>
          </div>

          <div class="report-paper-section">
            <h3>一、样品与检测信息</h3>
            <div class="report-paper-table">
              <div><span>检测需求</span><strong>{{ previewReport.requestNo }}</strong></div>
              <div><span>报告模板</span><strong>v{{ previewReport.templateVersion }}</strong></div>
              <div><span>数据快照</span><strong>{{ previewReport.dataSnapshotHash || '自动归档' }}</strong></div>
              <div><span>状态</span><strong>{{ previewReport.status }}</strong></div>
            </div>
          </div>

          <div class="report-paper-section">
            <h3>二、检测结果</h3>
            <div class="result-table">
              <div class="result-table-head">
                <span>项目</span>
                <span>结果</span>
                <span>判定</span>
              </div>
              <div v-for="item in previewResultRows" :key="item.item" class="result-table-row">
                <span>{{ item.item }}</span>
                <span>{{ item.value }}</span>
                <span>{{ item.conclusion }}</span>
              </div>
            </div>
          </div>

          <div class="report-paper-section">
            <h3>三、报告正文</h3>
            <p v-for="line in previewContentLines" :key="line">{{ line }}</p>
          </div>

          <div class="report-paper-footer">
            <div>
              <span>授权签字人</span>
              <strong>系统签发</strong>
            </div>
            <div>
              <span>证据链</span>
              <strong>原始记录 / 设备 / 人员 / 环境已归档</strong>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="report-operation-strip">
      <div class="report-latest">
        <span>最近报告</span>
        <strong>{{ latestReport?.reportNo || '暂无报告' }}</strong>
        <small>{{ latestReport?.reportName || '检测需求生成报告后，这里会展示最新交付件' }}</small>
      </div>
      <div class="report-lifecycle">
        <div v-for="step in lifecycleSteps" :key="step.label" class="report-lifecycle-step">
          <Icon :icon="step.icon" />
          <span>{{ step.label }}</span>
        </div>
      </div>
    </div>
  </ContentWrap>

  <LimsWorkflowPage
    title="检测报告"
    subtitle="由检测需求、结果字段、报告模板和证据链自动装配，支持 Word / PDF / Excel 输出"
    base-url="/lims/report"
    permission="lims:report"
    no-field="reportNo"
    no-label="报告编号"
    name-field="reportName"
    name-label="报告名称"
    :fields="fields"
    :row-actions="rowActions"
    :defaults="{ status: 'draft' }"
  />
</template>

<script lang="ts" setup>
import { LimsWorkflowApi, type LimsWorkflowVO } from '@/api/lims/workflow'
import LimsWorkflowPage from '@/views/lims/_components/LimsWorkflowPage.vue'

defineOptions({ name: 'LimsReport' })

const overviewLoading = ref(false)
const reportRows = ref<LimsWorkflowVO[]>([])
const activePreviewFormat = ref('PDF')

const lifecycleSteps = [
  { label: '结果汇总', icon: 'ep:document-copy' },
  { label: '模板装配', icon: 'ep:tickets' },
  { label: '签发确认', icon: 'ep:finished' },
  { label: '多格式输出', icon: 'ep:folder-checked' },
  { label: '证据归档', icon: 'ep:link' }
]

const fields = [
  { prop: 'requestId', label: '需求ID', table: false },
  { prop: 'requestNo', label: '需求编号' },
  { prop: 'templateId', label: '模板ID', table: false },
  { prop: 'templateVersion', label: '模板版本' },
  { prop: 'conclusion', label: '报告结论' },
  { prop: 'reportOutput', label: '输出清单', type: 'textarea', display: 'reportOutput', span: 24 },
  { prop: 'fileUrl', label: '文件地址' },
  { prop: 'issuedTime', label: '签发时间' },
  { prop: 'reportContent', label: '报告内容', type: 'textarea', span: 24, table: false },
  { prop: 'remark', label: '备注', type: 'textarea', span: 24, table: false }
]
const rowActions = [
  { label: '签发', url: '/lims/report/issue', method: 'put', permission: 'lims:report:update' }
] as const

const parseReportOutputs = (value?: string) => {
  if (!value) return []
  try {
    const manifest = typeof value === 'string' ? JSON.parse(value) : value
    if (Array.isArray(manifest?.outputs)) return manifest.outputs
    if (Array.isArray(manifest)) return manifest
    return []
  } catch {
    return []
  }
}

const parseReportOutputFormats = (value?: string) => {
  return parseReportOutputs(value).map((item) => String(item.format || '').toUpperCase()).filter(Boolean)
}

const parseReportContent = (value?: string): any => {
  if (!value) return {}
  try {
    return JSON.parse(value)
  } catch {
    return { summary: value }
  }
}

const formatLabel = (format: string) => {
  const upper = String(format || '').toUpperCase()
  if (upper === 'WORD') return 'Word'
  if (upper === 'PDF') return 'PDF'
  if (upper === 'EXCEL') return 'Excel'
  return format
}

const reportOverview = computed(() => {
  const rows = reportRows.value
  const issued = rows.filter((item) => item.status === 'issued').length
  const outputFormats = new Set<string>()
  rows.forEach((item) => {
    parseReportOutputFormats(item.reportOutput).forEach((format) => outputFormats.add(format))
  })
  return {
    total: rows.length,
    issued,
    issueRate: rows.length ? Math.round((issued / rows.length) * 100) : 0,
    outputFormats: Array.from(outputFormats)
  }
})

const latestReport = computed(() => reportRows.value[0])
const fallbackPreviewReport = computed<LimsWorkflowVO>(() => ({
  reportNo: 'RPT-PREVIEW-001',
  reportName: '通用检测报告预览',
  requestNo: 'REQ-PREVIEW-001',
  templateVersion: '1.0',
  conclusion: '符合要求',
  status: 'draft',
  issuedTime: '',
  dataSnapshotHash: 'preview',
  reportContent: JSON.stringify({
    summary: '本报告根据检测方向包、结果字段、报告模板和证据链自动装配，用于预览最终交付版式。',
    results: [
      { item: '样品接收', value: '符合样品要求', conclusion: '通过' },
      { item: '检测结果', value: '见检测数据表', conclusion: '符合' },
      { item: '质量控制', value: '质控样有效', conclusion: '通过' }
    ]
  }),
  reportOutput: JSON.stringify({
    outputs: [
      { format: 'WORD', fileName: '通用检测报告.docx' },
      { format: 'PDF', fileName: '通用检测报告.pdf' },
      { format: 'EXCEL', fileName: '检测结果明细.xlsx' }
    ]
  })
}))
const previewReport = computed(() => latestReport.value || fallbackPreviewReport.value)
const previewOutputs = computed(() => parseReportOutputs(previewReport.value.reportOutput))
const previewFormats = computed(() => {
  const formats = previewOutputs.value.map((item) => String(item.format || '').toUpperCase()).filter(Boolean)
  return formats.length ? Array.from(new Set(formats)) : ['WORD', 'PDF', 'EXCEL']
})
const previewReportContent = computed(() => parseReportContent(previewReport.value.reportContent))
const previewResultRows = computed(() => {
  const results = Array.isArray(previewReportContent.value?.results) ? previewReportContent.value.results : []
  if (results.length) {
    return results.map((item: any, index: number) => ({
      item: String(item.item || item.itemName || `检测项目 ${index + 1}`),
      value: String(item.value || item.resultValue || '-'),
      conclusion: String(item.conclusion || item.resultConclusion || previewReport.value.conclusion || '-')
    }))
  }
  return [
    { item: '检测结果汇总', value: previewReport.value.conclusion || '待生成', conclusion: previewReport.value.status || '-' },
    { item: '报告模板装配', value: `v${previewReport.value.templateVersion || '-'}`, conclusion: '已装配' },
    { item: '多格式输出', value: previewFormats.value.join(' / '), conclusion: previewOutputs.value.length ? '已生成' : '待生成' }
  ]
})
const previewContentLines = computed(() => {
  const content = previewReportContent.value
  const summary = content?.summary || content?.content || content?.text
  if (summary) {
    return String(summary).split(/\n+/).filter(Boolean)
  }
  if (previewReport.value.reportContent) {
    return String(previewReport.value.reportContent).split(/\n+/).filter(Boolean).slice(0, 4)
  }
  return ['报告正文将由检测需求、样品信息、检测结果、质控记录和证据链自动装配生成。']
})

watch(previewFormats, (formats) => {
  if (!formats.includes(activePreviewFormat.value)) {
    activePreviewFormat.value = formats[0] || 'PDF'
  }
})

const loadReportOverview = async () => {
  overviewLoading.value = true
  try {
    const data = await LimsWorkflowApi.page('/lims/report', { pageNo: 1, pageSize: 50 })
    reportRows.value = data.list || []
  } finally {
    overviewLoading.value = false
  }
}

onMounted(() => loadReportOverview())
</script>

<style scoped>
.report-center-header {
  display: flex;
  gap: 16px;
  align-items: flex-start;
  justify-content: space-between;
}

.report-title {
  color: var(--el-text-color-primary);
  font-size: 20px;
  font-weight: 600;
  line-height: 1.35;
}

.report-subtitle {
  max-width: 760px;
  margin-top: 6px;
  color: var(--el-text-color-secondary);
  font-size: 13px;
  line-height: 1.5;
}

.report-overview-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-top: 18px;
}

.report-overview-card {
  min-height: 116px;
  padding: 16px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 6px;
  background: var(--el-bg-color);
}

.report-overview-card span,
.report-overview-card small {
  display: block;
  color: var(--el-text-color-secondary);
  font-size: 12px;
  line-height: 1.45;
}

.report-overview-card strong {
  display: block;
  margin: 10px 0 8px;
  color: var(--el-text-color-primary);
  font-size: 28px;
  font-weight: 600;
  line-height: 1.15;
}

.report-overview-card em {
  margin-left: 2px;
  color: var(--el-text-color-secondary);
  font-size: 14px;
  font-style: normal;
  font-weight: 400;
}

.report-operation-strip {
  display: grid;
  grid-template-columns: minmax(260px, 360px) minmax(0, 1fr);
  gap: 12px;
  margin-top: 12px;
}

.report-preview-workspace {
  display: grid;
  grid-template-columns: 320px minmax(0, 1fr);
  gap: 16px;
  margin-top: 14px;
}

.report-preview-side,
.report-paper-shell {
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 6px;
  background: var(--el-bg-color);
}

.report-preview-side {
  padding: 16px;
}

.report-preview-side span,
.report-preview-side small {
  display: block;
  color: var(--el-text-color-secondary);
  font-size: 12px;
  line-height: 1.5;
}

.report-preview-side strong {
  display: block;
  margin: 8px 0 6px;
  color: var(--el-text-color-primary);
  font-size: 18px;
  font-weight: 600;
  line-height: 1.35;
}

.report-preview-status {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.report-format-tabs {
  margin-top: 12px;
}

.report-output-list {
  display: grid;
  gap: 8px;
  margin-top: 8px;
}

.report-output-row {
  padding: 10px 12px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 4px;
  background: var(--el-fill-color-extra-light);
}

.report-output-row strong {
  margin: 3px 0 0;
  font-size: 13px;
}

.report-paper-shell {
  padding: 18px;
  background: #f6f8fb;
}

.report-paper {
  max-width: 840px;
  min-height: 640px;
  margin: 0 auto;
  overflow: hidden;
  border: 1px solid #d8dee8;
  border-radius: 4px;
  background: #fff;
  box-shadow: 0 16px 36px rgb(28 39 64 / 10%);
}

.report-paper-topline {
  height: 6px;
  background: var(--el-color-primary);
}

.report-paper--word .report-paper-topline {
  background: #3b82f6;
}

.report-paper--pdf .report-paper-topline {
  background: #ef4444;
}

.report-paper--excel .report-paper-topline {
  background: #22c55e;
}

.report-paper-header {
  display: flex;
  gap: 20px;
  align-items: flex-start;
  justify-content: space-between;
  padding: 28px 34px 18px;
  border-bottom: 1px solid #e5eaf2;
}

.report-paper-org {
  color: #6b7280;
  font-size: 13px;
  font-weight: 600;
}

.report-paper-header h2 {
  margin: 8px 0;
  color: #111827;
  font-size: 24px;
  font-weight: 700;
  line-height: 1.3;
}

.report-paper-header p,
.report-paper-section p {
  margin: 0;
  color: #4b5563;
  font-size: 13px;
  line-height: 1.7;
}

.report-paper-seal {
  min-width: 104px;
  padding: 10px;
  text-align: center;
  border: 1px solid #d7deea;
  border-radius: 4px;
}

.report-paper-seal span {
  color: #6b7280;
  font-size: 12px;
}

.report-paper-seal strong {
  display: block;
  margin-top: 4px;
  color: #111827;
  font-size: 15px;
  letter-spacing: 0;
}

.report-paper-meta {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 1px;
  margin: 18px 34px 0;
  overflow: hidden;
  border: 1px solid #e5eaf2;
  border-radius: 4px;
  background: #e5eaf2;
}

.report-paper-meta div {
  padding: 12px;
  background: #f9fafb;
}

.report-paper-meta span,
.report-paper-footer span,
.report-paper-table span {
  display: block;
  color: #6b7280;
  font-size: 12px;
}

.report-paper-meta strong,
.report-paper-footer strong,
.report-paper-table strong {
  display: block;
  margin-top: 4px;
  color: #111827;
  font-size: 13px;
  font-weight: 600;
}

.report-paper-section {
  padding: 18px 34px 0;
}

.report-paper-section h3 {
  margin: 0 0 10px;
  color: #111827;
  font-size: 15px;
  font-weight: 700;
}

.report-paper-table {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  overflow: hidden;
  border: 1px solid #e5eaf2;
  border-radius: 4px;
}

.report-paper-table div {
  padding: 10px 12px;
  border-right: 1px solid #e5eaf2;
  border-bottom: 1px solid #e5eaf2;
}

.result-table {
  overflow: hidden;
  border: 1px solid #dfe5ee;
  border-radius: 4px;
}

.result-table-head,
.result-table-row {
  display: grid;
  grid-template-columns: 1.3fr 1fr 0.8fr;
}

.result-table-head {
  color: #374151;
  font-size: 12px;
  font-weight: 700;
  background: #f3f6fa;
}

.result-table-head span,
.result-table-row span {
  min-width: 0;
  padding: 10px 12px;
  border-right: 1px solid #dfe5ee;
}

.result-table-row {
  color: #111827;
  font-size: 13px;
  border-top: 1px solid #dfe5ee;
}

.report-paper-footer {
  display: grid;
  grid-template-columns: 1fr 1.4fr;
  gap: 12px;
  margin: 24px 34px 30px;
  padding-top: 16px;
  border-top: 1px solid #e5eaf2;
}

.report-latest,
.report-lifecycle {
  min-height: 92px;
  padding: 14px 16px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 6px;
  background: var(--el-fill-color-extra-light);
}

.report-latest span,
.report-latest small {
  display: block;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.report-latest strong {
  display: block;
  margin: 8px 0 6px;
  color: var(--el-text-color-primary);
  font-size: 16px;
  font-weight: 600;
}

.report-lifecycle {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 10px;
  align-items: center;
}

.report-lifecycle-step {
  display: flex;
  gap: 8px;
  align-items: center;
  justify-content: center;
  min-height: 42px;
  color: var(--el-text-color-regular);
  font-size: 13px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 4px;
  background: var(--el-bg-color);
}

@media (max-width: 1200px) {
  .report-overview-grid,
  .report-preview-workspace,
  .report-operation-strip,
  .report-lifecycle {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .report-center-header {
    flex-direction: column;
  }

  .report-overview-grid,
  .report-preview-workspace,
  .report-operation-strip,
  .report-lifecycle {
    grid-template-columns: 1fr;
  }

  .report-paper-shell {
    padding: 10px;
  }

  .report-paper-header,
  .report-paper-footer {
    grid-template-columns: 1fr;
    flex-direction: column;
  }

  .report-paper-header,
  .report-paper-section {
    padding-right: 16px;
    padding-left: 16px;
  }

  .report-paper-meta,
  .report-paper-footer {
    margin-right: 16px;
    margin-left: 16px;
  }

  .report-paper-meta,
  .report-paper-table,
  .result-table-head,
  .result-table-row {
    grid-template-columns: 1fr;
  }
}
</style>
