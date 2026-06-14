import request from '@/config/axios'

export interface LabStandardVO {
  id?: number
  standardCode: string
  standardName: string
  standardVersion: string
  standardType: string
  status: string
  remark?: string
  createTime?: string
}

export interface LabStandardClauseVO {
  id?: number
  standardId: number
  clauseCode: string
  clauseTitle: string
  clauseCategory: string
  requirementText: string
  evidenceTypeCodes?: string
  status: string
  createTime?: string
}

export const LabStandardApi = {
  getStandardPage: async (params: any) => {
    return await request.get({ url: '/lab/standard/page', params })
  },
  getStandard: async (id: number) => {
    return await request.get({ url: '/lab/standard/get', params: { id } })
  },
  createStandard: async (data: LabStandardVO) => {
    return await request.post({ url: '/lab/standard/create', data })
  },
  updateStandard: async (data: LabStandardVO) => {
    return await request.put({ url: '/lab/standard/update', data })
  },
  deleteStandard: async (id: number) => {
    return await request.delete({ url: '/lab/standard/delete', params: { id } })
  },
  updateStandardStatus: async (id: number, status: string) => {
    return await request.put({ url: '/lab/standard/update-status', params: { id, status } })
  },
  exportStandard: async (params: any) => {
    return await request.get({ url: '/lab/standard/export-excel', params })
  },
  getStandardClausePage: async (params: any) => {
    return await request.get({ url: '/lab/standard-clause/page', params })
  },
  getStandardClauseTree: async (standardId: number) => {
    return await request.get({ url: '/lab/standard-clause/tree', params: { standardId } })
  },
  getStandardClause: async (id: number) => {
    return await request.get({ url: '/lab/standard-clause/get', params: { id } })
  },
  createStandardClause: async (data: LabStandardClauseVO) => {
    return await request.post({ url: '/lab/standard-clause/create', data })
  },
  updateStandardClause: async (data: LabStandardClauseVO) => {
    return await request.put({ url: '/lab/standard-clause/update', data })
  },
  deleteStandardClause: async (id: number) => {
    return await request.delete({ url: '/lab/standard-clause/delete', params: { id } })
  },
  exportStandardClause: async (params: any) => {
    return await request.get({ url: '/lab/standard-clause/export-excel', params })
  }
}
