<template>
  <ContentWrap>
    <div class="mb-16px flex items-center justify-between gap-16px">
      <div>
        <div class="text-16px font-600">检测任务</div>
        <div class="mt-4px text-12px color-#909399">
          按任务生命周期管理排程、就绪、执行记录、QC 和技术复核
        </div>
      </div>
      <el-button @click="getList">
        <Icon class="mr-5px" icon="ep:refresh" />
        刷新
      </el-button>
    </div>

    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="88px"
    >
      <el-form-item label="关键字" prop="keyword">
        <el-input
          v-model="queryParams.keyword"
          class="!w-220px"
          clearable
          placeholder="任务/需求/样品"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="任务状态" prop="taskStatus">
        <el-select v-model="queryParams.taskStatus" class="!w-160px" clearable placeholder="全部">
          <el-option
            v-for="item in TASK_STATUS_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="排程状态" prop="scheduleStatus">
        <el-select v-model="queryParams.scheduleStatus" class="!w-160px" clearable placeholder="全部">
          <el-option
            v-for="item in SCHEDULE_STATUS_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="执行人" prop="assignedUserId">
        <el-input-number
          v-model="queryParams.assignedUserId"
          :min="1"
          class="!w-150px"
          controls-position="right"
        />
      </el-form-item>
      <el-form-item label="设备" prop="equipmentId">
        <el-input-number
          v-model="queryParams.equipmentId"
          :min="1"
          class="!w-150px"
          controls-position="right"
        />
      </el-form-item>
      <el-form-item label="计划开始">
        <el-date-picker
          v-model="plannedStartRange"
          class="!w-330px"
          end-placeholder="结束"
          format="YYYY-MM-DD HH:mm:ss"
          range-separator="-"
          start-placeholder="开始"
          type="datetimerange"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery">
          <Icon class="mr-5px" icon="ep:search" />
          搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon class="mr-5px" icon="ep:refresh" />
          重置
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" row-key="id">
      <el-table-column align="center" fixed="left" label="任务编号" min-width="150" prop="taskNo" show-overflow-tooltip />
      <el-table-column align="center" fixed="left" label="任务名称" min-width="190" prop="taskName" show-overflow-tooltip />
      <el-table-column align="center" label="检测项目" min-width="180" prop="testItem" show-overflow-tooltip />
      <el-table-column align="center" label="需求编号" min-width="150" prop="requestNo" show-overflow-tooltip />
      <el-table-column align="center" label="样品编号" min-width="150" prop="sampleNo" show-overflow-tooltip />
      <el-table-column align="center" label="生命周期" min-width="110">
        <template #default="{ row }">
          <el-tag :type="getTaskStatusTagType(row.taskStatus)">
            {{ formatTaskStatus(row.taskStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="排程" min-width="110">
        <template #default="{ row }">
          <el-tag :type="getScheduleStatusTagType(row.scheduleStatus)">
            {{ formatScheduleStatus(row.scheduleStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="执行人" min-width="90">
        <template #default="{ row }">{{ formatActorLabel(row.assignedUserName, row.assignedUserId) }}</template>
      </el-table-column>
      <el-table-column align="center" label="设备" min-width="150" show-overflow-tooltip>
        <template #default="{ row }">
          {{ row.equipmentCode || formatActorLabel(row.equipmentName, row.equipmentId) }}
        </template>
      </el-table-column>
      <el-table-column align="center" label="计划窗口" min-width="250" show-overflow-tooltip>
        <template #default="{ row }">{{ formatTaskWindow(row) }}</template>
      </el-table-column>
      <el-table-column align="center" label="QC" min-width="100">
        <template #default="{ row }">
          <el-tag :type="getReviewStatusTagType(row.qcStatus)">
            {{ formatReviewStatus(row.qcStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="复核" min-width="100">
        <template #default="{ row }">
          <el-tag :type="getReviewStatusTagType(row.reviewStatus)">
            {{ formatReviewStatus(row.reviewStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="可报告" min-width="100">
        <template #default="{ row }">
          <el-tag :type="getReportEligibleTagType(row.reportEligible)">
            {{ formatReportEligible(row.reportEligible) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="阻断原因" min-width="180" prop="blockReason" show-overflow-tooltip />
      <el-table-column align="center" fixed="right" label="操作" width="360">
        <template #default="{ row }">
          <el-button v-hasPermi="['lims:task:schedule']" link type="primary" @click="openSchedule(row)">
            排程
          </el-button>
          <el-button v-hasPermi="['lims:task:schedule']" link type="primary" @click="openDefaultSchedule(row)">
            快排
          </el-button>
          <el-button v-hasPermi="['lims:task:readiness']" link type="primary" @click="openReadiness(row, 'ready')">
            就绪
          </el-button>
          <el-button v-hasPermi="['lims:task:readiness']" link type="primary" @click="openReadiness(row, 'start')">
            开始
          </el-button>
          <el-button v-hasPermi="['lims:task:hold']" link type="warning" @click="handleHold(row)">
            挂起
          </el-button>
          <el-button v-hasPermi="['lims:task:record']" link type="primary" @click="openRecord(row, 'raw')">
            记录
          </el-button>
          <el-button v-hasPermi="['lims:task:record']" link type="primary" @click="openRecord(row, 'qc')">
            QC
          </el-button>
          <el-button v-hasPermi="['lims:task:review']" link type="primary" @click="openReview(row)">
            复核
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination
      v-model:limit="queryParams.pageSize"
      v-model:page="queryParams.pageNo"
      :total="total"
      @pagination="getList"
    />
  </ContentWrap>

  <TaskSchedulePanel ref="schedulePanelRef" @success="getList" />
  <TaskReadinessDrawer ref="readinessDrawerRef" @success="getList" />
  <TaskRecordDrawer ref="recordDrawerRef" @success="getList" />
  <TaskReviewDrawer ref="reviewDrawerRef" @success="getList" />
</template>

<script lang="ts" setup>
import { LimsWorkflowApi, type LimsTaskPageReqVO, type LimsTaskVO } from '@/api/lims/workflow'
import TaskReadinessDrawer from './TaskReadinessDrawer.vue'
import TaskRecordDrawer from './TaskRecordDrawer.vue'
import TaskReviewDrawer from './TaskReviewDrawer.vue'
import TaskSchedulePanel from './TaskSchedulePanel.vue'
import {
  formatActorLabel,
  formatReportEligible,
  formatReviewStatus,
  formatScheduleStatus,
  formatTaskStatus,
  formatTaskWindow,
  getReportEligibleTagType,
  getReviewStatusTagType,
  getScheduleStatusTagType,
  getTaskStatusTagType,
  SCHEDULE_STATUS_OPTIONS,
  TASK_STATUS_OPTIONS
} from './taskStatus'

defineOptions({ name: 'LimsTask' })

const loading = ref(true)
const list = ref<LimsTaskVO[]>([])
const total = ref(0)
const message = useMessage()
const queryFormRef = ref()
const plannedStartRange = ref<[string, string] | []>([])
const queryParams = reactive<LimsTaskPageReqVO>({
  pageNo: 1,
  pageSize: 10,
  keyword: undefined,
  taskStatus: undefined,
  scheduleStatus: undefined,
  assignedUserId: undefined,
  equipmentId: undefined,
  plannedStartTimeBegin: undefined,
  plannedStartTimeEnd: undefined
})

const schedulePanelRef = ref<InstanceType<typeof TaskSchedulePanel>>()
const readinessDrawerRef = ref<InstanceType<typeof TaskReadinessDrawer>>()
const recordDrawerRef = ref<InstanceType<typeof TaskRecordDrawer>>()
const reviewDrawerRef = ref<InstanceType<typeof TaskReviewDrawer>>()

const syncRangeToQuery = () => {
  queryParams.plannedStartTimeBegin = plannedStartRange.value?.[0]
  queryParams.plannedStartTimeEnd = plannedStartRange.value?.[1]
}

const getList = async () => {
  loading.value = true
  try {
    syncRangeToQuery()
    const data = await LimsWorkflowApi.getTaskPage(queryParams)
    list.value = data.list || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

const resetQuery = () => {
  queryFormRef.value?.resetFields()
  plannedStartRange.value = []
  handleQuery()
}

const openSchedule = (row: LimsTaskVO) => {
  schedulePanelRef.value?.open(row, 'schedule')
}

const openDefaultSchedule = (row: LimsTaskVO) => {
  schedulePanelRef.value?.open(row, 'default')
}

const openReadiness = (row: LimsTaskVO, action: 'ready' | 'start') => {
  readinessDrawerRef.value?.open(row, action)
}

const openRecord = (row: LimsTaskVO, mode: 'raw' | 'qc') => {
  recordDrawerRef.value?.open(row, mode)
}

const openReview = (row: LimsTaskVO) => {
  reviewDrawerRef.value?.open(row)
}

const handleHold = async (row: LimsTaskVO) => {
  if (!row.id) return
  await message.confirm('确认挂起该检测任务？')
  await LimsWorkflowApi.holdTask(row.id, '任务由任务工作台挂起')
  message.success('任务已挂起')
  await getList()
}

onMounted(() => getList())
</script>
