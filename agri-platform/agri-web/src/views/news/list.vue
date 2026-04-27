<template>
  <div class="news-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-bg"></div>
      <div class="container">
        <div class="breadcrumb">
          <router-link to="/">首页</router-link>
          <el-icon><ArrowRight /></el-icon>
          <span>行业资讯</span>
        </div>
        <h2>农业资讯中心</h2>
        <p>最新政策 · 市场动态 · 技术前沿</p>
      </div>
    </div>

    <div class="container main-container">
      <!-- 分类筛选 -->
      <div class="category-bar">
        <div class="category-tabs">
          <div 
            v-for="cat in categories" 
            :key="cat.value"
            class="tab"
            :class="{ active: activeCategory === cat.value }"
            @click="activeCategory = cat.value"
          >
            <el-icon :size="18"><component :is="cat.icon" /></el-icon>
            <span>{{ cat.label }}</span>
          </div>
        </div>
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索资讯"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
            <template #append>
              <el-button @click="handleSearch">
                <el-icon><Search /></el-icon>
              </el-button>
            </template>
          </el-input>
        </div>
      </div>

      <!-- 精选头条 -->
      <div class="featured-section" v-if="featuredNews.length > 0">
          <div class="featured-main" @click="goToDetail(featuredNews[0].id)">
          <div class="featured-image">
            <img :src="featuredNews[0].coverImage" />
            <div class="featured-overlay"></div>
            <div class="featured-badge">
              <el-icon><StarFilled /></el-icon>
              头条
            </div>
          </div>
          <div class="featured-content">
            <div class="category-tag">{{ featuredNews[0].category }}</div>
            <h3>{{ featuredNews[0].title }}</h3>
            <p>{{ featuredNews[0].summary }}</p>
            <div class="meta">
              <div class="author">
                <div class="avatar">{{ featuredNews[0].author?.charAt(0) }}</div>
                <span>{{ featuredNews[0].author }}</span>
              </div>
              <div class="stats">
                <span><el-icon><View /></el-icon> {{ featuredNews[0].viewCount }}</span>
                <span><el-icon><Timer /></el-icon> {{ formatTime(featuredNews[0].createTime) }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="featured-side">
          <div 
            v-for="news in featuredNews.slice(1, 4)" 
            :key="news.id"
            class="side-item"
            @click="goToDetail(news.id)"
          >
            <div class="side-image">
              <img :src="news.coverImage" />
            </div>
            <div class="side-content">
              <span class="category">{{ news.category }}</span>
              <h4>{{ news.title }}</h4>
              <span class="time">{{ formatTime(news.createTime) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 资讯列表 -->
      <div class="news-section">
        <div class="section-header">
          <div class="header-title">
            <el-icon><Document /></el-icon>
            <h3>最新资讯</h3>
          </div>
          <div class="sort-options">
            <span :class="{ active: sortBy === 'time' }" @click="sortBy = 'time'">最新</span>
            <span :class="{ active: sortBy === 'hot' }" @click="sortBy = 'hot'">最热</span>
          </div>
        </div>

        <div class="news-grid">
          <div 
            v-for="news in newsList" 
            :key="news.id"
            class="news-card"
            @click="goToDetail(news.id)"
          >
            <div class="card-image">
              <img :src="news.coverImage" />
              <div class="card-overlay">
                <el-button type="primary" circle>
                  <el-icon><View /></el-icon>
                </el-button>
              </div>
              <div class="category-badge">{{ news.category }}</div>
            </div>
            <div class="card-content">
              <h4>{{ news.title }}</h4>
              <p class="summary">{{ news.summary }}</p>
              <div class="card-footer">
                <div class="author">
                  <div class="avatar">{{ news.author?.charAt(0) }}</div>
                  <span>{{ news.author }}</span>
                </div>
                <div class="stats">
                  <span><el-icon><View /></el-icon> {{ news.viewCount }}</span>
                  <span><el-icon><Timer /></el-icon> {{ formatTime(news.createTime) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 加载更多 -->
        <div class="load-more" v-if="hasMore">
          <el-button type="primary" plain @click="loadMore" :loading="loading">
            加载更多
            <el-icon><ArrowDown /></el-icon>
          </el-button>
        </div>

        <!-- 空状态 -->
        <div v-if="newsList.length === 0" class="empty-state">
          <el-icon :size="64"><Document /></el-icon>
          <p>暂无相关资讯</p>
        </div>
      </div>

      <!-- 订阅区域 -->
      <div class="subscribe-section">
        <div class="subscribe-content">
          <div class="subscribe-icon">
            <el-icon :size="48"><Bell /></el-icon>
          </div>
          <div class="subscribe-text">
            <h3>订阅农业资讯</h3>
            <p>第一时间获取最新政策、市场动态和技术资讯</p>
          </div>
        </div>
        <div class="subscribe-form">
          <el-input
            v-model="email"
            placeholder="请输入您的邮箱"
            size="large"
          >
            <template #append>
              <el-button type="primary" size="large" @click="subscribe">
                订阅
              </el-button>
            </template>
          </el-input>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getNewsList } from '../../api/market'
import { 
  ArrowRight, Search, StarFilled, View, Timer, Document, 
  ArrowDown, Bell, TrendCharts, Money, Collection, Tools
} from '@element-plus/icons-vue'

const router = useRouter()
const newsList = ref([])
const featuredNews = ref([])
const page = ref(1)
const size = ref(12)
const total = ref(0)
const loading = ref(false)
const hasMore = ref(true)
const activeCategory = ref('all')
const searchKeyword = ref('')
const sortBy = ref('time')
const email = ref('')

const categories = [
  { value: 'all', label: '全部资讯', icon: 'Collection' }
]

onMounted(() => {
  loadData(true)
})

watch(activeCategory, () => {
  page.value = 1
  newsList.value = []
  featuredNews.value = []
  loadData(true)
})

watch(sortBy, () => {
  page.value = 1
  newsList.value = []
  featuredNews.value = []
  loadData(true)
})

const mapNewsItem = (item, index) => ({
  ...item,
  category: '农业资讯',
  summary: item.content?.slice(0, 80) || '暂无摘要',
  coverImage: item.coverImage || `https://picsum.photos/seed/agri-news-${item.id || index}/800/400`
})

const loadData = async (reset = false) => {
  loading.value = true
  try {
    const res = await getNewsList({
      page: page.value,
      size: size.value,
      keyword: searchKeyword.value || undefined,
      sortBy: sortBy.value
    })
    const records = (res.data.records || []).map(mapNewsItem)

    if (reset || page.value === 1) {
      featuredNews.value = records.slice(0, 4)
      newsList.value = records.slice(4)
    } else {
      newsList.value = [...newsList.value, ...records]
    }

    total.value = res.data.total || 0
    hasMore.value = featuredNews.value.length + newsList.value.length < total.value
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.value = 1
  newsList.value = []
  featuredNews.value = []
  loadData(true)
}

const loadMore = async () => {
  if (!hasMore.value || loading.value) {
    return
  }
  page.value += 1
  await loadData()
}

const subscribe = () => {
  if (!email.value) {
    return
  }
  // 模拟订阅
  alert('订阅成功！')
  email.value = ''
}

const goToDetail = (id) => {
  router.push(`/news/${id}`)
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  if (diff < 3600000) {
    return Math.floor(diff / 60000) + '分钟前'
  } else if (diff < 86400000) {
    return Math.floor(diff / 3600000) + '小时前'
  } else if (diff < 604800000) {
    return Math.floor(diff / 86400000) + '天前'
  } else {
    return date.toLocaleDateString()
  }
}
</script>

<style scoped>
.news-page {
  min-height: 100vh;
  background:
    linear-gradient(180deg, rgba(248, 250, 248, 0.95) 0%, rgba(240, 244, 240, 0.95) 100%),
    url('https://picsum.photos/seed/newsbg/1920/1080') center/cover fixed;
}

/* 页面头部 */
.page-header {
  position: relative;
  color: #fff;
  padding: 50px 0;
  text-align: center;
  overflow: hidden;
}

.header-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background:
    linear-gradient(135deg, rgba(26, 95, 42, 0.93) 0%, rgba(46, 125, 50, 0.9) 50%, rgba(76, 175, 80, 0.87) 100%),
    url('https://picsum.photos/seed/newsheader/1920/400') center/cover no-repeat;
  animation: headerBgZoom 25s ease-in-out infinite alternate;
}

@keyframes headerBgZoom {
  0% { transform: scale(1); }
  100% { transform: scale(1.08); }
}

.header-bg::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.04'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
  opacity: 0.4;
}

.page-header .container {
  position: relative;
  z-index: 1;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
}

.breadcrumb {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 20px;
  font-size: 14px;
}

.breadcrumb a {
  color: rgba(255,255,255,0.8);
  text-decoration: none;
}

.breadcrumb a:hover {
  color: #fff;
}

.breadcrumb span {
  color: #fff;
}

.page-header h2 {
  margin: 0 0 12px 0;
  font-size: 40px;
  font-weight: 700;
  text-shadow: 2px 2px 4px rgba(0,0,0,0.2);
}

.page-header p {
  margin: 0 0 24px 0;
  font-size: 18px;
  opacity: 0.95;
  letter-spacing: 4px;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
}

.main-container {
  padding: 32px 24px;
}

/* 分类筛选 */
.category-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.category-tabs {
  display: flex;
  gap: 12px;
}

.tab {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: #fff;
  border-radius: 30px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.tab:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.1);
}

.tab.active {
  background: linear-gradient(135deg, #4CAF50, #45a049);
  color: white;
}

.search-box {
  width: 300px;
}

/* 精选头条 */
.featured-section {
  display: grid;
  grid-template-columns: 1.5fr 1fr;
  gap: 24px;
  margin-bottom: 40px;
}

.featured-main {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  cursor: pointer;
  transition: transform 0.3s;
  position: relative;
  height: 400px;
}

.featured-main:hover {
  transform: translateY(-4px);
}

.featured-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.featured-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.featured-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to bottom, rgba(0,0,0,0.1) 0%, rgba(0,0,0,0.8) 100%);
  z-index: 1;
}

.featured-badge {
  position: absolute;
  top: 16px;
  left: 16px;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: linear-gradient(135deg, #ff6b6b, #ee5a5a);
  color: white;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
}

.featured-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 24px;
  color: white;
  z-index: 3;
  background: linear-gradient(to top, rgba(0,0,0,0.8) 0%, rgba(0,0,0,0.4) 60%, transparent 100%);
}

.featured-content h3,
.featured-content p,
.featured-content .category-tag,
.featured-content .meta,
.featured-content .author,
.featured-content .stats,
.featured-content .avatar {
  color: white;
  text-shadow: 0 1px 3px rgba(0,0,0,0.5);
}

.featured-content .avatar {
  background: rgba(76, 175, 80, 0.9);
}

.featured-content .category-tag {
  display: inline-block;
  padding: 4px 12px;
  background: rgba(255,255,255,0.2);
  border-radius: 4px;
  font-size: 12px;
  margin-bottom: 12px;
}

.featured-content h3 {
  margin: 0 0 12px 0;
  font-size: 24px;
  font-weight: 600;
  line-height: 1.4;
}

.featured-content p {
  margin: 0 0 16px 0;
  font-size: 14px;
  opacity: 0.9;
  line-height: 1.6;
}

.featured-content .meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.featured-content .author {
  display: flex;
  align-items: center;
  gap: 10px;
}

.featured-content .avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #4CAF50;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
}

.featured-content .stats {
  display: flex;
  gap: 16px;
  font-size: 13px;
}

.featured-content .stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 侧边头条 */
.featured-side {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.side-item {
  display: flex;
  gap: 16px;
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  cursor: pointer;
  transition: all 0.3s;
}

.side-item:hover {
  transform: translateX(4px);
  box-shadow: 0 8px 30px rgba(0,0,0,0.12);
}

.side-image {
  width: 100px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.side-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.side-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.side-content .category {
  font-size: 12px;
  color: #4CAF50;
  font-weight: 500;
}

.side-content h4 {
  margin: 0;
  font-size: 15px;
  color: #333;
  font-weight: 600;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.side-content .time {
  font-size: 12px;
  color: #999;
}

/* 资讯列表 */
.news-section {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  margin-bottom: 32px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-title .el-icon {
  color: #4CAF50;
  font-size: 22px;
}

.section-header h3 {
  margin: 0;
  font-size: 20px;
  color: #333;
  font-weight: 600;
}

.sort-options {
  display: flex;
  gap: 16px;
}

.sort-options span {
  font-size: 14px;
  color: #999;
  cursor: pointer;
  transition: color 0.3s;
}

.sort-options span:hover,
.sort-options span.active {
  color: #4CAF50;
  font-weight: 500;
}

.news-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.news-card {
  background: #f8f9fa;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.news-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0,0,0,0.12);
}

.card-image {
  position: relative;
  height: 180px;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.news-card:hover .card-image img {
  transform: scale(1.05);
}

.card-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.news-card:hover .card-overlay {
  opacity: 1;
}

.category-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  padding: 4px 12px;
  background: rgba(76, 175, 80, 0.9);
  color: white;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.card-content {
  padding: 20px;
}

.card-content h4 {
  margin: 0 0 10px 0;
  font-size: 16px;
  color: #333;
  font-weight: 600;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-content .summary {
  margin: 0 0 16px 0;
  font-size: 13px;
  color: #666;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #e0e0e0;
}

.card-footer .author {
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-footer .avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4CAF50, #45a049);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 12px;
  font-weight: 600;
}

.card-footer .author span {
  font-size: 13px;
  color: #666;
}

.card-footer .stats {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #999;
}

.card-footer .stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 加载更多 */
.load-more {
  text-align: center;
  margin-top: 32px;
  padding-top: 32px;
  border-top: 1px solid #f0f0f0;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-state .el-icon {
  color: #ddd;
  margin-bottom: 16px;
}

.empty-state p {
  color: #999;
}

/* 订阅区域 */
.subscribe-section {
  background: linear-gradient(135deg, #1a5f2a 0%, #2e7d32 50%, #4CAF50 100%);
  border-radius: 16px;
  padding: 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: white;
}

.subscribe-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.subscribe-icon {
  width: 80px;
  height: 80px;
  border-radius: 20px;
  background: rgba(255,255,255,0.15);
  display: flex;
  align-items: center;
  justify-content: center;
}

.subscribe-text h3 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
}

.subscribe-text p {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

.subscribe-form {
  width: 400px;
}

.subscribe-form :deep(.el-input__wrapper) {
  background: rgba(255,255,255,0.15);
  border: none;
  box-shadow: none;
}

.subscribe-form :deep(.el-input__inner) {
  color: white;
}

.subscribe-form :deep(.el-input__inner::placeholder) {
  color: rgba(255,255,255,0.6);
}

/* 响应式 */
@media (max-width: 1200px) {
  .featured-section {
    grid-template-columns: 1fr;
  }
  
  .featured-side {
    flex-direction: row;
    flex-wrap: wrap;
  }
  
  .side-item {
    flex: 1;
    min-width: 300px;
  }
  
  .news-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .subscribe-section {
    flex-direction: column;
    gap: 24px;
    text-align: center;
  }
  
  .subscribe-form {
    width: 100%;
    max-width: 400px;
  }
}

@media (max-width: 768px) {
  .page-header h2 {
    font-size: 28px;
  }
  
  .category-bar {
    flex-direction: column;
    gap: 16px;
  }
  
  .category-tabs {
    width: 100%;
    overflow-x: auto;
    flex-wrap: nowrap;
  }
  
  .search-box {
    width: 100%;
  }
  
  .featured-image {
    height: 200px;
  }
  
  .featured-content h3 {
    font-size: 18px;
  }
  
  .side-item {
    min-width: 100%;
  }
  
  .news-grid {
    grid-template-columns: 1fr;
  }
  
  .subscribe-content {
    flex-direction: column;
  }
}
</style>
