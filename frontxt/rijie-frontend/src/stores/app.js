import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
    const currentJobId = ref(localStorage.getItem('currentJobId') || '')
    const currentJobName = ref(localStorage.getItem('currentJobName') || '')
    const workStartTime = ref(localStorage.getItem('workStartTime') || '')
    const workEndTime = ref(localStorage.getItem('workEndTime') || '')

    function setCurrentJob({ jobId, jobName, workStartTime: start, workEndTime: end }) {
        currentJobId.value = jobId || ''
        currentJobName.value = jobName || ''
        workStartTime.value = start || ''
        workEndTime.value = end || ''
        if (jobId) localStorage.setItem('currentJobId', jobId)
        if (jobName) localStorage.setItem('currentJobName', jobName)
        if (start) localStorage.setItem('workStartTime', start)
        if (end) localStorage.setItem('workEndTime', end)
    }

    function clearCurrentJob() {
        currentJobId.value = ''
        currentJobName.value = ''
        workStartTime.value = ''
        workEndTime.value = ''
        localStorage.removeItem('currentJobId')
        localStorage.removeItem('currentJobName')
        localStorage.removeItem('workStartTime')
        localStorage.removeItem('workEndTime')
    }

    return { currentJobId, currentJobName, workStartTime, workEndTime, setCurrentJob, clearCurrentJob }
})
