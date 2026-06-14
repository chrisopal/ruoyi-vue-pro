<template>
  <el-drawer v-model="visible" append-to-body :title="drawerTitle" size="720px" :z-index="3000">
    <div v-loading="loading" class="flex h-full flex-col gap-16px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="任务编号">{{ taskDetail?.taskNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="任务名称">{{ taskDetail?.taskName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="检测项目">{{ taskDetail?.testItem || '-' }}</el-descriptions-item>
        <el-descriptions-item label="任务状态">
          <el-tag :type="getTaskStatusTagType(taskDetail?.taskStatus)">
            {{ formatTaskStatus(taskDetail?.taskStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="排程状态">
          <el-tag :type="getScheduleStatusTagType(taskDetail?.scheduleStatus)">
            {{ formatScheduleStatus(taskDetail?.scheduleStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="计划窗口">{{ formatTaskWindow(taskDetail) }}</el-descriptions-item>
      </el-descriptions>

      <el-alert
        v-if="taskDetail?.blockReason"
        :closable="false"
        show-icon
        :title="`阻断原因：${taskDetail.blockReason}`"
        type="warning"
      />

      <div class="grid grid-cols-1 gap-16px">
        <div class="rounded-4px border border-solid border-[var(--el-border-color-lighter)] p-12px">
          <div class="mb-8px text-14px font-600">就绪快照</div>
          <el-input :model-value="prettyJson(taskDetail?.readinessSnapshot)" autosize readonly type="textarea" />
        </div>
        <div class="rounded-4px border border-solid border-[var(--el-border-color-lighter)] p-12px">
          <div class="mb-8px text-14px font-600">方法快照</div>
          <el-input :model-value="prettyJson(taskDetail?.methodSnapshot)" autosize readonly type="textarea" />
        </div>
        <div class="rounded-4px border border-solid border-[var(--el-border-color-lighter)] p-12px">
          <div class="mb-8px text-14px font-600">设备快照</div>
          <el-input :model-value="prettyJson(taskDetail?.equipmentSnapshot)" autosize readonly type="textarea" />
        </div>
        <div class="rounded-4px border border-solid border-[var(--el-border-color-lighter)] p-12px">
          <div class="mb-8px text-14px font-600">设备证据快照</div>
          <el-input
            :model-value="prettyJson(taskDetail?.equipmentEvidenceSnapshot)"
            autosize
            readonly
            type="textarea"
          />
        </div>
      </div>
    </div>

    <template #footer>
      <div class="flex justify-end gap-12px">
        <el-button :loading="submitting" @click="visible = false">关闭</el-button>
        <el-button :disabled="!canMarkReady(taskDetail)" :loading="submitting" type="warning" @click="handleReady">
          确认就绪
        </el-button>
        <el-button :disabled="!canStartTask(taskDetail)" :loading="submitting" type="primary" @click="handleStart">
          开始检测
        </el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script lang="ts" setup>
import { LimsWorkflowApi, type LimsTaskVO } from '@/api/lims/workflow'
import {
  canMarkReady,
  canStartTask,
  formatScheduleStatus,
  formatTaskStatus,
  formatTaskWindow,
  getScheduleStatusTagType,
  getTaskStatusTagType
} from './taskStatus'

defineOptions({ name: 'TaskReadinessDrawer' })

const emit = defineEmits<{
  success: []
}>()

const message = useMessage()

const visible = ref(false)
const loading = ref(false)
const submitting = ref(false)
const preferredAction = ref<'ready' | 'start'>('ready')
const taskDetail = ref<LimsTaskVO>()

const drawerTitle = computed(() =>
  preferredAction.value === 'start' ? '任务开始检测' : '任务就绪确认'
)

const prettyJson = (value?: string) => {
  if (!value) {
    return '-'
  }
  try {
    return JSON.stringify(JSON.parse(value), null, 2)
  } catch {
    return value
  }
}

const reloadTask = async (taskId: number) => {
  taskDetail.value = await LimsWorkflowApi.getTask(taskId)
}

const handleReady = async () => {
  if (!taskDetail.value?.id) {
    return
  }
  submitting.value = true
  try {
    await LimsWorkflowApi.readyTask(taskDetail.value.id)
    await reloadTask(taskDetail.value.id)
    message.success('任务已确认就绪')
    emit('success')
  } finally {
    submitting.value = false
  }
}

const handleStart = async () => {
  if (!taskDetail.value?.id) {
    return
  }
  submitting.value = true
  try {
    await LimsWorkflowApi.startTask(taskDetail.value.id)
    await reloadTask(taskDetail.value.id)
    message.success('任务已开始检测')
    emit('success')
    visible.value = false
  } finally {
    submitting.value = false
  }
}

const open = async (task: LimsTaskVO, action: 'ready' | 'start' = 'ready') => {
  preferredAction.value = action
  visible.value = true
  loading.value = true
  try {
    await reloadTask(task.id!)
  } finally {
    loading.value = false
  }
}

defineExpose({ open })
</script>
