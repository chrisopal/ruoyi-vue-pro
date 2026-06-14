<template>
  <QualityRecordPage
    title="符合性检查明细"
    base-url="/lab/compliance-check-item"
    permission="lab:compliance-check-item"
    no-field="clauseId"
    no-label="条款ID"
    name-field="findingDescription"
    name-label="发现描述"
    :fields="fields"
    :row-actions="rowActions"
  />
</template>

<script lang="ts" setup>
import QualityRecordPage from '@/views/lab/_components/QualityRecordPage.vue'

defineOptions({ name: 'LabComplianceCheckItem' })

const fields = [
  {
    prop: "checkId",
    label: "检查ID"
  },
  {
    prop: "checkResult",
    label: "检查结果"
  },
  {
    prop: "evidenceStatus",
    label: "证据状态"
  },
  {
    prop: "evidenceSummary",
    label: "证据摘要",
    type: "textarea"
  },
  {
    prop: "severity",
    label: "严重程度"
  },
  {
    prop: "dueDate",
    label: "整改期限"
  }
]
const rowActions = [
  {
    label: "生成不符合",
    url: "/lab/compliance-check-item/create-nonconformity",
    method: "post"
  }
]
</script>
