<template>
    <div class="profile-card">
        <div class="profile-title">个人中心</div>
        <el-form
                ref="formRef"
                :model="data.user"
                :rules="data.rules"
                label-width="80px"
                class="profile-form"
        >

            <el-form-item prop="avatar" label="头像">
                <el-upload
                    action="http://127.0.0.1:8080/file/upload"
                    :headers="{token: data.user.token}"
                    :on-success="handleFileSuccess"
                    :file-list="fileList"
                    :auto-upload="true"
                    class="avatar-uploader"
                >
                    <!-- 头像预览容器 -->
                    <div class="avatar-preview">
                        <!-- 已有头像时显示图片 -->
                        <img v-if="data.user.avatar" :src="data.user.avatar" class="avatar-img" />
                        <!-- 无头像时显示占位符 -->
                        <div v-else class="avatar-placeholder">点击上传</div>
                        <!-- 左下角编辑按钮 -->
                        <div class="avatar-edit-btn">
                            <el-button type="text" icon="Edit" size="small">编辑</el-button>
                        </div>
                    </div>
                </el-upload>
            </el-form-item>

            <el-form-item prop="name" label="名称">
                <el-input v-model="data.user.name" autocomplete="off"/>
            </el-form-item>
            <el-form-item prop="major" label="专业">
                <el-input v-model="data.user.major" autocomplete="off"/>
            </el-form-item>
            <el-form-item prop="grade" label="年级">
                <el-input v-model="data.user.grade" autocomplete="off"/>
            </el-form-item>
            <el-form-item prop="college" label="学院">
                <el-input v-model="data.user.college" autocomplete="off"/>
            </el-form-item>

        </el-form>
        <div style="text-align: center">
            <el-button type="primary" style="padding: 18px 35px" @click="update">保存</el-button>
        </div>
    </div>
</template>

<script setup>
import {reactive, onMounted, ref} from "vue";
import {ElMessage} from "element-plus";
import request from "@/utils/request.js";
const formRef = ref()

const data = reactive({
    user: JSON.parse(localStorage.getItem("user") || "{}"),
    rules: {
        name: [{required: true, message: "请输入名称", trigger: "blur"}],
    },
});

// 用于 el-upload 管理预览文件列表
const fileList = reactive([]);

// 页面挂载时，若已有头像则加入文件列表
onMounted(() => {
    if (data.user.avatar) {
        fileList.push({
            url: data.user.avatar,
            name: "avatar",
        });
    }
});

// 上传成功回调：更新头像地址 & 文件列表
const handleFileSuccess = (res) => {
    if (res.code === 200) { // 假设后端返回 { code: 200, data: { url: "图片地址" } }
        data.user.avatar = res.data.url;
        // 保持文件列表只有一个头像（替换逻辑）
        fileList.splice(0, fileList.length, {
            url: res.data.url,
            name: "avatar",
        });
    }
};
const emit = defineEmits(['updateUser'])
const update = () => {
    let url
    // formRef 表单的验证
    formRef.value.validate((valid) => {
        if (valid) { // 表单验证成功
            if (data.user.role =="ADMIN") {
                url = '/admin/update'
            } else if (data.user.role =="STUDENT") {
                url = '/student/update'
            } else {
                ElMessage.error("出现错误 请查看数据库")
            }
            request.put(url, data.user).then(res => {
                if (res.code === '200') {
                    ElMessage.success("修改成功")
                    localStorage.setItem("user",JSON.stringify(data.user))
                    emit('updateUser')
                } else {
                    ElMessage.error(res.msg)
                }
            })
        }
    })
}
</script>

<style scoped>
/* 头像上传容器尺寸 */
.avatar-uploader {
    width: 160px;
    height: 160px;
}

/* 头像预览容器：圆形、相对定位（用于按钮绝对定位） */
.avatar-preview {
    position: relative;
    width: 160px;
    height: 160px;
    overflow: hidden; /* 裁剪超出圆形的内容 */
    border: 1px solid #ddd;
}

/* 头像图片：填充容器 */
.avatar-img {
    width: 100%;
    height: 100%;
    object-fit: cover; /* 保持比例，覆盖容器 */
}

/* 无头像时的占位符 */
.avatar-placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #f5f7fa;
    color: #909399;
}

/* 编辑按钮容器：绝对定位在左下角 */
.avatar-edit-btn {
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    background-color: rgba(0, 0, 0, 0.3); /* 半透明背景 */
    padding: 4px 0;
    text-align: center;
}

/* 编辑按钮样式：白色文字、紧凑布局 */
:deep(.avatar-edit-btn .el-button) {
    color: #fff;
    padding: 0;
}

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
}
</style>
