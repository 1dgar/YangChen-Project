<template>
  <div class="login-page">
    <!-- 动态农业背景 -->
    <div class="agri-background">
      <!-- 背景图片层 -->
      <div class="bg-image"></div>
      <!-- 渐变遮罩 -->
      <div class="bg-overlay"></div>
      <!-- 动态粒子效果 -->
      <div class="particles">
        <div class="particle" v-for="n in 20" :key="n" :style="getParticleStyle(n)"></div>
      </div>
      <!-- 浮动图标 -->
      <div class="floating-icons">
        <div class="float-icon icon-1">🌾</div>
        <div class="float-icon icon-2">🍎</div>
        <div class="float-icon icon-3">🥬</div>
        <div class="float-icon icon-4">🌽</div>
        <div class="float-icon icon-5">🍊</div>
        <div class="float-icon icon-6">🥕</div>
      </div>
      <!-- 科技线条 -->
      <div class="tech-lines">
        <div class="line line-1"></div>
        <div class="line line-2"></div>
        <div class="line line-3"></div>
        <div class="line line-4"></div>
      </div>
    </div>

    <div class="login-container">
      <!-- 左侧品牌区 -->
      <div class="brand-section">
        <div class="brand-content">
          <div class="logo">
            <el-icon :size="48"><Apple /></el-icon>
            <span>农贸通</span>
          </div>
          <h1>连接田间地头<br/>与城市餐桌</h1>
          <p>为农户和采购商搭建高效便捷的农产品交易平台</p>
          <div class="features">
            <div class="feature-item">
              <el-icon><CircleCheck /></el-icon>
              <span>海量农产品资源</span>
            </div>
            <div class="feature-item">
              <el-icon><CircleCheck /></el-icon>
              <span>实时市场行情</span>
            </div>
            <div class="feature-item">
              <el-icon><CircleCheck /></el-icon>
              <span>安全便捷交易</span>
            </div>
          </div>
        </div>
        <div class="brand-stats">
          <div class="stat-item">
            <span class="number">10,000+</span>
            <span class="label">注册农户</span>
          </div>
          <div class="stat-item">
            <span class="number">5,000+</span>
            <span class="label">采购商</span>
          </div>
          <div class="stat-item">
            <span class="number">50,000+</span>
            <span class="label">日均交易</span>
          </div>
        </div>
      </div>

      <!-- 右侧登录区 -->
      <div class="form-section">
        <div class="form-card">
          <!-- 登录/注册切换 -->
          <div class="form-tabs">
            <div 
              class="tab" 
              :class="{ active: !isRegister }"
              @click="isRegister = false"
            >
              登录
            </div>
            <div 
              class="tab" 
              :class="{ active: isRegister }"
              @click="isRegister = true"
            >
              注册
            </div>
          </div>

          <!-- 快捷登录（仅登录时显示） -->
          <div class="quick-login" v-if="!isRegister">
            <div class="quick-title">快捷登录</div>
            <div class="quick-buttons">
              <div class="quick-btn admin" @click="quickLogin('admin', '123456')">
                <div class="btn-icon">
                  <el-icon><UserFilled /></el-icon>
                </div>
                <span class="btn-label">管理员</span>
                <span class="btn-account">admin</span>
              </div>
              <div class="quick-btn farmer" @click="quickLogin('farmer1', '123456')">
                <div class="btn-icon">
                  <el-icon><Food /></el-icon>
                </div>
                <span class="btn-label">农户</span>
                <span class="btn-account">farmer1</span>
              </div>
              <div class="quick-btn buyer" @click="quickLogin('buyer1', '123456')">
                <div class="btn-icon">
                  <el-icon><ShoppingCart /></el-icon>
                </div>
                <span class="btn-label">采购商</span>
                <span class="btn-account">buyer1</span>
              </div>
            </div>
          </div>

          <div class="divider" v-if="!isRegister">
            <span>或账号密码登录</span>
          </div>

          <!-- 登录表单 -->
          <el-form 
            v-if="!isRegister"
            :model="loginForm" 
            :rules="loginRules" 
            ref="loginFormRef"
            class="login-form"
          >
            <el-form-item prop="username">
              <el-input 
                v-model="loginForm.username" 
                placeholder="请输入用户名" 
                size="large"
              >
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input 
                v-model="loginForm.password" 
                type="password" 
                placeholder="请输入密码" 
                size="large"
                show-password
              >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <div class="form-options">
              <el-checkbox v-model="rememberMe">记住我</el-checkbox>
              <a href="#" class="forgot-link">忘记密码？</a>
            </div>
            <el-form-item>
              <el-button 
                type="primary" 
                size="large" 
                @click="handleLogin" 
                :loading="loading"
                class="submit-btn"
              >
                登 录
              </el-button>
            </el-form-item>
          </el-form>

          <!-- 注册表单 -->
          <el-form 
            v-else
            :model="registerForm" 
            :rules="registerRules" 
            ref="registerFormRef"
            class="register-form"
          >
            <el-form-item prop="username">
              <el-input 
                v-model="registerForm.username" 
                placeholder="请输入用户名" 
                size="large"
              >
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input 
                v-model="registerForm.password" 
                type="password" 
                placeholder="请输入密码" 
                size="large"
                show-password
              >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="confirmPassword">
              <el-input 
                v-model="registerForm.confirmPassword" 
                type="password" 
                placeholder="请确认密码" 
                size="large"
                show-password
              >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="role">
              <el-select v-model="registerForm.role" placeholder="选择身份" size="large" style="width: 100%;">
                <el-option label="农户" value="farmer">
                  <div class="role-option">
                    <el-icon><Food /></el-icon>
                    <span>农户 - 发布农产品供应</span>
                  </div>
                </el-option>
                <el-option label="采购商" value="buyer">
                  <div class="role-option">
                    <el-icon><ShoppingCart /></el-icon>
                    <span>采购商 - 寻找优质货源</span>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item prop="agreement">
              <el-checkbox v-model="registerForm.agreement">
                我已阅读并同意 <a href="#">用户协议</a> 和 <a href="#">隐私政策</a>
              </el-checkbox>
            </el-form-item>
            <el-form-item>
              <el-button 
                type="primary" 
                size="large" 
                @click="handleRegister" 
                :loading="loading"
                class="submit-btn"
              >
                注 册
              </el-button>
            </el-form-item>
          </el-form>


        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, register } from '../../api/user'
import { 
  UserFilled, Food, ShoppingCart, CircleCheck, 
  User, Lock, Apple
} from '@element-plus/icons-vue'

const router = useRouter()
const loginFormRef = ref()
const registerFormRef = ref()
const loading = ref(false)
const isRegister = ref(false)
const rememberMe = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  role: '',
  agreement: false
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  role: [{ required: true, message: '请选择身份', trigger: 'change' }],
  agreement: [
    { 
      validator: (rule, value, callback) => {
        if (!value) {
          callback(new Error('请同意用户协议'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
}

// 快捷登录
const quickLogin = async (username, password) => {
  loginForm.username = username
  loginForm.password = password
  loading.value = true
  try {
    const res = await login({ username, password })
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('userId', res.data.id)
    localStorage.setItem('username', res.data.username)
    localStorage.setItem('role', res.data.role)
    ElMessage.success(`欢迎 ${username} 登录成功！`)
    if (res.data.role === 'admin') {
      router.push('/admin')
    } else {
      router.push('/')
    }
  } catch (error) {
    ElMessage.error('登录失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

const handleLogin = async () => {
  await loginFormRef.value.validate()
  loading.value = true
  try {
    const res = await login(loginForm)
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('userId', res.data.id)
    localStorage.setItem('username', res.data.username)
    localStorage.setItem('role', res.data.role)
    ElMessage.success('登录成功')
    if (res.data.role === 'admin') {
      router.push('/admin')
    } else {
      router.push('/')
    }
  } catch (error) {
    ElMessage.error('登录失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  await registerFormRef.value.validate()
  loading.value = true
  try {
    await register({
      username: registerForm.username,
      password: registerForm.password,
      role: registerForm.role
    })
    ElMessage.success('注册成功，请登录')
    isRegister.value = false
    loginForm.username = registerForm.username
  } catch (error) {
    ElMessage.error('注册失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 生成粒子样式
const getParticleStyle = (n) => {
  const size = Math.random() * 6 + 2
  const left = Math.random() * 100
  const delay = Math.random() * 10
  const duration = Math.random() * 10 + 10
  return {
    width: `${size}px`,
    height: `${size}px`,
    left: `${left}%`,
    animationDelay: `${delay}s`,
    animationDuration: `${duration}s`
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  position: relative;
  overflow: hidden;
}

/* 现代农业信息化动态背景 */
.agri-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 0;
}

/* 背景图片 - 现代农业场景 */
.bg-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url('https://picsum.photos/seed/loginbg/1920/1080') center/cover no-repeat;
  animation: bgZoom 20s ease-in-out infinite alternate;
}

@keyframes bgZoom {
  0% {
    transform: scale(1);
  }
  100% {
    transform: scale(1.1);
  }
}

/* 渐变遮罩 - 科技感 */
.bg-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: 
    linear-gradient(135deg, rgba(26, 95, 42, 0.85) 0%, rgba(46, 125, 50, 0.75) 50%, rgba(76, 175, 80, 0.65) 100%),
    linear-gradient(to bottom, rgba(0, 0, 0, 0.3) 0%, rgba(0, 0, 0, 0.1) 100%);
}

/* 动态粒子效果 */
.particles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.particle {
  position: absolute;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 50%;
  animation: float-up linear infinite;
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.5);
}

@keyframes float-up {
  0% {
    transform: translateY(100vh) scale(0);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-100vh) scale(1);
    opacity: 0;
  }
}

/* 浮动农产品图标 */
.floating-icons {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.float-icon {
  position: absolute;
  font-size: 40px;
  opacity: 0.15;
  animation: float-icon 8s ease-in-out infinite;
  filter: drop-shadow(0 0 10px rgba(255, 255, 255, 0.3));
}

.icon-1 {
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.icon-2 {
  top: 20%;
  right: 15%;
  animation-delay: 1s;
}

.icon-3 {
  top: 60%;
  left: 5%;
  animation-delay: 2s;
}

.icon-4 {
  top: 70%;
  right: 10%;
  animation-delay: 3s;
}

.icon-5 {
  top: 40%;
  left: 15%;
  animation-delay: 4s;
}

.icon-6 {
  top: 80%;
  right: 20%;
  animation-delay: 5s;
}

@keyframes float-icon {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(10deg);
  }
}

/* 科技线条 */
.tech-lines {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  overflow: hidden;
}

.line {
  position: absolute;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  height: 1px;
  animation: line-move 6s linear infinite;
}

.line-1 {
  top: 20%;
  left: -100%;
  width: 50%;
  animation-delay: 0s;
}

.line-2 {
  top: 40%;
  left: -100%;
  width: 30%;
  animation-delay: 2s;
}

.line-3 {
  top: 60%;
  left: -100%;
  width: 40%;
  animation-delay: 4s;
}

.line-4 {
  top: 80%;
  left: -100%;
  width: 35%;
  animation-delay: 1s;
}

@keyframes line-move {
  0% {
    left: -100%;
  }
  100% {
    left: 200%;
  }
}

.login-container {
  display: flex;
  width: 100%;
  max-width: 1200px;
  min-height: 600px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 24px;
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.3), 0 0 0 1px rgba(255, 255, 255, 0.1);
  overflow: hidden;
  position: relative;
  z-index: 1;
  backdrop-filter: blur(10px);
  animation: container-fade-in 0.8s ease-out;
}

@keyframes container-fade-in {
  0% {
    opacity: 0;
    transform: translateY(30px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 左侧品牌区 */
.brand-section {
  flex: 1;
  background: linear-gradient(135deg, #1a5f2a 0%, #2e7d32 50%, #4CAF50 100%);
  padding: 60px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  color: white;
  position: relative;
  overflow: hidden;
}

.brand-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #1a5f2a 0%, #2e7d32 50%, #4CAF50 100%);
  opacity: 0.1;
}

.brand-content {
  position: relative;
  z-index: 1;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 40px;
}

.logo span {
  font-size: 28px;
  font-weight: 700;
}

.brand-content h1 {
  font-size: 42px;
  font-weight: 700;
  line-height: 1.3;
  margin: 0 0 20px 0;
}

.brand-content p {
  font-size: 16px;
  opacity: 0.9;
  line-height: 1.6;
  margin: 0 0 40px 0;
}

.features {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 15px;
}

.feature-item .el-icon {
  font-size: 20px;
}

.brand-stats {
  display: flex;
  gap: 40px;
  position: relative;
  z-index: 1;
}

.stat-item {
  display: flex;
  flex-direction: column;
}

.stat-item .number {
  font-size: 28px;
  font-weight: 700;
}

.stat-item .label {
  font-size: 13px;
  opacity: 0.8;
}

/* 右侧表单区 */
.form-section {
  flex: 1;
  padding: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.form-card {
  width: 100%;
  max-width: 400px;
}

.form-tabs {
  display: flex;
  gap: 32px;
  margin-bottom: 32px;
  border-bottom: 2px solid #f0f0f0;
}

.tab {
  padding-bottom: 16px;
  font-size: 20px;
  font-weight: 600;
  color: #999;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.tab.active {
  color: #4CAF50;
}

.tab.active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 100%;
  height: 2px;
  background: #4CAF50;
}

/* 快捷登录 */
.quick-login {
  margin-bottom: 24px;
}

.quick-title {
  font-size: 14px;
  color: #999;
  margin-bottom: 16px;
}

.quick-buttons {
  display: flex;
  gap: 12px;
}

.quick-btn {
  flex: 1;
  padding: 16px 12px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  border: 2px solid transparent;
}

.quick-btn:hover {
  transform: translateY(-2px);
}

.quick-btn.admin {
  background: linear-gradient(135deg, rgba(76, 175, 80, 0.1), rgba(76, 175, 80, 0.05));
  color: #4CAF50;
}

.quick-btn.admin:hover {
  border-color: #4CAF50;
}

.quick-btn.farmer {
  background: linear-gradient(135deg, rgba(255, 152, 0, 0.1), rgba(255, 152, 0, 0.05));
  color: #FF9800;
}

.quick-btn.farmer:hover {
  border-color: #FF9800;
}

.quick-btn.buyer {
  background: linear-gradient(135deg, rgba(33, 150, 243, 0.1), rgba(33, 150, 243, 0.05));
  color: #2196F3;
}

.quick-btn.buyer:hover {
  border-color: #2196F3;
}

.btn-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.quick-btn.admin .btn-icon {
  background: #4CAF50;
  color: white;
}

.quick-btn.farmer .btn-icon {
  background: #FF9800;
  color: white;
}

.quick-btn.buyer .btn-icon {
  background: #2196F3;
  color: white;
}

.btn-label {
  font-size: 14px;
  font-weight: 600;
}

.btn-account {
  font-size: 12px;
  opacity: 0.7;
}

/* 分隔线 */
.divider {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  color: #999;
  font-size: 13px;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: #e0e0e0;
}

/* 表单 */
.login-form,
.register-form {
  margin-bottom: 24px;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.forgot-link {
  color: #4CAF50;
  text-decoration: none;
  font-size: 14px;
}

.forgot-link:hover {
  text-decoration: underline;
}

.submit-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
}

/* 角色选项 */
.role-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 响应式 */
@media (max-width: 992px) {
  .login-container {
    flex-direction: column;
  }
  
  .brand-section {
    padding: 40px;
    min-height: 300px;
  }
  
  .brand-content h1 {
    font-size: 28px;
  }
  
  .brand-stats {
    gap: 24px;
  }
  
  .form-section {
    padding: 40px;
  }
}

@media (max-width: 576px) {
  .login-page {
    padding: 0;
  }
  
  .login-container {
    border-radius: 0;
    min-height: 100vh;
  }
  
  .brand-section {
    display: none;
  }
  
  .form-section {
    padding: 24px;
  }
  
  .quick-buttons {
    flex-direction: column;
  }
  
  .quick-btn {
    flex-direction: row;
    justify-content: flex-start;
    padding: 12px 16px;
  }
  
  .btn-icon {
    width: 36px;
    height: 36px;
  }
}
</style>