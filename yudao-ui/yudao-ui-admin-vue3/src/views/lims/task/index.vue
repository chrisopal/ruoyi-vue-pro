<template>
  <LimsWorkflowPage
    title="检测任务"
    subtitle="由场景方案包的检测项目配置自动生成，也支持手工补充"
    base-url="/lims/task"
    permission="lims:task"
    no-field="taskNo"
    no-label="任务编号"
    name-field="taskName"
    name-label="任务名称"
    :fields="fields"
    :row-actions="rowActions"
    :defaults="{ status: 'assigned' }"
  />
</template>

<script lang="ts" setup>
import LimsWorkflowPage from '@/views/lims/_components/LimsWorkflowPage.vue'

defineOptions({ name: 'LimsTask' })

const fields = [
  { prop: 'requestId', label: '需求ID' },
  { prop: 'requestNo', label: '需求编号' },
  { prop: 'sampleId', label: '样品ID' },
  { prop: 'sampleNo', label: '样品编号' },
  { prop: 'taskStatus', label: '生命周期' },
  { prop: 'scheduleStatus', label: '排程状态' },
  { prop: 'reviewStatus', label: '复核状态' },
  { prop: 'reportEligible', label: '可报告' },
  { prop: 'testItem', label: '检测项目' },
  { prop: 'methodCode', label: '方法编码' },
  { prop: 'methodName', label: '方法名称' },
  { prop: 'assignedUserId', label: '执行人' },
  { prop: 'durationMinutes', label: '预计分钟' },
  { prop: 'actualStartTime', label: '实际开始' },
  { prop: 'actualEndTime', label: '实际结束' },
  { prop: 'blockReason', label: '阻断原因' },
  { prop: 'methodSnapshot', label: '方法快照', type: 'textarea', span: 24, table: false },
  { prop: 'readinessSnapshot', label: '就绪快照', type: 'textarea', span: 24, table: false },
  { prop: 'remark', label: '备注', type: 'textarea', span: 24, table: false }
]
const rowActions = [
  { label: '开始检测', url: '/lims/task/start', method: 'put' }
] as const
</script>
