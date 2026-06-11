import request from '@/utils/request'

export interface ProjectLxInfo {
  id?: number
  projectName: string
  projectCode: string
  projectAddress: string
  projectType: string
  mainDept: string
  buildUnit: string
  buildScale?: string
  estimatedInvestment?: number
  startDate?: string
  endDate?: string
  description?: string
  status?: string
  createTime?: string
  updateTime?: string
}

// 获取项目列表
export function getProjectLxList(params?: any) {
  return request.get('/project/lx/list', { params })
}

// 获取项目详情
export function getProjectLxDetail(id: number) {
  return request.get(`/project/lx/${id}`)
}

// 新增项目
export function addProjectLx(data: ProjectLxInfo) {
  return request.post('/project/lx', data)
}

// 编辑项目
export function updateProjectLx(data: ProjectLxInfo) {
  return request.put('/project/lx', data)
}

// 删除项目
export function deleteProjectLx(id: number) {
  return request.delete(`/project/lx/${id}`)
}