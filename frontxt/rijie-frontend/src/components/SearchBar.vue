<template>
  <!-- 外层容器：去掉overflow:hidden，避免裁剪下拉 -->
  <div class="search-form-con" ref="searchBarRef">
    <!-- 职位类型选择区：改用click切换（比hover更稳定） -->
    <div 
      class="position-sel"
      @click="showJobType = !showJobType"
    >
      <span class="sel-text">{{ selectedJobTypeText }}</span>
      <i class="sel-icon">▼</i>
      <!-- 下拉选项：加绝对定位+高层级，避免被遮挡 -->
      <div 
        class="sel-options" 
        v-show="showJobType"
        @click.stop <!-- 阻止点击选项时关闭下拉 -->
      >
        <div class="option-item" @click="selectJobType('', '岗位类型')">全部类型</div>
        <div class="option-item" @click="selectJobType('餐饮', '餐饮')">餐饮</div>
        <div class="option-item" @click="selectJobType('快递', '快递')">快递</div>
        <div class="option-item" @click="selectJobType('家教', '家教')">家教</div>
        <div class="option-item" @click="selectJobType('零售', '零售')">零售</div>
        <div class="option-item" @click="selectJobType('促销', '促销')">促销</div>
        <div class="option-item" @click="selectJobType('安保', '安保')">安保</div>
      </div>
    </div>

    <div class="ipt-wrap">
      <input 
        link 
        v-model="searchKey" 
        placeholder="搜索岗位名称"
        @keyup.enter="handleSearch"
      >
    </div>

    <input type="hidden" name="city" class="city-code" value="101280600">
    <input type="hidden" name="industry" class="industry-code" value="">
    <input type="hidden" name="position" class="position-code" value="">

    <!-- 新增：按钮组（搜索+恢复），替换原单独搜索按钮 -->
    <div class="btn-group">
      <!-- 恢复按钮：适配原有样式风格 -->
      <button class="btn-reset" @click="handleReset">
        恢复
      </button>
      <!-- 搜索按钮：保留原有样式，调整圆角 -->
      <button class="btn-search" @click="handleSearch">
        搜索
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, defineEmits, onMounted, onUnmounted } from 'vue'

// 定义搜索事件：向父组件传递搜索条件
const emit = defineEmits(['search'])

// 修复1：拆分“显示文本”和“实际值”，避免字符串匹配错误
const selectedJobTypeValue = ref('') // 传给后端的实际值（空/餐饮/快递等）
const selectedJobTypeText = ref('岗位类型') // 页面显示的文本
const showJobType = ref(false) // 下拉框显隐
const searchKey = ref('') // 搜索关键词
const searchBarRef = ref(null) // 容器ref，用于点击外部关闭下拉

// 修复2：选择岗位类型（传实际值+显示文本）
const selectJobType = (value, text) => {
  selectedJobTypeValue.value = value
  selectedJobTypeText.value = text
  showJobType.value = false // 选完后关闭下拉
}

// 修复3：搜索逻辑（传正确的条件给父组件）
const handleSearch = () => {
  const searchCondition = {
    jobType: selectedJobTypeValue.value, // 传给后端的实际类型值（空/餐饮等）
    key: searchKey.value.trim() // 关键词去空格
  }
  // 向父组件发送搜索条件
  emit('search', searchCondition)
  // 清空搜索框（可选，提升体验）
  searchKey.value = ''
}


// 新增：恢复按钮逻辑（清空条件+重新加载所有岗位）
const handleReset = () => {
  // 1. 清空搜索框
  searchKey.value = ''
  // 2. 重置岗位类型为默认（全部类型）
  selectedJobTypeValue.value = ''
  selectedJobTypeText.value = '岗位类型'
  // 3. 关闭下拉框（如果打开）
  showJobType.value = false
  // 4. 触发父组件加载所有已审核岗位（传空条件）
  emit('search', {
    jobType: '',
    key: ''
  })
}

// 修复4：点击外部关闭下拉框
const handleClickOutside = (e) => {
  if (searchBarRef.value && !searchBarRef.value.contains(e.target)) {
    showJobType.value = false
  }
}

// 挂载时监听点击事件
onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

// 卸载时移除监听（避免内存泄漏）
onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
/* 外层容器：去掉overflow:hidden，避免裁剪下拉 */
.search-form-con {
  display: flex;
  align-items: center;
  width: 700px;
  height: 44px;
  margin: 20px auto;
  border: 2px solid #00bfa5;
  border-radius: 22px;
  /* 删掉 overflow: hidden; 核心修复点 */
  box-shadow: 0 2px 8px rgba(0, 191, 165, 0.15);
  position: relative; /* 给子元素绝对定位做参考 */
}

/* 职位类型选择区：加高z-index，确保下拉在最上层 */
.position-sel {
  position: relative; /* 下拉面板的定位参考 */
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 120px;
  height: 100%;
  padding: 0 15px;
  background-color: #f5f5f5;
  cursor: pointer;
  font-size: 14px;
  color: #333;
  z-index: 1; /* 极高层级，防止被其他元素遮挡 */
  border-radius: 20px 0 0 20px; /* 保持左侧圆角 */
}
.sel-icon {
  font-size: 10px;
  color: #999;
  margin-left: 5px;
  line-height: 1;
}

/* 下拉选项面板：加高z-index，精准定位 */
.sel-options {
  position: absolute;
  top: 44px; /* 刚好贴在选择区底部 */
  left: 0;
  width: 100%;
  background-color: #fff;
  border: 1px solid #eee;
  border-top: none;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  border-radius: 0 0 6px 6px;
  z-index: 2; /* 比选择区更高，确保不被遮挡 */
}
.option-item {
  padding: 10px 15px;
  font-size: 14px;
}
.option-item:hover {
  background-color: #f5f5f5;
  color: #00bfa5; /* hover时文字变主题色，更醒目 */
}

/* 搜索输入区：样式不变 */
.ipt-wrap {
  flex: 1;
  height: 100%;
}
.ipt-wrap input {
  width: 100%;
  height: 100%;
  padding: 0 15px;
  border: none;
  outline: none;
  font-size: 14px;
  color: #333;
  background-color: #fff;
}
.ipt-wrap input::placeholder {
  color: #999;
}
/* 新增：按钮组 - 适配原有高度/主题色 */
.btn-group {
  display: flex;
  height: 100%;
}

/* 恢复按钮：和整体风格统一，配色协调 */
.btn-reset {
  width: 80px;
  height: 100%;
  background-color: #fff;
  border: none;
  color: #00bfa5;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}
.btn-reset:hover {
  background-color: #f5fff9; /* 浅绿背景，和主题呼应 */
}
/* 搜索按钮：样式不变 */
.btn-search {
  width: 100px;
  height: 100%;
  background-color: #00bfa5;
  border: none;
  color: #fff;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
  border-radius: 0 20px 20px 0; /* 保持右侧圆角 */
}
.btn-search:hover {
  background-color: #00a994;
}
</style>