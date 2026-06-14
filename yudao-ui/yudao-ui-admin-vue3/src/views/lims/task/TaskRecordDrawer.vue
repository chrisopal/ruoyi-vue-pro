<template>
  <el-drawer v-model="visible" :title="drawerTitle" size="760px">
    <div v-loading="loading" class="flex h-full flex-col gap-16px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="任务编号">{{ taskDetail?.taskNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="任务名称">{{ taskDetail?.taskName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="检测项目">{{ taskDetail?.testItem || '-' }}</el-descriptions-item>
        <el-descriptions-item label="当前状态">
          <el-tag :type="getTaskStatusTagType(taskDetail?.taskStatus)">
            {{ formatTaskStatus(taskDetail?.taskStatus) }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>

      <el-tabs v-model="activeTab" class="min-h-0 flex-1">
        <el-tab-pane label="原始记录" name="raw">
          <el-alert
            v-if="!canSubmitRawRecord(taskDetail)"
            :closable="false"
            show-icon
            title="当前任务状态通常不允许提交原始记录，若继续提交将由后端做最终校验。"
            type="warning"
          />
          <el-form ref="rawFormRef" :model="rawForm" label-width="96px" class="mt-12px">
            <el-form-item label="记录类型">
              <el-input v-model="rawForm.recordType" />
            </el-form-item>
            <el-form-item label="原始记录 JSON">
              <el-input v-model="rawForm.recordJson" :rows="12" type="textarea" />
            </el-form-item>
            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="结果值">
                  <el-input v-model="rawForm.resultValue" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="单位">
                  <el-input v-model="rawForm.resultUnit" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="结论">
                  <el-select v-model="rawForm.resultConclusion" class="!w-full">
                    <el-option label="通过" value="pass" />
                    <el-option label="不通过" value="fail" />
                    <el-option label="待判定" value="pending" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="结果原始数据">
              <el-input v-model="rawForm.rawData" :rows="5" type="textarea" />
            </el-form-item>
            <el-form-item label="附件地址">
              <el-input v-model="rawForm.attachmentUrl" />
            </el-form-item>
            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="版本号">
                  <el-input-number v-model="rawForm.versionNo" :min="1" class="!w-full" controls-position="right" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="提交人">
                  <el-input-number
                    v-model="rawForm.submittedBy"
                    :min="1"
                    class="!w-full"
                    controls-position="right"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="状态">
                  <el-input v-model="rawForm.status" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="提交说明">
              <el-input v-model="rawForm.remark" :rows="3" type="textarea" />
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="QC 记录" name="qc">
          <el-alert
            v-if="!canSubmitQcRecord(taskDetail)"
            :closable="false"
            show-icon
            title="当前任务状态通常不允许提交 QC 记录，若继续提交将由后端做最终校验。"
            type="warning"
          />
          <el-form ref="qcFormRef" :model="qcForm" label-width="96px" class="mt-12px">
            <el-form-item label="QC 类型">
              <el-input v-model="qcForm.qcType" />
            </el-form-item>
            <el-form-item label="规则快照 JSON">
              <el-input v-model="qcForm.qcRuleSnapshot" :rows="8" type="textarea" />
            </el-form-item>
            <el-form-item label="QC 数据 JSON">
              <el-input v-model="qcForm.qcDataJson" :rows="8" type="textarea" />
            </el-form-item>
            <el-row :gutter="16">
              <el-col :span="10">
                <el-form-item label="QC 结果">
                  <el-select v-model="qcForm.qcResult" class="!w-full">
                    <el-option label="通过" value="approved" />
                    <el-option label="驳回" value="rejected" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="14">
                <el-form-item label="QC 意见">
                  <el-input v-model="qcForm.reviewComment" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </div>

    <template #footer>
      <div class="flex justify-end gap-12px">
        <el-button :loading="submitting" @click="visible = false">取消</el-button>
        <el-button :loading="submitting" type="primary" @click="handleSubmit">
          {{ activeTab === 'raw' ? '提交原始记录' : '提交 QC 记录' }}
        </el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script lang="ts" setup>
import dayjs from 'dayjs'
import {
  LimsWorkflowApi,
  type LimsTaskQcRecordPayload,
  type LimsTaskRawRecordPayload,
  type LimsTaskVO
} from '@/api/lims/workflow'
import {
  canSubmitQcRecord,
  canSubmitRawRecord,
  formatTaskStatus,
  getTaskStatusTagType
} from './taskStatus'

defineOptions({ name: 'TaskRecordDrawer' })

const emit = defineEmits<{
  success: []
}>()

const message = useMessage()

const visible = ref(false)
const loading = ref(false)
const submitting = ref(false)
const activeTab = ref<'raw' | 'qc'>('raw')
const taskDetail = ref<LimsTaskVO>()

const rawForm = reactive<LimsTaskRawRecordPayload>({
  taskId: undefined,
  recordType: 'instrument',
  recordJson: '{}',
  attachmentUrl: '',
  versionNo: 1,
  submittedBy: undefined,
  status: 'submitted',
  remark: '原始记录已提交',
  resultValue: '',
  resultUnit: '',
  resultConclusion: 'pass',
  rawData: '{}'
})

const qcForm = reactive<LimsTaskQcRecordPayload>({
  taskId: undefined,
  qcType: 'routine_qc',
  qcRuleSnapshot: '{}',
  qcDataJson: '{}',
  qcResult: 'approved',
  reviewComment: 'QC 通过，待复核'
})

const drawerTitle = computed(() =>
  activeTab.value === 'raw' ? '任务原始记录' : '任务 QC 记录'
)

const prettyDefaultJson = (value: unknown) => JSON.stringify(value, null, 2)

const normalizeJson = (value?: string) => {
  if (!value) {
    throw new Error('JSON 内容不能为空')
  }
  return JSON.stringify(JSON.parse(value))
}

const resetRawForm = (task: LimsTaskVO) => {
  rawForm.taskId = task.id
  rawForm.recordType = 'instrument'
  rawForm.recordJson = prettyDefaultJson({
    taskNo: task.taskNo,
    testItem: task.testItem,
    capturedAt: dayjs().format('YYYY-MM-DD HH:mm:ss'),
    measurements: [{ name: 'result', value: null, unit: '' }]
  })
  rawForm.attachmentUrl = ''
  rawForm.versionNo = 1
  rawForm.submittedBy = task.submittedBy
  rawForm.status = 'submitted'
  rawForm.remark = '原始记录已提交'
  rawForm.resultValue = task.resultValue || ''
  rawForm.resultUnit = task.resultUnit || ''
  rawForm.resultConclusion = task.resultConclusion || 'pass'
  rawForm.rawData = task.rawData || prettyDefaultJson({ resultValues: [] })
}

const resetQcForm = (task: LimsTaskVO) => {
  qcForm.taskId = task.id
  qcForm.qcType = 'routine_qc'
  qcForm.qcRuleSnapshot = prettyDefaultJson(parseOrFallback(task.methodSnapshot, { rules: [] }))
  qcForm.qcDataJson = prettyDefaultJson({
    taskNo: task.taskNo,
    checkedAt: dayjs().format('YYYY-MM-DD HH:mm:ss'),
    observations: []
  })
  qcForm.qcResult = 'approved'
  qcForm.reviewComment = 'QC 通过，待复核'
}

const parseOrFallback = (value: string | undefined, fallback: Record<string, unknown>) => {
  if (!value) {
    return fallback
  }
  try {
    return JSON.parse(value) as Record<string, unknown>
  } catch {
    return { raw: value }
  }
}

const reloadTask = async (taskId: number) => {
  taskDetail.value = await LimsWorkflowApi.getTask(taskId)
}

const submitRaw = async () => {
  rawForm.recordJson = buildRecordJson()
  await LimsWorkflowApi.submitRawRecord({ ...rawForm })
}

const buildRecordJson = () => {
  const record = JSON.parse(normalizeJson(rawForm.recordJson)) as Record<string, unknown>
  record.resultValue = rawForm.resultValue || ''
  record.resultUnit = rawForm.resultUnit || ''
  record.resultConclusion = rawForm.resultConclusion || ''
  record.rawData = parseOrFallback(rawForm.rawData, {})
  return JSON.stringify(record)
}

const submitQc = async () => {
  qcForm.qcRuleSnapshot = normalizeJson(qcForm.qcRuleSnapshot)
  qcForm.qcDataJson = normalizeJson(qcForm.qcDataJson)
  await LimsWorkflowApi.submitQcRecord({ ...qcForm })
}

const handleSubmit = async () => {
  if (!taskDetail.value?.id) {
    return
  }
  submitting.value = true
  try {
    if (activeTab.value === 'raw') {
      await submitRaw()
      message.success('原始记录已提交')
    } else {
      await submitQc()
      message.success('QC 记录已提交')
    }
    await reloadTask(taskDetail.value.id)
    emit('success')
    visible.value = false
  } catch (error) {
    message.error((error as Error).message || '提交失败')
  } finally {
    submitting.value = false
  }
}

const open = async (task: LimsTaskVO, mode: 'raw' | 'qc' = 'raw') => {
  activeTab.value = mode
  visible.value = true
  loading.value = true
  try {
    await reloadTask(task.id!)
    resetRawForm(taskDetail.value!)
    resetQcForm(taskDetail.value!)
  } finally {
    loading.value = false
  }
}

defineExpose({ open })
</script>
