<template>
  <div class="market-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-bg"></div>
      <div class="container">
        <div class="breadcrumb">
          <router-link to="/">首页</router-link>
          <el-icon><ArrowRight /></el-icon>
          <span>市场行情</span>
        </div>
        <h2>市场行情监测</h2>
        <p>实时数据 · 精准分析 · 洞察趋势</p>
        <div class="header-stats">
          <div class="stat-item">
            <el-icon><DataLine /></el-icon>
            <span>{{ prices.length }}种农产品实时监测</span>
          </div>
          <div class="stat-item">
            <el-icon><TrendCharts /></el-icon>
            <span>覆盖全国主要批发市场</span>
          </div>
          <div class="stat-item">
            <el-icon><Timer /></el-icon>
            <span>每日更新价格数据</span>
          </div>
        </div>
      </div>
    </div>

    <div class="container main-container">
      <!-- 价格指数区 -->
      <div class="indices-section">
        <div class="index-card main-index">
          <div class="index-icon-wrapper">
            <div class="index-icon green">
              <el-icon :size="32"><TrendCharts /></el-icon>
            </div>
          </div>
          <div class="index-content">
            <div class="index-header">
              <span class="index-title">农产品批发价格200指数</span>
              <span class="index-date">{{ currentDate }}</span>
            </div>
            <div class="index-body">
              <div class="main-value">
                <span class="value">117.17</span>
                <span class="change down">
                  <el-icon><ArrowDown /></el-icon>
                  -0.16
                </span>
              </div>
              <div class="sub-info">
                <span class="info-item">
                  <label>环比</label>
                  <span class="down">-0.14%</span>
                </span>
                <span class="info-item">
                  <label>同比</label>
                  <span class="down">-5.12%</span>
                </span>
              </div>
            </div>
          </div>
        </div>

        <div class="index-card main-index">
          <div class="index-icon-wrapper">
            <div class="index-icon orange">
              <el-icon :size="32"><Basketball /></el-icon>
            </div>
          </div>
          <div class="index-content">
            <div class="index-header">
              <span class="index-title">"菜篮子"产品价格指数</span>
              <span class="index-date">{{ currentDate }}</span>
            </div>
            <div class="index-body">
              <div class="main-value">
                <span class="value">117.96</span>
                <span class="change down">
                  <el-icon><ArrowDown /></el-icon>
                  -0.19
                </span>
              </div>
              <div class="sub-info">
                <span class="info-item">
                  <label>环比</label>
                  <span class="down">-0.16%</span>
                </span>
                <span class="info-item">
                  <label>同比</label>
                  <span class="down">-4.89%</span>
                </span>
              </div>
            </div>
          </div>
        </div>

        <div class="index-card stats-index">
          <div class="stats-grid">
            <div class="stat-box">
              <div class="stat-icon up">
                <el-icon><ArrowUp /></el-icon>
              </div>
              <div class="stat-data">
                <span class="stat-value">{{ indexData.upCount }}</span>
                <span class="stat-label">上涨品种</span>
              </div>
            </div>
            <div class="stat-box">
              <div class="stat-icon down">
                <el-icon><ArrowDown /></el-icon>
              </div>
              <div class="stat-data">
                <span class="stat-value">{{ indexData.downCount }}</span>
                <span class="stat-label">下跌品种</span>
              </div>
            </div>
            <div class="stat-box">
              <div class="stat-icon flat">
                <el-icon><Minus /></el-icon>
              </div>
              <div class="stat-data">
                <span class="stat-value">{{ indexData.flatCount }}</span>
                <span class="stat-label">持平品种</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 主要内容区 -->
      <div class="content-grid">
        <!-- 左侧：价格趋势图 -->
        <div class="chart-section">
          <div class="section-header">
            <div class="header-title">
              <el-icon><DataAnalysis /></el-icon>
              <h3>价格趋势分析</h3>
            </div>
            <div class="chart-controls">
              <el-select v-model="selectedCategory" @change="loadTrend" size="default" style="width: 130px;">
                <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
              </el-select>
              <el-radio-group v-model="timeRange" size="default" @change="loadTrend">
                <el-radio-button value="7">7天</el-radio-button>
                <el-radio-button value="30">30天</el-radio-button>
                <el-radio-button value="90">90天</el-radio-button>
              </el-radio-group>
            </div>
          </div>
          <div ref="chartRef" class="chart-container"></div>
          <div class="chart-legend">
            <div class="legend-item">
              <span class="legend-color" style="background: #4CAF50;"></span>
              <span>平均价格</span>
            </div>
            <div class="legend-item">
              <span class="legend-line"></span>
              <span>趋势线</span>
            </div>
          </div>
        </div>

        <!-- 右侧：今日行情 -->
        <div class="price-section">
          <div class="section-header">
            <div class="header-title">
              <el-icon><List /></el-icon>
              <h3>今日行情</h3>
            </div>
            <el-input
              v-model="searchKeyword"
              placeholder="搜索产品"
              size="default"
              clearable
              style="width: 160px;"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>
          <div class="price-table-wrapper">
            <table class="price-table">
              <thead>
                <tr>
                  <th>分类</th>
                  <th>产品</th>
                  <th>价格</th>
                  <th>涨跌</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in filteredPrices" :key="item.id" @click="showDetail(item)">
                  <td>
                    <span class="category-tag" :style="getCategoryStyle(item.category)">
                      {{ item.category }}
                    </span>
                  </td>
                  <td class="product-name">{{ item.productName }}</td>
                  <td class="price-cell">
                    <span class="price">¥{{ item.price }}</span>
                    <span class="unit">/{{ item.unit }}</span>
                  </td>
                  <td>
                    <span class="trend" :class="item.trend">
                      <el-icon v-if="item.trend === 'up'"><ArrowUp /></el-icon>
                      <el-icon v-else-if="item.trend === 'down'"><ArrowDown /></el-icon>
                      <el-icon v-else><Minus /></el-icon>
                      {{ item.change > 0 ? '+' : '' }}{{ item.change }}%
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="table-footer">
            <span>共 {{ filteredPrices.length }} 条数据</span>
            <el-button type="primary" link>查看全部</el-button>
          </div>
        </div>
      </div>

      <!-- 分类价格排行 -->
      <div class="ranking-section">
        <div class="section-header">
          <div class="header-title">
            <el-icon><Trophy /></el-icon>
            <h3>价格涨跌排行</h3>
          </div>
          <el-radio-group v-model="rankType" size="default">
            <el-radio-button value="high">
              <el-icon><Top /></el-icon>
              涨幅榜
            </el-radio-button>
            <el-radio-button value="low">
              <el-icon><Bottom /></el-icon>
              跌幅榜
            </el-radio-button>
          </el-radio-group>
        </div>
        <div class="ranking-grid">
          <div class="rank-card" v-for="(item, index) in filteredRankingList" :key="index" :class="{ top3: index < 3 }">
            <div class="rank-number" :class="{ top: index < 3 }">{{ index + 1 }}</div>
            <div class="rank-icon" :style="{ background: getCategoryGradient(item.category) }">
              <el-icon :size="20"><component :is="getCategoryIcon(item.category)" /></el-icon>
            </div>
            <div class="rank-info">
              <h4>{{ item.name }}</h4>
              <p class="rank-category">{{ item.category }}</p>
            </div>
            <div class="rank-price">
              <span class="price">¥{{ item.price }}</span>
              <span class="change" :class="item.trend">
                {{ item.change > 0 ? '+' : '' }}{{ item.change }}%
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- 市场分析 -->
      <div class="analysis-section" v-if="false">
        <div class="section-header">
          <div class="header-title">
            <el-icon><DocumentChecked /></el-icon>
            <h3>市场分析</h3>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { getPrices, getTrend, getCategories } from '../../api/market'
import { 
  ArrowUp, ArrowDown, Minus, Search, ArrowRight, DataLine, 
  TrendCharts, Timer, DataAnalysis, List, Trophy, Top, Bottom,
  DocumentChecked, Food, Goods, Dish, Basketball
} from '@element-plus/icons-vue'

const currentDate = new Date().toISOString().split('T')[0]

const prices = ref([])
const categories = ref(['蔬菜', '水果', '粮油', '畜牧', '水产品'])
const selectedCategory = ref('蔬菜')
const timeRange = ref('30')
const searchKeyword = ref('')
const rankType = ref('high')
const chartRef = ref()
let chart = null

const rankingList = ref([])

const indexData = ref({
  mainIndex: 117.17,
  mainChange: -0.16,
  basketIndex: 117.96,
  basketChange: -0.19,
  upCount: 0,
  downCount: 0,
  flatCount: 0
})

const filteredPrices = computed(() => {
  if (!searchKeyword.value) return prices.value
  return prices.value.filter(p => 
    p.productName.includes(searchKeyword.value) || 
    p.category.includes(searchKeyword.value)
  )
})

const filteredRankingList = computed(() => {
  const list = rankingList.value.filter(item => 
    rankType.value === 'high' ? item.change > 0 : item.change < 0
  )
  return list.sort((a, b) => rankType.value === 'high' ? b.change - a.change : a.change - b.change)
})

onMounted(async () => {
  await loadPrices()
  setTimeout(loadTrend, 100)
  window.addEventListener('resize', handleResize)
})

const loadPrices = async () => {
  try {
    const res = await getPrices()
    const data = res.data || []
    
    prices.value = data.map((item, idx) => {
      const change = (Math.random() * 30 - 15).toFixed(1)
      return {
        id: item.id || idx + 1,
        productName: item.productName,
        category: item.category,
        price: item.price,
        unit: item.unit || '斤',
        trend: parseFloat(change) > 0 ? 'up' : parseFloat(change) < 0 ? 'down' : 'flat',
        change: parseFloat(change)
      }
    })
    
    rankingList.value = prices.value.map(item => ({
      name: item.productName,
      category: item.category,
      price: item.price,
      change: item.change,
      trend: item.trend
    }))
    
    indexData.value.upCount = prices.value.filter(p => p.trend === 'up').length
    indexData.value.downCount = prices.value.filter(p => p.trend === 'down').length
    indexData.value.flatCount = prices.value.filter(p => p.trend === 'flat').length
  } catch (e) {
    console.error('加载价格数据失败', e)
  }
}

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (chart) {
    chart.dispose()
  }
})

const handleResize = () => {
  if (chart) {
    chart.resize()
  }
}

const loadTrend = async () => {
  if (!chartRef.value) return
  
  try {
    // 根据选择的时间范围生成日期和模拟数据
    const days = parseInt(timeRange.value)
    const dates = []
    const priceData = []
    
    // 生成日期数组
    for (let i = days - 1; i >= 0; i--) {
      const date = new Date()
      date.setDate(date.getDate() - i)
      dates.push(`${date.getMonth() + 1}/${date.getDate()}`)
    }
    
    // 根据分类生成基础价格
    const basePrices = {
      '蔬菜': 3.0,
      '水果': 6.0,
      '粮油': 3.5,
      '畜牧': 25.0,
      '水产品': 30.0
    }
    const basePrice = basePrices[selectedCategory.value] || 3.0
    
    // 生成模拟价格数据（带随机波动）
    let currentPrice = basePrice
    for (let i = 0; i < days; i++) {
      // 随机波动 -5% 到 +5%
      const change = (Math.random() - 0.5) * 0.1
      currentPrice = currentPrice * (1 + change)
      // 保持在基础价格的 ±20% 范围内
      const minPrice = basePrice * 0.8
      const maxPrice = basePrice * 1.2
      currentPrice = Math.max(minPrice, Math.min(maxPrice, currentPrice))
      priceData.push(parseFloat(currentPrice.toFixed(2)))
    }
    
    if (!chart) {
      chart = echarts.init(chartRef.value)
    }
    
    const option = {
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        top: '10%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: dates,
        axisLine: { lineStyle: { color: '#e0e0e0' } },
        axisLabel: { 
          color: '#666', 
          fontSize: 12,
          interval: days === 7 ? 0 : days === 30 ? 4 : 14
        }
      },
      yAxis: {
        type: 'value',
        axisLine: { show: false },
        splitLine: { lineStyle: { color: '#f0f0f0', type: 'dashed' } },
        axisLabel: { color: '#666', fontSize: 12 },
        scale: true
      },
      series: [{
        data: priceData,
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: days === 7 ? 8 : 4,
        lineStyle: {
          color: '#4CAF50',
          width: 3
        },
        itemStyle: {
          color: '#4CAF50',
          borderWidth: 2,
          borderColor: '#fff'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(76, 175, 80, 0.3)' },
              { offset: 1, color: 'rgba(76, 175, 80, 0.05)' }
            ]
          }
        }
      }],
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(255, 255, 255, 0.95)',
        borderColor: '#e0e0e0',
        borderWidth: 1,
        textStyle: { color: '#333' },
        formatter: '{b}<br/>价格: <strong>¥{c}</strong>/斤'
      }
    }
    
    chart.setOption(option, true)
  } catch (e) {
    console.error('加载趋势数据失败', e)
  }
}

const getCategoryStyle = (category) => {
  const colors = {
    '蔬菜': '#4CAF50',
    '水果': '#FF9800',
    '粮油': '#795548',
    '畜牧': '#F44336',
    '水产品': '#2196F3'
  }
  return { backgroundColor: colors[category] || '#999' }
}

const getCategoryGradient = (category) => {
  const gradients = {
    '蔬菜': 'linear-gradient(135deg, #4CAF50, #45a049)',
    '水果': 'linear-gradient(135deg, #FF9800, #F57C00)',
    '粮油': 'linear-gradient(135deg, #795548, #5D4037)',
    '畜牧': 'linear-gradient(135deg, #F44336, #D32F2F)',
    '水产品': 'linear-gradient(135deg, #2196F3, #1976D2)'
  }
  return gradients[category] || 'linear-gradient(135deg, #4CAF50, #45a049)'
}

const getCategoryIcon = (category) => {
  const icons = {
    '蔬菜': 'Food',
    '水果': 'Food',
    '粮油': 'Goods',
    '畜牧': 'Dish',
    '水产品': 'Dish'
  }
  return icons[category] || 'Goods'
}

const showDetail = (item) => {
  // 显示详情逻辑
  console.log('查看详情:', item)
}
</script>

<style scoped>
.market-page {
  min-height: 100vh;
  background:
    linear-gradient(180deg, rgba(248, 250, 248, 0.95) 0%, rgba(240, 244, 240, 0.95) 100%),
    url('https://picsum.photos/seed/marketbg/1920/1080') center/cover fixed;
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
    linear-gradient(135deg, rgba(26, 95, 42, 0.94) 0%, rgba(46, 125, 50, 0.91) 50%, rgba(76, 175, 80, 0.88) 100%),
    url('https://picsum.photos/seed/marketheader/1920/400') center/cover no-repeat;
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
  background: url("data:image/svg+xml,%3Csvg width='100' height='100' viewBox='0 0 100 100' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M11 18c3.866 0 7-3.134 7-7s-3.134-7-7-7-7 3.134-7 7 3.134 7 7 7zm48 25c3.866 0 7-3.134 7-7s-3.134-7-7-7-7 3.134-7 7 3.134 7 7 7zm-43-7c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zm63 31c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zM34 90c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zm56-76c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zM12 86c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm28-65c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm23-11c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm-6 60c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm29 22c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zM32 63c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm57-13c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm-9-21c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM60 91c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM35 41c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM12 60c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2z' fill='%23ffffff' fill-opacity='0.03' fill-rule='evenodd'/%3E%3C/svg%3E");
  opacity: 0.5;
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

/* 价格指数区 */
.indices-section {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  margin-bottom: 32px;
}

.index-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s;
}

.index-card:hover {
  transform: translateY(-4px);
}

.index-card.main-index {
  display: flex;
  gap: 20px;
}

.index-icon-wrapper {
  flex-shrink: 0;
}

.index-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.index-icon.green {
  background: linear-gradient(135deg, #4CAF50, #45a049);
}

.index-icon.orange {
  background: linear-gradient(135deg, #FF9800, #F57C00);
}

.index-content {
  flex: 1;
}

.index-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.index-title {
  font-size: 15px;
  color: #666;
  font-weight: 500;
}

.index-date {
  font-size: 12px;
  color: #999;
  background: #f5f5f5;
  padding: 4px 10px;
  border-radius: 10px;
}

.main-value {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.main-value .value {
  font-size: 42px;
  font-weight: 700;
  color: #333;
}

.change {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
}

.change.up {
  background: rgba(244, 67, 54, 0.1);
  color: #f44336;
}

.change.down {
  background: rgba(76, 175, 80, 0.1);
  color: #4CAF50;
}

.sub-info {
  display: flex;
  gap: 24px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item label {
  font-size: 12px;
  color: #999;
}

.info-item span {
  font-size: 14px;
  font-weight: 600;
}

.info-item span.up {
  color: #f44336;
}

.info-item span.down {
  color: #4CAF50;
}

/* 统计卡片 */
.stats-index {
  padding: 20px;
}

.stats-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: 100%;
  justify-content: center;
}

.stat-box {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-icon.up {
  background: linear-gradient(135deg, #f44336, #d32f2f);
}

.stat-icon.down {
  background: linear-gradient(135deg, #4CAF50, #388e3c);
}

.stat-icon.flat {
  background: linear-gradient(135deg, #9e9e9e, #757575);
}

.stat-data {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #333;
}

.stat-label {
  font-size: 13px;
  color: #999;
}

/* 内容网格 */
.content-grid {
  display: grid;
  grid-template-columns: 1.5fr 1fr;
  gap: 24px;
  margin-bottom: 32px;
}

.chart-section,
.price-section {
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
  color: #4CAF50;
  font-size: 22px;
}

.section-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
  font-weight: 600;
}

.chart-controls {
  display: flex;
  gap: 12px;
}

.chart-container {
  height: 320px;
}

.chart-legend {
  display: flex;
  justify-content: center;
  gap: 24px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #666;
}

.legend-color {
  width: 16px;
  height: 4px;
  border-radius: 2px;
}

.legend-line {
  width: 16px;
  height: 2px;
  background: #4CAF50;
  border-radius: 1px;
}

/* 价格表格 */
.price-table-wrapper {
  max-height: 400px;
  overflow-y: auto;
}

.price-table {
  width: 100%;
  border-collapse: collapse;
}

.price-table th {
  text-align: left;
  padding: 12px;
  font-size: 13px;
  color: #999;
  font-weight: 500;
  border-bottom: 1px solid #f0f0f0;
}

.price-table td {
  padding: 14px 12px;
  border-bottom: 1px solid #f5f5f5;
}

.price-table tbody tr {
  cursor: pointer;
  transition: background 0.3s;
}

.price-table tbody tr:hover {
  background: #f8f9fa;
}

.category-tag {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  color: white;
  font-weight: 500;
}

.product-name {
  font-weight: 500;
  color: #333;
}

.price-cell {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.price-cell .price {
  font-size: 18px;
  font-weight: 700;
  color: #f44336;
}

.price-cell .unit {
  font-size: 12px;
  color: #999;
}

.trend {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 13px;
  font-weight: 500;
}

.trend.up {
  background: rgba(244, 67, 54, 0.1);
  color: #f44336;
}

.trend.down {
  background: rgba(76, 175, 80, 0.1);
  color: #4CAF50;
}

.trend:not(.up):not(.down) {
  background: rgba(158, 158, 158, 0.1);
  color: #999;
}

.table-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
  font-size: 13px;
  color: #999;
}

/* 排行区 */
.ranking-section {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  margin-bottom: 32px;
}

.ranking-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.rank-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
  transition: all 0.3s;
}

.rank-card:hover {
  background: #f0f0f0;
  transform: translateY(-2px);
}

.rank-card.top3 {
  background: linear-gradient(135deg, #fff9f0 0%, #fff 100%);
  border: 1px solid #ffe0b2;
}

.rank-number {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: #e0e0e0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 700;
  color: #666;
  flex-shrink: 0;
}

.rank-number.top {
  background: linear-gradient(135deg, #ff9800, #f57c00);
  color: white;
}

.rank-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.rank-info {
  flex: 1;
  min-width: 0;
}

.rank-info h4 {
  margin: 0 0 4px 0;
  font-size: 15px;
  color: #333;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.rank-category {
  margin: 0;
  font-size: 12px;
  color: #999;
}

.rank-price {
  text-align: right;
}

.rank-price .price {
  display: block;
  font-size: 18px;
  font-weight: 700;
  color: #333;
  margin-bottom: 4px;
}

.rank-price .change {
  font-size: 13px;
  font-weight: 600;
}

/* 市场分析 */
.analysis-section {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.analysis-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.analysis-card {
  display: flex;
  gap: 16px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
  transition: all 0.3s;
}

.analysis-card:hover {
  background: #f0f0f0;
  transform: translateY(-2px);
}

.analysis-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.analysis-content {
  flex: 1;
}

.analysis-content h4 {
  margin: 0 0 10px 0;
  font-size: 17px;
  color: #333;
  font-weight: 600;
}

.analysis-content p {
  margin: 0 0 14px 0;
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

.trend-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.trend-tags .tag {
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.trend-tags .tag.up {
  background: rgba(244, 67, 54, 0.1);
  color: #f44336;
}

.trend-tags .tag.down {
  background: rgba(76, 175, 80, 0.1);
  color: #4CAF50;
}

.trend-tags .tag.flat {
  background: rgba(158, 158, 158, 0.1);
  color: #999;
}

/* 响应式 */
@media (max-width: 1200px) {
  .indices-section {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .content-grid {
    grid-template-columns: 1fr;
  }
  
  .ranking-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .analysis-grid {
    grid-template-columns: 1fr;
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
  
  .indices-section {
    grid-template-columns: 1fr;
  }
  
  .index-card.main-index {
    flex-direction: column;
  }
  
  .ranking-grid {
    grid-template-columns: 1fr;
  }
  
  .chart-controls {
    flex-direction: column;
  }
}
</style>