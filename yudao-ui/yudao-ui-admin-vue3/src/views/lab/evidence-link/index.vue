<template>
  <ContentWrap>
    <el-form ref="queryFormRef" :inline="true" :model="queryParams" class="-mb-15px" label-width="88px">
      <el-form-item label="证据编码" prop="evidenceCode">
        <el-input v-model="queryParams.evidenceCode" class="!w-180px" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="来源单号" prop="sourceObjectNo">
        <el-input v-model="queryParams.sourceObjectNo" class="!w-220px" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="业务单号" prop="linkedBizNo">
        <el-input v-model="queryParams.linkedBizNo" class="!w-220px" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon class="mr-5px" icon="ep:search" />搜索</el-button>
        <el-button @click="resetQuery"><Icon class="mr-5px" icon="ep:refresh" />重置</el-button>
        <el-button v-hasPermi="['lab:evidence-link:create']" plain type="primary" @click="openForm('create')">
          <Icon class="mr-5px" icon="ep:plus" />新增
        </el-button>
        <el-button v-hasPermi="['lab:evidence-link:export']" plain type="success" @click="handleExport">
          <Icon class="mr-5px" icon="ep:download" />导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" row-key="id">
      <el-table-column align="center" label="证据编码" min-width="140" prop="evidenceCode" />
      <el-table-column align="center" label="证据名称" min-width="180" prop="evidenceName" show-overflow-tooltip />
      <el-table-column align="center" label="证据哈希" min-width="180" prop="evidenceHash" show-overflow-tooltip />
      <el-table-column align="center" label="来源对象" min-width="180" prop="sourceObject" />
      <el-table-column align="center" label="来源单号" min-width="180" prop="sourceObjectNo" />
      <el-table-column align="center" label="关联业务" min-width="160" prop="linkedBizType" />
      <el-table-column align="center" label="业务单号" min-width="180" prop="linkedBizNo" />
      <el-table-column align="center" label="条款分类" min-width="120" prop="clauseCategory" />
      <el-table-column align="center" fixed="right" label="操作" width="150">
        <template #default="scope">
          <el-button v-hasPermi="['lab:evidence-link:update']" link type="primary" @click="openForm('update', scope.row.id)">编辑</el-button>
          <el-button v-hasPermi="['lab:evidence-link:delete']" link type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination v-model:limit="queryParams.pageSize" v-model:page="queryParams.pageNo" :total="total" @pagination="getList" />
  </ContentWrap>

  <el-dialog v-model="formVisible" :title="formType === 'create' ? '新增证据关联' : '编辑证据关联'" width="680px">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="112px">
      <el-row :gutter="16">
        <el-col :span="12"><el-form-item label="证据编码" prop="evidenceCode"><el-input v-model="formData.evidenceCode" /></el-form-item></el-col>
        <el-col :span="12"><el-form-item label="条款分类" prop="clauseCategory"><el-input v-model="formData.clauseCategory" /></el-form-item></el-col>
      </el-row>
      <el-form-item label="证据名称" prop="evidenceName"><el-input v-model="formData.evidenceName" /></el-form-item>
      <el-form-item label="证据地址" prop="evidenceUrl"><el-input v-model="formData.evidenceUrl" /></el-form-item>
      <el-form-item label="证据哈希" prop="evidenceHash"><el-input v-model="formData.evidenceHash" placeholder="为空时服务端自动生成 SHA-256" /></el-form-item>
      <el-form-item label="来源对象" prop="sourceObject"><el-input v-model="formData.sourceObject" /></el-form-item>
      <el-form-item label="来源单号" prop="sourceObjectNo"><el-input v-model="formData.sourceObjectNo" /></el-form-item>
      <el-form-item label="关联业务类型" prop="linkedBizType"><el-input v-model="formData.linkedBizType" /></el-form-item>
      <el-form-item label="关联业务单号" prop="linkedBizNo"><el-input v-model="formData.linkedBizNo" /></el-form-item>
      <el-form-item label="关联状态" prop="linkStatus"><el-input v-model="formData.linkStatus" /></el-form-item>
      <el-form-item label="备注" prop="remark"><el-input v-model="formData.remark" type="textarea" /></el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="formVisible = false">取 消</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { LabEvidenceLinkApi, LabEvidenceLinkVO } from '@/api/lab/evidence-link'

defineOptions({ name: 'LabEvidenceLink' })

const message = useMessage()
const { t } = useI18n()
const loading = ref(true)
const list = ref<LabEvidenceLinkVO[]>([])
const total = ref(0)
const queryParams = reactive({ pageNo: 1, pageSize: 10, evidenceCode: undefined, sourceObjectNo: undefined, linkedBizNo: undefined })
const queryFormRef = ref()

const getList = async () => {
  loading.value = true
  try {
    const data = await LabEvidenceLinkApi.getEvidenceLinkPage(queryParams)
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
const formData = ref<LabEvidenceLinkVO>({ evidenceCode: '', sourceObject: '', linkedBizType: 'review_package', clauseCategory: '', linkStatus: 'linked' })
const formRules = reactive({
  evidenceCode: [{ required: true, message: '证据编码不能为空', trigger: 'blur' }],
  sourceObject: [{ required: true, message: '来源对象不能为空', trigger: 'blur' }],
  linkedBizType: [{ required: true, message: '关联业务类型不能为空', trigger: 'blur' }],
  clauseCategory: [{ required: true, message: '条款分类不能为空', trigger: 'blur' }],
  linkStatus: [{ required: true, message: '关联状态不能为空', trigger: 'blur' }]
})
const formRef = ref()

const openForm = async (type: string, id?: number) => {
  formType.value = type
  formVisible.value = true
  formData.value = { evidenceCode: '', sourceObject: '', linkedBizType: 'review_package', clauseCategory: '', linkStatus: 'linked' }
  if (id) {
    formLoading.value = true
    try { formData.value = await LabEvidenceLinkApi.getEvidenceLink(id) } finally { formLoading.value = false }
  }
}
const submitForm = async () => {
  await formRef.value.validate()
  formLoading.value = true
  try {
    if (formType.value === 'create') {
      await LabEvidenceLinkApi.createEvidenceLink(formData.value)
      message.success(t('common.createSuccess'))
    } else {
      await LabEvidenceLinkApi.updateEvidenceLink(formData.value)
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
  await LabEvidenceLinkApi.deleteEvidenceLink(id)
  message.success(t('common.delSuccess'))
  await getList()
}
const handleExport = async () => {
  await LabEvidenceLinkApi.exportEvidenceLink(queryParams)
  message.success('导出任务已生成')
}

onMounted(() => getList())
</script>
