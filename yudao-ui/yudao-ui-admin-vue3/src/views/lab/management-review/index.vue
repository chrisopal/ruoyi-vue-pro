<template>
  <QualityRecordPage
    title="管理评审"
    base-url="/lab/management-review"
    permission="lab:management-review"
    no-field="reviewNo"
    no-label="评审编号"
    name-field="reviewName"
    name-label="评审名称"
    :fields="fields"
    :row-actions="rowActions"
  />
</template>

<script lang="ts" setup>
import QualityRecordPage from '@/views/lab/_components/QualityRecordPage.vue'

defineOptions({ name: 'LabManagementReview' })

const fields = [
  {
    prop: "reviewDate",
    label: "评审日期"
  },
  {
    prop: "hostUserId",
    label: "主持人"
  },
  {
    prop: "participants",
    label: "参与人员",
    type: "textarea"
  },
  {
    prop: "inputSummary",
    label: "输入摘要",
    type: "textarea"
  },
  {
    prop: "outputDecision",
    label: "输出决议",
    type: "textarea"
  },
  {
    prop: "improvementActions",
    label: "改进措施",
    type: "textarea"
  },
  {
    prop: "reportFileUrl",
    label: "报告文件"
  }
]
const rowActions = [
  {
    label: "完成",
    url: "/lab/management-review/complete",
    method: "put"
  },
  {
    label: "关闭",
    url: "/lab/management-review/close",
    method: "put"
  }
]
</script>
