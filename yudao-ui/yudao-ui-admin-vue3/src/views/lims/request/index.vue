<template>
  <LimsWorkflowPage
    title="检测需求"
    subtitle="选择检测场景方案包后，后续样品、任务、结果和报告按配置驱动"
    base-url="/lims/request"
    permission="lims:request"
    no-field="requestNo"
    no-label="需求编号"
    name-field="requestName"
    name-label="需求名称"
    :fields="fields"
    :row-actions="rowActions"
    :defaults="{ requestType: 'internal', status: 'draft', priority: 'normal' }"
  />
</template>

<script lang="ts" setup>
import LimsWorkflowPage from '@/views/lims/_components/LimsWorkflowPage.vue'
import { LabDomainPackApi } from '@/api/lab/domain-pack'

defineOptions({ name: 'LimsRequest' })

const packOptions = ref<{ label: string; value: number }[]>([])
const fields = computed(() => [
  { prop: 'requestType', label: '需求类型' },
  { prop: 'customerName', label: '客户/部门' },
  { prop: 'requesterName', label: '提出人' },
  { prop: 'domainCode', label: '检测方向' },
  { prop: 'domainPackId', label: '场景方案包', type: 'select', options: packOptions.value },
  { prop: 'priority', label: '优先级' },
  { prop: 'dueDate', label: '期望完成' },
  { prop: 'scenarioConfig', label: '场景配置覆盖', type: 'textarea', span: 24, table: false },
  { prop: 'remark', label: '备注', type: 'textarea', span: 24, table: false }
])
const rowActions = [
  { label: '提交', url: '/lims/request/submit', method: 'put', permission: 'lims:request:update' },
  { label: '受理', url: '/lims/request/accept', method: 'put', permission: 'lims:request:update' },
  { label: '生成任务', url: '/lims/request/generate-tasks', method: 'post', permission: 'lims:request:update' },
  { label: '执行计划', kind: 'executionPlan', permission: 'lims:request:query' },
  { label: '生成报告', url: '/lims/request/generate-report', method: 'post', permission: 'lims:report:create' }
] as const

onMounted(async () => {
  const data = await LabDomainPackApi.getDomainPackPage({ pageNo: 1, pageSize: 100, status: 'published' })
  packOptions.value = (data.list || []).map((item) => ({
    label: `${item.packName} / ${item.packVersion}（${item.industry || item.packCode}）`,
    value: item.id!
  }))
})
</script>
