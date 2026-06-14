import request from '@/config/axios'

export interface LabEvidenceObjectVO {
  id?: number
  evidenceCode: string
  evidenceName: string
  evidenceType: string
  sourceObject: string
  sourceObjectId?: number
  sourceObjectNo?: string
  businessDomain: string
  fileUrl?: string
  fileName?: string
  fileFormat?: string
  evidenceHash?: string
  issuedBy?: string
  issuedAt?: string
  validFrom?: string
  validTo?: string
  status?: string
  summary?: string
  remark?: string
  createTime?: string
}

export const LabEvidenceObjectApi = {
  getEvidenceObjectPage: async (params: any) => {
    return await request.get({ url: '/lab/evidence-object/page', params })
  },

  getEvidenceObject: async (id: number) => {
    return await request.get({ url: '/lab/evidence-object/get', params: { id } })
  },

  createEvidenceObject: async (data: LabEvidenceObjectVO) => {
    return await request.post({ url: '/lab/evidence-object/create', data })
  },

  updateEvidenceObject: async (data: LabEvidenceObjectVO) => {
    return await request.put({ url: '/lab/evidence-object/update', data })
  },

  deleteEvidenceObject: async (id: number) => {
    return await request.delete({ url: '/lab/evidence-object/delete', params: { id } })
  }
}
