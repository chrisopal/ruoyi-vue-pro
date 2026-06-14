<template>
  <LimsWorkflowPage
    title="检测结果"
    subtitle="录入原始数据、结果值和单项结论，审核后进入报告生成"
    base-url="/lims/result"
    permission="lims:result"
    no-field="resultNo"
    no-label="结果编号"
    name-field="testItem"
    name-label="检测项目"
    :fields="fields"
    :row-actions="rowActions"
    :defaults="{ status: 'recorded', resultConclusion: 'pass' }"
  />
</template>

<script lang="ts" setup>
import LimsWorkflowPage from '@/views/lims/_components/LimsWorkflowPage.vue'

defineOptions({ name: 'LimsResult' })

const fields = [
  { prop: 'requestId', label: '需求ID' },
  { prop: 'sampleId', label: '样品ID' },
  { prop: 'taskId', label: '任务ID' },
  { prop: 'resultValue', label: '结果值' },
  { prop: 'resultUnit', label: '单位' },
  { prop: 'resultConclusion', label: '单项结论' },
  { prop: 'rawData', label: '原始数据', type: 'textarea', span: 24 },
  { prop: 'remark', label: '备注', type: 'textarea', span: 24, table: false }
]
const rowActions = [
  { label: '审核通过', url: '/lims/result/approve', method: 'put' }
] as const
</script>
