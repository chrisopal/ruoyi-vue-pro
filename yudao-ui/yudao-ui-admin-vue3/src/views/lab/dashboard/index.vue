<template>
  <ContentWrap>
    <div class="mb-18px flex items-center justify-between">
      <div>
        <div class="text-18px font-600">实验室数字化看板</div>
        <div class="mt-6px text-13px color-#606266">标准、证据、符合性、能力与设备状态概览</div>
      </div>
      <el-button :loading="loading" plain type="primary" @click="loadDashboard">
        <Icon class="mr-5px" icon="ep:refresh" />刷新
      </el-button>
    </div>

    <el-row :gutter="16">
      <el-col v-for="item in summaryCards" :key="item.label" :lg="6" :md="12" :sm="12" :xs="24">
        <div class="dashboard-card">
          <div class="flex items-center justify-between">
            <span class="text-13px color-#606266">{{ item.label }}</span>
            <Icon :icon="item.icon" class="text-20px color-#409eff" />
          </div>
          <div class="mt-14px text-26px font-600">{{ item.value }}</div>
          <div class="mt-8px text-12px color-#909399">{{ item.hint }}</div>
        </div>
      </el-col>
    </el-row>
  </ContentWrap>

  <ContentWrap>
    <el-row :gutter="16">
      <el-col :lg="12" :xs="24">
        <el-table v-loading="loading" :data="recentChecks" height="320">
          <el-table-column label="检查编号" min-width="150" prop="checkNo" show-overflow-tooltip />
          <el-table-column label="检查名称" min-width="180" prop="checkName" show-overflow-tooltip />
          <el-table-column align="center" label="状态" width="110" prop="status">
            <template #default="scope"><el-tag>{{ scope.row.status || '-' }}</el-tag></template>
          </el-table-column>
        </el-table>
      </el-col>
      <el-col :lg="12" :xs="24">
        <el-table v-loading="loading" :data="recentActions" height="320">
          <el-table-column label="措施编号" min-width="150" prop="actionNo" show-overflow-tooltip />
          <el-table-column label="标题" min-width="180" prop="title" show-overflow-tooltip />
          <el-table-column align="center" label="状态" width="110" prop="status">
            <template #default="scope"><el-tag>{{ scope.row.status || '-' }}</el-tag></template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>
  </ContentWrap>
</template>

<script lang="ts" setup>
import { LabStandardApi } from '@/api/lab/standard'
import { LabEvidenceLinkApi } from '@/api/lab/evidence-link'
import { LabQualityApi, LabQualityRecordVO } from '@/api/lab/quality'

defineOptions({ name: 'LabDashboard' })

const loading = ref(false)
const standardTotal = ref(0)
const evidenceTotal = ref(0)
const checkTotal = ref(0)
const actionTotal = ref(0)
const recentChecks = ref<LabQualityRecordVO[]>([])
const recentActions = ref<LabQualityRecordVO[]>([])

const summaryCards = computed(() => [
  { label: '标准体系', value: standardTotal.value, hint: '可用标准与条款的入口', icon: 'ep:collection' },
  { label: '证据关联', value: evidenceTotal.value, hint: '已登记的可追溯证据', icon: 'ep:connection' },
  { label: '符合性检查', value: checkTotal.value, hint: '自查、内审与认可准备', icon: 'ep:checked' },
  { label: '纠正措施', value: actionTotal.value, hint: 'CAPA 处理和验证记录', icon: 'ep:circle-check' }
])

const loadDashboard = async () => {
  loading.value = true
  try {
    const [standardPage, evidencePage, checkPage, actionPage] = await Promise.all([
      LabStandardApi.getStandardPage({ pageNo: 1, pageSize: 5 }),
      LabEvidenceLinkApi.getEvidenceLinkPage({ pageNo: 1, pageSize: 5 }),
      LabQualityApi.page('/lab/compliance-check', { pageNo: 1, pageSize: 6 }),
      LabQualityApi.page('/lab/corrective-action', { pageNo: 1, pageSize: 6 })
    ])
    standardTotal.value = standardPage.total || 0
    evidenceTotal.value = evidencePage.total || 0
    checkTotal.value = checkPage.total || 0
    actionTotal.value = actionPage.total || 0
    recentChecks.value = checkPage.list || []
    recentActions.value = actionPage.list || []
  } finally {
    loading.value = false
  }
}

onMounted(() => loadDashboard())
</script>

<style scoped>
.dashboard-card {
  min-height: 126px;
  margin-bottom: 16px;
  padding: 18px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 4px;
  background: var(--el-bg-color);
}
</style>
