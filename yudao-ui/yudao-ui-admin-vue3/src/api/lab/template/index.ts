import request from '@/config/axios'

export interface LabTemplateVersionVO {
  id?: number
  domainPackId: number
  templateCode: string
  templateName: string
  templateVersion: string
  templateType: string
  templateStatus?: string
  sectionSchema?: string
  outputFormats?: string
  dataSourceSchema?: string
  previewSchema?: string
  status: string
  createTime?: string
}

export interface LabTemplateFieldBindingVO {
  id?: number
  templateId: number
  fieldCode: string
  fieldName: string
  sourceType: string
  sourcePath: string
  requiredFlag?: boolean
  sort?: number
}

export const LabTemplateApi = {
  getTemplateVersionPage: async (params: any) => {
    return await request.get({ url: '/lab/template/version/page', params })
  },
  getTemplateVersion: async (id: number) => {
    return await request.get({ url: '/lab/template/version/get', params: { id } })
  },
  createTemplateVersion: async (data: LabTemplateVersionVO) => {
    return await request.post({ url: '/lab/template/version/create', data })
  },
  updateTemplateVersion: async (data: LabTemplateVersionVO) => {
    return await request.put({ url: '/lab/template/version/update', data })
  },
  deleteTemplateVersion: async (id: number) => {
    return await request.delete({ url: '/lab/template/version/delete', params: { id } })
  },
  publishTemplateVersion: async (id: number) => {
    return await request.put({ url: '/lab/template/version/publish', params: { id } })
  },
  archiveTemplateVersion: async (id: number) => {
    return await request.put({ url: '/lab/template/version/archive', params: { id } })
  },
  getFieldBindingPage: async (params: any) => {
    return await request.get({ url: '/lab/template/field/page', params })
  },
  createFieldBinding: async (data: LabTemplateFieldBindingVO) => {
    return await request.post({ url: '/lab/template/field/create', data })
  },
  updateFieldBinding: async (data: LabTemplateFieldBindingVO) => {
    return await request.put({ url: '/lab/template/field/update', data })
  },
  deleteFieldBinding: async (id: number) => {
    return await request.delete({ url: '/lab/template/field/delete', params: { id } })
  },
  getTemplatePreview: async (templateId: number) => {
    return await request.get({ url: '/lab/template/preview', params: { templateId } })
  }
}
