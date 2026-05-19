<script setup lang="ts">import { ref, onMounted } from 'vue';
import { Plus, Eye, Edit, Trash2, Search } from '@element-plus/icons-vue';
import { ElButton, ElTable, ElTableColumn, ElPagination, ElInput, ElDialog, ElForm, ElFormItem, ElInput as ElInput2, ElTextarea, ElSwitch, ElMessage } from 'element-plus';
import { getFormDefinitions, createFormDefinition, updateFormDefinition, deleteFormDefinition, type FormDefinitionRequestDTO } from '../../api/form';
import FormBuilder from '../../components/form/FormBuilder.vue';
import type { FormFieldDTO } from '../../api/form';
const formList = ref<{
 id: number;
 name: string;
 code: string;
 description: string;
 status: number;
 createTime: string;
}[]>([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const searchKeyword = ref('');
const showCreateModal = ref(false);
const showEditModal = ref(false);
const editingForm = ref<{
 id: number;
 name: string;
 code: string;
 description: string;
 status: number;
} | null>(null);
const formBuilderFields = ref<FormFieldDTO[]>([]);
const formName = ref('');
const formCode = ref('');
const formDescription = ref('');
const formStatus = ref(true);
const loadFormList = async () => {
 try {
 const response = await getFormDefinitions({ pageNum: pageNum.value, pageSize: pageSize.value });
 if (response.data && response.data.data) {
 formList.value = response.data.data.records;
 total.value = response.data.data.total;
 }
 }
 catch (error) {
 console.error('加载表单列表失败', error);
 ElMessage.error('加载表单列表失败');
 }
};
const handleSearch = () => {
 pageNum.value = 1;
 loadFormList();
};
const handlePageChange = (page: number) => {
 pageNum.value = page;
 loadFormList();
};
const handleSizeChange = (size: number) => {
 pageSize.value = size;
 pageNum.value = 1;
 loadFormList();
};
const handleCreate = () => {
 formBuilderFields.value = [];
 formName.value = '';
 formCode.value = '';
 formDescription.value = '';
 formStatus.value = true;
 showCreateModal.value = true;
};
const handleEdit = async (form: typeof formList.value[0]) => {
 editingForm.value = { ...form };
 formName.value = form.name;
 formCode.value = form.code;
 formDescription.value = form.description;
 formStatus.value = form.status === 1;
 showEditModal.value = true;
};
const handleDelete = async (id: number) => {
 try {
 const response = await deleteFormDefinition(id);
 if (response.data && response.data.success) {
 ElMessage.success('删除成功');
 loadFormList();
 }
 else {
 ElMessage.error(response.data?.message || '删除失败');
 }
 }
 catch (error) {
 console.error('删除表单失败', error);
 ElMessage.error('删除表单失败');
 }
};
const handleSaveCreate = async () => {
 if (!formName.value || !formCode.value) {
 ElMessage.warning('请填写表单名称和编码');
 return;
 }
 const requestData: FormDefinitionRequestDTO = {
 name: formName.value,
 code: formCode.value,
 description: formDescription.value,
 fields: formBuilderFields.value,
 layoutConfig: {},
 status: formStatus.value ? 1 : 0
 };
 try {
 const response = await createFormDefinition(requestData);
 if (response.data && response.data.success) {
 ElMessage.success('创建成功');
 showCreateModal.value = false;
 loadFormList();
 }
 else {
 ElMessage.error(response.data?.message || '创建失败');
 }
 }
 catch (error) {
 console.error('创建表单失败', error);
 ElMessage.error('创建表单失败');
 }
};
const handleSaveEdit = async () => {
 if (!editingForm.value)
 return;
 const requestData: FormDefinitionRequestDTO = {
 name: formName.value,
 code: formCode.value,
 description: formDescription.value,
 fields: formBuilderFields.value,
 layoutConfig: {},
 status: formStatus.value ? 1 : 0
 };
 try {
 const response = await updateFormDefinition(editingForm.value.id, requestData);
 if (response.data && response.data.success) {
 ElMessage.success('更新成功');
 showEditModal.value = false;
 loadFormList();
 }
 else {
 ElMessage.error(response.data?.message || '更新失败');
 }
 }
 catch (error) {
 console.error('更新表单失败', error);
 ElMessage.error('更新表单失败');
 }
};
const handleFormBuilderSave = (fields: FormFieldDTO[]) => {
 formBuilderFields.value = fields;
};
onMounted(() => {
 loadFormList();
});
</script>

<template>
  <div class="form-list-container">
    <div class="list-header">
      <h2>表单管理</h2>
      <div class="header-actions">
        <ElInput
          v-model="searchKeyword"
          placeholder="搜索表单名称"
          prefix-icon="Search"
          style="width: 200px"
          @keyup.enter="handleSearch"
        />
        <ElButton type="primary" icon="Plus" @click="handleCreate">
          新建表单
        </ElButton>
      </div>
    </div>

    <div class="list-content">
      <ElTable :data="formList" border style="width: 100%">
        <ElTableColumn prop="name" label="表单名称" min-width="150" />
        <ElTableColumn prop="code" label="表单编码" min-width="120" />
        <ElTableColumn prop="description" label="表单描述" min-width="200" />
        <ElTableColumn prop="status" label="状态" min-width="80">
          <template #default="scope">
            <ElSwitch :model-value="scope.row.status === 1" :disabled="true" />
          </template>
        </ElTableColumn>
        <ElTableColumn prop="createTime" label="创建时间" min-width="150" />
        <ElTableColumn label="操作" min-width="180">
          <template #default="scope">
            <ElButton icon="Eye" size="small" @click="handleEdit(scope.row)">
              查看
            </ElButton>
            <ElButton icon="Edit" size="small" type="primary" @click="handleEdit(scope.row)">
              编辑
            </ElButton>
            <ElButton icon="Delete" size="small" type="danger" @click="handleDelete(scope.row.id)">
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
      title="新建表单"
      width="900px"
      :footer="false"
    >
      <ElForm label-width="80px">
        <ElFormItem label="表单名称">
          <ElInput2 v-model="formName" placeholder="请输入表单名称" />
        </ElFormItem>
        <ElFormItem label="表单编码">
          <ElInput2 v-model="formCode" placeholder="请输入表单编码（唯一标识）" />
        </ElFormItem>
        <ElFormItem label="表单描述">
          <ElTextarea v-model="formDescription" placeholder="请输入表单描述" />
        </ElFormItem>
        <ElFormItem label="状态">
          <ElSwitch v-model="formStatus" />
        </ElFormItem>
        <ElFormItem label="表单设计">
          <FormBuilder @save="handleFormBuilderSave" />
        </ElFormItem>
      </ElForm>

      <div class="modal-footer">
        <ElButton @click="showCreateModal = false">取消</ElButton>
        <ElButton type="primary" @click="handleSaveCreate">保存</ElButton>
      </div>
    </ElDialog>

    <ElDialog
      v-model="showEditModal"
      title="编辑表单"
      width="900px"
      :footer="false"
    >
      <ElForm label-width="80px">
        <ElFormItem label="表单名称">
          <ElInput2 v-model="formName" placeholder="请输入表单名称" />
        </ElFormItem>
        <ElFormItem label="表单编码">
          <ElInput2 v-model="formCode" placeholder="请输入表单编码（唯一标识）" />
        </ElFormItem>
        <ElFormItem label="表单描述">
          <ElTextarea v-model="formDescription" placeholder="请输入表单描述" />
        </ElFormItem>
        <ElFormItem label="状态">
          <ElSwitch v-model="formStatus" />
        </ElFormItem>
        <ElFormItem label="表单设计">
          <FormBuilder v-model="formBuilderFields" @save="handleFormBuilderSave" />
        </ElFormItem>
      </ElForm>

      <div class="modal-footer">
        <ElButton @click="showEditModal = false">取消</ElButton>
        <ElButton type="primary" @click="handleSaveEdit">保存</ElButton>
      </div>
    </ElDialog>
  </div>
</template>

<style lang="scss" scoped>
.form-list-container {
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
