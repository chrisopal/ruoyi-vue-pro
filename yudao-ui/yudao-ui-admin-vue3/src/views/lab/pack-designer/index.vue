<template>
  <ContentWrap>
    <div class="mb-16px flex items-center justify-between">
      <div>
        <div class="text-18px font-600">检测方向包设计器</div>
        <div class="mt-4px text-12px color-#909399">
          面向食品、工业品、药品、3C、新能源、环境等方向，统一配置流程、项目、结果字段和报告章节
        </div>
      </div>
      <div class="flex flex-wrap gap-8px">
        <el-select
          v-model="selectedTemplateKey"
          class="template-select"
          placeholder="选择方向模板"
          @change="applySelectedTemplate"
        >
          <el-option
            v-for="item in scenarioTemplates"
            :key="item.key"
            :label="item.label"
            :value="item.key"
          />
        </el-select>
        <el-button @click="applySelectedTemplate">
          <Icon class="mr-5px" icon="ep:copy-document" />应用模板
        </el-button>
        <el-button @click="loadExistingPack"><Icon class="mr-5px" icon="ep:refresh" />加载配置</el-button>
        <el-button type="primary" @click="saveDesigner">
          <Icon class="mr-5px" icon="ep:finished" />保存方向包
        </el-button>
      </div>
    </div>

    <el-row :gutter="16">
      <el-col :md="6" :xs="24">
        <div class="metric">
          <span>检测方向</span>
          <strong>{{ packForm.industry }}</strong>
          <small>{{ packForm.packCode }}</small>
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
        <el-form :model="packForm" label-width="112px">
          <el-row :gutter="16">
            <el-col :md="8" :xs="24">
              <el-form-item label="方向编码">
                <el-input v-model="packForm.domainCode" />
              </el-form-item>
            </el-col>
            <el-col :md="8" :xs="24">
              <el-form-item label="方向名称">
                <el-input v-model="packForm.domainName" />
              </el-form-item>
            </el-col>
            <el-col :md="8" :xs="24">
              <el-form-item label="方案版本">
                <el-input v-model="packForm.packVersion" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :md="8" :xs="24">
              <el-form-item label="方案编码">
                <el-input v-model="packForm.packCode" />
              </el-form-item>
            </el-col>
            <el-col :md="8" :xs="24">
              <el-form-item label="方案名称">
                <el-input v-model="packForm.packName" />
              </el-form-item>
            </el-col>
            <el-col :md="8" :xs="24">
              <el-form-item label="行业方向">
                <el-input v-model="packForm.industry" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="适用范围">
            <el-input v-model="packForm.applicationScope" :rows="3" type="textarea" />
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <el-tab-pane label="流程节点" name="workflow">
        <div class="mb-10px flex justify-end">
          <el-button type="primary" plain @click="addWorkflowNode">新增节点</el-button>
        </div>
        <el-table :data="workflowNodes" row-key="nodeCode">
          <el-table-column label="排序" width="120">
            <template #default="scope">
              <el-input-number v-model="scope.row.sort" :min="1" :step="10" controls-position="right" />
            </template>
          </el-table-column>
          <el-table-column label="节点编码" min-width="170">
            <template #default="scope">
              <el-input v-model="scope.row.nodeCode" placeholder="如 result_review" />
            </template>
          </el-table-column>
          <el-table-column label="节点名称" min-width="170">
            <template #default="scope">
              <el-input v-model="scope.row.nodeName" placeholder="节点名称" />
            </template>
          </el-table-column>
          <el-table-column label="处理角色" min-width="170">
            <template #default="scope">
              <el-input v-model="scope.row.roleName" placeholder="处理角色" />
            </template>
          </el-table-column>
          <el-table-column label="是否必经" width="100">
            <template #default="scope">
              <el-switch v-model="scope.row.required" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="90" fixed="right">
            <template #default="scope">
              <el-button link type="danger" @click="removeWorkflowNode(scope.$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="检测项目" name="items">
        <div class="mb-10px flex justify-end">
          <el-button type="primary" plain @click="addTestItem">新增项目</el-button>
        </div>
        <el-table :data="testItems" row-key="itemCode">
          <el-table-column label="项目编码" min-width="160">
            <template #default="scope">
              <el-input v-model="scope.row.itemCode" placeholder="如 COD" />
            </template>
          </el-table-column>
          <el-table-column label="项目名称" min-width="160">
            <template #default="scope">
              <el-input v-model="scope.row.itemName" placeholder="项目名称" />
            </template>
          </el-table-column>
          <el-table-column label="方法编码" min-width="150">
            <template #default="scope">
              <el-input v-model="scope.row.methodCode" placeholder="方法编码" />
            </template>
          </el-table-column>
          <el-table-column label="方法名称" min-width="180">
            <template #default="scope">
              <el-input v-model="scope.row.methodName" placeholder="方法名称" />
            </template>
          </el-table-column>
          <el-table-column label="标准" min-width="140">
            <template #default="scope">
              <el-input v-model="scope.row.standardCode" placeholder="标准号" />
            </template>
          </el-table-column>
          <el-table-column label="默认单位" width="120">
            <template #default="scope">
              <el-input v-model="scope.row.resultUnit" />
            </template>
          </el-table-column>
          <el-table-column label="演示值" width="120">
            <template #default="scope">
              <el-input v-model="scope.row.demoValue" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="90" fixed="right">
            <template #default="scope">
              <el-button link type="danger" @click="removeTestItem(scope.$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="结果字段" name="fields">
        <div class="mb-10px flex justify-end">
          <el-button type="primary" plain @click="addResultField">新增字段</el-button>
        </div>
        <el-table :data="resultFields" row-key="fieldCode">
          <el-table-column label="检测项目" min-width="170">
            <template #default="scope">
              <el-select v-model="scope.row.itemCode" placeholder="选择项目">
                <el-option
                  v-for="item in testItems"
                  :key="item.itemCode"
                  :label="item.itemName"
                  :value="item.itemCode"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="字段编码" min-width="160">
            <template #default="scope">
              <el-input v-model="scope.row.fieldCode" placeholder="字段编码" />
            </template>
          </el-table-column>
          <el-table-column label="字段名称" min-width="160">
            <template #default="scope">
              <el-input v-model="scope.row.fieldName" placeholder="字段名称" />
            </template>
          </el-table-column>
          <el-table-column label="字段类型" width="140">
            <template #default="scope">
              <el-select v-model="scope.row.fieldType">
                <el-option label="数值" value="number" />
                <el-option label="文本" value="text" />
                <el-option label="选项" value="select" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="单位" width="110">
            <template #default="scope">
              <el-input v-model="scope.row.unit" />
            </template>
          </el-table-column>
          <el-table-column label="必填" width="90">
            <template #default="scope">
              <el-switch v-model="scope.row.required" />
            </template>
          </el-table-column>
          <el-table-column label="最小值" width="110">
            <template #default="scope">
              <el-input v-model="scope.row.minValue" />
            </template>
          </el-table-column>
          <el-table-column label="最大值" width="110">
            <template #default="scope">
              <el-input v-model="scope.row.maxValue" />
            </template>
          </el-table-column>
          <el-table-column label="选项" min-width="180">
            <template #default="scope">
              <el-input
                :model-value="formatEnumOptions(scope.row.enumOptions)"
                placeholder="逗号分隔"
                @update:model-value="(value) => updateEnumOptions(scope.row, value)"
              />
            </template>
          </el-table-column>
          <el-table-column label="演示值" width="120">
            <template #default="scope">
              <el-input v-model="scope.row.demoValue" />
            </template>
          </el-table-column>
          <el-table-column label="规则预览" min-width="160">
            <template #default="scope">{{ describeFieldRule(scope.row) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="90" fixed="right">
            <template #default="scope">
              <el-button link type="danger" @click="removeResultField(scope.$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="报告章节" name="report">
        <div class="mb-10px flex justify-end">
          <el-button type="primary" plain @click="addReportSection">新增章节</el-button>
        </div>
        <el-table :data="reportSections" row-key="sectionCode">
          <el-table-column label="排序" width="120">
            <template #default="scope">
              <el-input-number v-model="scope.row.sort" :min="1" :step="10" controls-position="right" />
            </template>
          </el-table-column>
          <el-table-column label="章节编码" min-width="160">
            <template #default="scope">
              <el-input v-model="scope.row.sectionCode" placeholder="章节编码" />
            </template>
          </el-table-column>
          <el-table-column label="章节名称" min-width="170">
            <template #default="scope">
              <el-input v-model="scope.row.sectionName" placeholder="章节名称" />
            </template>
          </el-table-column>
          <el-table-column label="数据来源" min-width="160">
            <template #default="scope">
              <el-select v-model="scope.row.sourceType">
                <el-option label="需求" value="request" />
                <el-option label="样品" value="sample" />
                <el-option label="检测项目" value="test_items" />
                <el-option label="结果实例" value="result_values" />
                <el-option label="规则" value="rules" />
                <el-option label="审批" value="approval" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="是否显示" width="100">
            <template #default="scope">
              <el-switch v-model="scope.row.visible" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="90" fixed="right">
            <template #default="scope">
              <el-button link type="danger" @click="removeReportSection(scope.$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </ContentWrap>

  <ContentWrap>
    <div class="mb-16px flex items-center justify-between">
      <div>
        <div class="text-16px font-600">检测方向执行工作台</div>
        <div class="mt-4px text-12px color-#909399">
          使用当前方向包配置，创建真实检测需求并完成任务、结果和报告签发
        </div>
      </div>
      <div class="flex flex-wrap gap-8px">
        <el-button type="primary" @click="createTestRequest">创建检测需求</el-button>
        <el-button @click="generateTasks">生成任务</el-button>
        <el-button @click="recordResults">录入动态结果</el-button>
        <el-button @click="generateReport">生成并签发报告</el-button>
      </div>
    </div>

    <el-steps :active="activeStep" finish-status="success" simple>
      <el-step title="方案配置" />
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
          <h3>{{ currentReport?.reportName || `${packForm.industry}检测报告预览` }}</h3>
          <p>报告编号：{{ currentReport?.reportNo || '待生成' }}</p>
          <p>方向包：{{ packForm.packName }}</p>
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

defineOptions({ name: 'LabPackDesigner' })

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
  minValue?: number | string
  maxValue?: number | string
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

interface PackForm {
  domainCode: string
  domainName: string
  packCode: string
  packName: string
  packVersion: string
  industry: string
  applicationScope: string
}

interface ScenarioTemplate {
  key: string
  label: string
  form: PackForm
  workflowNodes: WorkflowNode[]
  testItems: TestItem[]
  resultFields: ResultField[]
  reportSections: ReportSection[]
}

const scenarioTemplates: ScenarioTemplate[] = [
  {
    key: 'medical',
    label: '医疗临床检验',
    form: {
      domainCode: 'MEDICAL',
      domainName: '医疗检验',
      packCode: 'MEDICAL_CLINICAL_V1',
      packName: '医疗临床检验方案包',
      packVersion: '1.0',
      industry: '医疗',
      applicationScope: '血液、炎症、凝血等临床检验场景，支持动态结果字段、复核和报告签发'
    },
    workflowNodes: [
      { sort: 10, nodeCode: 'request', nodeName: '检验申请', roleName: '门诊/科室', required: true },
      { sort: 20, nodeCode: 'sample_receive', nodeName: '样本接收', roleName: '样本室', required: true },
      { sort: 30, nodeCode: 'analysis', nodeName: '仪器检测', roleName: '检验技师', required: true },
      { sort: 40, nodeCode: 'result_review', nodeName: '结果复核', roleName: '审核医生', required: true },
      { sort: 50, nodeCode: 'report_issue', nodeName: '报告签发', roleName: '授权签字人', required: true }
    ],
    testItems: [
      { itemCode: 'CBC_WBC', itemName: '白细胞计数', methodCode: 'MED-CBC-001', methodName: '血细胞分析法', standardCode: 'WS/T 406', resultUnit: '10^9/L', demoValue: '6.4' },
      { itemCode: 'CRP', itemName: 'C反应蛋白', methodCode: 'MED-CRP-001', methodName: '免疫比浊法', standardCode: 'YY/T 1454', resultUnit: 'mg/L', demoValue: '4.8' },
      { itemCode: 'D_DIMER', itemName: 'D-二聚体', methodCode: 'MED-DD-001', methodName: '免疫荧光法', standardCode: 'WS/T 477', resultUnit: 'mg/L FEU', demoValue: '0.32' }
    ],
    resultFields: [
      { itemCode: 'CBC_WBC', fieldCode: 'wbc_value', fieldName: '白细胞结果', fieldType: 'number', unit: '10^9/L', required: true, minValue: 3.5, maxValue: 9.5, demoValue: '6.4' },
      { itemCode: 'CBC_WBC', fieldCode: 'wbc_flag', fieldName: '白细胞提示', fieldType: 'select', required: true, enumOptions: ['正常', '偏高', '偏低'], demoValue: '正常' },
      { itemCode: 'CRP', fieldCode: 'crp_value', fieldName: 'CRP结果', fieldType: 'number', unit: 'mg/L', required: true, minValue: 0, maxValue: 10, demoValue: '4.8' },
      { itemCode: 'D_DIMER', fieldCode: 'ddimer_value', fieldName: 'D-二聚体结果', fieldType: 'number', unit: 'mg/L FEU', required: true, minValue: 0, maxValue: 0.5, demoValue: '0.32' }
    ],
    reportSections: [
      { sort: 10, sectionCode: 'patientInfo', sectionName: '患者/申请信息', sourceType: 'request', visible: true },
      { sort: 20, sectionCode: 'sampleInfo', sectionName: '样本信息', sourceType: 'sample', visible: true },
      { sort: 30, sectionCode: 'resultTable', sectionName: '检验结果', sourceType: 'result_values', visible: true },
      { sort: 40, sectionCode: 'clinicalHint', sectionName: '临床提示', sourceType: 'rules', visible: true },
      { sort: 50, sectionCode: 'sign', sectionName: '审核签发', sourceType: 'approval', visible: true }
    ]
  },
  {
    key: 'food',
    label: '食品理化检测',
    form: {
      domainCode: 'FOOD',
      domainName: '食品检测',
      packCode: 'FOOD_ROUTINE_DESIGNER_V1',
      packName: '食品理化常规检测方案包',
      packVersion: '1.0',
      industry: '食品',
      applicationScope: '适用于食品样品受理、感官检查、水分、酸价等理化项目检测和报告签发'
    },
    workflowNodes: [
      { sort: 10, nodeCode: 'sample_register', nodeName: '样品登记', roleName: '客服/收样员', required: true },
      { sort: 20, nodeCode: 'sample_prepare', nodeName: '制样前处理', roleName: '前处理人员', required: true },
      { sort: 30, nodeCode: 'test_execute', nodeName: '理化检测', roleName: '检测员', required: true },
      { sort: 40, nodeCode: 'quality_review', nodeName: '质量复核', roleName: '技术负责人', required: true },
      { sort: 50, nodeCode: 'report_issue', nodeName: '报告签发', roleName: '授权签字人', required: true }
    ],
    testItems: [
      { itemCode: 'MOISTURE', itemName: '水分', methodCode: 'GB5009.3', methodName: '直接干燥法', standardCode: 'GB 5009.3', resultUnit: '%', demoValue: '12.4' },
      { itemCode: 'ACID_VALUE', itemName: '酸价', methodCode: 'GB5009.229', methodName: '电位滴定法', standardCode: 'GB 5009.229', resultUnit: 'mg/g', demoValue: '1.2' },
      { itemCode: 'SENSORY', itemName: '感官检查', methodCode: 'FOOD-SENSE', methodName: '感官评价法', standardCode: 'GB 2716', resultUnit: '', demoValue: '正常' }
    ],
    resultFields: [
      { itemCode: 'MOISTURE', fieldCode: 'moisture_value', fieldName: '水分结果', fieldType: 'number', unit: '%', required: true, minValue: 0, maxValue: 20, demoValue: '12.4' },
      { itemCode: 'ACID_VALUE', fieldCode: 'acid_value', fieldName: '酸价结果', fieldType: 'number', unit: 'mg/g', required: true, minValue: 0, maxValue: 3, demoValue: '1.2' },
      { itemCode: 'SENSORY', fieldCode: 'sensory_result', fieldName: '感官结论', fieldType: 'select', required: true, enumOptions: ['正常', '异味', '变色'], demoValue: '正常' }
    ],
    reportSections: [
      { sort: 10, sectionCode: 'sampleInfo', sectionName: '样品信息', sourceType: 'sample', visible: true },
      { sort: 20, sectionCode: 'methodInfo', sectionName: '检测依据', sourceType: 'test_items', visible: true },
      { sort: 30, sectionCode: 'resultTable', sectionName: '检测结果', sourceType: 'result_values', visible: true },
      { sort: 40, sectionCode: 'qualityStatement', sectionName: '质量声明', sourceType: 'rules', visible: true },
      { sort: 50, sectionCode: 'sign', sectionName: '批准签发', sourceType: 'approval', visible: true }
    ]
  },
  {
    key: 'environment',
    label: '环境水质检测',
    form: {
      domainCode: 'ENVIRONMENT',
      domainName: '环境检测',
      packCode: 'ENV_WATER_DESIGNER_V1',
      packName: '环境水质常规检测方案包',
      packVersion: '1.0',
      industry: '环境',
      applicationScope: '适用于地表水、污水、地下水样品的 pH、COD、氨氮等常规项目检测'
    },
    workflowNodes: [
      { sort: 10, nodeCode: 'field_sampling', nodeName: '现场采样', roleName: '采样员', required: true },
      { sort: 20, nodeCode: 'sample_transfer', nodeName: '样品交接', roleName: '样品管理员', required: true },
      { sort: 30, nodeCode: 'lab_analysis', nodeName: '实验分析', roleName: '分析员', required: true },
      { sort: 40, nodeCode: 'qa_review', nodeName: '质控审核', roleName: '质控员', required: true },
      { sort: 50, nodeCode: 'report_issue', nodeName: '报告签发', roleName: '授权签字人', required: true }
    ],
    testItems: [
      { itemCode: 'PH', itemName: 'pH', methodCode: 'HJ-1147', methodName: '电极法', standardCode: 'HJ 1147', resultUnit: '', demoValue: '7.2' },
      { itemCode: 'COD', itemName: 'COD', methodCode: 'HJ-828', methodName: '重铬酸盐法', standardCode: 'HJ 828', resultUnit: 'mg/L', demoValue: '18' },
      { itemCode: 'NH3_N', itemName: '氨氮', methodCode: 'HJ-535', methodName: '纳氏试剂分光光度法', standardCode: 'HJ 535', resultUnit: 'mg/L', demoValue: '0.42' }
    ],
    resultFields: [
      { itemCode: 'PH', fieldCode: 'ph_value', fieldName: 'pH结果', fieldType: 'number', required: true, minValue: 6, maxValue: 9, demoValue: '7.2' },
      { itemCode: 'COD', fieldCode: 'cod_value', fieldName: 'COD结果', fieldType: 'number', unit: 'mg/L', required: true, minValue: 0, maxValue: 50, demoValue: '18' },
      { itemCode: 'NH3_N', fieldCode: 'nh3n_value', fieldName: '氨氮结果', fieldType: 'number', unit: 'mg/L', required: true, minValue: 0, maxValue: 1, demoValue: '0.42' }
    ],
    reportSections: [
      { sort: 10, sectionCode: 'samplingInfo', sectionName: '采样信息', sourceType: 'request', visible: true },
      { sort: 20, sectionCode: 'sampleInfo', sectionName: '样品交接', sourceType: 'sample', visible: true },
      { sort: 30, sectionCode: 'resultTable', sectionName: '分析结果', sourceType: 'result_values', visible: true },
      { sort: 40, sectionCode: 'qaInfo', sectionName: '质控信息', sourceType: 'rules', visible: true },
      { sort: 50, sectionCode: 'sign', sectionName: '审核签发', sourceType: 'approval', visible: true }
    ]
  },
  {
    key: 'industrial',
    label: '工业品可靠性',
    form: {
      domainCode: 'INDUSTRIAL',
      domainName: '工业品检测',
      packCode: 'IND_RELIABILITY_DESIGNER_V1',
      packName: '工业品可靠性检测方案包',
      packVersion: '1.0',
      industry: '工业品',
      applicationScope: '适用于尺寸、外观、温湿度循环、振动等工业品可靠性项目检测'
    },
    workflowNodes: [
      { sort: 10, nodeCode: 'order_review', nodeName: '需求评审', roleName: '项目工程师', required: true },
      { sort: 20, nodeCode: 'sample_confirm', nodeName: '样品确认', roleName: '样品管理员', required: true },
      { sort: 30, nodeCode: 'test_execute', nodeName: '可靠性测试', roleName: '测试工程师', required: true },
      { sort: 40, nodeCode: 'result_review', nodeName: '结果判定', roleName: '技术负责人', required: true },
      { sort: 50, nodeCode: 'report_issue', nodeName: '报告签发', roleName: '授权签字人', required: true }
    ],
    testItems: [
      { itemCode: 'DIMENSION', itemName: '尺寸检查', methodCode: 'DIM-001', methodName: '卡尺/三坐标测量', standardCode: 'GB/T 2828.1', resultUnit: 'mm', demoValue: '10.02' },
      { itemCode: 'TEMP_CYCLE', itemName: '温湿度循环', methodCode: 'REL-TH-001', methodName: '温湿度循环试验', standardCode: 'GB/T 2423', resultUnit: 'cycle', demoValue: '10' },
      { itemCode: 'VIBRATION', itemName: '振动试验', methodCode: 'REL-VIB-001', methodName: '扫频振动试验', standardCode: 'GB/T 2423.10', resultUnit: 'h', demoValue: '2' }
    ],
    resultFields: [
      { itemCode: 'DIMENSION', fieldCode: 'dimension_value', fieldName: '关键尺寸', fieldType: 'number', unit: 'mm', required: true, minValue: 9.8, maxValue: 10.2, demoValue: '10.02' },
      { itemCode: 'TEMP_CYCLE', fieldCode: 'temp_cycle_result', fieldName: '循环后状态', fieldType: 'select', required: true, enumOptions: ['正常', '开裂', '变形'], demoValue: '正常' },
      { itemCode: 'VIBRATION', fieldCode: 'vibration_result', fieldName: '振动后状态', fieldType: 'select', required: true, enumOptions: ['正常', '松动', '失效'], demoValue: '正常' }
    ],
    reportSections: [
      { sort: 10, sectionCode: 'sampleInfo', sectionName: '样品与工况', sourceType: 'sample', visible: true },
      { sort: 20, sectionCode: 'testCondition', sectionName: '测试条件', sourceType: 'test_items', visible: true },
      { sort: 30, sectionCode: 'resultTable', sectionName: '结果判定', sourceType: 'result_values', visible: true },
      { sort: 40, sectionCode: 'deviation', sectionName: '偏离说明', sourceType: 'rules', visible: true },
      { sort: 50, sectionCode: 'sign', sectionName: '审核签发', sourceType: 'approval', visible: true }
    ]
  }
]

const clone = <T>(value: T): T => JSON.parse(JSON.stringify(value))
const defaultTemplate = scenarioTemplates[0]

const message = useMessage()
const activeTab = ref('pack')
const activeStep = ref(0)
const selectedTemplateKey = ref(defaultTemplate.key)
const activePack = ref<LabDomainPackVO>()
const currentRequest = ref<LimsWorkflowVO>()
const currentReport = ref<LimsWorkflowVO>()
const tasks = ref<LimsWorkflowVO[]>([])
const resultPreviewRows = ref<any[]>([])
const auditLogs = ref<string[]>([])

const packForm = reactive<PackForm>(clone(defaultTemplate.form))
const workflowNodes = ref<WorkflowNode[]>(clone(defaultTemplate.workflowNodes))
const testItems = ref<TestItem[]>(clone(defaultTemplate.testItems))
const resultFields = ref<ResultField[]>(clone(defaultTemplate.resultFields))
const reportSections = ref<ReportSection[]>(clone(defaultTemplate.reportSections))

const describeFieldRule = (field: ResultField) => {
  if (field.enumOptions?.length) return field.enumOptions.join(' / ')
  if (field.minValue !== undefined || field.maxValue !== undefined) {
    return `${field.minValue ?? '-∞'} ~ ${field.maxValue ?? '+∞'}${field.unit || ''}`
  }
  return '-'
}

const resetExecutionState = () => {
  activePack.value = undefined
  currentRequest.value = undefined
  currentReport.value = undefined
  tasks.value = []
  resultPreviewRows.value = []
  activeStep.value = 0
}

const applySelectedTemplate = () => {
  const template = scenarioTemplates.find((item) => item.key === selectedTemplateKey.value) || defaultTemplate
  Object.assign(packForm, clone(template.form))
  workflowNodes.value = clone(template.workflowNodes)
  testItems.value = clone(template.testItems)
  resultFields.value = clone(template.resultFields)
  reportSections.value = clone(template.reportSections)
  auditLogs.value.unshift(`已应用${template.label}方向模板`)
  resetExecutionState()
}

const buildRequestNo = () =>
  `${packForm.domainCode || 'LAB'}-REQ-${Date.now().toString().slice(-8)}`

const nextSort = (rows: Array<{ sort?: number }>) =>
  Math.max(0, ...rows.map((item) => item.sort || 0)) + 10

const addWorkflowNode = () => {
  workflowNodes.value.push({
    sort: nextSort(workflowNodes.value),
    nodeCode: `node_${workflowNodes.value.length + 1}`,
    nodeName: '新流程节点',
    roleName: '处理角色',
    required: true
  })
}

const removeWorkflowNode = (index: number) => {
  workflowNodes.value.splice(index, 1)
}

const addTestItem = () => {
  const index = testItems.value.length + 1
  testItems.value.push({
    itemCode: `ITEM_${index}`,
    itemName: '新检测项目',
    methodCode: `METHOD-${index}`,
    methodName: '检测方法',
    standardCode: '',
    resultUnit: '',
    demoValue: '合格'
  })
}

const removeTestItem = (index: number) => {
  const [removed] = testItems.value.splice(index, 1)
  if (removed?.itemCode) {
    resultFields.value = resultFields.value.filter((field) => field.itemCode !== removed.itemCode)
  }
}

const addResultField = () => {
  const index = resultFields.value.length + 1
  const itemCode = testItems.value[0]?.itemCode || ''
  resultFields.value.push({
    itemCode,
    fieldCode: `field_${index}`,
    fieldName: '新结果字段',
    fieldType: 'text',
    required: true,
    demoValue: '合格'
  })
}

const removeResultField = (index: number) => {
  resultFields.value.splice(index, 1)
}

const addReportSection = () => {
  const index = reportSections.value.length + 1
  reportSections.value.push({
    sort: nextSort(reportSections.value),
    sectionCode: `section_${index}`,
    sectionName: '新报告章节',
    sourceType: 'result_values',
    visible: true
  })
}

const removeReportSection = (index: number) => {
  reportSections.value.splice(index, 1)
}

const formatEnumOptions = (options?: string[]) => options?.join(',') || ''

const updateEnumOptions = (field: ResultField, value: string) => {
  field.enumOptions = value
    .split(',')
    .map((item) => item.trim())
    .filter(Boolean)
}

const buildWorkflowSchema = () => ({
  stages: workflowNodes.value.map((item) => item.nodeCode),
  workflowNodes: workflowNodes.value,
  samplePolicy: {
    needSampling: true,
    sampleFields: ['样品名称', '样品编号', '样品类型', '接收时间', '委托部门']
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
  templates: [
    `${packForm.domainCode.toLowerCase()}_sample_label`,
    `${packForm.domainCode.toLowerCase()}_raw_record`,
    `${packForm.domainCode.toLowerCase()}_report`
  ],
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

const getOrCreateDomain = async () => {
  const data = await LabDomainProfileApi.getDomainProfilePage({
    pageNo: 1,
    pageSize: 20,
    domainCode: packForm.domainCode
  })
  const existing = (data.list || []).find((item) => item.domainCode === packForm.domainCode)
  if (existing?.id) return existing
  const id = await LabDomainProfileApi.createDomainProfile({
    domainCode: packForm.domainCode,
    domainName: packForm.domainName,
    description: '通过检测方向包设计器创建',
    status: 'active'
  })
  return { id, domainCode: packForm.domainCode, domainName: packForm.domainName, status: 'active' }
}

const loadExistingPack = async () => {
  const data = await LabDomainPackApi.getDomainPackPage({
    pageNo: 1,
    pageSize: 20,
    packCode: packForm.packCode
  })
  const pack = (data.list || []).find((item) => item.packCode === packForm.packCode)
  if (!pack) {
    message.info('未找到已保存方向包，可直接保存新配置')
    return
  }
  activePack.value = pack
  Object.assign(packForm, {
    packCode: pack.packCode,
    packName: pack.packName,
    packVersion: pack.packVersion,
    industry: pack.industry || packForm.industry,
    applicationScope: pack.applicationScope || packForm.applicationScope
  })
  await applyLoadedPackConfig(pack.id!)
  activeStep.value = Math.max(activeStep.value, 1)
  message.success('已加载方向包配置')
}

const saveDesigner = async () => {
  const domain = await getOrCreateDomain()
  const payload: LabDomainPackVO = {
    id: activePack.value?.id,
    domainId: domain.id!,
    packCode: packForm.packCode,
    packName: packForm.packName,
    packVersion: packForm.packVersion,
    industry: packForm.industry,
    applicationScope: packForm.applicationScope,
    workflowSchema: JSON.stringify(buildWorkflowSchema()),
    templateSchema: JSON.stringify(buildTemplateSchema()),
    status: 'active',
    remark: '由检测方向包设计器可视化维护'
  }
  if (payload.id) {
    await LabDomainPackApi.updateDomainPack(payload)
  } else {
    payload.id = await LabDomainPackApi.createDomainPack(payload)
  }
  await LabPackConfigApi.savePackConfig(getPackConfigPayload(payload.id!))
  activePack.value = payload
  activeStep.value = 1
  auditLogs.value.unshift(`${packForm.packName} 已保存到结构化配置表`)
  message.success('方向包已保存')
}

const ensureDesignerSaved = async () => {
  if (!activePack.value?.id) {
    await saveDesigner()
  }
  return activePack.value!
}

const createTestRequest = async () => {
  const pack = await ensureDesignerSaved()
  const requestNo = buildRequestNo()
  const id = await LimsWorkflowApi.create('/lims/request', {
    requestNo,
    requestName: `${packForm.industry}检测闭环`,
    requestType: 'internal',
    customerName: `${packForm.industry}实验室`,
    requesterName: '检测需求发起人',
    domainCode: packForm.domainCode,
    domainPackId: pack.id,
    priority: 'normal',
    status: 'draft',
    remark: '检测方向包设计器界面验收数据'
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

const generateTasks = async () => {
  if (!currentRequest.value?.id) await createTestRequest()
  await LimsWorkflowApi.postAction('/lims/request/generate-tasks', currentRequest.value!.id!)
  const data = await LimsWorkflowApi.page('/lims/task', {
    pageNo: 1,
    pageSize: 20,
    requestId: currentRequest.value!.id
  })
  tasks.value = data.list || []
  currentRequest.value = await LimsWorkflowApi.get('/lims/request', currentRequest.value!.id!)
  activeStep.value = 3
  auditLogs.value.unshift(`已按${packForm.industry}检测项目配置生成检测任务`)
}

const fieldsForTask = (task: LimsWorkflowVO) => {
  const item = testItems.value.find((config) => config.itemName === task.testItem)
  return resultFields.value.filter((field) => field.itemCode === item?.itemCode)
}

const recordResults = async () => {
  if (!tasks.value.length) await generateTasks()
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

const generateReport = async () => {
  if (!resultPreviewRows.value.length) await recordResults()
  const reportId = await LimsWorkflowApi.postAction('/lims/request/generate-report', currentRequest.value!.id!)
  await LimsWorkflowApi.putAction('/lims/report/issue', reportId)
  currentReport.value = await LimsWorkflowApi.get('/lims/report', reportId)
  currentRequest.value = await LimsWorkflowApi.get('/lims/request', currentRequest.value!.id!)
  activeStep.value = 5
  auditLogs.value.unshift(`${packForm.industry}检测报告已生成并签发`)
}

onMounted(() => {
  applySelectedTemplate()
})
</script>

<style scoped>
.template-select {
  width: 180px;
}

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
