import request from '../utils/request'

export interface FormFieldDTO {
  fieldType: string
  fieldName: string
  label: string
  placeholder?: string
  required?: boolean
  pattern?: string
  defaultValue?: string
  minLength?: number
  maxLength?: number
  min?: number
  max?: number
  options?: Array<{ value: string; label: string }>
  validationMessage?: string
}

export interface FormDefinitionRequestDTO {
  name: string
  code: string
  description?: string
  fields: FormFieldDTO[]
  layoutConfig?: object
  status?: number
}

export interface FormDefinition {
  id: number
  name: string
  code: string
  description: string
  fields: string
  layoutConfig: string
  status: number
  creatorId: number
  createTime: string
  updateTime: string
}

export interface FormInstanceRequestDTO {
  formDefinitionId: number
  formData: Record<string, any>
  businessKey?: string
}

export interface FormInstance {
  id: number
  formDefinitionId: number
  formData: string
  businessKey: string
  processInstanceId: number
  status: number
  submitterId: number
  submitTime: string
  createTime: string
  updateTime: string
}

export interface ProcessDefinitionRequestDTO {
  name: string
  code: string
  description?: string
  formDefinitionId: number
  flowNodes: FlowNodeDTO[]
  status?: number
}

export interface FlowNodeDTO {
  nodeId: string
  nodeName: string
  nodeType: string
  assigneeId?: number
  assigneeType?: string
  nextNodes?: string[]
  isStart?: boolean
  isEnd?: boolean
  sortOrder?: number
}

export interface TaskOperateRequestDTO {
  taskId: number
  action: string
  comment?: string
}

export interface Task {
  id: number
  processInstanceId: number
  nodeName: string
  nodeType: string
  assigneeId: number
  creatorId: number
  status: number
  comment: string
  action: string
  createTime: string
  completeTime: string
  updateTime: string
}

export function getFormDefinitions(params: { pageNum?: number; pageSize?: number }) {
  return request.get('/api/form/definitions', { params })
}

export function getFormDefinition(id: number) {
  return request.get(`/api/form/definitions/${id}`)
}

export function createFormDefinition(data: FormDefinitionRequestDTO) {
  return request.post('/api/form/definitions', data)
}

export function updateFormDefinition(id: number, data: FormDefinitionRequestDTO) {
  return request.put(`/api/form/definitions/${id}`, data)
}

export function deleteFormDefinition(id: number) {
  return request.delete(`/api/form/definitions/${id}`)
}

export function createFormInstance(data: FormInstanceRequestDTO) {
  return request.post('/api/form/instances', data)
}

export function submitFormInstance(data: FormInstanceRequestDTO) {
  return request.post('/api/form/instances/submit', data)
}

export function getFormInstance(id: number) {
  return request.get(`/api/form/instances/${id}`)
}

export function getProcessDefinitions(params: { pageNum?: number; pageSize?: number }) {
  return request.get('/api/form/process/definitions', { params })
}

export function createProcessDefinition(data: ProcessDefinitionRequestDTO) {
  return request.post('/api/form/process/definitions', data)
}

export function getTodoTasks(params: { userId: number }) {
  return request.get('/api/form/tasks/todo', { params })
}

export function operateTask(data: TaskOperateRequestDTO) {
  return request.post('/api/form/tasks/operate', data)
}