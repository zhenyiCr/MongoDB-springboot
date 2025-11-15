<template>
    <div class="profile-card">
        <div class="profile-title">修改密码</div>
        <div>
            <el-form ref="formRef"
                     :model="data.form"
                     :rules="data.rules"
                     label-width="80px"
                     class="profile-form">
                <el-form-item prop="oldPassword" label="原密码">
                    <el-input size="large" show-password v-model="data.form.oldPassword" autocomplete="off"
                              prefix-icon="Lock"
                              placeholder="请输入原密码"/>
                </el-form-item>
                <el-form-item prop="newPassword" label="新密码">
                    <el-input size="large" show-password v-model="data.form.newPassword" autocomplete="off"
                              prefix-icon="Lock"
                              placeholder="请输入新密码"/>
                </el-form-item>
                <el-form-item prop="confirmPassword" label="再次输出">
                    <el-input size="large" show-password v-model="data.form.confirmPassword" autocomplete="off"
                              prefix-icon="Lock"
                              placeholder="请再次输入新密码"/>
                </el-form-item>
            </el-form>
            <div style="text-align: center">
                <el-button type="primary" style="padding: 18px 35px" @click="updatePassword">保存</el-button>
            </div>
        </div>
    </div>
</template>

<script setup>
import {reactive, ref} from "vue";
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";

const validatePass = (rule, value, callback) => {
    // value 表示用户输入的确认密码
    if (value !== data.form.newPassword) {
        callback(new Error("两次密码不一样"))
    } else {
        callback()
    }
}
const formRef = ref()

const data = reactive({
    user: JSON.parse(localStorage.getItem("user") || "{}"),
    form:{},
    rules: {
        oldPassword: [
            {required: true, message: '请输入密码', trigger: 'blur'},
        ],
        newPassword: [
            {required: true, message: '请输入密码', trigger: 'blur'},
        ],
        confirmPassword :[
            {required: true, message: '请再次输入新密码', trigger: 'blur'},
            {validator : validatePass,trigger: 'blur'}
        ]
    },
})

const updatePassword = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            // 仅传递密码相关数据，用户身份由请求头的token确定
            request.post('/updatePassword', data.form).then(res => {
                if (res.code === '200') {
                    ElMessage.success("修改成功");
                    setTimeout(() => {
                        localStorage.removeItem("user");
                        router.push("/login");
                    }, 500);
                } else {
                    ElMessage.error(res.msg);
                }
            })
        }
    })
}
</script>

<style scoped>
/* 原有样式保持不变 */
.profile-card {
    width: 600px;
    margin: 40px auto;
    background-color: #fff;
    opacity: 0.95;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    padding: 30px;
}

.profile-title {
    font-size: 22px;
    font-weight: bold;
    color: #333;
    margin-bottom: 24px;
    padding-bottom: 12px;
    border-bottom: 1px solid #eee;
}
.profile-form {
    padding: 0 20px;
}

:deep(.el-form .el-form-item) {
    margin-bottom: 20px;
}

:deep(.el-form .el-form-item .el-input .el-input__wrapper) {
    border-radius: 4px;
    border-color: #ddd;
    transition: all 0.3s ease;
}

:deep(.el-form .el-form-item .el-input .el-input__wrapper:focus) {
    border-color: #409eff;
    box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

:deep(.el-form-item__label) {
    font-size: 16px;
    white-space: nowrap;
}
</style>
