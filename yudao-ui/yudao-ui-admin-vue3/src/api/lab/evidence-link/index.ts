import request from '@/config/axios'

export interface LabEvidenceLinkVO {
  id?: number
  evidenceObjectId?: number
  evidenceCode?: string
  evidenceName?: string
  evidenceUrl?: string
  evidenceHash?: string
  sourceObject?: string
  sourceObjectId?: number
  sourceObjectNo?: string
  linkedBizType: string
  linkedBizId?: number
  linkedBizNo?: string
  clauseId?: number
  capabilityScopeId?: number
  clauseCategory: string
  linkStatus: string
  linkReason?: string
  verifiedBy?: number
  verifiedAt?: string
  remark?: string
  createTime?: string
}

export const LabEvidenceLinkApi = {
  getEvidenceLinkPage: async (params: any) => {
    return await request.get({ url: '/lab/evidence-link/page', params })
  },
  getEvidenceLink: async (id: number) => {
    return await request.get({ url: '/lab/evidence-link/get', params: { id } })
  },
  createEvidenceLink: async (data: LabEvidenceLinkVO) => {
    return await request.post({ url: '/lab/evidence-link/create', data })
  },
  updateEvidenceLink: async (data: LabEvidenceLinkVO) => {
    return await request.put({ url: '/lab/evidence-link/update', data })
  },
  deleteEvidenceLink: async (id: number) => {
    return await request.delete({ url: '/lab/evidence-link/delete', params: { id } })
  },
  getEvidenceLinkList: async (params: { sourceType?: string; sourceId?: number }) => {
    return await request.get({ url: '/lab/evidence-link/list', params })
  },
  uploadEvidenceLink: async (data: LabEvidenceLinkVO) => {
    return await request.post({ url: '/lab/evidence-link/upload', data })
  },
  exportEvidenceLink: async (params: any) => {
    return await request.get({ url: '/lab/evidence-link/export-excel', params })
  }
}
