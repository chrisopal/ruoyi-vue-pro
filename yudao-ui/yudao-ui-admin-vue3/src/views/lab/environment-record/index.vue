<template>
  <QualityRecordPage
    title="环境记录"
    base-url="/lab/environment-record"
    permission="lab:environment-record"
    no-field="areaName"
    no-label="区域名称"
    name-field="abnormalDescription"
    name-label="异常记录"
    :fields="fields"
    :row-actions="rowActions"
  />
</template>

<script lang="ts" setup>
import QualityRecordPage from '@/views/lab/_components/QualityRecordPage.vue'

defineOptions({ name: 'LabEnvironmentRecord' })

const fields = [
  {
    prop: "areaId",
    label: "区域ID"
  },
  {
    prop: "recordTime",
    label: "记录时间"
  },
  {
    prop: "temperature",
    label: "温度"
  },
  {
    prop: "humidity",
    label: "湿度"
  },
  {
    prop: "pressure",
    label: "压力"
  },
  {
    prop: "cleanliness",
    label: "洁净度"
  },
  {
    prop: "dataSource",
    label: "数据来源"
  }
]
const rowActions = []
</script>
