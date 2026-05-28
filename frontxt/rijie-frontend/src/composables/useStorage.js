import { ref, watch } from 'vue'

/** 安全解析localStorage中的JSON */
function parseStored(key) {
    const raw = localStorage.getItem(key)
    if (!raw) return null
    try { return JSON.parse(raw) } catch { return raw }
}

/**
 * 响应式读取localStorage。写入时同步更新localStorage。
 * @param {string} key - localStorage键名
 * @param {*} defaultValue - 默认值
 */
export function useStorage(key, defaultValue = null) {
    const stored = ref(parseStored(key) ?? defaultValue)

    watch(stored, (val) => {
        if (val === null || val === undefined) {
            localStorage.removeItem(key)
        } else {
            localStorage.setItem(key, typeof val === 'string' ? val : JSON.stringify(val))
        }
    }, { deep: true })

    return stored
}

/**
 * 获取当前登录用户信息（响应式）。
 * 推荐在需要用户信息的组件中使用。
 */
export function useUserInfo() {
    const userInfo = useStorage('userInfo', {})
    const token = useStorage('token', '')
    const roleType = useStorage('roleType', -1)

    const isLoggedIn = () => !!token.value
    const isAdmin = () => roleType.value === 0
    const isEmployer = () => roleType.value === 1
    const isSeeker = () => roleType.value === 2

    return { userInfo, token, roleType, isLoggedIn, isAdmin, isEmployer, isSeeker }
}
