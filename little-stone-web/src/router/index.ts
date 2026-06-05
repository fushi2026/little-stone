import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import {isLogin} from '@/utils/auth'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'


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
 if(isLogin() && to.name === 'Login') {
    next({ path: '/' })
    return
  }

  if(!to.meta.requiresAuth) {
    next()
    return
  }

  if(!isLogin()) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    ElMessage.warning('请先登录！')
    return
  }

  //如果用户已登录但没有用户信息，尝试获取用户信息
  const userStore = useUserStore()
  if(!userStore.userInfo) {
    try {
        await userStore.fetchUserInfo();
    } catch (error) {
        userStore.logout()
        next({ path: '/login', query: { redirect: to.fullPath } })
        return
    }
  }

  //路由是否加载？如果没有加载，说明是第一次访问或刷新页面，需要重新设置动态路由


  //检查用户角色权限
  if(to.meta.roles && to.meta.roles.length > 0) {
    const hasPermission = to.meta.roles.some((role: string) => userStore.hasRole(role))
    if(!hasPermission) {
      ElMessage.error('您无权访问该页面！')
      next({ name: 'NotFound' })
      return
    }
  }

  next()
})


export default router








