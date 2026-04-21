<template>
  <div class="admin-news">
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon total">
          <el-icon><Document /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">总资讯数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon views">
          <el-icon><View /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalViews }}</div>
          <div class="stat-label">总浏览量</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon today">
          <el-icon><Calendar /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.todayCount }}</div>
          <div class="stat-label">今日发布</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon avg">
          <el-icon><TrendCharts /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.avgViews }}</div>
          <div class="stat-label">平均浏览</div>
        </div>
      </div>
    </div>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <div class="header-title">
            <el-icon><Document /></el-icon>
            <span>资讯列表</span>
          </div>
          <div class="header-actions">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索资讯标题"
              clearable
              style="width: 200px"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="showDialog = true">
              <el-icon><Plus /></el-icon>
              发布资讯
            </el-button>
          </div>
        </div>
      </template>

      <el-table :data="filteredNews" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column label="资讯信息" min-width="300">
          <template #default="{ row }">
            <div class="news-info">
              <el-image
                v-if="row.coverImage"
                :src="row.coverImage"
                fit="cover"
                class="news-image"
              >
                <template #error>
                  <div class="image-placeholder">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
              <div class="news-image image-placeholder" v-else>
                <el-icon><Picture /></el-icon>
              </div>
              <div class="news-detail">
                <div class="news-title">{{ row.title }}</div>
                <div class="news-meta">
                  <span class="author">
                    <el-icon><User /></el-icon>
                    {{ row.author }}
                  </span>
                  <span class="time">{{ formatDate(row.createTime) }}</span>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览量" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="primary" effect="light" round>
              <el-icon><View /></el-icon>
              {{ row.viewCount || 0 }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" round @click="viewNews(row)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button type="danger" size="small" round @click="deleteNewsItem(row)">
              <el-icon><Delete /></el-icon>
              删除
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
          @current-change="loadNews"
          @size-change="loadNews"
        />
      </div>
    </el-card>

    <el-dialog v-model="showDialog" title="发布资讯" width="700px" :close-on-click-modal="false">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入资讯标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="封面图" prop="coverImage">
          <el-input v-model="form.coverImage" placeholder="请输入封面图片URL（可选）" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            type="textarea"
            v-model="form.content"
            rows="8"
            placeholder="请输入资讯内容"
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="submitNews">
          <el-icon><Check /></el-icon>
          发布
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showDetail" title="资讯详情" width="700px">
      <div class="news-detail-dialog">
        <h2 class="detail-title">{{ currentNews.title }}</h2>
        <div class="detail-meta">
          <span><el-icon><User /></el-icon> {{ currentNews.author }}</span>
          <span><el-icon><Calendar /></el-icon> {{ formatDate(currentNews.createTime) }}</span>
          <span><el-icon><View /></el-icon> {{ currentNews.viewCount || 0 }} 次浏览</span>
        </div>
        <el-image
          v-if="currentNews.coverImage"
          :src="currentNews.coverImage"
          fit="cover"
          class="detail-cover"
        />
        <div class="detail-content">{{ currentNews.content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document, View, Calendar, TrendCharts, Search, Plus, Picture, User, Delete, Check } from '@element-plus/icons-vue'
import { getNewsList } from '../../api/market'
import { addNews, deleteNews } from '../../api/admin'

const newsList = ref([])
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const showDialog = ref(false)
const showDetail = ref(false)
const currentNews = ref({})
const formRef = ref(null)

const form = ref({
  title: '',
  content: '',
  coverImage: '',
  author: '管理员'
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const stats = computed(() => {
  const totalViews = newsList.value.reduce((sum, n) => sum + (n.viewCount || 0), 0)
  const today = new Date().toDateString()
  const todayCount = newsList.value.filter(n => new Date(n.createTime).toDateString() === today).length
  const avgViews = newsList.value.length > 0 ? Math.round(totalViews / newsList.value.length) : 0
  return {
    total: newsList.value.length,
    totalViews,
    todayCount,
    avgViews
  }
})

const filteredNews = computed(() => {
  if (!searchKeyword.value) return newsList.value
  const keyword = searchKeyword.value.toLowerCase()
  return newsList.value.filter(n => n.title?.toLowerCase().includes(keyword))
})

onMounted(() => {
  loadNews()
})

const loadNews = async () => {
  const res = await getNewsList({ page: currentPage.value, size: pageSize.value })
  newsList.value = res.data.records || res.data
  total.value = res.data.total || newsList.value.length
}

const handleSearch = () => {
  currentPage.value = 1
}

const viewNews = (row) => {
  currentNews.value = row
  showDetail.value = true
}

const submitNews = async () => {
  try {
    await formRef.value.validate()
    await addNews(form.value)
    ElMessage.success('发布成功')
    showDialog.value = false
    form.value = { title: '', content: '', coverImage: '', author: '管理员' }
    loadNews()
  } catch (e) {
    // validation failed
  }
}

const deleteNewsItem = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除资讯 "${row.title}" 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteNews(row.id)
    ElMessage.success('删除成功')
    loadNews()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}
</script>

<style scoped>
.admin-news {
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

.stat-icon.views {
  background: linear-gradient(135deg, #f093fb, #f5576c);
}

.stat-icon.today {
  background: linear-gradient(135deg, #11998e, #38ef7d);
}

.stat-icon.avg {
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

.news-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.news-image {
  width: 80px;
  height: 60px;
  border-radius: 8px;
  flex-shrink: 0;
}

.image-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #c0c4cc;
  font-size: 24px;
}

.news-detail {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.news-title {
  font-weight: 500;
  color: #303133;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 200px;
}

.news-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: #909399;
}

.author {
  display: flex;
  align-items: center;
  gap: 4px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.news-detail-dialog {
  padding: 10px;
}

.detail-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.detail-meta {
  display: flex;
  gap: 20px;
  color: #909399;
  font-size: 14px;
  margin-bottom: 16px;
}

.detail-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.detail-cover {
  width: 100%;
  max-height: 300px;
  border-radius: 8px;
  margin-bottom: 16px;
}

.detail-content {
  line-height: 1.8;
  color: #606266;
  white-space: pre-wrap;
}
</style>
