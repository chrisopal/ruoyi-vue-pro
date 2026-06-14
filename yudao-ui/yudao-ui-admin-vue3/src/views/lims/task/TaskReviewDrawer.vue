<template>
  <el-drawer v-model="visible" append-to-body :title="drawerTitle" size="820px" :z-index="3000">
    <div v-loading="loading" class="flex h-full flex-col gap-16px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="任务编号">{{ taskDetail?.taskNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="任务名称">{{ taskDetail?.taskName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="任务状态">
          <el-tag :type="getTaskStatusTagType(taskDetail?.taskStatus)">
            {{ formatTaskStatus(taskDetail?.taskStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="复核状态">
          <el-tag :type="getReviewStatusTagType(taskDetail?.reviewStatus)">
            {{ formatReviewStatus(taskDetail?.reviewStatus) }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>

      <el-alert
        v-if="!canReviewTask(taskDetail)"
        :closable="false"
        show-icon
        title="当前任务状态通常不允许技术复核，若继续操作将由后端做最终校验。"
        type="warning"
      />

      <div class="min-h-0 flex-1">
        <div class="mb-8px flex items-center justify-between">
          <div class="text-14px font-600">已有复核记录</div>
          <el-button link type="primary" @click="loadReviews">刷新</el-button>
        </div>
        <el-table :data="reviews" border height="280px" size="small">
          <el-table-column label="时间" min-width="160" prop="reviewTime" />
          <el-table-column label="类型" min-width="100" prop="reviewType" />
          <el-table-column label="状态" min-width="100">
            <template #default="{ row }">
              <el-tag :type="getReviewStatusTagType(row.reviewStatus)">
                {{ formatReviewStatus(row.reviewStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="复核人" min-width="90" prop="reviewerId" />
          <el-table-column label="意见" min-width="220" prop="comment" show-overflow-tooltip />
          <el-table-column label="快照哈希" min-width="180" prop="snapshotHash" show-overflow-tooltip />
        </el-table>
      </div>

      <el-form :model="reviewForm" label-width="96px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="复核类型">
              <el-input v-model="reviewForm.reviewType" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="复核人">
              <el-input-number
                v-model="reviewForm.reviewerId"
                :min="1"
                class="!w-full"
                controls-position="right"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="快照哈希">
              <el-input v-model="reviewForm.snapshotHash" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="复核意见">
              <el-input v-model="reviewForm.remark" :rows="4" type="textarea" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <template #footer>
      <div class="flex justify-end gap-12px">
        <el-button :loading="submitting" @click="visible = false">关闭</el-button>
        <el-button :loading="submitting" type="danger" @click="handleReject">复核驳回</el-button>
        <el-button :loading="submitting" type="primary" @click="handleApprove">复核通过</el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script lang="ts" setup>
import dayjs from 'dayjs'
import {
  LimsWorkflowApi,
  type LimsTaskReviewPayload,
  type LimsTaskReviewVO,
  type LimsTaskVO
} from '@/api/lims/workflow'
import {
  canReviewTask,
  formatReviewStatus,
  formatTaskStatus,
  getReviewStatusTagType,
  getTaskStatusTagType
} from './taskStatus'

defineOptions({ name: 'TaskReviewDrawer' })

const emit = defineEmits<{
  success: []
}>()

const message = useMessage()

const visible = ref(false)
const loading = ref(false)
const submitting = ref(false)
const taskDetail = ref<LimsTaskVO>()
const reviews = ref<LimsTaskReviewVO[]>([])
const reviewForm = reactive<LimsTaskReviewPayload>({
  taskId: undefined,
  reviewerId: undefined,
  reviewType: 'technical',
  snapshotHash: '',
  remark: '批准进入报告'
})

const drawerTitle = computed(() => '任务技术复核')

const buildSnapshotHash = (task: LimsTaskVO) =>
  task.snapshotHash ||
  task.workflowSnapshotHash ||
  task.dataSnapshotHash ||
  `${task.taskNo || 'task'}-${dayjs().format('YYYYMMDDHHmmss')}`

const resetForm = (task: LimsTaskVO) => {
  reviewForm.taskId = task.id
  reviewForm.reviewerId = task.reviewerId
  reviewForm.reviewType = task.reviewType || 'technical'
  reviewForm.snapshotHash = buildSnapshotHash(task)
  reviewForm.remark = '批准进入报告'
}

const reloadTask = async (taskId: number) => {
  taskDetail.value = await LimsWorkflowApi.getTask(taskId)
}

const loadReviews = async () => {
  if (!taskDetail.value?.id) {
    reviews.value = []
    return
  }
  reviews.value = await LimsWorkflowApi.getTaskReviews(taskDetail.value.id)
}

const ensureReviewPayload = () => {
  if (!reviewForm.taskId) {
    throw new Error('任务不存在，无法复核')
  }
  if (!reviewForm.reviewType) {
    throw new Error('请填写复核类型')
  }
  if (!reviewForm.reviewerId) {
    throw new Error('请填写复核人')
  }
  if (!reviewForm.remark) {
    throw new Error('请填写复核意见')
  }
}

const handleApprove = async () => {
  try {
    ensureReviewPayload()
  } catch (error) {
    message.error((error as Error).message)
    return
  }
  submitting.value = true
  try {
    await LimsWorkflowApi.approveTaskReview({ ...reviewForm })
    await reloadTask(reviewForm.taskId!)
    await loadReviews()
    message.success('复核已通过')
    emit('success')
    visible.value = false
  } finally {
    submitting.value = false
  }
}

const handleReject = async () => {
  try {
    ensureReviewPayload()
  } catch (error) {
    message.error((error as Error).message)
    return
  }
  submitting.value = true
  try {
    await LimsWorkflowApi.rejectTaskReview({ ...reviewForm })
    await reloadTask(reviewForm.taskId!)
    await loadReviews()
    message.success('复核已驳回')
    emit('success')
    visible.value = false
  } finally {
    submitting.value = false
  }
}

const open = async (task: LimsTaskVO) => {
  visible.value = true
  loading.value = true
  try {
    await reloadTask(task.id!)
    resetForm(taskDetail.value!)
    await loadReviews()
  } finally {
    loading.value = false
  }
}

defineExpose({ open })
</script>
