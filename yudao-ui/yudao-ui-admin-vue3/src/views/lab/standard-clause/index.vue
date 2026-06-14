<template>
  <ContentWrap>
    <el-form ref="queryFormRef" :inline="true" :model="queryParams" class="-mb-15px" label-width="88px">
      <el-form-item label="标准" prop="standardId">
        <el-select v-model="queryParams.standardId" class="!w-220px" clearable filterable>
          <el-option v-for="item in standards" :key="item.id" :label="item.standardName" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="条款编号" prop="clauseCode">
        <el-input v-model="queryParams.clauseCode" class="!w-160px" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="业务分类" prop="clauseCategory">
        <el-input v-model="queryParams.clauseCategory" class="!w-160px" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon class="mr-5px" icon="ep:search" />搜索</el-button>
        <el-button @click="resetQuery"><Icon class="mr-5px" icon="ep:refresh" />重置</el-button>
        <el-button v-hasPermi="['lab:standard-clause:create']" plain type="primary" @click="openForm('create')">
          <Icon class="mr-5px" icon="ep:plus" />新增
        </el-button>
        <el-button v-hasPermi="['lab:standard-clause:export']" plain type="success" @click="handleExport">
          <Icon class="mr-5px" icon="ep:download" />导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" row-key="id">
      <el-table-column align="center" label="标准" min-width="190">
        <template #default="scope">{{ getStandardName(scope.row.standardId) }}</template>
      </el-table-column>
      <el-table-column align="center" label="条款编号" min-width="140" prop="clauseCode" />
      <el-table-column align="center" label="条款标题" min-width="220" prop="clauseTitle" show-overflow-tooltip />
      <el-table-column align="center" label="业务分类" min-width="130" prop="clauseCategory" />
      <el-table-column align="center" label="证据类型" min-width="220" prop="evidenceTypeCodes" show-overflow-tooltip />
      <el-table-column align="center" label="状态" min-width="100" prop="status" />
      <el-table-column align="center" fixed="right" label="操作" width="150">
        <template #default="scope">
          <el-button v-hasPermi="['lab:standard-clause:update']" link type="primary" @click="openForm('update', scope.row.id)">编辑</el-button>
          <el-button v-hasPermi="['lab:standard-clause:delete']" link type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination v-model:limit="queryParams.pageSize" v-model:page="queryParams.pageNo" :total="total" @pagination="getList" />
  </ContentWrap>

  <el-dialog v-model="formVisible" :title="formType === 'create' ? '新增条款映射' : '编辑条款映射'" width="760px">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="112px">
      <el-form-item label="标准" prop="standardId">
        <el-select v-model="formData.standardId" class="w-1/1" filterable>
          <el-option v-for="item in standards" :key="item.id" :label="item.standardName" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-row :gutter="16">
        <el-col :span="12"><el-form-item label="条款编号" prop="clauseCode"><el-input v-model="formData.clauseCode" /></el-form-item></el-col>
        <el-col :span="12"><el-form-item label="业务分类" prop="clauseCategory"><el-input v-model="formData.clauseCategory" /></el-form-item></el-col>
      </el-row>
      <el-form-item label="条款标题" prop="clauseTitle"><el-input v-model="formData.clauseTitle" /></el-form-item>
      <el-form-item label="要求摘要" prop="requirementText"><el-input v-model="formData.requirementText" :rows="3" type="textarea" /></el-form-item>
      <el-form-item label="证据类型 JSON" prop="evidenceTypeCodes"><el-input v-model="formData.evidenceTypeCodes" :rows="3" type="textarea" /></el-form-item>
      <el-form-item label="状态" prop="status"><el-input v-model="formData.status" /></el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="formVisible = false">取 消</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { LabStandardApi, LabStandardClauseVO, LabStandardVO } from '@/api/lab/standard'

defineOptions({ name: 'LabStandardClause' })

const message = useMessage()
const { t } = useI18n()
const loading = ref(true)
const list = ref<LabStandardClauseVO[]>([])
const standards = ref<LabStandardVO[]>([])
const total = ref(0)
const queryParams = reactive({ pageNo: 1, pageSize: 10, standardId: undefined, clauseCode: undefined, clauseCategory: undefined })
const queryFormRef = ref()

const getStandards = async () => {
  const data = await LabStandardApi.getStandardPage({ pageNo: 1, pageSize: 100, status: 'active' })
  standards.value = data.list
}
const getList = async () => {
  loading.value = true
  try {
    const data = await LabStandardApi.getStandardClausePage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}
const getStandardName = (standardId?: number) => standards.value.find((item) => item.id === standardId)?.standardName || standardId
const handleQuery = () => { queryParams.pageNo = 1; getList() }
const resetQuery = () => { queryFormRef.value.resetFields(); handleQuery() }

const formVisible = ref(false)
const formLoading = ref(false)
const formType = ref('')
const formData = ref<LabStandardClauseVO>({ standardId: 0, clauseCode: '', clauseTitle: '', clauseCategory: '', requirementText: '', evidenceTypeCodes: '[]', status: 'active' })
const formRules = reactive({
  standardId: [{ required: true, message: '标准不能为空', trigger: 'change' }],
  clauseCode: [{ required: true, message: '条款编号不能为空', trigger: 'blur' }],
  clauseTitle: [{ required: true, message: '条款标题不能为空', trigger: 'blur' }],
  clauseCategory: [{ required: true, message: '业务分类不能为空', trigger: 'blur' }],
  requirementText: [{ required: true, message: '要求摘要不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'blur' }]
})
const formRef = ref()

const openForm = async (type: string, id?: number) => {
  formType.value = type
  formVisible.value = true
  formData.value = { standardId: standards.value[0]?.id || 0, clauseCode: '', clauseTitle: '', clauseCategory: '', requirementText: '', evidenceTypeCodes: '[]', status: 'active' }
  if (id) {
    formLoading.value = true
    try { formData.value = await LabStandardApi.getStandardClause(id) } finally { formLoading.value = false }
  }
}
const submitForm = async () => {
  await formRef.value.validate()
  formLoading.value = true
  try {
    if (formType.value === 'create') {
      await LabStandardApi.createStandardClause(formData.value)
      message.success(t('common.createSuccess'))
    } else {
      await LabStandardApi.updateStandardClause(formData.value)
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
  await LabStandardApi.deleteStandardClause(id)
  message.success(t('common.delSuccess'))
  await getList()
}
const handleExport = async () => {
  await LabStandardApi.exportStandardClause(queryParams)
  message.success('导出任务已生成')
}

onMounted(async () => {
  await getStandards()
  await getList()
})
</script>
