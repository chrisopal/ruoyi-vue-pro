import request from '@/config/axios'

export interface LabDomainPackVO {
  id?: number
  domainId: number
  packCode: string
  packName: string
  packVersion: string
  industry?: string
  applicationScope?: string
  workflowSchema?: string
  templateSchema?: string
  status: string
  remark?: string
  createTime?: string
}

export const LabDomainPackApi = {
  getDomainPackPage: async (params: any) => {
    return await request.get({ url: '/lab/domain-pack/page', params })
  },

  getDomainPack: async (id: number) => {
    return await request.get({ url: '/lab/domain-pack/get', params: { id } })
  },

  createDomainPack: async (data: LabDomainPackVO) => {
    return await request.post({ url: '/lab/domain-pack/create', data })
  },

  updateDomainPack: async (data: LabDomainPackVO) => {
    return await request.put({ url: '/lab/domain-pack/update', data })
  },

  deleteDomainPack: async (id: number) => {
    return await request.delete({ url: '/lab/domain-pack/delete', params: { id } })
  }
}
