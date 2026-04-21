<template>
  <div class="my-comments-page">
    <div class="page-header">
      <div class="header-content">
        <h2>
          <el-icon><ChatDotRound /></el-icon>
          我的留言
        </h2>
        <p>您与卖家的沟通记录</p>
      </div>
    </div>

    <div class="stats-section">
      <div class="stat-card total">
        <div class="stat-icon">
          <el-icon :size="28"><ChatDotRound /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.total }}</div>
          <div class="stat-label">留言总数</div>
        </div>
      </div>
      <div class="stat-card replied">
        <div class="stat-icon">
          <el-icon :size="28"><CircleCheck /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.replied }}</div>
          <div class="stat-label">已回复</div>
        </div>
      </div>
      <div class="stat-card pending">
        <div class="stat-icon">
          <el-icon :size="28"><Timer /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.pending }}</div>
          <div class="stat-label">待回复</div>
        </div>
      </div>
    </div>

    <div class="content-card">
      <div class="filter-bar">
        <div class="filter-left">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索留言内容"
            clearable
            style="width: 240px;"
            @input="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-select v-model="filterStatus" placeholder="回复状态" clearable style="width: 140px;" @change="handleSearch">
            <el-option label="全部状态" value="" />
            <el-option label="已回复" value="replied" />
            <el-option label="待回复" value="pending" />
          </el-select>
        </div>
      </div>

      <div class="comments-list" v-if="filteredComments.length > 0">
        <div class="comment-card" v-for="comment in filteredComments" :key="comment.id">
          <div class="comment-header">
            <div class="product-info" @click="goToProduct(comment.productId)">
              <div class="product-image" :style="{ backgroundColor: getCategoryBgColor(comment.product?.category) }">
                <el-icon :size="28" :color="getCategoryColor(comment.product?.category)">
                  <component :is="getCategoryIconComponent(comment.product?.category)" />
                </el-icon>
              </div>
              <div class="product-detail">
                <h4>{{ comment.product?.name || '未知产品' }}</h4>
                <p class="product-price">¥{{ comment.product?.price || 0 }}/{{ comment.product?.unit || '斤' }}</p>
              </div>
            </div>
            <div class="comment-time">
              <el-icon><Timer /></el-icon>
              {{ formatDate(comment.createTime) }}
            </div>
          </div>
          
          <div class="comment-body">
            <div class="comment-content">
              <div class="comment-label">
                <el-icon><User /></el-icon>
                我的留言
              </div>
              <p>{{ comment.content }}</p>
            </div>
            
            <div class="reply-content" v-if="comment.reply">
              <div class="reply-label">
                <el-icon><Service /></el-icon>
                卖家回复
              </div>
              <p>{{ comment.reply }}</p>
              <div class="reply-time" v-if="comment.replyTime">
                回复时间: {{ formatDate(comment.replyTime) }}
              </div>
            </div>
            
            <div class="no-reply" v-else>
              <el-icon><Timer /></el-icon>
              <span>等待卖家回复中...</span>
            </div>
          </div>

          <div class="comment-footer">
            <el-tag :type="comment.reply ? 'success' : 'warning'" effect="light" size="small">
              {{ comment.reply ? '已回复' : '待回复' }}
            </el-tag>
            <div class="actions">
              <el-button type="primary" link size="small" @click="goToProduct(comment.productId)">
                <el-icon><View /></el-icon>
                查看产品
              </el-button>
              <el-button type="danger" link size="small" @click="deleteComment(comment.id)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="empty-state">
        <el-icon :size="64" color="#ccc"><ChatDotRound /></el-icon>
        <p>暂无留言记录</p>
        <el-button type="primary" @click="$router.push('/product')">去浏览产品</el-button>
      </div>

      <div class="pagination-wrapper" v-if="filteredComments.length > 0">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="loadComments"
          @size-change="handleSizeChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyComments, deleteComment as deleteCommentApi } from '../../api/comment'
import { getCategoryIcon, getCategoryColor, getCategoryBgColor } from '../../utils/imageMap'
import { 
  ChatDotRound, CircleCheck, Timer, Search, User, 
  Service, View, Delete, Food, Apple, Orange, Bowl, IceCream, Dessert
} from '@element-plus/icons-vue'

const router = useRouter()
const userId = localStorage.getItem('userId')
const comments = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const filterStatus = ref('')

const statistics = computed(() => {
  const total = comments.value.length
  const replied = comments.value.filter(c => c.reply).length
  const pending = comments.value.filter(c => !c.reply).length
  return { total, replied, pending }
})

const filteredComments = computed(() => {
  let result = comments.value
  
  if (searchKeyword.value) {
    result = result.filter(c => 
      c.content?.includes(searchKeyword.value) || 
      c.reply?.includes(searchKeyword.value)
    )
  }
  
  if (filterStatus.value === 'replied') {
    result = result.filter(c => c.reply)
  } else if (filterStatus.value === 'pending') {
    result = result.filter(c => !c.reply)
  }
  
  return result
})

onMounted(() => {
  loadComments()
})

const loadComments = async () => {
  try {
    const res = await getMyComments(userId, { page: page.value, size: size.value })
    comments.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('加载留言失败:', error)
    comments.value = []
  }
}

const handleSizeChange = (newSize) => {
  size.value = newSize
  page.value = 1
  loadComments()
}

const handleSearch = () => {
  page.value = 1
}

const getCategoryIconComponent = (category) => {
  return getCategoryIcon(category)
}

const formatDate = (dateStr) => {
  if (!dateStr) return '未知时间'
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now - date
  
  if (diff < 3600000) {
    return Math.floor(diff / 60000) + '分钟前'
  } else if (diff < 86400000) {
    return Math.floor(diff / 3600000) + '小时前'
  } else if (diff < 604800000) {
    return Math.floor(diff / 86400000) + '天前'
  } else {
    return date.toLocaleDateString('zh-CN', { 
      year: 'numeric', 
      month: '2-digit', 
      day: '2-digit'
    })
  }
}

const goToProduct = (id) => {
  router.push(`/product/${id}`)
}

const deleteComment = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该留言吗？', '提示', { type: 'warning' })
    await deleteCommentApi(id)
    ElMessage.success('删除成功')
    loadComments()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}
</script>

<style scoped>
.my-comments-page {
  padding: 24px;
  background: linear-gradient(180deg, #f8faf8 0%, #f0f4f0 100%);
  min-height: calc(100vh - 60px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-content h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #1a5f2a;
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-content p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.stats-section {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: transform 0.3s, box-shadow 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-card.total .stat-icon {
  background: linear-gradient(135deg, #4CAF50, #45a049);
}

.stat-card.replied .stat-icon {
  background: linear-gradient(135deg, #2196F3, #1976D2);
}

.stat-card.pending .stat-icon {
  background: linear-gradient(135deg, #FF9800, #F57C00);
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-top: 4px;
}

.content-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.filter-left {
  display: flex;
  gap: 12px;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-card {
  background: #fff;
  border-radius: 12px;
  border: 1px solid #f0f0f0;
  overflow: hidden;
  transition: all 0.3s;
}

.comment-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: opacity 0.3s;
}

.product-info:hover {
  opacity: 0.8;
}

.product-image {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-detail h4 {
  margin: 0 0 4px 0;
  font-size: 15px;
  color: #333;
}

.product-price {
  margin: 0;
  font-size: 14px;
  color: #f56c6c;
  font-weight: 600;
}

.comment-time {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #999;
}

.comment-body {
  padding: 20px;
}

.comment-content, .reply-content {
  margin-bottom: 16px;
}

.comment-label, .reply-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 8px;
}

.comment-label {
  color: #4CAF50;
}

.reply-label {
  color: #2196F3;
}

.comment-content p, .reply-content p {
  margin: 0;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.6;
}

.comment-content p {
  background: rgba(76, 175, 80, 0.05);
  border-left: 3px solid #4CAF50;
}

.reply-content p {
  background: rgba(33, 150, 243, 0.05);
  border-left: 3px solid #2196F3;
}

.reply-time {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
  text-align: right;
}

.no-reply {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px;
  background: rgba(255, 152, 0, 0.05);
  border-radius: 8px;
  color: #FF9800;
  font-size: 14px;
}

.no-reply .el-icon {
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.comment-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
  background: #fafafa;
}

.actions {
  display: flex;
  gap: 8px;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
}

.empty-state p {
  color: #999;
  margin: 16px 0 24px 0;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .stats-section {
    grid-template-columns: 1fr;
  }
  
  .filter-bar {
    flex-direction: column;
    gap: 12px;
  }
  
  .filter-left {
    flex-wrap: wrap;
  }
  
  .comment-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .comment-footer {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
}
</style>
