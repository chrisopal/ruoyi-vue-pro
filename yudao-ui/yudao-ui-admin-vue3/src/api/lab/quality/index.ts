import request from '@/config/axios'

export interface LabQualityRecordVO {
  id?: number
  status?: string
  [key: string]: any
}

export const LabQualityApi = {
  page: async (baseUrl: string, params: any) => request.get({ url: baseUrl + '/page', params }),
  get: async (baseUrl: string, id: number) => request.get({ url: baseUrl + '/get', params: { id } }),
  create: async (baseUrl: string, data: LabQualityRecordVO) => request.post({ url: baseUrl + '/create', data }),
  update: async (baseUrl: string, data: LabQualityRecordVO) => request.put({ url: baseUrl + '/update', data }),
  delete: async (baseUrl: string, id: number) => request.delete({ url: baseUrl + '/delete', params: { id } }),
  export: async (baseUrl: string, params: any) => request.get({ url: baseUrl + '/export-excel', params }),
  postAction: async (url: string, id: number, data?: any) => request.post({ url, params: { id }, data }),
  putAction: async (url: string, id: number, data?: any) => request.put({ url, params: { id }, data }),
  getAction: async (url: string, id: number) => request.get({ url, params: { id } })
}
