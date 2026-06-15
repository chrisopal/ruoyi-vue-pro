<template>
  <ContentWrap>
    <div class="template-hero">
      <div class="template-heading">
        <div class="template-kicker">Report Template Designer</div>
        <div class="template-title">报告模板设计器</div>
        <div class="template-subtitle">
          设计 Word / PDF / Excel 报告模板，维护章节结构、数据来源、字段绑定和发布版本，让报告生成不再只是文件导出。
        </div>
        <div class="template-tags">
          <el-tag effect="plain">Word .docx</el-tag>
          <el-tag effect="plain" type="danger">PDF .pdf</el-tag>
          <el-tag effect="plain" type="success">Excel .xlsx</el-tag>
        </div>
      </div>
      <el-button v-hasPermi="['lab:template:save']" type="primary" @click="openTemplateForm('create')">
        <Icon class="mr-5px" icon="ep:plus" />新建设计
      </el-button>
    </div>

    <div class="template-overview-grid">
      <div class="template-overview-card">
        <span>模板版本</span>
        <strong>{{ templateOverview.total }}</strong>
        <small>草稿、发布和归档版本</small>
      </div>
      <div class="template-overview-card">
        <span>已发布</span>
        <strong>{{ templateOverview.published }}</strong>
        <small>可被报告生成引用</small>
      </div>
      <div class="template-overview-card">
        <span>输出通道</span>
        <strong>{{ templateOverview.formats.length }}</strong>
        <small>{{ templateOverview.formats.join(' / ') || '待配置' }}</small>
      </div>
      <div class="template-overview-card">
        <span>当前字段绑定</span>
        <strong>{{ fieldList.length }}</strong>
        <small>{{ currentTemplate?.templateName || '选择模板后查看' }}</small>
      </div>
    </div>
  </ContentWrap>

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
      <el-table-column align="center" label="输出格式" min-width="170">
        <template #default="scope">
          <div class="format-chip-list">
            <el-tag v-for="format in parseOutputFormats(scope.row.outputFormats)" :key="format" effect="plain" size="small">
              {{ format }}
            </el-tag>
          </div>
        </template>
      </el-table-column>
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
    <div class="designer-workbench-header">
      <div>
        <div class="workbench-title">{{ currentTemplate?.templateName || '模板设计工作台' }}</div>
        <div class="workbench-subtitle">
          {{ currentTemplate?.templateCode }} · v{{ currentTemplate?.templateVersion }} ·
          {{ getTemplateStatusLabel(currentTemplate?.templateStatus) }}
        </div>
      </div>
      <div class="workbench-actions">
        <el-button v-hasPermi="['lab:template:preview']" plain type="primary" @click="openPreview(currentTemplateId)">
          <Icon class="mr-5px" icon="ep:view" />结构预览
        </el-button>
        <el-button v-hasPermi="['lab:template:save']" v-if="currentTemplateEditable" plain type="primary" @click="openFieldForm('create')">
          <Icon class="mr-5px" icon="ep:plus" />新增字段
        </el-button>
      </div>
    </div>

    <el-tabs v-model="activeOutputFormat" class="output-tabs">
      <el-tab-pane
        v-for="format in currentOutputFormats"
        :key="format"
        :label="outputFormatLabel(format)"
        :name="format"
      />
    </el-tabs>

    <el-row :gutter="16">
      <el-col :lg="14" :xs="24">
        <div class="report-canvas">
          <div class="canvas-toolbar">
            <div>
              <div class="canvas-title">{{ outputFormatLabel(activeOutputFormat) }} 版式预览</div>
              <div class="canvas-subtitle">{{ outputFormatHint(activeOutputFormat) }}</div>
            </div>
            <el-tag :type="currentTemplateEditable ? 'warning' : 'success'" effect="light">
              {{ currentTemplateEditable ? '草稿可编辑' : '版本已冻结' }}
            </el-tag>
          </div>
          <div class="paper-preview" :class="`paper-preview--${activeOutputFormat.toLowerCase()}`">
            <div class="paper-header">
              <div>
                <strong>{{ currentTemplate?.templateName || '检测报告' }}</strong>
                <span>{{ currentTemplate?.templateCode }} / v{{ currentTemplate?.templateVersion }}</span>
              </div>
              <el-tag effect="plain" size="small">{{ activeOutputFormat }}</el-tag>
            </div>
            <div v-for="section in currentSections" :key="section.uid" class="paper-section">
              <div class="paper-section-title">
                <span>{{ section.sort }}</span>
                {{ section.sectionName || section.sectionCode }}
              </div>
              <div class="paper-section-body">
                来源：{{ section.sourceType || '-' }} ·
                字段：{{ fieldsForSource(section.sourceType).length || '待绑定' }}
              </div>
            </div>
          </div>
        </div>
      </el-col>

      <el-col :lg="10" :xs="24">
        <div class="binding-panel">
          <div class="panel-title">数据来源</div>
          <div v-for="source in currentDataSources" :key="source.uid" class="source-row">
            <span>{{ source.sourceCode }}</span>
            <strong>{{ source.sourcePath }}</strong>
          </div>
          <el-empty v-if="!currentDataSources.length" description="暂无数据来源" :image-size="64" />
        </div>

        <div class="binding-panel mt-16px">
          <div class="panel-title">字段绑定</div>
          <el-table v-loading="fieldLoading" :data="fieldList" height="258" row-key="id">
            <el-table-column label="字段" min-width="130">
              <template #default="scope">
                <div class="font-600">{{ scope.row.fieldName }}</div>
                <div class="table-subtext">{{ scope.row.fieldCode }}</div>
              </template>
            </el-table-column>
            <el-table-column label="来源路径" min-width="180" prop="sourcePath" show-overflow-tooltip />
            <el-table-column align="center" label="操作" width="96">
              <template #default="scope">
                <el-button v-hasPermi="['lab:template:save']" v-if="currentTemplateEditable" link type="primary" @click="openFieldForm('update', scope.row)">编辑</el-button>
                <el-button v-hasPermi="['lab:template:save']" v-if="currentTemplateEditable" link type="danger" @click="handleDeleteField(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>
    </el-row>
  </ContentWrap>

  <el-dialog v-model="templateFormVisible" :title="templateFormType === 'create' ? '新增模板版本' : '编辑模板版本'" width="1040px">
    <el-form ref="templateFormRef" :model="templateFormData" :rules="templateRules" label-width="112px">
      <el-row :gutter="16">
        <el-col :span="12"><el-form-item label="方案包编号" prop="domainPackId"><el-input-number v-model="templateFormData.domainPackId" class="w-1/1" :min="1" /></el-form-item></el-col>
        <el-col :span="12">
          <el-form-item label="发布状态">
            <el-tag :type="getTemplateStatusTag(templateFormData.templateStatus)">
              {{ getTemplateStatusLabel(templateFormData.templateStatus) }}
            </el-tag>
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
      <el-form-item label="输出格式" prop="outputFormats">
        <el-checkbox-group v-model="selectedOutputFormats">
          <el-checkbox-button
            v-for="item in outputFormatOptions"
            :key="item.value"
            :value="item.value"
          >
            {{ item.label }}
          </el-checkbox-button>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item label="报告章节" prop="sectionSchema">
        <div class="w-1/1">
          <div class="mb-8px flex items-center justify-between">
            <span class="text-[var(--el-text-color-secondary)] text-12px">配置报告章节顺序、来源和显隐，发布后随模板版本冻结。</span>
            <el-button plain type="primary" @click="addSection">
              <Icon class="mr-5px" icon="ep:plus" />新增章节
            </el-button>
          </div>
          <el-table :data="sectionRows" border size="small" row-key="uid">
            <el-table-column label="编码" min-width="150">
              <template #default="{ row }"><el-input v-model="row.sectionCode" placeholder="resultTable" /></template>
            </el-table-column>
            <el-table-column label="名称" min-width="150">
              <template #default="{ row }"><el-input v-model="row.sectionName" placeholder="检测结果" /></template>
            </el-table-column>
            <el-table-column label="来源" min-width="170">
              <template #default="{ row }"><el-input v-model="row.sourceType" placeholder="result_values" /></template>
            </el-table-column>
            <el-table-column align="center" label="显示" width="80">
              <template #default="{ row }"><el-switch v-model="row.visible" /></template>
            </el-table-column>
            <el-table-column label="排序" width="110">
              <template #default="{ row }"><el-input-number v-model="row.sort" :min="0" controls-position="right" class="!w-full" /></template>
            </el-table-column>
            <el-table-column align="center" label="操作" width="90">
              <template #default="{ $index }">
                <el-button link type="danger" @click="removeSection($index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-form-item>
      <el-form-item label="数据来源" prop="dataSourceSchema">
        <div class="w-1/1">
          <div class="mb-8px flex items-center justify-between">
            <span class="text-[var(--el-text-color-secondary)] text-12px">声明报告取数路径，供预览、字段绑定和后续渲染器共用。</span>
            <el-button plain type="primary" @click="addDataSource">
              <Icon class="mr-5px" icon="ep:plus" />新增来源
            </el-button>
          </div>
          <el-table :data="dataSourceRows" border size="small" row-key="uid">
            <el-table-column label="来源编码" min-width="160">
              <template #default="{ row }"><el-input v-model="row.sourceCode" placeholder="resultValues" /></template>
            </el-table-column>
            <el-table-column label="数据路径" min-width="260">
              <template #default="{ row }"><el-input v-model="row.sourcePath" placeholder="$.resultValues" /></template>
            </el-table-column>
            <el-table-column align="center" label="操作" width="90">
              <template #default="{ $index }">
                <el-button link type="danger" @click="removeDataSource($index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-form-item>
      <el-form-item label="预览配置" prop="previewSchema"><el-input v-model="templateFormData.previewSchema" :autosize="{ minRows: 3, maxRows: 6 }" type="textarea" /></el-form-item>
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

  <el-dialog v-model="previewVisible" title="报告模板结构预览" width="960px">
    <el-tabs model-value="sections">
      <el-tab-pane label="章节" name="sections">
        <el-table :data="previewSections" row-key="uid">
          <el-table-column label="排序" width="90" prop="sort" />
          <el-table-column label="章节编码" min-width="160" prop="sectionCode" />
          <el-table-column label="章节名称" min-width="180" prop="sectionName" />
          <el-table-column label="来源" min-width="160" prop="sourceType" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="字段绑定" name="fields">
        <el-table :data="previewData?.fields || []" row-key="id">
          <el-table-column label="字段编码" min-width="150" prop="fieldCode" />
          <el-table-column label="字段名称" min-width="160" prop="fieldName" />
          <el-table-column label="来源路径" min-width="220" prop="sourcePath" show-overflow-tooltip />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="Schema" name="schema">
        <pre class="schema-preview">{{ previewText }}</pre>
      </el-tab-pane>
    </el-tabs>
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
type ReportSectionRow = {
  uid: number
  sectionCode: string
  sectionName: string
  sourceType: string
  visible: boolean
  sort: number
}
type ReportDataSourceRow = {
  uid: number
  sourceCode: string
  sourcePath: string
}

const templateStatusOptions: Array<{ label: string; value: string; tag: TagType }> = [
  { label: '草稿', value: 'draft', tag: 'info' },
  { label: '已发布', value: 'published', tag: 'success' },
  { label: '已归档', value: 'archived', tag: 'warning' }
]
const outputFormatOptions = [
  { label: 'Word (.docx)', value: 'WORD' },
  { label: 'PDF (.pdf)', value: 'PDF' },
  { label: 'Excel (.xlsx)', value: 'EXCEL' }
]

const getTemplateStatusLabel = (status?: string) => {
  return templateStatusOptions.find((item) => item.value === status)?.label || status || '草稿'
}

const getTemplateStatusTag = (status?: string) => {
  return templateStatusOptions.find((item) => item.value === status)?.tag || 'info'
}

const isDraft = (row?: LabTemplateVersionVO) => !row?.templateStatus || row.templateStatus === 'draft'
const currentTemplateEditable = computed(() => isDraft(currentTemplate.value))
const activeOutputFormat = ref('WORD')

const outputFormatLabel = (format: string) => {
  if (format === 'WORD') return 'Word'
  if (format === 'PDF') return 'PDF'
  if (format === 'EXCEL') return 'Excel'
  return format
}

const outputFormatHint = (format: string) => {
  if (format === 'WORD') return '强调正式版式、页眉页脚、签字盖章和可编辑交付件'
  if (format === 'PDF') return '强调不可篡改交付、签发水印、页码和归档一致性'
  if (format === 'EXCEL') return '强调结果明细、原始数据、批量导出和复核分析'
  return '按模板章节和字段绑定生成输出'
}

const fieldsForSource = (sourceType?: string) => {
  if (!sourceType) return []
  return fieldList.value.filter((field) => field.sourceType === sourceType || field.sourcePath?.includes(sourceType))
}

const getTemplateList = async () => {
  loading.value = true
  try {
    const data = await LabTemplateApi.getTemplateVersionPage(queryParams)
    templateList.value = data.list
    total.value = data.total
    if (currentTemplateId.value) {
      currentTemplate.value = templateList.value.find((item) => item.id === currentTemplateId.value) || currentTemplate.value
    } else if (templateList.value[0]?.id) {
      await loadFields(templateList.value[0].id!)
    }
  } finally {
    loading.value = false
  }
}
const handleQuery = () => { queryParams.pageNo = 1; getTemplateList() }
const resetQuery = () => { queryFormRef.value.resetFields(); handleQuery() }

const templateFormVisible = ref(false)
const templateFormType = ref('')
const sectionRows = ref<ReportSectionRow[]>([])
const dataSourceRows = ref<ReportDataSourceRow[]>([])
const selectedOutputFormats = ref<string[]>([])
let rowSeed = 1

const nextRowId = () => rowSeed++
const defaultSectionRows = (): ReportSectionRow[] => [
  { uid: nextRowId(), sectionCode: 'sampleInfo', sectionName: '样品信息', sourceType: 'sample', visible: true, sort: 10 },
  { uid: nextRowId(), sectionCode: 'resultTable', sectionName: '检测结果', sourceType: 'result_values', visible: true, sort: 20 },
  { uid: nextRowId(), sectionCode: 'evidenceSummary', sectionName: '证据摘要', sourceType: 'evidence', visible: true, sort: 30 }
]
const defaultDataSourceRows = (): ReportDataSourceRow[] => [
  { uid: nextRowId(), sourceCode: 'report', sourcePath: '$.report' },
  { uid: nextRowId(), sourceCode: 'sample', sourcePath: '$.sample' },
  { uid: nextRowId(), sourceCode: 'resultValues', sourcePath: '$.resultValues' },
  { uid: nextRowId(), sourceCode: 'equipmentEvidence', sourcePath: '$.equipmentEvidenceSnapshots' },
  { uid: nextRowId(), sourceCode: 'personnelEvidence', sourcePath: '$.personnelEvidenceSnapshots' }
]
const defaultOutputFormats = () => ['WORD', 'PDF', 'EXCEL']
const emptyTemplateForm = (): LabTemplateVersionVO => ({
  domainPackId: 1,
  templateCode: '',
  templateName: '',
  templateVersion: '1.0',
  templateType: 'report',
  templateStatus: 'draft',
  sectionSchema: JSON.stringify(defaultSectionRows().map(({ uid, ...row }) => row)),
  outputFormats: JSON.stringify(defaultOutputFormats()),
  dataSourceSchema: JSON.stringify(Object.fromEntries(defaultDataSourceRows().map((row) => [row.sourceCode, row.sourcePath]))),
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
  status: [{ required: true, message: '状态不能为空', trigger: 'blur' }]
})
const templateFormRef = ref()

const openTemplateForm = async (type: string, id?: number) => {
  templateFormType.value = type
  templateFormVisible.value = true
  templateFormData.value = emptyTemplateForm()
  if (id) templateFormData.value = await LabTemplateApi.getTemplateVersion(id)
  syncDesignerFromForm()
}
const submitTemplateForm = async () => {
  await templateFormRef.value.validate()
  if (!syncFormFromDesigner()) {
    return
  }
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
const addSection = () => {
  sectionRows.value.push({ uid: nextRowId(), sectionCode: '', sectionName: '', sourceType: '', visible: true, sort: sectionRows.value.length * 10 + 10 })
}
const removeSection = (index: number) => {
  sectionRows.value.splice(index, 1)
}
const addDataSource = () => {
  dataSourceRows.value.push({ uid: nextRowId(), sourceCode: '', sourcePath: '' })
}
const removeDataSource = (index: number) => {
  dataSourceRows.value.splice(index, 1)
}
const safeJsonParse = (value?: string) => {
  if (!value) {
    return undefined
  }
  try {
    return JSON.parse(value)
  } catch {
    return undefined
  }
}
const syncDesignerFromForm = () => {
  sectionRows.value = parseSectionRows(templateFormData.value.sectionSchema)
  selectedOutputFormats.value = parseOutputFormats(templateFormData.value.outputFormats)
  dataSourceRows.value = parseDataSourceRows(templateFormData.value.dataSourceSchema)
  templateFormData.value.templateStatus = templateFormData.value.templateStatus || 'draft'
}
const syncFormFromDesigner = () => {
  const normalizedSections = sectionRows.value
    .map((row) => ({
      sectionCode: row.sectionCode?.trim(),
      sectionName: row.sectionName?.trim(),
      sourceType: row.sourceType?.trim(),
      visible: row.visible !== false,
      sort: Number(row.sort || 0)
    }))
    .filter((row) => row.sectionCode && row.sectionName && row.sourceType)
    .sort((left, right) => left.sort - right.sort)
  if (!normalizedSections.length) {
    message.warning('至少需要维护一个报告章节')
    return false
  }
  const formats = selectedOutputFormats.value.filter((format) =>
    outputFormatOptions.some((option) => option.value === format)
  )
  if (!formats.length) {
    message.warning('至少需要选择一种报告输出格式')
    return false
  }
  const dataSourceEntries = dataSourceRows.value
    .map((row) => [row.sourceCode?.trim(), row.sourcePath?.trim()] as const)
    .filter(([sourceCode, sourcePath]) => sourceCode && sourcePath)
  if (!dataSourceEntries.length) {
    message.warning('至少需要维护一个报告数据来源')
    return false
  }
  templateFormData.value.templateStatus = 'draft'
  templateFormData.value.sectionSchema = JSON.stringify(normalizedSections)
  templateFormData.value.outputFormats = JSON.stringify(Array.from(new Set(formats)))
  templateFormData.value.dataSourceSchema = JSON.stringify(Object.fromEntries(dataSourceEntries))
  return true
}
const parseSectionRows = (value?: string): ReportSectionRow[] => {
  const parsed = safeJsonParse(value)
  const list = Array.isArray(parsed) ? parsed : Array.isArray(parsed?.sections) ? parsed.sections : []
  const rows = list
    .map((item: any, index: number) => ({
      uid: nextRowId(),
      sectionCode: String(item?.sectionCode || item?.code || ''),
      sectionName: String(item?.sectionName || item?.name || ''),
      sourceType: String(item?.sourceType || item?.source || ''),
      visible: item?.visible !== false,
      sort: Number(item?.sort ?? (index + 1) * 10)
    }))
    .filter((row) => row.sectionCode || row.sectionName || row.sourceType)
  return rows.length ? rows : defaultSectionRows()
}
const parseOutputFormats = (value?: string): string[] => {
  const parsed = safeJsonParse(value)
  const values = Array.isArray(parsed)
    ? parsed
    : value
      ? value.split(',').map((item) => item.trim())
      : []
  const formats = values
    .map((item) => String(item).toUpperCase())
    .filter((format) => outputFormatOptions.some((option) => option.value === format))
  return formats.length ? Array.from(new Set(formats)) : defaultOutputFormats()
}
const parseDataSourceRows = (value?: string): ReportDataSourceRow[] => {
  const parsed = safeJsonParse(value)
  if (Array.isArray(parsed)) {
    const rows = parsed
      .map((item: any) => ({
        uid: nextRowId(),
        sourceCode: String(item?.sourceCode || item?.code || ''),
        sourcePath: String(item?.sourcePath || item?.path || '')
      }))
      .filter((row) => row.sourceCode || row.sourcePath)
    return rows.length ? rows : defaultDataSourceRows()
  }
  if (parsed && typeof parsed === 'object') {
    const rows = Object.entries(parsed)
      .map(([sourceCode, sourcePath]) => ({
        uid: nextRowId(),
        sourceCode,
        sourcePath: String(sourcePath || '')
      }))
      .filter((row) => row.sourceCode || row.sourcePath)
    return rows.length ? rows : defaultDataSourceRows()
  }
  return defaultDataSourceRows()
}

const templateOverview = computed(() => {
  const formats = new Set<string>()
  templateList.value.forEach((item) => parseOutputFormats(item.outputFormats).forEach((format) => formats.add(format)))
  return {
    total: total.value || templateList.value.length,
    published: templateList.value.filter((item) => item.templateStatus === 'published').length,
    formats: Array.from(formats)
  }
})

const currentOutputFormats = computed(() => parseOutputFormats(currentTemplate.value?.outputFormats))
const currentSections = computed(() =>
  parseSectionRows(currentTemplate.value?.sectionSchema).sort((left, right) => left.sort - right.sort)
)
const currentDataSources = computed(() => parseDataSourceRows(currentTemplate.value?.dataSourceSchema))

watch(currentOutputFormats, (formats) => {
  if (!formats.includes(activeOutputFormat.value)) {
    activeOutputFormat.value = formats[0] || 'WORD'
  }
})

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
const previewData = ref<any>()
const previewSections = computed(() => parseSectionRows(previewData.value?.sectionSchema))
const openPreview = async (templateId: number) => {
  const data = await LabTemplateApi.getTemplatePreview(templateId)
  previewData.value = data
  previewText.value = JSON.stringify(data, null, 2)
  previewVisible.value = true
}

onMounted(() => getTemplateList())
</script>

<style scoped>
.template-hero,
.designer-workbench-header {
  display: flex;
  gap: 16px;
  align-items: flex-start;
  justify-content: space-between;
}

.template-heading {
  min-width: 0;
}

.template-kicker {
  color: var(--el-color-primary);
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0;
}

.template-title {
  margin-top: 4px;
  color: var(--el-text-color-primary);
  font-size: 20px;
  font-weight: 600;
  line-height: 1.35;
}

.template-subtitle {
  max-width: 820px;
  margin-top: 6px;
  color: var(--el-text-color-secondary);
  font-size: 13px;
  line-height: 1.5;
}

.template-tags,
.format-chip-list,
.workbench-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.template-tags {
  margin-top: 12px;
}

.template-overview-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-top: 18px;
}

.template-overview-card {
  min-height: 110px;
  padding: 16px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 6px;
  background: var(--el-bg-color);
}

.template-overview-card span,
.template-overview-card small {
  display: block;
  color: var(--el-text-color-secondary);
  font-size: 12px;
  line-height: 1.45;
}

.template-overview-card strong {
  display: block;
  margin: 10px 0 8px;
  color: var(--el-text-color-primary);
  font-size: 28px;
  font-weight: 600;
  line-height: 1.15;
}

.workbench-title {
  color: var(--el-text-color-primary);
  font-size: 18px;
  font-weight: 600;
  line-height: 1.35;
}

.workbench-subtitle,
.table-subtext {
  margin-top: 5px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
  line-height: 1.45;
}

.output-tabs {
  margin-top: 12px;
}

.report-canvas,
.binding-panel {
  min-height: 380px;
  padding: 16px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 6px;
  background: var(--el-bg-color);
}

.canvas-toolbar {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 14px;
}

.canvas-title,
.panel-title {
  color: var(--el-text-color-primary);
  font-size: 15px;
  font-weight: 600;
}

.canvas-subtitle {
  margin-top: 4px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
  line-height: 1.45;
}

.paper-preview {
  min-height: 310px;
  padding: 18px;
  border: 1px solid var(--el-border-color);
  border-radius: 4px;
  background: var(--el-fill-color-extra-light);
}

.paper-preview--word {
  border-top: 3px solid var(--el-color-primary);
}

.paper-preview--pdf {
  border-top: 3px solid var(--el-color-danger);
}

.paper-preview--excel {
  border-top: 3px solid var(--el-color-success);
}

.paper-header {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  justify-content: space-between;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.paper-header strong,
.paper-header span {
  display: block;
}

.paper-header strong {
  color: var(--el-text-color-primary);
  font-size: 16px;
}

.paper-header span {
  margin-top: 4px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.paper-section {
  padding: 12px 0;
  border-bottom: 1px dashed var(--el-border-color-lighter);
}

.paper-section-title {
  display: flex;
  gap: 8px;
  align-items: center;
  color: var(--el-text-color-primary);
  font-size: 14px;
  font-weight: 600;
}

.paper-section-title span {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 26px;
  height: 22px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
  font-weight: 400;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 4px;
  background: var(--el-bg-color);
}

.paper-section-body {
  margin-top: 6px;
  padding-left: 34px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.source-row {
  display: grid;
  grid-template-columns: 120px minmax(0, 1fr);
  gap: 10px;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.source-row span {
  color: var(--el-text-color-primary);
  font-size: 13px;
  font-weight: 600;
}

.source-row strong {
  overflow: hidden;
  color: var(--el-text-color-secondary);
  font-size: 12px;
  font-weight: 400;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.schema-preview {
  max-height: 520px;
  padding: 14px;
  overflow: auto;
  color: var(--el-text-color-regular);
  font-size: 12px;
  line-height: 1.5;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 6px;
  background: var(--el-fill-color-extra-light);
}

@media (max-width: 1200px) {
  .template-overview-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .template-hero,
  .designer-workbench-header,
  .canvas-toolbar,
  .paper-header {
    flex-direction: column;
  }

  .template-overview-grid,
  .source-row {
    grid-template-columns: 1fr;
  }
}
</style>
