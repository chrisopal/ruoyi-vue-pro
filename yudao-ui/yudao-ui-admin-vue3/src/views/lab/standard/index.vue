<template>
  <ContentWrap>
    <el-form ref="queryFormRef" :inline="true" :model="queryParams" class="-mb-15px" label-width="88px">
      <el-form-item label="标准编码" prop="standardCode">
        <el-input v-model="queryParams.standardCode" class="!w-180px" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="标准名称" prop="standardName">
        <el-input v-model="queryParams.standardName" class="!w-220px" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="标准类型" prop="standardType">
        <el-input v-model="queryParams.standardType" class="!w-160px" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon class="mr-5px" icon="ep:search" />搜索</el-button>
        <el-button @click="resetQuery"><Icon class="mr-5px" icon="ep:refresh" />重置</el-button>
        <el-button v-hasPermi="['lab:standard:create']" plain type="primary" @click="openForm('create')">
          <Icon class="mr-5px" icon="ep:plus" />新增
        </el-button>
        <el-button v-hasPermi="['lab:standard:export']" plain type="success" @click="handleExport">
          <Icon class="mr-5px" icon="ep:download" />导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" row-key="id">
      <el-table-column align="center" label="标准编码" min-width="150" prop="standardCode" />
      <el-table-column align="center" label="标准名称" min-width="220" prop="standardName" />
      <el-table-column align="center" label="版本" min-width="100" prop="standardVersion" />
      <el-table-column align="center" label="类型" min-width="130" prop="standardType" />
      <el-table-column align="center" label="状态" min-width="100" prop="status" />
      <el-table-column align="center" label="备注" min-width="260" prop="remark" show-overflow-tooltip />
      <el-table-column align="center" fixed="right" label="操作" width="150">
        <template #default="scope">
          <el-button v-hasPermi="['lab:standard:update']" link type="primary" @click="openForm('update', scope.row.id)">编辑</el-button>
          <el-button v-hasPermi="['lab:standard:delete']" link type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination v-model:limit="queryParams.pageSize" v-model:page="queryParams.pageNo" :total="total" @pagination="getList" />
  </ContentWrap>

  <el-dialog v-model="formVisible" :title="formType === 'create' ? '新增标准' : '编辑标准'" width="680px">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="104px">
      <el-row :gutter="16">
        <el-col :span="12"><el-form-item label="标准编码" prop="standardCode"><el-input v-model="formData.standardCode" /></el-form-item></el-col>
        <el-col :span="12"><el-form-item label="标准版本" prop="standardVersion"><el-input v-model="formData.standardVersion" /></el-form-item></el-col>
      </el-row>
      <el-form-item label="标准名称" prop="standardName"><el-input v-model="formData.standardName" /></el-form-item>
      <el-row :gutter="16">
        <el-col :span="12"><el-form-item label="标准类型" prop="standardType"><el-input v-model="formData.standardType" /></el-form-item></el-col>
        <el-col :span="12"><el-form-item label="状态" prop="status"><el-input v-model="formData.status" /></el-form-item></el-col>
      </el-row>
      <el-form-item label="备注" prop="remark"><el-input v-model="formData.remark" type="textarea" /></el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="formVisible = false">取 消</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { LabStandardApi, LabStandardVO } from '@/api/lab/standard'

defineOptions({ name: 'LabStandard' })

const message = useMessage()
const { t } = useI18n()
const loading = ref(true)
const list = ref<LabStandardVO[]>([])
const total = ref(0)
const queryParams = reactive({ pageNo: 1, pageSize: 10, standardCode: undefined, standardName: undefined, standardType: undefined })
const queryFormRef = ref()

const getList = async () => {
  loading.value = true
  try {
    const data = await LabStandardApi.getStandardPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}
const handleQuery = () => { queryParams.pageNo = 1; getList() }
const resetQuery = () => { queryFormRef.value.resetFields(); handleQuery() }

const formVisible = ref(false)
const formLoading = ref(false)
const formType = ref('')
const formData = ref<LabStandardVO>({ standardCode: '', standardName: '', standardVersion: '', standardType: '', status: 'active' })
const formRules = reactive({
  standardCode: [{ required: true, message: '标准编码不能为空', trigger: 'blur' }],
  standardName: [{ required: true, message: '标准名称不能为空', trigger: 'blur' }],
  standardVersion: [{ required: true, message: '标准版本不能为空', trigger: 'blur' }],
  standardType: [{ required: true, message: '标准类型不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'blur' }]
})
const formRef = ref()

const openForm = async (type: string, id?: number) => {
  formType.value = type
  formVisible.value = true
  formData.value = { standardCode: '', standardName: '', standardVersion: '', standardType: '', status: 'active' }
  if (id) {
    formLoading.value = true
    try { formData.value = await LabStandardApi.getStandard(id) } finally { formLoading.value = false }
  }
}
const submitForm = async () => {
  await formRef.value.validate()
  formLoading.value = true
  try {
    if (formType.value === 'create') {
      await LabStandardApi.createStandard(formData.value)
      message.success(t('common.createSuccess'))
    } else {
      await LabStandardApi.updateStandard(formData.value)
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
  await LabStandardApi.deleteStandard(id)
  message.success(t('common.delSuccess'))
  await getList()
}
const handleExport = async () => {
  await LabStandardApi.exportStandard(queryParams)
  message.success('导出任务已生成')
}

onMounted(() => getList())
</script>
