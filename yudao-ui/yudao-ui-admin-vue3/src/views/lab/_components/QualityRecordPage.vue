<template>
  <ContentWrap>
    <div class="mb-16px text-16px font-600">{{ title }}</div>
    <el-form ref="queryFormRef" :inline="true" :model="queryParams" class="-mb-15px" label-width="88px">
      <el-form-item label="关键字" prop="recordName">
        <el-input v-model="queryParams.recordName" class="!w-220px" clearable @keyup.enter="handleQuery" />
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
        <el-button v-hasPermi="permissionOf('export')" plain type="success" @click="handleExport">
          <Icon class="mr-5px" icon="ep:download" />导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" row-key="id">
      <el-table-column align="center" :label="noLabel" min-width="150" :prop="noField" show-overflow-tooltip />
      <el-table-column align="center" :label="nameLabel" min-width="220" :prop="nameField" show-overflow-tooltip />
      <el-table-column v-for="field in visibleFields" :key="field.prop" align="center" :label="field.label" min-width="140" :prop="field.prop" show-overflow-tooltip />
      <el-table-column align="center" label="状态" min-width="100" prop="status">
        <template #default="scope"><el-tag>{{ scope.row.status || '-' }}</el-tag></template>
      </el-table-column>
      <el-table-column align="center" fixed="right" label="操作" width="260">
        <template #default="scope">
          <el-button v-for="action in rowActions" :key="action.label" link type="primary" @click="runAction(action, scope.row)">{{ action.label }}</el-button>
          <el-button v-hasPermi="permissionOf('update')" link type="primary" @click="openForm('update', scope.row.id)">编辑</el-button>
          <el-button v-hasPermi="permissionOf('delete')" link type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination v-model:limit="queryParams.pageSize" v-model:page="queryParams.pageNo" :total="total" @pagination="getList" />
  </ContentWrap>

  <el-dialog v-model="formVisible" :title="formType === 'create' ? '新增' + title : '编辑' + title" width="860px">
    <el-form ref="formRef" :model="formData" label-width="120px">
      <el-row :gutter="16">
        <el-col v-for="field in formFields" :key="field.prop" :span="field.span || 12">
          <el-form-item :label="field.label" :prop="field.prop">
            <el-input v-model="formData[field.prop]" :rows="field.type === 'textarea' ? 3 : undefined" :type="field.type || 'text'" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="formVisible = false">取 消</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { LabQualityApi, LabQualityRecordVO } from '@/api/lab/quality'

defineOptions({ name: 'LabQualityRecordPage' })

interface FieldConfig { prop: string; label: string; type?: string; span?: number; table?: boolean }
interface ActionConfig { label: string; url: string; method?: 'post' | 'put' | 'get' }

const props = defineProps<{
  title: string
  baseUrl: string
  permission: string
  noField: string
  noLabel: string
  nameField: string
  nameLabel: string
  fields: FieldConfig[]
  rowActions?: ActionConfig[]
}>()

const message = useMessage()
const { t } = useI18n()
const loading = ref(true)
const list = ref<LabQualityRecordVO[]>([])
const total = ref(0)
const queryParams = reactive({ pageNo: 1, pageSize: 10, recordName: undefined, status: undefined })
const queryFormRef = ref()
const formVisible = ref(false)
const formLoading = ref(false)
const formType = ref('')
const formData = ref<LabQualityRecordVO>({ status: 'draft' })
const formRef = ref()

const title = computed(() => props.title)
const noField = computed(() => props.noField)
const noLabel = computed(() => props.noLabel)
const nameField = computed(() => props.nameField)
const nameLabel = computed(() => props.nameLabel)
const rowActions = computed(() => props.rowActions || [])
const visibleFields = computed(() => props.fields.filter((field) => field.table !== false && field.prop !== props.noField && field.prop !== props.nameField && field.prop !== 'status').slice(0, 5))
const formFields = computed(() => {
  const core = [{ prop: props.noField, label: props.noLabel }, { prop: props.nameField, label: props.nameLabel }]
  const merged = [...core, ...props.fields, { prop: 'status', label: '状态' }]
  const seen = new Set<string>()
  return merged.filter((field) => {
    if (seen.has(field.prop)) return false
    seen.add(field.prop)
    return true
  })
})

const permissionOf = (action: string) => [  props.permission + ':' + action
]

const emptyForm = () => ({ status: 'draft' })
const getList = async () => {
  loading.value = true
  try {
    const data = await LabQualityApi.page(props.baseUrl, queryParams)
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
    try { formData.value = await LabQualityApi.get(props.baseUrl, id) } finally { formLoading.value = false }
  }
}
const submitForm = async () => {
  formLoading.value = true
  try {
    if (formType.value === 'create') {
      await LabQualityApi.create(props.baseUrl, formData.value)
      message.success(t('common.createSuccess'))
    } else {
      await LabQualityApi.update(props.baseUrl, formData.value)
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
  await LabQualityApi.delete(props.baseUrl, id)
  message.success(t('common.delSuccess'))
  await getList()
}
const handleExport = async () => {
  await LabQualityApi.export(props.baseUrl, queryParams)
  message.success('导出任务已生成')
}
const runAction = async (action: ActionConfig, row: LabQualityRecordVO) => {
  if (!row.id) return
  if (action.method === 'post') await LabQualityApi.postAction(action.url, row.id)
  else if (action.method === 'get') await LabQualityApi.getAction(action.url, row.id)
  else await LabQualityApi.putAction(action.url, row.id)
  message.success('操作成功')
  await getList()
}

onMounted(() => getList())
</script>
