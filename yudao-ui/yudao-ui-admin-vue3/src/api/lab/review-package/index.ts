import request from '@/config/axios'

export interface LabReviewBatchVO {
  id?: number
  batchCode: string
  batchName: string
  domainPackId?: number
  reviewType: string
  standardCodes?: string
  status: string
  remark?: string
  createTime?: string
}

export interface LabReviewItemVO {
  id?: number
  batchId: number
  itemType: string
  standardName?: string
  clauseCode?: string
  clauseCategory?: string
  checkPoint?: string
  expectedEvidence?: string
  ownerRole?: string
  evidenceName?: string
  sourceObject?: string
  linkedObjectNo?: string
  linkStatus?: string
  ncNo?: string
  severity?: string
  description?: string
  owner?: string
  dueDate?: string
  capaNo?: string
  rootCause?: string
  action?: string
  status?: string
  sort?: number
  remark?: string
  createTime?: string
}

export const LabReviewPackageApi = {
  createReviewBatch: async (data: LabReviewBatchVO) => {
    return await request.post({ url: '/lab/review-package/batch/create', data })
  },
  updateReviewBatch: async (data: LabReviewBatchVO) => {
    return await request.put({ url: '/lab/review-package/batch/update', data })
  },
  deleteReviewBatch: async (id: number) => {
    return await request.delete({ url: '/lab/review-package/batch/delete', params: { id } })
  },
  getReviewBatch: async (id: number) => {
    return await request.get({ url: '/lab/review-package/batch/get', params: { id } })
  },
  getReviewBatchPage: async (params: any) => {
    return await request.get({ url: '/lab/review-package/batch/page', params })
  },
  createReviewItem: async (data: LabReviewItemVO) => {
    return await request.post({ url: '/lab/review-package/item/create', data })
  },
  updateReviewItem: async (data: LabReviewItemVO) => {
    return await request.put({ url: '/lab/review-package/item/update', data })
  },
  deleteReviewItem: async (id: number) => {
    return await request.delete({ url: '/lab/review-package/item/delete', params: { id } })
  },
  getReviewItem: async (id: number) => {
    return await request.get({ url: '/lab/review-package/item/get', params: { id } })
  },
  getReviewItemPage: async (params: any) => {
    return await request.get({ url: '/lab/review-package/item/page', params })
  },

  exportChecklist: async (batchCode?: string) => {
    return await request.download({ url: '/lab/review-package/export-checklist', params: { batchCode } })
  },

  exportEvidence: async (batchCode?: string) => {
    return await request.download({ url: '/lab/review-package/export-evidence', params: { batchCode } })
  },

  exportNonconformity: async (batchCode?: string) => {
    return await request.download({ url: '/lab/review-package/export-nc', params: { batchCode } })
  },

  exportCapa: async (batchCode?: string) => {
    return await request.download({ url: '/lab/review-package/export-capa', params: { batchCode } })
  }
}
