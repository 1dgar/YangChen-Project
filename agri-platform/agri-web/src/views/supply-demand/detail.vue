<template>
  <div class="supply-demand-detail" v-if="detail">
    <div class="page-header">
      <div class="header-bg"></div>
      <div class="container">
        <div class="breadcrumb">
          <router-link to="/">首页</router-link>
          <el-icon><ArrowRight /></el-icon>
          <router-link to="/supply-demand">供需信息</router-link>
          <el-icon><ArrowRight /></el-icon>
          <span>{{ detail.title }}</span>
        </div>
        <div class="header-content">
          <div class="type-badge" :class="detail.type">
            <el-icon v-if="detail.type === 'supply'"><Box /></el-icon>
            <el-icon v-else><ShoppingCart /></el-icon>
            {{ detail.type === 'supply' ? '供应信息' : '求购信息' }}
          </div>
          <h1>{{ detail.title }}</h1>
          <div class="header-meta">
            <span class="meta-item">
              <el-icon><Timer /></el-icon>
              {{ formatTime(detail.createTime) }}
            </span>
            <span class="meta-item">
              <el-icon><View /></el-icon>
              {{ detail.viewCount || Math.floor(Math.random() * 100) }} 次浏览
            </span>
          </div>
        </div>
      </div>
    </div>

    <div class="container main-content">
      <div class="detail-grid">
        <div class="main-section">
          <div class="info-card">
            <div class="card-header">
              <h3>
                <el-icon><Document /></el-icon>
                基本信息
              </h3>
              <el-tag :type="detail.status === 1 ? 'success' : 'info'" size="large">
                {{ detail.status === 1 ? '进行中' : '已结束' }}
              </el-tag>
            </div>
            <div class="info-grid">
              <div class="info-item">
                <div class="info-icon" style="background: linear-gradient(135deg, #22c55e, #16a34a);">
                  <el-icon><Grid /></el-icon>
                </div>
                <div class="info-content">
                  <span class="label">产品分类</span>
                  <span class="value">{{ detail.category }}</span>
                </div>
              </div>
              <div class="info-item">
                <div class="info-icon" style="background: linear-gradient(135deg, #3b82f6, #2563eb);">
                  <el-icon><Box /></el-icon>
                </div>
                <div class="info-content">
                  <span class="label">供应数量</span>
                  <span class="value">{{ detail.quantity }}</span>
                </div>
              </div>
              <div class="info-item highlight">
                <div class="info-icon" style="background: linear-gradient(135deg, #f97316, #ea580c);">
                  <el-icon><Money /></el-icon>
                </div>
                <div class="info-content">
                  <span class="label">价格</span>
                  <span class="value price">¥{{ detail.price }}</span>
                </div>
              </div>
              <div class="info-item">
                <div class="info-icon" style="background: linear-gradient(135deg, #8b5cf6, #7c3aed);">
                  <el-icon><Location /></el-icon>
                </div>
                <div class="info-content">
                  <span class="label">地区</span>
                  <span class="value">{{ detail.contactAddress || '全国' }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="content-card">
            <div class="card-header">
              <h3>
                <el-icon><Document /></el-icon>
                详细描述
              </h3>
            </div>
            <div class="content-body">
              <p>{{ detail.content || '暂无详细描述' }}</p>
            </div>
          </div>
        </div>

        <div class="side-section">
          <div class="contact-card">
            <div class="contact-header">
              <div class="avatar">
                {{ detail.contactName?.charAt(0) || '农' }}
              </div>
              <div class="publisher-info">
                <h4>{{ detail.contactName || '农户' }}</h4>
                <span class="verify-badge" v-if="detail.verified">
                  <el-icon><CircleCheck /></el-icon>
                  已认证
                </span>
              </div>
            </div>
            <div class="contact-info">
              <div class="contact-item">
                <el-icon><User /></el-icon>
                <span>{{ detail.contactName }}</span>
              </div>
              <div class="contact-item">
                <el-icon><Phone /></el-icon>
                <span>{{ detail.contactPhone }}</span>
              </div>
              <div class="contact-item">
                <el-icon><Location /></el-icon>
                <span>{{ detail.contactAddress || '未填写地址' }}</span>
              </div>
            </div>
            <div class="contact-actions">
              <el-button type="primary" size="large" @click="handleContact" class="contact-btn">
                <el-icon><Phone /></el-icon>
                立即联系
              </el-button>
              <el-button size="large" @click="handleCopyPhone" class="copy-btn">
                <el-icon><DocumentCopy /></el-icon>
                复制电话
              </el-button>
            </div>
          </div>

          <div class="tips-card">
            <div class="tips-header">
              <el-icon><Warning /></el-icon>
              交易提示
            </div>
            <ul class="tips-list">
              <li>交易前请核实对方身份信息</li>
              <li>建议使用平台担保交易</li>
              <li>不要轻信过低价格的产品</li>
              <li>保留交易记录以备维权</li>
            </ul>
          </div>
        </div>
      </div>

      <div class="action-bar">
        <el-button @click="$router.back()" size="large">
          <el-icon><ArrowLeft /></el-icon>
          返回列表
        </el-button>
      </div>
    </div>
  </div>

  <div v-else class="loading-container">
    <el-icon class="loading-icon" :size="48"><Loading /></el-icon>
    <p>加载中...</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  ArrowRight, Box, ShoppingCart, Timer, View, Document, 
  Grid, Money, Location, User, Phone, CircleCheck, 
  DocumentCopy, Warning, ArrowLeft, Loading
} from '@element-plus/icons-vue'
import { getSupplyDemandDetail } from '../../api/supplyDemand'

const route = useRoute()
const detail = ref(null)

const formatTime = (time) => {
  if (!time) return '刚刚'
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'
  return time.split('T')[0]
}

onMounted(async () => {
  const res = await getSupplyDemandDetail(route.params.id)
  detail.value = res.data
})

const handleContact = () => {
  if (detail.value?.contactPhone) {
    window.location.href = `tel:${detail.value.contactPhone}`
  }
}

const handleCopyPhone = () => {
  if (detail.value?.contactPhone) {
    navigator.clipboard.writeText(detail.value.contactPhone)
    ElMessage.success('电话号码已复制')
  }
}
</script>

<style scoped>
.supply-demand-detail {
  min-height: calc(100vh - 70px);
  background: #f5f7fa;
}

.page-header {
  position: relative;
  color: #fff;
  padding: 40px 0;
  overflow: hidden;
}

.header-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(26, 95, 42, 0.95) 0%, rgba(46, 125, 50, 0.92) 50%, rgba(76, 175, 80, 0.88) 100%);
  animation: headerBgZoom 20s ease-in-out infinite alternate;
}

@keyframes headerBgZoom {
  0% { transform: scale(1); }
  100% { transform: scale(1.05); }
}

.header-bg::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.05'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
}

.container {
  position: relative;
  z-index: 1;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 20px;
  font-size: 14px;
}

.breadcrumb a {
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  transition: color 0.3s;
}

.breadcrumb a:hover {
  color: #fff;
}

.breadcrumb span {
  color: #fff;
  font-weight: 500;
}

.header-content {
  text-align: center;
}

.type-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 20px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 16px;
}

.type-badge.supply {
  background: rgba(34, 197, 94, 0.2);
  color: #86efac;
  border: 1px solid rgba(134, 239, 172, 0.3);
}

.type-badge.demand {
  background: rgba(249, 115, 22, 0.2);
  color: #fed7aa;
  border: 1px solid rgba(254, 215, 170, 0.3);
}

.header-content h1 {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 16px 0;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
}

.header-meta {
  display: flex;
  justify-content: center;
  gap: 24px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  opacity: 0.9;
}

.main-content {
  padding: 30px 20px;
}

.detail-grid {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 24px;
  margin-bottom: 24px;
}

.main-section {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.info-card, .content-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.card-header h3 {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  color: #303133;
  margin: 0;
}

.card-header h3 .el-icon {
  color: #22c55e;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  padding: 24px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
  transition: all 0.3s;
}

.info-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.info-item.highlight {
  background: linear-gradient(135deg, #fff7ed, #ffedd5);
}

.info-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  flex-shrink: 0;
}

.info-content {
  flex: 1;
}

.info-content .label {
  display: block;
  font-size: 13px;
  color: #909399;
  margin-bottom: 4px;
}

.info-content .value {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.info-content .value.price {
  color: #f97316;
  font-size: 20px;
}

.content-body {
  padding: 24px;
}

.content-body p {
  color: #606266;
  line-height: 1.8;
  margin: 0;
  font-size: 15px;
}

.side-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.contact-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  padding: 24px;
}

.contact-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #22c55e, #16a34a);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 600;
}

.publisher-info h4 {
  margin: 0 0 4px 0;
  font-size: 18px;
  color: #303133;
}

.verify-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #22c55e;
  background: #f0fdf4;
  padding: 2px 8px;
  border-radius: 4px;
}

.contact-info {
  margin-bottom: 20px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
  color: #606266;
  font-size: 14px;
}

.contact-item:last-child {
  border-bottom: none;
}

.contact-item .el-icon {
  color: #22c55e;
  font-size: 18px;
}

.contact-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.contact-btn {
  background: linear-gradient(135deg, #22c55e, #16a34a);
  border: none;
  font-weight: 500;
}

.contact-btn:hover {
  background: linear-gradient(135deg, #16a34a, #15803d);
}

.copy-btn {
  border: 1px solid #22c55e;
  color: #22c55e;
}

.copy-btn:hover {
  background: #f0fdf4;
}

.tips-card {
  background: linear-gradient(135deg, #fefce8, #fef9c3);
  border-radius: 16px;
  padding: 20px;
  border: 1px solid #fde047;
}

.tips-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #a16207;
  margin-bottom: 12px;
}

.tips-list {
  margin: 0;
  padding-left: 20px;
}

.tips-list li {
  color: #854d0e;
  font-size: 13px;
  line-height: 1.8;
}

.action-bar {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  color: #909399;
}

.loading-icon {
  animation: spin 1s linear infinite;
  color: #22c55e;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@media (max-width: 992px) {
  .detail-grid {
    grid-template-columns: 1fr;
  }
  
  .side-section {
    order: -1;
  }
}

@media (max-width: 768px) {
  .header-content h1 {
    font-size: 24px;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .header-meta {
    flex-direction: column;
    gap: 8px;
  }
}
</style>
