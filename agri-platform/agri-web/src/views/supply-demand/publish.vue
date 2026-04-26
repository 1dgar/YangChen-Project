<template>
  <div class="publish-page">
    <div class="page-header">
      <div class="container">
        <div class="breadcrumb">
          <router-link to="/supply-demand">供需信息</router-link>
          <el-icon><ArrowRight /></el-icon>
          <span>发布信息</span>
        </div>
        <h2>发布供需信息</h2>
        <p>填写供需信息，快速发布到平台</p>
      </div>
    </div>

    <div class="container">
      <div class="publish-form-wrapper">
        <el-form :model="form" :rules="rules" ref="formRef" label-position="top" class="publish-form">
          <!-- 基本信息 -->
          <div class="form-section">
            <div class="section-title">
              <el-icon><InfoFilled /></el-icon>
              <span>基本信息</span>
            </div>
            <el-form-item label="信息类型" prop="type">
              <el-radio-group v-model="form.type" size="large">
                <el-radio-button value="supply">
                  <el-icon><Sell /></el-icon>
                  供应
                </el-radio-button>
                <el-radio-button value="demand">
                  <el-icon><ShoppingCart /></el-icon>
                  求购
                </el-radio-button>
              </el-radio-group>
            </el-form-item>
            <div class="form-grid">
              <el-form-item label="标题" prop="title">
                <el-input v-model="form.title" placeholder="请输入标题" maxlength="50" show-word-limit />
              </el-form-item>
              <el-form-item label="产品分类" prop="category">
                <el-select v-model="form.category" placeholder="选择分类" style="width: 100%">
                  <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
                </el-select>
              </el-form-item>
            </div>
          </div>

          <!-- 交易信息 -->
          <div class="form-section">
            <div class="section-title">
              <el-icon><PriceTag /></el-icon>
              <span>交易信息</span>
            </div>
            <div class="form-grid">
              <el-form-item label="数量" prop="quantity">
                <el-input v-model="form.quantity" placeholder="如：1000斤" />
              </el-form-item>
              <el-form-item label="价格" prop="price">
                <el-input v-model="form.price" placeholder="如：面议、2元/斤" />
              </el-form-item>
            </div>
            <el-form-item label="详细内容" prop="content">
              <el-input type="textarea" v-model="form.content" rows="4" placeholder="描述产品的规格、品质要求、交货时间等详细信息" maxlength="500" show-word-limit />
            </el-form-item>
          </div>

          <!-- 联系方式 -->
          <div class="form-section">
            <div class="section-title">
              <el-icon><Phone /></el-icon>
              <span>联系方式</span>
            </div>
            <div class="form-grid">
              <el-form-item label="联系人" prop="contactName">
                <el-input v-model="form.contactName" placeholder="请输入联系人姓名" />
              </el-form-item>
              <el-form-item label="联系电话" prop="contactPhone">
                <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
              </el-form-item>
            </div>
            <el-form-item label="联系地址" prop="contactAddress">
              <el-input v-model="form.contactAddress" placeholder="请输入详细地址" />
            </el-form-item>
          </div>

          <!-- 提交按钮 -->
          <div class="form-actions">
            <el-button @click="$router.back()" size="large">取消</el-button>
            <el-button type="primary" @click="handleSubmit" :loading="loading" size="large">
              <el-icon><Check /></el-icon>
              发布信息
            </el-button>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowRight, InfoFilled, PriceTag, Phone, Check, Sell, ShoppingCart } from '@element-plus/icons-vue'
import { publishSupplyDemand } from '../../api/supplyDemand'
import { getCategories } from '../../api/market'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const categories = ref([])

const form = reactive({
  type: 'supply',
  title: '',
  category: '',
  quantity: '',
  price: '',
  content: '',
  contactName: '',
  contactPhone: '',
  contactAddress: '',
  status: 1
})

const rules = {
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

onMounted(async () => {
  const res = await getCategories()
  categories.value = res.data
})

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
  } catch (e) {
    return
  }
  
  loading.value = true
  try {
    await publishSupplyDemand(form)
    ElMessage.success('发布成功')
    router.push('/supply-demand')
  } catch (error) {
    console.error('发布失败:', error)
    if (error.response?.status === 401) {
      ElMessage.error('请先登录')
      router.push('/login')
    } else {
      ElMessage.error(error.response?.data?.message || '发布失败，请重试')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.publish-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #f5faf5 0%, #e8f5e9 50%, #f0f7f0 100%);
}

.page-header {
  background: linear-gradient(135deg, #1a5f2a 0%, #2e7d32 50%, #388e3c 100%);
  color: #fff;
  padding: 32px 0;
  position: relative;
}

.page-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: 
    radial-gradient(ellipse at 20% 30%, rgba(129, 199, 132, 0.2) 0%, transparent 50%),
    radial-gradient(ellipse at 80% 70%, rgba(165, 214, 167, 0.15) 0%, transparent 50%);
  pointer-events: none;
}

.page-header .container {
  position: relative;
  z-index: 1;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  font-size: 14px;
  opacity: 0.85;
}

.breadcrumb a {
  color: rgba(255,255,255,0.85);
  text-decoration: none;
}

.breadcrumb a:hover {
  color: #fff;
}

.page-header h2 {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 600;
}

.page-header p {
  margin: 0;
  opacity: 0.9;
  font-size: 15px;
}

.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 24px;
}

.publish-form-wrapper {
  background: #fff;
  border-radius: 16px;
  padding: 32px;
  margin: -24px auto 40px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.08);
  position: relative;
  z-index: 2;
}

.form-section {
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.form-section:last-of-type {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 17px;
  font-weight: 600;
  color: #1a5f2a;
  margin-bottom: 20px;
}

.section-title .el-icon {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #2e7d32;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.form-grid .el-form-item {
  margin-bottom: 0;
}

.publish-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: #333;
  padding-bottom: 6px;
}

.publish-form :deep(.el-input__wrapper),
.publish-form :deep(.el-select__wrapper),
.publish-form :deep(.el-textarea__inner) {
  border-radius: 8px;
  transition: all 0.3s;
}

.publish-form :deep(.el-input__wrapper:hover),
.publish-form :deep(.el-select__wrapper:hover),
.publish-form :deep(.el-textarea__inner:hover) {
  box-shadow: 0 0 0 1px #4CAF50 inset;
}

.publish-form :deep(.el-input__wrapper.is-focus),
.publish-form :deep(.el-select__wrapper.is-focus),
.publish-form :deep(.el-textarea__inner:focus) {
  box-shadow: 0 0 0 1px #4CAF50 inset !important;
}

.publish-form :deep(.el-radio-button__inner) {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 12px 24px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
}

.form-actions .el-button {
  min-width: 120px;
  border-radius: 8px;
}

.form-actions .el-button--primary {
  background: linear-gradient(135deg, #4CAF50 0%, #388e3c 100%);
  border: none;
}

.form-actions .el-button--primary:hover {
  background: linear-gradient(135deg, #388e3c 0%, #2e7d32 100%);
}

@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
  
  .publish-form-wrapper {
    padding: 20px;
    margin: -16px 16px 24px;
  }
  
  .form-actions {
    flex-direction: column-reverse;
  }
  
  .form-actions .el-button {
    width: 100%;
  }
}
</style>
