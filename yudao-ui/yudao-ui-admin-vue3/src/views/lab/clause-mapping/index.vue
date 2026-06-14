<template>
  <QualityRecordPage
    title="条款功能映射"
    base-url="/lab/clause-mapping"
    permission="lab:clause-mapping"
    no-field="moduleCode"
    no-label="模块编码"
    name-field="moduleName"
    name-label="模块名称"
    :fields="fields"
    :row-actions="rowActions"
  />
</template>

<script lang="ts" setup>
import QualityRecordPage from '@/views/lab/_components/QualityRecordPage.vue'

defineOptions({ name: 'LabClauseMapping' })

const fields = [
  {
    prop: "clauseId",
    label: "条款ID"
  },
  {
    prop: "functionCode",
    label: "功能编码"
  },
  {
    prop: "functionName",
    label: "功能名称"
  },
  {
    prop: "evidenceType",
    label: "证据类型"
  },
  {
    prop: "evidenceTable",
    label: "证据表"
  },
  {
    prop: "evidenceDescription",
    label: "证据说明",
    type: "textarea"
  }
]
const rowActions = []
</script>
