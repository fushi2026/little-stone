import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import {isLogin} from '@/utils/auth'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

export let isAddDynamicRoute = false


const routes: Array<RouteRecordRaw> = [
  { 
    path: '/',
    name: 'Layout',
    component: () => import('@/components/Layout.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/home/index.vue'),
        meta: { title: '首页', requiresAuth: true }
      },
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/components/NotFound.vue'),
    meta: { title: '页面不存在', requiresAuth: false }
  }
]

export const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 }) 
})

router.beforeEach(async (to, _from, next) => {
  // 已登录用户访问登录页 → 跳转首页
  if (isLogin() && to.name === 'Login') {
    next({ path: '/' })
    return
  }

  // 未登录用户访问需要认证的页面 → 跳转登录页
  if (!isLogin() && to.meta.requiresAuth) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    ElMessage.warning('请先登录！')
    return
  }

  const userStore = useUserStore()

  // 已登录但动态路由还没注册 → 先注册动态路由（无论当前匹配到哪个路由）
  if (isLogin() && !isAddDynamicRoute) {
    try {
      // 如果没有用户信息，先获取
      if (!userStore.userInfo) {
        await userStore.fetchUserInfo()
      } else {
        await userStore.getUserMenus()
      }

      isAddDynamicRoute = true
      // 用 path 重新导航（不能用 ...to，因为 to.name 可能是 NotFound）
      next({ path: to.fullPath, replace: true })
      return
    } catch (error) {
      userStore.logout()
      next({ path: '/login', query: { redirect: to.fullPath } })
      return
    }
  }

  // 不需要认证的页面直接放行
  if (!to.meta.requiresAuth) {
    next()
    return
  }

  // 检查用户角色权限
  if (to.meta.roles && to.meta.roles.length > 0) {
    const hasPermission = to.meta.roles.some((role: string) => userStore.hasRole(role))
    if (!hasPermission) {
      ElMessage.error('您无权访问该页面！')
      next({ name: 'NotFound' })
      return
    }
  }

  next()
})


export default router