<template>
  <div class="product-detail-page" v-if="product">
    <div class="page-header">
      <div class="container">
        <div class="breadcrumb">
          <router-link to="/">首页</router-link>
          <el-icon><ArrowRight /></el-icon>
          <router-link to="/products">农产品</router-link>
          <el-icon><ArrowRight /></el-icon>
          <span>{{ product.name }}</span>
        </div>
      </div>
    </div>
    
    <div class="container main-content">
      <div class="detail-card">
        <div class="product-main">
          <div class="product-gallery">
            <div class="main-image">
              <img v-if="product.image" :src="product.image" :alt="product.name" class="detail-image" />
              <div v-else class="detail-placeholder">
                <el-icon><Picture /></el-icon>
                <span>暂无图片</span>
              </div>
              <div class="image-overlay">
                <span class="category-tag">{{ product.category }}</span>
              </div>
            </div>
          </div>
          
          <div class="product-info">
            <h1 class="product-title">{{ product.name }}</h1>
            
            <div class="price-section">
              <div class="current-price">
                <span class="currency">¥</span>
                <span class="amount">{{ product.price }}</span>
                <span class="unit">/{{ product.unit }}</span>
              </div>
              <div class="stock-info" :class="{ 'low-stock': product.stock < 50 }">
                <el-icon><Box /></el-icon>
                库存: {{ product.stock }} {{ product.unit }}
              </div>
            </div>
            
            <div class="info-grid">
              <div class="info-item">
                <el-icon><Grid /></el-icon>
                <span class="label">分类</span>
                <span class="value">{{ product.category }}</span>
              </div>
              <div class="info-item">
                <el-icon><User /></el-icon>
                <span class="label">联系人</span>
                <span class="value">{{ product.contactName }}</span>
              </div>
              <div class="info-item">
                <el-icon><Phone /></el-icon>
                <span class="label">联系电话</span>
                <span class="value">{{ product.contactPhone }}</span>
              </div>
              <div class="info-item">
                <el-icon><Location /></el-icon>
                <span class="label">地址</span>
                <span class="value">{{ product.contactAddress || '未填写' }}</span>
              </div>
            </div>
            
            <div class="description-section">
              <h3>产品描述</h3>
              <p>{{ product.description || '暂无描述' }}</p>
            </div>
            
            <div class="action-buttons">
              <el-button 
                type="primary" 
                size="large" 
                @click="handleFavorite"
                :disabled="isOwnProduct"
                class="favorite-btn"
              >
                <el-icon><Star /></el-icon>
                {{ isFavorited ? '已收藏' : '收藏产品' }}
              </el-button>
              <el-button 
                v-if="product.contactPhone"
                size="large"
                @click="handleContact"
                class="contact-btn"
              >
                <el-icon><Phone /></el-icon>
                联系卖家
              </el-button>
            </div>
          </div>
        </div>
      </div>
      
      <div class="comments-section">
        <div class="section-header">
          <h3>
            <el-icon><ChatDotRound /></el-icon>
            留言咨询
          </h3>
          <span class="comment-count">{{ comments.length }} 条留言</span>
        </div>
        
        <div class="comment-form" v-if="!isOwnProduct">
          <el-input 
            type="textarea" 
            v-model="commentForm.content" 
            placeholder="请输入您的留言，询问产品详情、价格等信息..."
            rows="3"
            maxlength="500"
            show-word-limit
          />
          <el-button type="primary" @click="submitComment" :disabled="!commentForm.content.trim()">
            <el-icon><Position /></el-icon>
            发送留言
          </el-button>
        </div>
        
        <div class="comments-list">
          <div v-if="comments.length === 0" class="empty-comments">
            <el-icon :size="48"><ChatLineSquare /></el-icon>
            <p>暂无留言，快来抢沙发吧~</p>
          </div>
          
          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <div class="comment-avatar">
              {{ comment.username?.charAt(0) || '用' }}
            </div>
            <div class="comment-body">
              <div class="comment-header">
                <span class="username">{{ comment.username }}</span>
                <span class="time">{{ formatTime(comment.createTime) }}</span>
              </div>
              <p class="comment-content">{{ comment.content }}</p>
              <div v-if="comment.reply" class="reply-box">
                <div class="reply-header">
                  <el-icon><ChatDotRound /></el-icon>
                  卖家回复
                </div>
                <p>{{ comment.reply }}</p>
              </div>
              <div v-else-if="isOwnProduct" class="reply-form">
                <el-input 
                  v-model="comment.replyContent" 
                  placeholder="回复买家咨询..."
                  size="default"
                />
                <el-button type="primary" size="default" @click="handleReply(comment)">
                  回复
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  
  <div v-else class="loading-container">
    <el-icon class="loading-icon" :size="48"><Loading /></el-icon>
    <p>加载中...</p>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowRight, Box, Grid, User, Phone, Location, Star, ChatDotRound, Position, ChatLineSquare, Loading, Picture } from '@element-plus/icons-vue'
import { getProductDetail } from '../../api/product'
import { getProductComments, addComment, replyComment } from '../../api/comment'
import { addFavorite, removeFavorite, checkFavorite } from '../../api/favorite'
const route = useRoute()
const product = ref(null)
const comments = ref([])
const isFavorited = ref(false)
const commentForm = ref({ content: '' })
const userId = ref(localStorage.getItem('userId'))

const isOwnProduct = computed(() => {
  return product.value && product.value.userId == userId.value
})

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  return time.split('T')[0]
}

onMounted(async () => {
  const id = route.params.id
  const res = await getProductDetail(id)
  product.value = res.data
  loadComments()
  checkIsFavorited()
})

const loadComments = async () => {
  const res = await getProductComments(route.params.id, { page: 1, size: 100 })
  comments.value = res.data.records
}

const checkIsFavorited = async () => {
  try {
    const res = await checkFavorite(route.params.id)
    isFavorited.value = res.data
  } catch (e) {}
}

const handleFavorite = async () => {
  try {
    if (isFavorited.value) {
      await removeFavorite(product.value.id)
      isFavorited.value = false
      ElMessage.success('取消收藏成功')
    } else {
      await addFavorite({ productId: product.value.id })
      isFavorited.value = true
      ElMessage.success('收藏成功')
    }
  } catch (e) {}
}

const handleContact = () => {
  if (product.value?.contactPhone) {
    window.location.href = `tel:${product.value.contactPhone}`
  }
}

const submitComment = async () => {
  if (!commentForm.value.content.trim()) {
    ElMessage.warning('请输入留言内容')
    return
  }
  await addComment({
    productId: product.value.id,
    content: commentForm.value.content
  })
  ElMessage.success('留言成功')
  commentForm.value.content = ''
  loadComments()
}

const handleReply = async (comment) => {
  if (!comment.replyContent || !comment.replyContent.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  try {
    await replyComment(comment.id, { reply: comment.replyContent })
    ElMessage.success('回复成功')
    loadComments()
  } catch (error) {
    ElMessage.error('回复失败')
  }
}
</script>

<style scoped>
.product-detail-page {
  min-height: calc(100vh - 70px);
  background: #f5f7fa;
}

.page-header {
  background: linear-gradient(135deg, #1a5f2a 0%, #2e7d32 100%);
  padding: 20px 0;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
}

.breadcrumb a {
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  transition: color 0.3s;
}

.breadcrumb a:hover {
  color: white;
}

.breadcrumb span {
  color: white;
  font-weight: 500;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.main-content {
  padding: 30px 20px;
}

.detail-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  margin-bottom: 24px;
}

.product-main {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  padding: 30px;
}

.product-gallery {
  position: relative;
}

.main-image {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  height: 450px;
  background: #f6f8f6;
}

.detail-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.detail-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: #98a29a;
  background: linear-gradient(180deg, #fafcf9 0%, #eef3ee 100%);
  font-size: 18px;
}

.detail-placeholder .el-icon {
  font-size: 64px;
}

.image-overlay {
  position: absolute;
  top: 16px;
  left: 16px;
}

.category-tag {
  background: linear-gradient(135deg, #67c23a 0%, #95d475 100%);
  color: white;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
}

.product-info {
  display: flex;
  flex-direction: column;
}

.product-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0 0 20px 0;
  line-height: 1.3;
}

.price-section {
  background: linear-gradient(135deg, #fff5f5 0%, #fff0f0 100%);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.current-price {
  display: flex;
  align-items: baseline;
}

.currency {
  color: #f56c6c;
  font-size: 20px;
  font-weight: 600;
}

.amount {
  color: #f56c6c;
  font-size: 36px;
  font-weight: 700;
  margin: 0 4px;
}

.unit {
  color: #909399;
  font-size: 16px;
}

.stock-info {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #67c23a;
  font-size: 15px;
  font-weight: 500;
}

.stock-info.low-stock {
  color: #e6a23c;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 24px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.info-item .el-icon {
  color: #67c23a;
  font-size: 18px;
}

.info-item .label {
  color: #909399;
  font-size: 14px;
}

.info-item .value {
  color: #303133;
  font-weight: 500;
  margin-left: auto;
}

.description-section {
  margin-bottom: 24px;
}

.description-section h3 {
  font-size: 16px;
  color: #303133;
  margin: 0 0 12px 0;
  font-weight: 600;
}

.description-section p {
  color: #606266;
  line-height: 1.8;
  margin: 0;
}

.action-buttons {
  display: flex;
  gap: 16px;
  margin-top: auto;
}

.favorite-btn, .contact-btn {
  flex: 1;
  height: 48px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 8px;
}

.favorite-btn {
  background: linear-gradient(135deg, #67c23a 0%, #95d475 100%);
  border: none;
}

.favorite-btn:hover {
  background: linear-gradient(135deg, #5daf34 0%, #85ce61 100%);
}

.contact-btn {
  background: white;
  border: 2px solid #67c23a;
  color: #67c23a;
}

.contact-btn:hover {
  background: #f0f9eb;
}

.comments-section {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  padding: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.section-header h3 {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  color: #303133;
  margin: 0;
}

.section-header h3 .el-icon {
  color: #67c23a;
}

.comment-count {
  color: #909399;
  font-size: 14px;
}

.comment-form {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.comment-form .el-textarea {
  flex: 1;
}

.comment-form .el-button {
  align-self: flex-end;
  height: 40px;
}

.empty-comments {
  text-align: center;
  padding: 60px 0;
  color: #909399;
}

.empty-comments .el-icon {
  color: #c0c4cc;
  margin-bottom: 16px;
}

.comment-item {
  display: flex;
  gap: 16px;
  padding: 20px 0;
  border-bottom: 1px solid #f0f0f0;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: linear-gradient(135deg, #67c23a 0%, #95d475 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 600;
  flex-shrink: 0;
}

.comment-body {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.username {
  font-weight: 600;
  color: #303133;
}

.time {
  color: #909399;
  font-size: 13px;
}

.comment-content {
  color: #606266;
  line-height: 1.6;
  margin: 0 0 12px 0;
}

.reply-box {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 12px 16px;
  border-left: 3px solid #67c23a;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #67c23a;
  font-size: 13px;
  font-weight: 500;
  margin-bottom: 8px;
}

.reply-box p {
  color: #606266;
  margin: 0;
  line-height: 1.6;
}

.reply-form {
  display: flex;
  gap: 12px;
  margin-top: 12px;
}

.reply-form .el-input {
  flex: 1;
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
  color: #67c23a;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@media (max-width: 768px) {
  .product-main {
    grid-template-columns: 1fr;
    gap: 24px;
  }
  
  .main-image img {
    height: 300px;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .action-buttons {
    flex-direction: column;
  }
}
</style>
