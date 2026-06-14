import request from '@/config/axios'

export interface LimsWorkflowVO {
  id?: number
  requestNo?: string
  requestName?: string
  requestType?: string
  customerName?: string
  requesterName?: string
  domainCode?: string
  domainPackId?: number
  domainPackCode?: string
  standardId?: number
  priority?: string
  dueDate?: string
  status?: string
  scenarioConfig?: string
  remark?: string
  requestId?: number
  sampleNo?: string
  sampleName?: string
  sampleType?: string
  sampleSpec?: string
  sampleQty?: string
  receivedDate?: string
  storageCondition?: string
  sampleId?: number
  taskNo?: string
  taskName?: string
  testItem?: string
  methodCode?: string
  methodName?: string
  standardClauseId?: number
  assignedUserId?: number
  equipmentId?: number
  equipmentCode?: string
  equipmentName?: string
  plannedStartTime?: string
  plannedEndTime?: string
  taskStatus?: string
  scheduleStatus?: string
  actualStartTime?: string
  actualEndTime?: string
  durationMinutes?: number
  methodSnapshot?: string
  readinessSnapshot?: string
  qcStatus?: string
  reviewStatus?: string
  reportEligible?: boolean
  blockReason?: string
  recordType?: string
  recordJson?: string
  attachmentUrl?: string
  versionNo?: number
  submittedBy?: number
  submittedTime?: string
  qcType?: string
  qcRuleSnapshot?: string
  qcDataJson?: string
  qcResult?: string
  reviewComment?: string
  reviewType?: string
  reviewTime?: string
  comment?: string
  snapshotHash?: string
  taskId?: number
  resultNo?: string
  resultValue?: string
  resultUnit?: string
  resultConclusion?: string
  rawData?: string
  reviewerId?: number
  reviewedTime?: string
  reportNo?: string
  reportName?: string
  templateId?: number
  templateVersion?: string
  reportContent?: string
  conclusion?: string
  fileUrl?: string
  reportOutput?: string
  issuedTime?: string
  [key: string]: any
}

export const LimsWorkflowApi = {
  page: async (baseUrl: string, params: any) => request.get({ url: baseUrl + '/page', params }),
  get: async (baseUrl: string, id: number) => request.get({ url: baseUrl + '/get', params: { id } }),
  create: async (baseUrl: string, data: LimsWorkflowVO) => request.post({ url: baseUrl + '/create', data }),
  update: async (baseUrl: string, data: LimsWorkflowVO) => request.put({ url: baseUrl + '/update', data }),
  delete: async (baseUrl: string, id: number) => request.delete({ url: baseUrl + '/delete', params: { id } }),
  postAction: async (url: string, id: number) => request.post({ url, params: { id } }),
  putAction: async (url: string, id: number) => request.put({ url, params: { id } }),
  postBody: async (url: string, data: LimsWorkflowVO) => request.post({ url, data }),
  putBody: async (url: string, data: LimsWorkflowVO) => request.put({ url, data })
}
