<template>
  <LimsWorkflowPage
    title="检测报告"
    subtitle="由检测需求和结果自动生成结构化报告，可签发形成闭环"
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
import LimsWorkflowPage from '@/views/lims/_components/LimsWorkflowPage.vue'

defineOptions({ name: 'LimsReport' })

const fields = [
  { prop: 'requestId', label: '需求ID' },
  { prop: 'requestNo', label: '需求编号' },
  { prop: 'templateId', label: '模板ID' },
  { prop: 'templateVersion', label: '模板版本' },
  { prop: 'conclusion', label: '报告结论' },
  { prop: 'issuedTime', label: '签发时间' },
  { prop: 'reportOutput', label: '输出清单', type: 'textarea', display: 'reportOutput', span: 24 },
  { prop: 'fileUrl', label: '文件地址' },
  { prop: 'reportContent', label: '报告内容', type: 'textarea', span: 24 },
  { prop: 'remark', label: '备注', type: 'textarea', span: 24, table: false }
]
const rowActions = [
  { label: '签发', url: '/lims/report/issue', method: 'put', permission: 'lims:report:update' }
] as const
</script>
