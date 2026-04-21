<template>
  <div class="home">
    <!-- 顶部信息栏 -->
    <div class="top-bar">
      <div class="container">
        <div class="top-left">
          <span class="date">{{ currentDate }}</span>
        </div>
        <div class="top-right">
          <span class="notice">
            <el-icon><Bell /></el-icon>
            欢迎使用农产品交易平台
          </span>
        </div>
      </div>
    </div>

    <!-- 主导航搜索区 -->
    <section class="hero-section">
      <div class="hero-bg">
        <div class="hero-overlay"></div>
        <div class="container">
          <div class="hero-content">
            <h1 class="hero-title">连接田间地头与城市餐桌</h1>
            <p class="hero-subtitle">为农户和采购商搭建高效便捷的农产品交易平台</p>
            
            <!-- 搜索框 -->
            <div class="search-box">
              <div class="search-tabs">
                <span :class="['tab', { active: searchType === 'product' }]" @click="searchType = 'product'">找产品</span>
                <span :class="['tab', { active: searchType === 'supplier' }]" @click="searchType = 'supplier'">找供应商</span>
                <span :class="['tab', { active: searchType === 'purchase' }]" @click="searchType = 'purchase'">找采购</span>
              </div>
              <div class="search-input-wrapper">
                <el-input
                  v-model="searchKeyword"
                  :placeholder="searchPlaceholder"
                  size="large"
                  class="search-input"
                  @keyup.enter="handleSearch"
                >
                  <template #append>
                    <el-button type="primary" @click="handleSearch" class="search-btn">
                      <el-icon><Search /></el-icon>
                      搜索
                    </el-button>
                  </template>
                </el-input>
              </div>
              <div class="hot-keywords">
                <span>热门:</span>
                <a v-for="kw in hotKeywords" :key="kw" @click="searchKeyword = kw; handleSearch()">{{ kw }}</a>
              </div>
            </div>

            <!-- 平台核心功能 -->
            <div class="hero-features">
              <div class="feature-item">
                <div class="feature-icon"><el-icon><Goods /></el-icon></div>
                <div class="feature-info">
                  <span class="title">农产品展示</span>
                  <span class="desc">丰富的农产品信息库</span>
                </div>
              </div>
              <div class="feature-item">
                <div class="feature-icon"><el-icon><Connection /></el-icon></div>
                <div class="feature-info">
                  <span class="title">供需对接</span>
                  <span class="desc">高效的信息发布平台</span>
                </div>
              </div>
              <div class="feature-item">
                <div class="feature-icon"><el-icon><TrendCharts /></el-icon></div>
                <div class="feature-info">
                  <span class="title">行情资讯</span>
                  <span class="desc">实时市场动态分析</span>
                </div>
              </div>
              <div class="feature-item">
                <div class="feature-icon"><el-icon><User /></el-icon></div>
                <div class="feature-info">
                  <span class="title">用户中心</span>
                  <span class="desc">完善的账户管理体系</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 快捷入口 + 实时动态 -->
    <section class="quick-section">
      <div class="container">
        <div class="quick-grid">
          <!-- 左侧快捷入口 -->
          <div class="quick-links">
            <router-link to="/products" class="link-card green">
              <div class="card-bg"></div>
              <div class="card-content">
                <div class="link-icon">
                  <el-icon :size="28"><Goods /></el-icon>
                </div>
                <div class="link-info">
                  <h3>农产品</h3>
                  <p>浏览优质农产品信息</p>
                </div>
                <div class="card-arrow">
                  <el-icon><ArrowRight /></el-icon>
                </div>
              </div>
            </router-link>
            
            <router-link to="/supply-demand?type=supply" class="link-card orange">
              <div class="card-bg"></div>
              <div class="card-content">
                <div class="link-icon">
                  <el-icon :size="28"><Box /></el-icon>
                </div>
                <div class="link-info">
                  <h3>发布供应</h3>
                  <p>免费发布农产品</p>
                </div>
                <div class="card-arrow">
                  <el-icon><ArrowRight /></el-icon>
                </div>
              </div>
              <div class="card-badge">免费</div>
            </router-link>
            
            <router-link to="/supply-demand?type=demand" class="link-card blue">
              <div class="card-bg"></div>
              <div class="card-content">
                <div class="link-icon">
                  <el-icon :size="28"><ShoppingCart /></el-icon>
                </div>
                <div class="link-info">
                  <h3>发布求购</h3>
                  <p>寻找优质货源</p>
                </div>
                <div class="card-arrow">
                  <el-icon><ArrowRight /></el-icon>
                </div>
              </div>
              <div class="card-stats">
                <span class="stat">{{ homeStats.todayDemand }}</span>
                <span class="stat-label">今日求购</span>
              </div>
            </router-link>
            
            <router-link to="/market" class="link-card cyan">
              <div class="card-bg"></div>
              <div class="card-content">
                <div class="link-icon">
                  <el-icon :size="28"><TrendCharts /></el-icon>
                </div>
                <div class="link-info">
                  <h3>市场行情</h3>
                  <p>实时价格走势</p>
                </div>
                <div class="card-arrow">
                  <el-icon><ArrowRight /></el-icon>
                </div>
              </div>
              <div class="card-trend" :class="homeStats.priceTrend">
                <el-icon><ArrowUp v-if="homeStats.priceTrend === 'up'" /><ArrowDown v-else /></el-icon>
                <span>{{ homeStats.priceChange >= 0 ? '+' : '' }}{{ homeStats.priceChange }}%</span>
              </div>
            </router-link>
          </div>

          <!-- 右侧实时动态 -->
          <div class="realtime-panel">
            <div class="panel-header">
              <h3><el-icon><Timer /></el-icon> 最新供需动态</h3>
            </div>
            <div class="dynamic-list-wrapper">
              <div class="dynamic-list" :class="{ 'scroll-animate': realtimeDeals.length >= 4 }">
                <div class="dynamic-item" v-for="(item, index) in realtimeDeals" :key="'a' + index" @click="goToSupplyDetail(item.id)">
                  <div class="dynamic-icon" :class="item.type">
                    <el-icon v-if="item.type === 'supply'"><Box /></el-icon>
                    <el-icon v-else><ShoppingCart /></el-icon>
                  </div>
                  <div class="dynamic-content">
                    <p class="dynamic-text">{{ item.content }}</p>
                    <span class="dynamic-time">{{ item.time }}</span>
                  </div>
                  <div class="dynamic-price" v-if="item.price">¥{{ item.price }}</div>
                </div>
                <div class="dynamic-item" v-for="(item, index) in realtimeDeals" :key="'b' + index" v-if="realtimeDeals.length >= 4" @click="goToSupplyDetail(item.id)">
                  <div class="dynamic-icon" :class="item.type">
                    <el-icon v-if="item.type === 'supply'"><Box /></el-icon>
                    <el-icon v-else><ShoppingCart /></el-icon>
                  </div>
                  <div class="dynamic-content">
                    <p class="dynamic-text">{{ item.content }}</p>
                    <span class="dynamic-time">{{ item.time }}</span>
                  </div>
                  <div class="dynamic-price" v-if="item.price">¥{{ item.price }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 产品分类 + 热门推荐 -->
    <section class="category-section">
      <div class="container">
        <div class="section-layout">
          <!-- 左侧分类导航 -->
          <div class="category-nav">
            <h3 class="nav-title">
              <el-icon><Menu /></el-icon>
              产品分类
            </h3>
            <ul class="category-list">
              <li v-for="cat in categories" :key="cat.name" :class="{ active: activeCategory === cat.name }" @click="handleCategoryClick(cat.name)">
                <div class="cat-icon" :style="{ background: cat.color }">
                  <el-icon><component :is="cat.icon" /></el-icon>
                </div>
                <span class="cat-name">{{ cat.name }}</span>
                <el-icon class="cat-arrow"><ArrowRight /></el-icon>
              </li>
            </ul>
          </div>

          <!-- 右侧内容区 -->
          <div class="content-area">
            <!-- 分类快捷标签 -->
            <div class="subcategory-tags">
              <span class="tag" v-for="tag in subCategories" :key="tag" @click="searchKeyword = tag; handleSearch()">{{ tag }}</span>
            </div>

            <!-- 热门产品网格 -->
            <div class="products-section">
              <div class="section-header">
                <h2>
                  <el-icon><HotWater /></el-icon>
                  热门农产品
                </h2>
                <router-link to="/products" class="more-link">
                  查看更多 <el-icon><ArrowRight /></el-icon>
                </router-link>
              </div>
              <div class="products-grid">
                <div class="product-card-simple" v-for="product in filteredProducts.slice(0, 8)" :key="product.id" @click="goToProduct(product.id)">
                  <div class="card-header">
                    <span class="category-badge" :style="{ backgroundColor: getCategoryBgColor(product.category), color: getCategoryColor(product.category) }">
                      {{ product.category }}
                    </span>
                    <span class="stock-tag" v-if="product.stock < 50">库存紧张</span>
                  </div>
                  <h4 class="product-title">{{ product.name }}</h4>
                  <div class="price-row">
                    <span class="price">
                      <em>¥</em>{{ product.price }}
                      <small>/{{ product.unit }}</small>
                    </span>
                  </div>
                  <div class="info-row">
                    <span class="info-item">
                      <el-icon><Location /></el-icon>
                      {{ product.origin }}
                    </span>
                  </div>
                  <div class="card-footer">
                    <span class="seller">
                      <el-icon><User /></el-icon>
                      {{ product.seller }}
                    </span>
                    <span class="view-detail">查看详情 →</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 行情速递 -->
    <section class="market-section">
      <div class="container">
        <div class="section-header">
          <h2>
            <el-icon><DataLine /></el-icon>
            今日行情
          </h2>
          <router-link to="/market" class="more-link">
            查看完整行情 <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>
        
        <div class="market-content-full">
          <div class="ranking-panel-full">
            <div class="ranking-list">
              <div class="ranking-item" v-for="(item, index) in marketRanking" :key="index">
                <span class="rank-num" :class="{ top: index < 3 }">{{ index + 1 }}</span>
                <div class="rank-info">
                  <span class="rank-name">{{ item.name }}</span>
                  <span class="rank-category">{{ item.category }}</span>
                </div>
                <div class="rank-price">
                  <span class="current">¥{{ item.price }}/{{ item.unit || '斤' }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 供需信息 -->
    <section class="supply-demand-section">
      <div class="container">
        <div class="sd-layout">
          <!-- 最新供应 -->
          <div class="sd-block">
            <div class="block-header">
              <h3>
                <el-icon><Box /></el-icon>
                最新供应
                <span class="badge">{{ supplyCount }}条</span>
              </h3>
              <router-link to="/supply-demand?type=supply" class="more-link">
                更多 <el-icon><ArrowRight /></el-icon>
              </router-link>
            </div>
            <div class="sd-list">
              <div class="sd-item" v-for="item in supplyList.slice(0, 5)" :key="item.id" @click="goToSupplyDetail(item.id)">
                <div class="sd-main">
                  <h4 class="sd-title">{{ item.title }}</h4>
                  <div class="sd-tags">
                    <span class="tag category">{{ item.category }}</span>
                    <span class="tag location">
                      <el-icon><Location /></el-icon>{{ item.location }}
                    </span>
                  </div>
                </div>
                <div class="sd-side">
                  <span class="sd-price">¥{{ item.price }}/{{ item.unit }}</span>
                  <span class="sd-quantity">{{ item.quantity }}</span>
                  <span class="sd-time">{{ item.time }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 紧急求购 -->
          <div class="sd-block">
            <div class="block-header">
              <h3>
                <el-icon><ShoppingCart /></el-icon>
                紧急求购
                <span class="badge urgent">{{ demandCount }}条</span>
              </h3>
              <router-link to="/supply-demand?type=demand" class="more-link">
                更多 <el-icon><ArrowRight /></el-icon>
              </router-link>
            </div>
            <div class="sd-list">
              <div class="sd-item demand" v-for="item in demandList.slice(0, 5)" :key="item.id" @click="goToSupplyDetail(item.id)">
                <div class="sd-main">
                  <h4 class="sd-title">{{ item.title }}</h4>
                  <div class="sd-tags">
                    <span class="tag category">{{ item.category }}</span>
                    <span class="tag urgent">紧急</span>
                  </div>
                </div>
                <div class="sd-side">
                  <span class="sd-price">¥{{ item.price }}/{{ item.unit }}</span>
                  <span class="sd-quantity">求购{{ item.quantity }}</span>
                  <span class="sd-time">{{ item.time }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 资讯政策 -->
    <section class="news-section">
      <div class="container">
        <div class="news-layout">
          <!-- 左侧头条 -->
          <div class="news-highlight">
            <div class="section-header">
              <h2>
                <el-icon><Collection /></el-icon>
                资讯政策
              </h2>
              <router-link to="/news" class="more-link">
                更多资讯 <el-icon><ArrowRight /></el-icon>
              </router-link>
            </div>
            <div class="highlight-card" @click="goToNews(highlightNews.id)">
              <div class="highlight-image">
                <img :src="highlightNews.cover" :alt="highlightNews.title" />
                <span class="highlight-tag">头条</span>
              </div>
              <div class="highlight-content">
                <h3>{{ highlightNews.title }}</h3>
                <p>{{ highlightNews.summary }}</p>
                <div class="highlight-meta">
                  <span><el-icon><View /></el-icon> {{ highlightNews.views }}</span>
                  <span><el-icon><Timer /></el-icon> {{ highlightNews.time }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 右侧列表 -->
          <div class="news-list">
            <div class="news-item" v-for="news in newsList.slice(0, 4)" :key="news.id" @click="goToNews(news.id)">
              <span class="news-category" :class="news.categoryType">{{ news.category }}</span>
              <h4 class="news-title">{{ news.title }}</h4>
              <span class="news-time">{{ news.time }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 平台优势 -->
    <section class="features-section">
      <div class="container">
        <h2 class="section-title-center">为什么选择我们</h2>
        <div class="features-grid">
          <div class="feature-item">
            <div class="feature-icon" style="background: linear-gradient(135deg, #4CAF50, #2e7d32);">
              <el-icon :size="40"><CircleCheck /></el-icon>
            </div>
            <h3>源头直供</h3>
            <p>产地直发，省去中间环节<br/>价格更实惠，品质有保障</p>
          </div>
          <div class="feature-item">
            <div class="feature-icon" style="background: linear-gradient(135deg, #2196F3, #1976D2);">
              <el-icon :size="40"><CircleCheck /></el-icon>
            </div>
            <h3>交易保障</h3>
            <p>实名认证，资金托管<br/>让每一笔交易都安全可靠</p>
          </div>
          <div class="feature-item">
            <div class="feature-icon" style="background: linear-gradient(135deg, #FF9800, #F57C00);">
              <el-icon :size="40"><Van /></el-icon>
            </div>
            <h3>物流配套</h3>
            <p>整合优质物流资源<br/>冷链运输，新鲜直达</p>
          </div>
          <div class="feature-item">
            <div class="feature-icon" style="background: linear-gradient(135deg, #9C27B0, #7B1FA2);">
              <el-icon :size="40"><Service /></el-icon>
            </div>
            <h3>专业服务</h3>
            <p>7×24小时客服支持<br/>专业团队全程跟进</p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCategoryIcon, getCategoryColor, getCategoryBgColor } from '../../utils/imageMap'
import { getProductList } from '../../api/product'
import { getPrices, getNewsList, getHomeStats } from '../../api/market'
import { getSupplyDemandList } from '../../api/supplyDemand'
import {
  Search, ArrowRight, TrendCharts, HotWater, Location,
  Box, ShoppingCart, Collection, View, Timer, Menu,
  DataLine, CircleCheck, Sunny, Bell, User, OfficeBuilding,
  Goods, Money, Basketball, Van, Service, ArrowUp, ArrowDown, Minus,
  Food, Apple, Dish, Watermelon, Tools, Present, Folder, Connection,
  Orange, Bowl, IceCream, Dessert
} from '@element-plus/icons-vue'

const router = useRouter()
const searchKeyword = ref('')
const searchType = ref('product')
const activeCategory = ref('蔬菜')

const searchPlaceholder = computed(() => {
  const placeholders = {
    product: '搜索农产品名称、品种、产地...',
    supplier: '搜索供应商、合作社、产地...',
    purchase: '搜索采购需求、采购商...'
  }
  return placeholders[searchType.value]
})

const hotKeywords = ref([])

const currentDate = computed(() => {
  const date = new Date()
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`
})

const categories = [
  { name: '蔬菜', icon: Food, color: '#4CAF50' },
  { name: '水果', icon: Apple, color: '#FF9800' },
  { name: '粮油', icon: Goods, color: '#795548' },
  { name: '畜牧', icon: Dish, color: '#F44336' },
  { name: '水产品', icon: Watermelon, color: '#2196F3' },
  { name: '农资', icon: Tools, color: '#9C27B0' },
  { name: '苗木', icon: Folder, color: '#009688' },
  { name: '特产', icon: Present, color: '#E91E63' }
]

const subCategories = ref([])

const realtimeDeals = ref([])

const homeStats = ref({
  todayDemand: 0,
  priceChange: 0,
  priceTrend: 'up'
})

const products = ref([])
const loading = ref(false)

const marketRanking = ref([])
const marketPrices = ref([])

const supplyCount = ref(0)
const demandCount = ref(0)
const supplyList = ref([])
const demandList = ref([])

const highlightNews = ref({
  id: 1,
  title: '2025年中央一号文件发布：全面推进乡村振兴',
  summary: '文件强调要巩固拓展脱贫攻坚成果，全面推进乡村振兴，加快农业农村现代化。',
  cover: 'https://picsum.photos/seed/news1/600/400',
  views: '12.5万',
  time: '今天 08:00'
})

const newsList = ref([])

const filteredProducts = computed(() => {
  return products.value
})

const loadProducts = async () => {
  try {
    loading.value = true
    const res = await getProductList({ page: 1, size: 100 })
    products.value = res.data.records.map(p => ({
      ...p,
      origin: p.contactAddress || '产地未知',
      sales: Math.floor(Math.random() * 10000) + 1000,
      seller: p.contactName || '农户',
      verified: true,
      tag: p.stock < 100 ? '热销' : ''
    }))
    
    const nameCounts = {}
    products.value.forEach(p => {
      const name = p.name.replace(/^(新鲜|优质|有机|红皮|紫皮|农家|本地|散养|野生|大|小)/, '')
      nameCounts[name] = (nameCounts[name] || 0) + 1
    })
    hotKeywords.value = Object.entries(nameCounts)
      .sort((a, b) => b[1] - a[1])
      .slice(0, 6)
      .map(([name]) => name)
    
    const categoryProducts = products.value.filter(p => p.category === activeCategory.value)
    subCategories.value = [...new Set(categoryProducts.map(p => p.name.replace(/^(新鲜|优质|有机|红皮|紫皮|农家|本地|散养|野生|大|小)/, '')))].slice(0, 8)
    if (subCategories.value.length < 4) {
      subCategories.value = [...new Set(products.value.map(p => p.name.replace(/^(新鲜|优质|有机|红皮|紫皮|农家|本地|散养|野生|大|小)/, '')))].slice(0, 8)
    }
  } catch (e) {
    console.error('加载产品失败', e)
  } finally {
    loading.value = false
  }
}

const loadMarketData = async () => {
  try {
    const res = await getPrices()
    marketPrices.value = res.data || []
    
    marketRanking.value = (marketPrices.value || []).slice(0, 10).map((item) => {
      return {
        name: item.productName,
        category: item.category,
        price: item.price,
        unit: item.unit || '斤'
      }
    })
  } catch (e) {
    console.error('加载行情数据失败', e)
  }
}

const loadSupplyDemand = async () => {
  try {
    const [supplyRes, demandRes] = await Promise.all([
      getSupplyDemandList({ page: 1, size: 5, type: 'supply' }),
      getSupplyDemandList({ page: 1, size: 5, type: 'demand' })
    ])
    
    supplyList.value = (supplyRes.data.records || []).map(item => ({
      id: item.id,
      title: item.title,
      category: item.category,
      location: item.contactAddress || '未知',
      price: item.price || '面议',
      unit: '斤',
      quantity: item.quantity,
      time: formatTime(item.createTime)
    }))
    supplyCount.value = supplyRes.data.total || 0
    
    demandList.value = (demandRes.data.records || []).map(item => ({
      id: item.id,
      title: item.title,
      category: item.category,
      price: item.price || '面议',
      unit: '斤',
      quantity: item.quantity,
      time: formatTime(item.createTime)
    }))
    demandCount.value = demandRes.data.total || 0
    
    const supplyDeals = (supplyRes.data.records || []).map(item => ({
      id: item.id,
      type: 'supply',
      content: `发布了${item.quantity || ''}${item.title}`,
      time: formatTime(item.createTime),
      price: item.price
    }))
    const demandDeals = (demandRes.data.records || []).map(item => ({
      id: item.id,
      type: 'demand',
      content: `求购${item.quantity || ''}${item.title}`,
      time: formatTime(item.createTime),
      price: item.price
    }))
    
    realtimeDeals.value = [...supplyDeals, ...demandDeals]
      .sort((a, b) => a.time.localeCompare(b.time))
      .slice(0, 5)
  } catch (e) {
    console.error('加载供需数据失败', e)
  }
}

const loadNews = async () => {
  try {
    const res = await getNewsList({ page: 1, size: 5 })
    const records = res.data.records || []
    
    if (records.length > 0) {
      highlightNews.value = {
        id: records[0].id,
        title: records[0].title,
        summary: records[0].content ? records[0].content.substring(0, 100) + '...' : '',
        cover: records[0].coverImage || 'https://picsum.photos/seed/news2/600/400',
        views: records[0].viewCount || 0,
        time: formatTime(records[0].createTime)
      }
      
      newsList.value = records.slice(1, 5).map(item => ({
        id: item.id,
        title: item.title,
        category: getCategoryName(item),
        categoryType: getCategoryType(item),
        time: formatTime(item.createTime)
      }))
    }
  } catch (e) {
    console.error('加载资讯失败', e)
  }
}

const formatTime = (time) => {
  if (!time) return '刚刚'
  const date = new Date(time)
  const now = new Date()
  const diff = Math.floor((now - date) / 1000 / 60)
  if (diff < 60) return `${diff}分钟前`
  if (diff < 1440) return `${Math.floor(diff / 60)}小时前`
  return `${Math.floor(diff / 1440)}天前`
}

const getCategoryName = (item) => {
  if (item.title?.includes('政策') || item.title?.includes('文件')) return '政策'
  if (item.title?.includes('技术') || item.title?.includes('智慧')) return '技术'
  return '市场'
}

const getCategoryType = (item) => {
  const name = getCategoryName(item)
  return { '政策': 'policy', '技术': 'tech', '市场': 'market' }[name] || 'market'
}

const getCategoryIconComponent = (category) => {
  return getCategoryIcon(category)
}

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push(`/products?keyword=${encodeURIComponent(searchKeyword.value)}`)
  }
}

const handleCategoryClick = (categoryName) => {
  activeCategory.value = categoryName
  router.push(`/products?category=${encodeURIComponent(categoryName)}`)
}

const goToProduct = (id) => {
  router.push(`/product/${id}`)
}

const goToSupplyDetail = (id) => {
  router.push(`/supply-demand/${id}`)
}

const goToNews = (id) => {
  router.push(`/news/${id}`)
}

const loadHomeStats = async () => {
  try {
    const res = await getHomeStats()
    homeStats.value = res.data
  } catch (e) {
    console.error('加载首页统计失败', e)
  }
}

onMounted(() => {
  loadProducts()
  loadMarketData()
  loadSupplyDemand()
  loadNews()
  loadHomeStats()
})
</script>

<style scoped>
/* 基础布局 */
.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
}

/* 顶部信息栏 */
.top-bar {
  background: linear-gradient(135deg, #1a5f2a 0%, #2e7d32 50%, #388e3c 100%);
  color: rgba(255, 255, 255, 0.95);
  padding: 10px 0;
  font-size: 13px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.top-bar .container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.top-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.top-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.top-right .divider {
  opacity: 0.3;
}

.market-index strong {
  color: #fff;
  font-weight: 600;
}

.market-index .change {
  margin-left: 8px;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 11px;
}

.market-index .change.down {
  background: rgba(244, 67, 54, 0.2);
  color: #ffcdd2;
}

.notice {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
}

.notice:hover {
  color: #fff;
}

/* 主导航区 */
.hero-section {
  position: relative;
  min-height: 520px;
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #1a5f2a 0%, #2e7d32 35%, #388e3c 65%, #43a047 100%);
}

.hero-bg::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: 
    radial-gradient(ellipse at 20% 30%, rgba(129, 199, 132, 0.3) 0%, transparent 50%),
    radial-gradient(ellipse at 80% 70%, rgba(165, 214, 167, 0.25) 0%, transparent 50%),
    radial-gradient(ellipse at 50% 50%, rgba(200, 230, 201, 0.2) 0%, transparent 70%);
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(180deg, rgba(0,0,0,0.02) 0%, rgba(0,0,0,0.1) 100%);
}

.hero-content {
  position: relative;
  z-index: 1;
  padding: 40px 0;
  text-align: center;
  color: white;
}

.hero-title {
  font-size: 40px;
  font-weight: 700;
  margin: 0 0 12px 0;
  text-shadow: 0 2px 8px rgba(0,0,0,0.15);
  letter-spacing: 2px;
}

.hero-subtitle {
  font-size: 17px;
  opacity: 0.92;
  margin: 0 0 32px 0;
  letter-spacing: 1px;
}

/* 搜索框 */
.search-box {
  max-width: 700px;
  margin: 0 auto 32px;
  background: rgba(255, 255, 255, 0.18);
  backdrop-filter: blur(12px);
  border-radius: 16px;
  padding: 20px;
  border: 1px solid rgba(255, 255, 255, 0.25);
  box-shadow: 0 8px 32px rgba(0,0,0,0.1);
}

.search-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.search-tabs .tab {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
  background: rgba(255, 255, 255, 0.1);
}

.search-tabs .tab:hover,
.search-tabs .tab.active {
  background: #fff;
  color: #1a5f2a;
}

.search-input-wrapper {
  margin-bottom: 12px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 8px;
  padding: 4px;
}

.search-btn {
  padding: 12px 32px;
  font-size: 16px;
}

.hot-keywords {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;
}

.hot-keywords span {
  opacity: 0.8;
}

.hot-keywords a {
  color: #fff;
  text-decoration: none;
  cursor: pointer;
  padding: 2px 8px;
  border-radius: 4px;
  background: rgba(255, 255, 255, 0.1);
  transition: all 0.3s;
}

.hot-keywords a:hover {
  background: rgba(255, 255, 255, 0.25);
}

/* 平台核心功能 */
.hero-features {
  display: flex;
  justify-content: center;
  gap: 24px;
  margin-bottom: 40px;
}

.hero-features .feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  background: rgba(255, 255, 255, 0.12);
  padding: 14px 20px;
  border-radius: 12px;
  backdrop-filter: blur(8px);
  transition: all 0.3s;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.hero-features .feature-item:hover {
  background: rgba(255, 255, 255, 0.22);
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.15);
}

.hero-features .feature-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
}

.hero-features .feature-info {
  text-align: left;
}

.hero-features .feature-info .title {
  display: block;
  font-size: 15px;
  font-weight: 600;
}

.hero-features .feature-info .desc {
  font-size: 12px;
  opacity: 0.8;
  margin-top: 2px;
}

/* 快捷入口区 */
.quick-section {
  padding: 32px 0;
  background: linear-gradient(180deg, #f0f7f0 0%, #e8f5e9 50%, #f5faf5 100%);
}

.quick-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
}

.quick-links {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.link-card {
  position: relative;
  display: flex;
  flex-direction: column;
  padding: 24px;
  background: linear-gradient(180deg, #ffffff 0%, #fafcf9 100%);
  border-radius: 16px;
  text-decoration: none;
  color: inherit;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 20px rgba(0,0,0,0.06);
  overflow: hidden;
  min-height: 140px;
  border: 1px solid #e8f5e9;
}

.link-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  transition: height 0.3s ease;
}

.link-card.green::before { background: linear-gradient(90deg, #4CAF50, #8BC34A); }
.link-card.orange::before { background: linear-gradient(90deg, #FF9800, #FFC107); }
.link-card.blue::before { background: linear-gradient(90deg, #2196F3, #03A9F4); }
.link-card.cyan::before { background: linear-gradient(90deg, #00BCD4, #26C6DA); }

.link-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 20px 40px rgba(76, 175, 80, 0.15);
  border-color: #4CAF50;
}

.link-card:hover::before {
  height: 6px;
}

.card-bg {
  position: absolute;
  top: 0;
  right: 0;
  width: 100px;
  height: 100px;
  opacity: 0.08;
  transition: all 0.4s ease;
}

.link-card.green .card-bg { background: radial-gradient(circle at center, #4CAF50 0%, transparent 70%); }
.link-card.orange .card-bg { background: radial-gradient(circle at center, #FF9800 0%, transparent 70%); }
.link-card.blue .card-bg { background: radial-gradient(circle at center, #2196F3 0%, transparent 70%); }
.link-card.cyan .card-bg { background: radial-gradient(circle at center, #00BCD4 0%, transparent 70%); }

.link-card:hover .card-bg {
  opacity: 0.15;
  transform: scale(1.5) translate(10%, -10%);
}

.card-content {
  display: flex;
  align-items: center;
  gap: 16px;
  position: relative;
  z-index: 1;
}

.link-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.link-card:hover .link-icon {
  transform: scale(1.1) rotate(-5deg);
}

.link-card.green .link-icon { background: linear-gradient(135deg, #4CAF50 0%, #66BB6A 100%); }
.link-card.orange .link-icon { background: linear-gradient(135deg, #FF9800 0%, #FFB74D 100%); }
.link-card.blue .link-icon { background: linear-gradient(135deg, #2196F3 0%, #42A5F5 100%); }
.link-card.cyan .link-icon { background: linear-gradient(135deg, #00BCD4 0%, #4DD0E1 100%); }

.link-info {
  flex: 1;
}

.link-info h3 {
  font-size: 18px;
  font-weight: 700;
  margin: 0 0 4px 0;
  color: #1a1a1a;
}

.link-info p {
  font-size: 13px;
  color: #888;
  margin: 0;
}

.card-arrow {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  transition: all 0.3s ease;
}

.link-card:hover .card-arrow {
  background: #e8f5e9;
  color: #4CAF50;
  transform: translateX(4px);
}

.link-card.orange:hover .card-arrow { background: #fff3e0; color: #FF9800; }
.link-card.blue:hover .card-arrow { background: #e3f2fd; color: #2196F3; }
.link-card.cyan:hover .card-arrow { background: #e0f7fa; color: #00BCD4; }

.card-stats {
  display: flex;
  align-items: baseline;
  gap: 6px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px dashed #eee;
  position: relative;
  z-index: 1;
}

.card-stats .stat {
  font-size: 24px;
  font-weight: 800;
  background: linear-gradient(135deg, #333 0%, #666 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.card-stats .stat-label {
  font-size: 12px;
  color: #999;
}

.card-badge {
  position: absolute;
  top: 16px;
  right: 16px;
  background: linear-gradient(135deg, #FF5722, #FF9800);
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(255, 87, 34, 0.4);
  animation: badge-pulse 2s infinite;
}

@keyframes badge-pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.card-trend {
  position: absolute;
  top: 16px;
  right: 16px;
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
}

.card-trend.up {
  background: rgba(76, 175, 80, 0.1);
  color: #4CAF50;
}

.card-trend.down {
  background: rgba(244, 67, 54, 0.1);
  color: #F44336;
}

/* 实时动态面板 */
.realtime-panel {
  background: linear-gradient(180deg, #ffffff 0%, #fafcf9 100%);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  border: 1px solid #e8f5e9;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.panel-header h3 {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  margin: 0;
}

.dynamic-list-wrapper {
  height: 280px;
  overflow: hidden;
  position: relative;
}

.dynamic-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.dynamic-list.scroll-animate {
  animation: scrollUp 15s linear infinite;
}

.dynamic-list.scroll-animate:hover {
  animation-play-state: paused;
}

@keyframes scrollUp {
  0% {
    transform: translateY(0);
  }
  100% {
    transform: translateY(-50%);
  }
}

.dynamic-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  background: linear-gradient(135deg, #f5faf5 0%, #f0f7f0 100%);
  border-radius: 8px;
  font-size: 13px;
  transition: all 0.3s;
  cursor: pointer;
}

.dynamic-item:hover {
  background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
  transform: translateX(4px);
}

.dynamic-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.dynamic-icon.supply { background: #4CAF50; }
.dynamic-icon.demand { background: #FF9800; }

.dynamic-content {
  flex: 1;
  min-width: 0;
}

.dynamic-text {
  margin: 0 0 4px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.dynamic-time {
  font-size: 11px;
  color: #999;
}

.dynamic-price {
  color: #f44336;
  font-weight: 600;
  font-size: 14px;
}

/* 产品分类区 */
.category-section {
  padding: 48px 0;
  background: linear-gradient(180deg, #ffffff 0%, #fafcf9 100%);
}

.section-layout {
  display: grid;
  grid-template-columns: 220px 1fr;
  gap: 24px;
}

/* 分类导航 */
.category-nav {
  background: linear-gradient(180deg, #f8fcf8 0%, #f0f7f0 100%);
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #e8f5e9;
}

.nav-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 16px 0;
  padding-bottom: 12px;
  border-bottom: 1px solid #e8e8e8;
}

.category-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.category-list li {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 4px;
}

.category-list li:hover,
.category-list li.active {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.cat-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
}

.cat-name {
  flex: 1;
  font-size: 14px;
}

.cat-arrow {
  font-size: 12px;
  color: #ccc;
}

/* 子分类标签 */
.subcategory-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 20px;
}

.subcategory-tags .tag {
  padding: 6px 14px;
  background: linear-gradient(135deg, #f0f7f0 0%, #e8f5e9 100%);
  border-radius: 16px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #c8e6c9;
}

.subcategory-tags .tag:hover {
  background: linear-gradient(135deg, #4CAF50 0%, #388e3c 100%);
  color: white;
  border-color: #388e3c;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.3);
}

/* 产品网格 */
.products-section .section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.products-section .section-header h2 {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  margin: 0;
}

.more-link {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #4CAF50;
  text-decoration: none;
  font-size: 14px;
}

.more-link:hover {
  color: #2e7d32;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.product-card-simple {
  background: linear-gradient(180deg, #ffffff 0%, #fafcf9 100%);
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #e8f5e9;
  display: flex;
  flex-direction: column;
}

.product-card-simple:hover {
  transform: translateY(-6px);
  box-shadow: 0 16px 32px rgba(76, 175, 80, 0.15);
  border-color: #4CAF50;
  background: #fff;
}

.product-card-simple .card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.product-card-simple .category-badge {
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.product-card-simple .stock-tag {
  background: #fff3e0;
  color: #ff9800;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
}

.product-card-simple .product-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 12px 0;
  color: #333;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-card-simple .price-row {
  margin-bottom: 10px;
}

.product-card-simple .price {
  color: #f44336;
  font-size: 22px;
  font-weight: 700;
}

.product-card-simple .price em {
  font-size: 14px;
  font-style: normal;
}

.product-card-simple .price small {
  font-size: 12px;
  color: #999;
  font-weight: 400;
}

.product-card-simple .info-row {
  margin-bottom: 12px;
}

.product-card-simple .info-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #666;
}

.product-card-simple .info-item .el-icon {
  color: #999;
}

.product-card-simple .card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 12px;
  border-top: 1px solid #f5f5f5;
  margin-top: auto;
}

.product-card-simple .seller {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #666;
}

.product-card-simple .seller .el-icon {
  color: #4CAF50;
}

.product-card-simple .view-detail {
  font-size: 12px;
  color: #4CAF50;
  font-weight: 500;
}

/* 行情区 */
.market-section {
  padding: 48px 0;
  background: linear-gradient(180deg, #f5faf5 0%, #e8f5e9 50%, #f0f7f0 100%);
}

.market-section .section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.market-section .section-header h2 {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  margin: 0;
}

.header-tabs {
  display: flex;
  gap: 8px;
}

.header-tabs .tab {
  padding: 6px 16px;
  border-radius: 16px;
  font-size: 14px;
  cursor: pointer;
  background: #fff;
  transition: all 0.3s;
}

.header-tabs .tab:hover,
.header-tabs .tab.active {
  background: #4CAF50;
  color: white;
}

.market-content-full {
  display: block;
}

.ranking-panel-full {
  background: linear-gradient(180deg, #ffffff 0%, #fafcf9 100%);
  border-radius: 12px;
  padding: 16px;
  border: 1px solid #e8f5e9;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}

.market-content {
  display: grid;
  grid-template-columns: 1.5fr 1fr;
  gap: 24px;
}

.chart-panel,
.ranking-panel {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.chart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.chart-header h4 {
  margin: 0;
  font-size: 16px;
}

.chart-legend {
  display: flex;
  gap: 16px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #666;
}

.legend-item .dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.chart-placeholder {
  height: 250px;
  display: flex;
  align-items: flex-end;
  padding-bottom: 10px;
}

.chart-bars {
  display: flex;
  align-items: flex-end;
  justify-content: space-around;
  width: 100%;
  height: 100%;
  padding: 0 20px;
}

.bar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.bar-group {
  display: flex;
  align-items: flex-end;
  gap: 4px;
}

.bar {
  width: 20px;
  min-height: 10px;
  border-radius: 4px 4px 0 0;
  transition: all 0.3s;
}

.bar.last-week {
  background: #e0e0e0;
}

.bar.current {
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
}

.bar-label {
  font-size: 12px;
  color: #666;
}

/* 排行列表 */
.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  background: #f8faf8;
  border-radius: 8px;
}

.rank-num {
  width: 24px;
  height: 24px;
  border-radius: 4px;
  background: #e0e0e0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  color: #666;
}

.rank-num.top {
  background: #FF5722;
  color: white;
}

.rank-info {
  flex: 1;
}

.rank-name {
  display: block;
  font-weight: 600;
  font-size: 14px;
}

.rank-category {
  font-size: 12px;
  color: #999;
}

.rank-price {
  text-align: right;
}

.rank-price .current {
  display: block;
  font-weight: 600;
  color: #f44336;
}

.rank-price .change {
  font-size: 12px;
}

.rank-price .change.up {
  color: #f44336;
}

.rank-price .change.down {
  color: #4CAF50;
}

/* 供需区 */
.supply-demand-section {
  padding: 48px 0;
  background: linear-gradient(180deg, #f5faf5 0%, #e8f5e9 50%, #f0f7f0 100%);
}

.sd-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.sd-block {
  background: #f8faf8;
  border-radius: 12px;
  padding: 20px;
}

.block-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.block-header h3 {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  margin: 0;
}

.block-header .badge {
  background: #4CAF50;
  color: white;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 11px;
  margin-left: 8px;
}

.block-header .badge.urgent {
  background: #f44336;
}

.sd-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.sd-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  background: #fff;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.sd-item:hover {
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
}

.sd-main {
  flex: 1;
  min-width: 0;
}

.sd-title {
  font-size: 14px;
  font-weight: 600;
  margin: 0 0 8px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.sd-tags {
  display: flex;
  gap: 8px;
}

.sd-tags .tag {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 4px;
}

.sd-tags .tag.category {
  background: #e8f5e9;
  color: #4CAF50;
}

.sd-tags .tag.location {
  display: flex;
  align-items: center;
  gap: 2px;
  background: #fff3e0;
  color: #FF9800;
}

.sd-tags .tag.urgent {
  background: #ffebee;
  color: #f44336;
}

.sd-side {
  text-align: right;
}

.sd-price {
  display: block;
  color: #f44336;
  font-weight: 700;
  font-size: 15px;
}

.sd-quantity {
  display: block;
  font-size: 12px;
  color: #666;
  margin: 4px 0;
}

.sd-time {
  font-size: 11px;
  color: #999;
}

/* 资讯区 */
.news-section {
  padding: 48px 0;
  background: linear-gradient(180deg, #ffffff 0%, #fafcf9 100%);
}

.news-layout {
  display: grid;
  grid-template-columns: 1.2fr 1fr;
  gap: 24px;
}

.news-highlight .section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.news-highlight .section-header h2 {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  margin: 0;
}

.highlight-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  cursor: pointer;
  transition: all 0.3s;
}

.highlight-card:hover {
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
}

.highlight-image {
  position: relative;
  height: 200px;
}

.highlight-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.highlight-tag {
  position: absolute;
  top: 12px;
  left: 12px;
  background: #f44336;
  color: white;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.highlight-content {
  padding: 20px;
}

.highlight-content h3 {
  font-size: 18px;
  margin: 0 0 12px 0;
  line-height: 1.4;
}

.highlight-content p {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin: 0 0 16px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.highlight-meta {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #999;
}

.highlight-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.news-list {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.news-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s;
}

.news-item:last-child {
  border-bottom: none;
}

.news-item:hover {
  background: #f8faf8;
  margin: 0 -12px;
  padding-left: 12px;
  padding-right: 12px;
  border-radius: 8px;
}

.news-category {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 4px;
  white-space: nowrap;
}

.news-category.policy {
  background: #e3f2fd;
  color: #2196F3;
}

.news-category.tech {
  background: #f3e5f5;
  color: #9C27B0;
}

.news-category.market {
  background: #e8f5e9;
  color: #4CAF50;
}

.news-title {
  flex: 1;
  font-size: 14px;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.news-time {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
}

/* 平台优势 */
.features-section {
  padding: 60px 0;
  background: linear-gradient(135deg, #1a5f2a 0%, #2e7d32 100%);
  color: white;
}

.section-title-center {
  text-align: center;
  font-size: 32px;
  margin: 0 0 48px 0;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 32px;
}

.feature-item {
  text-align: center;
}

.feature-icon {
  width: 80px;
  height: 80px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  color: white;
}

.feature-item h3 {
  font-size: 20px;
  margin: 0 0 12px 0;
}

.feature-item p {
  font-size: 14px;
  opacity: 0.9;
  line-height: 1.6;
  margin: 0;
}

/* 响应式 */
@media (max-width: 1200px) {
  .products-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 992px) {
  .quick-grid {
    grid-template-columns: 1fr;
  }
  
  .section-layout {
    grid-template-columns: 1fr;
  }
  
  .category-nav {
    display: none;
  }
  
  .market-content,
  .sd-layout,
  .news-layout {
    grid-template-columns: 1fr;
  }
  
  .hero-features {
    flex-wrap: wrap;
    gap: 16px;
  }
  
  .hero-features .feature-item {
    padding: 12px 16px;
  }
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 28px;
  }
  
  .quick-links {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .products-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .features-grid {
    grid-template-columns: 1fr;
  }
  
  .top-bar .container {
    flex-direction: column;
    gap: 8px;
  }
}
</style>
