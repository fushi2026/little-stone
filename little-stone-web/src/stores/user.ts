import {defineStore} from 'pinia'
import { ref } from 'vue'
import type { LoginForm, UserInfo, ModuleItem, MenuItem, LoginResponse } from '@/types/auth'
import { 
    setToken, getToken, 
    getRefreshToken, setRefreshToken, 
    clearAuthStorage, 
    setUserInfo, getUserInfo,
    setModuleList, getModuleList,
    setMenuList, getMenuList,
    setPermList, getPermList
 } from '@/utils/auth'
import request from '@/utils/request'
import { generateRoutes } from '@/utils/router'
import router from '@/router'


export const useUserStore = defineStore('user', () => {
    const token = ref<string>(getToken())
    const refreshToken = ref<string>(getRefreshToken())
    const userInfo = ref<UserInfo | null>(getUserInfo())
    const moduleList = ref<ModuleItem[]>(getModuleList())
    const menuList = ref<MenuItem[]>(getMenuList())
    const permList = ref<string[]>(getPermList())

    /**
     * 登录
     * @param form 
     */
    const login = async (form: LoginForm): Promise<void> => {
        try {
            const res = await request.post('/auth/login', form)

            const loginResponse = res.data.data as LoginResponse

            token.value = loginResponse.token
            refreshToken.value = loginResponse.refreshToken

            userInfo.value = loginResponse.userInfo

            moduleList.value = loginResponse.moduleList
            menuList.value = loginResponse.menuList
            permList.value = loginResponse.permList

            setToken(token.value)
            setRefreshToken(refreshToken.value)
            setUserInfo(userInfo.value)
            setModuleList(moduleList.value)
            setMenuList(menuList.value)
            setPermList(permList.value)

            await setupRoutes(loginResponse.menuList)
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
            const loginResponse = res.data.data as LoginResponse

            token.value = loginResponse.token
            refreshToken.value = loginResponse.refreshToken

            userInfo.value = loginResponse.userInfo

            moduleList.value = loginResponse.moduleList
            menuList.value = loginResponse.menuList
            permList.value = loginResponse.permList

            setToken(token.value)
            setRefreshToken(refreshToken.value)
            setUserInfo(userInfo.value)
            setModuleList(moduleList.value)
            setMenuList(menuList.value)
            setPermList(permList.value)

            await setupRoutes(loginResponse.menuList)
        } catch (error) {
            throw new Error('Failed to fetch user info')
        }
    }

    /**
     * 退出登录
     */
    const logout = (): void => {
        token.value = ''
        refreshToken.value = ''
        userInfo.value = null
        moduleList.value = []
        menuList.value = []
        permList.value = []

        clearAuthStorage()

        router.push('/login')
    }

    /**
     * 重置用户状态（Token过期时调用）
     */
    const resetUser = (): void => {
        token.value = ''
        refreshToken.value = ''
        userInfo.value = null
        moduleList.value = []
        menuList.value = []
        permList.value = []

        clearAuthStorage()

        router.push('/login')
    }

    /**
     * 检查权限
     */
    const hasPermission = (perm: string): boolean => {
        return permList.value.includes(perm)
    }

    /**
     * 检查角色
     */
    const hasRole = (role: string): boolean => {
        return userInfo.value?.roleList.includes(role) || false
    }

    return {
        token,
        refreshToken,
        userInfo,
        moduleList,
        menuList,
        permList,
        hasPermission,
        hasRole,
        setupRoutes,
        login,
        fetchUserInfo,
        logout,
        resetUser
    }
})