<template>
    <div class="project-lx-container">
        <!-- 搜索区域 -->
        <el-card class="search-card">
            <el-form :inline="true" :model="searchForm" class="search-form">
                <el-form-item label="项目名称">
                    <el-input v-model="searchForm.projectName" placeholder="请输入项目名称" clearable />
                </el-form-item>
                <el-form-item label="项目编号">
                    <el-input v-model="searchForm.projectCode" placeholder="请输入项目编号" clearable />
                </el-form-item>
                <el-form-item label="项目类型">
                    <el-select v-model="searchForm.projectType" placeholder="请选择项目类型" clearable style="width: 200px">
                        <el-option label="新建项目" value="new" />
                        <el-option label="续建项目" value="continue" />
                        <el-option label="改造项目" value="transform" />
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleSearch">
                        <el-icon><Search /></el-icon>
                        搜索
                    </el-button>
                    <el-button @click="handleReset">
                        <el-icon><RefreshLeft /></el-icon>
                        重置
                    </el-button>
                </el-form-item>
            </el-form>
        </el-card>

        <!-- 操作按钮区域 -->
        <el-card class="table-card">
            <template #header>
                <div class="card-header">
                    <span>项目列表</span>
                    <el-button type="primary" @click="handleAdd">
                        <el-icon><Plus /></el-icon>
                        新增项目
                    </el-button>
                </div>
            </template>

            <!-- 表格 -->
            <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
                <el-table-column type="index" label="序号" width="60" align="center" />
                <el-table-column prop="projectName" label="项目名称" min-width="200" show-overflow-tooltip />
                <el-table-column prop="projectCode" label="项目编号" width="150" show-overflow-tooltip />
                <el-table-column prop="projectType" label="项目类型" width="120">
                    <template #default="{ row }">
                        <el-tag :type="getProjectTypeTagType(row.projectType)">
                            {{ getProjectTypeText(row.projectType) }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="mainDept" label="主责部门" width="150" show-overflow-tooltip />
                <el-table-column prop="buildUnit" label="建设单位" width="180" show-overflow-tooltip />
                <el-table-column prop="projectAddress" label="项目地址" min-width="200" show-overflow-tooltip />
                <el-table-column prop="estimatedInvestment" label="预估投资(万元)" width="140">
                    <template #default="{ row }">
                        {{ row.estimatedInvestment ? row.estimatedInvestment.toLocaleString() : '-' }}
                    </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="100">
                    <template #default="{ row }">
                        <el-tag :type="getStatusTagType(row.status)">
                            {{ getStatusText(row.status) }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="200" fixed="right" align="center">
                    <template #default="{ row }">
                        <el-button type="primary" link @click="handleEdit(row)">
                            <el-icon><Edit /></el-icon>
                            编辑
                        </el-button>
                        <el-button type="danger" link @click="handleDelete(row)">
                            <el-icon><Delete /></el-icon>
                            删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 分页 -->
            <div class="pagination">
                <el-pagination
                    v-model:current-page="pagination.pageNum"
                    v-model:page-size="pagination.pageSize"
                    :page-sizes="[10, 20, 50, 100]"
                    :total="pagination.total"
                    layout="total, sizes, prev, pager, next, jumper"
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                />
            </div>
        </el-card>

        <!-- 新增/编辑对话框 -->
        <el-dialog
            v-model="dialogVisible"
            :title="dialogTitle"
            width="1000px"
            :close-on-click-modal="false"
            destroy-on-close
        >
            <el-form
                ref="formRef"
                :model="form"
                :rules="rules"
                label-width="110px"
            >
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="项目名称" prop="projectName">
                            <el-input v-model="form.projectName" placeholder="请输入项目名称" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="项目编号" prop="projectCode">
                            <el-input v-model="form.projectCode" placeholder="请输入项目编号" />
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="项目类型" prop="projectType">
                            <el-select v-model="form.projectType" placeholder="请选择项目类型" style="width: 100%">
                                <el-option label="新建项目" value="new" />
                                <el-option label="续建项目" value="continue" />
                                <el-option label="改造项目" value="transform" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="主责部门" prop="mainDept">
                            <el-input v-model="form.mainDept" placeholder="请输入主责部门" />
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="建设单位" prop="buildUnit">
                            <el-input v-model="form.buildUnit" placeholder="请输入建设单位" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="预估投资(万元)" prop="estimatedInvestment">
                            <el-input-number v-model="form.estimatedInvestment" :min="0" :precision="2" placeholder="请输入预估投资" style="width: 100%" />
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-form-item label="项目地址" prop="projectAddress">
                    <el-input v-model="form.projectAddress" placeholder="请输入项目地址" />
                </el-form-item>
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="开始日期" prop="startDate">
                            <el-date-picker
                                v-model="form.startDate"
                                type="date"
                                placeholder="请选择开始日期"
                                value-format="YYYY-MM-DD"
                                style="width: 100%"
                            />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="结束日期" prop="endDate">
                            <el-date-picker
                                v-model="form.endDate"
                                type="date"
                                placeholder="请选择结束日期"
                                value-format="YYYY-MM-DD"
                                style="width: 100%"
                            />
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-form-item label="建设规模" prop="buildScale">
                    <el-input v-model="form.buildScale" type="textarea" :rows="3" placeholder="请输入建设规模" />
                </el-form-item>
                <el-form-item label="项目描述" prop="description">
                    <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入项目描述" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
                    确定
                </el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
    Search,
    RefreshLeft,
    Plus,
    Edit,
    Delete
} from '@element-plus/icons-vue'
import {
    getProjectLxList,
    addProjectLx,
    updateProjectLx,
    deleteProjectLx,
    type ProjectLxInfo
} from '@/api/project_lx'

// 搜索表单
const searchForm = reactive({
    projectName: '',
    projectCode: '',
    projectType: ''
})

// 表格数据
const tableData = ref<ProjectLxInfo[]>([])
const loading = ref(false)

// 分页
const pagination = reactive({
    pageNum: 1,
    pageSize: 10,
    total: 0
})

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增项目')
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

// 表单数据
const form = reactive<ProjectLxInfo>({
    projectName: '',
    projectCode: '',
    projectAddress: '',
    projectType: '',
    mainDept: '',
    buildUnit: '',
    buildScale: '',
    estimatedInvestment: undefined,
    startDate: '',
    endDate: '',
    description: '',
    status: 'draft'
})

// 表单验证规则
const rules: FormRules = {
    projectName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
    projectCode: [{ required: true, message: '请输入项目编号', trigger: 'blur' }],
    projectType: [{ required: true, message: '请选择项目类型', trigger: 'change' }],
    mainDept: [{ required: true, message: '请输入主责部门', trigger: 'blur' }],
    buildUnit: [{ required: true, message: '请输入建设单位', trigger: 'blur' }],
    projectAddress: [{ required: true, message: '请输入项目地址', trigger: 'blur' }]
}

// 获取项目类型标签类型
const getProjectTypeTagType = (type: string) => {
    const map: Record<string, any> = {
        new: 'primary',
        continue: 'success',
        transform: 'warning'
    }
    return map[type] || 'info'
}

// 获取项目类型文本
const getProjectTypeText = (type: string) => {
    const map: Record<string, string> = {
        new: '新建项目',
        continue: '续建项目',
        transform: '改造项目'
    }
    return map[type] || type
}

// 获取状态标签类型
const getStatusTagType = (status: string) => {
    const map: Record<string, any> = {
        draft: 'info',
        pending: 'warning',
        approved: 'success',
        rejected: 'danger'
    }
    return map[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: string) => {
    const map: Record<string, string> = {
        draft: '草稿',
        pending: '待审批',
        approved: '已审批',
        rejected: '已驳回'
    }
    return map[status] || status
}

// 获取数据列表
const fetchData = async () => {
    loading.value = true
    try {
        // 模拟数据，等后端接口完成后替换
        tableData.value = [
            {
                id: 1,
                projectName: '小石头在线平台建设项目',
                projectCode: 'XST-2024-001',
                projectAddress: '北京市朝阳区科技园区',
                projectType: 'new',
                mainDept: '技术部',
                buildUnit: '小石头科技有限公司',
                buildScale: '建筑面积5000平方米',
                estimatedInvestment: 500,
                startDate: '2024-01-01',
                endDate: '2024-12-31',
                description: '建设小石头在线管理平台，提升企业管理效率',
                status: 'approved',
                createTime: '2024-01-01 10:00:00'
            },
            {
                id: 2,
                projectName: '办公楼装修改造项目',
                projectCode: 'XST-2024-002',
                projectAddress: '北京市海淀区中关村大街',
                projectType: 'transform',
                mainDept: '行政部',
                buildUnit: '建筑装饰工程公司',
                buildScale: '装修面积3000平方米',
                estimatedInvestment: 200,
                startDate: '2024-03-01',
                endDate: '2024-06-30',
                description: '办公楼内部装修改造工程',
                status: 'pending',
                createTime: '2024-02-15 14:30:00'
            }
        ]
        pagination.total = tableData.value.length

        // TODO: 等后端接口完成后使用真实接口
        // const res = await getProjectLxList({
        //     ...searchForm,
        //     pageNum: pagination.pageNum,
        //     pageSize: pagination.pageSize
        // })
        // tableData.value = res.data.list || []
        // pagination.total = res.data.total || 0
    } catch (error) {
        ElMessage.error('获取数据失败')
    } finally {
        loading.value = false
    }
}

// 搜索
const handleSearch = () => {
    pagination.pageNum = 1
    fetchData()
}

// 重置
const handleReset = () => {
    searchForm.projectName = ''
    searchForm.projectCode = ''
    searchForm.projectType = ''
    pagination.pageNum = 1
    fetchData()
}

// 新增
const handleAdd = () => {
    dialogTitle.value = '新增项目'
    Object.assign(form, {
        projectName: '',
        projectCode: '',
        projectAddress: '',
        projectType: '',
        mainDept: '',
        buildUnit: '',
        buildScale: '',
        estimatedInvestment: undefined,
        startDate: '',
        endDate: '',
        description: '',
        status: 'draft'
    })
    formRef.value?.resetFields()
    dialogVisible.value = true
}

// 编辑
const handleEdit = (row: ProjectLxInfo) => {
    dialogTitle.value = '编辑项目'
    Object.assign(form, row)
    dialogVisible.value = true
}

// 删除
const handleDelete = async (row: ProjectLxInfo) => {
    try {
        await ElMessageBox.confirm(
            `确定要删除项目"${row.projectName}"吗？`,
            '提示',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }
        )
        // TODO: 等后端接口完成后使用真实接口
        // await deleteProjectLx(row.id!)
        ElMessage.success('删除成功')
        fetchData()
    } catch (error) {
        if (error !== 'cancel') {
            ElMessage.error('删除失败')
        }
    }
}

// 提交
const handleSubmit = async () => {
    if (!formRef.value) return
    await formRef.value.validate(async (valid) => {
        if (valid) {
            submitLoading.value = true
            try {
                if (form.id) {
                    // TODO: 等后端接口完成后使用真实接口
                    // await updateProjectLx(form)
                    ElMessage.success('更新成功')
                } else {
                    // TODO: 等后端接口完成后使用真实接口
                    // await addProjectLx(form)
                    ElMessage.success('新增成功')
                }
                dialogVisible.value = false
                fetchData()
            } catch (error) {
                ElMessage.error(form.id ? '更新失败' : '新增失败')
            } finally {
                submitLoading.value = false
            }
        }
    })
}

// 分页大小改变
const handleSizeChange = (size: number) => {
    pagination.pageSize = size
    fetchData()
}

// 页码改变
const handleCurrentChange = (page: number) => {
    pagination.pageNum = page
    fetchData()
}

onMounted(() => {
    fetchData()
})
</script>

<style scoped>
.project-lx-container {
    padding: 20px;
}

.search-card {
    margin-bottom: 20px;
}

.search-form {
    margin-bottom: 0;
}

.table-card {
    margin-bottom: 20px;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
}
</style>

<!-- 深色模式适配 -->
<style>
.dark .el-card {
    background: #1f2937 !important;
    border-color: #374151 !important;
}

.dark .el-card__header {
    border-color: #374151 !important;
    color: #f9fafb !important;
}

.dark .el-form-item__label {
    color: #d1d5db !important;
}

.dark .el-input__wrapper {
    background: #111827 !important;
    box-shadow: 0 0 0 1px #374151 inset !important;
}

.dark .el-input__inner {
    color: #e5e7eb !important;
}

.dark .el-input__inner::placeholder {
    color: #6b7280 !important;
}

.dark .el-select .el-input__wrapper {
    background: #111827 !important;
}

.dark .el-select-dropdown {
    background: #1f2937 !important;
    border-color: #374151 !important;
}

.dark .el-select-dropdown__item {
    color: #e5e7eb !important;
}

.dark .el-select-dropdown__item:hover {
    background: #374151 !important;
}

.dark .el-table {
    background: #1f2937 !important;
    color: #e5e7eb !important;
}

.dark .el-table__body tr {
    background-color: #1f2937 !important;
}

.dark .el-table__body tr.el-table__row--striped {
    background-color: #1a2332 !important;
}

.dark .el-table__body tr.el-table__row--striped td.el-table__cell {
    background-color: #1a2332 !important;
}

.dark .el-table__body tr td.el-table__cell {
    background-color: #1f2937 !important;
}

.dark .el-table th.el-table__cell {
    background: #111827 !important;
    border-color: #374151 !important;
    color: #d1d5db !important;
}

.dark .el-table td.el-table__cell {
    border-color: #374151 !important;
}

.dark .el-table--enable-row-hover .el-table__body tr:hover > td {
    background: #374151 !important;
}

.dark .el-pagination {
    color: #d1d5db !important;
}

.dark .el-pager li {
    color: #d1d5db !important;
    background: #1f2937 !important;
}

.dark .el-pager li.is-active {
    background: #3b82f6 !important;
    color: #fff !important;
}

.dark .el-pagination button {
    background: #1f2937 !important;
    color: #d1d5db !important;
}

.dark .el-pagination button:disabled {
    color: #4b5563 !important;
}

.dark .el-pagination .el-select .el-input__wrapper {
    background: #111827 !important;
}

.dark .el-dialog {
    background: #1f2937 !important;
}

.dark .el-dialog__header {
    border-bottom-color: #374151 !important;
}

.dark .el-dialog__title {
    color: #f9fafb !important;
}

.dark .el-dialog__footer {
    border-top-color: #374151 !important;
}

.dark .el-button {
    border-color: #374151 !important;
}

.dark .el-button:not(.el-button--primary) {
    color: #d1d5db !important;
    background: #1f2937 !important;
}

.dark .el-button:not(.el-button--primary):hover {
    background: #374151 !important;
    color: #fff !important;
    border-color: #4b5563 !important;
}

.dark .el-date-editor {
    background: #111827 !important;
    border-color: #374151 !important;
}

.dark .el-date-editor .el-input__wrapper {
    background: transparent !important;
    box-shadow: none !important;
}

.dark .el-picker-panel {
    background: #1f2937 !important;
    border-color: #374151 !important;
}

.dark .el-picker-panel__header-title {
    color: #f9fafb !important;
}

.dark .el-picker-panel__icon-btn {
    color: #d1d5db !important;
}

.dark .el-date-table th {
    color: #9ca3af !important;
    border-bottom-color: #374151 !important;
}

.dark .el-date-table td .el-date-table-cell__text {
    color: #e5e7eb !important;
}

.dark .el-date-table td.today .el-date-table-cell__text {
    color: #3b82f6 !important;
}

.dark .el-date-table td.available:hover .el-date-table-cell__text {
    background: #374151 !important;
}

.dark .el-input-number {
    background: #111827 !important;
    border-color: #374151 !important;
}

.dark .el-input-number__decrease,
.dark .el-input-number__increase {
    background: #374151 !important;
    border-color: #374151 !important;
    color: #d1d5db !important;
}

.dark .el-input-number__decrease:hover,
.dark .el-input-number__increase:hover {
    background: #4b5563 !important;
    color: #fff !important;
}

.dark .el-input-number__decrease:disabled,
.dark .el-input-number__increase:disabled {
    color: #4b5563 !important;
}

.dark .el-textarea__inner {
    background: #111827 !important;
    border-color: #374151 !important;
    color: #e5e7eb !important;
}

.dark .el-textarea__inner::placeholder {
    color: #6b7280 !important;
}

.dark .el-message-box {
    background: #1f2937 !important;
    border-color: #374151 !important;
}

.dark .el-message-box__title {
    color: #f9fafb !important;
}

.dark .el-message-box__message {
    color: #d1d5db !important;
}

.dark .el-message-box__btns .el-button:not(.el-button--primary) {
    border-color: #374151 !important;
    color: #d1d5db !important;
    background: #1f2937 !important;
}

.dark .el-message-box__btns .el-button:not(.el-button--primary):hover {
    background: #374151 !important;
    color: #fff !important;
    border-color: #4b5563 !important;
}

.dark .el-select .el-input .el-input__wrapper {
    background: #111827 !important;
    box-shadow: 0 0 0 1px #374151 inset !important;
}

.dark .el-select .el-input.is-focus .el-input__wrapper {
    box-shadow: 0 0 0 1px #3b82f6 inset !important;
}

.dark .el-select-dropdown__wrap {
    background: #1f2937 !important;
}

.dark .el-select-dropdown__list {
    background: #1f2937 !important;
}

.dark .el-select-dropdown__item.is-selected {
    background: #374151 !important;
    color: #fff !important;
}

.dark .el-popper {
    background: #1f2937 !important;
    border-color: #374151 !important;
}

.dark .el-pagination .el-select .el-input .el-input__wrapper {
    background: #1f2937 !important;
    box-shadow: 0 0 0 1px #374151 inset !important;
}

.dark .el-pagination .el-select .el-input .el-input__inner {
    color: #e5e7eb !important;
}
</style>