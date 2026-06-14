import request from '@/config/axios'

export interface LabEquipmentAssetVO {
  id?: number
  equipmentCode: string
  equipmentName: string
  equipmentType?: string
  manufacturer?: string
  model?: string
  serialNo?: string
  labArea?: string
  domainCode?: string
  capabilityScope?: string
  responsibleUserId?: number
  calibrationValidUntil?: string
  status?: string
  iotEnabled?: boolean
  iotProductId?: number
  iotDeviceId?: string
  dataSourceType?: string
  remark?: string
  createTime?: string
}

export const LabEquipmentAssetApi = {
  getEquipmentAssetPage: async (params: any) => {
    return await request.get({ url: '/lab/equipment-asset/page', params })
  },

  getEquipmentAsset: async (id: number) => {
    return await request.get({ url: '/lab/equipment-asset/get', params: { id } })
  },

  createEquipmentAsset: async (data: LabEquipmentAssetVO) => {
    return await request.post({ url: '/lab/equipment-asset/create', data })
  },

  updateEquipmentAsset: async (data: LabEquipmentAssetVO) => {
    return await request.put({ url: '/lab/equipment-asset/update', data })
  },

  deleteEquipmentAsset: async (id: number) => {
    return await request.delete({ url: '/lab/equipment-asset/delete', params: { id } })
  },

  getAvailableEquipment: async (params: { domainCode?: string; testItem?: string }) => {
    return await request.get<LabEquipmentAssetVO[]>({ url: '/lab/equipment-asset/available', params })
  }
}
