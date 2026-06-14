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
import type { LimsWorkflowVO } from '@/api/lims/workflow'
import LimsWorkflowPage from '@/views/lims/_components/LimsWorkflowPage.vue'

defineOptions({ name: 'LimsTask' })

const fields = [
  { prop: 'taskStatus', label: '生命周期' },
  { prop: 'scheduleStatus', label: '排程状态' },
  { prop: 'qcStatus', label: '质控状态' },
  { prop: 'reviewStatus', label: '复核状态' },
  { prop: 'testItem', label: '检测项目' },
  { prop: 'equipmentCode', label: '设备编码' },
  { prop: 'requestId', label: '需求ID' },
  { prop: 'requestNo', label: '需求编号' },
  { prop: 'sampleId', label: '样品ID' },
  { prop: 'sampleNo', label: '样品编号' },
  { prop: 'reportEligible', label: '可报告' },
  { prop: 'methodCode', label: '方法编码' },
  { prop: 'methodName', label: '方法名称' },
  { prop: 'equipmentId', label: '设备ID' },
  { prop: 'assignedUserId', label: '执行人' },
  { prop: 'plannedStartTime', label: '计划开始' },
  { prop: 'plannedEndTime', label: '计划结束' },
  { prop: 'durationMinutes', label: '预计分钟' },
  { prop: 'actualStartTime', label: '实际开始' },
  { prop: 'actualEndTime', label: '实际结束' },
  { prop: 'blockReason', label: '阻断原因' },
  { prop: 'methodSnapshot', label: '方法快照', type: 'textarea', span: 24, table: false },
  { prop: 'readinessSnapshot', label: '就绪快照', type: 'textarea', span: 24, table: false },
  { prop: 'remark', label: '备注', type: 'textarea', span: 24, table: false }
]
const rowActions = [
  { label: '快速排程', url: '/lims/task/schedule-default', method: 'post' },
  { label: '确认就绪', url: '/lims/task/ready', method: 'put' },
  { label: '开始检测', url: '/lims/task/start', method: 'put' },
  {
    label: '原始记录',
    url: '/lims/task/raw-record',
    method: 'post',
    formTitle: '提交原始记录',
    defaults: (row: LimsWorkflowVO) => ({
      taskId: row.id,
      recordType: 'manual_entry',
      recordJson: JSON.stringify({ taskNo: row.taskNo, testItem: row.testItem, capturedAt: new Date().toISOString() }),
      versionNo: 1,
      status: 'submitted',
      remark: '原始记录已提交'
    }),
    fields: [
      { prop: 'taskId', label: '任务ID', hidden: true },
      { prop: 'recordType', label: '记录类型' },
      { prop: 'recordJson', label: '原始记录JSON', type: 'textarea', span: 24 },
      { prop: 'attachmentUrl', label: '附件地址', span: 24 },
      { prop: 'versionNo', label: '版本号' },
      { prop: 'submittedBy', label: '提交人ID' },
      { prop: 'status', label: '记录状态' },
      { prop: 'remark', label: '提交说明', type: 'textarea', span: 24 }
    ]
  },
  {
    label: '提交QC',
    url: '/lims/task/qc-record',
    method: 'post',
    formTitle: '提交质控记录',
    defaults: (row: LimsWorkflowVO) => ({
      taskId: row.id,
      qcType: 'routine_qc',
      qcRuleSnapshot: row.methodSnapshot || '{}',
      qcDataJson: '{}',
      qcResult: 'approved',
      reviewComment: 'QC通过，待技术复核'
    }),
    fields: [
      { prop: 'taskId', label: '任务ID', hidden: true },
      { prop: 'qcType', label: '质控类型' },
      { prop: 'qcRuleSnapshot', label: '质控规则快照', type: 'textarea', span: 24 },
      { prop: 'qcDataJson', label: '质控数据JSON', type: 'textarea', span: 24 },
      {
        prop: 'qcResult',
        label: '质控结果',
        type: 'select',
        options: [
          { label: '通过', value: 'approved' },
          { label: '不通过', value: 'rejected' }
        ]
      },
      { prop: 'reviewComment', label: '质控意见', type: 'textarea', span: 24 }
    ]
  },
  {
    label: '复核通过',
    url: '/lims/task/approve',
    method: 'put',
    formTitle: '技术复核通过',
    defaults: (row: LimsWorkflowVO) => ({
      taskId: row.id,
      reviewType: 'technical',
      snapshotHash: row.methodSnapshot || row.readinessSnapshot || '',
      remark: '批准进入报告'
    }),
    fields: [
      { prop: 'taskId', label: '任务ID', hidden: true },
      { prop: 'reviewType', label: '复核类型' },
      { prop: 'reviewerId', label: '复核人ID' },
      { prop: 'snapshotHash', label: '复核快照哈希', span: 24 },
      { prop: 'remark', label: '复核意见', type: 'textarea', span: 24 }
    ]
  },
  {
    label: '复核驳回',
    url: '/lims/task/reject',
    method: 'put',
    formTitle: '技术复核驳回',
    defaults: (row: LimsWorkflowVO) => ({
      taskId: row.id,
      reviewType: 'technical',
      snapshotHash: row.methodSnapshot || row.readinessSnapshot || '',
      remark: '数据需返工'
    }),
    fields: [
      { prop: 'taskId', label: '任务ID', hidden: true },
      { prop: 'reviewType', label: '复核类型' },
      { prop: 'reviewerId', label: '复核人ID' },
      { prop: 'snapshotHash', label: '复核快照哈希', span: 24 },
      { prop: 'remark', label: '驳回原因', type: 'textarea', span: 24 }
    ]
  }
] as const
</script>
