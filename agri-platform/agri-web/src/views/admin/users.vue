<template>
  <div class="admin-users">
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon total">
          <el-icon><User /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">总用户数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon farmer">
          <el-icon><Avatar /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.farmers }}</div>
          <div class="stat-label">农户</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon buyer">
          <el-icon><ShoppingCart /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.buyers }}</div>
          <div class="stat-label">采购商</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon active">
          <el-icon><CircleCheck /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.active }}</div>
          <div class="stat-label">活跃用户</div>
        </div>
      </div>
    </div>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <div class="header-title">
            <el-icon><User /></el-icon>
            <span>用户列表</span>
          </div>
          <div class="header-actions">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索用户名/手机号"
              clearable
              style="width: 200px"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select v-model="filterRole" placeholder="角色筛选" clearable style="width: 120px" @change="handleSearch">
              <el-option label="全部" value="" />
              <el-option label="管理员" value="admin" />
              <el-option label="农户" value="farmer" />
              <el-option label="采购商" value="buyer" />
            </el-select>
            <el-select v-model="filterStatus" placeholder="状态筛选" clearable style="width: 120px" @change="handleSearch">
              <el-option label="全部" value="" />
              <el-option label="正常" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>
          </div>
        </div>
      </template>

      <el-table :data="filteredUsers" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column label="用户信息" min-width="200">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :size="40" :style="{ background: getAvatarColor(row.role) }">
                {{ row.username?.charAt(0)?.toUpperCase() || 'U' }}
              </el-avatar>
              <div class="user-detail">
                <div class="user-name">{{ row.username }}</div>
                <div class="user-phone">{{ row.phone || '未绑定手机' }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="role" label="角色" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.role)" effect="light" round>
              <el-icon v-if="row.role === 'admin'"><UserFilled /></el-icon>
              <el-icon v-else-if="row.role === 'farmer'"><Avatar /></el-icon>
              <el-icon v-else><ShoppingCart /></el-icon>
              {{ getRoleName(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" effect="dark" round>
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" align="center">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.role !== 'admin'"
              :type="row.status === 1 ? 'danger' : 'success'"
              size="small"
              round
              @click="toggleStatus(row)"
            >
              <el-icon><CircleClose v-if="row.status === 1" /><CircleCheck v-else /></el-icon>
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <span v-else class="no-action">-</span>
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
          @current-change="loadUsers"
          @size-change="loadUsers"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Avatar, ShoppingCart, CircleCheck, CircleClose, Search, UserFilled } from '@element-plus/icons-vue'
import { getUsers, updateUserStatus } from '../../api/admin'

const users = ref([])
const searchKeyword = ref('')
const filterRole = ref('')
const filterStatus = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const stats = computed(() => {
  const farmers = users.value.filter(u => u.role === 'farmer').length
  const buyers = users.value.filter(u => u.role === 'buyer').length
  const active = users.value.filter(u => u.status === 1).length
  return {
    total: users.value.length,
    farmers,
    buyers,
    active
  }
})

const filteredUsers = computed(() => {
  let result = users.value
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(u => 
      u.username?.toLowerCase().includes(keyword) || 
      u.phone?.includes(keyword)
    )
  }
  if (filterRole.value) {
    result = result.filter(u => u.role === filterRole.value)
  }
  if (filterStatus.value !== '' && filterStatus.value !== null) {
    result = result.filter(u => u.status === filterStatus.value)
  }
  return result
})

onMounted(() => {
  loadUsers()
})

const loadUsers = async () => {
  const res = await getUsers({ page: currentPage.value, size: pageSize.value })
  users.value = res.data.records || res.data
  total.value = res.data.total || users.value.length
}

const handleSearch = () => {
  currentPage.value = 1
}

const toggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 0 ? '禁用' : '启用'
  
  try {
    await ElMessageBox.confirm(`确定要${action}用户 "${row.username}" 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await updateUserStatus(row.id, { status: newStatus })
    ElMessage.success(`${action}成功`)
    loadUsers()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const getRoleType = (role) => {
  const types = { admin: 'danger', farmer: 'success', buyer: 'primary' }
  return types[role] || 'info'
}

const getRoleName = (role) => {
  const names = { admin: '管理员', farmer: '农户', buyer: '采购商' }
  return names[role] || role
}

const getAvatarColor = (role) => {
  const colors = { 
    admin: 'linear-gradient(135deg, #f56c6c, #e6a23c)', 
    farmer: 'linear-gradient(135deg, #67c23a, #95d475)', 
    buyer: 'linear-gradient(135deg, #409eff, #79bbff)' 
  }
  return colors[role] || '#909399'
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}
</script>

<style scoped>
.admin-users {
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

.stat-icon.farmer {
  background: linear-gradient(135deg, #11998e, #38ef7d);
}

.stat-icon.buyer {
  background: linear-gradient(135deg, #4facfe, #00f2fe);
}

.stat-icon.active {
  background: linear-gradient(135deg, #f093fb, #f5576c);
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

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-detail {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-weight: 500;
  color: #303133;
}

.user-phone {
  font-size: 12px;
  color: #909399;
}

.no-action {
  color: #c0c4cc;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
