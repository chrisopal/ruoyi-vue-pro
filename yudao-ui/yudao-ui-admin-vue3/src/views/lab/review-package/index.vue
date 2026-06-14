<template>
  <ContentWrap>
    <el-form ref="queryFormRef" :inline="true" :model="queryParams" class="-mb-15px" label-width="88px">
      <el-form-item label="批次编码" prop="batchCode">
        <el-input v-model="queryParams.batchCode" class="!w-180px" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="批次名称" prop="batchName">
        <el-input v-model="queryParams.batchName" class="!w-220px" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon class="mr-5px" icon="ep:search" />搜索</el-button>
        <el-button @click="resetQuery"><Icon class="mr-5px" icon="ep:refresh" />重置</el-button>
        <el-button v-hasPermi="['lab:review-package:create']" plain type="primary" @click="openBatchForm('create')">
          <Icon class="mr-5px" icon="ep:plus" />新增批次
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="batchLoading" :data="batchList" highlight-current-row row-key="id" @current-change="handleCurrentBatch">
      <el-table-column align="center" label="批次编码" min-width="150" prop="batchCode" />
      <el-table-column align="center" label="批次名称" min-width="180" prop="batchName" />
      <el-table-column align="center" label="评审类型" min-width="130" prop="reviewType" />
      <el-table-column align="center" label="状态" min-width="100" prop="status" />
      <el-table-column align="center" label="备注" min-width="220" prop="remark" show-overflow-tooltip />
      <el-table-column align="center" fixed="right" label="操作" width="170">
        <template #default="scope">
          <el-button v-hasPermi="['lab:review-package:update']" link type="primary" @click="openBatchForm('update', scope.row.id)">编辑</el-button>
          <el-button v-hasPermi="['lab:review-package:delete']" link type="danger" @click="handleDeleteBatch(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination v-model:limit="queryParams.pageSize" v-model:page="queryParams.pageNo" :total="batchTotal" @pagination="getBatchList" />
  </ContentWrap>

  <ContentWrap>
    <el-alert :closable="false" class="mb-16px" show-icon title="当前编制批次" type="info" :description="currentBatchLabel" />
    <el-form :inline="true" :model="itemQueryParams" class="-mb-15px" label-width="88px">
      <el-form-item label="条目类型" prop="itemType">
        <el-select v-model="itemQueryParams.itemType" class="!w-160px" clearable @change="handleItemQuery">
          <el-option v-for="item in itemTypes" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="条款编号" prop="clauseCode">
        <el-input v-model="itemQueryParams.clauseCode" class="!w-160px" clearable @keyup.enter="handleItemQuery" />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleItemQuery"><Icon class="mr-5px" icon="ep:search" />搜索条目</el-button>
        <el-button v-hasPermi="['lab:review-package:create']" :disabled="!currentBatch?.id" plain type="primary" @click="openItemForm('create')">
          <Icon class="mr-5px" icon="ep:plus" />新增条目
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-row :gutter="16" class="mb-16px">
      <el-col v-for="item in exports" :key="item.key" :md="6" :sm="12" :xs="24">
        <el-button
          v-hasPermi="['lab:review-package:export']"
          :disabled="!currentBatch?.batchCode"
          :loading="loadingKey === item.key"
          class="w-1/1"
          plain
          type="primary"
          @click="handleExport(item.key)"
        >
          <Icon class="mr-5px" :icon="item.icon" />
          {{ item.label }}
        </el-button>
      </el-col>
    </el-row>
    <el-table v-loading="itemLoading" :data="itemList" row-key="id">
      <el-table-column align="center" label="类型" min-width="110">
        <template #default="scope">{{ getItemTypeLabel(scope.row.itemType) }}</template>
      </el-table-column>
      <el-table-column align="center" label="条款/证据/编号" min-width="180">
        <template #default="scope">{{ getItemMainNo(scope.row) }}</template>
      </el-table-column>
      <el-table-column align="center" label="业务分类" min-width="120" prop="clauseCategory" />
      <el-table-column align="center" label="描述" min-width="320" show-overflow-tooltip>
        <template #default="scope">{{ getItemDescription(scope.row) }}</template>
      </el-table-column>
      <el-table-column align="center" label="责任人/角色" min-width="140">
        <template #default="scope">{{ scope.row.owner || scope.row.ownerRole }}</template>
      </el-table-column>
      <el-table-column align="center" label="排序" min-width="80" prop="sort" />
      <el-table-column align="center" fixed="right" label="操作" width="150">
        <template #default="scope">
          <el-button v-hasPermi="['lab:review-package:update']" link type="primary" @click="openItemForm('update', scope.row.id)">编辑</el-button>
          <el-button v-hasPermi="['lab:review-package:delete']" link type="danger" @click="handleDeleteItem(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination v-model:limit="itemQueryParams.pageSize" v-model:page="itemQueryParams.pageNo" :total="itemTotal" @pagination="getItemList" />
  </ContentWrap>

  <el-dialog v-model="batchFormVisible" :title="batchFormType === 'create' ? '新增评审批次' : '编辑评审批次'" width="720px">
    <el-form ref="batchFormRef" :model="batchFormData" :rules="batchFormRules" label-width="112px">
      <el-row :gutter="16">
        <el-col :span="12"><el-form-item label="批次编码" prop="batchCode"><el-input v-model="batchFormData.batchCode" /></el-form-item></el-col>
        <el-col :span="12"><el-form-item label="批次名称" prop="batchName"><el-input v-model="batchFormData.batchName" /></el-form-item></el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12"><el-form-item label="评审类型" prop="reviewType"><el-input v-model="batchFormData.reviewType" /></el-form-item></el-col>
        <el-col :span="12"><el-form-item label="状态" prop="status"><el-input v-model="batchFormData.status" /></el-form-item></el-col>
      </el-row>
      <el-form-item label="标准编码 JSON" prop="standardCodes"><el-input v-model="batchFormData.standardCodes" :rows="3" type="textarea" /></el-form-item>
      <el-form-item label="备注" prop="remark"><el-input v-model="batchFormData.remark" type="textarea" /></el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="batchFormLoading" type="primary" @click="submitBatchForm">确 定</el-button>
      <el-button @click="batchFormVisible = false">取 消</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="itemFormVisible" :title="itemFormType === 'create' ? '新增评审条目' : '编辑评审条目'" width="860px">
    <el-form ref="itemFormRef" :model="itemFormData" :rules="itemFormRules" label-width="116px">
      <el-row :gutter="16">
        <el-col :span="8">
          <el-form-item label="条目类型" prop="itemType">
            <el-select v-model="itemFormData.itemType" class="w-1/1">
              <el-option v-for="item in itemTypes" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8"><el-form-item label="排序" prop="sort"><el-input-number v-model="itemFormData.sort" :min="0" class="w-1/1" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="状态" prop="status"><el-input v-model="itemFormData.status" /></el-form-item></el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="8"><el-form-item label="标准" prop="standardName"><el-input v-model="itemFormData.standardName" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="条款编号" prop="clauseCode"><el-input v-model="itemFormData.clauseCode" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="业务分类" prop="clauseCategory"><el-input v-model="itemFormData.clauseCategory" /></el-form-item></el-col>
      </el-row>
      <el-form-item label="检查要点" prop="checkPoint"><el-input v-model="itemFormData.checkPoint" :rows="2" type="textarea" /></el-form-item>
      <el-row :gutter="16">
        <el-col :span="8"><el-form-item label="期望证据" prop="expectedEvidence"><el-input v-model="itemFormData.expectedEvidence" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="证据名称" prop="evidenceName"><el-input v-model="itemFormData.evidenceName" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="来源对象" prop="sourceObject"><el-input v-model="itemFormData.sourceObject" /></el-form-item></el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="8"><el-form-item label="关联对象编号" prop="linkedObjectNo"><el-input v-model="itemFormData.linkedObjectNo" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="关联状态" prop="linkStatus"><el-input v-model="itemFormData.linkStatus" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="责任角色" prop="ownerRole"><el-input v-model="itemFormData.ownerRole" /></el-form-item></el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="8"><el-form-item label="NC 编号" prop="ncNo"><el-input v-model="itemFormData.ncNo" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="CAPA 编号" prop="capaNo"><el-input v-model="itemFormData.capaNo" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="严重程度" prop="severity"><el-input v-model="itemFormData.severity" /></el-form-item></el-col>
      </el-row>
      <el-form-item label="描述" prop="description"><el-input v-model="itemFormData.description" :rows="2" type="textarea" /></el-form-item>
      <el-form-item label="原因分析" prop="rootCause"><el-input v-model="itemFormData.rootCause" :rows="2" type="textarea" /></el-form-item>
      <el-form-item label="措施" prop="action"><el-input v-model="itemFormData.action" :rows="2" type="textarea" /></el-form-item>
      <el-row :gutter="16">
        <el-col :span="12"><el-form-item label="责任人" prop="owner"><el-input v-model="itemFormData.owner" /></el-form-item></el-col>
        <el-col :span="12"><el-form-item label="计划完成日期" prop="dueDate"><el-input v-model="itemFormData.dueDate" /></el-form-item></el-col>
      </el-row>
      <el-form-item label="备注" prop="remark"><el-input v-model="itemFormData.remark" type="textarea" /></el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="itemFormLoading" type="primary" @click="submitItemForm">确 定</el-button>
      <el-button @click="itemFormVisible = false">取 消</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import download from '@/utils/download'
import { LabReviewBatchVO, LabReviewItemVO, LabReviewPackageApi } from '@/api/lab/review-package'

defineOptions({ name: 'LabReviewPackage' })

const message = useMessage()
const { t } = useI18n()
const batchLoading = ref(true)
const itemLoading = ref(false)
const loadingKey = ref('')
const batchList = ref<LabReviewBatchVO[]>([])
const itemList = ref<LabReviewItemVO[]>([])
const batchTotal = ref(0)
const itemTotal = ref(0)
const currentBatch = ref<LabReviewBatchVO>()
const queryParams = reactive({ pageNo: 1, pageSize: 10, batchCode: undefined, batchName: undefined })
const itemQueryParams = reactive({ pageNo: 1, pageSize: 10, batchId: undefined as number | undefined, itemType: undefined, clauseCode: undefined })
const queryFormRef = ref()

const itemTypes = [
  { value: 'checklist', label: '检查表' },
  { value: 'evidence', label: '证据清单' },
  { value: 'nc', label: 'NC 清单' },
  { value: 'capa', label: 'CAPA 清单' }
]
const exports = [
  { key: 'checklist', label: '导出检查表', icon: 'ep:document-checked' },
  { key: 'evidence', label: '导出证据清单', icon: 'ep:folder-checked' },
  { key: 'nc', label: '导出 NC 清单', icon: 'ep:warning' },
  { key: 'capa', label: '导出 CAPA 清单', icon: 'ep:finished' }
]

const currentBatchLabel = computed(() => currentBatch.value ? `${currentBatch.value.batchCode} / ${currentBatch.value.batchName}` : '请先选择或新增评审批次')
const getItemTypeLabel = (type: string) => itemTypes.find((item) => item.value === type)?.label || type
const getItemMainNo = (row: LabReviewItemVO) => row.clauseCode || row.linkedObjectNo || row.ncNo || row.capaNo || row.evidenceName || row.id
const getItemDescription = (row: LabReviewItemVO) => row.checkPoint || row.description || row.rootCause || row.remark

const getBatchList = async () => {
  batchLoading.value = true
  try {
    const data = await LabReviewPackageApi.getReviewBatchPage(queryParams)
    batchList.value = data.list
    batchTotal.value = data.total
    if (!currentBatch.value || !batchList.value.some((item) => item.id === currentBatch.value?.id)) {
      currentBatch.value = batchList.value[0]
      await refreshItemsForCurrentBatch()
    }
  } finally {
    batchLoading.value = false
  }
}
const refreshItemsForCurrentBatch = async () => {
  itemQueryParams.batchId = currentBatch.value?.id
  itemQueryParams.pageNo = 1
  await getItemList()
}
const getItemList = async () => {
  if (!itemQueryParams.batchId) {
    itemList.value = []
    itemTotal.value = 0
    return
  }
  itemLoading.value = true
  try {
    const data = await LabReviewPackageApi.getReviewItemPage(itemQueryParams)
    itemList.value = data.list
    itemTotal.value = data.total
  } finally {
    itemLoading.value = false
  }
}
const handleQuery = () => { queryParams.pageNo = 1; getBatchList() }
const resetQuery = () => { queryFormRef.value.resetFields(); handleQuery() }
const handleItemQuery = () => { itemQueryParams.pageNo = 1; getItemList() }
const handleCurrentBatch = async (row?: LabReviewBatchVO) => {
  if (row?.id) {
    currentBatch.value = row
    await refreshItemsForCurrentBatch()
  }
}

const emptyBatch = (): LabReviewBatchVO => ({ batchCode: '', batchName: '', reviewType: 'accreditation', standardCodes: '[]', status: 'active' })
const batchFormVisible = ref(false)
const batchFormLoading = ref(false)
const batchFormType = ref('')
const batchFormData = ref<LabReviewBatchVO>(emptyBatch())
const batchFormRules = reactive({
  batchCode: [{ required: true, message: '批次编码不能为空', trigger: 'blur' }],
  batchName: [{ required: true, message: '批次名称不能为空', trigger: 'blur' }],
  reviewType: [{ required: true, message: '评审类型不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'blur' }]
})
const batchFormRef = ref()
const openBatchForm = async (type: string, id?: number) => {
  batchFormType.value = type
  batchFormVisible.value = true
  batchFormData.value = emptyBatch()
  if (id) {
    batchFormLoading.value = true
    try { batchFormData.value = await LabReviewPackageApi.getReviewBatch(id) } finally { batchFormLoading.value = false }
  }
}
const submitBatchForm = async () => {
  await batchFormRef.value.validate()
  batchFormLoading.value = true
  try {
    if (batchFormType.value === 'create') {
      await LabReviewPackageApi.createReviewBatch(batchFormData.value)
      message.success(t('common.createSuccess'))
    } else {
      await LabReviewPackageApi.updateReviewBatch(batchFormData.value)
      message.success(t('common.updateSuccess'))
    }
    batchFormVisible.value = false
    await getBatchList()
  } finally {
    batchFormLoading.value = false
  }
}
const handleDeleteBatch = async (id: number) => {
  await message.delConfirm()
  await LabReviewPackageApi.deleteReviewBatch(id)
  message.success(t('common.delSuccess'))
  currentBatch.value = undefined
  await getBatchList()
}

const emptyItem = (): LabReviewItemVO => ({
  batchId: currentBatch.value?.id || 0,
  itemType: itemQueryParams.itemType || 'checklist',
  linkStatus: '已关联',
  status: 'active',
  sort: 10
})
const itemFormVisible = ref(false)
const itemFormLoading = ref(false)
const itemFormType = ref('')
const itemFormData = ref<LabReviewItemVO>(emptyItem())
const itemFormRules = reactive({
  batchId: [{ required: true, message: '批次不能为空', trigger: 'change' }],
  itemType: [{ required: true, message: '条目类型不能为空', trigger: 'change' }]
})
const itemFormRef = ref()
const openItemForm = async (type: string, id?: number) => {
  itemFormType.value = type
  itemFormVisible.value = true
  itemFormData.value = emptyItem()
  if (id) {
    itemFormLoading.value = true
    try { itemFormData.value = await LabReviewPackageApi.getReviewItem(id) } finally { itemFormLoading.value = false }
  }
}
const submitItemForm = async () => {
  await itemFormRef.value.validate()
  itemFormLoading.value = true
  try {
    if (itemFormType.value === 'create') {
      await LabReviewPackageApi.createReviewItem(itemFormData.value)
      message.success(t('common.createSuccess'))
    } else {
      await LabReviewPackageApi.updateReviewItem(itemFormData.value)
      message.success(t('common.updateSuccess'))
    }
    itemFormVisible.value = false
    await getItemList()
  } finally {
    itemFormLoading.value = false
  }
}
const handleDeleteItem = async (id: number) => {
  await message.delConfirm()
  await LabReviewPackageApi.deleteReviewItem(id)
  message.success(t('common.delSuccess'))
  await getItemList()
}

const handleExport = async (key: string) => {
  if (!currentBatch.value?.batchCode) return
  loadingKey.value = key
  try {
    if (key === 'checklist') {
      const data = await LabReviewPackageApi.exportChecklist(currentBatch.value.batchCode)
      download.excel(data, '评审检查表.xls')
    } else if (key === 'evidence') {
      const data = await LabReviewPackageApi.exportEvidence(currentBatch.value.batchCode)
      download.excel(data, '证据清单.xls')
    } else if (key === 'nc') {
      const data = await LabReviewPackageApi.exportNonconformity(currentBatch.value.batchCode)
      download.excel(data, 'NC清单.xls')
    } else {
      const data = await LabReviewPackageApi.exportCapa(currentBatch.value.batchCode)
      download.excel(data, 'CAPA清单.xls')
    }
  } finally {
    loadingKey.value = ''
  }
}

onMounted(() => getBatchList())
</script>
