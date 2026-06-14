<template>
  <QualityRecordPage
    title="人员授权"
    base-url="/lab/personnel-authorization"
    permission="lab:personnel-authorization"
    no-field="authType"
    no-label="授权类型"
    name-field="authScope"
    name-label="授权范围"
    :fields="fields"
    :row-actions="rowActions"
  />
</template>

<script lang="ts" setup>
import QualityRecordPage from '@/views/lab/_components/QualityRecordPage.vue'

defineOptions({ name: 'LabPersonnelAuthorization' })

const fields = [
  {
    prop: "userId",
    label: "人员ID"
  },
  {
    prop: "methodId",
    label: "方法ID"
  },
  {
    prop: "equipmentId",
    label: "设备ID"
  },
  {
    prop: "authorizedBy",
    label: "授权人"
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
    prop: "fileUrl",
    label: "授权文件"
  }
]
const rowActions = [
  {
    label: "暂停",
    url: "/lab/personnel-authorization/suspend",
    method: "put"
  },
  {
    label: "撤销",
    url: "/lab/personnel-authorization/revoke",
    method: "put"
  }
]
</script>
