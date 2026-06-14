<template>
  <ContentWrap>
    <div class="mb-16px flex items-center justify-between">
      <div>
        <div class="text-18px font-600">医疗行业插件配置</div>
        <div class="mt-4px text-12px color-#909399">
          通过表格配置医疗检测方向包，并在同一工作台完成检测需求、任务、结果和报告闭环
        </div>
      </div>
      <div class="flex gap-8px">
        <el-button @click="loadExistingPack"><Icon class="mr-5px" icon="ep:refresh" />加载配置</el-button>
        <el-button type="primary" @click="savePlugin">
          <Icon class="mr-5px" icon="ep:finished" />保存医疗插件
        </el-button>
      </div>
    </div>

    <el-row :gutter="16">
      <el-col :md="6" :xs="24">
        <div class="metric">
          <span>检测方向</span>
          <strong>{{ pluginForm.industry }}</strong>
          <small>{{ pluginForm.packCode }}</small>
        </div>
      </el-col>
      <el-col :md="6" :xs="24">
        <div class="metric">
          <span>流程节点</span>
          <strong>{{ workflowNodes.length }}</strong>
          <small>受理到签发</small>
        </div>
      </el-col>
      <el-col :md="6" :xs="24">
        <div class="metric">
          <span>检测项目</span>
          <strong>{{ testItems.length }}</strong>
          <small>按样品生成任务</small>
        </div>
      </el-col>
      <el-col :md="6" :xs="24">
        <div class="metric">
          <span>结果字段</span>
          <strong>{{ resultFields.length }}</strong>
          <small>动态录入实例</small>
        </div>
      </el-col>
    </el-row>
  </ContentWrap>

  <ContentWrap>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="方案包" name="pack">
        <el-form :model="pluginForm" label-width="112px">
          <el-row :gutter="16">
            <el-col :md="8" :xs="24">
              <el-form-item label="方向编码">
                <el-input v-model="pluginForm.domainCode" />
              </el-form-item>
            </el-col>
            <el-col :md="8" :xs="24">
              <el-form-item label="方向名称">
                <el-input v-model="pluginForm.domainName" />
              </el-form-item>
            </el-col>
            <el-col :md="8" :xs="24">
              <el-form-item label="方案版本">
                <el-input v-model="pluginForm.packVersion" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :md="8" :xs="24">
              <el-form-item label="方案编码">
                <el-input v-model="pluginForm.packCode" />
              </el-form-item>
            </el-col>
            <el-col :md="8" :xs="24">
              <el-form-item label="方案名称">
                <el-input v-model="pluginForm.packName" />
              </el-form-item>
            </el-col>
            <el-col :md="8" :xs="24">
              <el-form-item label="行业方向">
                <el-input v-model="pluginForm.industry" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="适用范围">
            <el-input v-model="pluginForm.applicationScope" :rows="3" type="textarea" />
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <el-tab-pane label="流程节点" name="workflow">
        <el-table :data="workflowNodes" row-key="nodeCode">
          <el-table-column label="排序" prop="sort" width="80" />
          <el-table-column label="节点编码" prop="nodeCode" min-width="150" />
          <el-table-column label="节点名称" prop="nodeName" min-width="150" />
          <el-table-column label="处理角色" prop="roleName" min-width="150" />
          <el-table-column label="是否必经" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.required ? 'success' : 'info'">{{ scope.row.required ? '是' : '否' }}</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="检测项目" name="items">
        <el-table :data="testItems" row-key="itemCode">
          <el-table-column label="项目编码" prop="itemCode" min-width="150" />
          <el-table-column label="项目名称" prop="itemName" min-width="160" />
          <el-table-column label="方法编码" prop="methodCode" min-width="150" />
          <el-table-column label="方法名称" prop="methodName" min-width="180" />
          <el-table-column label="标准" prop="standardCode" min-width="140" />
          <el-table-column label="默认单位" prop="resultUnit" width="100" />
          <el-table-column label="默认值" prop="demoValue" width="100" />
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="结果字段" name="fields">
        <el-table :data="resultFields" row-key="fieldCode">
          <el-table-column label="检测项目" prop="itemCode" min-width="150" />
          <el-table-column label="字段编码" prop="fieldCode" min-width="160" />
          <el-table-column label="字段名称" prop="fieldName" min-width="160" />
          <el-table-column label="字段类型" prop="fieldType" width="110" />
          <el-table-column label="单位" prop="unit" width="90" />
          <el-table-column label="必填" width="90">
            <template #default="scope">
              <el-tag :type="scope.row.required ? 'success' : 'info'">{{ scope.row.required ? '是' : '否' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="范围/选项" min-width="180">
            <template #default="scope">{{ describeFieldRule(scope.row) }}</template>
          </el-table-column>
          <el-table-column label="演示值" prop="demoValue" width="120" />
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="报告章节" name="report">
        <el-table :data="reportSections" row-key="sectionCode">
          <el-table-column label="排序" prop="sort" width="80" />
          <el-table-column label="章节编码" prop="sectionCode" min-width="150" />
          <el-table-column label="章节名称" prop="sectionName" min-width="160" />
          <el-table-column label="数据来源" prop="sourceType" min-width="150" />
          <el-table-column label="是否显示" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.visible ? 'success' : 'info'">{{ scope.row.visible ? '显示' : '隐藏' }}</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </ContentWrap>

  <ContentWrap>
    <div class="mb-16px flex items-center justify-between">
      <div>
        <div class="text-16px font-600">医疗检测执行工作台</div>
        <div class="mt-4px text-12px color-#909399">
          使用上方医疗插件配置，创建真实检测需求并完成报告签发
        </div>
      </div>
      <div class="flex flex-wrap gap-8px">
        <el-button type="primary" @click="createMedicalRequest">创建检测需求</el-button>
        <el-button @click="generateMedicalTasks">生成任务</el-button>
        <el-button @click="recordMedicalResults">录入动态结果</el-button>
        <el-button @click="generateMedicalReport">生成并签发报告</el-button>
      </div>
    </div>

    <el-steps :active="activeStep" finish-status="success" simple>
      <el-step title="插件配置" />
      <el-step title="检测需求" />
      <el-step title="检测任务" />
      <el-step title="结果录入" />
      <el-step title="报告签发" />
    </el-steps>

    <el-row :gutter="16" class="mt-16px">
      <el-col :md="10" :xs="24">
        <el-descriptions :column="1" border title="当前需求">
          <el-descriptions-item label="需求编号">{{ currentRequest?.requestNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="方案包">{{ activePack?.packName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ currentRequest?.status || '-' }}</el-descriptions-item>
          <el-descriptions-item label="报告">{{ currentReport?.reportNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="结论">{{ currentReport?.conclusion || '-' }}</el-descriptions-item>
        </el-descriptions>
      </el-col>
      <el-col :md="14" :xs="24">
        <el-table :data="tasks" height="260" row-key="id">
          <el-table-column label="任务编号" prop="taskNo" min-width="160" />
          <el-table-column label="检测项目" prop="testItem" min-width="160" />
          <el-table-column label="方法" prop="methodCode" min-width="130" />
          <el-table-column label="状态" prop="status" width="110">
            <template #default="scope"><el-tag>{{ scope.row.status }}</el-tag></template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="mt-16px">
      <el-col :md="13" :xs="24">
        <div class="sub-title">动态结果字段实例</div>
        <el-table :data="resultPreviewRows" height="280" row-key="key">
          <el-table-column label="任务" prop="taskNo" min-width="150" />
          <el-table-column label="检测项目" prop="itemName" min-width="140" />
          <el-table-column label="字段" prop="fieldName" min-width="150" />
          <el-table-column label="结果" prop="displayValue" min-width="130" />
          <el-table-column label="判定" prop="conclusion" width="100">
            <template #default="scope"><el-tag type="success">{{ scope.row.conclusion }}</el-tag></template>
          </el-table-column>
        </el-table>
      </el-col>
      <el-col :md="11" :xs="24">
        <div class="sub-title">报告预览</div>
        <div class="report-preview">
          <h3>{{ currentReport?.reportName || '医疗检验报告预览' }}</h3>
          <p>报告编号：{{ currentReport?.reportNo || '待生成' }}</p>
          <p>方向包：{{ pluginForm.packName }}</p>
          <p>报告章节：{{ reportSections.map((item) => item.sectionName).join(' / ') }}</p>
          <p>结论：{{ currentReport?.conclusion || '待生成' }}</p>
          <el-tag :type="currentReport?.status === 'issued' ? 'success' : 'info'">
            {{ currentReport?.status || '未签发' }}
          </el-tag>
        </div>
        <div class="mt-12px">
          <el-timeline>
            <el-timeline-item v-for="item in auditLogs" :key="item" :timestamp="item" />
          </el-timeline>
        </div>
      </el-col>
    </el-row>
  </ContentWrap>
</template>

<script lang="ts" setup>
import { LabDomainProfileApi } from '@/api/lab/domain'
import { LabDomainPackApi, LabDomainPackVO } from '@/api/lab/domain-pack'
import { LabPackConfigApi } from '@/api/lab/pack-config'
import { LimsWorkflowApi, LimsWorkflowVO } from '@/api/lims/workflow'

defineOptions({ name: 'LabMedicalPlugin' })

interface WorkflowNode {
  nodeCode: string
  nodeName: string
  roleName: string
  required: boolean
  sort: number
}

interface TestItem {
  itemCode: string
  itemName: string
  methodCode: string
  methodName: string
  standardCode: string
  resultUnit: string
  demoValue: string
}

interface ResultField {
  itemCode: string
  fieldCode: string
  fieldName: string
  fieldType: string
  unit?: string
  required: boolean
  minValue?: number
  maxValue?: number
  enumOptions?: string[]
  demoValue: string
}

interface ReportSection {
  sectionCode: string
  sectionName: string
  sourceType: string
  visible: boolean
  sort: number
}

const message = useMessage()
const activeTab = ref('pack')
const activeStep = ref(0)
const activePack = ref<LabDomainPackVO>()
const currentRequest = ref<LimsWorkflowVO>()
const currentReport = ref<LimsWorkflowVO>()
const tasks = ref<LimsWorkflowVO[]>([])
const resultPreviewRows = ref<any[]>([])
const auditLogs = ref<string[]>([])

const pluginForm = reactive({
  domainCode: 'MEDICAL',
  domainName: '医疗检验',
  packCode: 'MEDICAL_CLINICAL_V1',
  packName: '医疗临床检验方案包',
  packVersion: '1.0',
  industry: '医疗',
  applicationScope: '血液、炎症、凝血等临床检验场景，支持动态结果字段、复核和报告签发'
})

const workflowNodes = ref<WorkflowNode[]>([
  { sort: 10, nodeCode: 'request', nodeName: '检验申请', roleName: '门诊/科室', required: true },
  { sort: 20, nodeCode: 'sample_receive', nodeName: '样本接收', roleName: '样本室', required: true },
  { sort: 30, nodeCode: 'analysis', nodeName: '仪器检测', roleName: '检验技师', required: true },
  { sort: 40, nodeCode: 'result_review', nodeName: '结果复核', roleName: '审核医生', required: true },
  { sort: 50, nodeCode: 'report_issue', nodeName: '报告签发', roleName: '授权签字人', required: true }
])

const testItems = ref<TestItem[]>([
  { itemCode: 'CBC_WBC', itemName: '白细胞计数', methodCode: 'MED-CBC-001', methodName: '血细胞分析法', standardCode: 'WS/T 406', resultUnit: '10^9/L', demoValue: '6.4' },
  { itemCode: 'CRP', itemName: 'C反应蛋白', methodCode: 'MED-CRP-001', methodName: '免疫比浊法', standardCode: 'YY/T 1454', resultUnit: 'mg/L', demoValue: '4.8' },
  { itemCode: 'D_DIMER', itemName: 'D-二聚体', methodCode: 'MED-DD-001', methodName: '免疫荧光法', standardCode: 'WS/T 477', resultUnit: 'mg/L FEU', demoValue: '0.32' }
])

const resultFields = ref<ResultField[]>([
  { itemCode: 'CBC_WBC', fieldCode: 'wbc_value', fieldName: '白细胞结果', fieldType: 'number', unit: '10^9/L', required: true, minValue: 3.5, maxValue: 9.5, demoValue: '6.4' },
  { itemCode: 'CBC_WBC', fieldCode: 'wbc_flag', fieldName: '白细胞提示', fieldType: 'select', required: true, enumOptions: ['正常', '偏高', '偏低'], demoValue: '正常' },
  { itemCode: 'CRP', fieldCode: 'crp_value', fieldName: 'CRP结果', fieldType: 'number', unit: 'mg/L', required: true, minValue: 0, maxValue: 10, demoValue: '4.8' },
  { itemCode: 'D_DIMER', fieldCode: 'ddimer_value', fieldName: 'D-二聚体结果', fieldType: 'number', unit: 'mg/L FEU', required: true, minValue: 0, maxValue: 0.5, demoValue: '0.32' }
])

const reportSections = ref<ReportSection[]>([
  { sort: 10, sectionCode: 'patientInfo', sectionName: '患者/申请信息', sourceType: 'request', visible: true },
  { sort: 20, sectionCode: 'sampleInfo', sectionName: '样本信息', sourceType: 'sample', visible: true },
  { sort: 30, sectionCode: 'resultTable', sectionName: '检验结果', sourceType: 'result_values', visible: true },
  { sort: 40, sectionCode: 'clinicalHint', sectionName: '临床提示', sourceType: 'rules', visible: true },
  { sort: 50, sectionCode: 'sign', sectionName: '审核签发', sourceType: 'approval', visible: true }
])

const describeFieldRule = (field: ResultField) => {
  if (field.enumOptions?.length) return field.enumOptions.join(' / ')
  if (field.minValue !== undefined || field.maxValue !== undefined) {
    return `${field.minValue ?? '-∞'} ~ ${field.maxValue ?? '+∞'}${field.unit || ''}`
  }
  return '-'
}

const buildWorkflowSchema = () => ({
  stages: workflowNodes.value.map((item) => item.nodeCode),
  workflowNodes: workflowNodes.value,
  samplePolicy: {
    needSampling: false,
    sampleFields: ['患者姓名', '病历号', '样本类型', '采样时间', '申请科室']
  },
  testItems: testItems.value.map((item) => ({
    ...item,
    resultFields: resultFields.value.filter((field) => field.itemCode === item.itemCode)
  })),
  reviewPolicy: {
    resultReview: true,
    reportReview: true
  }
})

const buildTemplateSchema = () => ({
  templates: ['medical_sample_label', 'medical_raw_record', 'medical_report'],
  resultFields: resultFields.value,
  reportSections: reportSections.value
})

const getPackConfigPayload = (domainPackId: number) => ({
  domainPackId,
  workflowNodes: workflowNodes.value.map((item) => ({ ...item, status: 'active' })),
  testItems: testItems.value.map((item, index) => ({ ...item, sort: (index + 1) * 10, status: 'active' })),
  resultFields: resultFields.value.map((item, index) => ({
    ...item,
    minValue: item.minValue === undefined ? undefined : String(item.minValue),
    maxValue: item.maxValue === undefined ? undefined : String(item.maxValue),
    sort: (index + 1) * 10,
    status: 'active'
  })),
  reportSections: reportSections.value.map((item) => ({ ...item, status: 'active' }))
})

const applyLoadedPackConfig = async (domainPackId: number) => {
  const config = await LabPackConfigApi.getPackConfig(domainPackId)
  if (config.workflowNodes?.length) workflowNodes.value = config.workflowNodes
  if (config.testItems?.length) testItems.value = config.testItems
  if (config.resultFields?.length) {
    resultFields.value = config.resultFields.map((field) => ({
      ...field,
      required: Boolean(field.required),
      minValue: field.minValue === undefined ? undefined : Number(field.minValue),
      maxValue: field.maxValue === undefined ? undefined : Number(field.maxValue),
      demoValue: field.demoValue || ''
    }))
  }
  if (config.reportSections?.length) reportSections.value = config.reportSections
}

const getOrCreateMedicalDomain = async () => {
  const data = await LabDomainProfileApi.getDomainProfilePage({
    pageNo: 1,
    pageSize: 20,
    domainCode: pluginForm.domainCode
  })
  const existing = (data.list || []).find((item) => item.domainCode === pluginForm.domainCode)
  if (existing?.id) return existing
  const id = await LabDomainProfileApi.createDomainProfile({
    domainCode: pluginForm.domainCode,
    domainName: pluginForm.domainName,
    description: '通过医疗行业插件配置中心创建',
    status: 'active'
  })
  return { id, domainCode: pluginForm.domainCode, domainName: pluginForm.domainName, status: 'active' }
}

const loadExistingPack = async () => {
  const data = await LabDomainPackApi.getDomainPackPage({
    pageNo: 1,
    pageSize: 20,
    packCode: pluginForm.packCode
  })
  const pack = (data.list || []).find((item) => item.packCode === pluginForm.packCode)
  if (!pack) {
    message.info('未找到已保存医疗插件，可直接保存新配置')
    return
  }
  activePack.value = pack
  await applyLoadedPackConfig(pack.id!)
  activeStep.value = Math.max(activeStep.value, 1)
  message.success('已加载医疗插件配置')
}

const savePlugin = async () => {
  const domain = await getOrCreateMedicalDomain()
  const payload: LabDomainPackVO = {
    id: activePack.value?.id,
    domainId: domain.id!,
    packCode: pluginForm.packCode,
    packName: pluginForm.packName,
    packVersion: pluginForm.packVersion,
    industry: pluginForm.industry,
    applicationScope: pluginForm.applicationScope,
    workflowSchema: JSON.stringify(buildWorkflowSchema()),
    templateSchema: JSON.stringify(buildTemplateSchema()),
    status: 'active',
    remark: '由医疗行业插件配置中心可视化维护'
  }
  if (payload.id) {
    await LabDomainPackApi.updateDomainPack(payload)
  } else {
    payload.id = await LabDomainPackApi.createDomainPack(payload)
  }
  await LabPackConfigApi.savePackConfig(getPackConfigPayload(payload.id!))
  activePack.value = payload
  activeStep.value = 1
  auditLogs.value.unshift('医疗插件配置已保存到结构化配置表')
  message.success('医疗插件已保存')
}

const ensurePluginSaved = async () => {
  if (!activePack.value?.id) {
    await savePlugin()
  }
  return activePack.value!
}

const createMedicalRequest = async () => {
  const pack = await ensurePluginSaved()
  const requestNo = `MED-REQ-${Date.now().toString().slice(-8)}`
  const id = await LimsWorkflowApi.create('/lims/request', {
    requestNo,
    requestName: '医疗临床检验闭环',
    requestType: 'internal',
    customerName: '检验科',
    requesterName: '门诊医生',
    domainCode: pluginForm.domainCode,
    domainPackId: pack.id,
    priority: 'normal',
    status: 'draft',
    remark: '医疗插件界面验收数据'
  })
  await LimsWorkflowApi.putAction('/lims/request/submit', id)
  await LimsWorkflowApi.putAction('/lims/request/accept', id)
  currentRequest.value = await LimsWorkflowApi.get('/lims/request', id)
  currentReport.value = undefined
  tasks.value = []
  resultPreviewRows.value = []
  activeStep.value = 2
  auditLogs.value.unshift(`检测需求 ${requestNo} 已创建并受理`)
}

const generateMedicalTasks = async () => {
  if (!currentRequest.value?.id) await createMedicalRequest()
  await LimsWorkflowApi.postAction('/lims/request/generate-tasks', currentRequest.value!.id!)
  const data = await LimsWorkflowApi.page('/lims/task', {
    pageNo: 1,
    pageSize: 20,
    requestId: currentRequest.value!.id
  })
  tasks.value = data.list || []
  currentRequest.value = await LimsWorkflowApi.get('/lims/request', currentRequest.value!.id!)
  activeStep.value = 3
  auditLogs.value.unshift('已按医疗检测项目配置生成检测任务')
}

const fieldsForTask = (task: LimsWorkflowVO) => {
  const item = testItems.value.find((config) => config.itemName === task.testItem)
  return resultFields.value.filter((field) => field.itemCode === item?.itemCode)
}

const recordMedicalResults = async () => {
  if (!tasks.value.length) await generateMedicalTasks()
  resultPreviewRows.value = []
  for (const task of tasks.value) {
    await LimsWorkflowApi.putAction('/lims/task/start', task.id!)
    const fields = fieldsForTask(task)
    const values = fields.map((field) => ({
      fieldCode: field.fieldCode,
      fieldName: field.fieldName,
      fieldType: field.fieldType,
      value: field.demoValue,
      unit: field.unit || '',
      conclusion: '合格'
    }))
    const resultId = await LimsWorkflowApi.create('/lims/result', {
      taskId: task.id,
      resultNo: `${task.taskNo}-R`,
      resultValue: values.map((item) => `${item.fieldName}:${item.value}${item.unit}`).join('; '),
      resultUnit: fields[0]?.unit || '',
      resultConclusion: 'pass',
      rawData: JSON.stringify({ resultValues: values })
    })
    await LimsWorkflowApi.putAction('/lims/result/approve', resultId)
    values.forEach((value) => {
      resultPreviewRows.value.push({
        key: `${task.id}-${value.fieldCode}`,
        taskNo: task.taskNo,
        itemName: task.testItem,
        fieldName: value.fieldName,
        displayValue: `${value.value}${value.unit}`,
        conclusion: value.conclusion
      })
    })
  }
  const data = await LimsWorkflowApi.page('/lims/task', {
    pageNo: 1,
    pageSize: 20,
    requestId: currentRequest.value!.id
  })
  tasks.value = data.list || []
  currentRequest.value = await LimsWorkflowApi.get('/lims/request', currentRequest.value!.id!)
  activeStep.value = 4
  auditLogs.value.unshift('动态结果字段已录入并复核')
}

const generateMedicalReport = async () => {
  if (!resultPreviewRows.value.length) await recordMedicalResults()
  const reportId = await LimsWorkflowApi.postAction('/lims/request/generate-report', currentRequest.value!.id!)
  await LimsWorkflowApi.putAction('/lims/report/issue', reportId)
  currentReport.value = await LimsWorkflowApi.get('/lims/report', reportId)
  currentRequest.value = await LimsWorkflowApi.get('/lims/request', currentRequest.value!.id!)
  activeStep.value = 5
  auditLogs.value.unshift('医疗检验报告已生成并签发')
}

onMounted(() => {
  loadExistingPack()
})
</script>

<style scoped>
.metric {
  min-height: 86px;
  border: 1px solid var(--el-border-color-light);
  border-radius: 6px;
  padding: 14px 16px;
  background: var(--el-bg-color);
}

.metric span,
.metric small {
  display: block;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.metric strong {
  display: block;
  margin: 8px 0 4px;
  color: var(--el-text-color-primary);
  font-size: 22px;
  line-height: 1.2;
}

.sub-title {
  margin-bottom: 10px;
  color: var(--el-text-color-primary);
  font-size: 14px;
  font-weight: 600;
}

.report-preview {
  min-height: 170px;
  border: 1px solid var(--el-border-color-light);
  border-radius: 6px;
  padding: 16px;
  background: var(--el-fill-color-extra-light);
}

.report-preview h3 {
  margin: 0 0 10px;
  font-size: 18px;
}

.report-preview p {
  margin: 7px 0;
  color: var(--el-text-color-regular);
}
</style>
