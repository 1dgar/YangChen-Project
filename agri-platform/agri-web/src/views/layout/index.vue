<template>
  <el-container class="layout-container">
    <el-header class="header">
      <div class="logo">
        <el-icon :size="28" class="logo-icon"><Basketball /></el-icon>
        <span class="logo-text">农产品信息整合平台</span>
      </div>
      <nav class="nav-menu-wrapper">
        <router-link
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          :class="['nav-item', { active: isActive(item.path) }]"
        >
          <el-icon :size="18" class="nav-icon">
            <component :is="item.icon" />
          </el-icon>
          <span class="nav-text">{{ item.name }}</span>
        </router-link>
      </nav>
      <div class="header-right">
        <el-button
          v-if="!username"
          type="primary"
          class="login-btn"
          @click="router.push('/login')"
        >
          登录 / 注册
        </el-button>
        <el-dropdown v-else @command="handleCommand" class="user-dropdown">
          <div class="user-info">
            <el-avatar :size="36" :icon="UserFilled" class="user-avatar" />
            <span class="username">{{ username }}</span>
            <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu class="user-dropdown-menu">
              <el-dropdown-item command="profile">
                <el-icon><User /></el-icon>个人中心
              </el-dropdown-item>
              <el-dropdown-item command="myProducts" v-if="role === 'farmer'">
                <el-icon><Box /></el-icon>我的产品
              </el-dropdown-item>
              <el-dropdown-item command="mySupplyDemand">
                <el-icon><Document /></el-icon>我的供需
              </el-dropdown-item>
              <el-dropdown-item command="favorites">
                <el-icon><Star /></el-icon>我的收藏
              </el-dropdown-item>
              <el-dropdown-item divided command="logout" class="logout-item">
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
    <el-footer class="footer">
      <div class="footer-content">
        <p>农产品信息整合平台 2024</p>
        <div class="footer-links">
          <router-link to="/help">帮助中心</router-link>
          <span class="divider">|</span>
          <a href="javascript:void(0)" @click="openAiAssistant">AI助手</a>
        </div>
      </div>
    </el-footer>
    <AiAssistant />
  </el-container>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  House,
  Goods,
  Box,
  TrendCharts,
  Document,
  User,
  UserFilled,
  Star,
  SwitchButton,
  ArrowDown,
  Basketball,
  QuestionFilled
} from '@element-plus/icons-vue'
import AiAssistant from '../../components/AiAssistant.vue'

const router = useRouter()
const route = useRoute()
const username = ref(localStorage.getItem('username') || '')
const role = ref(localStorage.getItem('role') || '')

const navItems = [
  { path: '/', name: '首页', icon: House },
  { path: '/products', name: '农产品', icon: Goods },
  { path: '/supply-demand', name: '供需信息', icon: Box },
  { path: '/market', name: '行情', icon: TrendCharts },
  { path: '/news', name: '资讯', icon: Document },
  { path: '/help', name: '帮助中心', icon: QuestionFilled }
]

const isActive = (path) => {
  if (path === '/') {
    return route.path === '/' || route.path === '/home'
  }
  return route.path === path || route.path.startsWith(path + '/')
}

const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'myProducts':
      router.push('/my-products')
      break
    case 'mySupplyDemand':
      router.push('/my-supply-demand')
      break
    case 'favorites':
      router.push('/favorites')
      break
    case 'logout':
      localStorage.clear()
      ElMessage.success('已退出登录')
      router.push('/login')
      break
  }
}

const openAiAssistant = () => {
  const event = new CustomEvent('open-ai-assistant')
  window.dispatchEvent(event)
}
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(135deg, #ffffff 0%, #f8faf8 100%);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  padding: 0 40px;
  height: 70px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-right: 50px;
}

.logo-icon {
  color: #67c23a;
  background: linear-gradient(135deg, #67c23a 0%, #95d475 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.logo-text {
  font-size: 22px;
  font-weight: 700;
  background: linear-gradient(135deg, #2c3e2c 0%, #67c23a 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.5px;
}

.nav-menu-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 25px;
  text-decoration: none;
  color: #606266;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.nav-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #67c23a 0%, #95d475 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: 25px;
  z-index: -1;
}

.nav-item:hover {
  color: #67c23a;
  transform: translateY(-2px);
}

.nav-item:hover .nav-icon {
  transform: scale(1.1);
}

.nav-item.active {
  color: #fff;
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.4);
}

.nav-item.active::before {
  opacity: 1;
}

.nav-icon {
  transition: transform 0.3s ease;
}

.nav-text {
  position: relative;
  z-index: 1;
}

.header-right {
  margin-left: 30px;
}

.login-btn {
  background: linear-gradient(135deg, #67c23a 0%, #95d475 100%);
  border: none;
  padding: 10px 24px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 25px;
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3);
  transition: all 0.3s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(103, 194, 58, 0.4);
}

.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 16px;
  border-radius: 30px;
  background: #f5f7fa;
  transition: all 0.3s ease;
}

.user-info:hover {
  background: #e8f5e9;
}

.user-avatar {
  background: linear-gradient(135deg, #67c23a 0%, #95d475 100%) !important;
  color: #fff !important;
}

.username {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.dropdown-icon {
  color: #909399;
  font-size: 12px;
  transition: transform 0.3s ease;
}

.user-dropdown:hover .dropdown-icon {
  transform: rotate(180deg);
}

:deep(.user-dropdown-menu) {
  padding: 8px;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

:deep(.user-dropdown-menu .el-dropdown-menu__item) {
  padding: 10px 16px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  transition: all 0.2s ease;
}

:deep(.user-dropdown-menu .el-dropdown-menu__item:hover) {
  background: #f0f9eb;
  color: #67c23a;
}

:deep(.user-dropdown-menu .el-dropdown-menu__item .el-icon) {
  font-size: 16px;
}

:deep(.logout-item) {
  color: #f56c6c !important;
}

:deep(.logout-item:hover) {
  background: #fef0f0 !important;
  color: #f56c6c !important;
}

.main {
  background: #f5f7fa;
  padding: 0;
}

.footer {
  text-align: center;
  color: #909399;
  background: #fff;
  padding: 20px;
  border-top: 1px solid #ebeef5;
  font-size: 14px;
}

.footer-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
}

.footer-content p {
  margin: 0;
}

.footer-links {
  display: flex;
  align-items: center;
  gap: 12px;
}

.footer-links a,
.footer-links router-link {
  color: #67c23a;
  text-decoration: none;
  transition: color 0.3s;
}

.footer-links a:hover {
  color: #4CAF50;
}

.footer-links .divider {
  color: #dcdfe6;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .header {
    padding: 0 20px;
  }
  
  .logo-text {
    font-size: 18px;
  }
  
  .nav-item {
    padding: 8px 16px;
    font-size: 14px;
  }
}

@media (max-width: 992px) {
  .nav-text {
    display: none;
  }
  
  .nav-item {
    padding: 10px;
  }
}
</style>
