import type { RouteRecordRaw } from "vue-router"
import type { MenuItem } from '@/types/auth'


//组件映射表
const componentMap: Record<string, any> = {
    Home: () => import('@/views/home/index.vue'),
    NotFound: () => import('@/components/NotFound.vue'),
}

//将后端菜单数据转化为路由
export function generateRoutes(menus: MenuItem[]): RouteRecordRaw[] {
    const routes: RouteRecordRaw[] = []

    menus.forEach(menu => {
        const route = transformMenuItemToRoute(menu)
        if(route) {
            routes.push(route)
        }
    })

    return routes
}

//转换单个菜单项为路由
function transformMenuItemToRoute(menu: MenuItem): RouteRecordRaw | null {
    if(!menu.path) {
        return null
    }

    const route: RouteRecordRaw = {
        path: menu.path,
        name: undefined,
        meta: {
            title: menu.menuName || undefined,
            requiresAuth: true
        }
    } as RouteRecordRaw

    if(menu.component && menu.component === 'string') {
        route.component = componentMap[menu.component]
    } else {
        route.component = import('@/views/home/index.vue')
    }

    if(menu.children && menu.children.length > 0) {
        const children = generateRoutes(menu.children)
        if(children.length > 0) {
            route.children = children
        }
    }

    return route
}
