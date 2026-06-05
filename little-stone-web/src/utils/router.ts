import type { RouteRecordRaw } from "vue-router"
import type { MenuItem } from '@/types/auth'

const modules = import.meta.glob('@/views/**/*.vue')
const BlankLayout = () => import('@/components/BlankLayout.vue')


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

    if(!menu.path) return null

    //去掉path中的第一个斜杠
    const normalizedPath = menu.path.startsWith('/') ? menu.path.slice(1) : menu.path

    const route: RouteRecordRaw = {
        path: normalizedPath,
        name: normalizedPath,
        meta: {
            title: menu.menuName || undefined,
            requiresAuth: true
        },
        component: menu.component ? modules[`/src/views/${menu.component}.vue`] : BlankLayout,
    } as RouteRecordRaw

    if(menu.children && menu.children.length > 0) {
        route.children = []
        menu.children.forEach(child => {
            const childRoute : RouteRecordRaw = transformMenuItemToRoute(child) as RouteRecordRaw
            if(childRoute) {                 
                route.children?.push(childRoute)
            }
        })
    }

    return route
}
