import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

function loadFromStorage(key, fallback = null) {
    const raw = localStorage.getItem(key)
    if (!raw) return fallback
    try { return JSON.parse(raw) } catch { return raw }
}

export const useAuthStore = defineStore('auth', () => {
    const token = ref(loadFromStorage('token', ''))
    const userInfo = ref(loadFromStorage('userInfo', {}))
    const roleType = ref(loadFromStorage('roleType', -1))

    const isLoggedIn = computed(() => !!token.value)
    const isAdmin = computed(() => roleType.value === 0)
    const isEmployer = computed(() => roleType.value === 1)
    const isSeeker = computed(() => roleType.value === 2)
    const userId = computed(() => userInfo.value.id || null)
    const username = computed(() => userInfo.value.username || userInfo.value.realName || '')

    function login(data) {
        token.value = data.token
        roleType.value = data.roleType
        userInfo.value = {
            id: data.id,
            username: data.username,
            realName: data.realName,
            phone: data.phone,
            email: data.email,
            roleType: data.roleType
        }
        localStorage.setItem('token', token.value)
        localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
        localStorage.setItem('roleType', roleType.value)
    }

    function updateProfile(partial) {
        userInfo.value = { ...userInfo.value, ...partial }
        localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    }

    function logout() {
        token.value = ''
        userInfo.value = {}
        roleType.value = -1
        localStorage.clear()
    }

    return {
        token, userInfo, roleType,
        isLoggedIn, isAdmin, isEmployer, isSeeker,
        userId, username,
        login, updateProfile, logout
    }
})
