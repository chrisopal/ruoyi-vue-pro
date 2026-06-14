<template>
  <ContentWrap>
    <el-form ref="queryFormRef" :inline="true" :model="queryParams" class="-mb-15px" label-width="88px">
      <el-form-item label="模板编码" prop="templateCode">
        <el-input v-model="queryParams.templateCode" class="!w-200px" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="模板名称" prop="templateName">
        <el-input v-model="queryParams.templateName" class="!w-220px" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="模板类型" prop="templateType">
        <el-input v-model="queryParams.templateType" class="!w-160px" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="发布状态" prop="templateStatus">
        <el-select v-model="queryParams.templateStatus" class="!w-150px" clearable>
          <el-option
            v-for="item in templateStatusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon class="mr-5px" icon="ep:search" />搜索</el-button>
        <el-button @click="resetQuery"><Icon class="mr-5px" icon="ep:refresh" />重置</el-button>
        <el-button v-hasPermi="['lab:template:save']" plain type="primary" @click="openTemplateForm('create')">
          <Icon class="mr-5px" icon="ep:plus" />新增模板
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="templateList" row-key="id" @current-change="handleCurrentTemplate">
      <el-table-column align="center" label="模板编码" min-width="160" prop="templateCode" />
      <el-table-column align="center" label="模板名称" min-width="180" prop="templateName" />
      <el-table-column align="center" label="版本" width="90" prop="templateVersion" />
      <el-table-column align="center" label="类型" width="110" prop="templateType" />
      <el-table-column align="center" label="发布状态" width="110" prop="templateStatus">
        <template #default="scope">
          <el-tag :type="getTemplateStatusTag(scope.row.templateStatus)">
            {{ getTemplateStatusLabel(scope.row.templateStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="启用状态" width="100" prop="status" />
      <el-table-column align="center" fixed="right" label="操作" width="310">
        <template #default="scope">
          <el-button v-hasPermi="['lab:template:query']" link type="primary" @click="loadFields(scope.row.id)">字段</el-button>
          <el-button v-hasPermi="['lab:template:preview']" link type="primary" @click="openPreview(scope.row.id)">预览</el-button>
          <el-button v-hasPermi="['lab:template:save']" v-if="isDraft(scope.row)" link type="primary" @click="openTemplateForm('update', scope.row.id)">编辑</el-button>
          <el-button v-hasPermi="['lab:template:save']" v-if="isDraft(scope.row)" link type="success" @click="handlePublishTemplate(scope.row.id)">发布</el-button>
          <el-button v-hasPermi="['lab:template:save']" v-if="scope.row.templateStatus === 'published'" link type="warning" @click="handleArchiveTemplate(scope.row.id)">归档</el-button>
          <el-button v-hasPermi="['lab:template:save']" v-if="isDraft(scope.row)" link type="danger" @click="handleDeleteTemplate(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination v-model:limit="queryParams.pageSize" v-model:page="queryParams.pageNo" :total="total" @pagination="getTemplateList" />
  </ContentWrap>

  <ContentWrap v-if="currentTemplateId">
    <div class="mb-12px flex items-center justify-between">
      <span>字段绑定</span>
      <el-button v-hasPermi="['lab:template:save']" v-if="currentTemplateEditable" plain type="primary" @click="openFieldForm('create')">
        <Icon class="mr-5px" icon="ep:plus" />新增字段
      </el-button>
    </div>
    <el-table v-loading="fieldLoading" :data="fieldList" row-key="id">
      <el-table-column align="center" label="字段编码" min-width="140" prop="fieldCode" />
      <el-table-column align="center" label="字段名称" min-width="160" prop="fieldName" />
      <el-table-column align="center" label="来源类型" min-width="120" prop="sourceType" />
      <el-table-column align="center" label="来源路径" min-width="220" prop="sourcePath" show-overflow-tooltip />
      <el-table-column align="center" label="必填" width="80" prop="requiredFlag" />
      <el-table-column align="center" label="排序" width="80" prop="sort" />
      <el-table-column align="center" fixed="right" label="操作" width="140">
        <template #default="scope">
          <el-button v-hasPermi="['lab:template:save']" v-if="currentTemplateEditable" link type="primary" @click="openFieldForm('update', scope.row)">编辑</el-button>
          <el-button v-hasPermi="['lab:template:save']" v-if="currentTemplateEditable" link type="danger" @click="handleDeleteField(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </ContentWrap>

  <el-dialog v-model="templateFormVisible" :title="templateFormType === 'create' ? '新增模板版本' : '编辑模板版本'" width="720px">
    <el-form ref="templateFormRef" :model="templateFormData" :rules="templateRules" label-width="112px">
      <el-row :gutter="16">
        <el-col :span="12"><el-form-item label="方案包编号" prop="domainPackId"><el-input-number v-model="templateFormData.domainPackId" class="w-1/1" :min="1" /></el-form-item></el-col>
        <el-col :span="12">
          <el-form-item label="发布状态" prop="templateStatus">
            <el-select v-model="templateFormData.templateStatus" class="w-1/1">
              <el-option v-for="item in templateStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12"><el-form-item label="模板编码" prop="templateCode"><el-input v-model="templateFormData.templateCode" /></el-form-item></el-col>
        <el-col :span="12"><el-form-item label="模板版本" prop="templateVersion"><el-input v-model="templateFormData.templateVersion" /></el-form-item></el-col>
      </el-row>
      <el-form-item label="模板名称" prop="templateName"><el-input v-model="templateFormData.templateName" /></el-form-item>
      <el-form-item label="模板类型" prop="templateType"><el-input v-model="templateFormData.templateType" /></el-form-item>
      <el-form-item label="启用状态" prop="status"><el-input v-model="templateFormData.status" /></el-form-item>
      <el-form-item label="章节配置" prop="sectionSchema"><el-input v-model="templateFormData.sectionSchema" :autosize="{ minRows: 4, maxRows: 8 }" type="textarea" /></el-form-item>
      <el-form-item label="输出格式" prop="outputFormats"><el-input v-model="templateFormData.outputFormats" :autosize="{ minRows: 2, maxRows: 4 }" type="textarea" /></el-form-item>
      <el-form-item label="数据来源" prop="dataSourceSchema"><el-input v-model="templateFormData.dataSourceSchema" :autosize="{ minRows: 3, maxRows: 6 }" type="textarea" /></el-form-item>
      <el-form-item label="预览配置" prop="previewSchema"><el-input v-model="templateFormData.previewSchema" :autosize="{ minRows: 4, maxRows: 8 }" type="textarea" /></el-form-item>
    </el-form>
    <template #footer>
      <el-button type="primary" @click="submitTemplateForm">确 定</el-button>
      <el-button @click="templateFormVisible = false">取 消</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="fieldFormVisible" :title="fieldFormType === 'create' ? '新增字段绑定' : '编辑字段绑定'" width="640px">
    <el-form ref="fieldFormRef" :model="fieldFormData" :rules="fieldRules" label-width="96px">
      <el-form-item label="字段编码" prop="fieldCode"><el-input v-model="fieldFormData.fieldCode" /></el-form-item>
      <el-form-item label="字段名称" prop="fieldName"><el-input v-model="fieldFormData.fieldName" /></el-form-item>
      <el-form-item label="来源类型" prop="sourceType"><el-input v-model="fieldFormData.sourceType" /></el-form-item>
      <el-form-item label="来源路径" prop="sourcePath"><el-input v-model="fieldFormData.sourcePath" /></el-form-item>
      <el-form-item label="必填" prop="requiredFlag"><el-switch v-model="fieldFormData.requiredFlag" /></el-form-item>
      <el-form-item label="排序" prop="sort"><el-input-number v-model="fieldFormData.sort" class="w-1/1" :min="0" /></el-form-item>
    </el-form>
    <template #footer>
      <el-button type="primary" @click="submitFieldForm">确 定</el-button>
      <el-button @click="fieldFormVisible = false">取 消</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="previewVisible" title="报告预览配置" width="760px">
    <pre class="whitespace-pre-wrap">{{ previewText }}</pre>
  </el-dialog>
</template>

<script lang="ts" setup>
import { LabTemplateApi, LabTemplateFieldBindingVO, LabTemplateVersionVO } from '@/api/lab/template'

defineOptions({ name: 'LabTemplate' })

const message = useMessage()
const { t } = useI18n()
const loading = ref(true)
const templateList = ref<LabTemplateVersionVO[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  templateCode: undefined,
  templateName: undefined,
  templateType: undefined,
  templateStatus: undefined
})
const queryFormRef = ref()
const currentTemplateId = ref<number>()
const currentTemplate = ref<LabTemplateVersionVO>()

type TagType = 'primary' | 'success' | 'warning' | 'danger' | 'info'

const templateStatusOptions: Array<{ label: string; value: string; tag: TagType }> = [
  { label: '草稿', value: 'draft', tag: 'info' },
  { label: '已发布', value: 'published', tag: 'success' },
  { label: '已归档', value: 'archived', tag: 'warning' }
]

const getTemplateStatusLabel = (status?: string) => {
  return templateStatusOptions.find((item) => item.value === status)?.label || status || '草稿'
}

const getTemplateStatusTag = (status?: string) => {
  return templateStatusOptions.find((item) => item.value === status)?.tag || 'info'
}

const isDraft = (row?: LabTemplateVersionVO) => !row?.templateStatus || row.templateStatus === 'draft'
const currentTemplateEditable = computed(() => isDraft(currentTemplate.value))

const getTemplateList = async () => {
  loading.value = true
  try {
    const data = await LabTemplateApi.getTemplateVersionPage(queryParams)
    templateList.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}
const handleQuery = () => { queryParams.pageNo = 1; getTemplateList() }
const resetQuery = () => { queryFormRef.value.resetFields(); handleQuery() }

const templateFormVisible = ref(false)
const templateFormType = ref('')
const emptyTemplateForm = (): LabTemplateVersionVO => ({
  domainPackId: 1,
  templateCode: '',
  templateName: '',
  templateVersion: '1.0',
  templateType: 'report',
  templateStatus: 'draft',
  sectionSchema: '[{"sectionCode":"resultTable","sectionName":"检测结果","sourceType":"result_values","visible":true,"sort":10}]',
  outputFormats: '["WORD","PDF","EXCEL"]',
  dataSourceSchema: '{"report":"$.report","sample":"$.sample","resultValues":"$.resultValues","evidenceObjects":"$.equipmentEvidenceSnapshots"}',
  previewSchema: '{"layout":"basic-report-preview"}',
  status: 'active'
})
const templateFormData = ref<LabTemplateVersionVO>(emptyTemplateForm())
const templateRules = reactive({
  domainPackId: [{ required: true, message: '方案包不能为空', trigger: 'blur' }],
  templateCode: [{ required: true, message: '模板编码不能为空', trigger: 'blur' }],
  templateName: [{ required: true, message: '模板名称不能为空', trigger: 'blur' }],
  templateVersion: [{ required: true, message: '模板版本不能为空', trigger: 'blur' }],
  templateType: [{ required: true, message: '模板类型不能为空', trigger: 'blur' }],
  templateStatus: [{ required: true, message: '发布状态不能为空', trigger: 'change' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'blur' }]
})
const templateFormRef = ref()

const openTemplateForm = async (type: string, id?: number) => {
  templateFormType.value = type
  templateFormVisible.value = true
  templateFormData.value = emptyTemplateForm()
  if (id) templateFormData.value = await LabTemplateApi.getTemplateVersion(id)
}
const submitTemplateForm = async () => {
  await templateFormRef.value.validate()
  if (templateFormType.value === 'create') {
    await LabTemplateApi.createTemplateVersion(templateFormData.value)
    message.success(t('common.createSuccess'))
  } else {
    await LabTemplateApi.updateTemplateVersion(templateFormData.value)
    message.success(t('common.updateSuccess'))
  }
  templateFormVisible.value = false
  await getTemplateList()
}
const handleDeleteTemplate = async (id: number) => {
  await message.delConfirm()
  await LabTemplateApi.deleteTemplateVersion(id)
  message.success(t('common.delSuccess'))
  await getTemplateList()
}
const handlePublishTemplate = async (id: number) => {
  await message.confirm('发布后当前模板版本不能原地修改，报告生成将固定引用该版本结构。')
  await LabTemplateApi.publishTemplateVersion(id)
  message.success('模板版本已发布')
  await getTemplateList()
}
const handleArchiveTemplate = async (id: number) => {
  await message.confirm('归档后该模板版本不再用于新的报告配置。')
  await LabTemplateApi.archiveTemplateVersion(id)
  message.success('模板版本已归档')
  await getTemplateList()
}

const fieldLoading = ref(false)
const fieldList = ref<LabTemplateFieldBindingVO[]>([])
const loadFields = async (templateId: number) => {
  currentTemplateId.value = templateId
  currentTemplate.value = templateList.value.find((item) => item.id === templateId)
  fieldLoading.value = true
  try {
    const data = await LabTemplateApi.getFieldBindingPage({ pageNo: 1, pageSize: 100, templateId })
    fieldList.value = data.list
  } finally {
    fieldLoading.value = false
  }
}
const handleCurrentTemplate = (row?: LabTemplateVersionVO) => {
  if (row?.id) loadFields(row.id)
}

const fieldFormVisible = ref(false)
const fieldFormType = ref('')
const fieldFormData = ref<LabTemplateFieldBindingVO>({ templateId: 0, fieldCode: '', fieldName: '', sourceType: '', sourcePath: '', requiredFlag: false, sort: 0 })
const fieldRules = reactive({
  fieldCode: [{ required: true, message: '字段编码不能为空', trigger: 'blur' }],
  fieldName: [{ required: true, message: '字段名称不能为空', trigger: 'blur' }],
  sourceType: [{ required: true, message: '来源类型不能为空', trigger: 'blur' }],
  sourcePath: [{ required: true, message: '来源路径不能为空', trigger: 'blur' }]
})
const fieldFormRef = ref()

const openFieldForm = (type: string, row?: LabTemplateFieldBindingVO) => {
  fieldFormType.value = type
  fieldFormVisible.value = true
  fieldFormData.value = row ? { ...row } : { templateId: currentTemplateId.value!, fieldCode: '', fieldName: '', sourceType: '', sourcePath: '', requiredFlag: false, sort: 0 }
}
const submitFieldForm = async () => {
  await fieldFormRef.value.validate()
  if (fieldFormType.value === 'create') {
    await LabTemplateApi.createFieldBinding(fieldFormData.value)
    message.success(t('common.createSuccess'))
  } else {
    await LabTemplateApi.updateFieldBinding(fieldFormData.value)
    message.success(t('common.updateSuccess'))
  }
  fieldFormVisible.value = false
  await loadFields(currentTemplateId.value!)
}
const handleDeleteField = async (id: number) => {
  await message.delConfirm()
  await LabTemplateApi.deleteFieldBinding(id)
  message.success(t('common.delSuccess'))
  await loadFields(currentTemplateId.value!)
}

const previewVisible = ref(false)
const previewText = ref('')
const openPreview = async (templateId: number) => {
  const data = await LabTemplateApi.getTemplatePreview(templateId)
  previewText.value = JSON.stringify(data, null, 2)
  previewVisible.value = true
}

onMounted(() => getTemplateList())
</script>
