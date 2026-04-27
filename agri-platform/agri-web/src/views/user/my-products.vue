<template>
  <div class="my-products-page">
    <div class="page-header">
      <div class="header-content">
        <h2>
          <el-icon><Goods /></el-icon>
          我的产品管理
        </h2>
        <p>管理您发布的所有农产品信息</p>
      </div>
      <el-button type="primary" size="large" @click="$router.push('/product/publish')" class="publish-btn">
        <el-icon><Plus /></el-icon>
        发布新产品
      </el-button>
    </div>

    <div class="stats-section">
      <div class="stat-card total">
        <div class="stat-icon">
          <el-icon :size="28"><Goods /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.total }}</div>
          <div class="stat-label">产品总数</div>
        </div>
      </div>
      <div class="stat-card online">
        <div class="stat-icon">
          <el-icon :size="28"><CircleCheck /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.online }}</div>
          <div class="stat-label">上架中</div>
        </div>
      </div>
      <div class="stat-card offline">
        <div class="stat-icon">
          <el-icon :size="28"><Remove /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.offline }}</div>
          <div class="stat-label">已下架</div>
        </div>
      </div>
      <div class="stat-card sales">
        <div class="stat-icon">
          <el-icon :size="28"><TrendCharts /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.totalSales }}</div>
          <div class="stat-label">总销量</div>
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
          <el-select v-model="filterStatus" placeholder="状态筛选" clearable style="width: 140px;" @change="handleSearch">
            <el-option label="全部状态" value="" />
            <el-option label="上架中" :value="1" />
            <el-option label="已下架" :value="0" />
          </el-select>
          <el-select v-model="filterCategory" placeholder="分类筛选" clearable style="width: 140px;" @change="handleSearch">
            <el-option label="全部分类" value="" />
            <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
          </el-select>
        </div>
        <div class="filter-right">
          <el-button-group>
            <el-button :type="viewMode === 'table' ? 'primary' : ''" @click="viewMode = 'table'">
              <el-icon><List /></el-icon>
            </el-button>
            <el-button :type="viewMode === 'grid' ? 'primary' : ''" @click="viewMode = 'grid'">
              <el-icon><Grid /></el-icon>
            </el-button>
          </el-button-group>
        </div>
      </div>

      <div v-if="viewMode === 'table'" class="table-wrapper">
        <el-table :data="filteredProducts" stripe style="width: 100%">
          <el-table-column label="产品信息" min-width="280">
            <template #default="{ row }">
              <div class="product-cell">
                <div class="product-image">
                  <img v-if="row.image" :src="row.image" :alt="row.name" class="product-thumb" />
                  <div v-else class="product-placeholder">
                    <el-icon><Picture /></el-icon>
                    <span>暂无图片</span>
                  </div>
                </div>
                <div class="product-info">
                  <h4>{{ row.name }}</h4>
                  <p class="category">{{ row.category }}</p>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="价格" width="150" align="center">
            <template #default="{ row }">
              <div class="price-cell">
                <span class="price">¥{{ row.price }}</span>
                <span class="unit">/{{ row.unit }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="stock" label="库存" width="100" align="center">
            <template #default="{ row }">
              <span :class="{ 'low-stock': row.stock < 50 }">{{ row.stock }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="sales" label="销量" width="100" align="center">
            <template #default="{ row }">
              {{ row.sales || 0 }}
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'" effect="light">
                {{ row.status === 1 ? '上架中' : '已下架' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="发布时间" width="160" align="center">
            <template #default="{ row }">
              {{ formatDate(row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" align="center" fixed="right">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button type="primary" link size="small" @click="editProduct(row)">
                  <el-icon><Edit /></el-icon>
                  编辑
                </el-button>
                <el-button 
                  :type="row.status === 1 ? 'warning' : 'success'" 
                  link 
                  size="small" 
                  @click="toggleStatus(row)"
                >
                  <el-icon><component :is="row.status === 1 ? 'Remove' : 'CircleCheck'" /></el-icon>
                  {{ row.status === 1 ? '下架' : '上架' }}
                </el-button>
                <el-button type="danger" link size="small" @click="deleteProduct(row.id)">
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div v-else class="grid-wrapper">
        <div class="product-grid">
          <div class="product-card" v-for="product in filteredProducts" :key="product.id">
            <div class="card-image">
              <img v-if="product.image" :src="product.image" :alt="product.name" class="card-photo" />
              <div v-else class="card-placeholder">
                <el-icon><Picture /></el-icon>
                <span>暂无图片</span>
              </div>
              <div class="card-overlay">
                <el-button type="primary" circle size="small" @click="editProduct(product)">
                  <el-icon><Edit /></el-icon>
                </el-button>
              </div>
              <div class="status-badge" :class="product.status === 1 ? 'online' : 'offline'">
                {{ product.status === 1 ? '上架中' : '已下架' }}
              </div>
            </div>
            <div class="card-content">
              <h4>{{ product.name }}</h4>
              <p class="category">{{ product.category }}</p>
              <div class="price-row">
                <span class="price">¥{{ product.price }}</span>
                <span class="unit">/{{ product.unit }}</span>
              </div>
              <div class="stats-row">
                <span><el-icon><Box /></el-icon> 库存: {{ product.stock }}</span>
                <span><el-icon><TrendCharts /></el-icon> 销量: {{ product.sales || 0 }}</span>
              </div>
            </div>
            <div class="card-footer">
              <el-button type="primary" size="small" @click="editProduct(product)">编辑</el-button>
              <el-button 
                :type="product.status === 1 ? 'warning' : 'success'" 
                size="small"
                @click="toggleStatus(product)"
              >
                {{ product.status === 1 ? '下架' : '上架' }}
              </el-button>
              <el-button type="danger" size="small" @click="deleteProduct(product.id)">删除</el-button>
            </div>
          </div>
        </div>
      </div>

      <div v-if="filteredProducts.length === 0" class="empty-state">
        <el-icon :size="64" color="#ccc"><Goods /></el-icon>
        <p>暂无产品数据</p>
        <el-button type="primary" @click="$router.push('/product/publish')">发布第一个产品</el-button>
      </div>

      <div class="pagination-wrapper" v-if="filteredProducts.length > 0">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="loadProducts"
          @size-change="handleSizeChange"
        />
      </div>
    </div>

    <el-dialog v-model="editDialogVisible" title="编辑产品" width="600px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="产品名称">
          <el-input v-model="editForm.name" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="editForm.category" style="width: 100%;">
            <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="editForm.price" :min="0" :precision="2" style="width: 200px;" />
          <span style="margin-left: 10px;">元/</span>
          <el-input v-model="editForm.unit" style="width: 80px; margin-left: 10px;" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="editForm.stock" :min="0" />
        </el-form-item>
        <el-form-item label="产品描述">
          <el-input v-model="editForm.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="产品图片">
          <div class="edit-image-panel">
            <el-upload
              class="edit-image-uploader"
              :show-file-list="false"
              :http-request="handleEditImageUpload"
              :before-upload="beforeImageUpload"
              :disabled="imageUploading"
              accept="image/*"
            >
              <img v-if="editForm.image" :src="editForm.image" :alt="editForm.name || '产品图片'" class="edit-preview-image" />
              <div v-else class="edit-image-placeholder">
                <el-icon><Plus /></el-icon>
                <span>{{ imageUploading ? '上传中...' : '上传产品图片' }}</span>
                <small>支持 jpg/png/webp，最大 5MB</small>
              </div>
            </el-upload>
            <div class="edit-image-actions">
              <el-button v-if="editForm.image" size="small" @click="editForm.image = ''">移除图片</el-button>
              <span v-else class="edit-image-tip">未上传时将显示“暂无图片”</span>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyProducts, deleteProduct as deleteProductApi, updateProduct } from '../../api/product'
import { uploadImage } from '../../api/file'
import { 
  Goods, Plus, CircleCheck, Remove, TrendCharts, Search, 
  List, Grid, Edit, Delete, Box, Picture
} from '@element-plus/icons-vue'

const router = useRouter()
const userId = localStorage.getItem('userId')
const products = ref([])
const categories = ref(['蔬菜', '水果', '粮油', '畜牧', '水产品'])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const filterStatus = ref('')
const filterCategory = ref('')
const viewMode = ref('table')
const editDialogVisible = ref(false)
const editForm = ref({})
const imageUploading = ref(false)

const statistics = computed(() => {
  const total = products.value.length
  const online = products.value.filter(p => p.status === 1).length
  const offline = products.value.filter(p => p.status === 0).length
  const totalSales = products.value.reduce((sum, p) => sum + (p.sales || 0), 0)
  return { total, online, offline, totalSales }
})

const filteredProducts = computed(() => {
  let result = products.value
  
  if (searchKeyword.value) {
    result = result.filter(p => p.name.includes(searchKeyword.value))
  }
  
  if (filterStatus.value !== '') {
    result = result.filter(p => p.status === filterStatus.value)
  }
  
  if (filterCategory.value) {
    result = result.filter(p => p.category === filterCategory.value)
  }
  
  return result
})

onMounted(() => {
  loadProducts()
})

const loadProducts = async () => {
  try {
    const res = await getMyProducts(userId, { page: page.value, size: size.value })
    products.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('加载产品失败:', error)
    products.value = []
  }
}

const handleSizeChange = (newSize) => {
  size.value = newSize
  page.value = 1
  loadProducts()
}

const handleSearch = () => {
  page.value = 1
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', { 
    year: 'numeric', 
    month: '2-digit', 
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const editProduct = (product) => {
  editForm.value = { ...product }
  editDialogVisible.value = true
}

const beforeImageUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB')
    return false
  }
  return true
}

const handleEditImageUpload = async ({ file }) => {
  imageUploading.value = true
  try {
    const res = await uploadImage(file)
    editForm.value.image = res.data
    ElMessage.success('图片上传成功')
  } catch (error) {
    ElMessage.error(error.message || '图片上传失败')
  } finally {
    imageUploading.value = false
  }
}

const saveEdit = async () => {
  try {
    await updateProduct(editForm.value.id, editForm.value)
    ElMessage.success('保存成功')
    editDialogVisible.value = false
    loadProducts()
  } catch (error) {
    ElMessage.error('保存失败：' + (error.message || '未知错误'))
  }
}

const toggleStatus = async (product) => {
  const newStatus = product.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '上架' : '下架'
  
  try {
    await ElMessageBox.confirm(`确定要${action}该产品吗？`, '提示', { type: 'warning' })
    await updateProduct(product.id, { status: newStatus })
    ElMessage.success(`${action}成功`)
    loadProducts()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

const deleteProduct = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该产品吗？删除后无法恢复', '提示', { type: 'warning' })
    await deleteProductApi(id)
    ElMessage.success('删除成功')
    loadProducts()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}
</script>

<style scoped>
.my-products-page {
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

.publish-btn {
  background: linear-gradient(135deg, #4CAF50, #45a049);
  border: none;
  padding: 12px 24px;
  font-size: 15px;
}

.publish-btn:hover {
  background: linear-gradient(135deg, #45a049, #388e3c);
}

.stats-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
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

.stat-card.online .stat-icon {
  background: linear-gradient(135deg, #2196F3, #1976D2);
}

.stat-card.offline .stat-icon {
  background: linear-gradient(135deg, #9E9E9E, #757575);
}

.stat-card.sales .stat-icon {
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

.table-wrapper {
  margin-bottom: 20px;
}

.product-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-image {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  background: #f6f8f6;
}

.product-thumb {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
  color: #98a29a;
  font-size: 10px;
}

.product-placeholder .el-icon {
  font-size: 16px;
}

.product-info h4 {
  margin: 0 0 4px 0;
  font-size: 15px;
  color: #333;
}

.product-info .category {
  margin: 0;
  font-size: 13px;
  color: #999;
}

.price-cell .price {
  font-size: 18px;
  font-weight: 600;
  color: #f56c6c;
}

.price-cell .unit {
  font-size: 13px;
  color: #999;
}

.low-stock {
  color: #f56c6c;
  font-weight: 600;
}

.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.grid-wrapper {
  margin-bottom: 20px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.product-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #f0f0f0;
  transition: transform 0.3s, box-shadow 0.3s;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.card-image {
  position: relative;
  height: 160px;
  overflow: hidden;
  background: #f6f8f6;
}

.card-photo {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.card-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #98a29a;
}

.card-placeholder .el-icon {
  font-size: 28px;
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

.product-card:hover .card-overlay {
  opacity: 1;
}

.status-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.status-badge.online {
  background: #4CAF50;
  color: white;
}

.status-badge.offline {
  background: #9E9E9E;
  color: white;
}

.card-content {
  padding: 16px;
}

.card-content h4 {
  margin: 0 0 8px 0;
  font-size: 15px;
  color: #333;
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

.stats-row {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: #999;
}

.stats-row span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-footer {
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  gap: 8px;
}

.card-footer .el-button {
  flex: 1;
}

.edit-image-panel {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.edit-image-uploader {
  width: 100%;
}

.edit-image-uploader :deep(.el-upload) {
  width: 100%;
  border: 1px dashed #c8d6c9;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: border-color 0.3s;
}

.edit-image-uploader :deep(.el-upload:hover) {
  border-color: #4CAF50;
}

.edit-preview-image {
  display: block;
  width: 100%;
  height: 220px;
  object-fit: cover;
  background: #f6f8f6;
}

.edit-image-placeholder {
  height: 220px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #6b7280;
  background: linear-gradient(180deg, #fafcf9 0%, #f1f6f0 100%);
}

.edit-image-placeholder .el-icon {
  font-size: 28px;
  color: #4CAF50;
}

.edit-image-placeholder small {
  color: #9aa29c;
}

.edit-image-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.edit-image-tip {
  font-size: 13px;
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
}

@media (max-width: 1200px) {
  .stats-section {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .product-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .stats-section {
    grid-template-columns: 1fr;
  }
  
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
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
