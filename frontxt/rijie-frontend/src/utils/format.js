// src/utils/format.js
/**
 * 格式化日期为 YYYY-MM-DD
 * @param {String/Date} date - 日期
 * @returns {String}
 */
export const formatDate = (date) => {
    if (!date) return ''
    const newDate = new Date(date)
    const year = newDate.getFullYear()
    const month = (newDate.getMonth() + 1).toString().padStart(2, '0')
    const day = newDate.getDate().toString().padStart(2, '0')
    return `${year}-${month}-${day}`
}

/**
 * 格式化日期时间为 YYYY-MM-DD HH:mm:ss
 * @param {String/Date} date - 日期时间
 * @returns {String}
 */
export const formatDateTime = (date) => {
    if (!date) return ''
    const newDate = new Date(date)
    const year = newDate.getFullYear()
    const month = (newDate.getMonth() + 1).toString().padStart(2, '0')
    const day = newDate.getDate().toString().padStart(2, '0')
    const hour = newDate.getHours().toString().padStart(2, '0')
    const minute = newDate.getMinutes().toString().padStart(2, '0')
    const second = newDate.getSeconds().toString().padStart(2, '0')
    return `${year}-${month}-${day} ${hour}:${minute}:${second}`
}