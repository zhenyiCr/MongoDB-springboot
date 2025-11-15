<template>
    <div>
        <div class="card">
            <div>
                <el-input clearable @clear="getData"
                          style="margin-right: 10px;width: 260px;margin-bottom: 7px;margin-top: 8px"
                          placeholder="请输入标题"
                          :prefix-icon="Search"
                          v-model="data.title"></el-input>
                <el-button type="primary" @click="getData">查询</el-button>
                <el-button @click="reset">重置</el-button>
            </div>


            <div style="margin-bottom: 5px;margin-top: 5px">
                <el-button @click="headleAdd" type="primary">新增</el-button>
            </div>
        </div>
        <div>
            <el-table :data="data.tableData" style="width: 100%"
                      :header-cell-style="{fontWeight:'bold',background:'#f5f5f5'}">
                <el-table-column prop="title" label="标题"/>
                <el-table-column prop="content" label="内容"/>
                <el-table-column prop="time" label="发布时间"/>
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
        <el-dialog title="公告信息" v-model="data.formVisible" width="500" destroy-on-close>
            <el-form ref="formRef" :model="data.form" :rules="data.rules" label-width="80px"
                     style="padding:20px 30px 20px 0">
                <el-form-item prop="title" label="标题">
                    <el-input v-model="data.form.title" autocomplete="off"/>
                </el-form-item>
                <el-form-item prop="content" label="内容">
                    <el-input type="textarea" rows="4" v-model="data.form.content" autocomplete="off"/>
                </el-form-item>
                <!--                <el-form-item prop="time" label="发布时间">-->
                <!--                    <el-date-picker v-model="data.form.time" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"-->
                <!--                                    placeholder="选择发布时间"/>-->
                <!--                </el-form-item>-->
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
    title: null,
    pageNum: 1,
    pageSize: 10,
    total: 0,
    tableData: [],
    formVisible: false,
    form: {},
    rules: {
        title: [
            {required: true, message: '请输入标题', trigger: 'blur'},
        ],
        content: [
            {required: true, message: '请输入内容', trigger: 'blur'},
        ]
    },
})

const formRef = ref()

const getData = () => {
    request.get('/notice/selectPage', {
            params: {
                pageNum: data.pageNum,
                pageSize: data.pageSize,
                title: data.title
            }
        }
    ).then(res => {
        if (res.code === '200') {
            data.tableData = res.data?.list
            data.total = res.data?.total
        } else {
            ElMessage.error(res.msg)
        }
    })
}
getData()
const reset = () => {
    data.title = null
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
            request.post('/notice/add', data.form).then(res => {
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
            request.put('/notice/update', data.form).then(res => {
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
        request.delete('/notice/delete/' + id).then(res => {
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

</script>

<style scoped>

</style>
