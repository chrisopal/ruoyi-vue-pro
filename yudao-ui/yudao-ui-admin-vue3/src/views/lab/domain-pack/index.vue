<template>
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="88px"
    >
      <el-form-item label="检测领域" prop="domainId">
        <el-select v-model="queryParams.domainId" class="!w-220px" clearable placeholder="请选择检测领域">
          <el-option
            v-for="item in domainOptions"
            :key="item.id"
            :label="item.domainName"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="方案编码" prop="packCode">
        <el-input
          v-model="queryParams.packCode"
          class="!w-220px"
          clearable
          placeholder="请输入方案编码"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="方案名称" prop="packName">
        <el-input
          v-model="queryParams.packName"
          class="!w-220px"
          clearable
          placeholder="请输入方案名称"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="行业方向" prop="industry">
        <el-input
          v-model="queryParams.industry"
          class="!w-180px"
          clearable
          placeholder="请输入行业方向"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" class="!w-150px" clearable placeholder="请选择状态">
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
          v-hasPermi="['lab:domain-pack:create']"
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
      <el-table-column align="center" label="方案编码" min-width="180" prop="packCode" />
      <el-table-column align="center" label="方案名称" min-width="180" prop="packName" />
      <el-table-column align="center" label="检测领域" min-width="140">
        <template #default="scope">
          {{ getDomainName(scope.row.domainId) }}
        </template>
      </el-table-column>
      <el-table-column align="center" label="版本" min-width="90" prop="packVersion" />
      <el-table-column align="center" label="行业方向" min-width="110" prop="industry" />
      <el-table-column align="center" label="适用范围" min-width="260" prop="applicationScope" show-overflow-tooltip />
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
            v-hasPermi="['lab:domain-pack:update']"
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >
            编辑
          </el-button>
          <el-button
            v-hasPermi="['lab:domain-pack:delete']"
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

  <el-dialog v-model="formVisible" :title="formTitle" width="760px">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="112px">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="检测领域" prop="domainId">
            <el-select v-model="formData.domainId" class="w-1/1" placeholder="请选择检测领域">
              <el-option
                v-for="item in domainOptions"
                :key="item.id"
                :label="item.domainName"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
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
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="方案编码" prop="packCode">
            <el-input v-model="formData.packCode" maxlength="64" placeholder="如 FOOD_ROUTINE_V1" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="方案版本" prop="packVersion">
            <el-input v-model="formData.packVersion" maxlength="32" placeholder="如 1.0" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="方案名称" prop="packName">
        <el-input v-model="formData.packName" maxlength="128" placeholder="请输入方案名称" />
      </el-form-item>
      <el-form-item label="行业方向" prop="industry">
        <el-input v-model="formData.industry" maxlength="64" placeholder="如 食品、工业品、环境" />
      </el-form-item>
      <el-form-item label="适用范围" prop="applicationScope">
        <el-input
          v-model="formData.applicationScope"
          maxlength="512"
          placeholder="请输入适用的样品、项目、标准或客户场景"
          show-word-limit
          type="textarea"
        />
      </el-form-item>
      <el-form-item label="流程配置" prop="workflowSchema">
        <el-input
          v-model="formData.workflowSchema"
          :autosize="{ minRows: 4, maxRows: 8 }"
          placeholder='例如 {"stages":["request","sample","task","raw_record","report"]}'
          type="textarea"
        />
      </el-form-item>
      <el-form-item label="模板配置" prop="templateSchema">
        <el-input
          v-model="formData.templateSchema"
          :autosize="{ minRows: 4, maxRows: 8 }"
          placeholder='例如 {"templates":["sample_label","raw_record","report"]}'
          type="textarea"
        />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="formData.remark" maxlength="512" show-word-limit type="textarea" />
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
import { LabDomainPackApi, LabDomainPackVO } from '@/api/lab/domain-pack'

defineOptions({ name: 'LabDomainPack' })

const message = useMessage()
const { t } = useI18n()

const loading = ref(true)
const list = ref<LabDomainPackVO[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  domainId: undefined,
  packCode: undefined,
  packName: undefined,
  industry: undefined,
  status: undefined
})
const queryFormRef = ref()

const statusOptions = [
  { label: '启用', value: 'active', tag: 'success' },
  { label: '草稿', value: 'draft', tag: 'info' },
  { label: '停用', value: 'disabled', tag: 'danger' }
]

const domainOptions = ref<LabDomainProfileVO[]>([])

const getStatusLabel = (status: string) => {
  return statusOptions.find((item) => item.value === status)?.label || status
}

const getStatusTag = (status: string) => {
  return statusOptions.find((item) => item.value === status)?.tag || ''
}

const getDomainName = (domainId: number) => {
  return domainOptions.value.find((item) => item.id === domainId)?.domainName || domainId
}

const getDomainOptions = async () => {
  const data = await LabDomainProfileApi.getDomainProfilePage({
    pageNo: 1,
    pageSize: 100,
    status: 'active'
  })
  domainOptions.value = data.list
}

const getList = async () => {
  loading.value = true
  try {
    const data = await LabDomainPackApi.getDomainPackPage(queryParams)
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
const formTitle = computed(() => (formType.value === 'create' ? '新增检测方案包' : '编辑检测方案包'))
const formData = ref<LabDomainPackVO>({
  domainId: undefined as unknown as number,
  packCode: '',
  packName: '',
  packVersion: '1.0',
  industry: '',
  applicationScope: '',
  workflowSchema: '',
  templateSchema: '',
  status: 'draft',
  remark: ''
})
const formRules = reactive({
  domainId: [{ required: true, message: '检测领域不能为空', trigger: 'change' }],
  packCode: [{ required: true, message: '方案编码不能为空', trigger: 'blur' }],
  packName: [{ required: true, message: '方案名称不能为空', trigger: 'blur' }],
  packVersion: [{ required: true, message: '方案版本不能为空', trigger: 'blur' }],
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
    formData.value = await LabDomainPackApi.getDomainPack(id)
  } finally {
    formLoading.value = false
  }
}

const submitForm = async () => {
  await formRef.value.validate()
  formLoading.value = true
  try {
    if (formType.value === 'create') {
      await LabDomainPackApi.createDomainPack(formData.value)
      message.success(t('common.createSuccess'))
    } else {
      await LabDomainPackApi.updateDomainPack(formData.value)
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
  await LabDomainPackApi.deleteDomainPack(id)
  message.success(t('common.delSuccess'))
  await getList()
}

const resetForm = () => {
  formData.value = {
    domainId: undefined as unknown as number,
    packCode: '',
    packName: '',
    packVersion: '1.0',
    industry: '',
    applicationScope: '',
    workflowSchema: '',
    templateSchema: '',
    status: 'draft',
    remark: ''
  }
  formRef.value?.resetFields()
}

onMounted(async () => {
  await getDomainOptions()
  await getList()
})
</script>
