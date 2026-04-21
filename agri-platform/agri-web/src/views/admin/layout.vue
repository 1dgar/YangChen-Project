<template>
  <el-container class="admin-layout">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="aside">
      <div class="sidebar-header">
        <div class="logo-wrapper">
          <div class="logo-icon">
            <el-icon :size="24"><Basketball /></el-icon>
          </div>
          <transition name="fade">
            <span v-if="!isCollapse" class="logo-text">管理后台</span>
          </transition>
        </div>
        <div class="collapse-btn" @click="isCollapse = !isCollapse">
          <el-icon :size="18">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
        </div>
      </div>

      <div class="user-card" v-if="!isCollapse">
        <el-avatar :size="48" class="user-avatar">
          <el-icon :size="24"><User /></el-icon>
        </el-avatar>
        <div class="user-info">
          <div class="user-name">{{ username }}</div>
          <el-tag type="danger" size="small" effect="dark" round>管理员</el-tag>
        </div>
      </div>

      <el-menu
        router
        :default-active="$route.path"
        class="admin-menu"
        :collapse="isCollapse"
        :collapse-transition="false"
      >
        <el-menu-item index="/admin/dashboard" class="menu-item">
          <el-icon class="menu-icon"><DataLine /></el-icon>
          <template #title>
            <span class="menu-text">数据概览</span>
          </template>
        </el-menu-item>
        <el-menu-item index="/admin/users" class="menu-item">
          <el-icon class="menu-icon"><User /></el-icon>
          <template #title>
            <span class="menu-text">用户管理</span>
          </template>
        </el-menu-item>
        <el-menu-item index="/admin/products" class="menu-item">
          <el-icon class="menu-icon"><Goods /></el-icon>
          <template #title>
            <span class="menu-text">产品管理</span>
          </template>
        </el-menu-item>
        <el-menu-item index="/admin/news" class="menu-item">
          <el-icon class="menu-icon"><Document /></el-icon>
          <template #title>
            <span class="menu-text">资讯管理</span>
          </template>
        </el-menu-item>
      </el-menu>

      <div class="sidebar-footer" v-if="!isCollapse">
        <div class="footer-stats">
          <div class="stat-item">
            <el-icon><Connection /></el-icon>
            <span>系统运行正常</span>
          </div>
        </div>
      </div>
    </el-aside>
    <el-container class="main-container">
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentPageTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-tooltip content="刷新页面" placement="bottom">
            <el-button circle class="header-btn" @click="refreshPage">
              <el-icon><Refresh /></el-icon>
            </el-button>
          </el-tooltip>
          <el-tooltip content="返回前台" placement="bottom">
            <el-button circle class="header-btn" @click="goHome">
              <el-icon><House /></el-icon>
            </el-button>
          </el-tooltip>
          <el-dropdown @command="handleCommand" trigger="click">
            <div class="user-dropdown">
              <el-avatar :size="36" class="header-avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  DataLine,
  User,
  Goods,
  Document,
  Basketball,
  Fold,
  Expand,
  Refresh,
  House,
  ArrowDown,
  SwitchButton,
  Connection
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const username = ref(localStorage.getItem('username') || '管理员')
const isCollapse = ref(false)

const currentPageTitle = computed(() => {
  const titles = {
    '/admin/dashboard': '数据概览',
    '/admin/users': '用户管理',
    '/admin/products': '产品管理',
    '/admin/news': '资讯管理'
  }
  return titles[route.path] || '管理后台'
})

const refreshPage = () => {
  window.location.reload()
}

const goHome = () => {
  router.push('/')
}

const handleCommand = (command) => {
  if (command === 'logout') {
    localStorage.clear()
    ElMessage.success('已退出登录')
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/profile')
  }
}
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
}

.aside {
  background: linear-gradient(180deg, #1a1f36 0%, #252d4a 50%, #1a1f36 100%);
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  overflow: hidden;
}

.sidebar-header {
  padding: 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #67c23a, #95d475);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: #fff;
  white-space: nowrap;
}

.collapse-btn {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.7);
  cursor: pointer;
  transition: all 0.3s ease;
}

.collapse-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}

.user-card {
  padding: 16px;
  margin: 12px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  background: linear-gradient(135deg, #667eea, #764ba2) !important;
  color: #fff !important;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-name {
  color: #fff;
  font-size: 14px;
  font-weight: 500;
}

.admin-menu {
  flex: 1;
  background: transparent !important;
  border-right: none !important;
  padding: 8px;
}

.admin-menu :deep(.el-menu-item) {
  height: 48px;
  line-height: 48px;
  margin: 4px 0;
  border-radius: 10px;
  color: rgba(255, 255, 255, 0.7);
  transition: all 0.3s ease;
}

.admin-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}

.admin-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #67c23a, #95d475) !important;
  color: #fff !important;
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.4);
}

.admin-menu :deep(.el-menu-item .el-icon) {
  font-size: 18px;
  transition: transform 0.3s ease;
}

.admin-menu :deep(.el-menu-item:hover .el-icon) {
  transform: scale(1.1);
}

.admin-menu :deep(.el-menu-item.is-active .el-icon) {
  transform: scale(1.1);
}

.menu-icon {
  margin-right: 8px;
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.footer-stats {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  padding: 12px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: rgba(255, 255, 255, 0.6);
  font-size: 12px;
}

.stat-item .el-icon {
  color: #67c23a;
}

.main-container {
  background: #f0f2f5;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  padding: 0 24px;
  height: 60px;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-btn {
  border: none;
  background: #f5f7fa;
  color: #606266;
  transition: all 0.3s ease;
}

.header-btn:hover {
  background: #e8f5e9;
  color: #67c23a;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 20px;
  transition: all 0.3s ease;
}

.user-dropdown:hover {
  background: #f5f7fa;
}

.header-avatar {
  background: linear-gradient(135deg, #667eea, #764ba2) !important;
  color: #fff !important;
}

.dropdown-arrow {
  color: #909399;
  font-size: 12px;
  transition: transform 0.3s ease;
}

.user-dropdown:hover .dropdown-arrow {
  transform: rotate(180deg);
}

.main {
  padding: 0;
  min-height: calc(100vh - 60px);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
}

:deep(.el-dropdown-menu__item .el-icon) {
  font-size: 16px;
}
</style>
