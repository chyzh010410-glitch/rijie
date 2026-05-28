<template>
  <div class="ai-chat-wrapper">
    <!-- 浮动按钮 -->
    <div class="ai-chat-float-btn" @click="toggleChat" v-show="!visible">
      <span>AI客服</span>
    </div>

    <!-- 聊天窗口 -->
    <transition name="slide">
      <div class="ai-chat-panel" v-show="visible">
        <div class="chat-header">
          <span>AI智能客服 · 小兼</span>
          <div>
            <el-button link @click="chat.clearHistory()" style="color:#fff;font-size:12px;margin-right:8px" title="清空对话">清空</el-button>
            <el-button link @click="visible = false" style="color:#fff;font-size:18px">×</el-button>
          </div>
        </div>
        <div class="chat-body" ref="chatBody">
          <div v-for="(msg, i) in messages" :key="i" :class="['msg', msg.role]">
            <div class="msg-content">{{ msg.content }}</div>
          </div>
          <div v-if="loading" class="msg assistant">
            <div class="msg-content typing">小兼正在思考...</div>
          </div>
        </div>
        <div class="chat-footer">
          <el-input
            v-model="input"
            placeholder="输入您的问题，如：朝阳区日结餐饮岗位"
            @keyup.enter="send"
            :disabled="loading"
          >
            <template #append>
              <el-button @click="send" :disabled="loading || !input.trim()">发送</el-button>
            </template>
          </el-input>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, nextTick, watch } from 'vue'
import { ElButton, ElInput, ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { useChatStore } from '@/stores/chat'
import axios from 'axios'

const visible = ref(false)
const input = ref('')
const loading = ref(false)
const chat = useChatStore()
const messages = chat.messages
const chatBody = ref(null)

const toggleChat = () => { visible.value = true }

watch(visible, async (v) => {
  if (v) await nextTick()
  scrollBottom()
})

function scrollBottom() {
  nextTick(() => {
    if (chatBody.value) chatBody.value.scrollTop = chatBody.value.scrollHeight
  })
}

async function send() {
  const text = input.value.trim()
  if (!text || loading.value) return

  chat.addMessage({ role: 'user', content: text })
  input.value = ''
  loading.value = true
  scrollBottom()

  try {
    const auth = useAuthStore()
    if (!auth.token) {
      chat.addMessage({ role: 'assistant', content: '请先登录后再使用智能客服功能~' })
      loading.value = false
      return
    }
    const history = messages.value.slice(0, -1).map(m => ({ role: m.role, content: m.content }))
    const { data } = await axios.post('/ai/chat', { message: text, token: auth.token, history })
    chat.addMessage({ role: 'assistant', content: data.reply })
  } catch (err) {
    console.error('[AI客服] 请求失败:', err)
    const msg = err.response ? `服务异常(${err.response.status})，请稍后重试` : 'AI服务未启动，请确认已运行 python main.py'
    chat.addMessage({ role: 'assistant', content: msg })
  } finally {
    loading.value = false
    scrollBottom()
  }
}
</script>

<style scoped>
.ai-chat-wrapper { position: fixed; right: 20px; bottom: 20px; z-index: 1000; }

.ai-chat-float-btn {
  width: 56px; height: 56px; border-radius: 50%;
  background: linear-gradient(135deg, #1e88e5, #1565c0);
  color: #fff; display: flex; align-items: center; justify-content: center;
  cursor: pointer; box-shadow: 0 4px 15px rgba(30,136,229,0.4);
  font-size: 12px; font-weight: 600; transition: transform 0.2s;
}
.ai-chat-float-btn:hover { transform: scale(1.1); }

.ai-chat-panel {
  width: 380px; height: 520px; background: #fff; border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0,0,0,0.15); display: flex; flex-direction: column;
  overflow: hidden;
}

.chat-header {
  background: linear-gradient(90deg, #1e88e5, #1565c0);
  color: #fff; padding: 12px 16px; display: flex;
  justify-content: space-between; align-items: center; font-weight: 600;
}

.chat-body { flex: 1; overflow-y: auto; padding: 16px; background: #f7f8fa; }
.msg { margin-bottom: 12px; display: flex; }
.msg.user { justify-content: flex-end; }
.msg.user .msg-content {
  background: #1e88e5; color: #fff; border-radius: 12px 12px 4px 12px;
}
.msg.assistant .msg-content {
  background: #fff; color: #333; border-radius: 12px 12px 12px 4px;
  border: 1px solid #eee;
}
.msg-content {
  max-width: 80%; padding: 10px 14px; font-size: 14px;
  line-height: 1.6; white-space: pre-wrap; word-break: break-word;
}
.typing { color: #999; font-style: italic; }

.chat-footer { padding: 10px; border-top: 1px solid #eee; background: #fff; }

.slide-enter-active, .slide-leave-active { transition: all 0.3s ease; }
.slide-enter-from, .slide-leave-to { opacity: 0; transform: translateY(20px); }
</style>
