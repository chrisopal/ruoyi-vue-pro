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
        <el-descriptions-item label="快照 Hash">{{ gate?.workflowSnapshotHash || '-' }}</el-descriptions-item>
      </el-descriptions>

      <el-alert
        v-if="gate?.blockReason"
        :closable="false"
        show-icon
        :title="`阻断原因：${gate.blockReason}`"
        type="warning"
      />

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
