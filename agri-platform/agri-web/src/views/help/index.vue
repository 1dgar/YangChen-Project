<template>
  <div class="help-center">
    <div class="help-header">
      <div class="container">
        <h1>帮助中心</h1>
        <p>快速了解平台功能，解答您的疑问</p>
      </div>
    </div>
    
    <div class="container">
      <div class="help-content">
        <div class="help-tabs">
          <div 
            v-for="tab in tabs" 
            :key="tab.key" 
            :class="['tab-item', { active: activeTab === tab.key }]"
            @click="activeTab = tab.key"
          >
            <el-icon><component :is="tab.icon" /></el-icon>
            <span>{{ tab.name }}</span>
          </div>
        </div>
        
        <div class="help-body">
          <div v-if="activeTab === 'guide'" class="guide-section">
            <div class="guide-grid">
              <div class="guide-card" v-for="guide in guides" :key="guide.title">
                <div class="guide-icon">
                  <el-icon :size="32"><component :is="guide.icon" /></el-icon>
                </div>
                <h3>{{ guide.title }}</h3>
                <p>{{ guide.desc }}</p>
                <ul class="guide-steps">
                  <li v-for="(step, idx) in guide.steps" :key="idx">{{ step }}</li>
                </ul>
              </div>
            </div>
          </div>
          
          <div v-if="activeTab === 'faq'" class="faq-section">
            <el-collapse v-model="activeFaq" accordion>
              <el-collapse-item 
                v-for="(item, index) in faqList" 
                :key="index" 
                :name="index"
              >
                <template #title>
                  <div class="faq-title">
                    <el-icon><QuestionFilled /></el-icon>
                    <span>{{ item.question }}</span>
                  </div>
                </template>
                <div class="faq-answer" v-html="item.answer"></div>
              </el-collapse-item>
            </el-collapse>
          </div>
          
          <div v-if="activeTab === 'contact'" class="contact-section">
            <div class="contact-cards">
              <div class="contact-card">
                <el-icon :size="40"><Service /></el-icon>
                <h3>在线客服</h3>
                <p>点击右下角AI助手图标</p>
                <p class="contact-desc">智能解答您的问题</p>
              </div>
              <div class="contact-card">
                <el-icon :size="40"><Phone /></el-icon>
                <h3>客服热线</h3>
                <p class="phone">400-888-8888</p>
                <p class="contact-desc">工作日 9:00-18:00</p>
              </div>
              <div class="contact-card">
                <el-icon :size="40"><Message /></el-icon>
                <h3>邮箱联系</h3>
                <p>support@agri-platform.com</p>
                <p class="contact-desc">24小时内回复</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { 
  QuestionFilled, Service, Phone, Message,
  User, Goods, Document, Star, Setting
} from '@element-plus/icons-vue'

const activeTab = ref('guide')
const activeFaq = ref(0)

const tabs = [
  { key: 'guide', name: '使用指南', icon: Setting },
  { key: 'faq', name: '常见问题', icon: QuestionFilled },
  { key: 'contact', name: '联系我们', icon: Service }
]

const guides = [
  {
    title: '注册与登录',
    icon: User,
    desc: '快速注册成为平台用户',
    steps: [
      '点击右上角"登录/注册"按钮',
      '选择"注册"标签，填写用户信息',
      '选择用户类型：农户或采购商',
      '完成注册后即可登录使用'
    ]
  },
  {
    title: '发布产品',
    icon: Goods,
    desc: '农户发布农产品信息',
    steps: [
      '登录后进入"个人中心"',
      '点击"我的产品"→"发布产品"',
      '填写产品名称、分类、价格等信息',
      '上传产品图片，提交发布'
    ]
  },
  {
    title: '发布供需',
    icon: Document,
    desc: '发布供应或求购信息',
    steps: [
      '进入"供需信息"页面',
      '点击"发布信息"按钮',
      '选择类型：供应或求购',
      '填写详细信息并提交'
    ]
  },
  {
    title: '收藏与关注',
    icon: Star,
    desc: '收藏感兴趣的产品',
    steps: [
      '浏览产品详情页',
      '点击"收藏"按钮',
      '在"个人中心"查看收藏列表',
      '随时查看收藏的产品动态'
    ]
  }
]

const faqList = [
  {
    question: '如何注册成为平台用户？',
    answer: '点击页面右上角的"登录/注册"按钮，在弹出的对话框中选择"注册"标签，填写用户名、密码、手机号等信息，选择用户类型（农户或采购商），点击"注册"按钮即可完成注册。'
  },
  {
    question: '农户和采购商有什么区别？',
    answer: '<strong>农户：</strong>可以发布农产品信息、供应信息，管理自己的产品。<br><strong>采购商：</strong>可以发布求购信息，浏览和收藏农产品，与农户进行对接。'
  },
  {
    question: '如何发布农产品？',
    answer: '登录后进入"个人中心"，点击左侧菜单"我的产品"，然后点击"发布产品"按钮，填写产品名称、分类、价格、库存、描述等信息，上传产品图片，点击"提交"即可发布。'
  },
  {
    question: '如何发布供需信息？',
    answer: '进入"供需信息"页面，点击页面上的"发布信息"按钮，选择发布类型（供应或求购），填写标题、分类、数量、价格、联系方式等信息，点击"提交"即可发布。'
  },
  {
    question: '如何联系产品发布者？',
    answer: '在产品详情页或供需详情页，可以查看发布者的联系方式（电话、地址等），您可以直接拨打电话或通过其他方式联系对方。'
  },
  {
    question: '如何修改个人信息？',
    answer: '登录后进入"个人中心"，点击左侧菜单"个人信息"，在此页面可以修改您的头像、昵称、手机号、地址等个人信息，修改完成后点击"保存"按钮。'
  },
  {
    question: '忘记密码怎么办？',
    answer: '目前平台暂不支持在线找回密码，如需重置密码，请联系客服人员协助处理。'
  },
  {
    question: '如何使用AI助手？',
    answer: '点击页面右下角的绿色"AI助手"按钮，即可打开智能助手对话框。您可以向AI助手咨询农产品种植技术、市场行情、平台使用等问题，AI助手会智能回答您的问题。'
  },
  {
    question: '发布的信息可以修改或删除吗？',
    answer: '可以。登录后进入"个人中心"，在"我的产品"或"我的供需"页面，找到需要修改或删除的信息，点击相应的操作按钮即可。'
  },
  {
    question: '平台收费吗？',
    answer: '平台基础功能完全免费，包括注册、发布产品、发布供需信息、浏览信息等。平台旨在为农户和采购商提供免费的对接服务。'
  }
]
</script>

<style scoped>
.help-center {
  min-height: 100vh;
  background: #f5f7fa;
}

.help-header {
  background: linear-gradient(135deg, #4CAF50 0%, #2e7d32 100%);
  color: white;
  padding: 60px 0;
  text-align: center;
}

.help-header h1 {
  margin: 0 0 12px 0;
  font-size: 36px;
}

.help-header p {
  margin: 0;
  font-size: 16px;
  opacity: 0.9;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.help-content {
  background: #fff;
  border-radius: 16px;
  margin: -40px 0 40px 0;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.help-tabs {
  display: flex;
  border-bottom: 1px solid #f0f0f0;
  padding: 0 24px;
}

.tab-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 20px 32px;
  cursor: pointer;
  color: #666;
  font-size: 15px;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
}

.tab-item:hover {
  color: #4CAF50;
}

.tab-item.active {
  color: #4CAF50;
  border-bottom-color: #4CAF50;
  font-weight: 500;
}

.help-body {
  padding: 32px;
}

.guide-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.guide-card {
  padding: 24px;
  background: #f8faf8;
  border-radius: 12px;
  transition: all 0.3s;
}

.guide-card:hover {
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.15);
}

.guide-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  background: linear-gradient(135deg, #4CAF50 0%, #2e7d32 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-bottom: 16px;
}

.guide-card h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #333;
}

.guide-card p {
  margin: 0 0 16px 0;
  color: #666;
  font-size: 14px;
}

.guide-steps {
  margin: 0;
  padding-left: 20px;
}

.guide-steps li {
  margin-bottom: 8px;
  color: #666;
  font-size: 14px;
  line-height: 1.6;
}

.faq-section {
  max-width: 900px;
  margin: 0 auto;
}

.faq-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 15px;
  font-weight: 500;
}

.faq-title .el-icon {
  color: #4CAF50;
}

.faq-answer {
  padding: 16px 20px;
  background: #f8faf8;
  border-radius: 8px;
  line-height: 1.8;
  color: #666;
}

.contact-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  max-width: 900px;
  margin: 0 auto;
}

.contact-card {
  text-align: center;
  padding: 40px 24px;
  background: #f8faf8;
  border-radius: 12px;
  transition: all 0.3s;
}

.contact-card:hover {
  background: #e8f5e9;
}

.contact-card .el-icon {
  color: #4CAF50;
  margin-bottom: 16px;
}

.contact-card h3 {
  margin: 0 0 12px 0;
  font-size: 18px;
  color: #333;
}

.contact-card p {
  margin: 0 0 4px 0;
  color: #666;
  font-size: 14px;
}

.contact-card .phone {
  font-size: 20px;
  font-weight: 600;
  color: #4CAF50;
}

.contact-card .contact-desc {
  color: #999;
  font-size: 13px;
  margin-top: 8px;
}

@media (max-width: 992px) {
  .guide-grid {
    grid-template-columns: 1fr;
  }
  
  .contact-cards {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .help-header {
    padding: 40px 0;
  }
  
  .help-header h1 {
    font-size: 28px;
  }
  
  .tab-item {
    padding: 16px 20px;
    font-size: 14px;
  }
  
  .help-body {
    padding: 20px;
  }
}
</style>
