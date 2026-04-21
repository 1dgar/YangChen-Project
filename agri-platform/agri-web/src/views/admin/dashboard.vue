<template>
  <div class="dashboard">
    <div class="welcome-section">
      <div class="welcome-content">
        <h1>欢迎回来，管理员</h1>
        <p>今天是 {{ currentDate }}，祝您工作愉快！</p>
      </div>
      <div class="quick-actions">
        <el-button type="primary" @click="$router.push('/admin/users')">
          <el-icon><User /></el-icon>
          用户管理
        </el-button>
        <el-button type="success" @click="$router.push('/admin/products')">
          <el-icon><Goods /></el-icon>
          产品管理
        </el-button>
        <el-button type="warning" @click="$router.push('/admin/news')">
          <el-icon><Document /></el-icon>
          资讯管理
        </el-button>
      </div>
    </div>

    <div class="stats-grid">
      <div class="stat-card blue">
        <div class="stat-icon">
          <el-icon :size="32"><User /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.userCount }}</div>
          <div class="stat-label">用户总数</div>
        </div>
      </div>

      <div class="stat-card green">
        <div class="stat-icon">
          <el-icon :size="32"><Goods /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.productCount }}</div>
          <div class="stat-label">产品总数</div>
        </div>
      </div>

      <div class="stat-card orange">
        <div class="stat-icon">
          <el-icon :size="32"><List /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.supplyDemandCount }}</div>
          <div class="stat-label">供需信息</div>
        </div>
      </div>

      <div class="stat-card red">
        <div class="stat-icon">
          <el-icon :size="32"><Document /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.newsCount }}</div>
          <div class="stat-label">资讯数量</div>
        </div>
      </div>
    </div>

    <div class="content-grid">
      <div class="chart-section">
        <div class="section-header">
          <h3><el-icon><TrendCharts /></el-icon> 数据趋势</h3>
          <el-radio-group v-model="chartType" size="small">
            <el-radio-button value="week">本周</el-radio-button>
            <el-radio-button value="month">本月</el-radio-button>
          </el-radio-group>
        </div>
        <div class="chart-container">
          <div class="chart-bars">
            <div class="bar-item" v-for="(item, idx) in chartData" :key="idx">
              <div class="bar-wrapper">
                <div class="bar user" :style="{ height: item.user + '%' }"></div>
                <div class="bar product" :style="{ height: item.product + '%' }"></div>
              </div>
              <span class="bar-label">{{ item.label }}</span>
            </div>
          </div>
          <div class="chart-legend">
            <span class="legend-item"><i class="dot user"></i>新增用户</span>
            <span class="legend-item"><i class="dot product"></i>新增产品</span>
          </div>
        </div>
      </div>

      <div class="recent-section">
        <div class="section-header">
          <h3><el-icon><Timer /></el-icon> 最新动态</h3>
          <el-button text type="primary">查看全部</el-button>
        </div>
        <div class="activity-list">
          <div class="activity-item" v-for="(item, idx) in recentActivities" :key="idx">
            <div class="activity-icon" :class="item.type">
              <el-icon v-if="item.type === 'user'"><User /></el-icon>
              <el-icon v-else-if="item.type === 'product'"><Goods /></el-icon>
              <el-icon v-else><Document /></el-icon>
            </div>
            <div class="activity-content">
              <p class="activity-text">{{ item.content }}</p>
              <span class="activity-time">{{ item.time }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="bottom-section">
      <div class="category-stats">
        <div class="section-header">
          <h3><el-icon><PieChart /></el-icon> 产品分类统计</h3>
        </div>
        <div class="category-list">
          <div class="category-item" v-for="(item, idx) in categoryStats" :key="idx">
            <div class="category-info">
              <span class="category-name">{{ item.name }}</span>
              <span class="category-count">{{ item.count }} 件</span>
            </div>
            <div class="category-bar">
              <div class="bar-fill" :style="{ width: item.percent + '%', background: item.color }"></div>
            </div>
          </div>
        </div>
      </div>

      <div class="quick-links">
        <div class="section-header">
          <h3><el-icon><Link /></el-icon> 快捷入口</h3>
        </div>
        <div class="links-grid">
          <div class="link-item" @click="$router.push('/admin/users')">
            <el-icon :size="24"><User /></el-icon>
            <span>用户管理</span>
          </div>
          <div class="link-item" @click="$router.push('/admin/products')">
            <el-icon :size="24"><Goods /></el-icon>
            <span>产品审核</span>
          </div>
          <div class="link-item" @click="$router.push('/admin/news')">
            <el-icon :size="24"><Document /></el-icon>
            <span>发布资讯</span>
          </div>
          <div class="link-item" @click="$router.push('/market')">
            <el-icon :size="24"><TrendCharts /></el-icon>
            <span>市场行情</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { User, Goods, List, Document, TrendCharts, Timer, ArrowUp, PieChart, Link } from '@element-plus/icons-vue'
import { getStatistics, getCategoryStats, getRecentActivities } from '../../api/admin'

const statistics = ref({
  userCount: 0,
  productCount: 0,
  supplyDemandCount: 0,
  newsCount: 0
})

const chartType = ref('week')

const currentDate = computed(() => {
  const date = new Date()
  const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日 ${weekDays[date.getDay()]}`
})

const chartData = ref([])

const recentActivities = ref([])

const categoryStats = ref([])

const generateChartData = () => {
  const labels = chartType.value === 'week' 
    ? ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    : ['第1周', '第2周', '第3周', '第4周']
  
  return labels.map(label => ({
    label,
    user: Math.floor(Math.random() * 60) + 20,
    product: Math.floor(Math.random() * 60) + 20
  }))
}

onMounted(async () => {
  try {
    const res = await getStatistics()
    statistics.value = res.data
  } catch (e) {
    console.error('加载统计数据失败', e)
  }
  
  try {
    const res = await getCategoryStats()
    categoryStats.value = res.data
  } catch (e) {
    console.error('加载分类统计失败', e)
  }
  
  try {
    const res = await getRecentActivities()
    recentActivities.value = res.data
  } catch (e) {
    console.error('加载最近动态失败', e)
  }
  
  chartData.value = generateChartData()
})

watch(chartType, () => {
  chartData.value = generateChartData()
})
</script>

<style scoped>
.dashboard {
  padding: 24px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.welcome-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #1a5f2a 0%, #2e7d32 100%);
  color: white;
  padding: 24px 32px;
  border-radius: 16px;
  margin-bottom: 24px;
}

.welcome-content h1 {
  font-size: 24px;
  margin: 0 0 8px 0;
}

.welcome-content p {
  margin: 0;
  opacity: 0.9;
}

.quick-actions {
  display: flex;
  gap: 12px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
}

.stat-card.blue::before { background: linear-gradient(90deg, #409eff, #66b1ff); }
.stat-card.green::before { background: linear-gradient(90deg, #67c23a, #85ce61); }
.stat-card.orange::before { background: linear-gradient(90deg, #e6a23c, #ebb563); }
.stat-card.red::before { background: linear-gradient(90deg, #f56c6c, #f78989); }

.stat-card .stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-bottom: 16px;
}

.stat-card.blue .stat-icon { background: linear-gradient(135deg, #409eff, #66b1ff); }
.stat-card.green .stat-icon { background: linear-gradient(135deg, #67c23a, #85ce61); }
.stat-card.orange .stat-icon { background: linear-gradient(135deg, #e6a23c, #ebb563); }
.stat-card.red .stat-icon { background: linear-gradient(135deg, #f56c6c, #f78989); }

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #303133;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}

.stat-footer {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  font-weight: 500;
}

.trend.up { color: #67c23a; }
.trend.down { color: #f56c6c; }

.period {
  font-size: 12px;
  color: #c0c4cc;
}

.content-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
  margin-bottom: 24px;
}

.chart-section, .recent-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  margin: 0;
  color: #303133;
}

.chart-container {
  height: 200px;
}

.chart-bars {
  display: flex;
  align-items: flex-end;
  justify-content: space-around;
  height: 160px;
  padding: 0 20px;
}

.bar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.bar-wrapper {
  display: flex;
  align-items: flex-end;
  gap: 4px;
  height: 140px;
}

.bar {
  width: 16px;
  border-radius: 4px 4px 0 0;
  transition: height 0.3s;
}

.bar.user { background: linear-gradient(180deg, #409eff, #66b1ff); }
.bar.product { background: linear-gradient(180deg, #67c23a, #85ce61); }

.bar-label {
  font-size: 12px;
  color: #909399;
}

.chart-legend {
  display: flex;
  justify-content: center;
  gap: 24px;
  margin-top: 12px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #606266;
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.dot.user { background: #409eff; }
.dot.product { background: #67c23a; }

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  background: #f9fafc;
  border-radius: 8px;
}

.activity-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.activity-icon.user { background: #409eff; }
.activity-icon.product { background: #67c23a; }
.activity-icon.news { background: #e6a23c; }

.activity-text {
  margin: 0 0 4px 0;
  font-size: 14px;
  color: #303133;
}

.activity-time {
  font-size: 12px;
  color: #909399;
}

.bottom-section {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
}

.category-stats, .quick-links {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.category-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.category-info {
  display: flex;
  justify-content: space-between;
}

.category-name {
  font-size: 14px;
  color: #303133;
}

.category-count {
  font-size: 14px;
  color: #909399;
}

.category-bar {
  height: 8px;
  background: #f0f2f5;
  border-radius: 4px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.3s;
}

.links-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.link-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px;
  background: #f9fafc;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.link-item:hover {
  background: #1a5f2a;
  color: white;
}

.link-item span {
  font-size: 13px;
}
</style>
