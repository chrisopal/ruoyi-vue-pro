<template>
  <QualityRecordPage
    title="纠正措施"
    base-url="/lab/corrective-action"
    permission="lab:corrective-action"
    no-field="actionNo"
    no-label="措施编号"
    name-field="rootCause"
    name-label="原因分析"
    :fields="fields"
    :row-actions="rowActions"
  />
</template>

<script lang="ts" setup>
import QualityRecordPage from '@/views/lab/_components/QualityRecordPage.vue'

defineOptions({ name: 'LabCorrectiveAction' })

const fields = [
  {
    prop: "nonconformityId",
    label: "不符合项ID"
  },
  {
    prop: "correction",
    label: "纠正",
    type: "textarea"
  },
  {
    prop: "correctiveAction",
    label: "纠正措施",
    type: "textarea"
  },
  {
    prop: "preventiveAction",
    label: "预防措施",
    type: "textarea"
  },
  {
    prop: "plannedFinishDate",
    label: "计划完成"
  },
  {
    prop: "verificationResult",
    label: "验证结果",
    type: "textarea"
  }
]
const rowActions = [
  {
    label: "提交",
    url: "/lab/corrective-action/submit",
    method: "put"
  },
  {
    label: "验证",
    url: "/lab/corrective-action/verify",
    method: "put"
  },
  {
    label: "驳回",
    url: "/lab/corrective-action/reject",
    method: "put"
  }
]
</script>
