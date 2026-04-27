<template>
  <div class="product-list-page">
    <div class="page-header">
      <div class="header-bg"></div>
      <div class="container">
        <div class="breadcrumb">
          <router-link to="/">首页</router-link>
          <el-icon><ArrowRight /></el-icon>
          <span>农产品信息</span>
        </div>
        <h2>优质农产品</h2>
        <p>产地直供 · 品质保证 · 价格透明</p>
        <div class="header-stats">
          <div class="stat-item">
            <el-icon><Goods /></el-icon>
            <span>{{ total }}+ 件产品</span>
          </div>
          <div class="stat-item">
            <el-icon><User /></el-icon>
            <span>{{ farmerCount }}+ 认证农户</span>
          </div>
          <div class="stat-item">
            <el-icon><Location /></el-icon>
            <span>{{ categoryCount }}+ 产品分类</span>
          </div>
        </div>
      </div>
    </div>

    <div class="container main-container">
      <aside class="filter-sidebar">
        <div class="filter-card">
          <h3>
            <el-icon><Menu /></el-icon>
            产品分类
          </h3>
          <ul class="category-list">
            <li :class="{ active: !searchForm.category }" @click="selectCategory('')">
              <div class="cat-icon all">
                <el-icon><Grid /></el-icon>
              </div>
              <span>全部产品</span>
              <el-icon class="arrow"><ArrowRight /></el-icon>
            </li>
            <li v-for="cat in categories" :key="cat" :class="{ active: searchForm.category === cat }" @click="selectCategory(cat)">
              <div class="cat-icon" :style="{ background: getCategoryColor(cat) }">
                <el-icon><component :is="getCategoryIcon(cat)" /></el-icon>
              </div>
              <span>{{ cat }}</span>
              <el-icon class="arrow"><ArrowRight /></el-icon>
            </li>
          </ul>
        </div>

        <div class="filter-card">
          <h3>
            <el-icon><Money /></el-icon>
            价格区间
          </h3>
          <div class="price-range">
            <el-input v-model="priceMin" placeholder="最低价" size="large">
              <template #prefix>¥</template>
            </el-input>
            <span class="separator">-</span>
            <el-input v-model="priceMax" placeholder="最高价" size="large">
              <template #prefix>¥</template>
            </el-input>
          </div>
          <el-button type="primary" size="large" @click="handleSearch" class="filter-btn">
            <el-icon><Search /></el-icon>
            筛选
          </el-button>
        </div>

        <div class="filter-card hot-tags">
          <h3>
            <el-icon><HotWater /></el-icon>
            热门标签
          </h3>
          <div class="tag-list">
            <span class="tag" v-for="tag in hotTags" :key="tag" @click="searchForm.keyword = tag; handleSearch()">
              {{ tag }}
            </span>
          </div>
        </div>
      </aside>

      <div class="content-area">
        <div class="search-section">
          <div class="search-bar">
            <el-input
              v-model="searchForm.keyword"
              placeholder="搜索农产品名称、产地、品种..."
              clearable
              size="large"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" size="large" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
          </div>
          <el-button 
            v-if="role === 'farmer'" 
            type="success" 
            size="large" 
            @click="$router.push('/product/publish')"
            class="publish-btn"
          >
            <el-icon><Plus /></el-icon>
            发布产品
          </el-button>
        </div>

        <div class="toolbar">
          <div class="sort-bar">
            <span class="sort-label">排序：</span>
            <a :class="{ active: sortBy === 'newest' }" @click="setSort('newest')">
              <el-icon><Timer /></el-icon>
              最新发布
            </a>
            <a :class="{ active: sortBy === 'price_asc' }" @click="setSort('price_asc')">
              <el-icon><SortUp /></el-icon>
              价格从低到高
            </a>
            <a :class="{ active: sortBy === 'price_desc' }" @click="setSort('price_desc')">
              <el-icon><SortDown /></el-icon>
              价格从高到低
            </a>
            <a :class="{ active: sortBy === 'sales' }" @click="setSort('sales')">
              <el-icon><TrendCharts /></el-icon>
              销量优先
            </a>
          </div>
          <span class="total-info">共 <strong>{{ total }}</strong> 件产品</span>
        </div>

        <div class="product-grid" v-if="products.length > 0">
          <div 
            class="product-card" 
            v-for="product in products" 
            :key="product.id"
            @click="goToDetail(product.id)"
          >
            <div class="product-main">
              <span class="product-tag" v-if="product.stock < 50">库存紧张</span>
              <span class="product-tag new" v-if="isNewProduct(product.createTime)">新品</span>
              <h4 class="product-name">{{ product.name }}</h4>
              <p class="product-category">{{ product.category }}</p>
              <div class="product-price">
                <span class="price-num">¥{{ product.price }}</span>
                <span class="price-unit">/{{ product.unit }}</span>
              </div>
            </div>
            <div class="product-meta">
              <span class="meta-item">
                <el-icon><Location /></el-icon>
                {{ product.contactAddress || '产地直供' }}
              </span>
              <span class="meta-item seller">
                <el-icon><User /></el-icon>
                {{ product.username || '农户' }}
              </span>
            </div>
          </div>
        </div>

        <el-empty v-else description="暂无相关产品" class="empty-state">
          <template #image>
            <el-icon :size="80" color="#ccc"><Goods /></el-icon>
          </template>
          <el-button type="primary" @click="resetSearch">清除筛选</el-button>
        </el-empty>

        <div class="pagination-wrapper" v-if="total > 0">
          <el-pagination
            v-model:current-page="page"
            v-model:page-size="size"
            :total="total"
            :page-sizes="[12, 24, 36, 48]"
            layout="total, sizes, prev, pager, next, jumper"
            @current-change="loadProducts"
            @size-change="handleSizeChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getProductList } from '../../api/product'
import { getCategories } from '../../api/market'
import { getCategoryIcon, getCategoryColor } from '../../utils/imageMap'
import { 
  ArrowRight, Search, Plus, User, Goods, Location, 
  Menu, Grid, Money, HotWater, Timer, SortUp, SortDown, 
  TrendCharts, Box, Document
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const role = ref(localStorage.getItem('role'))
const products = ref([])
const categories = ref([])
const page = ref(1)
const size = ref(12)
const total = ref(0)
const farmerCount = ref(0)
const categoryCount = ref(0)
const sortBy = ref('newest')
const priceMin = ref('')
const priceMax = ref('')
const tooltipProduct = ref(null)
const searchForm = ref({
  category: route.query.category || '',
  keyword: route.query.keyword || ''
})

const hotTags = ['有机蔬菜', '新鲜水果', '土鸡蛋', '农家大米', '绿色认证', '产地直发']

watch(() => route.query, (newQuery) => {
  if (newQuery.category !== undefined) {
    searchForm.value.category = newQuery.category || ''
  }
  if (newQuery.keyword !== undefined) {
    searchForm.value.keyword = newQuery.keyword || ''
  }
  page.value = 1
  loadProducts()
}, { immediate: false })

onMounted(async () => {
  loadProducts()
  const res = await getCategories()
  categories.value = res.data
})

const loadProducts = async () => {
  const res = await getProductList({
    page: page.value,
    size: size.value,
    category: searchForm.value.category,
    keyword: searchForm.value.keyword
  })
  products.value = res.data.records
  total.value = res.data.total
  
  applySort()
  
  const uniqueUsers = new Set(products.value.map(p => p.userId))
  farmerCount.value = uniqueUsers.size || Math.ceil(total.value / 10)
  
  const uniqueCategories = new Set(products.value.map(p => p.category))
  categoryCount.value = uniqueCategories.size || categories.value.length || 5
}

const applySort = () => {
  if (!products.value || products.value.length === 0) return
  
  const sortedProducts = [...products.value]
  
  switch (sortBy.value) {
    case 'price_asc':
      sortedProducts.sort((a, b) => parseFloat(a.price) - parseFloat(b.price))
      break
    case 'price_desc':
      sortedProducts.sort((a, b) => parseFloat(b.price) - parseFloat(a.price))
      break
    case 'sales':
      sortedProducts.sort((a, b) => (b.sales || 0) - (a.sales || 0))
      break
    case 'newest':
    default:
      sortedProducts.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
      break
  }
  
  products.value = sortedProducts
}

const handleSearch = () => {
  page.value = 1
  loadProducts()
}

const handleSizeChange = (newSize) => {
  size.value = newSize
  page.value = 1
  loadProducts()
}

const selectCategory = (cat) => {
  searchForm.value.category = cat
  page.value = 1
  loadProducts()
}

const setSort = (sort) => {
  sortBy.value = sort
  applySort()
}

const resetSearch = () => {
  searchForm.value = { category: '', keyword: '' }
  priceMin.value = ''
  priceMax.value = ''
  page.value = 1
  loadProducts()
}

const goToDetail = (id) => {
  router.push(`/product/${id}`)
}

const isNewProduct = (createTime) => {
  if (!createTime) return false
  const create = new Date(createTime)
  const now = new Date()
  const diffDays = Math.floor((now - create) / (1000 * 60 * 60 * 24))
  return diffDays <= 7
}

const showTooltip = (product) => {
  tooltipProduct.value = product
}

const hideTooltip = () => {
  tooltipProduct.value = null
}
</script>

<style scoped>
.product-list-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%);
}

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
  background: linear-gradient(135deg, rgba(22, 163, 74, 0.95) 0%, rgba(34, 197, 94, 0.9) 50%, rgba(74, 222, 128, 0.85) 100%);
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
  background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.06'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
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
  transition: color 0.3s;
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
  backdrop-filter: blur(4px);
  transition: all 0.3s;
}

.header-stats .stat-item:hover {
  background: rgba(255,255,255,0.25);
  transform: translateY(-2px);
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
}

.main-container {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 24px;
  padding: 32px 24px;
}

.filter-sidebar {
  position: sticky;
  top: 90px;
  height: fit-content;
}

.filter-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  transition: all 0.3s;
}

.filter-card:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
}

.filter-card h3 {
  margin: 0 0 20px 0;
  font-size: 17px;
  color: #1f2937;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-card h3 .el-icon {
  color: #22c55e;
}

.category-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.category-list li {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 12px;
  cursor: pointer;
  color: #6b7280;
  border-radius: 10px;
  transition: all 0.3s;
  margin-bottom: 4px;
}

.category-list li:hover {
  background: #f0fdf4;
  color: #22c55e;
}

.category-list li.active {
  background: linear-gradient(135deg, #dcfce7, #bbf7d0);
  color: #16a34a;
  font-weight: 600;
}

.cat-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
}

.cat-icon.all {
  background: linear-gradient(135deg, #6b7280, #4b5563);
}

.category-list li span {
  flex: 1;
}

.category-list li .arrow {
  color: #d1d5db;
  transition: all 0.3s;
}

.category-list li:hover .arrow,
.category-list li.active .arrow {
  color: #22c55e;
  transform: translateX(4px);
}

.price-range {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.price-range .separator {
  color: #9ca3af;
  font-weight: 500;
}

.filter-btn {
  width: 100%;
  background: linear-gradient(135deg, #22c55e, #16a34a);
  border: none;
  height: 44px;
  font-weight: 500;
}

.filter-btn:hover {
  background: linear-gradient(135deg, #16a34a, #15803d);
}

.hot-tags .tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.hot-tags .tag {
  background: #f3f4f6;
  color: #6b7280;
  padding: 8px 14px;
  border-radius: 20px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
}

.hot-tags .tag:hover {
  background: linear-gradient(135deg, #22c55e, #16a34a);
  color: #fff;
  transform: translateY(-2px);
}

.content-area {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.search-section {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}

.search-bar {
  display: flex;
  gap: 12px;
  flex: 1;
}

.search-bar .el-input {
  flex: 1;
}

.publish-btn {
  background: linear-gradient(135deg, #22c55e, #16a34a);
  border: none;
  font-weight: 500;
}

.publish-btn:hover {
  background: linear-gradient(135deg, #16a34a, #15803d);
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f3f4f6;
  margin-bottom: 24px;
}

.sort-bar {
  display: flex;
  align-items: center;
  gap: 8px;
}

.sort-label {
  color: #6b7280;
  font-size: 14px;
  margin-right: 8px;
}

.sort-bar a {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #6b7280;
  text-decoration: none;
  cursor: pointer;
  font-size: 14px;
  padding: 8px 14px;
  border-radius: 8px;
  transition: all 0.3s;
}

.sort-bar a:hover {
  background: #f3f4f6;
  color: #22c55e;
}

.sort-bar a.active {
  background: linear-gradient(135deg, #dcfce7, #bbf7d0);
  color: #16a34a;
  font-weight: 500;
}

.total-info {
  color: #9ca3af;
  font-size: 14px;
}

.total-info strong {
  color: #22c55e;
  font-size: 18px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

/* 简洁产品卡片 */
.product-card {
  background: #fff;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid #e8e8e8;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-height: 140px;
}

.product-card:hover {
  border-color: #4CAF50;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

.product-main {
  flex: 1;
  padding: 16px 16px 0;
}

.product-tag {
  display: inline-block;
  background: #fff7e6;
  color: #fa8c16;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  margin-bottom: 8px;
  margin-right: 6px;
}

.product-tag.new {
  background: #f6ffed;
  color: #52c41a;
}

.product-name {
  font-size: 15px;
  font-weight: 500;
  color: #333;
  margin: 0 0 4px 0;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-category {
  font-size: 13px;
  color: #999;
  margin: 0 0 12px 0;
}

.product-price {
  display: flex;
  align-items: baseline;
  gap: 2px;
}

.price-num {
  color: #f5222d;
  font-size: 20px;
  font-weight: 600;
}

.price-unit {
  color: #999;
  font-size: 13px;
}

.product-meta {
  display: flex;
  gap: 16px;
  padding: 12px 16px 16px;
  margin-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #666;
}

.meta-item .el-icon {
  font-size: 13px;
  color: #bfbfbf;
}

.meta-item.seller {
  margin-left: auto;
}

.tooltip-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f3f4f6;
}

.tooltip-title {
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
}

.tooltip-category {
  background: #f3f4f6;
  color: #6b7280;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.tooltip-content {
  margin-bottom: 12px;
}

.tooltip-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 13px;
  color: #6b7280;
}

.tooltip-item .el-icon {
  color: #22c55e;
  margin-top: 2px;
}

.tooltip-item strong {
  color: #1f2937;
}

.tooltip-desc {
  line-height: 1.5;
}

.tooltip-action {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding-top: 12px;
  border-top: 1px solid #f3f4f6;
  font-size: 13px;
  color: #22c55e;
  font-weight: 500;
}

.empty-state {
  padding: 60px 0;
}

.pagination-wrapper {
  margin-top: 40px;
  padding-top: 24px;
  border-top: 1px solid #f3f4f6;
  display: flex;
  justify-content: center;
}

@media (max-width: 1400px) {
  .hover-tooltip {
    left: auto;
    right: 105%;
  }
}

@media (max-width: 1200px) {
  .main-container {
    grid-template-columns: 1fr;
  }
  
  .filter-sidebar {
    position: static;
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;
  }
  
  .filter-card {
    margin-bottom: 0;
  }
  
  .hover-tooltip {
    display: none;
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
  
  .filter-sidebar {
    grid-template-columns: 1fr;
  }
  
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
  
  .card-visual {
    height: 100px;
  }
  
  .visual-icon {
    width: 60px;
    height: 60px;
  }
  
  .card-body {
    padding: 14px;
  }
  
  .product-title {
    font-size: 14px;
    min-height: 36px;
  }
  
  .price {
    font-size: 18px;
  }
  
  .toolbar {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .search-section {
    flex-direction: column;
  }
}
</style>
