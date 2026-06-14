<template>
  <QualityRecordPage
    title="人员能力"
    base-url="/lab/personnel-competence"
    permission="lab:personnel-competence"
    no-field="certificateNo"
    no-label="证书编号"
    name-field="competenceItem"
    name-label="能力项目"
    :fields="fields"
    :row-actions="rowActions"
  />
</template>

<script lang="ts" setup>
import QualityRecordPage from '@/views/lab/_components/QualityRecordPage.vue'

defineOptions({ name: 'LabPersonnelCompetence' })

const fields = [
  {
    prop: "userId",
    label: "人员ID"
  },
  {
    prop: "userName",
    label: "人员姓名"
  },
  {
    prop: "competenceType",
    label: "能力类型"
  },
  {
    prop: "validFrom",
    label: "有效期起"
  },
  {
    prop: "validTo",
    label: "有效期止"
  },
  {
    prop: "assessmentResult",
    label: "评价结果"
  },
  {
    prop: "certificateFileUrl",
    label: "证书文件"
  }
]
const rowActions = []
</script>
