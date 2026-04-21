<template>
  <div class="supply-demand-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-bg"></div>
      <div class="container">
        <div class="breadcrumb">
          <router-link to="/">首页</router-link>
          <el-icon><ArrowRight /></el-icon>
          <span>供需信息</span>
        </div>
        <h2>供需信息发布平台</h2>
        <p>精准对接 · 高效匹配 · 诚信交易</p>
        <div class="header-stats">
          <div class="stat-item">
            <el-icon><TrendCharts /></el-icon>
            <span>今日新增 {{ todayCount }} 条</span>
          </div>
          <div class="stat-item">
            <el-icon><Document /></el-icon>
            <span>累计发布 {{ totalCount }} 条</span>
          </div>
          <div class="stat-item">
            <el-icon><CircleCheck /></el-icon>
            <span>成功匹配 {{ successCount }} 单</span>
          </div>
        </div>
      </div>
    </div>

    <div class="container main-container">
      <!-- 统计卡片 -->
      <div class="stats-section">
        <div class="stat-card supply">
          <div class="stat-icon">
            <el-icon :size="32"><Box /></el-icon>
          </div>
          <div class="stat-content">
            <span class="stat-value">{{ supplyCount }}</span>
            <span class="stat-label">供应信息</span>
          </div>
        </div>
        <div class="stat-card demand">
          <div class="stat-icon">
            <el-icon :size="32"><ShoppingCart /></el-icon>
          </div>
          <div class="stat-content">
            <span class="stat-value">{{ demandCount }}</span>
            <span class="stat-label">求购信息</span>
          </div>
        </div>
        <div class="stat-card success">
          <div class="stat-icon">
            <el-icon :size="32"><CircleCheck /></el-icon>
          </div>
          <div class="stat-content">
            <span class="stat-value">{{ successRate }}%</span>
            <span class="stat-label">成交率</span>
          </div>
        </div>
        <div class="stat-card users">
          <div class="stat-icon">
            <el-icon :size="32"><User /></el-icon>
          </div>
          <div class="stat-content">
            <span class="stat-value">{{ activeUsers }}</span>
            <span class="stat-label">活跃用户</span>
          </div>
        </div>
      </div>

      <!-- 快捷发布区 -->
      <div class="quick-publish">
        <div class="publish-card supply-card" @click="goToPublish('supply')">
          <div class="publish-icon">
            <el-icon :size="40"><Box /></el-icon>
          </div>
          <div class="publish-content">
            <h3>我有农产品要卖</h3>
            <p>发布供应信息，让采购商主动找到您</p>
            <div class="publish-tags">
              <span class="tag">免费发布</span>
              <span class="tag">精准匹配</span>
              <span class="tag">快速成交</span>
            </div>
          </div>
          <el-button type="primary" size="large" class="publish-btn">
            发布供应
            <el-icon class="btn-icon"><ArrowRight /></el-icon>
          </el-button>
        </div>
        <div class="publish-card demand-card" @click="goToPublish('demand')">
          <div class="publish-icon">
            <el-icon :size="40"><ShoppingCart /></el-icon>
          </div>
          <div class="publish-content">
            <h3>我要采购农产品</h3>
            <p>发布求购信息，让供应商主动联系您</p>
            <div class="publish-tags">
              <span class="tag">免费发布</span>
              <span class="tag">多方比价</span>
              <span class="tag">品质保障</span>
            </div>
          </div>
          <el-button type="warning" size="large" class="publish-btn">
            发布求购
            <el-icon class="btn-icon"><ArrowRight /></el-icon>
          </el-button>
        </div>
      </div>

      <!-- 筛选和列表 -->
      <div class="content-section">
        <div class="filter-bar">
          <div class="filter-tabs">
            <div 
              class="tab" 
              :class="{ active: activeTab === 'all' }"
              @click="activeTab = 'all'; page = 1; loadData()"
            >
              <el-icon><Grid /></el-icon>
              全部信息
              <span class="count">{{ total }}</span>
            </div>
            <div 
              class="tab" 
              :class="{ active: activeTab === 'supply' }"
              @click="activeTab = 'supply'; page = 1; loadData()"
            >
              <el-icon><Box /></el-icon>
              供应信息
              <span class="count">{{ supplyCount }}</span>
            </div>
            <div 
              class="tab" 
              :class="{ active: activeTab === 'demand' }"
              @click="activeTab = 'demand'; page = 1; loadData()"
            >
              <el-icon><ShoppingCart /></el-icon>
              求购信息
              <span class="count">{{ demandCount }}</span>
            </div>
          </div>
          <div class="filter-controls">
            <el-select v-model="searchForm.category" placeholder="全部分类" clearable @change="handleSearch">
              <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
            </el-select>
            <el-input
              v-model="searchForm.keyword"
              placeholder="搜索产品名称"
              clearable
              style="width: 200px;"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
          </div>
        </div>

        <!-- 信息列表 -->
        <div class="info-list">
          <div 
            v-for="item in paginatedList" 
            :key="item.id" 
            class="info-card"
            @click="goToDetail(item.id)"
          >
            <div class="card-header">
              <div class="type-badge" :class="item.type">
                <el-icon v-if="item.type === 'supply'"><Box /></el-icon>
                <el-icon v-else><ShoppingCart /></el-icon>
                {{ item.type === 'supply' ? '供应' : '求购' }}
              </div>
              <span class="category-tag">{{ item.category }}</span>
              <span class="time">{{ formatTime(item.createTime) }}</span>
            </div>
            <div class="card-body">
              <h4 class="title">{{ item.title }}</h4>
              <p class="description">{{ item.description }}</p>
              <div class="details">
                <div class="detail-item">
                  <el-icon><Goods /></el-icon>
                  <span>数量: <strong>{{ item.quantity }}</strong></span>
                </div>
                <div class="detail-item">
                  <el-icon><Money /></el-icon>
                  <span>价格: <strong class="price">¥{{ item.price }}</strong></span>
                </div>
                <div class="detail-item">
                  <el-icon><Location /></el-icon>
                  <span>地区: {{ item.location || '全国' }}</span>
                </div>
              </div>
            </div>
            <div class="card-footer">
              <div class="publisher">
                <div class="avatar">{{ item.username?.charAt(0) || '农' }}</div>
                <div class="publisher-info">
                  <span class="name">{{ item.username || '农户' }}</span>
                  <span class="verify" v-if="item.verified">
                    <el-icon><CircleCheck /></el-icon>
                    已认证
                  </span>
                </div>
              </div>
              <div class="actions">
                <el-button type="primary" link>
                  <el-icon><View /></el-icon>
                  {{ item.viewCount || Math.floor(Math.random() * 100) }} 浏览
                </el-button>
                <el-button type="primary" link>
                  <el-icon><ChatDotRound /></el-icon>
                  联系
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-if="paginatedList.length === 0 && filteredList.length === 0" class="empty-state">
          <el-icon :size="64"><Document /></el-icon>
          <p>暂无相关信息</p>
          <el-button type="primary" @click="goToPublish('supply')">发布第一条信息</el-button>
        </div>

        <!-- 分页 -->
        <div class="pagination-wrapper" v-if="filteredList.length > 0">
          <el-pagination
            v-model:current-page="page"
            v-model:page-size="size"
            :total="filteredList.length"
            :page-sizes="[10, 20, 30, 50]"
            layout="total, sizes, prev, pager, next, jumper"
          />
        </div>
      </div>

      <!-- 热门推荐 -->
      <div class="hot-section">
        <div class="section-header">
          <div class="header-title">
            <el-icon><HotWater /></el-icon>
            <h3>热门推荐</h3>
          </div>
          <router-link to="/supply-demand" class="more-link">
            查看更多
            <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>
        <div class="hot-grid">
          <div class="hot-item" v-for="(item, index) in hotList" :key="index">
            <div class="hot-rank">{{ index + 1 }}</div>
            <div class="hot-info">
              <h4>{{ item.title }}</h4>
              <p>{{ item.category }} · {{ item.quantity }}</p>
            </div>
            <div class="hot-price">¥{{ item.price }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getSupplyDemandList } from '../../api/supplyDemand'
import { getCategories } from '../../api/market'
import {
  ArrowRight, TrendCharts, Document, CircleCheck, Box,
  ShoppingCart, User, ArrowUp, Grid, Search, Goods,
  Money, Location, View, ChatDotRound, HotWater
} from '@element-plus/icons-vue'

const router = useRouter()
const list = ref([])
const categories = ref(['蔬菜', '水果', '粮油', '畜牧', '水产品'])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const activeTab = ref('all')
const searchForm = ref({
  category: '',
  keyword: ''
})

const todayCount = ref(0)
const totalCount = ref(0)
const supplyCount = ref(0)
const demandCount = ref(0)
const successCount = ref(0)
const successRate = ref(0)
const activeUsers = ref(0)

const hotList = ref([])

const filteredList = computed(() => {
  let result = list.value
  
  if (searchForm.value.keyword) {
    const keyword = searchForm.value.keyword.toLowerCase()
    result = result.filter(item => 
      item.title?.toLowerCase().includes(keyword) || 
      item.description?.toLowerCase().includes(keyword)
    )
  }
  
  return result
})

const paginatedList = computed(() => {
  const start = (page.value - 1) * size.value
  const end = start + size.value
  return filteredList.value.slice(start, end)
})

onMounted(() => {
  loadData()
})

const loadData = async () => {
  try {
    const params = {
      page: 1,
      size: 100
    }
    if (activeTab.value !== 'all') {
      params.type = activeTab.value
    }
    if (searchForm.value.category) {
      params.category = searchForm.value.category
    }
    
    const res = await getSupplyDemandList(params)
    list.value = res.data.records || []
    total.value = res.data.total || list.value.length
    
    supplyCount.value = list.value.filter(item => item.type === 'supply').length
    demandCount.value = list.value.filter(item => item.type === 'demand').length
    totalCount.value = list.value.length
    
    const today = new Date().toDateString()
    todayCount.value = list.value.filter(item => 
      new Date(item.createTime).toDateString() === today
    ).length
    
    successCount.value = Math.floor(totalCount.value * 0.3)
    successRate.value = totalCount.value > 0 ? Math.round((successCount.value / totalCount.value) * 100) : 0
    activeUsers.value = new Set(list.value.map(item => item.userId)).size || Math.floor(totalCount.value * 0.5)
    
    hotList.value = list.value
      .sort((a, b) => (b.viewCount || 0) - (a.viewCount || 0))
      .slice(0, 5)
      .map(item => ({
        title: item.title,
        category: item.category,
        quantity: item.quantity,
        price: item.price
      }))
  } catch (e) {
    console.error('加载数据失败', e)
  }
}

const handleSearch = () => {
  page.value = 1
  loadData()
}

const handleSizeChange = (val) => {
  size.value = val
  page.value = 1
}

const goToDetail = (id) => {
  router.push(`/supply-demand/${id}`)
}

const goToPublish = (type) => {
  router.push(`/supply-demand/publish?type=${type}`)
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
.supply-demand-page {
  min-height: 100vh;
  background:
    linear-gradient(180deg, rgba(248, 250, 248, 0.95) 0%, rgba(240, 244, 240, 0.95) 100%),
    url('https://picsum.photos/seed/supplydemandbg/1920/1080') center/cover fixed;
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
    linear-gradient(135deg, rgba(13, 51, 32, 0.93) 0%, rgba(26, 95, 42, 0.9) 50%, rgba(46, 125, 50, 0.87) 100%),
    url('https://picsum.photos/seed/supplydemandheader/1920/400') center/cover no-repeat;
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
  background: url("data:image/svg+xml,%3Csvg width='80' height='80' viewBox='0 0 80 80' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.04'%3E%3Cpath d='M50 50c0-5.523 4.477-10 10-10s10 4.477 10 10-4.477 10-10 10-10-4.477-10-10zM10 10c0-5.523 4.477-10 10-10s10 4.477 10 10-4.477 10-10 10S10 15.523 10 10zM10 50c0-5.523 4.477-10 10-10s10 4.477 10 10-4.477 10-10 10-10-4.477-10-10zM50 10c0-5.523 4.477-10 10-10s10 4.477 10 10-4.477 10-10 10-10-4.477-10-10z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
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

.header-stats {
  display: flex;
  justify-content: center;
  gap: 40px;
}

.header-stats .stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255,255,255,0.15);
  padding: 10px 20px;
  border-radius: 25px;
  font-size: 14px;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
}

.main-container {
  padding: 32px 24px;
}

/* 统计卡片 */
.stats-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-bottom: 32px;
}

.stat-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  gap: 16px;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-card.supply .stat-icon {
  background: linear-gradient(135deg, #4CAF50, #45a049);
}

.stat-card.demand .stat-icon {
  background: linear-gradient(135deg, #FF9800, #F57C00);
}

.stat-card.success .stat-icon {
  background: linear-gradient(135deg, #2196F3, #1976D2);
}

.stat-card.users .stat-icon {
  background: linear-gradient(135deg, #9C27B0, #7B1FA2);
}

.stat-content {
  flex: 1;
}

.stat-value {
  display: block;
  font-size: 32px;
  font-weight: 700;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #999;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
}

.stat-trend.up {
  background: rgba(76, 175, 80, 0.1);
  color: #4CAF50;
}

/* 快捷发布区 */
.quick-publish {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
  margin-bottom: 32px;
}

.publish-card {
  background: #fff;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  gap: 24px;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.publish-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.publish-card.supply-card:hover {
  border-color: #4CAF50;
}

.publish-card.demand-card:hover {
  border-color: #FF9800;
}

.publish-icon {
  width: 80px;
  height: 80px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.supply-card .publish-icon {
  background: linear-gradient(135deg, rgba(76, 175, 80, 0.1), rgba(76, 175, 80, 0.2));
  color: #4CAF50;
}

.demand-card .publish-icon {
  background: linear-gradient(135deg, rgba(255, 152, 0, 0.1), rgba(255, 152, 0, 0.2));
  color: #FF9800;
}

.publish-content {
  flex: 1;
}

.publish-content h3 {
  margin: 0 0 8px 0;
  font-size: 20px;
  color: #333;
  font-weight: 600;
}

.publish-content p {
  margin: 0 0 16px 0;
  font-size: 14px;
  color: #666;
}

.publish-tags {
  display: flex;
  gap: 8px;
}

.publish-tags .tag {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.supply-card .publish-tags .tag {
  background: rgba(76, 175, 80, 0.1);
  color: #4CAF50;
}

.demand-card .publish-tags .tag {
  background: rgba(255, 152, 0, 0.1);
  color: #FF9800;
}

.publish-btn {
  flex-shrink: 0;
}

.publish-btn .btn-icon {
  margin-left: 8px;
  transition: transform 0.3s;
}

.publish-card:hover .btn-icon {
  transform: translateX(4px);
}

/* 内容区 */
.content-section {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  margin-bottom: 32px;
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.filter-tabs {
  display: flex;
  gap: 8px;
}

.tab {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 8px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.tab:hover {
  background: #f5f5f5;
}

.tab.active {
  background: linear-gradient(135deg, #4CAF50, #45a049);
  color: white;
}

.tab .count {
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  background: rgba(0,0,0,0.1);
}

.tab.active .count {
  background: rgba(255,255,255,0.2);
}

.filter-controls {
  display: flex;
  gap: 12px;
}

/* 信息列表 */
.info-list {
  display: grid;
  gap: 16px;
}

.info-card {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid transparent;
}

.info-card:hover {
  background: #fff;
  border-color: #4CAF50;
  box-shadow: 0 4px 20px rgba(76, 175, 80, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.type-badge {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 600;
}

.type-badge.supply {
  background: rgba(76, 175, 80, 0.1);
  color: #4CAF50;
}

.type-badge.demand {
  background: rgba(255, 152, 0, 0.1);
  color: #FF9800;
}

.category-tag {
  padding: 4px 10px;
  background: #e3f2fd;
  color: #2196F3;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.time {
  margin-left: auto;
  font-size: 13px;
  color: #999;
}

.card-body {
  margin-bottom: 16px;
}

.title {
  margin: 0 0 8px 0;
  font-size: 17px;
  color: #333;
  font-weight: 600;
}

.description {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

.details {
  display: flex;
  gap: 24px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #666;
}

.detail-item .el-icon {
  color: #4CAF50;
}

.detail-item .price {
  color: #f44336;
  font-size: 18px;
  font-weight: 700;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #e0e0e0;
}

.publisher {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4CAF50, #45a049);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
  font-weight: 600;
}

.publisher-info {
  display: flex;
  flex-direction: column;
}

.publisher-info .name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.publisher-info .verify {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #4CAF50;
}

.actions {
  display: flex;
  gap: 16px;
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
  margin-bottom: 20px;
}

/* 分页 */
.pagination-wrapper {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: center;
}

/* 热门推荐 */
.hot-section {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-title .el-icon {
  color: #ff6b6b;
  font-size: 22px;
}

.section-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
  font-weight: 600;
}

.more-link {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #4CAF50;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
}

.more-link:hover {
  color: #45a049;
}

.hot-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
}

.hot-item {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 16px;
  transition: all 0.3s;
  cursor: pointer;
}

.hot-item:hover {
  background: #fff;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.hot-rank {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  background: #e0e0e0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 700;
  color: #666;
  margin-bottom: 12px;
}

.hot-item:nth-child(1) .hot-rank {
  background: linear-gradient(135deg, #ff6b6b, #ee5a5a);
  color: white;
}

.hot-item:nth-child(2) .hot-rank {
  background: linear-gradient(135deg, #ffa726, #ff9800);
  color: white;
}

.hot-item:nth-child(3) .hot-rank {
  background: linear-gradient(135deg, #66bb6a, #4caf50);
  color: white;
}

.hot-info h4 {
  margin: 0 0 6px 0;
  font-size: 15px;
  color: #333;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.hot-info p {
  margin: 0 0 10px 0;
  font-size: 12px;
  color: #999;
}

.hot-price {
  font-size: 18px;
  font-weight: 700;
  color: #f44336;
}

/* 响应式 */
@media (max-width: 1200px) {
  .stats-section {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .quick-publish {
    grid-template-columns: 1fr;
  }
  
  .hot-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  
  .details {
    flex-wrap: wrap;
    gap: 12px;
  }
}

@media (max-width: 768px) {
  .page-header h2 {
    font-size: 28px;
  }
  
  .header-stats {
    flex-direction: column;
    gap: 12px;
  }
  
  .stats-section {
    grid-template-columns: 1fr;
  }
  
  .publish-card {
    flex-direction: column;
    text-align: center;
  }
  
  .filter-bar {
    flex-direction: column;
    gap: 16px;
  }
  
  .filter-tabs {
    width: 100%;
    overflow-x: auto;
  }
  
  .filter-controls {
    width: 100%;
  }
  
  .card-footer {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .hot-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>