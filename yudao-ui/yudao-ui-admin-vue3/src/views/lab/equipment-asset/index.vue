<template>
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="88px"
    >
      <el-form-item label="设备编码" prop="equipmentCode">
        <el-input
          v-model="queryParams.equipmentCode"
          class="!w-200px"
          clearable
          placeholder="请输入设备编码"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="设备名称" prop="equipmentName">
        <el-input
          v-model="queryParams.equipmentName"
          class="!w-220px"
          clearable
          placeholder="请输入设备名称"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="检测方向" prop="domainCode">
        <el-input
          v-model="queryParams.domainCode"
          class="!w-160px"
          clearable
          placeholder="如 FOOD"
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
          v-hasPermi="['lab:equipment-asset:create']"
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
      <el-table-column align="center" label="设备编码" min-width="150" prop="equipmentCode" />
      <el-table-column align="center" label="设备名称" min-width="180" prop="equipmentName" />
      <el-table-column align="center" label="类型" min-width="110" prop="equipmentType" />
      <el-table-column align="center" label="型号" min-width="120" prop="model" show-overflow-tooltip />
      <el-table-column align="center" label="实验区域" min-width="150" prop="labArea" show-overflow-tooltip />
      <el-table-column align="center" label="检测方向" min-width="110" prop="domainCode" />
      <el-table-column align="center" label="能力范围" min-width="220" prop="capabilityScope" show-overflow-tooltip />
      <el-table-column align="center" label="校准有效期" min-width="120" prop="calibrationValidUntil" />
      <el-table-column align="center" label="IoT" min-width="80">
        <template #default="scope">
          <el-tag :type="scope.row.iotEnabled ? 'success' : 'info'">
            {{ scope.row.iotEnabled ? '启用' : '未启用' }}
          </el-tag>
        </template>
      </el-table-column>
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
      <el-table-column align="center" fixed="right" label="操作" width="210">
        <template #default="scope">
          <el-button link type="primary" @click="openEvidenceDrawer(scope.row)">
            证据链
          </el-button>
          <el-button
            v-hasPermi="['lab:equipment-asset:update']"
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >
            编辑
          </el-button>
          <el-button
            v-hasPermi="['lab:equipment-asset:delete']"
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

  <el-dialog v-model="formVisible" :title="formTitle" width="820px">
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="112px">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="设备编码" prop="equipmentCode">
            <el-input v-model="formData.equipmentCode" maxlength="64" placeholder="如 PH-METER-001" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="设备名称" prop="equipmentName">
            <el-input v-model="formData.equipmentName" maxlength="128" placeholder="请输入设备名称" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="设备类型" prop="equipmentType">
            <el-input v-model="formData.equipmentType" maxlength="64" placeholder="如 instrument" />
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
          <el-form-item label="制造商" prop="manufacturer">
            <el-input v-model="formData.manufacturer" maxlength="128" placeholder="请输入制造商" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="型号" prop="model">
            <el-input v-model="formData.model" maxlength="128" placeholder="请输入型号" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="序列号" prop="serialNo">
            <el-input v-model="formData.serialNo" maxlength="128" placeholder="请输入序列号" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="实验区域" prop="labArea">
            <el-input v-model="formData.labArea" maxlength="128" placeholder="请输入实验区域" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="检测方向" prop="domainCode">
            <el-input v-model="formData.domainCode" maxlength="64" placeholder="如 FOOD、ENV" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="校准有效期" prop="calibrationValidUntil">
            <el-date-picker
              v-model="formData.calibrationValidUntil"
              class="w-1/1"
              placeholder="请选择日期"
              type="date"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="能力范围" prop="capabilityScope">
        <el-input
          v-model="formData.capabilityScope"
          maxlength="1024"
          placeholder="请输入适用项目、方法或能力关键词，多个用逗号分隔"
          show-word-limit
          type="textarea"
        />
      </el-form-item>
      <el-row :gutter="16">
        <el-col :span="8">
          <el-form-item label="启用物联" prop="iotEnabled">
            <el-switch v-model="formData.iotEnabled" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="IoT产品ID" prop="iotProductId">
            <el-input-number v-model="formData.iotProductId" :min="1" class="w-1/1" controls-position="right" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="数据来源" prop="dataSourceType">
            <el-select v-model="formData.dataSourceType" class="w-1/1" clearable placeholder="请选择">
              <el-option label="人工录入" value="manual" />
              <el-option label="IoT采集" value="iot" />
              <el-option label="导入" value="import" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="IoT设备ID" prop="iotDeviceId">
        <el-input v-model="formData.iotDeviceId" maxlength="128" placeholder="芋道 IoT 设备标识" />
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

  <el-drawer
    v-model="evidenceDrawerVisible"
    :title="evidenceDrawerTitle"
    destroy-on-close
    size="880px"
  >
    <div v-if="selectedEquipment" class="flex flex-col gap-16px">
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="设备编码">{{ selectedEquipment.equipmentCode }}</el-descriptions-item>
        <el-descriptions-item label="设备名称">{{ selectedEquipment.equipmentName }}</el-descriptions-item>
        <el-descriptions-item label="检测方向">{{ selectedEquipment.domainCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="校准有效期">
          {{ selectedEquipment.calibrationValidUntil || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="能力范围" :span="2">
          {{ selectedEquipment.capabilityScope || '-' }}
        </el-descriptions-item>
      </el-descriptions>

      <div class="flex items-center justify-between">
        <div>
          <div class="text-15px font-600">当前有效校准证据</div>
        </div>
        <el-button
          v-hasPermi="['lab:equipment-traceability:create']"
          plain
          type="primary"
          @click="openCalibrationForm"
        >
          <Icon class="mr-5px" icon="ep:plus" />
          新增校准证据
        </el-button>
      </div>

      <el-table v-loading="evidenceLoading" :data="calibrationEvidenceList" border size="small">
        <el-table-column label="证书编号" min-width="140" prop="certificateNo" />
        <el-table-column label="校准机构" min-width="160" prop="calibrationOrg" show-overflow-tooltip />
        <el-table-column label="校准日期" min-width="110" prop="calibrationDate" />
        <el-table-column label="有效期至" min-width="110" prop="validTo" />
        <el-table-column label="结果" min-width="90" prop="result" />
        <el-table-column label="证书文件" min-width="140">
          <template #default="scope">
            <el-link
              v-if="scope.row.certificateFileUrl"
              :href="scope.row.certificateFileUrl"
              target="_blank"
              type="primary"
            >
              查看
            </el-link>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" min-width="90">
          <template #default="scope">
            <el-tag :type="scope.row.effective ? 'success' : 'info'">
              {{ scope.row.effective ? '有效' : scope.row.status || '-' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>

      <div>
        <div class="mb-8px text-15px font-600">设备证据链映射</div>
        <el-table v-loading="evidenceLoading" :data="evidenceLinkList" border size="small">
          <el-table-column label="证据" min-width="230">
            <template #default="scope">
              <div>{{ scope.row.evidenceCode || '-' }}</div>
              <div class="mt-2px text-12px color-#6b7280 truncate">
                {{ scope.row.evidenceName || '-' }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="映射关系" min-width="210">
            <template #default="scope">
              <div>{{ scope.row.sourceObjectNo || '-' }}</div>
              <div class="mt-4px flex gap-6px">
                <el-tag size="small">{{ scope.row.clauseCategory || '-' }}</el-tag>
                <el-tag size="small" type="success">{{ scope.row.linkStatus || '-' }}</el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="证据哈希" min-width="150">
            <template #default="scope">{{ formatHash(scope.row.evidenceHash) }}</template>
          </el-table-column>
          <el-table-column label="关联原因" min-width="210" prop="linkReason" show-overflow-tooltip />
        </el-table>
      </div>
    </div>
  </el-drawer>

  <el-dialog v-model="calibrationFormVisible" title="新增校准证据" width="720px">
    <el-form
      ref="calibrationFormRef"
      :model="calibrationFormData"
      :rules="calibrationFormRules"
      label-width="112px"
    >
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="设备ID" prop="equipmentId">
            <el-input-number
              v-model="calibrationFormData.equipmentId"
              :disabled="true"
              :min="1"
              class="w-1/1"
              controls-position="right"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="证书编号" prop="certificateNo">
            <el-input v-model="calibrationFormData.certificateNo" maxlength="128" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="校准机构" prop="calibrationOrg">
            <el-input v-model="calibrationFormData.calibrationOrg" maxlength="128" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="溯源类型" prop="traceabilityType">
            <el-select v-model="calibrationFormData.traceabilityType" class="w-1/1">
              <el-option label="校准" value="calibration" />
              <el-option label="检定" value="verification" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="校准日期" prop="calibrationDate">
            <el-date-picker
              v-model="calibrationFormData.calibrationDate"
              class="w-1/1"
              type="date"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="有效期至" prop="validTo">
            <el-date-picker
              v-model="calibrationFormData.validTo"
              class="w-1/1"
              type="date"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="结果" prop="result">
            <el-select v-model="calibrationFormData.result" class="w-1/1">
              <el-option label="合格" value="合格" />
              <el-option label="限用" value="限用" />
              <el-option label="不合格" value="不合格" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="不确定度" prop="uncertainty">
            <el-input v-model="calibrationFormData.uncertainty" maxlength="128" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="证书文件" prop="certificateFileUrl">
        <el-input v-model="calibrationFormData.certificateFileUrl" maxlength="512" />
      </el-form-item>
      <el-form-item label="溯源链" prop="traceabilityChain">
        <el-input v-model="calibrationFormData.traceabilityChain" rows="3" type="textarea" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="calibrationFormLoading" type="primary" @click="submitCalibrationForm">
        确 定
      </el-button>
      <el-button @click="calibrationFormVisible = false">取 消</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { dateFormatter } from '@/utils/formatTime'
import { LabEquipmentAssetApi, LabEquipmentAssetVO } from '@/api/lab/equipment-asset'
import { LabEvidenceLinkApi, LabEvidenceLinkVO } from '@/api/lab/evidence-link'
import {
  LabEquipmentCalibrationEvidenceVO,
  LabQualityApi,
  LabQualityRecordVO
} from '@/api/lab/quality'

defineOptions({ name: 'LabEquipmentAsset' })

const message = useMessage()
const { t } = useI18n()

const loading = ref(true)
const list = ref<LabEquipmentAssetVO[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  equipmentCode: undefined,
  equipmentName: undefined,
  domainCode: undefined,
  status: undefined
})
const queryFormRef = ref()

const statusOptions = [
  { label: '启用', value: 'enabled', tag: 'success' },
  { label: '草稿', value: 'draft', tag: 'info' },
  { label: '过期', value: 'expired', tag: 'warning' },
  { label: '停用', value: 'disabled', tag: 'danger' }
]

const getStatusLabel = (status?: string) => {
  return statusOptions.find((item) => item.value === status)?.label || status || '-'
}

const getStatusTag = (status?: string) => {
  return statusOptions.find((item) => item.value === status)?.tag || ''
}

const getList = async () => {
  loading.value = true
  try {
    const data = await LabEquipmentAssetApi.getEquipmentAssetPage(queryParams)
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
const formTitle = computed(() => (formType.value === 'create' ? '新增设备主档' : '编辑设备主档'))
const formData = ref<LabEquipmentAssetVO>({
  equipmentCode: '',
  equipmentName: '',
  equipmentType: 'instrument',
  manufacturer: '',
  model: '',
  serialNo: '',
  labArea: '',
  domainCode: '',
  capabilityScope: '',
  calibrationValidUntil: '',
  status: 'enabled',
  iotEnabled: false,
  iotProductId: undefined,
  iotDeviceId: '',
  dataSourceType: 'manual',
  remark: ''
})
const formRules = reactive({
  equipmentCode: [{ required: true, message: '设备编码不能为空', trigger: 'blur' }],
  equipmentName: [{ required: true, message: '设备名称不能为空', trigger: 'blur' }]
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
    formData.value = await LabEquipmentAssetApi.getEquipmentAsset(id)
  } finally {
    formLoading.value = false
  }
}

const submitForm = async () => {
  await formRef.value.validate()
  formLoading.value = true
  try {
    if (formType.value === 'create') {
      await LabEquipmentAssetApi.createEquipmentAsset(formData.value)
      message.success(t('common.createSuccess'))
    } else {
      await LabEquipmentAssetApi.updateEquipmentAsset(formData.value)
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
  await LabEquipmentAssetApi.deleteEquipmentAsset(id)
  message.success(t('common.delSuccess'))
  await getList()
}

const evidenceDrawerVisible = ref(false)
const evidenceLoading = ref(false)
const selectedEquipment = ref<LabEquipmentAssetVO>()
const calibrationEvidenceList = ref<LabEquipmentCalibrationEvidenceVO[]>([])
const evidenceLinkList = ref<LabEvidenceLinkVO[]>([])
const evidenceDrawerTitle = computed(() => {
  return selectedEquipment.value
    ? `设备证据链 - ${selectedEquipment.value.equipmentCode}`
    : '设备证据链'
})

const openEvidenceDrawer = async (equipment: LabEquipmentAssetVO) => {
  selectedEquipment.value = equipment
  evidenceDrawerVisible.value = true
  await refreshEvidenceChain()
}

const refreshEvidenceChain = async () => {
  if (!selectedEquipment.value?.id) {
    calibrationEvidenceList.value = []
    evidenceLinkList.value = []
    return
  }
  evidenceLoading.value = true
  try {
    const [calibrationEvidence, evidenceLinkPage] = await Promise.all([
      LabQualityApi.getCurrentEquipmentCalibrationEvidence(selectedEquipment.value.id),
      LabEvidenceLinkApi.getEvidenceLinkPage({
        pageNo: 1,
        pageSize: 50,
        linkedBizType: 'equipment_asset',
        linkedBizId: selectedEquipment.value.id
      })
    ])
    calibrationEvidenceList.value = calibrationEvidence
    evidenceLinkList.value = evidenceLinkPage.list || []
  } finally {
    evidenceLoading.value = false
  }
}

const formatHash = (hash?: string) => {
  if (!hash) return '-'
  return hash.length > 16 ? `${hash.slice(0, 16)}...` : hash
}

const calibrationFormVisible = ref(false)
const calibrationFormLoading = ref(false)
const calibrationFormRef = ref()
const calibrationFormData = ref<LabQualityRecordVO>({})
const calibrationFormRules = reactive({
  equipmentId: [{ required: true, message: '设备不能为空', trigger: 'change' }],
  certificateNo: [{ required: true, message: '证书编号不能为空', trigger: 'blur' }],
  calibrationOrg: [{ required: true, message: '校准机构不能为空', trigger: 'blur' }],
  calibrationDate: [{ required: true, message: '校准日期不能为空', trigger: 'change' }],
  validTo: [{ required: true, message: '有效期不能为空', trigger: 'change' }],
  result: [{ required: true, message: '结果不能为空', trigger: 'change' }]
})

const openCalibrationForm = () => {
  if (!selectedEquipment.value?.id) return
  calibrationFormData.value = {
    equipmentId: selectedEquipment.value.id,
    traceabilityType: 'calibration',
    certificateNo: '',
    calibrationOrg: '',
    calibrationDate: '',
    validTo: '',
    result: '合格',
    uncertainty: '',
    certificateFileUrl: '',
    traceabilityChain: '',
    status: 'valid'
  }
  calibrationFormVisible.value = true
  nextTick(() => calibrationFormRef.value?.clearValidate())
}

const submitCalibrationForm = async () => {
  await calibrationFormRef.value.validate()
  calibrationFormLoading.value = true
  try {
    await LabQualityApi.create('/lab/equipment-traceability', calibrationFormData.value)
    message.success('校准证据已登记，设备主档有效期已同步')
    calibrationFormVisible.value = false
    await Promise.all([refreshEvidenceChain(), getList()])
  } finally {
    calibrationFormLoading.value = false
  }
}

const resetForm = () => {
  formData.value = {
    equipmentCode: '',
    equipmentName: '',
    equipmentType: 'instrument',
    manufacturer: '',
    model: '',
    serialNo: '',
    labArea: '',
    domainCode: '',
    capabilityScope: '',
    calibrationValidUntil: '',
    status: 'enabled',
    iotEnabled: false,
    iotProductId: undefined,
    iotDeviceId: '',
    dataSourceType: 'manual',
    remark: ''
  }
  formRef.value?.resetFields()
}

onMounted(() => {
  getList()
})
</script>
