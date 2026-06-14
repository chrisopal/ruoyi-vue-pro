<template>
  <QualityRecordPage
    title="不符合项"
    base-url="/lab/nonconformity"
    permission="lab:nonconformity"
    no-field="ncNo"
    no-label="不符合编号"
    name-field="title"
    name-label="标题"
    :fields="fields"
    :row-actions="rowActions"
  />
</template>

<script lang="ts" setup>
import QualityRecordPage from '@/views/lab/_components/QualityRecordPage.vue'

defineOptions({ name: 'LabNonconformity' })

const fields = [
  {
    prop: "sourceType",
    label: "来源"
  },
  {
    prop: "sourceId",
    label: "来源ID"
  },
  {
    prop: "clauseId",
    label: "条款ID"
  },
  {
    prop: "description",
    label: "描述",
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
    label: "关闭",
    url: "/lab/nonconformity/close",
    method: "put"
  }
]
</script>
