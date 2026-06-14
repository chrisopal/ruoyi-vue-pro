<template>
  <ContentWrap>
    <div class="mb-16px flex items-center justify-between">
      <div>
        <div class="text-16px font-600">{{ title }}</div>
        <div v-if="subtitle" class="mt-4px text-12px color-#909399">{{ subtitle }}</div>
      </div>
      <el-button @click="getList"><Icon class="mr-5px" icon="ep:refresh" />刷新</el-button>
    </div>
    <el-form ref="queryFormRef" :inline="true" :model="queryParams" class="-mb-15px" label-width="88px">
      <el-form-item label="关键字" prop="keyword">
        <el-input v-model="queryParams.keyword" class="!w-220px" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-input v-model="queryParams.status" class="!w-160px" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon class="mr-5px" icon="ep:search" />搜索</el-button>
        <el-button @click="resetQuery"><Icon class="mr-5px" icon="ep:refresh" />重置</el-button>
        <el-button v-hasPermi="permissionOf('create')" plain type="primary" @click="openForm('create')">
          <Icon class="mr-5px" icon="ep:plus" />新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" row-key="id">
      <el-table-column align="center" :label="noLabel" min-width="160" :prop="noField" show-overflow-tooltip />
      <el-table-column align="center" :label="nameLabel" min-width="220" :prop="nameField" show-overflow-tooltip />
      <el-table-column
        v-for="field in visibleFields"
        :key="field.prop"
        align="center"
        :label="field.label"
        min-width="140"
        :prop="field.prop"
        show-overflow-tooltip
      >
        <template #default="scope">
          <div v-if="field.display === 'reportOutput'" class="flex flex-wrap justify-center gap-4px">
            <el-tag v-for="output in parseReportOutputs(scope.row[field.prop])" :key="output.fileUrl || output.format" size="small">
              <el-link
                v-if="output.fileUrl"
                :href="output.fileUrl"
                underline="never"
                target="_blank"
                type="primary"
              >
                {{ output.format }}
              </el-link>
              <span v-else>{{ output.format }}</span>
              <span v-if="output.contentHash" class="ml-4px text-10px opacity-70">
                #{{ shortHash(output.contentHash) }}
              </span>
            </el-tag>
            <span v-if="parseReportOutputs(scope.row[field.prop]).length === 0">-</span>
          </div>
          <span v-else>{{ formatCellValue(scope.row[field.prop]) }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="状态" min-width="110" prop="status">
        <template #default="scope"><el-tag>{{ scope.row.status || '-' }}</el-tag></template>
      </el-table-column>
      <el-table-column align="center" fixed="right" label="操作" min-width="420">
        <template #default="scope">
          <el-button
            v-for="action in rowActions"
            :key="action.label"
            v-hasPermi="permissionOfAction(action)"
            link
            type="primary"
            @click="runAction(action, scope.row)"
          >
            {{ action.label }}
          </el-button>
          <el-button v-hasPermi="permissionOf('update')" link type="primary" @click="openForm('update', scope.row.id)">编辑</el-button>
          <el-button v-hasPermi="permissionOf('delete')" link type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination v-model:limit="queryParams.pageSize" v-model:page="queryParams.pageNo" :total="total" @pagination="getList" />
  </ContentWrap>

  <el-dialog v-model="formVisible" :title="formType === 'create' ? '新增' + title : '编辑' + title" width="900px">
    <el-form :model="formData" label-width="120px">
      <el-row :gutter="16">
        <el-col v-for="field in formFields" :key="field.prop" :span="field.span || 12">
          <el-form-item :label="field.label" :prop="field.prop">
            <el-select v-if="field.type === 'select'" v-model="formData[field.prop]" class="w-1/1" filterable clearable>
              <el-option v-for="item in field.options || []" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
            <el-input v-else v-model="formData[field.prop]" :rows="field.type === 'textarea' ? 4 : undefined" :type="field.type || 'text'" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="formVisible = false">取 消</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="actionFormVisible" :title="actionFormTitle" width="760px">
    <el-form :model="actionFormData" label-width="120px">
      <el-row :gutter="16">
        <el-col v-for="field in actionFormFields" :key="field.prop" :span="field.span || 12">
          <el-form-item v-if="!field.hidden" :label="field.label" :prop="field.prop">
            <el-select v-if="field.type === 'select'" v-model="actionFormData[field.prop]" class="w-1/1" filterable clearable>
              <el-option v-for="item in field.options || []" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
            <el-input v-else v-model="actionFormData[field.prop]" :rows="field.type === 'textarea' ? 4 : undefined" :type="field.type || 'text'" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button :disabled="actionFormLoading" type="primary" @click="submitActionForm">确 定</el-button>
      <el-button @click="actionFormVisible = false">取 消</el-button>
    </template>
  </el-dialog>

  <el-drawer
    v-model="executionPlanVisible"
    append-to-body
    modal-class="lims-execution-plan-drawer"
    size="820px"
    title="执行计划与报告草稿计划"
  >
    <div v-loading="executionPlanLoading">
      <el-empty v-if="!executionPlan?.id" description="尚未生成执行计划，请先生成任务或执行计划" />
      <template v-else>
        <el-descriptions :column="3" border>
          <el-descriptions-item label="需求ID">{{ executionPlan.requestId }}</el-descriptions-item>
          <el-descriptions-item label="计划状态">{{ executionPlan.status || '-' }}</el-descriptions-item>
          <el-descriptions-item label="快照哈希">
            <span class="snapshot-hash">{{ executionPlan.workflowSnapshotHash || '-' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="样品要求">{{ executionSampleRequirements.length }}</el-descriptions-item>
          <el-descriptions-item label="任务计划">{{ executionTaskPlans.length }}</el-descriptions-item>
          <el-descriptions-item label="结果字段">{{ executionResultFieldPlans.length }}</el-descriptions-item>
          <el-descriptions-item label="质控规则">{{ executionQcPlans.length }}</el-descriptions-item>
          <el-descriptions-item label="证据要求">{{ executionEvidencePlans.length }}</el-descriptions-item>
          <el-descriptions-item label="报告格式">{{ reportOutputFormats.join(' / ') || '-' }}</el-descriptions-item>
        </el-descriptions>

        <el-row :gutter="16" class="mt-16px">
          <el-col :md="12" :xs="24">
            <div class="sub-title">ExecutionPlan</div>
            <el-table :data="executionTaskPlans" height="260" row-key="itemCode">
              <el-table-column label="检测项目" min-width="150" prop="itemName" />
              <el-table-column label="方法" min-width="130" prop="methodCode" />
              <el-table-column label="结果字段" width="100">
                <template #default="scope">{{ arraySize(scope.row.resultFields) }}</template>
              </el-table-column>
              <el-table-column label="质控" width="90">
                <template #default="scope">{{ arraySize(scope.row.qcRules) }}</template>
              </el-table-column>
              <el-table-column label="证据" width="90">
                <template #default="scope">{{ arraySize(scope.row.evidenceRequirements) }}</template>
              </el-table-column>
            </el-table>
          </el-col>
          <el-col :md="12" :xs="24">
            <div class="sub-title">ReportDraftPlan</div>
            <el-table :data="reportSections" height="260" row-key="sectionCode">
              <el-table-column label="章节" min-width="160" prop="sectionName" />
              <el-table-column label="来源" min-width="140" prop="sourceType" />
              <el-table-column label="显示" width="90">
                <template #default="scope">
                  <el-tag :type="scope.row.visible === false ? 'info' : 'success'">
                    {{ scope.row.visible === false ? '隐藏' : '显示' }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-col>
        </el-row>

        <el-row :gutter="16" class="mt-16px">
          <el-col :md="12" :xs="24">
            <div class="sub-title">样品 / 质控 / 证据</div>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="样品要求">
                {{ executionSampleRequirements.map((item) => item.requirementName || item.requirementCode).join(' / ') || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="质控规则">
                {{ executionQcPlans.map((item) => item.ruleName || item.ruleCode).join(' / ') || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="证据要求">
                {{ executionEvidencePlans.map((item) => item.requirementName || item.requirementCode).join(' / ') || '-' }}
              </el-descriptions-item>
            </el-descriptions>
          </el-col>
          <el-col :md="12" :xs="24">
            <div class="sub-title">报告数据绑定</div>
            <el-table :data="reportDataBindings" height="220" row-key="fieldCode">
              <el-table-column label="字段" min-width="150" prop="fieldName" />
              <el-table-column label="来源路径" min-width="180" prop="sourcePath" />
              <el-table-column label="必填" width="90">
                <template #default="scope">
                  <el-tag :type="scope.row.required ? 'danger' : 'info'">
                    {{ scope.row.required ? '必填' : '可选' }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-col>
        </el-row>
      </template>
    </div>
  </el-drawer>
</template>

<script lang="ts" setup>
import { LimsWorkflowApi } from '@/api/lims/workflow'
import type { LimsExecutionPlanVO, LimsWorkflowVO } from '@/api/lims/workflow'

defineOptions({ name: 'LimsWorkflowPage' })

interface FieldOption { label: string; value: string | number }
interface FieldConfig { prop: string; label: string; type?: string; display?: string; span?: number; table?: boolean; hidden?: boolean; options?: readonly FieldOption[] }
interface ActionConfig {
  label: string
  url?: string
  method?: 'post' | 'put'
  kind?: 'executionPlan'
  permission?: string | readonly string[]
  fields?: readonly FieldConfig[]
  formTitle?: string
  defaults?: LimsWorkflowVO | ((row: LimsWorkflowVO) => LimsWorkflowVO)
}
interface ReportOutput { format?: string; fileUrl?: string; contentHash?: string }

const props = defineProps<{
  title: string
  subtitle?: string
  baseUrl: string
  permission: string
  noField: string
  noLabel: string
  nameField: string
  nameLabel: string
  fields: readonly FieldConfig[]
  rowActions?: readonly ActionConfig[]
  defaults?: LimsWorkflowVO
}>()

const message = useMessage()
const { t } = useI18n()
const loading = ref(true)
const list = ref<LimsWorkflowVO[]>([])
const total = ref(0)
const queryParams = reactive({ pageNo: 1, pageSize: 10, keyword: undefined, status: undefined })
const queryFormRef = ref()
const formVisible = ref(false)
const formLoading = ref(false)
const formType = ref('')
const formData = ref<LimsWorkflowVO>({})
const actionFormVisible = ref(false)
const actionFormLoading = ref(false)
const actionFormConfig = ref<ActionConfig>()
const actionFormData = ref<LimsWorkflowVO>({})
const executionPlanVisible = ref(false)
const executionPlanLoading = ref(false)
const executionPlan = ref<LimsExecutionPlanVO>()

const title = computed(() => props.title)
const subtitle = computed(() => props.subtitle)
const noField = computed(() => props.noField)
const noLabel = computed(() => props.noLabel)
const nameField = computed(() => props.nameField)
const nameLabel = computed(() => props.nameLabel)
const rowActions = computed(() => props.rowActions || [])
const actionFormTitle = computed(() => actionFormConfig.value?.formTitle || actionFormConfig.value?.label || '操作')
const actionFormFields = computed(() => actionFormConfig.value?.fields || [])
const visibleFields = computed(() => props.fields.filter((field) => field.table !== false && field.prop !== props.noField && field.prop !== props.nameField && field.prop !== 'status').slice(0, 6))
const executionPlanJson = computed(() => parseJsonObject(executionPlan.value?.planJson))
const reportDraftPlanJson = computed(() => parseJsonObject(executionPlan.value?.reportDraftPlan || executionPlanJson.value.reportDraftPlan))
const executionSampleRequirements = computed(() => toArray(executionPlanJson.value.sampleRequirements))
const executionTaskPlans = computed(() => toArray(executionPlanJson.value.taskPlans))
const executionResultFieldPlans = computed(() => toArray(executionPlanJson.value.resultFieldPlans))
const executionQcPlans = computed(() => toArray(executionPlanJson.value.qcCheckPlans))
const executionEvidencePlans = computed(() => toArray(executionPlanJson.value.evidenceRequirementPlans))
const reportOutputFormats = computed(() => toArray(reportDraftPlanJson.value.outputFormats).map((item) => String(item)))
const reportSections = computed(() => toArray(reportDraftPlanJson.value.sections))
const reportDataBindings = computed(() => toArray(reportDraftPlanJson.value.dataBindings))
const formFields = computed(() => {
  const core: FieldConfig[] = [{ prop: props.noField, label: props.noLabel }, { prop: props.nameField, label: props.nameLabel }]
  const merged = [...core, ...props.fields, { prop: 'status', label: '状态' }]
  const seen = new Set<string>()
  return merged.filter((field) => {
    if (seen.has(field.prop)) return false
    seen.add(field.prop)
    return true
  })
})

const permissionOf = (action: string) => [props.permission + ':' + action]
const permissionOfAction = (action: ActionConfig) => {
  if (Array.isArray(action.permission)) return action.permission
  return [action.permission || props.permission + ':update']
}
const formatCellValue = (value: unknown) => {
  if (value === undefined || value === null || value === '') return '-'
  return String(value)
}
const emptyForm = () => ({ status: 'draft', ...(props.defaults || {}) })
const getList = async () => {
  loading.value = true
  try {
    const data = await LimsWorkflowApi.page(props.baseUrl, queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}
const handleQuery = () => { queryParams.pageNo = 1; getList() }
const resetQuery = () => { queryFormRef.value?.resetFields(); handleQuery() }
const openForm = async (type: string, id?: number) => {
  formType.value = type
  formVisible.value = true
  formData.value = emptyForm()
  if (id) {
    formLoading.value = true
    try { formData.value = await LimsWorkflowApi.get(props.baseUrl, id) } finally { formLoading.value = false }
  }
}
const submitForm = async () => {
  formLoading.value = true
  try {
    if (formType.value === 'create') {
      await LimsWorkflowApi.create(props.baseUrl, formData.value)
      message.success(t('common.createSuccess'))
    } else {
      await LimsWorkflowApi.update(props.baseUrl, formData.value)
      message.success(t('common.updateSuccess'))
    }
    formVisible.value = false
    await getList()
  } finally {
    formLoading.value = false
  }
}
const handleDelete = async (id: number) => {
  await message.delConfirm()
  await LimsWorkflowApi.delete(props.baseUrl, id)
  message.success(t('common.delSuccess'))
  await getList()
}
const runAction = async (action: ActionConfig, row: LimsWorkflowVO) => {
  if (!row.id) return
  if (action.kind === 'executionPlan') {
    await openExecutionPlan(row.id)
    return
  }
  if (action.fields?.length) {
    actionFormConfig.value = action
    actionFormData.value = resolveActionDefaults(action, row)
    actionFormVisible.value = true
    return
  }
  if (!action.url) return
  if (action.method === 'post') await LimsWorkflowApi.postAction(action.url, row.id)
  else await LimsWorkflowApi.putAction(action.url, row.id)
  message.success('操作成功')
  await getList()
}
const openExecutionPlan = async (requestId: number) => {
  executionPlanVisible.value = true
  executionPlanLoading.value = true
  executionPlan.value = undefined
  try {
    executionPlan.value = await LimsWorkflowApi.getExecutionPlan(requestId)
  } finally {
    executionPlanLoading.value = false
  }
}
const resolveActionDefaults = (action: ActionConfig, row: LimsWorkflowVO) => {
  if (typeof action.defaults === 'function') return action.defaults(row)
  return { id: row.id, taskId: row.id, ...(action.defaults || {}) }
}
const submitActionForm = async () => {
  if (!actionFormConfig.value) return
  if (!actionFormConfig.value.url) {
    message.error('操作地址未配置')
    return
  }
  actionFormLoading.value = true
  try {
    if (actionFormConfig.value.method === 'post') await LimsWorkflowApi.postBody(actionFormConfig.value.url, actionFormData.value)
    else await LimsWorkflowApi.putBody(actionFormConfig.value.url, actionFormData.value)
    message.success('操作成功')
    actionFormVisible.value = false
    await getList()
  } finally {
    actionFormLoading.value = false
  }
}
const parseReportOutputs = (value: unknown): ReportOutput[] => {
  if (!value) return []
  try {
    const manifest = (typeof value === 'string' ? JSON.parse(value) : value) as { outputs?: ReportOutput[] }
    return Array.isArray(manifest?.outputs) ? manifest.outputs : []
  } catch {
    return []
  }
}
const shortHash = (hash: string) => (hash.length > 10 ? hash.slice(0, 10) : hash)
const parseJsonObject = (value: unknown): Record<string, any> => {
  if (!value) return {}
  if (typeof value === 'object') return value as Record<string, any>
  try {
    const parsed = JSON.parse(String(value))
    return parsed && typeof parsed === 'object' && !Array.isArray(parsed) ? parsed : {}
  } catch {
    return {}
  }
}
const toArray = (value: unknown): any[] => Array.isArray(value) ? value : []
const arraySize = (value: unknown) => toArray(value).length

onMounted(() => getList())
</script>

<style scoped>
.snapshot-hash {
  display: inline-block;
  max-width: 260px;
  overflow: hidden;
  text-overflow: ellipsis;
  vertical-align: bottom;
  white-space: nowrap;
}

.sub-title {
  margin-bottom: 10px;
  font-weight: 600;
}

:global(.lims-execution-plan-drawer) {
  position: fixed !important;
  inset: 0 !important;
  width: 100vw !important;
}

:global(.lims-execution-plan-drawer .el-drawer.rtl) {
  right: 0 !important;
}
</style>
