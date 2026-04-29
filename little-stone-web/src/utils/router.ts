import type { RouteRecordRaw } from "vue-router"
import type { MenuItem } from '@/types/auth'



//组件映射表
const componentMap: Record<string, any> = {
    Home: () => import('@/views/Home.vue'),
    NotFound: () => import('@/components/NotFound.vue'),
}

//将后端菜单数据转化为路由
export function generateRoutes(menus: MenuItem[]): RouteRecordRaw[] {
    const routes: RouteRecordRaw[] = []

    menus.forEach(menu => {
        if(menu.permType === 1) {
            const route = transformMenuItemToRoute(menu)
            if(route) {
                routes.push(route)
            }
        }
    })

    return routes
}

//转换单个菜单项为路由
function transformMenuItemToRoute(menu: MenuItem): RouteRecordRaw | null {
    if(!menu.path || !menu.component) {
        return null
    }

    const route: RouteRecordRaw = {
        path: menu.path,
        name: menu.permName,
        meta: {
            title: menu.permName,
            requiresAuth: true,
            icon: menu.icon
        }
    }

    if(menu.component && menu.component === 'string') {
        route.component = componentMap[menu.component]
    } else {
        route.component = import('@/views/Home.vue')
    }

    if(menu.children && menu.children.length > 0) {
        const children = generateRoutes(menu.children)
        if(children.length > 0) {
            route.children = children
        }
    }

    return route
}
