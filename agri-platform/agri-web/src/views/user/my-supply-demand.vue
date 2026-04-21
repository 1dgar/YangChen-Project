<template>
  <div class="my-supply-demand-page">
    <div class="page-header">
      <div class="header-content">
        <h2>
          <el-icon><Document /></el-icon>
          我的供需信息
        </h2>
        <p>管理您发布的供应和求购信息</p>
      </div>
      <el-button type="primary" size="large" @click="$router.push('/supply-demand/publish')" class="publish-btn">
        <el-icon><Plus /></el-icon>
        发布新信息
      </el-button>
    </div>

    <div class="stats-section">
      <div class="stat-card supply">
        <div class="stat-icon">
          <el-icon :size="28"><Box /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.supply }}</div>
          <div class="stat-label">供应信息</div>
        </div>
      </div>
      <div class="stat-card demand">
        <div class="stat-icon">
          <el-icon :size="28"><ShoppingCart /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.demand }}</div>
          <div class="stat-label">求购信息</div>
        </div>
      </div>
      <div class="stat-card active">
        <div class="stat-icon">
          <el-icon :size="28"><CircleCheck /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.active }}</div>
          <div class="stat-label">进行中</div>
        </div>
      </div>
      <div class="stat-card ended">
        <div class="stat-icon">
          <el-icon :size="28"><Remove /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.ended }}</div>
          <div class="stat-label">已结束</div>
        </div>
      </div>
    </div>

    <div class="content-card">
      <div class="filter-bar">
        <div class="filter-left">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索标题"
            clearable
            style="width: 240px;"
            @input="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-select v-model="filterType" placeholder="类型筛选" clearable style="width: 140px;" @change="handleSearch">
            <el-option label="全部类型" value="" />
            <el-option label="供应" value="supply" />
            <el-option label="求购" value="demand" />
          </el-select>
          <el-select v-model="filterStatus" placeholder="状态筛选" clearable style="width: 140px;" @change="handleSearch">
            <el-option label="全部状态" value="" />
            <el-option label="进行中" :value="1" />
            <el-option label="已结束" :value="0" />
          </el-select>
        </div>
      </div>

      <div class="info-list" v-if="filteredList.length > 0">
        <div class="info-card" v-for="item in filteredList" :key="item.id">
          <div class="card-left">
            <div class="type-badge" :class="item.type">
              <el-icon v-if="item.type === 'supply'"><Box /></el-icon>
              <el-icon v-else><ShoppingCart /></el-icon>
              {{ item.type === 'supply' ? '供应' : '求购' }}
            </div>
          </div>
          <div class="card-body">
            <div class="card-header">
              <h4>{{ item.title }}</h4>
              <el-tag :type="item.status === 1 ? 'success' : 'info'" effect="light" size="small">
                {{ item.status === 1 ? '进行中' : '已结束' }}
              </el-tag>
            </div>
            <p class="description">{{ item.description || '暂无描述' }}</p>
            <div class="card-meta">
              <div class="meta-item">
                <el-icon><Goods /></el-icon>
                <span>{{ item.category }}</span>
              </div>
              <div class="meta-item">
                <el-icon><Box /></el-icon>
                <span>数量: {{ item.quantity }}</span>
              </div>
              <div class="meta-item price">
                <el-icon><Money /></el-icon>
                <span>¥{{ item.price }}</span>
              </div>
              <div class="meta-item">
                <el-icon><Location /></el-icon>
                <span>{{ item.location || '全国' }}</span>
              </div>
            </div>
            <div class="card-footer">
              <div class="time-info">
                <el-icon><Timer /></el-icon>
                <span>发布于 {{ formatDate(item.createTime) }}</span>
              </div>
              <div class="view-count">
                <el-icon><View /></el-icon>
                <span>{{ item.viewCount || 0 }} 次浏览</span>
              </div>
            </div>
          </div>
          <div class="card-actions">
            <el-button type="primary" size="small" @click="viewDetail(item)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button 
              :type="item.status === 1 ? 'warning' : 'success'" 
              size="small"
              @click="toggleStatus(item)"
            >
              <el-icon><component :is="item.status === 1 ? 'Remove' : 'CircleCheck'" /></el-icon>
              {{ item.status === 1 ? '结束' : '重新发布' }}
            </el-button>
            <el-button type="danger" size="small" @click="deleteItem(item.id)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </div>
        </div>
      </div>

      <div v-else class="empty-state">
        <el-icon :size="64" color="#ccc"><Document /></el-icon>
        <p>暂无供需信息</p>
        <el-button type="primary" @click="$router.push('/supply-demand/publish')">发布第一条信息</el-button>
      </div>

      <div class="pagination-wrapper" v-if="filteredList.length > 0">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="loadData"
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
import { getMySupplyDemand, deleteSupplyDemand, updateSupplyDemand } from '../../api/supplyDemand'
import { 
  Document, Plus, Box, ShoppingCart, CircleCheck, Remove, 
  Search, Goods, Money, Location, Timer, View, Delete
} from '@element-plus/icons-vue'

const router = useRouter()
const userId = localStorage.getItem('userId')
const list = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const filterType = ref('')
const filterStatus = ref('')

const statistics = computed(() => {
  const supply = list.value.filter(item => item.type === 'supply').length
  const demand = list.value.filter(item => item.type === 'demand').length
  const active = list.value.filter(item => item.status === 1).length
  const ended = list.value.filter(item => item.status === 0).length
  return { supply, demand, active, ended }
})

const filteredList = computed(() => {
  let result = list.value
  
  if (searchKeyword.value) {
    result = result.filter(item => item.title.includes(searchKeyword.value))
  }
  
  if (filterType.value) {
    result = result.filter(item => item.type === filterType.value)
  }
  
  if (filterStatus.value !== '') {
    result = result.filter(item => item.status === filterStatus.value)
  }
  
  return result
})

onMounted(() => {
  loadData()
})

const loadData = async () => {
  try {
    const res = await getMySupplyDemand(userId, { page: page.value, size: size.value })
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('加载数据失败:', error)
    list.value = []
  }
}

const handleSizeChange = (newSize) => {
  size.value = newSize
  page.value = 1
  loadData()
}

const handleSearch = () => {
  page.value = 1
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
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
    return date.toLocaleDateString('zh-CN')
  }
}

const viewDetail = (item) => {
  router.push(`/supply-demand/${item.id}`)
}

const toggleStatus = async (item) => {
  const newStatus = item.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '重新发布' : '结束'
  
  try {
    await ElMessageBox.confirm(`确定要${action}该信息吗？`, '提示', { type: 'warning' })
    await updateSupplyDemand(item.id, { status: newStatus })
    ElMessage.success(`${action}成功`)
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

const deleteItem = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该信息吗？删除后无法恢复', '提示', { type: 'warning' })
    await deleteSupplyDemand(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}
</script>

<style scoped>
.my-supply-demand-page {
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

.stat-card.supply .stat-icon {
  background: linear-gradient(135deg, #4CAF50, #45a049);
}

.stat-card.demand .stat-icon {
  background: linear-gradient(135deg, #FF9800, #F57C00);
}

.stat-card.active .stat-icon {
  background: linear-gradient(135deg, #2196F3, #1976D2);
}

.stat-card.ended .stat-icon {
  background: linear-gradient(135deg, #9E9E9E, #757575);
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

.info-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-card {
  display: flex;
  align-items: stretch;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #f0f0f0;
  overflow: hidden;
  transition: all 0.3s;
}

.info-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.card-left {
  width: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
}

.type-badge {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
}

.type-badge.supply {
  background: linear-gradient(135deg, rgba(76, 175, 80, 0.1), rgba(76, 175, 80, 0.05));
  color: #4CAF50;
}

.type-badge.demand {
  background: linear-gradient(135deg, rgba(255, 152, 0, 0.1), rgba(255, 152, 0, 0.05));
  color: #FF9800;
}

.card-body {
  flex: 1;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h4 {
  margin: 0;
  font-size: 16px;
  color: #333;
  font-weight: 600;
}

.description {
  margin: 0;
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #999;
}

.meta-item.price {
  color: #f56c6c;
  font-weight: 600;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f5f5f5;
}

.time-info, .view-count {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #999;
}

.card-actions {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 8px;
  padding: 16px;
  border-left: 1px solid #f0f0f0;
  background: #fafafa;
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
  .stats-section {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .stats-section {
    grid-template-columns: 1fr;
  }
  
  .info-card {
    flex-direction: column;
  }
  
  .card-left {
    width: 100%;
    padding: 12px;
  }
  
  .type-badge {
    flex-direction: row;
  }
  
  .card-actions {
    flex-direction: row;
    border-left: none;
    border-top: 1px solid #f0f0f0;
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
