import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // ---------------- 根路径直接跳登录页 ----------------
    // {
    //   path: '/',
    //   redirect: '/login', // 核心修改：访问/直接跳登录页
    //   meta: { requiresAuth: false }
    // },
    {
      path: '/',
      name: 'Home',
      component: () => import('@/views/HomeView.vue'),
      meta: {
        title: '兼职平台-首页',
        requireAuth: false // 无需登录即可访问
      }
    },

    // ---------------- 管理员专属路由（/admin/*） ----------------
    {
      path: '/admin',
      name: 'AdminLayout',
      component: () => import('@/views/admin/layout/index.vue'),
      meta: {
        requiresAuth: true,
        roleType: 0 // 0=管理员，仅该角色可访问
      },
      children: [
        {
          path: 'index', // 完整路径：/admin/index
          name: 'AdminWorkbench',
          component: () => import('@/views/admin/workbench/index.vue'),
          meta: { requiresAuth: true, roleType: 0 }
        },
        {
          path: 'joblist', // 完整路径：/admin/joblist（兼容原有/joblist）
          name: 'AdminJobList',
          component: () => import('@/views/admin/job/JobList.vue'),
          meta: { requiresAuth: true, roleType: 0 }
        },
        {
          path: 'jobaudit', // 完整路径：/admin/jobaudit
          name: 'AdminJobAudit',
          component: () => import('@/views/admin/job/JobAudit.vue'),
          meta: { requiresAuth: true, roleType: 0 }
        },
        {
          path: 'user-management', // 完整路径：/admin/user-management
          name: 'AdminUserManagement',
          component: () => import('@/views/admin/user-management/index.vue'),
          meta: { requiresAuth: true, roleType: 0 }
        },
        {
          path: 'evaluation', // 完整路径：/admin/user-management
          name: 'AdminEvaluation',
          component: () => import('@/views/admin/evaluation/index.vue'),
          meta: { requiresAuth: true, roleType: 0 }
        },
        {
          path: 'stat', // 完整路径：/admin/stat
          name: 'AdminStat',
          component: () => import('@/views/admin/stat/index.vue'),
          meta: { requiresAuth: true, roleType: 0 }
        }

      ]
    },

    // ---------------- 兼容原有/joblist路径：重定向到管理员岗位列表 ----------------
    // {
    //   path: '/joblist',
    //   redirect: '/admin/joblist',
    //   meta: { requiresAuth: true }
    // },

    // ---------------- 雇主/求职者路由（暂时注释，后续启用时取消） ----------------
    {
      path: '/employer',
      name: 'EmployerLayout',
      component: () => import('@/views/employer/layout/index.vue'),
      meta: { requiresAuth: true, roleType: 1 }, // 仅雇主（roleType=1）可访问
      children: [
        // 布局页默认页（可选，点击侧边栏前显示）
        {
          path: 'index',
          name: 'EmployerWorkbench',
          component: () => import('@/views/employer/workbench/index.vue'), // 可新建空的工作台页
          meta: { requiresAuth: true, roleType: 1 }
        },
        // 发布/管理岗位子路由
        {
          path: 'jobAdd',
          name: 'EmployerJobAdd',
          component: () => import('@/views/employer/job/JobAdd.vue'),
          meta: { requiresAuth: true, roleType: 1 }
        },
        {
          path: 'evaluation',
          name: 'EmployerEvaluation',
          component: () => import('@/views/employer/evaluation/index.vue'),
          meta: { requiresAuth: true, roleType: 1 }
        },
        // ========== 新增：筛选/聘用求职者子路由 ==========
        {
          path: 'recruit',
          name: 'EmployerRecruit',
          component: () => import('@/views/employer/recruit/index.vue'),
          meta: { requiresAuth: true, roleType: 1 }
        },
        { path: 'attendance', name: 'EmployerAttendance', component: () => import('@/views/employer/attendance/index.vue') } // 雇主考勤页
        // 后续可扩展：筛选/聘用求职者子路由
        // {
        //   path: 'recruit',
        //   name: 'EmployerRecruit',
        //   component: () => import('@/views/employer/recruit/index.vue'),
        //   meta: { requiresAuth: true, roleType: 1 }
        // }
      ]
    },
    {
      path: '/jobseeker',
      name: 'JobseekerLayout',
      component: () => import('@/views/jobseeker/layout/index.vue'),
      meta: { requiresAuth: true, roleType: 2 },
      children: [
        { path: 'index', name: 'JobseekerWorkbench', component: () => import('@/views/jobseeker/workbench/index.vue'), meta: { requiresAuth: true, roleType: 2 } },
        { path: 'attendance', name: 'SeekerAttendance', component: () => import('@/views/jobseeker/attendance/index.vue'), meta: { requiresAuth: true, roleType: 2 } }, // 求职者考勤页
        { path: 'joblist', name: 'JobseekerJobList', component: () => import('@/views/jobseeker/job/index.vue'), meta: { requiresAuth: true, roleType: 2 } }, // ✅ 岗位列表：正式启用，配置完整
        // { path: 'joblist', name: 'JobseekerJobList', component: () => import('@/views/jobseeker/job/JobList.vue'), meta: { requiresAuth: true, roleType: 2 } }
        // ✅ 新增：薪资记录路由（和现有配置完全一致）
        { path: 'salary', name: 'SeekerSalary', component: () => import('@/views/jobseeker/salary/index.vue'), meta: { requiresAuth: true, roleType: 2 } }

      ]
    },

    // ---------------- 公共路由 ----------------
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/auth/Login.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/auth/Register.vue'),
      meta: { requiresAuth: false }
    },

    // ---------------- 404路由（恢复，兜底不存在的路径） ----------------
    // {
    //   path: '/:pathMatch(.*)*',
    //   name: 'NotFound',
    //   component: () => import('@/views/404.vue'), // 需创建空的404.vue（内容可自定义）
    //   meta: { requiresAuth: false }
    // }
  ]
})

// ---------------- 增强版路由守卫（登录+角色校验） ----------------
router.beforeEach((to, from, next) => {
  // 1. 无需登录的页面直接放行
  if (!to.meta.requiresAuth) {
    next()
    return
  }

  // 2. 检查是否登录（无Token跳登录页）
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.error('请先登录')
    next('/login')
    return
  }

  // 3. 角色校验（仅对有roleType的路由生效）
  if (to.meta.roleType !== undefined) {
    const currentRoleType = Number(localStorage.getItem('roleType'))
    // 角色不匹配 → 直接跳登录页（避免跳转不存在的路径）
    if (currentRoleType !== to.meta.roleType) {
      ElMessage.error('无权限访问该页面，请重新登录')
      next('/login')
      return
    }
  }

  // 4. 所有校验通过，放行
  next()
})

export default router