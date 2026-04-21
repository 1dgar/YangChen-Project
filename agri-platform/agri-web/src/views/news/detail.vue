<template>
  <div class="news-detail" v-if="news">
    <el-card>
      <template #header>
        <h2>{{ news.title }}</h2>
        <div class="meta">
          <span>作者: {{ news.author }}</span>
          <span>发布时间: {{ news.createTime }}</span>
          <span>浏览量: {{ news.viewCount }}</span>
        </div>
      </template>
      <div class="content">
        <img v-if="news.coverImage" :src="news.coverImage" class="cover-image" />
        <p>{{ news.content }}</p>
      </div>
      <div class="footer">
        <el-button @click="$router.back()">返回</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getNewsDetail } from '../../api/market'

const route = useRoute()
const news = ref(null)

onMounted(async () => {
  const res = await getNewsDetail(route.params.id)
  news.value = res.data
})
</script>

<style scoped>
.meta {
  display: flex;
  gap: 20px;
  color: #909399;
  font-size: 14px;
  margin-top: 10px;
}

.cover-image {
  width: 100%;
  max-height: 400px;
  object-fit: cover;
  margin-bottom: 20px;
}

.content {
  line-height: 1.8;
  font-size: 16px;
}

.footer {
  margin-top: 30px;
  text-align: center;
}
</style>
