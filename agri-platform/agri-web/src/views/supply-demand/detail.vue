<template>
  <div class="supply-demand-detail" v-if="detail">
    <el-card>
      <template #header>
        <div class="header">
          <h2>{{ detail.title }}</h2>
          <el-tag :type="detail.type === 'supply' ? 'success' : 'warning'" size="large">
            {{ detail.type === 'supply' ? '供应' : '求购' }}
          </el-tag>
        </div>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="分类">{{ detail.category }}</el-descriptions-item>
        <el-descriptions-item label="数量">{{ detail.quantity }}</el-descriptions-item>
        <el-descriptions-item label="价格">{{ detail.price }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detail.status === 1 ? 'success' : 'info'">
            {{ detail.status === 1 ? '进行中' : '已结束' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="联系人">{{ detail.contactName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detail.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="联系地址" :span="2">{{ detail.contactAddress }}</el-descriptions-item>
        <el-descriptions-item label="详细内容" :span="2">{{ detail.content }}</el-descriptions-item>
      </el-descriptions>
      <div class="footer">
        <el-button @click="$router.back()">返回</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getSupplyDemandDetail } from '../../api/supplyDemand'

const route = useRoute()
const detail = ref(null)

onMounted(async () => {
  const res = await getSupplyDemandDetail(route.params.id)
  detail.value = res.data
})
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header h2 {
  margin: 0;
}

.footer {
  margin-top: 20px;
  text-align: center;
}
</style>
