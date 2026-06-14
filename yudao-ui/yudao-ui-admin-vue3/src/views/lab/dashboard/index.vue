<template>
  <ContentWrap>
    <div class="dashboard-header">
      <div>
        <div class="dashboard-title">评审与运营看板</div>
        <div class="dashboard-subtitle">申请准备度、复评审风险、能力范围覆盖、整改闭环</div>
      </div>
      <el-button :loading="loading" plain type="primary" @click="loadDashboard">
        <Icon class="mr-5px" icon="ep:refresh" />刷新
      </el-button>
    </div>

    <div class="score-grid">
      <div v-for="score in scoreCards" :key="score.code" class="score-card">
        <el-progress
          :color="score.color"
          :percentage="score.value"
          :stroke-width="10"
          type="dashboard"
        />
        <div class="score-info">
          <div class="score-label">{{ score.label }}</div>
          <div class="score-hint">{{ score.hint }}</div>
        </div>
      </div>
    </div>
  </ContentWrap>

  <ContentWrap>
    <el-row :gutter="12">
      <el-col v-for="item in dashboard?.metrics || []" :key="item.code" :lg="6" :md="8" :sm="12" :xs="24">
        <div class="metric-card">
          <div class="metric-topline">
            <span>{{ item.label }}</span>
            <el-tag :type="tagType(item.level)" effect="light" size="small">{{ levelText(item.level) }}</el-tag>
          </div>
          <div class="metric-value">
            {{ formatNumber(item.value) }}<span>{{ item.unit }}</span>
          </div>
          <div class="metric-hint">{{ item.hint || '-' }}</div>
        </div>
      </el-col>
    </el-row>
  </ContentWrap>

  <el-row :gutter="16">
    <el-col :lg="12" :xs="24">
      <ContentWrap>
        <div class="section-title">执行阶段</div>
        <el-table v-loading="loading" :data="dashboard?.workflowStages || []" height="330">
          <el-table-column label="阶段" min-width="110" prop="label" />
          <el-table-column align="right" label="总量" width="76" prop="total" />
          <el-table-column align="right" label="完成" width="76" prop="done" />
          <el-table-column align="right" label="阻塞" width="76" prop="blocked">
            <template #default="scope">
              <el-tag :type="scope.row.blocked > 0 ? 'danger' : 'success'" effect="light" size="small">
                {{ scope.row.blocked }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="完成率" min-width="150">
            <template #default="scope">
              <el-progress :percentage="scope.row.completionRate || 0" :stroke-width="8" />
            </template>
          </el-table-column>
        </el-table>
      </ContentWrap>
    </el-col>

    <el-col :lg="12" :xs="24">
      <ContentWrap>
        <div class="section-title">能力范围覆盖</div>
        <el-table v-loading="loading" :data="dashboard?.capabilityCoverage || []" height="330">
          <el-table-column label="方向" min-width="110">
            <template #default="scope">
              <div class="font-600">{{ scope.row.domainName || scope.row.domainCode }}</div>
              <div class="table-subtext">{{ scope.row.domainCode }}</div>
            </template>
          </el-table-column>
          <el-table-column align="right" label="方向包" width="82" prop="publishedPackCount" />
          <el-table-column align="right" label="设备" width="76" prop="enabledEquipmentCount" />
          <el-table-column align="right" label="任务" width="76" prop="taskCount" />
          <el-table-column align="right" label="报告" width="76" prop="reportCount" />
          <el-table-column label="覆盖率" min-width="138">
            <template #default="scope">
              <el-progress :percentage="scope.row.coverageRate || 0" :stroke-width="8" />
            </template>
          </el-table-column>
        </el-table>
      </ContentWrap>
    </el-col>
  </el-row>

  <el-row :gutter="16">
    <el-col :lg="14" :xs="24">
      <ContentWrap>
        <div class="section-title">风险清单</div>
        <el-table v-loading="loading" :data="dashboard?.risks || []" height="360">
          <el-table-column label="等级" width="88">
            <template #default="scope">
              <el-tag :type="tagType(scope.row.level)" effect="light">{{ levelText(scope.row.level) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="风险" min-width="210">
            <template #default="scope">
              <div class="font-600">{{ scope.row.title }}</div>
              <div class="table-subtext">{{ scope.row.description }}</div>
            </template>
          </el-table-column>
          <el-table-column label="领域" min-width="120" prop="ownerContext" />
          <el-table-column align="right" label="数量" width="76" prop="relatedCount" />
          <el-table-column label="动作" min-width="180" prop="actionText" show-overflow-tooltip />
        </el-table>
      </ContentWrap>
    </el-col>

    <el-col :lg="10" :xs="24">
      <ContentWrap>
        <div class="section-title">最近报告</div>
        <el-table v-loading="loading" :data="dashboard?.recentReports || []" height="360">
          <el-table-column label="报告编号" min-width="150" prop="reportNo" show-overflow-tooltip />
          <el-table-column label="状态" width="96">
            <template #default="scope">
              <el-tag :type="scope.row.status === 'issued' ? 'success' : 'warning'" effect="light">
                {{ reportStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="模板" min-width="90" prop="templateVersion" show-overflow-tooltip />
          <el-table-column label="签发时间" min-width="150" prop="issuedTime" show-overflow-tooltip />
        </el-table>
      </ContentWrap>
    </el-col>
  </el-row>
</template>

<script lang="ts" setup>
import { LabDashboardApi } from '@/api/lab/dashboard'
import type { LabOperationsDashboardVO } from '@/api/lab/dashboard'

defineOptions({ name: 'LabDashboard' })

const loading = ref(false)
const dashboard = ref<LabOperationsDashboardVO>()

const scoreCards = computed(() => {
  const data = dashboard.value
  return [
    {
      code: 'readiness',
      label: '申请准备度',
      value: data?.applicationReadinessScore || 0,
      color: progressColor(data?.applicationReadinessScore || 0, false),
      hint: 'CNAS/CMA 申请基础'
    },
    {
      code: 'risk',
      label: '复评审风险',
      value: data?.reassessmentRiskScore || 0,
      color: progressColor(data?.reassessmentRiskScore || 0, true),
      hint: '风险越低越好'
    },
    {
      code: 'coverage',
      label: '能力范围覆盖',
      value: data?.capabilityCoverageRate || 0,
      color: progressColor(data?.capabilityCoverageRate || 0, false),
      hint: '方向包、设备、任务、报告'
    },
    {
      code: 'capa',
      label: '整改闭环',
      value: data?.correctionClosureRate || 0,
      color: progressColor(data?.correctionClosureRate || 0, false),
      hint: 'NC/CAPA 完成度'
    }
  ]
})

const loadDashboard = async () => {
  loading.value = true
  try {
    dashboard.value = await LabDashboardApi.getOperationsDashboard()
  } finally {
    loading.value = false
  }
}

const formatNumber = (value?: number) => Number(value || 0).toLocaleString()

const tagType = (level?: string) => {
  if (level === 'danger') return 'danger'
  if (level === 'warning') return 'warning'
  if (level === 'success') return 'success'
  return 'info'
}

const levelText = (level?: string) => {
  if (level === 'danger') return '高'
  if (level === 'warning') return '中'
  if (level === 'success') return '稳'
  return '低'
}

const reportStatusText = (status?: string) => {
  if (status === 'issued') return '已签发'
  if (status === 'generated') return '已生成'
  if (status === 'draft') return '草稿'
  return status || '-'
}

const progressColor = (value: number, reverse: boolean) => {
  const normalized = reverse ? 100 - value : value
  if (normalized >= 80) return '#67c23a'
  if (normalized >= 60) return '#e6a23c'
  return '#f56c6c'
}

onMounted(() => loadDashboard())
</script>

<style scoped>
.dashboard-header {
  display: flex;
  gap: 16px;
  align-items: center;
  justify-content: space-between;
}

.dashboard-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.dashboard-subtitle {
  margin-top: 6px;
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.score-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-top: 18px;
}

.score-card {
  display: flex;
  gap: 14px;
  align-items: center;
  min-height: 124px;
  padding: 14px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 4px;
}

.score-info {
  min-width: 0;
}

.score-label {
  font-size: 15px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.score-hint {
  margin-top: 8px;
  font-size: 12px;
  line-height: 1.5;
  color: var(--el-text-color-secondary);
}

.metric-card {
  min-height: 122px;
  padding: 14px;
  margin-bottom: 12px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 4px;
}

.metric-topline {
  display: flex;
  gap: 8px;
  align-items: center;
  justify-content: space-between;
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.metric-value {
  margin-top: 12px;
  font-size: 26px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.metric-value span {
  margin-left: 4px;
  font-size: 13px;
  font-weight: 400;
  color: var(--el-text-color-secondary);
}

.metric-hint,
.table-subtext {
  margin-top: 6px;
  font-size: 12px;
  line-height: 1.45;
  color: var(--el-text-color-secondary);
}

.section-title {
  margin-bottom: 12px;
  font-size: 15px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

@media (max-width: 1200px) {
  .score-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .dashboard-header,
  .score-card {
    align-items: flex-start;
    flex-direction: column;
  }

  .score-grid {
    grid-template-columns: 1fr;
  }
}
</style>
