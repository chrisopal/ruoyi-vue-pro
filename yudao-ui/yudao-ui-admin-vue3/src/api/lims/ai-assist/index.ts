import request from '@/config/axios'

export interface LimsAiKnowledgeMetric {
  code: string
  label: string
  value: number
  unit?: string
  hint?: string
}

export interface LimsAiClauseHit {
  clauseId?: number
  clauseCode?: string
  clauseTitle?: string
  clauseCategory?: string
  requirementText?: string
  evidenceTypeCodes?: string
}

export interface LimsAiStandardAnswer {
  question?: string
  answer?: string
  confidence?: number
  matchedClauses: LimsAiClauseHit[]
}

export interface LimsAiEvidenceGap {
  code: string
  title: string
  severity: string
  message: string
  actionText: string
  sourceType?: string
  sourceNo?: string
  clauseCategory?: string
}

export interface LimsAiReportInterpretation {
  reportNo?: string
  status?: string
  conclusion?: string
  interpretation?: string
  outputFormats: string[]
  riskLevel?: string
}

export interface LimsAiDataInterpretation {
  taskNo?: string
  testItem?: string
  methodName?: string
  taskStatus?: string
  missingRequirementCount?: number
  qualityGateSatisfied?: boolean
  interpretation?: string
}

export interface LimsAiSmartRecommendation {
  code: string
  title: string
  priority: string
  content: string
}

export interface LimsAiAssistCenterVO {
  requestId?: number
  requestNo?: string
  requestName?: string
  domainCode?: string
  domainPackCode?: string
  domainPackVersion?: string
  metrics: LimsAiKnowledgeMetric[]
  standardAnswer?: LimsAiStandardAnswer
  evidenceGaps: LimsAiEvidenceGap[]
  reportInterpretation?: LimsAiReportInterpretation
  dataInterpretations: LimsAiDataInterpretation[]
  recommendations: LimsAiSmartRecommendation[]
}

export const LimsAiAssistApi = {
  getCenter: async (params: { requestId?: number; question?: string }) => {
    return await request.get<LimsAiAssistCenterVO>({ url: '/lims/ai-assist/center', params })
  }
}
