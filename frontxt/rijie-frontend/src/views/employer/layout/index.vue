<template>
    <div>
        <UserNavbar />
    </div>
    <!-- 雇主布局：侧边栏 + 主内容区（路由视图） -->
    <div class="employer-layout">
        <!-- 1. 侧边栏 -->
        <div class="employer-sidebar">
            <div class="sidebar-title">雇主中心</div>
            <!-- 侧边菜单：点击跳转子路由 -->
            <el-menu default-active="/employer/jobAdd" class="sidebar-menu" background-color="#fff" text-color="#333"
                active-text-color="#00bfa5" @select="handleMenuSelect">
                <el-menu-item index="/employer/jobAdd">
                    <el-icon>
                        <Position />
                    </el-icon>
                    <span slot="title">发布/管理岗位</span>
                </el-menu-item>
                <el-menu-item index="/employer/recruit">
                    <el-icon>
                        <User />
                    </el-icon>
                    <span slot="title">筛选/聘用求职者</span>
                </el-menu-item>

                <el-menu-item index="/employer/evaluation">
                    <el-icon>
                        <Position />
                    </el-icon>
                    <span slot="title">查看评论</span>
                </el-menu-item>


                <el-menu-item index="/employer/setting">
                    <el-icon>
                        <Setting />
                    </el-icon>
                    <span slot="title">账户设置</span>
                </el-menu-item>
            </el-menu>
        </div>

        <!-- 2. 主内容区：渲染子路由（JobAdd.vue等） -->
        <div class="employer-main">
            <router-view />
        </div>
    </div>
</template>

<script setup>
import UserNavbar from '@/components/UserNavbar.vue';
import { useRouter } from 'vue-router'
// 引入Element Plus图标
import { Position, User, Setting } from '@element-plus/icons-vue'
import { ElMenu, ElMenuItem } from 'element-plus'

const router = useRouter()

// 侧边菜单点击：跳转到对应子路由
const handleMenuSelect = (index) => {
    router.push(index)
}
</script>

<style scoped>
/* 整体布局 */
.employer-layout {
    display: flex;
    height: calc(100vh - 60px);
    /* 减去顶部导航栏高度 */
}

/* 侧边栏 */
.employer-sidebar {
    width: 200px;
    background-color: #fff;
    border-right: 1px solid #eee;
}

.sidebar-title {
    font-size: 16px;
    font-weight: 700;
    color: #333;
    padding: 20px;
    border-bottom: 1px solid #eee;
}

.sidebar-menu {
    border-right: none;
}

/* 主内容区（路由视图容器） */
.employer-main {
    flex: 1;
    padding: 20px;
    background-color: #f5f7fa;
    overflow-y: auto;
}
</style>