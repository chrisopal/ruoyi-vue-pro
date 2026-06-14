<template>
  <el-drawer v-model="visible" :title="drawerTitle" size="760px">
    <div v-loading="loading" class="flex h-full flex-col gap-16px">
      <el-form :model="formData" label-width="96px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="任务编号">
              <el-input :model-value="taskDetail?.taskNo || '-'" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="任务名称">
              <el-input :model-value="taskDetail?.taskName || '-'" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="检测项目">
              <el-input :model-value="taskDetail?.testItem || '-'" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="执行人">
              <el-input-number v-model="formData.assignedUserId" :min="1" class="!w-full" controls-position="right" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备">
              <el-select
                v-model="formData.equipmentId"
                class="!w-full"
                clearable
                filterable
                :loading="equipmentLoading"
                placeholder="选择可用设备"
                @visible-change="(open) => open && loadEquipmentOptions()"
                @change="handleEquipmentChange"
              >
                <el-option
                  v-for="item in equipmentOptions"
                  :key="item.id"
                  :label="equipmentLabel(item)"
                  :value="item.id!"
                >
                  <div class="flex items-center justify-between gap-12px">
                    <span>{{ item.equipmentCode }} / {{ item.equipmentName }}</span>
                    <span class="text-12px text-[var(--el-text-color-secondary)]">
                      {{ item.calibrationValidUntil || '未维护有效期' }}
                    </span>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col v-if="selectedEquipment" :span="24">
            <el-alert
              :closable="false"
              show-icon
              type="success"
              :title="selectedEquipmentSummary"
            />
          </el-col>
          <el-col :span="12">
            <el-form-item label="计划开始">
              <el-date-picker
                v-model="formData.plannedStartTime"
                class="!w-full"
                format="YYYY-MM-DD HH:mm:ss"
                placeholder="选择计划开始时间"
                type="datetime"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计划结束">
              <el-date-picker
                v-model="formData.plannedEndTime"
                class="!w-full"
                format="YYYY-MM-DD HH:mm:ss"
                placeholder="选择计划结束时间"
                type="datetime"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预计分钟">
              <el-input-number
                v-model="formData.durationMinutes"
                :min="1"
                :step="5"
                class="!w-full"
                controls-position="right"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="当前状态">
              <div class="flex items-center gap-8px">
                <el-tag :type="getTaskStatusTagType(taskDetail?.taskStatus)">
                  {{ formatTaskStatus(taskDetail?.taskStatus) }}
                </el-tag>
                <el-tag :type="getScheduleStatusTagType(taskDetail?.scheduleStatus)">
                  {{ formatScheduleStatus(taskDetail?.scheduleStatus) }}
                </el-tag>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <el-alert
        show-icon
        title="保存排程会使用当前表单；默认排程会使用任务已有执行人、设备和计划窗口。"
        type="info"
      />

      <div class="min-h-0 flex-1">
        <div class="mb-8px flex items-center justify-between">
          <div class="text-14px font-600">最近排程记录</div>
          <el-button link type="primary" @click="loadSchedules">刷新</el-button>
        </div>
        <el-table :data="scheduleList" border height="100%" size="small">
          <el-table-column label="计划窗口" min-width="220">
            <template #default="{ row }">
              <div>{{ row.plannedStartTime || '-' }}</div>
              <div class="text-[var(--el-text-color-secondary)]">{{ row.plannedEndTime || '-' }}</div>
            </template>
          </el-table-column>
          <el-table-column label="排程状态" min-width="110">
            <template #default="{ row }">
              <el-tag :type="getScheduleStatusTagType(row.scheduleStatus)">
                {{ formatScheduleStatus(row.scheduleStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="执行人" min-width="100">
            <template #default="{ row }">{{ row.assignedUserId || '-' }}</template>
          </el-table-column>
          <el-table-column label="设备" min-width="100">
            <template #default="{ row }">{{ scheduleEquipmentLabel(row.equipmentId) }}</template>
          </el-table-column>
          <el-table-column label="锁定" min-width="80">
            <template #default="{ row }">
              <el-tag :type="row.locked ? 'warning' : 'info'">{{ row.locked ? '是' : '否' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" min-width="160" prop="createTime" />
        </el-table>
      </div>
    </div>

    <template #footer>
      <div class="flex justify-end gap-12px">
        <el-button :loading="submitting" @click="visible = false">取消</el-button>
        <el-button :loading="submitting" type="warning" @click="handleDefaultSchedule">默认排程</el-button>
        <el-button :loading="submitting" type="primary" @click="handleSchedule">保存排程</el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script lang="ts" setup>
import dayjs from 'dayjs'
import { LabEquipmentAssetApi, type LabEquipmentAssetVO } from '@/api/lab/equipment-asset'
import {
  LimsWorkflowApi,
  type LimsTaskPageReqVO,
  type LimsTaskSchedulePayload,
  type LimsTaskScheduleVO,
  type LimsTaskVO
} from '@/api/lims/workflow'
import {
  formatScheduleStatus,
  formatTaskStatus,
  getScheduleStatusTagType,
  getTaskStatusTagType
} from './taskStatus'

defineOptions({ name: 'TaskSchedulePanel' })

const emit = defineEmits<{
  success: []
}>()

const message = useMessage()

const visible = ref(false)
const loading = ref(false)
const submitting = ref(false)
const preferredMode = ref<'schedule' | 'default'>('schedule')
const taskDetail = ref<LimsTaskVO>()
const scheduleList = ref<LimsTaskScheduleVO[]>([])
const equipmentLoading = ref(false)
const equipmentOptions = ref<LabEquipmentAssetVO[]>([])
const formData = reactive<LimsTaskSchedulePayload>({
  taskId: undefined,
  assignedUserId: undefined,
  equipmentId: undefined,
  plannedStartTime: '',
  plannedEndTime: '',
  durationMinutes: 60
})

const drawerTitle = computed(() =>
  preferredMode.value === 'default' ? '任务默认排程' : '任务排程'
)
const selectedEquipment = computed(() =>
  equipmentOptions.value.find((item) => item.id === formData.equipmentId)
)
const selectedEquipmentSummary = computed(() => {
  const equipment = selectedEquipment.value
  if (!equipment) {
    return ''
  }
  const iot = equipment.iotEnabled ? `IoT:${equipment.iotDeviceId || equipment.iotProductId || '已启用'}` : '未启用IoT'
  return `已选择 ${equipment.equipmentCode} / ${equipment.equipmentName}，校准有效期 ${equipment.calibrationValidUntil || '未维护'}，${iot}`
})

watch(
  () => [formData.plannedStartTime, formData.durationMinutes] as const,
  ([plannedStartTime, durationMinutes]) => {
    if (!plannedStartTime || !durationMinutes) {
      return
    }
    if (!formData.plannedEndTime || dayjs(formData.plannedEndTime).isBefore(dayjs(plannedStartTime))) {
      formData.plannedEndTime = dayjs(plannedStartTime)
        .add(durationMinutes, 'minute')
        .format('YYYY-MM-DD HH:mm:ss')
    }
  }
)

const fillForm = (task: LimsTaskVO) => {
  formData.id = task.id
  formData.taskId = task.id
  formData.assignedUserId = task.assignedUserId
  formData.equipmentId = task.equipmentId
  formData.equipmentCode = task.equipmentCode
  formData.equipmentName = task.equipmentName
  formData.plannedStartTime = task.plannedStartTime || ''
  formData.plannedEndTime = task.plannedEndTime || ''
  formData.durationMinutes = task.durationMinutes || 60
}

const loadTask = async (taskId: number) => {
  taskDetail.value = await LimsWorkflowApi.getTask(taskId)
  fillForm(taskDetail.value)
}

const equipmentLabel = (equipment: LabEquipmentAssetVO) =>
  `${equipment.equipmentCode} / ${equipment.equipmentName}`

const scheduleEquipmentLabel = (equipmentId?: number) => {
  if (!equipmentId) {
    return '-'
  }
  const equipment = equipmentOptions.value.find((item) => item.id === equipmentId)
  return equipment ? equipmentLabel(equipment) : `#${equipmentId}`
}

const loadEquipmentOptions = async () => {
  equipmentLoading.value = true
  try {
    const list = await LabEquipmentAssetApi.getAvailableEquipment({
      domainCode: taskDetail.value?.domainCode,
      testItem: taskDetail.value?.testItem
    })
    equipmentOptions.value = normalizeEquipmentOptions(list || [])
  } finally {
    equipmentLoading.value = false
  }
}

const normalizeEquipmentOptions = (list: LabEquipmentAssetVO[]) => {
  if (!taskDetail.value?.equipmentId || list.some((item) => item.id === taskDetail.value?.equipmentId)) {
    return list
  }
  return [
    {
      id: taskDetail.value.equipmentId,
      equipmentCode: taskDetail.value.equipmentCode || `#${taskDetail.value.equipmentId}`,
      equipmentName: taskDetail.value.equipmentName || '当前绑定设备',
      status: 'enabled'
    },
    ...list
  ]
}

const handleEquipmentChange = (equipmentId?: number) => {
  const equipment = equipmentOptions.value.find((item) => item.id === equipmentId)
  formData.equipmentCode = equipment?.equipmentCode
  formData.equipmentName = equipment?.equipmentName
}

const loadSchedules = async () => {
  if (!taskDetail.value?.id) {
    scheduleList.value = []
    return
  }
  const params: LimsTaskPageReqVO = {
    pageNo: 1,
    pageSize: 20,
    taskId: taskDetail.value.id
  }
  const page = await LimsWorkflowApi.getTaskSchedulePage(params)
  scheduleList.value = page.list || []
}

const validateWindow = () => {
  if (!formData.taskId) {
    throw new Error('任务不存在，无法排程')
  }
  if (!formData.plannedStartTime || !formData.plannedEndTime) {
    throw new Error('请填写完整的计划开始和计划结束时间')
  }
  if (!dayjs(formData.plannedEndTime).isAfter(dayjs(formData.plannedStartTime))) {
    throw new Error('计划结束时间必须晚于计划开始时间')
  }
}

const afterSubmit = async (successText: string) => {
  await loadTask(taskDetail.value!.id!)
  await loadSchedules()
  message.success(successText)
  emit('success')
  visible.value = false
}

const handleSchedule = async () => {
  try {
    validateWindow()
  } catch (error) {
    message.error((error as Error).message)
    return
  }
  submitting.value = true
  try {
    await LimsWorkflowApi.scheduleTask({ ...formData })
    await afterSubmit('排程已更新')
  } finally {
    submitting.value = false
  }
}

const handleDefaultSchedule = async () => {
  if (!taskDetail.value?.id) {
    return
  }
  submitting.value = true
  try {
    await LimsWorkflowApi.scheduleDefaultTask(taskDetail.value.id)
    await afterSubmit('默认排程已执行')
  } finally {
    submitting.value = false
  }
}

const open = async (task: LimsTaskVO, mode: 'schedule' | 'default' = 'schedule') => {
  preferredMode.value = mode
  visible.value = true
  loading.value = true
  try {
    await loadTask(task.id!)
    await loadEquipmentOptions()
    await loadSchedules()
  } finally {
    loading.value = false
  }
}

defineExpose({ open })
</script>
