import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import {isLogin} from '@/utils/auth'
import { useUserStore } from '@/stores/user'
import type {RouteMeta} from '@/types/auth'
import { ElMessage } from 'element-plus'


const routes: Array<RouteRecordRaw> = [
  { 
    path: '/',
    component: () => import('@/components/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '业财仪表盘', requiresAuth: true }
      },
      {
        path: 'bill',
        name: 'Bill',
        component: () => import('@/views/BillList.vue'),
        meta: { title: '账单管理', requiresAuth: true }
      },
      {
        path: 'bill/:id',
        name: 'BillDetail',
        component: () => import('@/views/BillDetail.vue'),
        meta: { title: '账单详情', requiresAuth: true },
        props: true
      },
      {
        path: 'bill/create',
        name: 'BillCreate',
        component: () => import('@/views/BillCreate.vue'),
        meta: { title: '新增账单', requiresAuth: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '个人设置', requiresAuth: true, roles: ['admin'] }
      }
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

router.beforeEach(async (to, from, next) => {
  if(!to.meta.requiresAuth) {
    next();
    return;
  }

  if(!isLogin()) {
    next({ path: '/login', query: { redirect: to.fullPath } });
    ElMessage.warning('请先登录！');
    return;
  }

  const userStore = useUserStore();
  if(!userStore.userInfo) {
    try {
        await userStore.fetchUserInfo();
    } catch (error) {
        userStore.logout();
        next({ path: '/login', query: { redirect: to.fullPath } });
        return;
    }
  }

  if(to.meta.roles && to.meta.roles.length > 0) {
    const userRole = userStore.userInfo?.role;
    if(!userRole || !to.meta.roles.includes(userRole)) {
      ElMessage.error('您无权访问该页面！');
      next({ name: 'NotFound' });
      return;
    }
  }

  next()

})


export default router








