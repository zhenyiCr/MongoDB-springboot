<template>
    <div class="page-container">
        <!-- 页面标题 -->
        <div class="page-header">
            <h2>社团成员管理</h2>
            <p class="header-desc">管理社团成员信息，可按社团筛选查看</p>
        </div>

        <!-- 筛选区域 -->
        <div class="filter-container" v-if="data.user?.role === 'ADMIN'">
            <el-select
                    v-model="data.selectedClubId"
                    placeholder="请选择社团"
                    class="club-select"
                    @change="handleClubChange"
                    :disabled="data.clubSelectDisabled"
            >
                <el-option
                        v-for="club in data.clubList"
                        :key="club.id"
                        :label="club.name"
                        :value="club.id"
                />
            </el-select>
            <!-- 添加成员按钮：仅ADMIN和LEADER可见 -->
            <el-button
                    type="primary"
                    @click="showAddMemberDialog"
            >
                <el-icon><Plus /></el-icon>
                添加成员
            </el-button>
        </div>

        <!-- 表格操作列：LEADER只能操作自己社团的成员 -->
        <el-table
                :data="data.tableData"
                border
                class="member-table"
                :header-cell-style="headerCellStyle"
        >
            <el-table-column prop="studentId" label="学生学号"/>
            <el-table-column prop="studentName" label="学生姓名"/>
            <el-table-column prop="clubName" label="社团名称"/>
            <el-table-column prop="role" label="角色">
                <template #default="scope">
                    <el-tag :type="scope.row.role === 'LEADER' ? 'primary' : 'success'">
                        {{ scope.row.role === 'LEADER' ? '社长' : '成员' }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="status" label="状态">
                <template #default="scope">
                    <el-tag :type="scope.row.status === 'ACTIVE' ? 'success' : 'warning'">
                        {{ scope.row.status === 'ACTIVE' ? '活跃' : '非活跃' }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="joinTime" label="加入时间"/>
            <el-table-column label="操作" width="200" v-if="data.user.role === 'ADMIN' || data.user.clubRole === 'LEADER'">
                <template #default="scope">
                    <el-button
                            size="small"
                            @click="handleUpdateRole(scope.row)"
                            v-if="data.user.role === 'ADMIN'"
                    >
                        修改角色
                    </el-button>
                    <el-button
                            size="small"
                            type="danger"
                            @click="handleRemoveMember(scope.row.id)"
                            v-if="data.user.role === 'ADMIN' || (data.user.clubRole === 'LEADER' && scope.row.studentId !== data.user.id)"
                    >
                        移除
                    </el-button>
                </template>
            </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container">
            <el-pagination
                    v-model:current-page="data.pageNum"
                    v-model:page-size="data.pageSize"
                    layout="total, prev, pager, next"
                    :total="data.total"
                    @current-change="getData"
                    class="pagination"
            />
        </div>

        <!-- 添加成员弹窗 -->
        <el-dialog
                title="添加社团成员"
                v-model="data.addDialogVisible"
                width="500px"
        >
            <el-form
                    ref="addFormRef"
                    :model="data.addForm"
                    :rules="data.addRules"
                    label-width="100px"
            >
                <el-form-item prop="clubId" label="社团ID">
                    <el-input v-model="data.addForm.clubId" readonly />
                </el-form-item>
                <el-form-item prop="studentId" label="学生ID">
                    <el-input v-model="data.addForm.studentId" placeholder="请输入学生ID" />
                </el-form-item>
                <el-form-item prop="role" label="角色">
                    <el-radio-group v-model="data.addForm.role">
                        <el-radio label="MEMBER">成员</el-radio>
                        <el-radio label="LEADER">社长</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="data.addDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="submitAddMember">确定</el-button>
            </template>
        </el-dialog>

        <!-- 修改角色弹窗 -->
        <el-dialog
                title="修改成员角色"
                v-model="data.roleDialogVisible"
                width="400px"
        >
            <el-form
                    ref="roleFormRef"
                    :model="data.roleForm"
                    label-width="100px"
            >
                <el-form-item label="成员">
                    <span>{{ data.roleForm.studentName }}</span>
                </el-form-item>
                <el-form-item prop="role" label="新角色">
                    <el-radio-group v-model="data.roleForm.role">
                        <el-radio label="MEMBER">成员</el-radio>
                        <el-radio label="LEADER">社长</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="data.roleDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="submitUpdateRole">确定</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { reactive, ref, computed, onMounted } from 'vue';
import { Plus } from '@element-plus/icons-vue';
import request from '@/utils/request.js';
import { ElMessage, ElMessageBox } from 'element-plus';

const data = reactive({
    user: JSON.parse(localStorage.getItem('user') || '{}'),
    clubList: [],
    tableData: [],
    pageNum: 1,
    pageSize: 10,
    total: 0,
    selectedClubId: '',

    // 添加成员相关
    addDialogVisible: false,
    clubSelectDisabled: false, // 新增：是否禁用社团选择器
    addForm: {
        clubId: '',
        studentId: '',
        role: 'MEMBER'
    },
    addRules: {
        clubId: [{ required: true, message: '请选择社团', trigger: 'blur' }],
        studentId: [{ required: true, message: '请输入学生ID', trigger: 'blur' }]
    },

    // 修改角色相关
    roleDialogVisible: false,
    roleForm: {
        id: '',
        role: '',
        studentName: ''
    }
});

const addFormRef = ref();
const roleFormRef = ref();

// 新增：判断是否有成员操作权限
const hasOperationPermission = (row) => {
    if (data.user.role === 'ADMIN') return true;
    if (data.user.role === 'LEADER') {
        // LEADER只能操作自己社团的成员
        return row.clubId === data.selectedClubId;
    }
    return false
}

// 表格头部样式
const headerCellStyle = computed(() => ({
    'background-color': '#f5f7fa',
    'font-weight': 'bold'
}))

// 获取社团列表时，LEADER默认选中自己的社团
const getClubs = () => {
    request.get('/club/selectAll').then(res => {
        if (res.code === '200') {
            data.clubList = res.data;
            // LEADER默认选中自己的社团，且禁用选择器
            if (data.user.role === 'LEADER' && data.clubList.length > 0) {
                data.selectedClubId = data.clubList[0].id;
                data.clubSelectDisabled = true; // 禁用选择器
                handleClubChange(); // 加载成员列表
            }
        }
    });
};

// 获取成员列表
const getData = () => {
    request.get('/clubMember/selectPage', {
        params: {
            pageNum: data.pageNum,
            pageSize: data.pageSize,
            clubId: data.selectedClubId
        }
    }).then(res => {
        if (res.code === '200') {
            data.tableData = res.data.list;
            data.total = res.data.total;
        }
    });
};

// 切换社团
const handleClubChange = () => {
    data.pageNum = 1;
    getData();
    // 同步设置添加成员表单中的社团ID
    data.addForm.clubId = data.selectedClubId;
};

// 显示添加成员弹窗
const showAddMemberDialog = () => {
    if (!data.selectedClubId) {
        ElMessage.warning('请先选择社团');
        return;
    }
    data.addForm = {
        clubId: data.selectedClubId,
        studentId: '',
        role: 'MEMBER'
    };
    data.addDialogVisible = true;
};

// 提交添加成员
const submitAddMember = () => {
    addFormRef.value.validate(valid => {
        if (valid) {
            request.post('/clubMember/add', data.addForm).then(res => {
                if (res.code === '200') {
                    ElMessage.success('添加成功');
                    data.addDialogVisible = false;
                    getData();
                } else {
                    ElMessage.error(res.msg);
                }
            });
        }
    });
};

// 处理修改角色
const handleUpdateRole = (row) => {
    data.roleForm = {
        id: row.id,
        role: row.role,
        studentName: row.studentName
    };
    data.roleDialogVisible = true;
};

// 提交修改角色
const submitUpdateRole = () => {
    request.put('/clubMember/updateRole', data.roleForm).then(res => {
        if (res.code === '200') {
            ElMessage.success('修改成功');
            data.roleDialogVisible = false;
            getData();
        } else {
            ElMessage.error(res.msg);
        }
    });
};

// 移除成员
const handleRemoveMember = (id) => {
    ElMessageBox.confirm('确定要移除该成员吗？', '提示', {
        type: 'warning'
    }).then(() => {
        request.delete(`/clubMember/delete/${id}`).then(res => {
            if (res.code === '200') {
                ElMessage.success('移除成功');
                getData();
            } else {
                ElMessage.error(res.msg);
            }
        });
    });
};

// 页面加载时获取数据
onMounted(() => {
    getClubs();
    getData();
});
</script>

<style scoped>
.page-container {
    padding: 20px;
}

.filter-container {
    display: flex;
    margin: 20px 0;
    gap: 10px;
}

.club-select {
    width: 250px;
}

.member-table {
    width: 100%;
    margin-bottom: 20px;
}

.pagination-container {
    text-align: right;
}
</style>
