<template>
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
        <span>{{ router.currentRoute.value.meta.name }}</span>
        </el-breadcrumb>

        <div class="header-actions">
          <el-button size="small" icon="Search" circle class="action-btn"/>
          <el-button size="small" icon="Bell" circle class="action-btn">

          </el-button>
          <el-dropdown placement="bottom-end">
            <div class="user-info">
              <img
                  class="avatar"
                  src="https://picsum.photos/id/1005/40/40"
                  alt="管理员头像"
              >
              <span class="username">管理员</span>
              <el-icon class="arrow-icon">
                <ChevronDown/>
              </el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item icon="User">个人信息</el-dropdown-item>
                <el-dropdown-item icon="Lock">修改密码</el-dropdown-item>
                <el-dropdown-item icon="Setting">系统设置</el-dropdown-item>
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

          <el-sub-menu index="data">
            <template #title>
              <el-icon>
                <Database/>
              </el-icon>
              <span>数据管理</span>
            </template>
            <el-menu-item index="/manager/data/list">数据列表</el-menu-item>
            <el-menu-item index="/manager/data/import">数据导入</el-menu-item>
            <el-menu-item index="/manager/data/export">数据导出</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="analysis">
            <template #title>
              <el-icon>
                <BarChart/>
              </el-icon>
              <span>数据分析</span>
            </template>
            <el-menu-item index="/manager/analysis/overview">数据概览</el-menu-item>
            <el-menu-item index="/manager/analysis/trends">趋势分析</el-menu-item>
            <el-menu-item index="/manager/analysis/comparison">对比分析</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="system">
            <template #title>
              <el-icon>
                <Setting/>
              </el-icon>
              <span>系统设置</span>
            </template>
            <el-menu-item index="/manager/users">用户管理</el-menu-item>
            <el-menu-item index="/manager/system/roles">角色权限</el-menu-item>
            <el-menu-item index="/manager/system/logs">操作日志</el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/manager/help">
            <el-icon>
              <Help/>
            </el-icon>
            <span>帮助中心</span>
          </el-menu-item>
        </el-menu>
      </aside>
      <!--  菜单区域结束    -->

      <main class="content-container">

        <!-- 主内容区域 -->
        <div class="main-content">
          <RouterView/>
        </div>
      </main>
      <!--  数据区域结束    -->
    </div>
    <!-- 下方区域结束   -->

  </div>
</template>

<script setup>
import router from "../router/index.js";

const Logout = () => {
  localStorage.removeItem('user')
  location.href = '/login'
}
</script>

<style scoped>
.admin-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f5f7fa;
}

/* 头部样式 */
.header-container {
  height: 60px;
  display: flex;
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  z-index: 10;
}

.logo-container {
  width: 240px;
  display: flex;
  align-items: center;
  background-color: #63dc9d;
  color: white;
  padding: 0 20px;
}

.logo-icon {
  font-size: 24px;
  margin-right: 10px;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
}

.header-content {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.breadcrumb {
  font-size: 14px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 15px;
}

.action-btn {
  background-color: #f5f7fa;
  color: #666;
  transition: all 0.3s;
}

.action-btn:hover {
  background-color: #e8eaec;
  color: #8fe5b7;
}

.notification-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  background-color: #ff4d4f;
  color: white;
  border-radius: 50%;
  width: 18px;
  height: 18px;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
}

.username {
  margin: 0 8px;
  font-size: 14px;
}

.arrow-icon {
  font-size: 16px;
  color: #999;
}

/* 主内容区域样式 */
.main-container {
  display: flex;
  flex: 1;
}

/* 侧边栏样式 */
.sidebar-container {
  width: 240px;
  background-color: #0F172A;
  transition: width 0.3s;
}

.sidebar-menu {
  min-height: calc(100vh - 60px);
  border-right: none;
  background-color: #000000;
  color: #94A3B8;
}

.sidebar-menu .el-menu-item,
.sidebar-menu .el-sub-menu__title {
  color: #94A3B8;
  height: 50px;
  line-height: 50px;
  padding-left: 25px !important;
  transition: all 0.3s;
}

.sidebar-menu .el-menu-item:hover,
.sidebar-menu .el-sub-menu__title:hover {
  color: #fff;
  background-color: #1E293B;
}

.sidebar-menu .el-menu-item.is-active {
  color: #fff;
  background-color: #165DFF;
}

.sidebar-menu .el-icon {
  margin-right: 12px;
  font-size: 18px;
}

/* 内容区域样式 */
.content-container {

  flex: 1;
  padding: 20px;
  overflow-y: auto;

}

/* 统计卡片样式 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.stat-card .el-card__body {
  padding: 15px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-info {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #1D2939;
  margin-bottom: 5px;
}

.stat-change {
  font-size: 12px;
  display: flex;
  align-items: center;
}

.stat-change.positive {
  color: #039855;
}

.stat-change.negative {
  color: #D92D20;
}

.stat-change .el-icon {
  font-size: 14px;
  margin-right: 3px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-icon.primary {
  background-color: rgba(22, 93, 255, 0.1);
  color: #165DFF;
}

.stat-icon.success {
  background-color: rgba(3, 152, 85, 0.1);
  color: #039855;
}

.stat-icon.warning {
  background-color: rgba(250, 173, 20, 0.1);
  color: #FACC15;
}

.stat-icon.info {
  background-color: rgba(16, 185, 129, 0.1);
  color: #10B981;
}

.main-content {
  height: 830px;
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  min-height: calc(100% - 140px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

/* 响应式调整 */
@media (max-width: 1024px) {
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .sidebar-container {
    width: 60px;
  }

  .logo-text {
    display: none;
  }

  .sidebar-menu .el-menu-item span,
  .sidebar-menu .el-sub-menu__title span {
    display: none;
  }

  .sidebar-menu .el-menu-item,
  .sidebar-menu .el-sub-menu__title {
    padding-left: 15px !important;
    justify-content: center;
  }

  .sidebar-menu .el-icon {
    margin-right: 0;
  }

  .stats-cards {
    grid-template-columns: 1fr;
  }
}
</style>
