<template>
  <!-- 首页只留这一行，引入岗位列表组件，其他啥都不用写 -->
  <JobList />
</template>

<script setup>
// 只需要导入岗位列表组件，无任何其他逻辑
import JobList from '@/views/jobseeker/job/index.vue';
// // 2. 导出组件，并注册Header
// export default {
//   components: {
//     JobList
//   }
// }
</script>

<style scoped>
/* 首页如果有全局样式可以保留，没有就清空，岗位列表的样式在自己的文件里 */
</style>