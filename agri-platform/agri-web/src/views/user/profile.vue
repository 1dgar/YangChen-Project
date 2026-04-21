<template>
  <div class="profile-page">
    <div class="profile-header">
      <div class="header-bg"></div>
      <div class="header-content">
        <div class="avatar-section">
          <el-upload
            class="avatar-uploader"
            action="#"
            :auto-upload="false"
            :on-change="handleAvatarChange"
            :show-file-list="false"
            accept="image/*"
          >
            <img v-if="form.avatar" :src="form.avatar" class="avatar" />
            <div v-else class="avatar-placeholder">
              <el-icon :size="48"><User /></el-icon>
            </div>
            <div class="avatar-overlay">
              <el-icon><Camera /></el-icon>
              <span>更换头像</span>
            </div>
          </el-upload>
        </div>
        <div class="user-info">
          <h2>{{ form.username }}</h2>
          <div class="user-role">
            <el-tag :type="form.role === 'admin' ? 'danger' : form.role === 'farmer' ? 'success' : 'primary'" size="large">
              {{ form.role === 'admin' ? '管理员' : form.role === 'farmer' ? '农户' : '采购商' }}
            </el-tag>
          </div>
          <p class="join-date">
            <el-icon><Timer /></el-icon>
            注册时间：{{ form.createTime || '未知' }}
          </p>
        </div>
      </div>
    </div>

    <div class="profile-content">
      <div class="content-grid">
        <div class="info-card">
          <div class="card-header">
            <h3><el-icon><User /></el-icon> 基本信息</h3>
          </div>
          <el-form :model="form" label-width="80px" class="info-form">
            <el-form-item label="用户名">
              <el-input v-model="form.username" disabled>
                <template #prefix><el-icon><User /></el-icon></template>
              </el-input>
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="form.phone" placeholder="请输入手机号">
                <template #prefix><el-icon><Phone /></el-icon></template>
              </el-input>
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="form.email" placeholder="请输入邮箱">
                <template #prefix><el-icon><Message /></el-icon></template>
              </el-input>
            </el-form-item>
            <el-form-item label="地址">
              <el-input v-model="form.address" placeholder="请输入地址">
                <template #prefix><el-icon><Location /></el-icon></template>
              </el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdate" :loading="loading">
                <el-icon><Check /></el-icon>
                保存修改
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <div class="side-cards">
          <div class="action-card">
            <div class="card-header">
              <h3><el-icon><Setting /></el-icon> 账户安全</h3>
            </div>
            <div class="action-list">
              <div class="action-item" @click="showPasswordDialog = true">
                <div class="action-icon">
                  <el-icon><Lock /></el-icon>
                </div>
                <div class="action-content">
                  <span class="action-title">修改密码</span>
                  <span class="action-desc">定期修改密码可以保护账户安全</span>
                </div>
                <el-icon class="action-arrow"><ArrowRight /></el-icon>
              </div>
            </div>
          </div>

          <div class="stats-card">
            <div class="card-header">
              <h3><el-icon><TrendCharts /></el-icon> 我的数据</h3>
            </div>
            <div class="stats-grid">
              <div class="stat-item" v-if="form.role === 'farmer'">
                <div class="stat-value">{{ myStats.products }}</div>
                <div class="stat-label">发布产品</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ myStats.supplyDemand }}</div>
                <div class="stat-label">供需信息</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ myStats.favorites }}</div>
                <div class="stat-label">收藏数量</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ myStats.comments }}</div>
                <div class="stat-label">留言评价</div>
              </div>
            </div>
          </div>

          <div class="quick-links-card">
            <div class="card-header">
              <h3><el-icon><Menu /></el-icon> 快捷入口</h3>
            </div>
            <div class="links-grid">
              <div class="link-item" @click="$router.push('/my-products')" v-if="form.role === 'farmer'">
                <el-icon><Goods /></el-icon>
                <span>我的产品</span>
              </div>
              <div class="link-item" @click="$router.push('/my-supply-demand')">
                <el-icon><List /></el-icon>
                <span>我的供需</span>
              </div>
              <div class="link-item" @click="$router.push('/favorites')">
                <el-icon><Star /></el-icon>
                <span>我的收藏</span>
              </div>
              <div class="link-item" @click="$router.push('/my-comments')">
                <el-icon><ChatDotRound /></el-icon>
                <span>我的留言</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-dialog v-model="showPasswordDialog" title="修改密码" width="420px" class="password-dialog">
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordRef" label-width="80px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入原密码">
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码">
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码">
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="handlePasswordChange" :loading="pwdLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  User, Camera, Timer, Phone, Message, Location, Check, 
  Setting, Lock, ArrowRight, TrendCharts, Menu, Goods, 
  List, Star, ChatDotRound 
} from '@element-plus/icons-vue'
import { getUserInfo, updateUser, updatePassword } from '../../api/user'
import { uploadImage } from '../../api/file'

const userId = localStorage.getItem('userId')
const form = ref({})
const loading = ref(false)
const showPasswordDialog = ref(false)
const pwdLoading = ref(false)
const passwordRef = ref()
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirm = (rule, value, callback) => {
  if (value !== passwordForm.value.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

const myStats = ref({
  products: 0,
  supplyDemand: 0,
  favorites: 0,
  comments: 0
})

onMounted(async () => {
  try {
    const res = await getUserInfo(userId)
    form.value = res.data
    
    myStats.value = {
      products: Math.floor(Math.random() * 20) + 5,
      supplyDemand: Math.floor(Math.random() * 15) + 3,
      favorites: Math.floor(Math.random() * 30) + 10,
      comments: Math.floor(Math.random() * 25) + 5
    }
  } catch (e) {
    console.error('加载用户信息失败', e)
  }
})

const handleUpdate = async () => {
  loading.value = true
  try {
    await updateUser(userId, form.value)
    ElMessage.success('修改成功')
  } catch (e) {
    ElMessage.error('修改失败')
  } finally {
    loading.value = false
  }
}

const handlePasswordChange = async () => {
  await passwordRef.value.validate()
  pwdLoading.value = true
  try {
    await updatePassword({
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    })
    ElMessage.success('密码修改成功')
    showPasswordDialog.value = false
    passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  } catch (e) {
    ElMessage.error('密码修改失败')
  } finally {
    pwdLoading.value = false
  }
}

const handleAvatarChange = async (file) => {
  const isJPG = file.raw.type === 'image/jpeg'
  const isPNG = file.raw.type === 'image/png'
  const isLt2M = file.raw.size / 1024 / 1024 < 2

  if (!isJPG && !isPNG) {
    ElMessage.error('只支持 JPG 或 PNG 格式!')
    return
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!')
    return
  }

  try {
    const res = await uploadImage(file.raw)
    form.value.avatar = res.data
    ElMessage.success('头像上传成功')
  } catch (error) {
    ElMessage.error('头像上传失败')
  }
}
</script>

<style scoped>
.profile-page {
  min-height: calc(100vh - 200px);
  background: #f5f7fa;
}

.profile-header {
  position: relative;
  padding: 60px 24px 24px;
  margin-bottom: 24px;
}

.header-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 180px;
  background: linear-gradient(135deg, #1a5f2a 0%, #2e7d32 50%, #43a047 100%);
  border-radius: 0 0 24px 24px;
}

.header-content {
  position: relative;
  display: flex;
  align-items: flex-end;
  gap: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.avatar-section {
  position: relative;
}

.avatar-uploader {
  position: relative;
  cursor: pointer;
}

.avatar, .avatar-placeholder {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  border: 4px solid white;
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
  object-fit: cover;
}

.avatar-placeholder {
  background: linear-gradient(135deg, #e8f5e9, #c8e6c9);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #2e7d32;
}

.avatar-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 40px;
  background: rgba(0,0,0,0.6);
  border-radius: 0 0 60px 60px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 12px;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-uploader:hover .avatar-overlay {
  opacity: 1;
}

.user-info {
  padding-bottom: 8px;
  color: white;
}

.user-info h2 {
  font-size: 28px;
  margin: 0 0 12px 0;
}

.user-role {
  margin-bottom: 8px;
}

.join-date {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  opacity: 0.9;
  margin: 0;
}

.profile-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px 40px;
}

.content-grid {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 24px;
}

.info-card, .action-card, .stats-card, .quick-links-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
  overflow: hidden;
}

.card-header {
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.card-header h3 {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  margin: 0;
  color: #303133;
}

.info-form {
  padding: 20px;
}

.side-cards {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.action-list {
  padding: 12px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.action-item:hover {
  background: #f5f7fa;
}

.action-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: linear-gradient(135deg, #409eff, #66b1ff);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
}

.action-content {
  flex: 1;
}

.action-title {
  display: block;
  font-size: 15px;
  font-weight: 500;
  color: #303133;
}

.action-desc {
  display: block;
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

.action-arrow {
  color: #c0c4cc;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  padding: 20px;
}

.stat-item {
  text-align: center;
  padding: 16px;
  background: #f9fafc;
  border-radius: 12px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1a5f2a;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

.links-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  padding: 16px;
}

.link-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px;
  background: #f9fafc;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  color: #606266;
}

.link-item:hover {
  background: #1a5f2a;
  color: white;
}

.link-item span {
  font-size: 13px;
}

.password-dialog :deep(.el-dialog__body) {
  padding: 20px 24px;
}
</style>
