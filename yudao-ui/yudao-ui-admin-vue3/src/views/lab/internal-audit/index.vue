<template>
  <QualityRecordPage
    title="内部审核"
    base-url="/lab/internal-audit"
    permission="lab:internal-audit"
    no-field="auditNo"
    no-label="审核编号"
    name-field="auditName"
    name-label="内审名称"
    :fields="fields"
    :row-actions="rowActions"
  />
</template>

<script lang="ts" setup>
import QualityRecordPage from '@/views/lab/_components/QualityRecordPage.vue'

defineOptions({ name: 'LabInternalAudit' })

const fields = [
  {
    prop: "auditScope",
    label: "范围"
  },
  {
    prop: "auditCriteria",
    label: "依据"
  },
  {
    prop: "auditLeaderId",
    label: "组长"
  },
  {
    prop: "plannedStartDate",
    label: "计划开始"
  },
  {
    prop: "plannedEndDate",
    label: "计划结束"
  },
  {
    prop: "summary",
    label: "总结",
    type: "textarea"
  },
  {
    prop: "reportFileUrl",
    label: "报告文件"
  }
]
const rowActions = [
  {
    label: "启动",
    url: "/lab/internal-audit/start",
    method: "put"
  },
  {
    label: "完成",
    url: "/lab/internal-audit/complete",
    method: "put"
  },
  {
    label: "关闭",
    url: "/lab/internal-audit/close",
    method: "put"
  }
]
</script>
