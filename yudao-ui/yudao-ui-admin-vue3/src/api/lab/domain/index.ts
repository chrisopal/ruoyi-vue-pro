import request from '@/config/axios'

export interface LabDomainProfileVO {
  id?: number
  domainCode: string
  domainName: string
  description?: string
  status: string
  createTime?: string
}

export const LabDomainProfileApi = {
  getDomainProfilePage: async (params: any) => {
    return await request.get({ url: '/lab/domain/page', params })
  },

  getDomainProfile: async (id: number) => {
    return await request.get({ url: '/lab/domain/get', params: { id } })
  },

  createDomainProfile: async (data: LabDomainProfileVO) => {
    return await request.post({ url: '/lab/domain/create', data })
  },

  updateDomainProfile: async (data: LabDomainProfileVO) => {
    return await request.put({ url: '/lab/domain/update', data })
  },

  deleteDomainProfile: async (id: number) => {
    return await request.delete({ url: '/lab/domain/delete', params: { id } })
  }
}
