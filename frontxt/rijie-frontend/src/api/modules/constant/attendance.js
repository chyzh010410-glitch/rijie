// 考勤状态映射：0-正常，1-迟到，2-早退，3-旷工
export const AttendanceStatus = {
  0: { text: '正常', color: 'green' },
  1: { text: '迟到', color: 'orange' },
  2: { text: '早退', color: 'orange' },
  3: { text: '旷工', color: 'red' }
}
// 状态码转文字
export const getStatusText = (status) => {
  return AttendanceStatus[status]?.text || '未知状态'
}
// 状态码转颜色
export const getStatusColor = (status) => {
  return AttendanceStatus[status]?.color || 'gray'
}