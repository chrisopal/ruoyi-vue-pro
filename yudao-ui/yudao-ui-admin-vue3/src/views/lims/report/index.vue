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

const parseReportOutputFormats = (value?: string) => {
  if (!value) return []
  try {
    const manifest = JSON.parse(value)
    if (!Array.isArray(manifest?.outputs)) return []
    return manifest.outputs
      .map((item) => String(item?.format || '').toUpperCase())
      .filter(Boolean)
  } catch {
    return []
  }
}

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
  .report-operation-strip,
  .report-lifecycle {
    grid-template-columns: 1fr;
  }
}
</style>
