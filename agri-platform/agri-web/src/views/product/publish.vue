<template>
  <div class="publish-page">
    <div class="page-header">
      <div class="container">
        <div class="breadcrumb">
          <router-link to="/products">农产品管理</router-link>
          <el-icon><ArrowRight /></el-icon>
          <span>发布产品</span>
        </div>
        <h2>发布农产品</h2>
        <p>填写产品信息，快速发布到平台</p>
      </div>
    </div>

    <div class="container">
      <div class="publish-form-wrapper">
        <el-form :model="form" :rules="rules" ref="formRef" label-position="top" class="publish-form">
          <!-- 基本信息 -->
          <div class="form-section">
            <div class="section-title">
              <el-icon><Goods /></el-icon>
              <span>基本信息</span>
            </div>
            <div class="form-grid">
              <el-form-item label="产品名称" prop="name">
                <el-input v-model="form.name" placeholder="请输入产品名称" maxlength="50" show-word-limit />
              </el-form-item>
              <el-form-item label="产品分类" prop="category">
                <el-select v-model="form.category" placeholder="选择分类" style="width: 100%">
                  <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
                </el-select>
              </el-form-item>
            </div>
            <el-form-item label="产品描述" prop="description">
              <el-input type="textarea" v-model="form.description" rows="3" placeholder="描述产品的特点、品质等信息" maxlength="500" show-word-limit />
            </el-form-item>
            <el-form-item label="产品图片">
              <div class="image-upload-panel">
                <el-upload
                  class="image-uploader"
                  :show-file-list="false"
                  :http-request="handleImageUpload"
                  :before-upload="beforeImageUpload"
                  :disabled="imageUploading"
                  accept="image/*"
                >
                  <img v-if="form.image" :src="form.image" alt="产品图片预览" class="preview-image" />
                  <div v-else class="upload-placeholder">
                    <el-icon class="upload-icon"><Plus /></el-icon>
                    <span>{{ imageUploading ? '上传中...' : '上传产品图片' }}</span>
                    <small>支持 jpg/png/webp，最大 5MB</small>
                  </div>
                </el-upload>
                <div class="upload-actions">
                  <el-button v-if="form.image" size="small" @click="clearImage">移除图片</el-button>
                  <span v-else class="upload-tip">未上传时将显示“暂无图片”</span>
                </div>
              </div>
            </el-form-item>
          </div>

          <!-- 价格库存 -->
          <div class="form-section">
            <div class="section-title">
              <el-icon><PriceTag /></el-icon>
              <span>价格与库存</span>
            </div>
            <div class="form-grid three-cols">
              <el-form-item label="价格" prop="price">
                <el-input-number v-model="form.price" :min="0" :precision="2" :step="0.1" style="width: 100%" placeholder="0.00" />
              </el-form-item>
              <el-form-item label="单位" prop="unit">
                <el-select v-model="form.unit" placeholder="选择单位" style="width: 100%" allow-create filterable>
                  <el-option label="斤" value="斤" />
                  <el-option label="公斤" value="公斤" />
                  <el-option label="吨" value="吨" />
                  <el-option label="箱" value="箱" />
                  <el-option label="袋" value="袋" />
                  <el-option label="个" value="个" />
                </el-select>
              </el-form-item>
              <el-form-item label="库存" prop="stock">
                <el-input-number v-model="form.stock" :min="0" style="width: 100%" placeholder="0" />
              </el-form-item>
            </div>
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
              发布产品
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
import { ArrowRight, Goods, PriceTag, Phone, Check } from '@element-plus/icons-vue'
import { publishProduct } from '../../api/product'
import { getCategories } from '../../api/market'
import { uploadImage } from '../../api/file'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const imageUploading = ref(false)
const categories = ref([])

const form = reactive({
  name: '',
  category: '',
  price: 0,
  unit: '斤',
  stock: 0,
  description: '',
  image: '',
  contactName: '',
  contactPhone: '',
  contactAddress: ''
})

const rules = {
  name: [{ required: true, message: '请输入产品名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  unit: [{ required: true, message: '请选择单位', trigger: 'change' }]
}

onMounted(async () => {
  const res = await getCategories()
  categories.value = res.data
})

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

const handleImageUpload = async ({ file }) => {
  imageUploading.value = true
  try {
    const res = await uploadImage(file)
    form.image = res.data
    ElMessage.success('图片上传成功')
  } catch (error) {
    ElMessage.error(error.message || '图片上传失败')
  } finally {
    imageUploading.value = false
  }
}

const clearImage = () => {
  form.image = ''
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
  } catch (e) {
    return
  }
  
  loading.value = true
  try {
    await publishProduct(form)
    ElMessage.success('发布成功')
    router.push('/products')
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

.form-grid.three-cols {
  grid-template-columns: repeat(3, 1fr);
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

.image-upload-panel {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.image-uploader {
  width: 100%;
}

.publish-form :deep(.image-uploader .el-upload) {
  width: 100%;
  border: 1px dashed #c8d6c9;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: border-color 0.3s;
}

.publish-form :deep(.image-uploader .el-upload:hover) {
  border-color: #4CAF50;
}

.preview-image {
  display: block;
  width: 100%;
  height: 240px;
  object-fit: cover;
  background: #f6f8f6;
}

.upload-placeholder {
  height: 240px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #6b7280;
  background: linear-gradient(180deg, #fafcf9 0%, #f1f6f0 100%);
}

.upload-icon {
  font-size: 28px;
  color: #4CAF50;
}

.upload-placeholder small {
  color: #9aa29c;
}

.upload-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.upload-tip {
  font-size: 13px;
  color: #999;
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
  .form-grid,
  .form-grid.three-cols {
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
