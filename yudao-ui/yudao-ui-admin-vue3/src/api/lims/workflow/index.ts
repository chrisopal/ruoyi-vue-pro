import request from '@/config/axios'

export interface LimsWorkflowVO {
  id?: number
  requestNo?: string
  requestName?: string
  requestType?: string
  requestSourceType?: string
  customerName?: string
  requesterName?: string
  requesterOrgId?: number
  costCenterId?: number
  commercialOrderId?: string
  internalProjectNo?: string
  domainCode?: string
  domainPackId?: number
  domainPackCode?: string
  domainPackVersion?: string
  standardId?: number
  priority?: string
  dueDate?: string
  status?: string
  scenarioConfig?: string
  workflowSnapshot?: string
  workflowSnapshotHash?: string
  workflowSnapshotTime?: string
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
  equipmentSnapshot?: string
  equipmentEvidenceSnapshot?: string
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
  eventLogJson?: string
  reviewJson?: string
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
  dataSnapshot?: string
  dataSnapshotHash?: string
  conclusion?: string
  fileUrl?: string
  reportOutput?: string
  issuedTime?: string
  createTime?: string
  locked?: boolean
  conflictReason?: string
  [key: string]: any
}

export interface LimsTaskVO extends LimsWorkflowVO {}

export interface LimsTaskPageReqVO extends PageParam {
  keyword?: string
  requestNo?: string
  requestId?: number
  sampleId?: number
  taskId?: number
  assignedUserId?: number
  equipmentId?: number
  status?: string
  taskStatus?: string
  scheduleStatus?: string
  plannedStartTimeBegin?: string
  plannedStartTimeEnd?: string
  domainPackId?: number
}

export interface LimsTaskSchedulePayload {
  id?: number
  taskId?: number
  assignedUserId?: number
  equipmentId?: number
  equipmentCode?: string
  equipmentName?: string
  plannedStartTime?: string
  plannedEndTime?: string
  durationMinutes?: number
}

export interface LimsTaskScheduleVO extends LimsWorkflowVO {
  id?: number
  taskId?: number
  requestId?: number
  sampleId?: number
  equipmentId?: number
  assignedUserId?: number
  plannedStartTime?: string
  plannedEndTime?: string
  scheduleStatus?: string
  conflictReason?: string
  locked?: boolean
  createTime?: string
}

export interface LimsTaskRawRecordPayload {
  taskId?: number
  recordType?: string
  recordJson?: string
  attachmentUrl?: string
  versionNo?: number
  submittedBy?: number
  status?: string
  remark?: string
  resultValue?: string
  resultUnit?: string
  resultConclusion?: string
  rawData?: string
}

export interface LimsTaskQcRecordPayload {
  taskId?: number
  qcType?: string
  qcRuleSnapshot?: string
  qcDataJson?: string
  qcResult?: string
  reviewComment?: string
}

export interface LimsTaskReviewPayload {
  taskId?: number
  reviewerId?: number
  reviewType?: string
  snapshotHash?: string
  remark?: string
}

export interface LimsTaskReviewVO extends LimsWorkflowVO {
  id?: number
  taskId?: number
  taskNo?: string
  reviewType?: string
  reviewStatus?: string
  reviewerId?: number
  reviewTime?: string
  comment?: string
  snapshotHash?: string
  createTime?: string
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
  putBody: async (url: string, data: LimsWorkflowVO) => request.put({ url, data }),

  getTaskPage: async (params: LimsTaskPageReqVO) =>
    request.get<PageResult<LimsTaskVO[]>>({ url: '/lims/task/page', params }),
  getTask: async (id: number) => request.get<LimsTaskVO>({ url: '/lims/task/get', params: { id } }),
  scheduleTask: async (data: LimsTaskSchedulePayload) =>
    request.post<number>({ url: '/lims/task/schedule', data }),
  scheduleDefaultTask: async (id: number) =>
    request.post<number>({ url: '/lims/task/schedule-default', params: { id } }),
  readyTask: async (id: number) => request.put<boolean>({ url: '/lims/task/ready', params: { id } }),
  startTask: async (id: number) => request.put<boolean>({ url: '/lims/task/start', params: { id } }),
  holdTask: async (id: number, reason?: string) =>
    request.put<boolean>({ url: '/lims/task/hold', params: { id, reason } }),
  submitRawRecord: async (data: LimsTaskRawRecordPayload) =>
    request.post<number>({ url: '/lims/task/raw-record', data }),
  submitQcRecord: async (data: LimsTaskQcRecordPayload) =>
    request.post<number>({ url: '/lims/task/qc-record', data }),
  approveTaskReview: async (data: LimsTaskReviewPayload) =>
    request.put<boolean>({ url: '/lims/task/approve', data }),
  rejectTaskReview: async (data: LimsTaskReviewPayload) =>
    request.put<boolean>({ url: '/lims/task/reject', data }),
  getTaskReviews: async (taskId: number) =>
    request.get<LimsTaskReviewVO[]>({ url: '/lims/task/reviews', params: { taskId } }),
  getTaskSchedulePage: async (params: LimsTaskPageReqVO) =>
    request.get<PageResult<LimsTaskScheduleVO[]>>({ url: '/lims/task/schedule/page', params })
}
