<template>
  <el-drawer v-model="visible" append-to-body title="任务质量门禁" size="840px" :z-index="3000">
    <div v-loading="loading" class="flex h-full flex-col gap-16px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="任务编号">{{ gate?.taskNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="检测项目">{{ gate?.testItem || '-' }}</el-descriptions-item>
        <el-descriptions-item label="方向包">
          {{ formatPack(gate) }}
        </el-descriptions-item>
        <el-descriptions-item label="执行计划">
          <el-tag type="primary">{{ gate?.executionPlanStatus || '-' }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="QC">
          <el-tag :type="getReviewStatusTagType(gate?.qcStatus)">
            {{ formatReviewStatus(gate?.qcStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="复核">
          <el-tag :type="getReviewStatusTagType(gate?.reviewStatus)">
            {{ formatReviewStatus(gate?.reviewStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="可报告">
          <el-tag :type="getReportEligibleTagType(gate?.reportEligible)">
            {{ formatReportEligible(gate?.reportEligible) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="总门禁">
          <el-tag :type="getGateTagType(gate?.qualityGateSatisfied)">
            {{ gate?.qualityGateSatisfied ? '已满足' : '有缺口' }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>

      <el-alert
        v-if="gate?.blockReason"
        :closable="false"
        show-icon
        :title="`阻断原因：${gate.blockReason}`"
        type="warning"
      />
      <el-alert
        v-if="gate?.qualityGateSatisfied"
        :closable="false"
        show-icon
        title="当前任务的原始记录、QC、设备/人员证据和技术复核门禁均已满足。"
        type="success"
      />
      <el-alert
        v-else
        :closable="false"
        show-icon
        :title="`当前还有 ${gate?.missingRequirementCount || 0} 个门禁缺口，需要补齐后才能稳定进入报告。`"
        type="warning"
      />

      <div class="grid grid-cols-2 gap-12px md:grid-cols-4">
        <div
          v-for="metric in metricRows"
          :key="metric.name"
          class="rounded-4px border border-solid border-[var(--el-border-color-light)] p-12px"
        >
          <div class="text-12px text-[var(--el-text-color-secondary)]">{{ metric.name }}</div>
          <div class="mt-6px text-22px font-600 leading-28px text-[var(--el-text-color-primary)]">
            {{ metric.value }}
          </div>
          <el-tag class="mt-6px" :type="getGateTagType(metric.satisfied)">
            {{ formatSatisfied(metric.satisfied) }}
          </el-tag>
        </div>
      </div>

      <el-tabs v-model="activeTab" class="min-h-0 flex-1">
        <el-tab-pane :label="`计划要求 (${requirementTotal})`" name="requirements">
          <div class="grid grid-cols-1 gap-16px">
            <section>
              <div class="mb-8px text-14px font-600">样品要求</div>
              <el-table :data="asArray(gate?.sampleRequirements)" border size="small">
                <el-table-column label="编码" min-width="130">
                  <template #default="{ row }">{{ valueOf(row, ['requirementCode', 'code']) }}</template>
                </el-table-column>
                <el-table-column label="名称" min-width="160">
                  <template #default="{ row }">{{ valueOf(row, ['requirementName', 'name']) }}</template>
                </el-table-column>
                <el-table-column label="要求" min-width="220" show-overflow-tooltip>
                  <template #default="{ row }">{{ valueOf(row, ['requirementText', 'description']) }}</template>
                </el-table-column>
              </el-table>
            </section>

            <section>
              <div class="mb-8px text-14px font-600">结果字段</div>
              <el-table :data="asArray(gate?.resultFields)" border size="small">
                <el-table-column label="字段编码" min-width="130">
                  <template #default="{ row }">{{ valueOf(row, ['fieldCode', 'code']) }}</template>
                </el-table-column>
                <el-table-column label="字段名称" min-width="160">
                  <template #default="{ row }">{{ valueOf(row, ['fieldName', 'name']) }}</template>
                </el-table-column>
                <el-table-column label="类型" width="100">
                  <template #default="{ row }">{{ valueOf(row, ['fieldType', 'type']) }}</template>
                </el-table-column>
                <el-table-column label="必填" width="90">
                  <template #default="{ row }">{{ row.required ? '是' : '否' }}</template>
                </el-table-column>
              </el-table>
            </section>

            <section>
              <div class="mb-8px text-14px font-600">QC 规则</div>
              <el-table :data="asArray(gate?.qcRules)" border size="small">
                <el-table-column label="规则编码" min-width="130">
                  <template #default="{ row }">{{ valueOf(row, ['ruleCode', 'code']) }}</template>
                </el-table-column>
                <el-table-column label="规则名称" min-width="160">
                  <template #default="{ row }">{{ valueOf(row, ['ruleName', 'name']) }}</template>
                </el-table-column>
                <el-table-column label="判定要求" min-width="220" show-overflow-tooltip>
                  <template #default="{ row }">{{ valueOf(row, ['acceptanceCriteria', 'criteria']) }}</template>
                </el-table-column>
              </el-table>
            </section>

            <section>
              <div class="mb-8px text-14px font-600">证据要求</div>
              <el-table :data="asArray(gate?.evidenceRequirements)" border size="small">
                <el-table-column label="要求编码" min-width="130">
                  <template #default="{ row }">{{ valueOf(row, ['requirementCode', 'code']) }}</template>
                </el-table-column>
                <el-table-column label="要求名称" min-width="170">
                  <template #default="{ row }">{{ valueOf(row, ['requirementName', 'name']) }}</template>
                </el-table-column>
                <el-table-column label="证据类型" min-width="160">
                  <template #default="{ row }">{{ valueOf(row, ['evidenceType', 'sourceType']) }}</template>
                </el-table-column>
                <el-table-column label="必需" width="90">
                  <template #default="{ row }">{{ row.required === false ? '否' : '是' }}</template>
                </el-table-column>
              </el-table>
            </section>
          </div>
        </el-tab-pane>

        <el-tab-pane :label="`报告规则 (${asArray(gate?.reportSections).length})`" name="report">
          <div class="grid grid-cols-1 gap-16px">
            <section>
              <div class="mb-8px text-14px font-600">报告模板</div>
              <el-input :model-value="prettyJson(gate?.templateCodes)" autosize readonly type="textarea" />
            </section>
            <section>
              <div class="mb-8px text-14px font-600">报告章节</div>
              <el-table :data="asArray(gate?.reportSections)" border size="small">
                <el-table-column label="章节编码" min-width="140">
                  <template #default="{ row }">{{ valueOf(row, ['sectionCode', 'code']) }}</template>
                </el-table-column>
                <el-table-column label="章节名称" min-width="180">
                  <template #default="{ row }">{{ valueOf(row, ['sectionName', 'name']) }}</template>
                </el-table-column>
                <el-table-column label="数据来源" min-width="180">
                  <template #default="{ row }">{{ valueOf(row, ['sourceType', 'sourcePath']) }}</template>
                </el-table-column>
              </el-table>
            </section>
            <section>
              <div class="mb-8px text-14px font-600">报告草稿计划</div>
              <el-input :model-value="prettyJson(gate?.reportDraftPlan)" autosize readonly type="textarea" />
            </section>
          </div>
        </el-tab-pane>

        <el-tab-pane label="证据快照" name="evidence">
          <div class="grid grid-cols-1 gap-16px">
            <el-alert
              :closable="false"
              show-icon
              :title="`设备证据：${gate?.hasEquipmentEvidence ? '已采集' : '未采集'}；人员证据：${gate?.hasPersonnelEvidence ? '已采集' : '未采集'}`"
              :type="gate?.hasEquipmentEvidence ? 'success' : 'warning'"
            />
            <section>
              <div class="mb-8px text-14px font-600">证据进度</div>
              <el-table :data="progressRows" border size="small">
                <el-table-column label="门禁项" min-width="120" prop="name" />
                <el-table-column label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag :type="getGateTagType(row.satisfied)">{{ formatSatisfied(row.satisfied) }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="当前记录" min-width="120" prop="summary" />
              </el-table>
            </section>
            <section>
              <div class="mb-8px text-14px font-600">设备证据快照</div>
              <el-input :model-value="prettyJson(gate?.equipmentEvidenceSnapshot)" autosize readonly type="textarea" />
            </section>
            <section>
              <div class="mb-8px text-14px font-600">人员证据快照</div>
              <el-input :model-value="prettyJson(gate?.personnelEvidenceSnapshot)" autosize readonly type="textarea" />
            </section>
            <section>
              <div class="mb-8px text-14px font-600">推荐 QC 规则快照</div>
              <el-input :model-value="prettyJson(gate?.qcRuleSnapshot)" autosize readonly type="textarea" />
            </section>
          </div>
        </el-tab-pane>

        <el-tab-pane :label="`缺口 (${gate?.missingRequirementCount || 0})`" name="missing">
          <el-table :data="asArray(gate?.missingRequirements)" border size="small">
            <el-table-column label="类型" min-width="140">
              <template #default="{ row }">{{ formatMissingType(valueOf(row, ['type'])) }}</template>
            </el-table-column>
            <el-table-column label="编码" min-width="150">
              <template #default="{ row }">{{ valueOf(row, ['code']) }}</template>
            </el-table-column>
            <el-table-column label="名称" min-width="160">
              <template #default="{ row }">{{ valueOf(row, ['name']) }}</template>
            </el-table-column>
            <el-table-column label="处理提示" min-width="260" show-overflow-tooltip>
              <template #default="{ row }">{{ valueOf(row, ['message']) }}</template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </div>
  </el-drawer>
</template>

<script lang="ts" setup>
import { LimsWorkflowApi, type LimsTaskQualityGateVO, type LimsTaskVO } from '@/api/lims/workflow'
import {
  formatReportEligible,
  formatReviewStatus,
  getReportEligibleTagType,
  getReviewStatusTagType
} from './taskStatus'

defineOptions({ name: 'TaskQualityGateDrawer' })

const visible = ref(false)
const loading = ref(false)
const activeTab = ref('requirements')
const gate = ref<LimsTaskQualityGateVO>()

const asArray = (value?: unknown) => (Array.isArray(value) ? value : [])

const requirementTotal = computed(
  () =>
    asArray(gate.value?.sampleRequirements).length +
    asArray(gate.value?.resultFields).length +
    asArray(gate.value?.qcRules).length +
    asArray(gate.value?.evidenceRequirements).length
)

const isEvidenceSatisfied = computed(
  () => Boolean(gate.value?.equipmentEvidenceSatisfied) && Boolean(gate.value?.personnelEvidenceSatisfied)
)

const metricRows = computed(() => [
  {
    name: '原始记录',
    value: gate.value?.rawRecordCount || 0,
    satisfied: gate.value?.rawRecordSatisfied
  },
  {
    name: 'QC 规则',
    value: `${gate.value?.satisfiedQcRuleCount || 0}/${gate.value?.qcRuleCount || 0}`,
    satisfied: gate.value?.qcSatisfied
  },
  {
    name: '证据要求',
    value: gate.value?.evidenceRequirementCount || 0,
    satisfied: isEvidenceSatisfied.value
  },
  {
    name: '通过复核',
    value: gate.value?.approvedReviewCount || 0,
    satisfied: gate.value?.reviewSatisfied
  }
])

const progressRows = computed(() => [
  {
    name: '原始记录',
    satisfied: gate.value?.rawRecordSatisfied,
    summary: `${gate.value?.rawRecordCount || 0} 条`
  },
  {
    name: 'QC 规则',
    satisfied: gate.value?.qcSatisfied,
    summary: `${gate.value?.satisfiedQcRuleCount || 0}/${gate.value?.qcRuleCount || 0}`
  },
  {
    name: '设备证据',
    satisfied: gate.value?.equipmentEvidenceSatisfied,
    summary: gate.value?.hasEquipmentEvidence ? '已采集' : '未采集'
  },
  {
    name: '人员证据',
    satisfied: gate.value?.personnelEvidenceSatisfied,
    summary: gate.value?.hasPersonnelEvidence ? '已采集' : '未采集'
  },
  {
    name: '技术复核',
    satisfied: gate.value?.reviewSatisfied,
    summary: `${gate.value?.approvedReviewCount || 0}/${gate.value?.reviewRecordCount || 0}`
  }
])

const valueOf = (row: Record<string, any>, keys: string[]) => {
  for (const key of keys) {
    const value = row?.[key]
    if (value !== undefined && value !== null && value !== '') {
      return value
    }
  }
  return '-'
}

const prettyJson = (value?: unknown) => {
  if (value === undefined || value === null || value === '') {
    return '-'
  }
  try {
    return typeof value === 'string'
      ? JSON.stringify(JSON.parse(value), null, 2)
      : JSON.stringify(value, null, 2)
  } catch {
    return String(value)
  }
}

const formatPack = (value?: LimsTaskQualityGateVO) => {
  if (!value?.domainPackCode && !value?.domainPackVersion) {
    return '-'
  }
  return `${value.domainPackCode || '-'} / ${value.domainPackVersion || '-'}`
}

const formatSatisfied = (value?: boolean) => (value ? '已满足' : '待补齐')
const getGateTagType = (value?: boolean) => (value ? 'success' : 'warning')
const MISSING_TYPE_LABELS: Record<string, string> = {
  RAW_RECORD: '原始记录',
  RAW_RESULT_FIELD: '原始结果字段',
  QC_RULE: 'QC 规则',
  EQUIPMENT_EVIDENCE: '设备证据',
  PERSONNEL_EVIDENCE: '人员证据',
  TECH_REVIEW: '技术复核'
}
const formatMissingType = (type: string) => {
  const label = MISSING_TYPE_LABELS[type]
  return label ? `${label}（${type}）` : type
}

const open = async (task: LimsTaskVO) => {
  if (!task.id) {
    return
  }
  visible.value = true
  activeTab.value = 'requirements'
  loading.value = true
  try {
    gate.value = await LimsWorkflowApi.getTaskQualityGate(task.id)
  } finally {
    loading.value = false
  }
}

defineExpose({ open })
</script>
