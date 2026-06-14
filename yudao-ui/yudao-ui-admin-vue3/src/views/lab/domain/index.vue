<template>
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="88px"
    >
      <el-form-item label="领域编码" prop="domainCode">
        <el-input
          v-model="queryParams.domainCode"
          class="!w-240px"
          clearable
          placeholder="请输入领域编码"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="领域名称" prop="domainName">
        <el-input
          v-model="queryParams.domainName"
          class="!w-240px"
          clearable
          placeholder="请输入领域名称"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" class="!w-160px" clearable placeholder="请选择状态">
          <el-option
            v-for="item in statusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
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
        <el-button
          v-hasPermi="['lab:domain:create']"
          plain
          type="primary"
          @click="openForm('create')"
        >
          <Icon class="mr-5px" icon="ep:plus" />
          新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" row-key="id">
      <el-table-column align="center" label="领域编码" min-width="140" prop="domainCode" />
      <el-table-column align="center" label="领域名称" min-width="160" prop="domainName" />
      <el-table-column align="center" label="说明" min-width="260" prop="description" show-overflow-tooltip />
      <el-table-column align="center" label="状态" min-width="100" prop="status">
        <template #default="scope">
          <el-tag :type="getStatusTag(scope.row.status)">
            {{ getStatusLabel(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="创建时间"
        prop="createTime"
        width="180"
      />
      <el-table-column align="center" fixed="right" label="操作" width="160">
        <template #default="scope">
          <el-button
            v-hasPermi="['lab:domain:update']"
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >
            编辑
          </el-button>
          <el-button
            v-hasPermi="['lab:domain:delete']"
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
          >
            删除
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

  <el-dialog v-model="formVisible" :title="formTitle" width="560px">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="96px">
      <el-form-item label="领域编码" prop="domainCode">
        <el-input v-model="formData.domainCode" maxlength="64" placeholder="如 FOOD" />
      </el-form-item>
      <el-form-item label="领域名称" prop="domainName">
        <el-input v-model="formData.domainName" maxlength="128" placeholder="请输入领域名称" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="formData.status" class="w-1/1" placeholder="请选择状态">
          <el-option
            v-for="item in statusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="说明" prop="description">
        <el-input
          v-model="formData.description"
          maxlength="512"
          placeholder="请输入领域说明"
          show-word-limit
          type="textarea"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="formVisible = false">取 消</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { dateFormatter } from '@/utils/formatTime'
import { LabDomainProfileApi, LabDomainProfileVO } from '@/api/lab/domain'

defineOptions({ name: 'LabDomain' })

const message = useMessage()
const { t } = useI18n()

const loading = ref(true)
const list = ref<LabDomainProfileVO[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  domainCode: undefined,
  domainName: undefined,
  status: undefined
})
const queryFormRef = ref()

const statusOptions = [
  { label: '启用', value: 'active', tag: 'success' },
  { label: '草稿', value: 'draft', tag: 'info' },
  { label: '停用', value: 'disabled', tag: 'danger' }
]

const getStatusLabel = (status: string) => {
  return statusOptions.find((item) => item.value === status)?.label || status
}

const getStatusTag = (status: string) => {
  return statusOptions.find((item) => item.value === status)?.tag || ''
}

const getList = async () => {
  loading.value = true
  try {
    const data = await LabDomainProfileApi.getDomainProfilePage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

const formVisible = ref(false)
const formLoading = ref(false)
const formType = ref('')
const formTitle = computed(() => (formType.value === 'create' ? '新增检测领域' : '编辑检测领域'))
const formData = ref<LabDomainProfileVO>({
  domainCode: '',
  domainName: '',
  description: '',
  status: 'active'
})
const formRules = reactive({
  domainCode: [{ required: true, message: '领域编码不能为空', trigger: 'blur' }],
  domainName: [{ required: true, message: '领域名称不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'change' }]
})
const formRef = ref()

const openForm = async (type: string, id?: number) => {
  formVisible.value = true
  formType.value = type
  resetForm()
  if (!id) {
    return
  }
  formLoading.value = true
  try {
    formData.value = await LabDomainProfileApi.getDomainProfile(id)
  } finally {
    formLoading.value = false
  }
}

const submitForm = async () => {
  await formRef.value.validate()
  formLoading.value = true
  try {
    if (formType.value === 'create') {
      await LabDomainProfileApi.createDomainProfile(formData.value)
      message.success(t('common.createSuccess'))
    } else {
      await LabDomainProfileApi.updateDomainProfile(formData.value)
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
  await LabDomainProfileApi.deleteDomainProfile(id)
  message.success(t('common.delSuccess'))
  await getList()
}

const resetForm = () => {
  formData.value = {
    domainCode: '',
    domainName: '',
    description: '',
    status: 'active'
  }
  formRef.value?.resetFields()
}

onMounted(() => {
  getList()
})
</script>
