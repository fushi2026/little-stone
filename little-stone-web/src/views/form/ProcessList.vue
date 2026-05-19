<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Plus, Eye, Edit, Trash2, Search } from '@element-plus/icons-vue'
import {
  ElButton,
  ElTable,
  ElTableColumn,
  ElPagination,
  ElInput,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput as ElInput2,
  ElTextarea,
  ElSelect,
  ElOption,
  ElSwitch,
  ElMessage,
  ElCard,
  ElAlert,
  ElTag
} from 'element-plus'
import {
  getProcessDefinitions,
  createProcessDefinition,
  type ProcessDefinitionRequestDTO,
  type FlowNodeDTO
} from '../../api/form'
import { getFormDefinitions } from '../../api/form'

const processList = ref<{
  id: number
  name: string
  code: string
  description: string
  formDefinitionId: number
  status: number
  createTime: string
}[]>([])

const formDefinitions = ref<{ id: number; name: string; code: string }[]>([])

const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const showCreateModal = ref(false)

const processName = ref('')
const processCode = ref('')
const processDescription = ref('')
const selectedFormId = ref<number | null>(null)
const processStatus = ref(true)

const flowNodes = ref<FlowNodeDTO[]>([])
const showNodeConfig = ref(false)
const editingNodeIndex = ref<number | null>(null)

const nodeTypes = [
  { value: 'start', label: '开始节点' },
  { value: 'approve', label: '审批节点' },
  { value: 'cc', label: '抄送节点' },
  { value: 'end', label: '结束节点' }
]

const loadProcessList = async () => {
  try {
    const response = await getProcessDefinitions({ pageNum: pageNum.value, pageSize: pageSize.value })
    if (response.data && response.data.data) {
      processList.value = response.data.data.records
      total.value = response.data.data.total
    }
  } catch (error) {
    console.error('加载流程列表失败', error)
    ElMessage.error('加载流程列表失败')
  }
}

const loadFormDefinitions = async () => {
  try {
    const response = await getFormDefinitions({ pageNum: 1, pageSize: 100 })
    if (response.data && response.data.data) {
      formDefinitions.value = response.data.data.records.map((item: { id: number; name: string; code: string }) => ({
        id: item.id,
        name: item.name,
        code: item.code
      }))
    }
  } catch (error) {
    console.error('加载表单定义失败', error)
  }
}

const handleCreate = () => {
  processName.value = ''
  processCode.value = ''
  processDescription.value = ''
  selectedFormId.value = null
  processStatus.value = true
  flowNodes.value = []
  showCreateModal.value = true
}

const handleNodeConfig = () => {
  if (!selectedFormId.value) {
    ElMessage.warning('请先选择关联表单')
    return
  }
  showNodeConfig.value = true
}

const addNode = () => {
  const nodeId = `node_${Date.now()}`
  const newNode: FlowNodeDTO = {
    nodeId,
    nodeName: `节点${flowNodes.value.length + 1}`,
    nodeType: 'approve',
    sortOrder: flowNodes.value.length + 1
  }
  
  if (flowNodes.value.length === 0) {
    newNode.nodeType = 'start'
    newNode.isStart = true
  }
  
  flowNodes.value.push(newNode)
}

const removeNode = (index: number) => {
  const node = flowNodes.value[index]
  if (node.isStart) {
    ElMessage.warning('不能删除开始节点')
    return
  }
  flowNodes.value.splice(index, 1)
  updateNodeConnections()
}

const editNode = (index: number) => {
  editingNodeIndex.value = index
}

const saveNode = () => {
  editingNodeIndex.value = null
  updateNodeConnections()
}

const updateNodeConnections = () => {
  flowNodes.value.forEach((node, index) => {
    const nextIndex = index + 1
    if (nextIndex < flowNodes.value.length && !node.isEnd) {
      node.nextNodes = [flowNodes.value[nextIndex].nodeId]
    } else {
      node.nextNodes = undefined
    }
  })
}

const handleSaveProcess = async () => {
  if (!processName.value || !processCode.value) {
    ElMessage.warning('请填写流程名称和编码')
    return
  }
  
  if (!selectedFormId.value) {
    ElMessage.warning('请选择关联表单')
    return
  }
  
  if (flowNodes.value.length === 0) {
    ElMessage.warning('请至少添加一个节点')
    return
  }
  
  const hasStart = flowNodes.value.some(n => n.isStart)
  const hasEnd = flowNodes.value.some(n => n.isEnd)
  
  if (!hasStart) {
    flowNodes.value[0].nodeType = 'start'
    flowNodes.value[0].isStart = true
  }
  
  if (!hasEnd) {
    flowNodes.value.push({
      nodeId: `node_${Date.now()}`,
      nodeName: '结束节点',
      nodeType: 'end',
      isEnd: true,
      sortOrder: flowNodes.value.length + 1
    })
  }

  const requestData: ProcessDefinitionRequestDTO = {
    name: processName.value,
    code: processCode.value,
    description: processDescription.value,
    formDefinitionId: selectedFormId.value,
    flowNodes: flowNodes.value,
    status: processStatus.value ? 1 : 0
  }

  try {
    const response = await createProcessDefinition(requestData)
    if (response.data && response.data.success) {
      ElMessage.success('创建成功')
      showCreateModal.value = false
      showNodeConfig.value = false
      loadProcessList()
    } else {
      ElMessage.error(response.data?.message || '创建失败')
    }
  } catch (error) {
    console.error('创建流程失败', error)
    ElMessage.error('创建流程失败')
  }
}

const handlePageChange = (page: number) => {
  pageNum.value = page
  loadProcessList()
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  pageNum.value = 1
  loadProcessList()
}

onMounted(() => {
  loadProcessList()
  loadFormDefinitions()
})
</script>

<template>
  <div class="process-list-container">
    <div class="list-header">
      <h2>流程管理</h2>
      <div class="header-actions">
        <ElInput
          v-model="searchKeyword"
          placeholder="搜索流程名称"
          prefix-icon="Search"
          style="width: 200px"
        />
        <ElButton type="primary" icon="Plus" @click="handleCreate">
          新建流程
        </ElButton>
      </div>
    </div>

    <div class="list-content">
      <ElTable :data="processList" border style="width: 100%">
        <ElTableColumn prop="name" label="流程名称" min-width="150" />
        <ElTableColumn prop="code" label="流程编码" min-width="120" />
        <ElTableColumn prop="description" label="流程描述" min-width="200" />
        <ElTableColumn prop="formDefinitionId" label="关联表单" min-width="150">
          <template #default="scope">
            <span>
              {{ formDefinitions.find(f => f.id === scope.row.formDefinitionId)?.name || '未关联' }}
            </span>
          </template>
        </ElTableColumn>
        <ElTableColumn prop="status" label="状态" min-width="80">
          <template #default="scope">
            <ElSwitch :model-value="scope.row.status === 1" :disabled="true" />
          </template>
        </ElTableColumn>
        <ElTableColumn prop="createTime" label="创建时间" min-width="150" />
        <ElTableColumn label="操作" min-width="180">
          <template #default="scope">
            <ElButton icon="Eye" size="small">
              查看
            </ElButton>
            <ElButton icon="Edit" size="small" type="primary">
              编辑
            </ElButton>
            <ElButton icon="Delete" size="small" type="danger">
              删除
            </ElButton>
          </template>
        </ElTableColumn>
      </ElTable>

      <div class="pagination-wrapper">
        <ElPagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <ElDialog
      v-model="showCreateModal"
      title="新建流程"
      width="600px"
      :footer="false"
    >
      <ElForm label-width="100px">
        <ElFormItem label="流程名称">
          <ElInput2 v-model="processName" placeholder="请输入流程名称" />
        </ElFormItem>
        <ElFormItem label="流程编码">
          <ElInput2 v-model="processCode" placeholder="请输入流程编码（唯一标识）" />
        </ElFormItem>
        <ElFormItem label="流程描述">
          <ElTextarea v-model="processDescription" placeholder="请输入流程描述" />
        </ElFormItem>
        <ElFormItem label="关联表单">
          <ElSelect v-model="selectedFormId" placeholder="请选择表单">
            <ElOption
              v-for="form in formDefinitions"
              :key="form.id"
              :value="form.id"
              :label="form.name"
            />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="状态">
          <ElSwitch v-model="processStatus" />
        </ElFormItem>
      </ElForm>

      <div class="modal-footer">
        <ElButton @click="showCreateModal = false">取消</ElButton>
        <ElButton type="primary" @click="handleNodeConfig">
          配置流程节点
        </ElButton>
      </div>
    </ElDialog>

    <ElDialog
      v-model="showNodeConfig"
      title="流程节点配置"
      width="800px"
      :footer="false"
    >
      <div class="node-config-wrapper">
        <div class="node-toolbar">
          <ElButton type="primary" icon="Plus" @click="addNode">
            添加节点
          </ElButton>
        </div>

        <div v-if="flowNodes.length === 0" class="empty-nodes">
          <ElAlert
            title="点击上方按钮添加流程节点"
            type="info"
            :closable="false"
          />
        </div>

        <div v-else class="nodes-container">
          <div
            v-for="(node, index) in flowNodes"
            :key="node.nodeId"
            class="node-card"
          >
            <div class="node-header">
              <span class="node-index">{{ index + 1 }}</span>
              <span class="node-name">{{ node.nodeName }}</span>
              <ElTag :type="node.nodeType === 'start' ? 'success' : node.nodeType === 'end' ? 'warning' : 'primary'">
                {{ nodeTypes.find(t => t.value === node.nodeType)?.label }}
              </ElTag>
              <div class="node-actions">
                <ElButton icon="Edit" size="small" @click="editNode(index)" />
                <ElButton icon="Delete" size="small" type="danger" @click="removeNode(index)" />
              </div>
            </div>

            <div v-if="editingNodeIndex === index" class="node-edit-form">
              <ElForm label-width="80px" size="small">
                <ElFormItem label="节点名称">
                  <ElInput2 v-model="node.nodeName" />
                </ElFormItem>
                <ElFormItem label="节点类型">
                  <ElSelect v-model="node.nodeType" :disabled="node.isStart || node.isEnd">
                    <ElOption
                      v-for="type in nodeTypes"
                      :key="type.value"
                      :value="type.value"
                      :label="type.label"
                    />
                  </ElSelect>
                </ElFormItem>
                <ElFormItem label="处理人">
                  <ElInput2 v-model="node.assigneeId" type="number" placeholder="用户ID" />
                </ElFormItem>
                <ElFormItem>
                  <ElButton type="primary" size="small" @click="saveNode">
                    保存
                  </ElButton>
                </ElFormItem>
              </ElForm>
            </div>

            <div v-else class="node-info">
              <p><strong>处理人ID：</strong>{{ node.assigneeId || '未设置' }}</p>
              <p><strong>下一个节点：</strong>{{ node.nextNodes?.join(', ') || '无' }}</p>
            </div>
          </div>
        </div>

        <div class="flow-preview">
          <h4>流程预览</h4>
          <div class="flow-diagram">
            <template v-for="(node, index) in flowNodes" :key="node.nodeId">
              <div class="flow-node" :class="node.nodeType">
                {{ node.nodeName }}
              </div>
              <div v-if="index < flowNodes.length - 1" class="flow-arrow">→</div>
            </template>
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <ElButton @click="showNodeConfig = false">返回</ElButton>
        <ElButton type="primary" @click="handleSaveProcess">
          保存流程
        </ElButton>
      </div>
    </ElDialog>
  </div>
</template>

<style lang="scss" scoped>
.process-list-container {
  padding: 24px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);

  .list-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h2 {
      margin: 0;
      font-size: 18px;
      font-weight: 600;
    }

    .header-actions {
      display: flex;
      gap: 12px;
      align-items: center;
    }
  }

  .list-content {
    background: #fff;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

    .pagination-wrapper {
      display: flex;
      justify-content: flex-end;
      margin-top: 20px;
    }
  }

  .node-config-wrapper {
    .node-toolbar {
      margin-bottom: 16px;
    }

    .empty-nodes {
      margin: 32px 0;
    }

    .nodes-container {
      display: flex;
      flex-direction: column;
      gap: 12px;
      margin-bottom: 24px;
    }

    .node-card {
      background: #f8f9fa;
      border-radius: 8px;
      padding: 12px;

      .node-header {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 12px;

        .node-index {
          width: 24px;
          height: 24px;
          background: #409eff;
          color: #fff;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 12px;
        }

        .node-name {
          font-weight: 500;
        }

        .node-actions {
          margin-left: auto;
          display: flex;
          gap: 4px;
        }
      }

      .node-edit-form {
        background: #fff;
        padding: 12px;
        border-radius: 8px;
      }

      .node-info {
        font-size: 13px;
        color: #606266;
      }
    }

    .flow-preview {
      background: #fff;
      padding: 16px;
      border-radius: 8px;
      margin-top: 16px;

      h4 {
        margin: 0 0 12px 0;
        font-size: 14px;
        font-weight: 500;
      }

      .flow-diagram {
        display: flex;
        align-items: center;
        gap: 8px;
        flex-wrap: wrap;

        .flow-node {
          padding: 6px 16px;
          border-radius: 20px;
          font-size: 13px;

          &.start {
            background: #e8f5e9;
            color: #2e7d32;
          }

          &.approve {
            background: #e3f2fd;
            color: #1565c0;
          }

          &.cc {
            background: #fff3e0;
            color: #ef6c00;
          }

          &.end {
            background: #ffebee;
            color: #c62828;
          }
        }

        .flow-arrow {
          font-size: 18px;
          color: #909399;
        }
      }
    }
  }

  .modal-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    padding: 16px;
    border-top: 1px solid #ebeef5;
    margin-top: 16px;
  }
}
</style>
