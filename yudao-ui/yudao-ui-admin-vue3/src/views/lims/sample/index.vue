<template>
  <LimsWorkflowPage
    title="样品管理"
    subtitle="需求受理后自动生成默认样品，也可补录多样品"
    base-url="/lims/sample"
    permission="lims:sample"
    no-field="sampleNo"
    no-label="样品编号"
    name-field="sampleName"
    name-label="样品名称"
    :fields="fields"
    :defaults="{ status: 'received' }"
  />
</template>

<script lang="ts" setup>
import LimsWorkflowPage from '@/views/lims/_components/LimsWorkflowPage.vue'

defineOptions({ name: 'LimsSample' })

const fields = [
  { prop: 'requestId', label: '需求ID' },
  { prop: 'requestNo', label: '需求编号' },
  { prop: 'sampleType', label: '样品类型' },
  { prop: 'sampleSpec', label: '规格型号' },
  { prop: 'sampleQty', label: '样品数量' },
  { prop: 'receivedDate', label: '接收日期' },
  { prop: 'storageCondition', label: '保存条件' },
  { prop: 'remark', label: '备注', type: 'textarea', span: 24, table: false }
]
</script>
