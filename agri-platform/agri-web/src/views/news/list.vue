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
import { ref, computed, onMounted, watch } from 'vue'
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
const size = ref(9)
const total = ref(0)
const loading = ref(false)
const hasMore = ref(true)
const activeCategory = ref('all')
const searchKeyword = ref('')
const sortBy = ref('time')
const email = ref('')

const categories = [
  { value: 'all', label: '全部', icon: 'Collection' },
  { value: '政策法规', label: '政策法规', icon: 'Document' },
  { value: '市场动态', label: '市场动态', icon: 'TrendCharts' },
  { value: '农业技术', label: '农业技术', icon: 'Tools' },
  { value: '价格行情', label: '价格行情', icon: 'Money' },
]

onMounted(() => {
  loadData()
})

// 监听分类变化，重新加载数据
watch(activeCategory, () => {
  page.value = 1
  newsList.value = []
  featuredNews.value = []
  loadData()
})

// 所有资讯数据
const allNewsData = [
  {
    id: 1,
    title: '2025年中央一号文件发布：全面推进乡村振兴',
    summary: '文件强调要巩固拓展脱贫攻坚成果，全面推进乡村振兴，加快农业农村现代化。重点包括粮食安全、乡村产业、乡村建设等方面。',
    coverImage: 'https://picsum.photos/seed/news1/800/400',
    category: '政策法规',
    author: '农业农村部',
    viewCount: 12580,
    createTime: '2025-04-14 08:00:00'
  },
  {
    id: 2,
    title: '全国春季农业生产工作会议召开',
    summary: '会议强调要抓好春耕备耕工作，确保粮食播种面积稳定。',
    coverImage: 'https://picsum.photos/seed/news2/400/300',
    category: '政策法规',
    author: '新华社',
    viewCount: 8920,
    createTime: '2025-04-13 14:30:00'
  },
  {
    id: 3,
    title: '智慧农业：无人机植保技术推广加速',
    summary: '无人机植保作业效率是人工的30倍以上，正在全国快速推广。',
    coverImage: 'https://picsum.photos/seed/news3/400/300',
    category: '农业技术',
    author: '农业科技报',
    viewCount: 6540,
    createTime: '2025-04-13 10:00:00'
  },
  {
    id: 4,
    title: '猪肉价格连续三个月回落，市场供应充足',
    summary: '生猪产能持续恢复，市场供应充足，价格保持低位运行。',
    coverImage: 'https://picsum.photos/seed/news4/400/300',
    category: '市场动态',
    author: '市场监测中心',
    viewCount: 5230,
    createTime: '2025-04-12 16:00:00'
  },
  {
    id: 5,
    title: '有机农产品认证新规实施，标准更加严格',
    summary: '新规对有机农产品的生产、加工、销售等环节提出了更严格的要求，有助于提升有机农产品质量。',
    coverImage: 'https://picsum.photos/seed/news5/400/300',
    category: '政策法规',
    author: '质量监管局',
    viewCount: 4560,
    createTime: '2025-04-12 09:00:00'
  },
  {
    id: 6,
    title: '大棚蔬菜种植技术：如何提高产量和品质',
    summary: '详细介绍大棚蔬菜的温度控制、水肥管理、病虫害防治等关键技术要点。',
    coverImage: 'https://picsum.photos/seed/news6/400/300',
    category: '农业技术',
    author: '农技推广站',
    viewCount: 3890,
    createTime: '2025-04-11 15:30:00'
  },
  {
    id: 7,
    title: '春季水果市场分析：草莓价格创新高',
    summary: '受天气影响，今年春季草莓产量下降，价格较去年同期上涨20%。',
    coverImage: 'https://picsum.photos/seed/news7/400/300',
    category: '市场动态',
    author: '市场分析师',
    viewCount: 3240,
    createTime: '2025-04-11 11:00:00'
  },
  {
    id: 8,
    title: '农村电商发展报告：农产品网上销售额突破万亿',
    summary: '农村电商快速发展，成为农产品销售的重要渠道，带动农民增收。',
    coverImage: 'https://picsum.photos/seed/news8/400/300',
    category: '市场动态',
    author: '商务部',
    viewCount: 2980,
    createTime: '2025-04-10 14:00:00'
  },
  {
    id: 9,
    title: '水稻新品种推广：亩产提高15%',
    summary: '新型高产水稻品种在全国推广，平均亩产提高15%，助力粮食安全。',
    coverImage: 'https://picsum.photos/seed/news9/400/300',
    category: '农业技术',
    author: '农科院',
    viewCount: 2650,
    createTime: '2025-04-10 09:30:00'
  },
  {
    id: 10,
    title: '农业补贴政策解读：种粮大户可获得额外补助',
    summary: '今年农业补贴政策有所调整，对种粮大户和新型农业经营主体给予更多支持。',
    coverImage: 'https://picsum.photos/seed/news10/400/300',
    category: '政策法规',
    author: '财政厅',
    viewCount: 2340,
    createTime: '2025-04-09 16:00:00'
  },
  {
    id: 11,
    title: '农业机械化率持续提升，助力农业现代化',
    summary: '全国农作物耕种收综合机械化率超过72%，农业机械化水平不断提高。',
    coverImage: 'https://picsum.photos/seed/news11/400/300',
    category: '农业技术',
    author: '农机推广站',
    viewCount: 1890,
    createTime: '2025-04-09 10:00:00'
  },
  {
    id: 12,
    title: '绿色食品认证企业数量突破2万家',
    summary: '绿色食品产业快速发展，认证企业数量持续增长，产品质量不断提升。',
    coverImage: 'https://picsum.photos/seed/news12/400/300',
    category: '市场动态',
    author: '绿色食品中心',
    viewCount: 1650,
    createTime: '2025-04-08 14:30:00'
  }
]

const loadData = async () => {
  // 根据分类筛选数据
  let filteredNews = allNewsData
  if (activeCategory.value !== 'all') {
    filteredNews = allNewsData.filter(news => news.category === activeCategory.value)
  }
  
  // 设置头条（前4条）
  featuredNews.value = filteredNews.slice(0, 4)
  
  // 设置列表（第5条开始）
  newsList.value = filteredNews.slice(4)
  
  total.value = filteredNews.length
}

const handleSearch = () => {
  page.value = 1
  loadData()
}

const loadMore = () => {
  // 静态数据模式下，加载更多功能暂时不可用
  hasMore.value = false
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