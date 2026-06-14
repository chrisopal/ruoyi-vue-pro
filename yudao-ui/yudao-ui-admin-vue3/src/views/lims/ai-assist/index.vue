<template>
  <ContentWrap>
    <div class="ai-header">
      <div>
        <div class="ai-title">AI 标准与解读中心</div>
        <div class="ai-subtitle">
          {{ center?.requestNo || '最新检测需求' }}
          <span v-if="center?.domainPackCode">/ {{ center.domainPackCode }} {{ center.domainPackVersion }}</span>
        </div>
      </div>
      <el-button :loading="loading" plain type="primary" @click="loadCenter">
        <Icon class="mr-5px" icon="ep:refresh" />刷新
      </el-button>
    </div>

    <el-form :inline="true" class="query-form" @submit.prevent>
      <el-form-item label="需求ID">
        <el-input-number v-model="query.requestId" :min="1" controls-position="right" />
      </el-form-item>
      <el-form-item label="标准问题">
        <el-input
          v-model="query.question"
          class="question-input"
          clearable
          placeholder="例如：报告需要哪些证据"
          @keyup.enter="loadCenter"
        />
      </el-form-item>
      <el-form-item>
        <el-button :loading="loading" type="primary" @click="loadCenter">
          <Icon class="mr-5px" icon="ep:search" />分析
        </el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="12">
      <el-col v-for="metric in center?.metrics || []" :key="metric.code" :lg="6" :md="8" :sm="12" :xs="24">
        <div class="metric-card">
          <div class="metric-label">{{ metric.label }}</div>
          <div class="metric-value">{{ formatNumber(metric.value) }}<span>{{ metric.unit }}</span></div>
          <div class="metric-hint">{{ metric.hint }}</div>
        </div>
      </el-col>
    </el-row>
  </ContentWrap>

  <el-row :gutter="16">
    <el-col :lg="12" :xs="24">
      <ContentWrap>
        <div class="section-title">标准问答</div>
        <div class="answer-box">
          <div class="answer-question">{{ center?.standardAnswer?.question || '-' }}</div>
          <div class="answer-text">{{ center?.standardAnswer?.answer || '-' }}</div>
          <el-progress
            :percentage="center?.standardAnswer?.confidence || 0"
            :stroke-width="8"
            class="confidence"
          />
        </div>
        <el-table v-loading="loading" :data="center?.standardAnswer?.matchedClauses || []" height="280">
          <el-table-column label="条款" min-width="130">
            <template #default="scope">
              <div class="font-600">{{ scope.row.clauseCode }}</div>
              <div class="table-subtext">{{ scope.row.clauseCategory }}</div>
            </template>
          </el-table-column>
          <el-table-column label="标题" min-width="150" prop="clauseTitle" show-overflow-tooltip />
          <el-table-column label="要求" min-width="260" prop="requirementText" show-overflow-tooltip />
          <el-table-column label="证据类型" min-width="130" prop="evidenceTypeCodes" show-overflow-tooltip />
        </el-table>
      </ContentWrap>
    </el-col>

    <el-col :lg="12" :xs="24">
      <ContentWrap>
        <div class="section-title">证据缺口诊断</div>
        <el-table v-loading="loading" :data="center?.evidenceGaps || []" height="420">
          <el-table-column label="等级" width="86">
            <template #default="scope">
              <el-tag :type="tagType(scope.row.severity)" effect="light">{{ levelText(scope.row.severity) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="缺口" min-width="210">
            <template #default="scope">
              <div class="font-600">{{ scope.row.title }}</div>
              <div class="table-subtext">{{ scope.row.message }}</div>
            </template>
          </el-table-column>
          <el-table-column label="来源" min-width="110">
            <template #default="scope">
              <div>{{ scope.row.sourceType || '-' }}</div>
              <div class="table-subtext">{{ scope.row.sourceNo || '-' }}</div>
            </template>
          </el-table-column>
          <el-table-column label="动作" min-width="170" prop="actionText" show-overflow-tooltip />
        </el-table>
      </ContentWrap>
    </el-col>
  </el-row>

  <el-row :gutter="16">
    <el-col :lg="9" :xs="24">
      <ContentWrap>
        <div class="section-title">报告解读</div>
        <div class="report-panel">
          <div class="report-row">
            <span>报告编号</span>
            <strong>{{ center?.reportInterpretation?.reportNo || '-' }}</strong>
          </div>
          <div class="report-row">
            <span>状态</span>
            <el-tag :type="tagType(center?.reportInterpretation?.riskLevel)" effect="light">
              {{ reportStatusText(center?.reportInterpretation?.status) }}
            </el-tag>
          </div>
          <div class="report-row">
            <span>结论</span>
            <strong>{{ center?.reportInterpretation?.conclusion || '-' }}</strong>
          </div>
          <div class="format-list">
            <el-tag
              v-for="format in center?.reportInterpretation?.outputFormats || []"
              :key="format"
              effect="plain"
              type="primary"
            >
              {{ format }}
            </el-tag>
          </div>
          <div class="interpretation-text">{{ center?.reportInterpretation?.interpretation || '-' }}</div>
        </div>
      </ContentWrap>
    </el-col>

    <el-col :lg="15" :xs="24">
      <ContentWrap>
        <div class="section-title">实验数据解读</div>
        <el-table v-loading="loading" :data="center?.dataInterpretations || []" height="330">
          <el-table-column label="任务" min-width="130" prop="taskNo" show-overflow-tooltip />
          <el-table-column label="项目" min-width="120" prop="testItem" show-overflow-tooltip />
          <el-table-column label="方法" min-width="160" prop="methodName" show-overflow-tooltip />
          <el-table-column align="right" label="缺口" width="76" prop="missingRequirementCount" />
          <el-table-column label="门禁" width="96">
            <template #default="scope">
              <el-tag :type="scope.row.qualityGateSatisfied ? 'success' : 'warning'" effect="light">
                {{ scope.row.qualityGateSatisfied ? '通过' : '待补齐' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="解读" min-width="260" prop="interpretation" show-overflow-tooltip />
        </el-table>
      </ContentWrap>
    </el-col>
  </el-row>

  <ContentWrap>
    <div class="section-title">智能建议</div>
    <el-row :gutter="12">
      <el-col v-for="item in center?.recommendations || []" :key="item.code" :lg="8" :md="12" :xs="24">
        <div class="recommend-card">
          <div class="recommend-topline">
            <span>{{ item.title }}</span>
            <el-tag :type="priorityType(item.priority)" effect="light" size="small">{{ priorityText(item.priority) }}</el-tag>
          </div>
          <div class="recommend-content">{{ item.content }}</div>
        </div>
      </el-col>
    </el-row>
  </ContentWrap>
</template>

<script lang="ts" setup>
import { LimsAiAssistApi } from '@/api/lims/ai-assist'
import type { LimsAiAssistCenterVO } from '@/api/lims/ai-assist'

defineOptions({ name: 'LimsAiAssist' })

const loading = ref(false)
const center = ref<LimsAiAssistCenterVO>()
const query = reactive<{ requestId?: number; question?: string }>({
  question: '报告需要哪些证据'
})

const loadCenter = async () => {
  loading.value = true
  try {
    center.value = await LimsAiAssistApi.getCenter({
      requestId: query.requestId,
      question: query.question
    })
    if (center.value?.requestId && !query.requestId) {
      query.requestId = center.value.requestId
    }
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

const priorityType = (priority?: string) => {
  if (priority === 'high') return 'danger'
  if (priority === 'medium') return 'warning'
  return 'success'
}

const priorityText = (priority?: string) => {
  if (priority === 'high') return '高'
  if (priority === 'medium') return '中'
  return '低'
}

const reportStatusText = (status?: string) => {
  if (status === 'issued') return '已签发'
  if (status === 'generated') return '已生成'
  if (status === 'missing') return '未生成'
  if (status === 'draft') return '草稿'
  return status || '-'
}

onMounted(() => loadCenter())
</script>

<style scoped>
.ai-header {
  display: flex;
  gap: 16px;
  align-items: center;
  justify-content: space-between;
}

.ai-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.ai-subtitle {
  margin-top: 6px;
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.query-form {
  margin-top: 18px;
}

.question-input {
  width: 360px;
}

.metric-card,
.recommend-card {
  min-height: 112px;
  padding: 14px;
  margin-bottom: 12px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 4px;
}

.metric-label,
.recommend-topline {
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

.answer-box,
.report-panel {
  padding: 14px;
  margin-bottom: 12px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 4px;
}

.answer-question {
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.answer-text,
.interpretation-text,
.recommend-content {
  margin-top: 10px;
  font-size: 14px;
  line-height: 1.65;
  color: var(--el-text-color-primary);
}

.confidence {
  margin-top: 12px;
}

.report-row {
  display: flex;
  gap: 12px;
  align-items: center;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.report-row span {
  color: var(--el-text-color-secondary);
}

.format-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.recommend-topline {
  display: flex;
  gap: 8px;
  align-items: center;
  justify-content: space-between;
}

@media (max-width: 640px) {
  .ai-header {
    align-items: flex-start;
    flex-direction: column;
  }

  .question-input {
    width: 100%;
  }
}
</style>
