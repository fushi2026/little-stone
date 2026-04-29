import {defineStore} from 'pinia'
import { ref } from 'vue'
import type { LoginForm, UserInfo, MenuItem, LoginResponse } from '@/types/auth'
import { setToken, getToken, clearAuthStorage, setUserInfo, getUserInfo } from '@/utils/auth'
import request from '@/utils/request'
import { generateRoutes } from '@/utils/router'
import router from '@/router'


export const useUserStore = defineStore('user', () => {
    const token = ref<string>(getToken())
    const userInfo = ref<UserInfo | null>(getUserInfo())
    const menuTree = ref<MenuItem[]>(userInfo.value?.menuTree || [])
    const permissions = ref<string[]>(userInfo.value?.permissions || [])
    const roles = ref<string[]>(userInfo.value?.roles || [])

    /**
     * 登录
     * @param form 
     */
    const login = async (form: LoginForm): Promise<void> => {
        try {
            const res = await request.post('/auth/login', form)

            const loginResponse = res.data.data as LoginResponse

            token.value = loginResponse.token

            userInfo.value = {
                username: loginResponse.username,
                realName: loginResponse.realName,
                token: loginResponse.token,
                menuTree: loginResponse.menuTree,
                permissions: loginResponse.permissions, 
                roles: loginResponse.roles,
            }

            menuTree.value = loginResponse.menuTree
            permissions.value = loginResponse.permissions
            roles.value = loginResponse.roles

            setToken(loginResponse.token)
            setUserInfo(userInfo.value)

            await setupRoutes(loginResponse.menuTree)
        } catch (error) {
            throw new Error('Login failed');
        }
    }

    //设置动态路由
    const setupRoutes = async (menus: MenuItem[]): Promise<void> => {
        const routes = generateRoutes(menus)

        const layoutRoute = router.getRoutes().find(r => r.path === '/')
        if(layoutRoute) {
            routes.forEach(route => {
                router.addRoute('/', route)
            })
        }
    }

    const fetchUserInfo = async (): Promise<void> => {
        try {
            const res = await request.get('/user/info')
            const data = res.data as UserInfo

            userInfo.value = {
                username: data.username,
                realName: data.realName,
                menuTree: data.menuTree,
                permissions: data.permissions, 
                roles: data.roles,
                token: token.value || '',
            }

            menuTree.value = data.menuTree
            permissions.value = data.permissions
            roles.value = data.roles

            setUserInfo(userInfo.value)

            await setupRoutes(data.menuTree)
        } catch (error) {
            throw new Error('Failed to fetch user info')
        }
    }

    /**
     * 退出登录
     */
    const logout = (): void => {
        token.value = ''
        userInfo.value = null
        menuTree.value = []
        permissions.value = []
        roles.value = []

        clearAuthStorage()

        router.push('/login')
    }

    /**
     * 重置用户状态（Token过期时调用）
     */
    const resetUser = (): void => {
        token.value = ''
        userInfo.value = null
        menuTree.value = []
        permissions.value = []
        roles.value = []
    }

    /**
     * 检查权限
     */
    const hasPermission = (permission: string): boolean => {
        return permissions.value.includes(permission)
    }

    /**
     * 检查角色
     */
    const hasRole = (role: string): boolean => {
        return roles.value.includes(role)
    }

    return {
        token,
        userInfo,
        menuTree,
        permissions,
        roles,
        hasPermission,
        hasRole,
        setupRoutes,
        login,
        fetchUserInfo,
        logout,
        resetUser
    }
})