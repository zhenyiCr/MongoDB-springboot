<template>
    <div class="page-container">
        <!-- 页面标题 -->
        <div class="page-header">
            <h2>社团申请管理</h2>
            <p class="header-desc">
                {{ data.user.role === 'STUDENT' ? '在这里提交你的社团加入申请' : '管理所有社团申请的审核工作' }}
            </p>
        </div>

        <!-- 学生视角：提交申请 -->
        <el-card v-if="data.user.role === 'STUDENT'" class="submit-card">
            <el-button
                type="primary"
                @click="data.formVisible = true"
                class="submit-btn"
            >
                <el-icon><Plus /></el-icon>
                提交社团申请
            </el-button>

            <el-dialog
                title="申请加入社团"
                v-model="data.formVisible"
                width="500px"
                :close-on-click-modal="false"
                class="application-dialog"
            >
                <el-form
                    ref="formRef"
                    :model="data.form"
                    :rules="data.rules"
                    label-width="80px"
                    class="application-form"
                >
                    <el-form-item prop="clubId" label="社团名称">
                        <el-select v-model="data.form.clubId"
                                   placeholder="请选择社团">
                            <el-option
                                v-for="item in data.clubData"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id"
                            />
                        </el-select>
                    </el-form-item>
                    <el-form-item prop="reason" label="申请理由">
                        <el-input
                            type="textarea"
                            rows="4"
                            v-model="data.form.reason"
                            placeholder="请说明申请理由（例如：我对该社团的活动很感兴趣...）"
                            class="form-textarea"
                        />
                    </el-form-item>
                </el-form>
                <template #footer>
                    <el-button
                        @click="data.formVisible = false"
                        class="dialog-btn cancel-btn"
                    >
                        取消
                    </el-button>
                    <el-button
                        type="primary"
                        @click="submitApplication"
                        class="dialog-btn confirm-btn"
                    >
                        提交申请
                    </el-button>
                </template>
            </el-dialog>
        </el-card>

        <!-- 公共：申请列表 -->
        <div class="list-container">
            <div class="search-bar">
                <el-select
                    v-model="data.searchStatus"
                    placeholder="请选择状态"
                    class="status-input"
                    clearable
                    @clear="getData"
                >
                    <el-option
                        label="待审核"
                        value="PENDING"
                    />
                    <el-option
                        label="已通过"
                        value="APPROVED"
                    />
                    <el-option
                        label="已拒绝"
                        value="REJECTED"
                    />
                </el-select>
                <el-button
                    @click="getData"
                    class="search-btn"
                >
                    <el-icon><Search /></el-icon>
                    查询
                </el-button>
                <el-button @click="reset">重置</el-button>
            </div>

            <el-table
                :data="data.tableData"
                border
                class="application-table"
                :header-cell-style="headerCellStyle"
                :row-class-name="tableRowClassName"
            >
                <el-table-column prop="studentName" label="学生名字"/>
                <el-table-column prop="clubName" label="社团名称"/>
                <el-table-column prop="reason" label="申请理由" min-width="200"/>
                <el-table-column prop="remark" label="审核理由" min-width="200"/>
                <el-table-column prop="status" label="状态" width="140">
                    <template #default="scope">
                        <el-tag
                            :type="scope.row.status === 'APPROVED' ? 'success' : scope.row.status === 'REJECTED' ? 'danger' : 'warning'"
                            class="status-tag"
                        >
                            {{ formatStatus(scope.row.status) }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="申请时间" width="180"/>
                <!-- 管理员操作列 -->
                <el-table-column label="操作" width="180" v-if="data.user.role === 'ADMIN' || data.user.clubRole === 'LEADER'">
                    <template #default="scope">
                        <el-button
                            type="success"
                            size="small"
                            @click="handleApprove(scope.row, 'APPROVED')"
                            v-if="canApprove(scope.row)"
                            class="table-btn approve-btn"
                        >
                            <el-icon><Check /></el-icon>
                            通过
                        </el-button>
                        <el-button
                            type="danger"
                            size="small"
                            @click="handleApprove(scope.row, 'REJECTED')"
                            v-if="canApprove(scope.row)"
                            class="table-btn reject-btn"
                        >
                            <el-icon><Close /></el-icon>
                            拒绝
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <div class="pagination-container">
                <el-pagination
                    v-model:current-page="data.pageNum"
                    v-model:page-size="data.pageSize"
                    :total="data.total"
                    layout="total, prev, pager, next"
                    @current-change="getData"
                    class="pagination"
                />
            </div>
        </div>
    </div>
</template>

<script setup>
import { reactive, ref, computed } from 'vue';
import request from '@/utils/request.js';
import { ElMessage } from 'element-plus';
import { Plus, Search, Check, Close } from '@element-plus/icons-vue';

const data = reactive({
    user: JSON.parse(localStorage.getItem('user') || '{}'),
    formVisible: false,
    form: { clubId: '', reason: '' },
    tableData: [],
    pageNum: 1,
    pageSize: 10,
    total: 0,
    searchStatus: null,
    clubData: [],
    rules: {
        clubId: [{ required: true, message: '请输入社团ID', trigger: 'blur' }],
    }
})

const formRef = ref()

// 新增：判断是否有审核权限
const canApprove = (row) => {
    // 核心：只有“待审核”的申请才允许操作
    if (row.status !== 'PENDING') return false;

    if (data.user.role === 'ADMIN') return true;
    if (data.user.role === 'STUDENT' && data.user.clubRole === 'LEADER') {
        return row.clubId === data.user.clubId;
    }
    return false;
};

// 表格样式相关计算属性
const headerCellStyle = computed(() => ({
    'background-color': '#f5f7fa',
    'font-weight': 'bold',
    'color': '#333'
}))

// 格式化状态显示
const formatStatus = (status) => {
    const statusMap = {
        'PENDING': '待审核',
        'APPROVED': '已通过',
        'REJECTED': '已拒绝'
    };
    return statusMap[status] || status;
}

// 表格行样式
const tableRowClassName = ({ row, rowIndex }) => {
    return rowIndex % 2 === 0 ? 'even-row' : 'odd-row';
}

// 获取申请列表
const getData = () => {
    // 构建查询参数
    const params = {
        pageNum: data.pageNum,
        pageSize: data.pageSize,
        status: data.searchStatus
    };
    // 社长（社团内角色LEADER）需添加社团ID筛选
    if (data.user.role === 'STUDENT' && data.user.clubRole === 'LEADER') {
        params.clubId = data.user.clubId; // 假设user对象中存储了所属社团ID
    }
    request.get('/application/selectPage', { params }).then(res => {
        if (res.code === '200') {
            data.tableData = res.data.content || [];
            data.total = res.data.totalElements || 0;
        }
    })
}
// 重置查询条件
const reset = () => {
    data.searchStatus = null;
    getData();
}
// 提交申请
const submitApplication = () => {
    formRef.value.validate(valid => {
        if (valid) {
            request.post('/application/add', data.form).then(res => {
                if (res.code === '200') {
                    ElMessage.success('申请提交成功');
                    data.formVisible = false;
                    data.form = { clubId: '', reason: '' };
                    getData();
                } else {
                    ElMessage.error(res.msg);
                }
            })
        }
    })
}

// 审核申请
const handleApprove = (row, status) => {
    if (!canApprove(row)) {
        ElMessage.warning('无权限审核该申请');
        return;
    }
    let remark = prompt('请输入理由：');
    if (status === 'APPROVED') {
        // 先调用添加成员接口
        request.post('/clubMember/add', {
            clubId: row.clubId,
            studentId: row.studentId
        }).then(memberRes => {
            if (memberRes.code === '200') {
                // 成员添加成功，再更新申请状态
                request.put('/application/approve', {
                    id: row.id,
                    status: status,
                    remark
                }).then(res => {
                    if (res.code === '200') {
                        ElMessage.success('操作成功');
                        getData();
                    } else {
                        ElMessage.error('更新申请状态失败：' + res.msg);
                    }
                });
            } else {
                ElMessage.error('加入社团失败：' + memberRes.msg);
            }
        });
    } else {
        // 拒绝申请时直接更新状态（无需添加成员）
        request.put('/application/approve', {
            id: row.id,
            status: status,
            remark
        }).then(res => {
            if (res.code === '200') {
                ElMessage.success('操作成功');
                getData();
            }
        });
    }
}

// 初始化加载列表
getData()

const loadClubData = () => {
    request.get('/club/selectAll').then(res => {
        if (res.code === '200') {
            data.clubData = res.data;
        }
    })
}
loadClubData()

</script>

<style scoped>
/* 基础样式 */
.page-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
    box-sizing: border-box;
}

/* 页面标题 */
.page-header {
    margin-bottom: 30px;
}

.page-header h2 {
    margin: 0 0 10px 0;
    color: #1f2329;
    font-size: 24px;
    font-weight: 600;
}

.header-desc {
    margin: 0;
    color: #6b7280;
    font-size: 14px;
}

/* 提交申请卡片 */
.submit-card {
    margin-bottom: 25px;
    padding: 15px 20px;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    transition: box-shadow 0.3s ease;
}

.submit-card:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.submit-btn {
    padding: 8px 16px;
    font-size: 14px;
    transition: all 0.2s ease;
}

.submit-btn:hover {
    transform: translateY(-2px);
}

/* 对话框样式 */
.application-dialog {
    --el-dialog-border-radius: 8px;
}

.application-form {
    margin-top: 15px;
}

.form-input, .form-textarea {
    border-radius: 4px;
    transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.form-input:focus, .form-textarea:focus {
    border-color: #409eff;
    box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.dialog-btn {
    padding: 8px 16px;
    font-size: 14px;
    margin-left: 8px;
}

.dialog-btn:first-child {
    margin-left: 0;
}

/* 列表容器 */
.list-container {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

/* 搜索栏 */
.search-bar {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
    gap: 12px;
}

.status-input {
    width: 300px;
}

.search-btn {
    padding: 8px 16px;
    transition: all 0.2s ease;
}

.search-btn:hover {
    transform: translateY(-1px);
}

/* 表格样式 */
.application-table {
    width: 100%;
    border-radius: 6px;
    overflow: hidden;
}

.el-table th {
    padding: 12px 0;
}

.el-table td {
    padding: 12px 0;
}

.even-row {
    background-color: #f9fafb;
}

.odd-row {
    background-color: #fff;
}

/* 状态标签 */
.status-tag {
    padding: 3px 8px;
    border-radius: 4px;
    font-size: 12px;
}

/* 表格按钮 */
.table-btn {
    margin-right: 6px;
    padding: 4px 8px;
    font-size: 12px;
    transition: all 0.2s ease;
}

.table-btn:last-child {
    margin-right: 0;
}

.table-btn:hover {
    transform: scale(1.05);
}

/* 分页容器 */
.pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: center;
}

.pagination {
    --el-pagination-item-border-radius: 4px;
}

/* 响应式调整 */
@media (max-width: 768px) {
    .page-container {
        padding: 15px 10px;
    }

    .search-bar {
        flex-direction: column;
        align-items: stretch;
    }

    .status-input {
        width: 100%;
    }

    .submit-card, .list-container {
        padding: 15px 10px;
    }
}
</style>
