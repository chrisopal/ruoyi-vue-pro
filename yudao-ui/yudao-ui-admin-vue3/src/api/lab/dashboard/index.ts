import request from '@/config/axios'

export interface LabDashboardMetric {
  code: string
  label: string
  value: number
  unit?: string
  level?: string
  hint?: string
}

export interface LabDashboardWorkflowStage {
  code: string
  label: string
  total: number
  done: number
  blocked: number
  completionRate: number
}

export interface LabDashboardCoverageItem {
  domainCode: string
  domainName: string
  publishedPackCount: number
  enabledEquipmentCount: number
  taskCount: number
  reportCount: number
  coverageRate: number
}

export interface LabDashboardRiskItem {
  code: string
  title: string
  description: string
  level: string
  ownerContext: string
  relatedCount: number
  actionText: string
}

export interface LabDashboardRecentReport {
  reportNo?: string
  requestNo?: string
  reportName?: string
  status?: string
  templateVersion?: string
  fileUrl?: string
  issuedTime?: string
}

export interface LabOperationsDashboardVO {
  applicationReadinessScore: number
  reassessmentRiskScore: number
  capabilityCoverageRate: number
  correctionClosureRate: number
  metrics: LabDashboardMetric[]
  workflowStages: LabDashboardWorkflowStage[]
  capabilityCoverage: LabDashboardCoverageItem[]
  risks: LabDashboardRiskItem[]
  recentReports: LabDashboardRecentReport[]
}

export const LabDashboardApi = {
  getOperationsDashboard: async () => {
    return await request.get<LabOperationsDashboardVO>({ url: '/lims/dashboard/operations' })
  }
}
