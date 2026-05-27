<template>
  <div class="employer-workbench">
    <div class="workbench-tips">
      <h3>欢迎进入雇主工作台</h3>
      <p>请点击左侧菜单选择需要操作的功能</p>
    </div>
  </div>
</template>

<script setup>
// 空逻辑
</script>

<style scoped>
.employer-workbench {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}
.workbench-tips {
  text-align: center;
  color: #666;
}
.workbench-tips h3 {
  font-size: 18px;
  color: #333;
  margin-bottom: 10px;
}
</style>