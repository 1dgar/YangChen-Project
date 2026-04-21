<template>
  <div class="admin-products">
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon total">
          <el-icon><Goods /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">总产品数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon onsale">
          <el-icon><CircleCheck /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.onSale }}</div>
          <div class="stat-label">在售产品</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon offsale">
          <el-icon><CircleClose /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.offSale }}</div>
          <div class="stat-label">已下架</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon category">
          <el-icon><Grid /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.categories }}</div>
          <div class="stat-label">产品分类</div>
        </div>
      </div>
    </div>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <div class="header-title">
            <el-icon><Goods /></el-icon>
            <span>产品列表</span>
          </div>
          <div class="header-actions">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索产品名称"
              clearable
              style="width: 200px"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select v-model="filterCategory" placeholder="分类筛选" clearable style="width: 120px" @change="handleSearch">
              <el-option label="全部" value="" />
              <el-option label="蔬菜" value="蔬菜" />
              <el-option label="水果" value="水果" />
              <el-option label="粮油" value="粮油" />
            </el-select>
            <el-select v-model="filterStatus" placeholder="状态筛选" clearable style="width: 120px" @change="handleSearch">
              <el-option label="全部" value="" />
              <el-option label="上架" :value="1" />
              <el-option label="下架" :value="0" />
            </el-select>
          </div>
        </div>
      </template>

      <el-table :data="filteredProducts" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column label="产品信息" min-width="280">
          <template #default="{ row }">
            <div class="product-info">
              <div class="product-image-wrapper">
                <img
                  v-if="row.image && !row.imageError"
                  :src="row.image"
                  class="product-image"
                  @error="handleImageError($event, row)"
                  loading="lazy"
                  referrerpolicy="no-referrer"
                />
                <div v-else class="image-placeholder">
                  <el-icon><Picture /></el-icon>
                </div>
              </div>
              <div class="product-detail">
                <div class="product-name">{{ row.name }}</div>
                <div class="product-meta">
                  <el-tag size="small" type="info">{{ row.category }}</el-tag>
                  <span class="price">¥{{ row.price }}/{{ row.unit }}</span>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" align="center">
          <template #default="{ row }">
            <span :class="{ 'low-stock': row.stock < 100 }">
              {{ row.stock }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="发布人" width="120" align="center">
          <template #default="{ row }">
            <el-tag type="success" effect="light" round>
              <el-icon><User /></el-icon>
              {{ row.username || '未知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" effect="dark" round>
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="160" align="center">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              round
              @click="viewDetail(row)"
            >
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button
              :type="row.status === 1 ? 'warning' : 'success'"
              size="small"
              round
              @click="toggleStatus(row)"
            >
              <el-icon><CircleClose v-if="row.status === 1" /><CircleCheck v-else /></el-icon>
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="loadProducts"
          @size-change="loadProducts"
        />
      </div>
    </el-card>

    <el-dialog v-model="showDetail" title="产品详情" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="产品名称">{{ currentProduct.name }}</el-descriptions-item>
        <el-descriptions-item label="分类">{{ currentProduct.category }}</el-descriptions-item>
        <el-descriptions-item label="价格">¥{{ currentProduct.price }}/{{ currentProduct.unit }}</el-descriptions-item>
        <el-descriptions-item label="库存">{{ currentProduct.stock }}</el-descriptions-item>
        <el-descriptions-item label="描述">{{ currentProduct.description }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ currentProduct.contactName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentProduct.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="联系地址">{{ currentProduct.contactAddress }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Goods, CircleCheck, CircleClose, Grid, Search, Picture, User, View } from '@element-plus/icons-vue'
import { getAllProducts, updateProductStatus } from '../../api/admin'

const products = ref([])
const searchKeyword = ref('')
const filterCategory = ref('')
const filterStatus = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const showDetail = ref(false)
const currentProduct = ref({})

const stats = computed(() => {
  const onSale = products.value.filter(p => p.status === 1).length
  const offSale = products.value.filter(p => p.status === 0).length
  const categories = [...new Set(products.value.map(p => p.category))].length
  return {
    total: products.value.length,
    onSale,
    offSale,
    categories
  }
})

const filteredProducts = computed(() => {
  let result = products.value
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(p => p.name?.toLowerCase().includes(keyword))
  }
  if (filterCategory.value) {
    result = result.filter(p => p.category === filterCategory.value)
  }
  if (filterStatus.value !== '' && filterStatus.value !== null) {
    result = result.filter(p => p.status === filterStatus.value)
  }
  return result
})

onMounted(() => {
  loadProducts()
})

const loadProducts = async () => {
  const res = await getAllProducts({ page: currentPage.value, size: pageSize.value })
  products.value = res.data.records || res.data
  total.value = res.data.total || products.value.length
}

const handleSearch = () => {
  currentPage.value = 1
}

const viewDetail = (row) => {
  currentProduct.value = row
  showDetail.value = true
}

const toggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 0 ? '下架' : '上架'
  
  try {
    await ElMessageBox.confirm(`确定要${action}产品 "${row.name}" 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await updateProductStatus(row.id, { status: newStatus })
    ElMessage.success(`${action}成功`)
    loadProducts()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

const handleImageError = (e, row) => {
  e.target.style.display = 'none'
  row.imageError = true
}
</script>

<style scoped>
.admin-products {
  padding: 20px;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s, box-shadow 0.3s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
}

.stat-icon.total {
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.stat-icon.onsale {
  background: linear-gradient(135deg, #11998e, #38ef7d);
}

.stat-icon.offsale {
  background: linear-gradient(135deg, #f093fb, #f5576c);
}

.stat-icon.category {
  background: linear-gradient(135deg, #4facfe, #00f2fe);
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.table-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-image {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  flex-shrink: 0;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #c0c4cc;
  font-size: 24px;
}

.product-detail {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.product-name {
  font-weight: 500;
  color: #303133;
  font-size: 14px;
}

.product-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.price {
  color: #f56c6c;
  font-weight: 600;
  font-size: 14px;
}

.low-stock {
  color: #f56c6c;
  font-weight: 600;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
