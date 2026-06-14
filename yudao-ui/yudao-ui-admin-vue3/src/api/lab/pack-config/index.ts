import request from '@/config/axios'

export interface WorkflowNodeVO {
  nodeCode: string
  nodeName: string
  roleName?: string
  required?: boolean
  sort?: number
  status?: string
}

export interface TestItemVO {
  itemCode: string
  itemName: string
  methodCode?: string
  methodName?: string
  standardCode?: string
  resultUnit?: string
  demoValue?: string
  sort?: number
  status?: string
}

export interface ResultFieldVO {
  itemCode: string
  fieldCode: string
  fieldName: string
  fieldType: string
  unit?: string
  required?: boolean
  minValue?: string | number
  maxValue?: string | number
  enumOptions?: string[]
  demoValue?: string
  sort?: number
  status?: string
}

export interface ReportSectionVO {
  sectionCode: string
  sectionName: string
  sourceType?: string
  visible?: boolean
  sort?: number
  status?: string
}

export interface SampleRequirementVO {
  requirementCode: string
  requirementName: string
  requirementType?: string
  requirementText?: string
  sort?: number
  status?: string
}

export interface QcRuleVO {
  ruleCode: string
  ruleName: string
  ruleType?: string
  ruleExpression?: string
  acceptanceCriteria?: string
  sort?: number
  status?: string
}

export interface EvidenceRequirementVO {
  requirementCode: string
  requirementName: string
  evidenceType?: string
  sourceType?: string
  clauseCategory?: string
  required?: boolean
  sort?: number
  status?: string
}

export interface LabPackConfigVO {
  domainPackId: number
  workflowNodes: WorkflowNodeVO[]
  testItems: TestItemVO[]
  resultFields: ResultFieldVO[]
  reportSections: ReportSectionVO[]
  sampleRequirements?: SampleRequirementVO[]
  qcRules?: QcRuleVO[]
  evidenceRequirements?: EvidenceRequirementVO[]
}

export const LabPackConfigApi = {
  getPackConfig: async (domainPackId: number) => {
    return await request.get({ url: '/lab/pack-config/get', params: { domainPackId } })
  },

  savePackConfig: async (data: LabPackConfigVO) => {
    return await request.post({ url: '/lab/pack-config/save', data })
  }
}
