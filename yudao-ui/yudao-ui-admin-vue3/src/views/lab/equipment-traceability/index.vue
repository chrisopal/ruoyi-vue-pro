<template>
  <QualityRecordPage
    title="设备溯源"
    base-url="/lab/equipment-traceability"
    permission="lab:equipment-traceability"
    no-field="certificateNo"
    no-label="证书编号"
    name-field="calibrationOrg"
    name-label="校准机构"
    :fields="fields"
    :row-actions="rowActions"
  />
</template>

<script lang="ts" setup>
import QualityRecordPage from '@/views/lab/_components/QualityRecordPage.vue'

defineOptions({ name: 'LabEquipmentTraceability' })

const fields = [
  {
    prop: "equipmentId",
    label: "设备ID"
  },
  {
    prop: "traceabilityType",
    label: "溯源类型"
  },
  {
    prop: "calibrationDate",
    label: "校准日期"
  },
  {
    prop: "validTo",
    label: "有效期至"
  },
  {
    prop: "result",
    label: "结果"
  },
  {
    prop: "uncertainty",
    label: "不确定度"
  },
  {
    prop: "certificateFileUrl",
    label: "证书文件"
  }
]
const rowActions = []
</script>
