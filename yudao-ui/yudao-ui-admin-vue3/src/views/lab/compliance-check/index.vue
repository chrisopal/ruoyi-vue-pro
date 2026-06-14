<template>
  <QualityRecordPage
    title="符合性检查"
    base-url="/lab/compliance-check"
    permission="lab:compliance-check"
    no-field="checkNo"
    no-label="检查编号"
    name-field="checkName"
    name-label="检查名称"
    :fields="fields"
    :row-actions="rowActions"
  />
</template>

<script lang="ts" setup>
import QualityRecordPage from '@/views/lab/_components/QualityRecordPage.vue'

defineOptions({ name: 'LabComplianceCheck' })

const fields = [
  {
    prop: "checkType",
    label: "检查类型"
  },
  {
    prop: "standardId",
    label: "标准ID"
  },
  {
    prop: "checkScope",
    label: "检查范围"
  },
  {
    prop: "startDate",
    label: "开始日期"
  },
  {
    prop: "endDate",
    label: "结束日期"
  },
  {
    prop: "summary",
    label: "总结",
    type: "textarea"
  }
]
const rowActions = [
  {
    label: "生成明细",
    url: "/lab/compliance-check/generate-items",
    method: "post"
  },
  {
    label: "启动",
    url: "/lab/compliance-check/start",
    method: "put"
  },
  {
    label: "完成",
    url: "/lab/compliance-check/complete",
    method: "put"
  },
  {
    label: "关闭",
    url: "/lab/compliance-check/close",
    method: "put"
  },
  {
    label: "导出",
    url: "/lab/compliance-check/export-package",
    method: "get"
  }
]
</script>
