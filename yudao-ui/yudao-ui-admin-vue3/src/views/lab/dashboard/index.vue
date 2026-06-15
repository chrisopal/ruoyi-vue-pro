<template>
  <ContentWrap>
    <div class="dashboard-hero">
      <div class="dashboard-heading">
        <div class="dashboard-title">评审与运营看板</div>
        <div class="dashboard-subtitle">
          汇总申请准备度、复评审风险、能力范围覆盖和整改闭环，帮助实验室优先处理影响评审的事项
        </div>
        <div class="dashboard-tags">
          <el-tag effect="plain">CNAS/CMA</el-tag>
          <el-tag effect="plain" type="success">证据链</el-tag>
          <el-tag effect="plain" type="warning">风险闭环</el-tag>
        </div>
      </div>
      <div class="dashboard-focus">
        <div class="focus-label">当前关注</div>
        <div class="focus-title">{{ topRisk?.title || '暂无高优先级风险' }}</div>
        <div class="focus-desc">{{ topRisk?.actionText || '保持方向包、设备、证据和报告链路持续更新' }}</div>
        <el-button :loading="loading" plain type="primary" @click="loadDashboard">
          <Icon class="mr-5px" icon="ep:refresh" />刷新
        </el-button>
      </div>
    </div>

    <div class="health-grid">
      <div v-for="score in scoreCards" :key="score.code" class="health-card">
        <div class="health-card-topline">
          <span>{{ score.label }}</span>
          <el-tag :type="score.levelType" effect="light" size="small">{{ score.levelText }}</el-tag>
        </div>
        <div class="health-value">{{ score.value }}<em>%</em></div>
        <el-progress
          :color="score.color"
          :percentage="score.value"
          :show-text="false"
          :stroke-width="10"
        />
        <div class="health-hint">{{ score.hint }}</div>
      </div>
    </div>
  </ContentWrap>

  <ContentWrap>
    <el-row :gutter="12">
      <el-col v-for="item in dashboard?.metrics || []" :key="item.code" :lg="6" :md="8" :sm="12" :xs="24">
        <div class="metric-card" :class="`metric-card--${item.level || 'info'}`">
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
        <div class="section-header">
          <div>
            <div class="section-title">执行阶段</div>
            <div class="section-subtitle">从检测需求到报告签发的阶段阻塞情况</div>
          </div>
          <el-tag effect="plain">{{ workflowBlockedTotal }} 个阻塞</el-tag>
        </div>
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
        <div class="section-header">
          <div>
            <div class="section-title">能力范围覆盖</div>
            <div class="section-subtitle">方向包、设备、任务和报告对能力范围的支撑</div>
          </div>
          <el-tag effect="plain" type="success">{{ dashboard?.capabilityCoverage?.length || 0 }} 个方向</el-tag>
        </div>
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
        <div class="section-header">
          <div>
            <div class="section-title">风险清单</div>
            <div class="section-subtitle">优先处理会影响申请、复评审和报告交付的风险</div>
          </div>
          <el-tag :type="riskTotal > 0 ? 'warning' : 'success'" effect="light">{{ riskTotal }} 项</el-tag>
        </div>
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
        <div class="section-header">
          <div>
            <div class="section-title">最近报告</div>
            <div class="section-subtitle">已生成或已签发报告的交付状态</div>
          </div>
          <el-tag effect="plain">{{ dashboard?.recentReports?.length || 0 }} 份</el-tag>
        </div>
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
      hint: 'CNAS/CMA 申请基础',
      ...scoreLevel(data?.applicationReadinessScore || 0, false)
    },
    {
      code: 'risk',
      label: '复评审风险',
      value: data?.reassessmentRiskScore || 0,
      color: progressColor(data?.reassessmentRiskScore || 0, true),
      hint: '风险越低越好',
      ...scoreLevel(data?.reassessmentRiskScore || 0, true)
    },
    {
      code: 'coverage',
      label: '能力范围覆盖',
      value: data?.capabilityCoverageRate || 0,
      color: progressColor(data?.capabilityCoverageRate || 0, false),
      hint: '方向包、设备、任务、报告',
      ...scoreLevel(data?.capabilityCoverageRate || 0, false)
    },
    {
      code: 'capa',
      label: '整改闭环',
      value: data?.correctionClosureRate || 0,
      color: progressColor(data?.correctionClosureRate || 0, false),
      hint: 'NC/CAPA 完成度',
      ...scoreLevel(data?.correctionClosureRate || 0, false)
    }
  ]
})

const topRisk = computed(() => {
  const risks = dashboard.value?.risks || []
  return risks.find((item) => item.level === 'danger') || risks.find((item) => item.level === 'warning') || risks[0]
})

const workflowBlockedTotal = computed(() =>
  (dashboard.value?.workflowStages || []).reduce((total, item) => total + Number(item.blocked || 0), 0)
)

const riskTotal = computed(() =>
  (dashboard.value?.risks || []).reduce((total, item) => total + Number(item.relatedCount || 0), 0)
)

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

const scoreLevel = (value: number, reverse: boolean) => {
  const normalized = reverse ? 100 - value : value
  if (normalized >= 80) return { levelText: '稳', levelType: 'success' }
  if (normalized >= 60) return { levelText: '中', levelType: 'warning' }
  return { levelText: '警', levelType: 'danger' }
}

onMounted(() => loadDashboard())
</script>

<style scoped>
.dashboard-hero {
  display: flex;
  gap: 16px;
  align-items: stretch;
  justify-content: space-between;
}

.dashboard-heading {
  min-width: 0;
}

.dashboard-title {
  color: var(--el-text-color-primary);
  font-size: 20px;
  font-weight: 600;
  line-height: 1.35;
}

.dashboard-subtitle {
  max-width: 760px;
  margin-top: 6px;
  color: var(--el-text-color-secondary);
  font-size: 13px;
  line-height: 1.5;
}

.dashboard-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.dashboard-focus {
  width: 360px;
  min-height: 126px;
  padding: 14px 16px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 6px;
  background: var(--el-fill-color-extra-light);
}

.focus-label {
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.focus-title {
  margin-top: 8px;
  color: var(--el-text-color-primary);
  font-size: 16px;
  font-weight: 600;
}

.focus-desc {
  min-height: 34px;
  margin: 6px 0 12px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
  line-height: 1.45;
}

.health-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-top: 18px;
}

.health-card {
  min-height: 132px;
  padding: 16px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 6px;
  background: var(--el-bg-color);
}

.health-card-topline {
  display: flex;
  gap: 8px;
  align-items: center;
  justify-content: space-between;
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.health-value {
  margin: 12px 0 10px;
  color: var(--el-text-color-primary);
  font-size: 30px;
  font-weight: 600;
  line-height: 1.1;
}

.health-value em {
  margin-left: 3px;
  color: var(--el-text-color-secondary);
  font-size: 14px;
  font-style: normal;
  font-weight: 400;
}

.health-hint {
  margin-top: 10px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
  line-height: 1.5;
}

.metric-card {
  min-height: 122px;
  padding: 14px;
  margin-bottom: 12px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 6px;
  background: var(--el-bg-color);
}

.metric-card--danger {
  border-left: 3px solid var(--el-color-danger);
}

.metric-card--warning {
  border-left: 3px solid var(--el-color-warning);
}

.metric-card--success {
  border-left: 3px solid var(--el-color-success);
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

.section-header {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 12px;
}

.section-title {
  color: var(--el-text-color-primary);
  font-size: 15px;
  font-weight: 600;
}

.section-subtitle {
  margin-top: 4px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
  line-height: 1.45;
}

@media (max-width: 1200px) {
  .dashboard-hero {
    flex-direction: column;
  }

  .dashboard-focus {
    width: auto;
  }

  .health-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .dashboard-hero,
  .section-header {
    flex-direction: column;
  }

  .health-grid {
    grid-template-columns: 1fr;
  }
}
</style>
