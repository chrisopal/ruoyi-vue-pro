<template>
  <QualityRecordPage
    title="方法验证"
    base-url="/lab/method-validation"
    permission="lab:method-validation"
    no-field="validationNo"
    no-label="验证编号"
    name-field="validationType"
    name-label="验证类型"
    :fields="fields"
    :row-actions="rowActions"
  />
</template>

<script lang="ts" setup>
import QualityRecordPage from '@/views/lab/_components/QualityRecordPage.vue'

defineOptions({ name: 'LabMethodValidation' })

const fields = [
  {
    prop: "methodId",
    label: "方法ID"
  },
  {
    prop: "validationDate",
    label: "验证日期"
  },
  {
    prop: "validationItems",
    label: "验证项目",
    type: "textarea"
  },
  {
    prop: "conclusion",
    label: "结论"
  },
  {
    prop: "responsibleUserId",
    label: "负责人"
  },
  {
    prop: "reviewerId",
    label: "复核人"
  },
  {
    prop: "reportFileUrl",
    label: "报告文件"
  }
]
const rowActions = [
  {
    label: "提交",
    url: "/lab/method-validation/submit-review",
    method: "put"
  },
  {
    label: "批准",
    url: "/lab/method-validation/approve",
    method: "put"
  },
  {
    label: "驳回",
    url: "/lab/method-validation/reject",
    method: "put"
  }
]
</script>
