<template>
  <div class="favorites-page">
    <div class="page-header">
      <div class="header-content">
        <h2>
          <el-icon><Star /></el-icon>
          我的收藏
        </h2>
        <p>您收藏的优质农产品</p>
      </div>
      <el-button 
        v-if="selectedItems.length > 0" 
        type="danger" 
        size="large" 
        @click="batchDelete"
      >
        <el-icon><Delete /></el-icon>
        批量取消收藏 ({{ selectedItems.length }})
      </el-button>
    </div>

    <div class="stats-section">
      <div class="stat-card total">
        <div class="stat-icon">
          <el-icon :size="28"><Star /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.total }}</div>
          <div class="stat-label">收藏总数</div>
        </div>
      </div>
      <div class="stat-card categories">
        <div class="stat-icon">
          <el-icon :size="28"><Grid /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.categories }}</div>
          <div class="stat-label">涉及分类</div>
        </div>
      </div>
      <div class="stat-card recent">
        <div class="stat-icon">
          <el-icon :size="28"><Timer /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.recent }}</div>
          <div class="stat-label">近7天新增</div>
        </div>
      </div>
    </div>

    <div class="content-card">
      <div class="filter-bar">
        <div class="filter-left">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索产品名称"
            clearable
            style="width: 240px;"
            @input="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-select v-model="filterCategory" placeholder="分类筛选" clearable style="width: 140px;" @change="handleSearch">
            <el-option label="全部分类" value="" />
            <el-option v-for="cat in categoryList" :key="cat" :label="cat" :value="cat" />
          </el-select>
        </div>
        <div class="filter-right">
          <el-checkbox 
            v-model="selectAll" 
            @change="handleSelectAll"
            :indeterminate="selectedItems.length > 0 && selectedItems.length < filteredFavorites.length"
          >
            全选
          </el-checkbox>
        </div>
      </div>

      <div class="favorites-grid" v-if="filteredFavorites.length > 0">
        <div 
          class="favorite-card" 
          v-for="item in filteredFavorites" 
          :key="item.id"
          :class="{ selected: selectedItems.includes(item.id) }"
        >
          <div class="card-checkbox">
            <el-checkbox 
              :model-value="selectedItems.includes(item.id)"
              @change="(val) => handleSelect(item.id, val)"
            />
          </div>
          <div class="card-image" @click="goToProduct(item.productId)" :style="{ backgroundColor: getCategoryBgColor(item.product?.category) }">
            <div class="card-icon">
              <el-icon :size="36" :color="getCategoryColor(item.product?.category)">
                <component :is="getCategoryIconComponent(item.product?.category)" />
              </el-icon>
            </div>
            <div class="card-name">{{ item.product?.name || '未知产品' }}</div>
            <div class="card-overlay">
              <el-button type="primary" circle>
                <el-icon><View /></el-icon>
              </el-button>
            </div>
          </div>
          <div class="card-content" @click="goToProduct(item.productId)">
            <h4>{{ item.product?.name || '未知产品' }}</h4>
            <p class="category">{{ item.product?.category || '未分类' }}</p>
            <div class="price-row">
              <span class="price">¥{{ item.product?.price || 0 }}</span>
              <span class="unit">/{{ item.product?.unit || '斤' }}</span>
            </div>
            <div class="meta-row">
              <span class="seller">
                <el-icon><User /></el-icon>
                {{ item.product?.username || '农户' }}
              </span>
              <span class="stock" :class="{ low: item.product?.stock < 50 }">
                库存: {{ item.product?.stock || 0 }}
              </span>
            </div>
          </div>
          <div class="card-footer">
            <span class="collect-time">
              <el-icon><Timer /></el-icon>
              {{ formatDate(item.createTime) }}
            </span>
            <el-button type="danger" link size="small" @click="removeFavorite(item.productId)">
              <el-icon><Delete /></el-icon>
              取消收藏
            </el-button>
          </div>
        </div>
      </div>

      <div v-else class="empty-state">
        <el-icon :size="64" color="#ccc"><Star /></el-icon>
        <p>暂无收藏产品</p>
        <el-button type="primary" @click="$router.push('/product')">去浏览产品</el-button>
      </div>

      <div class="pagination-wrapper" v-if="filteredFavorites.length > 0">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          :page-sizes="[12, 24, 36, 48]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="loadFavorites"
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
import { getMyFavorites, removeFavorite as removeFavoriteApi } from '../../api/favorite'
import { getCategoryIcon, getCategoryColor, getCategoryBgColor } from '../../utils/imageMap'
import { Star, Delete, Grid, Timer, Search, View, User, Food, Apple, Orange, Bowl, IceCream, Dessert } from '@element-plus/icons-vue'

const router = useRouter()
const userId = localStorage.getItem('userId')
const favorites = ref([])
const page = ref(1)
const size = ref(12)
const total = ref(0)
const searchKeyword = ref('')
const filterCategory = ref('')
const selectedItems = ref([])
const selectAll = ref(false)

const categoryList = computed(() => {
  const cats = new Set()
  favorites.value.forEach(item => {
    if (item.product?.category) {
      cats.add(item.product.category)
    }
  })
  return Array.from(cats)
})

const statistics = computed(() => {
  const total = favorites.value.length
  const categories = categoryList.value.length
  
  const weekAgo = new Date()
  weekAgo.setDate(weekAgo.getDate() - 7)
  const recent = favorites.value.filter(item => {
    if (!item.createTime) return false
    return new Date(item.createTime) > weekAgo
  }).length
  
  return { total, categories, recent }
})

const filteredFavorites = computed(() => {
  let result = favorites.value
  
  if (searchKeyword.value) {
    result = result.filter(item => 
      item.product?.name?.includes(searchKeyword.value)
    )
  }
  
  if (filterCategory.value) {
    result = result.filter(item => 
      item.product?.category === filterCategory.value
    )
  }
  
  return result
})

onMounted(() => {
  loadFavorites()
})

const loadFavorites = async () => {
  try {
    const res = await getMyFavorites(userId, { page: page.value, size: size.value })
    favorites.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('加载收藏失败:', error)
    favorites.value = []
  }
}

const handleSizeChange = (newSize) => {
  size.value = newSize
  page.value = 1
  loadFavorites()
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
  
  if (diff < 86400000) {
    return '今天'
  } else if (diff < 604800000) {
    return Math.floor(diff / 86400000) + '天前'
  } else {
    return date.toLocaleDateString('zh-CN')
  }
}

const goToProduct = (id) => {
  router.push(`/product/${id}`)
}

const handleSelectAll = (val) => {
  if (val) {
    selectedItems.value = filteredFavorites.value.map(item => item.id)
  } else {
    selectedItems.value = []
  }
}

const handleSelect = (id, val) => {
  if (val) {
    if (!selectedItems.value.includes(id)) {
      selectedItems.value.push(id)
    }
  } else {
    selectedItems.value = selectedItems.value.filter(item => item !== id)
  }
  selectAll.value = selectedItems.value.length === filteredFavorites.value.length
}

const removeFavorite = async (id) => {
  try {
    await ElMessageBox.confirm('确定取消收藏该产品吗？', '提示', { type: 'warning' })
    await removeFavoriteApi(id)
    ElMessage.success('已取消收藏')
    loadFavorites()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const batchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定取消收藏选中的 ${selectedItems.value.length} 个产品吗？`, '提示', { type: 'warning' })
    
    for (const favoriteId of selectedItems.value) {
      const favorite = favorites.value.find(item => item.id === favoriteId)
      if (favorite?.productId) {
        await removeFavoriteApi(favorite.productId)
      }
    }
    
    ElMessage.success('批量取消收藏成功')
    selectedItems.value = []
    selectAll.value = false
    loadFavorites()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}
</script>

<style scoped>
.favorites-page {
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
  background: linear-gradient(135deg, #FFB300, #FFA000);
}

.stat-card.categories .stat-icon {
  background: linear-gradient(135deg, #4CAF50, #45a049);
}

.stat-card.recent .stat-icon {
  background: linear-gradient(135deg, #2196F3, #1976D2);
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

.favorites-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.favorite-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  border: 2px solid #f0f0f0;
  transition: all 0.3s;
  position: relative;
}

.favorite-card:hover {
  border-color: #4CAF50;
  box-shadow: 0 8px 24px rgba(76, 175, 80, 0.15);
}

.favorite-card.selected {
  border-color: #4CAF50;
  background: rgba(76, 175, 80, 0.02);
}

.card-checkbox {
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 10;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 4px;
  padding: 4px;
}

.card-image {
  position: relative;
  height: 180px;
  overflow: hidden;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.card-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 10px;
}

.card-name {
  background: rgba(255, 255, 255, 0.95);
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 13px;
  font-weight: 600;
  color: #333;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.card-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.favorite-card:hover .card-overlay {
  opacity: 1;
}

.card-content {
  padding: 16px;
  cursor: pointer;
}

.card-content h4 {
  margin: 0 0 8px 0;
  font-size: 15px;
  color: #333;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-content .category {
  margin: 0 0 12px 0;
  font-size: 13px;
  color: #999;
}

.price-row {
  margin-bottom: 12px;
}

.price-row .price {
  font-size: 20px;
  font-weight: 700;
  color: #f56c6c;
}

.price-row .unit {
  font-size: 13px;
  color: #999;
}

.meta-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: #999;
}

.meta-row .seller {
  display: flex;
  align-items: center;
  gap: 4px;
}

.meta-row .stock.low {
  color: #f56c6c;
  font-weight: 600;
}

.card-footer {
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.collect-time {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #999;
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

@media (max-width: 1200px) {
  .favorites-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 992px) {
  .stats-section {
    grid-template-columns: 1fr;
  }
  
  .favorites-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .favorites-grid {
    grid-template-columns: 1fr;
  }
  
  .filter-bar {
    flex-direction: column;
    gap: 12px;
  }
  
  .filter-left {
    flex-wrap: wrap;
  }
}
</style>
