<template>
  <div class="ai-assistant">
    <div class="ai-trigger" @click="toggleChat" v-if="!isOpen">
      <el-icon :size="24"><ChatDotRound /></el-icon>
      <span class="trigger-text">AI助手</span>
    </div>
    
    <transition name="slide-up">
      <div 
        class="ai-chat-window" 
        v-if="isOpen"
        :class="{ 'is-maximized': isMaximized }"
        :style="!isMaximized ? { width: windowWidth + 'px', height: windowHeight + 'px' } : {}"
      >
        <div class="chat-header">
          <div class="header-left">
            <div class="ai-avatar">🌾</div>
            <div class="header-info">
              <span class="title">小农助手</span>
              <span class="status">在线</span>
            </div>
          </div>
          <div class="header-actions">
            <el-tooltip content="最大化" placement="top" v-if="!isMaximized">
              <el-icon class="action-btn" @click="toggleMaximize"><FullScreen /></el-icon>
            </el-tooltip>
            <el-tooltip content="还原" placement="top" v-else>
              <el-icon class="action-btn" @click="toggleMaximize"><CopyDocument /></el-icon>
            </el-tooltip>
            <el-tooltip content="清空对话" placement="top">
              <el-icon class="action-btn" @click="clearMessages"><Delete /></el-icon>
            </el-tooltip>
            <el-tooltip content="关闭" placement="top">
              <el-icon class="action-btn close" @click="toggleChat"><Close /></el-icon>
            </el-tooltip>
          </div>
        </div>
        
        <div class="chat-body">
          <div class="chat-messages" ref="messagesContainer">
            <div class="welcome-message" v-if="messages.length === 0">
              <div class="welcome-icon">🌾</div>
              <h3>你好，我是小农助手</h3>
              <p>我可以帮你解答农产品交易、种植技术、市场行情等问题</p>
              
              <div class="quick-section">
                <div class="quick-title">
                  <el-icon><Opportunity /></el-icon>
                  热门问题
                </div>
                <div class="quick-grid">
                  <div 
                    class="quick-item" 
                    v-for="(q, index) in quickQuestions" 
                    :key="index" 
                    @click="askQuickQuestion(q)"
                  >
                    <el-icon class="quick-icon"><QuestionFilled /></el-icon>
                    <span>{{ q }}</span>
                  </div>
                </div>
              </div>

              <div class="feature-cards">
                <div class="feature-card" @click="askQuickQuestion('推荐一些适合春季种植的蔬菜')">
                  <el-icon :size="24"><Sunny /></el-icon>
                  <span>春季种植</span>
                </div>
                <div class="feature-card" @click="askQuickQuestion('如何提高农产品的销售价格？')">
                  <el-icon :size="24"><TrendCharts /></el-icon>
                  <span>销售技巧</span>
                </div>
                <div class="feature-card" @click="askQuickQuestion('农产品如何保鲜储存？')">
                  <el-icon :size="24"><Box /></el-icon>
                  <span>保鲜储存</span>
                </div>
                <div class="feature-card" @click="askQuickQuestion('常见病虫害防治方法')">
                  <el-icon :size="24"><FirstAidKit /></el-icon>
                  <span>病虫害防治</span>
                </div>
              </div>
            </div>
            
            <div class="message" v-for="(msg, index) in messages" :key="index" :class="msg.role">
              <div class="message-avatar">
                <el-icon v-if="msg.role === 'user'"><User /></el-icon>
                <span v-else>🌾</span>
              </div>
              <div class="message-wrapper">
                <div class="message-header">
                  <span class="message-time">{{ formatTime(msg.time) }}</span>
                  <el-tooltip content="复制" placement="top" v-if="msg.role === 'assistant'">
                    <el-icon class="copy-btn" @click="copyMessage(msg.content)"><DocumentCopy /></el-icon>
                  </el-tooltip>
                </div>
                <div class="message-content">
                  <div class="message-text" v-html="formatMessage(msg.content)"></div>
                </div>
              </div>
            </div>
            
            <div class="message assistant" v-if="loading">
              <div class="message-avatar">🌾</div>
              <div class="message-wrapper">
                <div class="message-content">
                  <div class="typing-indicator">
                    <span></span>
                    <span></span>
                    <span></span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="chat-input">
          <div class="input-wrapper">
            <el-input
              v-model="inputMessage"
              placeholder="输入你的问题，按Enter发送..."
              @keyup.enter="sendMessage"
              :disabled="loading"
              type="textarea"
              :rows="1"
              :autosize="{ minRows: 1, maxRows: 4 }"
              resize="none"
            />
            <el-button 
              type="primary" 
              @click="sendMessage" 
              :loading="loading"
              :disabled="!inputMessage.trim()"
              circle
              class="send-btn"
            >
              <el-icon><Position /></el-icon>
            </el-button>
          </div>
          <div class="input-tips">
            <span>按 Enter 发送，Shift + Enter 换行</span>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted, onUnmounted, watch } from 'vue'
import { chat } from '../api/ai'
import { 
  ChatDotRound, Close, User, Position, Delete, FullScreen,
  DocumentCopy, QuestionFilled, Opportunity, Sunny, TrendCharts,
  Box, FirstAidKit, CopyDocument
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const isOpen = ref(false)
const isMaximized = ref(false)
const inputMessage = ref('')
const messages = ref([])
const loading = ref(false)
const messagesContainer = ref(null)

const windowWidth = ref(480)
const windowHeight = ref(650)

const quickQuestions = [
  '如何种植有机蔬菜？',
  '当前农产品市场行情如何？',
  '如何发布供应信息？',
  '农产品如何保鲜？',
  '推荐一些适合春季种植的蔬菜',
  '如何提高农产品产量？'
]

const STORAGE_KEY = 'ai_chat_history'

const loadHistory = () => {
  try {
    const saved = localStorage.getItem(STORAGE_KEY)
    if (saved) {
      messages.value = JSON.parse(saved)
    }
  } catch (e) {
    console.error('加载聊天记录失败', e)
  }
}

const saveHistory = () => {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(messages.value.slice(-50)))
  } catch (e) {
    console.error('保存聊天记录失败', e)
  }
}

watch(messages, () => {
  saveHistory()
}, { deep: true })

const toggleChat = () => {
  isOpen.value = !isOpen.value
}

const toggleMaximize = () => {
  isMaximized.value = !isMaximized.value
}

const handleOpenEvent = () => {
  isOpen.value = true
}

onMounted(() => {
  window.addEventListener('open-ai-assistant', handleOpenEvent)
  loadHistory()
})

onUnmounted(() => {
  window.removeEventListener('open-ai-assistant', handleOpenEvent)
})

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

const formatMessage = (content) => {
  if (!content) return ''
  return content
    .replace(/\n/g, '<br>')
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    .replace(/\*(.*?)\*/g, '<em>$1</em>')
    .replace(/`(.*?)`/g, '<code>$1</code>')
    .replace(/(\d+)\.\s/g, '<br>$1. ')
    .replace(/[-•]\s/g, '<br>• ')
}

const copyMessage = async (content) => {
  try {
    await navigator.clipboard.writeText(content)
    ElMessage.success('已复制到剪贴板')
  } catch (e) {
    ElMessage.error('复制失败')
  }
}

const clearMessages = () => {
  messages.value = []
  localStorage.removeItem(STORAGE_KEY)
  ElMessage.success('对话已清空')
}

const sendMessage = async () => {
  if (!inputMessage.value.trim() || loading.value) return
  
  const userMessage = inputMessage.value.trim()
  messages.value.push({ 
    role: 'user', 
    content: userMessage,
    time: new Date().toISOString()
  })
  inputMessage.value = ''
  scrollToBottom()
  
  loading.value = true
  try {
    const history = JSON.stringify(messages.value.slice(-6).map(m => ({
      role: m.role,
      content: m.content
    })))
    const res = await chat(userMessage, history)
    messages.value.push({ 
      role: 'assistant', 
      content: res.data,
      time: new Date().toISOString()
    })
  } catch (e) {
    messages.value.push({ 
      role: 'assistant', 
      content: '抱歉，我暂时无法回答您的问题，请稍后再试。如果问题持续存在，请联系客服。',
      time: new Date().toISOString()
    })
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

const askQuickQuestion = (question) => {
  inputMessage.value = question
  sendMessage()
}
</script>

<style scoped>
.ai-assistant {
  position: fixed;
  right: 24px;
  bottom: 24px;
  z-index: 9999;
}

.ai-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 24px;
  background: linear-gradient(135deg, #4CAF50 0%, #2e7d32 100%);
  color: white;
  border-radius: 28px;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(76, 175, 80, 0.4);
  transition: all 0.3s ease;
}

.ai-trigger:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(76, 175, 80, 0.5);
}

.trigger-text {
  font-size: 15px;
  font-weight: 600;
}

.ai-chat-window {
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.18);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  transition: all 0.3s ease;
}

.ai-chat-window.is-maximized {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100% !important;
  height: 100% !important;
  border-radius: 0;
}

.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: linear-gradient(135deg, #4CAF50 0%, #2e7d32 100%);
  color: white;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ai-avatar {
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
}

.header-info {
  display: flex;
  flex-direction: column;
}

.title {
  font-size: 17px;
  font-weight: 600;
}

.status {
  font-size: 12px;
  opacity: 0.85;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s;
  font-size: 16px;
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.15);
}

.action-btn.close:hover {
  background: rgba(255, 0, 0, 0.2);
}

.chat-body {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: linear-gradient(180deg, #f8faf8 0%, #f0f4f0 100%);
}

.welcome-message {
  text-align: center;
  padding: 24px 16px;
}

.welcome-icon {
  font-size: 56px;
  margin-bottom: 16px;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

.welcome-message h3 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 20px;
}

.welcome-message p {
  margin: 0 0 24px 0;
  color: #666;
  font-size: 14px;
}

.quick-section {
  text-align: left;
  margin-bottom: 20px;
}

.quick-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #888;
  margin-bottom: 12px;
  font-weight: 500;
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
}

.quick-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 14px;
  background: #fff;
  border-radius: 10px;
  font-size: 13px;
  color: #333;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #e8f5e9;
  text-align: left;
}

.quick-item:hover {
  background: #e8f5e9;
  border-color: #4CAF50;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.15);
}

.quick-icon {
  color: #4CAF50;
  font-size: 14px;
}

.feature-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-top: 16px;
}

.feature-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px 12px;
  background: #fff;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #f0f0f0;
}

.feature-card:hover {
  background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
  border-color: #4CAF50;
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(76, 175, 80, 0.2);
}

.feature-card .el-icon {
  color: #4CAF50;
}

.feature-card span {
  font-size: 12px;
  color: #666;
}

.message {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 18px;
}

.message.user .message-avatar {
  background: linear-gradient(135deg, #4CAF50 0%, #2e7d32 100%);
  color: white;
}

.message.assistant .message-avatar {
  background: #e8f5e9;
}

.message-wrapper {
  max-width: 80%;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.message-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.message.user .message-header {
  flex-direction: row-reverse;
}

.message-time {
  font-size: 11px;
  color: #999;
}

.copy-btn {
  cursor: pointer;
  font-size: 12px;
  color: #999;
  transition: color 0.2s;
}

.copy-btn:hover {
  color: #4CAF50;
}

.message-content {
  display: flex;
}

.message-text {
  padding: 12px 16px;
  border-radius: 16px;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
}

.message.user .message-text {
  background: linear-gradient(135deg, #4CAF50 0%, #2e7d32 100%);
  color: white;
  border-bottom-right-radius: 4px;
}

.message.assistant .message-text {
  background: #fff;
  color: #333;
  border-bottom-left-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.message.assistant .message-text :deep(code) {
  background: #f5f5f5;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: monospace;
  font-size: 13px;
}

.message.assistant .message-text :deep(strong) {
  color: #2e7d32;
}

.typing-indicator {
  display: flex;
  gap: 5px;
  padding: 12px 16px;
  background: #fff;
  border-radius: 16px;
  width: fit-content;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background: #4CAF50;
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(1) { animation-delay: 0s; }
.typing-indicator span:nth-child(2) { animation-delay: 0.2s; }
.typing-indicator span:nth-child(3) { animation-delay: 0.4s; }

@keyframes typing {
  0%, 60%, 100% { transform: translateY(0); opacity: 0.6; }
  30% { transform: translateY(-8px); opacity: 1; }
}

.chat-input {
  padding: 16px 20px;
  background: #fff;
  border-top: 1px solid #f0f0f0;
  flex-shrink: 0;
}

.input-wrapper {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.input-wrapper :deep(.el-textarea__inner) {
  border-radius: 16px;
  padding: 12px 16px;
  font-size: 14px;
  resize: none;
  border: 2px solid #e8f5e9;
  transition: border-color 0.3s;
}

.input-wrapper :deep(.el-textarea__inner:focus) {
  border-color: #4CAF50;
}

.send-btn {
  width: 44px;
  height: 44px;
  flex-shrink: 0;
}

.input-tips {
  margin-top: 8px;
  font-size: 11px;
  color: #999;
  text-align: center;
}

.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s ease;
}

.slide-up-enter-from,
.slide-up-leave-to {
  opacity: 0;
  transform: translateY(20px) scale(0.95);
}

@media (max-width: 768px) {
  .ai-assistant {
    right: 12px;
    bottom: 12px;
  }
  
  .ai-chat-window {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    width: 100% !important;
    height: 100% !important;
    border-radius: 0;
  }
  
  .feature-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
