<template>
    <div>
        <div class="card">
            <div>
                <el-input clearable @clear="getData"
                          style="margin-right: 10px;width: 260px;margin-bottom: 7px;margin-top: 8px"
                          placeholder="请输入账户"
                          :prefix-icon="Search"
                          v-model="data.username"></el-input>
                <el-input clearable @clear="getData"
                          style="margin-right: 10px;width: 260px;margin-bottom: 7px;margin-top: 8px"
                          placeholder="请输入姓名"
                          :prefix-icon="Search"
                          v-model="data.name"></el-input>
                <el-button type="primary" @click="getData">查询</el-button>
                <el-button @click="reset">重置</el-button>
            </div>

            <div style="margin-bottom: 5px;margin-top: 5px">
                <el-button @click="headleAdd" type="primary">新增</el-button>
                <el-button @click="deleteBatch" type="danger">批量删除</el-button>

            </div>
        </div>


        <div>
            <el-table :data="data.tableData" style="width: 100%" @selection-change="handleSelectionChange"
                      :header-cell-style="{fontWeight:'bold',background:'#f5f5f5'}">
                <el-table-column type="selection" width="55"/>
                <el-table-column label="头像" width="120px">
                    <template #default="scope">
                        <el-image
                                v-if="scope.row.avatar"
                                :src="scope.row.avatar"
                                :preview-src-list="[scope.row.avatar]"
                                fit="cover"
                                style="width: 40px; height: 40px; border-radius: 50%; display: block"
                        />
                        <!-- 没有头像时显示默认图标或图片 -->
                        <el-avatar v-else icon="User" style="width: 40px; height: 40px;"/>
                    </template>
                </el-table-column>
                <el-table-column prop="username" label="账户"/>
                <el-table-column prop="name" label="姓名"/>
                <el-table-column label="操作" width="150">
                    <template #default="scope">
                        <el-button @click="headleEdit(scope.row)" type="primary" icon="edit"></el-button>
                        <el-button @click="del(scope.row.id)" type="danger" icon="delete"></el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>

        <div style=": margin-top: 10px">
            <el-pagination
                    v-model:current-page="data.pageNum"
                    v-model:page-size="data.pageSize"
                    layout="total, sizes, prev, pager, next, jumper"
                    :page-sizes="[5, 10]"
                    :total="data.total"
                    @current-change="getData"
                    @size-change="getData"
            />
        </div>
        <el-dialog title="管理员信息" v-model="data.formVisible" width="500" destroy-on-close>
            <el-form ref="formRef" :model="data.form" :rules="data.rules" label-width="80px"
                     style="padding:20px 30px 20px 0">
                <el-form-item prop="username" label="账号">
                    <el-input v-model="data.form.username" autocomplete="off"/>
                </el-form-item>
                <el-form-item prop="name" label="名称">
                    <el-input v-model="data.form.name" autocomplete="off"/>
                </el-form-item>
                <el-form-item prop="avatar" label="头像">
                    <el-upload action="http://127.0.0.1:8088/file/upload"
                               :headers="{token: data.user.token}"
                               :on-success="handleFileSuccess"
                               list-type="picture"
                    >
                        <el-button>上传头像</el-button>
                    </el-upload>
                </el-form-item>
            </el-form>
            <template #footer>
                <div>
                    <el-button @click="data.formVisible = false">取 消</el-button>
                    <el-button type="primary" @click="save">提 交</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import {reactive, ref} from "vue"
import {Search} from "@element-plus/icons-vue";
import request from "@/utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";


const data = reactive({
        user: JSON.parse(localStorage.getItem('user') || '{}'),
        username: null,
        name: null,
        pageNum: 1,
        pageSize: 10,
        total: 0,
        tableData: [],
        formVisible: false,
        form: {},
        rules: {
            username: [
                {required: true, message: '请输入账号', trigger: 'blur'},
            ],
            name: [
                {required: true, message: '请输入名称', trigger: 'blur'},
            ],
            phone: []
            , email: [
                {required: true, message: '请输入邮箱', trigger: 'blur'},
            ]
        },
        rows: [],
        ids: [],
    }
)

const formRef = ref()

const getData = () => {
    request.get('/admin/selectPage', {
            params: {
                pageNum: data.pageNum,
                pageSize: data.pageSize,
                name: data.name,
                username: data.username
            }
        }
    ).then(res => {
        if (res.code === '200') {
            data.tableData = res.data.content
            data.total = res.data.totalElements
        } else {
            ElMessage.error(res.msg)
        }
    })
}
getData()
const reset = () => {
    data.username = null
    data.name = null
    getData()
}
const headleAdd = () => {
    data.formVisible = true
    data.form = {}
}
const add = () => {
    // formRef 表单的验证
    formRef.value.validate((valid) => {
        if (valid) { // 表单验证成功
            request.post('/admin/add', data.form).then(res => {
                if (res.code === '200') {
                    ElMessage.success("新增成功")
                    data.formVisible = false
                    getData()
                } else {
                    ElMessage.error(res.msg)
                }
            })
        }
    })
}
const headleEdit = (row) => {
    data.formVisible = true
    data.form = JSON.parse(JSON.stringify(row))
}
const edit = () => {
    // formRef 表单的验证
    formRef.value.validate((valid) => {
        if (valid) { // 表单验证成功
            request.put('/admin/update', data.form).then(res => {
                if (res.code === '200') {
                    ElMessage.success("修改成功")
                    data.formVisible = false
                    getData()
                } else {
                    ElMessage.error(res.msg)
                }
            })
        }
    })
}
const save = () => {
    data.form.id ? edit() : add()
}

const del = (id) => {
    ElMessageBox.confirm(' 你确定删除信息吗', 'Warning', {type: 'warning'}).then(() => {
        request.delete('/admin/delete/' + id).then(res => {
            if (res.code === '200') {
                ElMessage.success("删除成功")
                getData()
            } else {
                ElMessage.error(res.msg)
            }
        }).catch(() => {
        })
    })
}

const handleSelectionChange = (rows) => { // rows 实际选择的数组
    data.rows = rows
    data.ids = data.rows.map(v => v.id) // map可以把对象的数组 转换成纯数字的数组 [1,2,3]
}

const deleteBatch = () => {
    if (data.rows.length === 0) {
        ElMessage.warning("请选择要删除的行")
        return
    }
    ElMessageBox.confirm(' 你确定删除信息吗', 'Warning', {type: 'warning'}).then(() => {
        request.delete('/admin/deleteBatch', {data: data.rows}).then(res => {
            if (res.code === '200') {
                ElMessage.success("删除成功")
                getData()
            } else {
                ElMessage.error(res.msg)
            }
        })
    })
}


const handleFileSuccess = (res) => {
    data.form.avatar = res.data
}
</script>
