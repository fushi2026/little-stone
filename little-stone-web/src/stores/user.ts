import {defineStore} from 'pinia'
import { ref } from 'vue'
import type { LoginForm, UserInfo, ModuleItem, MenuItem, LoginResponse } from '@/types/auth'
import { 
    setToken, getToken, 
    clearAuthStorage, 
    setUserInfo, getUserInfo,
    setModuleList, getModuleList,
    setMenuList, getMenuList,
    setPermList, getPermList
 } from '@/utils/auth'
import { loginWithEncrypt } from '@/api/auth'
import { getDeviceFingerprint } from '@/utils/fingerprint'
import { generateRoutes } from '@/utils/router'
import router from '@/router'
import request from '@/utils/request'


export const useUserStore = defineStore('user', () => {

    const token = ref<string>(getToken())
    const userInfo = ref<UserInfo | null>(getUserInfo())
    const moduleList = ref<ModuleItem[]>(getModuleList())
    const menuList = ref<MenuItem[]>(getMenuList())
    const permList = ref<string[]>(getPermList())

    /**
     * 登录（带动态盐加密和设备指纹）
     * @param form 
     */
    const login = async (form: LoginForm): Promise<void> => {
        try {
            // 添加设备指纹（异步）
            const deviceFingerprint = await getDeviceFingerprint()
            const formWithFingerprint = {
                ...form,
                deviceFingerprint
            }

            const loginResponse = await loginWithEncrypt(formWithFingerprint) as LoginResponse

            token.value = loginResponse.token

            userInfo.value = loginResponse.userInfo

            moduleList.value = loginResponse.moduleList
            menuList.value = loginResponse.menuList
            permList.value = loginResponse.permList

            setToken(token.value)
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

        const layoutRoute = router.getRoutes().find(r => r.name === 'Layout')
        if (layoutRoute) {
            routes.forEach(route => {
                router.addRoute('Layout', route)
            })
        }
    }

    const fetchUserInfo = async (): Promise<void> => {
        try {
            const res = await request.get('/user/info')
            const loginResponse = res.data.data as LoginResponse

            token.value = loginResponse.token

            userInfo.value = loginResponse.userInfo

            moduleList.value = loginResponse.moduleList
            menuList.value = loginResponse.menuList
            permList.value = loginResponse.permList

            setToken(token.value)
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
     * 获取用户菜单
     */
    const getUserMenus = async (): Promise<void> => {
        try {
            const res = await request.get('/menu/getUserMenus')
            const menuList = res.data as MenuItem[]
            await setupRoutes(menuList)
        } catch (error) {
            throw new Error('fail to fetch user menus！')
        }
    }

    /**
     * 退出登录
     */
    const logout = (): void => {
        token.value = ''
        userInfo.value = null
        moduleList.value = []
        menuList.value = []
        permList.value = []

        clearAuthStorage()

        import('@/router').then(mod => mod.isAddDynamicRoute = false)

        router.push('/login')
    }

    /**
     * 刷新 Token
     */
    const refreshAccessToken = async (): Promise<string | null> => {
        if (!token.value) return null

        try {
            // 获取设备指纹
            const deviceFingerprint = getDeviceFingerprint()

            const res = await request.post('/auth/refresh', {
                deviceFingerprint
            })

            const data = res.data.data
            token.value = data.token

            setToken(token.value)

            return token.value
        } catch (error) {
            console.error('Token refresh failed:', error)
            resetUser()
            return null
        }
    }

    /**
     * 重置用户状态（Token过期时调用）
     */
    const resetUser = (): void => {
        token.value = ''
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
        userInfo,
        moduleList,
        menuList,
        permList,
        hasPermission,
        hasRole,
        setupRoutes,
        login,
        fetchUserInfo,
        getUserMenus,
        refreshAccessToken,
        logout,
        resetUser
    }
})