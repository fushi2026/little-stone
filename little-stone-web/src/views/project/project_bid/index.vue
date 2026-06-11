<template>
    <div class="project-bid-container">
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
                        <el-option label="工程建设" value="engineering" />
                        <el-option label="设备采购" value="procurement" />
                        <el-option label="服务类" value="service" />
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
                    <span>投标项目列表</span>
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
                <el-table-column prop="tenderUnit" label="招标单位" width="180" show-overflow-tooltip />
                <el-table-column prop="bidManager" label="投标负责人" width="120" />
                <el-table-column prop="bidDeadline" label="投标截止日期" width="140" />
                <el-table-column prop="estimatedAmount" label="预估金额(万元)" width="150">
                    <template #default="{ row }">
                        {{ row.estimatedAmount ? row.estimatedAmount.toLocaleString() : '-' }}
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
                                <el-option label="工程建设" value="engineering" />
                                <el-option label="设备采购" value="procurement" />
                                <el-option label="服务类" value="service" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="投标负责人" prop="bidManager">
                            <el-input v-model="form.bidManager" placeholder="请输入投标负责人" />
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="招标单位" prop="tenderUnit">
                            <el-input v-model="form.tenderUnit" placeholder="请输入招标单位" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="预估金额(万元)" prop="estimatedAmount">
                            <el-input-number v-model="form.estimatedAmount" :min="0" :precision="2" placeholder="请输入预估金额" style="width: 100%" />
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-form-item label="项目地址" prop="projectAddress">
                    <el-input v-model="form.projectAddress" placeholder="请输入项目地址" />
                </el-form-item>
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="投标截止日期" prop="bidDeadline">
                            <el-date-picker
                                v-model="form.bidDeadline"
                                type="date"
                                placeholder="请选择投标截止日期"
                                value-format="YYYY-MM-DD"
                                style="width: 100%"
                            />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开标日期" prop="bidOpenDate">
                            <el-date-picker
                                v-model="form.bidOpenDate"
                                type="date"
                                placeholder="请选择开标日期"
                                value-format="YYYY-MM-DD"
                                style="width: 100%"
                            />
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-form-item label="项目描述" prop="description">
                    <el-input v-model="form.description" type="textarea" :rows="5" placeholder="请输入项目描述" />
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
    getProjectBidList,
    addProjectBid,
    updateProjectBid,
    deleteProjectBid,
    type ProjectBidInfo
} from '@/api/project_bid'

// 搜索表单
const searchForm = reactive({
    projectName: '',
    projectCode: '',
    projectType: ''
})

// 表格数据
const tableData = ref<ProjectBidInfo[]>([])
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
const form = reactive<ProjectBidInfo>({
    projectName: '',
    projectCode: '',
    projectAddress: '',
    projectType: '',
    tenderUnit: '',
    bidManager: '',
    estimatedAmount: undefined,
    bidDeadline: '',
    bidOpenDate: '',
    description: '',
    status: 'draft'
})

// 表单验证规则
const rules: FormRules = {
    projectName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
    projectCode: [{ required: true, message: '请输入项目编号', trigger: 'blur' }],
    projectType: [{ required: true, message: '请选择项目类型', trigger: 'change' }],
    tenderUnit: [{ required: true, message: '请输入招标单位', trigger: 'blur' }],
    bidManager: [{ required: true, message: '请输入投标负责人', trigger: 'blur' }],
    projectAddress: [{ required: true, message: '请输入项目地址', trigger: 'blur' }]
}

// 获取项目类型标签类型
const getProjectTypeTagType = (type: string) => {
    const map: Record<string, any> = {
        engineering: 'primary',
        procurement: 'success',
        service: 'warning'
    }
    return map[type] || 'info'
}

// 获取项目类型文本
const getProjectTypeText = (type: string) => {
    const map: Record<string, string> = {
        engineering: '工程建设',
        procurement: '设备采购',
        service: '服务类'
    }
    return map[type] || type
}

// 获取状态标签类型
const getStatusTagType = (status: string) => {
    const map: Record<string, any> = {
        draft: 'info',
        preparing: 'primary',
        bidding: 'warning',
        successful: 'success',
        failed: 'danger'
    }
    return map[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: string) => {
    const map: Record<string, string> = {
        draft: '草稿',
        preparing: '投标准备',
        bidding: '投标中',
        successful: '中标',
        failed: '未中标'
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
                projectName: '政府办公楼智能化改造项目',
                projectCode: 'ZB-2024-001',
                projectAddress: '北京市西城区政府大楼',
                projectType: 'engineering',
                tenderUnit: '北京市政府采购中心',
                bidManager: '张三',
                estimatedAmount: 800,
                bidDeadline: '2024-06-20',
                bidOpenDate: '2024-06-25',
                description: '政府办公楼智能化系统改造，包括安防、网络、会议系统等',
                status: 'bidding',
                createTime: '2024-06-01 10:00:00'
            },
            {
                id: 2,
                projectName: '办公设备采购项目',
                projectCode: 'ZB-2024-002',
                projectAddress: '北京市朝阳区科技园区',
                projectType: 'procurement',
                tenderUnit: '小石头科技有限公司',
                bidManager: '李四',
                estimatedAmount: 200,
                bidDeadline: '2024-06-15',
                bidOpenDate: '2024-06-18',
                description: '采购电脑、打印机、办公家具等设备',
                status: 'successful',
                createTime: '2024-06-05 14:30:00'
            }
        ]
        pagination.total = tableData.value.length

        // TODO: 等后端接口完成后使用真实接口
        // const res = await getProjectBidList({
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
        tenderUnit: '',
        bidManager: '',
        estimatedAmount: undefined,
        bidDeadline: '',
        bidOpenDate: '',
        description: '',
        status: 'draft'
    })
    formRef.value?.resetFields()
    dialogVisible.value = true
}

// 编辑
const handleEdit = (row: ProjectBidInfo) => {
    dialogTitle.value = '编辑项目'
    Object.assign(form, row)
    dialogVisible.value = true
}

// 删除
const handleDelete = async (row: ProjectBidInfo) => {
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
        // await deleteProjectBid(row.id!)
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
                    // await updateProjectBid(form)
                    ElMessage.success('更新成功')
                } else {
                    // TODO: 等后端接口完成后使用真实接口
                    // await addProjectBid(form)
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
.project-bid-container {
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