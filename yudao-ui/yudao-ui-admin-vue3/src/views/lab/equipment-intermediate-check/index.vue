<template>
  <QualityRecordPage
    title="期间核查"
    base-url="/lab/equipment-intermediate-check"
    permission="lab:equipment-intermediate-check"
    no-field="checkNo"
    no-label="核查编号"
    name-field="checkMethod"
    name-label="核查方法"
    :fields="fields"
    :row-actions="rowActions"
  />
</template>

<script lang="ts" setup>
import QualityRecordPage from '@/views/lab/_components/QualityRecordPage.vue'

defineOptions({ name: 'LabEquipmentIntermediateCheck' })

const fields = [
  {
    prop: "equipmentId",
    label: "设备ID"
  },
  {
    prop: "checkDate",
    label: "核查日期"
  },
  {
    prop: "checkResult",
    label: "核查结果"
  },
  {
    prop: "checkRecord",
    label: "核查记录",
    type: "textarea"
  },
  {
    prop: "checkerId",
    label: "核查人"
  },
  {
    prop: "reviewerId",
    label: "复核人"
  }
]
const rowActions = [
  {
    label: "批准",
    url: "/lab/equipment-intermediate-check/approve",
    method: "put"
  },
  {
    label: "驳回",
    url: "/lab/equipment-intermediate-check/reject",
    method: "put"
  }
]
</script>
