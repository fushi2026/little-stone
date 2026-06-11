import request from '@/utils/request'

export interface ProjectBidInfo {
  id?: number
  projectName: string
  projectCode: string
  tenderUnit: string
  bidDeadline?: string
  bidOpenDate?: string
  projectAddress: string
  bidManager: string
  estimatedAmount?: number
  projectType: string
  description?: string
  status?: string
  createTime?: string
  updateTime?: string
}

// 获取投标项目列表
export function getProjectBidList(params?: any) {
  return request.get('/project/bid/list', { params })
}

// 获取投标项目详情
export function getProjectBidDetail(id: number) {
  return request.get(`/project/bid/${id}`)
}

// 新增投标项目
export function addProjectBid(data: ProjectBidInfo) {
  return request.post('/project/bid', data)
}

// 编辑投标项目
export function updateProjectBid(data: ProjectBidInfo) {
  return request.put('/project/bid', data)
}

// 删除投标项目
export function deleteProjectBid(id: number) {
  return request.delete(`/project/bid/${id}`)
}