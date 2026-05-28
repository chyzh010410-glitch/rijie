import { defineStore } from 'pinia'
import { ref } from 'vue'

const MAX_HISTORY = 50

export const useChatStore = defineStore('chat', () => {
    const messages = ref(loadHistory())

    function loadHistory() {
        try {
            const raw = localStorage.getItem('chat_history')
            return raw ? JSON.parse(raw) : [
                { role: 'assistant', content: '您好！我是小兼，日结兼职平台的智能客服。您可以问我：\n• "帮我找朝阳区餐饮类的日结"\n• "有没有日薪200以上的快递岗位"\n• "推荐一些适合我的工作"' }
            ]
        } catch { return [] }
    }

    function saveHistory() {
        const arr = messages.value.slice(-MAX_HISTORY)
        localStorage.setItem('chat_history', JSON.stringify(arr))
    }

    function addMessage(msg) {
        messages.value.push(msg)
        saveHistory()
    }

    function clearHistory() {
        messages.value = [
            { role: 'assistant', content: '您好！我是小兼，日结兼职平台的智能客服。请问有什么可以帮您的？' }
        ]
        localStorage.removeItem('chat_history')
    }

    return { messages, addMessage, clearHistory }
})
