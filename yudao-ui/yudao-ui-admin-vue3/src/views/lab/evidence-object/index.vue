<template>
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="88px"
    >
      <el-form-item label="证据编码" prop="evidenceCode">
        <el-input
          v-model="queryParams.evidenceCode"
          class="!w-190px"
          clearable
          placeholder="请输入证据编码"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="证据名称" prop="evidenceName">
        <el-input
          v-model="queryParams.evidenceName"
          class="!w-220px"
          clearable
          placeholder="请输入证据名称"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="证据类型" prop="evidenceType">
        <el-select v-model="queryParams.evidenceType" class="!w-190px" clearable placeholder="请选择">
          <el-option
            v-for="item in evidenceTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="业务域" prop="businessDomain">
        <el-input
          v-model="queryParams.businessDomain"
          class="!w-150px"
          clearable
          placeholder="如 equipment"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" class="!w-140px" clearable placeholder="请选择">
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
          v-hasPermi="['lab:evidence-object:create']"
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
      <el-table-column align="center" label="证据编码" min-width="170" prop="evidenceCode" />
      <el-table-column align="center" label="证据名称" min-width="190" prop="evidenceName" show-overflow-tooltip />
      <el-table-column align="center" label="证据类型" min-width="170" prop="evidenceType" />
      <el-table-column align="center" label="业务域" min-width="110" prop="businessDomain" />
      <el-table-column align="center" label="来源对象" min-width="180" prop="sourceObject" show-overflow-tooltip />
      <el-table-column align="center" label="来源单号" min-width="150" prop="sourceObjectNo" show-overflow-tooltip />
      <el-table-column align="center" label="有效期至" min-width="120" prop="validTo" />
      <el-table-column align="center" label="状态" min-width="100" prop="status">
        <template #default="scope">
          <el-tag :type="getStatusTag(scope.row.status)">
            {{ getStatusLabel(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="证据文件" min-width="110">
        <template #default="scope">
          <el-link v-if="scope.row.fileUrl" :href="scope.row.fileUrl" target="_blank" type="primary">
            查看
          </el-link>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="证据哈希" min-width="180" prop="evidenceHash" show-overflow-tooltip />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="创建时间"
        prop="createTime"
        width="180"
      />
      <el-table-column align="center" fixed="right" label="操作" width="150">
        <template #default="scope">
          <el-button
            v-hasPermi="['lab:evidence-object:update']"
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >
            编辑
          </el-button>
          <el-button
            v-hasPermi="['lab:evidence-object:delete']"
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

  <el-dialog v-model="formVisible" :title="formTitle" width="860px">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="112px">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="证据编码" prop="evidenceCode">
            <el-input v-model="formData.evidenceCode" maxlength="64" placeholder="如 EQ-CERT-100-1" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="证据名称" prop="evidenceName">
            <el-input v-model="formData.evidenceName" maxlength="256" placeholder="请输入证据名称" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="证据类型" prop="evidenceType">
            <el-select v-model="formData.evidenceType" class="w-1/1" filterable placeholder="请选择">
              <el-option
                v-for="item in evidenceTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="业务域" prop="businessDomain">
            <el-input v-model="formData.businessDomain" maxlength="64" placeholder="如 equipment、report" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="来源对象" prop="sourceObject">
            <el-input v-model="formData.sourceObject" maxlength="128" placeholder="如 lab_equipment_traceability" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="来源单号" prop="sourceObjectNo">
            <el-input v-model="formData.sourceObjectNo" maxlength="128" placeholder="如 CERT-001" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="来源编号" prop="sourceObjectId">
            <el-input-number v-model="formData.sourceObjectId" :min="1" class="w-1/1" controls-position="right" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-select v-model="formData.status" class="w-1/1" placeholder="请选择">
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
          <el-form-item label="签发机构" prop="issuedBy">
            <el-input v-model="formData.issuedBy" maxlength="128" placeholder="请输入签发机构" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="签发日期" prop="issuedAt">
            <el-date-picker v-model="formData.issuedAt" class="w-1/1" type="date" value-format="YYYY-MM-DD" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="有效期开始" prop="validFrom">
            <el-date-picker v-model="formData.validFrom" class="w-1/1" type="date" value-format="YYYY-MM-DD" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="有效期结束" prop="validTo">
            <el-date-picker v-model="formData.validTo" class="w-1/1" type="date" value-format="YYYY-MM-DD" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="文件地址" prop="fileUrl">
            <el-input v-model="formData.fileUrl" maxlength="512" placeholder="请输入文件地址" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="文件格式" prop="fileFormat">
            <el-input v-model="formData.fileFormat" maxlength="32" placeholder="如 pdf、docx" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="证据哈希" prop="evidenceHash">
        <el-input v-model="formData.evidenceHash" maxlength="128" placeholder="为空时服务端自动生成 SHA-256" />
      </el-form-item>
      <el-form-item label="摘要" prop="summary">
        <el-input v-model="formData.summary" maxlength="1024" show-word-limit type="textarea" />
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
import { LabEvidenceObjectApi, LabEvidenceObjectVO } from '@/api/lab/evidence-object'

defineOptions({ name: 'LabEvidenceObject' })

const message = useMessage()
const { t } = useI18n()

const evidenceTypeOptions = [
  { label: '检测报告', value: 'REPORT' },
  { label: '原始数据', value: 'RAW_DATA' },
  { label: '设备校准证书', value: 'EQUIPMENT_CERTIFICATE' },
  { label: '人员授权', value: 'PERSON_AUTH' },
  { label: '环境记录', value: 'ENVIRONMENT_RECORD' },
  { label: '方法验证', value: 'METHOD_VALIDATION' },
  { label: '审核记录', value: 'AUDIT_RECORD' }
]
const statusOptions = [
  { label: '有效', value: 'effective' },
  { label: '草稿', value: 'draft' },
  { label: '过期', value: 'expired' },
  { label: '撤销', value: 'revoked' }
]

const loading = ref(true)
const list = ref<LabEvidenceObjectVO[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  evidenceCode: undefined,
  evidenceName: undefined,
  evidenceType: undefined,
  businessDomain: undefined,
  status: undefined
})
const queryFormRef = ref()

const getList = async () => {
  loading.value = true
  try {
    const data = await LabEvidenceObjectApi.getEvidenceObjectPage(queryParams)
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
const formTitle = computed(() => (formType.value === 'create' ? '新增证据对象' : '编辑证据对象'))
const formData = ref<LabEvidenceObjectVO>(emptyForm())
const formRules = reactive({
  evidenceCode: [{ required: true, message: '证据编码不能为空', trigger: 'blur' }],
  evidenceName: [{ required: true, message: '证据名称不能为空', trigger: 'blur' }],
  evidenceType: [{ required: true, message: '证据类型不能为空', trigger: 'change' }],
  sourceObject: [{ required: true, message: '来源对象不能为空', trigger: 'blur' }],
  businessDomain: [{ required: true, message: '业务域不能为空', trigger: 'blur' }]
})
const formRef = ref()

function emptyForm(): LabEvidenceObjectVO {
  return {
    evidenceCode: '',
    evidenceName: '',
    evidenceType: 'EQUIPMENT_CERTIFICATE',
    sourceObject: 'lab_equipment_traceability',
    businessDomain: 'equipment',
    status: 'effective'
  }
}

const openForm = async (type: string, id?: number) => {
  formType.value = type
  formVisible.value = true
  formData.value = emptyForm()
  if (id) {
    formLoading.value = true
    try {
      formData.value = await LabEvidenceObjectApi.getEvidenceObject(id)
    } finally {
      formLoading.value = false
    }
  }
}
const submitForm = async () => {
  await formRef.value.validate()
  formLoading.value = true
  try {
    if (formType.value === 'create') {
      await LabEvidenceObjectApi.createEvidenceObject(formData.value)
      message.success(t('common.createSuccess'))
    } else {
      await LabEvidenceObjectApi.updateEvidenceObject(formData.value)
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
  await LabEvidenceObjectApi.deleteEvidenceObject(id)
  message.success(t('common.delSuccess'))
  await getList()
}

const getStatusLabel = (status?: string) =>
  statusOptions.find((item) => item.value === status)?.label || status || '-'
const getStatusTag = (status?: string) => {
  if (status === 'effective') return 'success'
  if (status === 'expired') return 'warning'
  if (status === 'revoked') return 'danger'
  return 'info'
}

onMounted(() => getList())
</script>
