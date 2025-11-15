<template>
    <!-- 模板内容保持不变 -->
    <div class="admin-container">
        <!-- 头部区域开始 -->
        <header class="header-container">
            <div class="logo-container">
                <el-icon class="logo-icon">
                    <LayoutDashboard/>
                </el-icon>
                <span class="logo-text">数据管理平台</span>
            </div>

            <div class="header-content">
                <el-breadcrumb class="breadcrumb" separator="/">
                    <el-breadcrumb-item :to="{ path: '/manager/home' }">首页</el-breadcrumb-item>
                    <span>{{ router.currentRoute.value.meta.name}}</span>
                </el-breadcrumb>

                <div class="header-actions">
                    <el-button size="small" icon="Search" circle class="action-btn"/>
                    <el-button size="small" icon="Bell" circle class="action-btn">
                    </el-button>
                    <el-dropdown placement="bottom-end">
                        <div class="user-info">
                            <img v-if="data.user?.avatar" class="avatar" :src=" data.user?.avatar "/>
                            <el-avatar v-else icon="User" class="avatar"/>
                            <span class="username">{{ data.user?.name }}</span>
                            <el-icon class="arrow-icon">
                                <ChevronDown/>
                            </el-icon>
                        </div>
                        <template #dropdown>
                            <el-dropdown-menu>
                                <el-dropdown-item icon="User" @click="router.push('/manager/person')">个人信息</el-dropdown-item>
                                <el-dropdown-item icon="Lock" @click="router.push('/manager/upadatePassward')">修改密码</el-dropdown-item>
                                <el-dropdown-item @click="Logout" icon="Remove" divided>退出登录</el-dropdown-item>
                            </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                </div>
            </div>
        </header>
        <!-- 头部区域结束 -->

        <!-- 下方区域开始   -->
        <div class="main-container">
            <!--  菜单区域开始    -->
            <aside class="sidebar-container">
                <el-menu
                    default-active="/manager/home"
                    class="sidebar-menu"
                    router
                    :collapse-transition="false"
                >
                    <el-menu-item index="/manager/home">
                        <el-icon>
                            <House/>
                        </el-icon>
                        <span>首页</span>
                    </el-menu-item>
                    <el-menu-item index="/manager/Club">
                        <el-icon>
                            <House/>
                        </el-icon>
                        <span>社团信息</span>
                    </el-menu-item>
                    <el-sub-menu index="system" v-if="data.user.role === 'ADMIN'">
                        <template #title>
                            <el-icon><User /></el-icon>
                            <span>角色管理</span>
                        </template>
                        <!-- 管理员信息：仅ADMIN可见 -->
                        <el-menu-item index="/manager/admin">管理员信息</el-menu-item>
                        <!-- 学生信息：ADMIN和LEADER可见 -->
                        <el-menu-item index="/manager/student">学生信息</el-menu-item>
                    </el-sub-menu>

                    <el-menu-item index="/manager/notice">
                        <el-icon>
                            <House/>
                        </el-icon>
                        <span>系统公告</span>
                    </el-menu-item>
                    <el-menu-item index="/manager/ApplicationManage">
                        <el-icon>
                            <House/>
                        </el-icon>
                        <span>申请</span>
                    </el-menu-item>
                    <el-menu-item index="/manager/clubMemberManage">
                        <el-icon>
                            <House/>
                        </el-icon>
                        <span>社团成员管理</span>
                    </el-menu-item>
                </el-menu>
            </aside>
            <!--  菜单区域结束    -->

            <main class="content-container">
                <!-- 主内容区域 -->
                <div class="main-content">
                    <RouterView @updateUser="updateUser"/>
                </div>
            </main>
            <!--  数据区域结束    -->
        </div>
        <!-- 下方区域结束   -->
    </div>
</template>

<script setup>
// 脚本内容保持不变
import {reactive} from 'vue'
import router from "../router/index.js";

const data = reactive({
    user: JSON.parse(localStorage.getItem('user'))
})

const Logout = () => {
    localStorage.removeItem('user')
    location.href = '/login'
}
const updateUser = () => {
    data.user = JSON.parse(localStorage.getItem('user') || '{}')
}
</script>

<style scoped>
.admin-container {
    display: flex;
    flex-direction: column;
    min-height: 100vh;
    background-color: #f5f7fa; /* 更柔和的底色 */
}

/* 头部样式优化 */
.header-container {
    height: 64px;
    display: flex;
    background-color: #fff;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06); /* 更细腻的阴影 */
    z-index: 10;
    transition: all 0.3s ease;
}

.logo-container {
    width: 240px;
    display: flex;
    align-items: center;
    background: linear-gradient(135deg, #2c82ff, #1a73e8); /* 渐变主题色 */
    color: white;
    padding: 0 24px;
    transition: width 0.3s ease, background 0.3s;
}

.logo-container:hover {
    background: linear-gradient(135deg, #1a73e8, #0d66d0); /* hover加深渐变 */
}

.logo-icon {
    font-size: 26px;
    margin-right: 12px;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1); /* 图标轻微阴影 */
}

.logo-text {
    font-size: 18px;
    font-weight: 600;
    letter-spacing: 0.8px;
}

.header-content {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 28px; /* 增加内边距更舒展 */
}

.breadcrumb {
    font-size: 14px;
    color: #666;
    padding: 6px 0; /* 增加点击区域 */
}

:deep(.breadcrumb .el-breadcrumb__item:not(:last-child) .el-breadcrumb__inner)  {
    transition: color 0.2s;
}

:deep(.breadcrumb .el-breadcrumb__item:not(:last-child) .el-breadcrumb__inner:hover)  {
    color: #2c82ff; /* 面包屑父级hover效果 */
}

:deep(.breadcrumb .el-breadcrumb__item:last-child)  {
    color: #2c82ff;
    font-weight: 500;
}

.header-actions {
    display: flex;
    align-items: center;
    gap: 16px; /* 增加间距避免拥挤 */
}

.action-btn {
    background-color: #f0f5ff; /* 与主题色呼应的浅背景 */
    color: #666;
    width: 36px;
    height: 36px;
    transition: all 0.25s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.action-btn:hover {
    background-color: #e6f0ff;
    color: #2c82ff;
    transform: translateY(-2px) scale(1.05); /* 轻微上浮+放大 */
    box-shadow: 0 3px 8px rgba(44, 130, 255, 0.2);
}

/* 用户信息区域优化 */
.user-info {
    display: flex;
    align-items: center;
    cursor: pointer;
    padding: 6px 14px;
    border-radius: 22px; /* 更圆润的圆角 */
    transition: all 0.3s ease;
}

.user-info:hover {
    background-color: #f0f5ff;
    box-shadow: 0 2px 8px rgba(44, 130, 255, 0.1);
}

.avatar {
    width: 38px;
    height: 38px;
    border-radius: 50%;
    object-fit: cover;
    border: 2px solid transparent;
    transition: all 0.3s;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
}

.user-info:hover .avatar {
    border-color: #2c82ff;
    transform: scale(1.05); /* 头像轻微放大 */
}

.username {
    margin: 0 10px;
    font-size: 14px;
    font-weight: 500;
    color: #333;
}

.arrow-icon {
    font-size: 16px;
    color: #999;
    transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.user-info:hover .arrow-icon {
    transform: rotate(180deg) scale(1.1);
    color: #2c82ff;
}

/* 主内容区域样式优化 */
.main-container {
    display: flex;
    flex: 1;
    overflow: hidden;
}

/* 侧边栏样式优化 */
.sidebar-container {
    width: 240px;
    background-color: #0f172a;
    transition: width 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
    box-shadow: 1px 0 5px rgba(0, 0, 0, 0.1);
    z-index: 5;
}

.sidebar-menu {
    min-height: calc(100vh - 64px);
    border-right: none;
    background-color: #0f172a;
    color: #94a3b8;
    padding-top: 20px; /* 增加顶部间距 */
}
:deep(.sidebar-menu .el-menu-item),
:deep(.sidebar-menu .el-sub-menu__title) {
    color: #94a3b8;
    height: 54px; /* 增加高度提升点击体验 */
    line-height: 54px;
    padding-left: 28px !important; /* 左移增加内部空间 */
    transition: all 0.25s ease;
    border-left: 3px solid transparent;
}

:deep(.sidebar-menu .el-menu-item:hover),
:deep(.sidebar-menu .el-sub-menu__title:hover) {
    color: #fff;
    background-color: #1e293b;
    border-left-color: rgba(44, 130, 255, 0.6);
    padding-left: 32px !important; /* hover时右移增强动效 */
}

:deep(.sidebar-menu .el-menu-item.is-active) {
    color: #fff;
    background-color: #4177c4;
    border-left-color: #2c82ff;
    font-weight: 500;
    box-shadow: inset 0 0 10px rgba(44, 130, 255, 0.15); /* 内阴影增强选中感 */
}

:deep(.sidebar-menu .el-icon) {
    margin-right: 14px;
    font-size: 19px;
    width: 22px;
    text-align: center;
}

/* 子菜单展开动画 */
:deep(.el-sub-menu .el-sub-menu__list) {
    background-color: #172035 !important; /* 子菜单更深色背景 */
}

:deep(.el-sub-menu .el-menu-item) {
    padding-left: 50px !important; /* 子菜单缩进更明显 */
}

:deep(.el-sub-menu .el-menu-item:hover) {
    padding-left: 54px !important;
}

/* 内容区域样式优化 */
.content-container {
    flex: 1;
    padding: 24px;
    overflow-y: auto;
    background-color: #f5f7fa;
}

.main-content {
    min-height: calc(100% - 20px);
    background-color: #fff;
    border-radius: 10px; /* 更大圆角更现代 */
    padding: 28px;
    box-shadow: 0 3px 15px rgba(0, 0, 0, 0.05);
    transition: all 0.3s ease;
}

.main-content:hover {
    box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
}
</style>
