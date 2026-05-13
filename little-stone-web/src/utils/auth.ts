import type { ModuleItem, MenuItem, UserInfo } from '@/types/auth'

const TOKEN_KEY = 'USER_TOKEN' as const
const REFRESH_TOKEN_KEY = 'REFRESH_TOKEN' as const
const USER_INFO_KEY = 'USER_INFO' as const
const MODULE_LIST_KEY = 'MODULE_LIST' as const
const MENU_LIST_KEY = 'MENU_LIST' as const
const PERM_LIST_KEY = 'PERM_LIST' as const

const TOKEN_EXPIRE_KEY = 'TOKEN_EXPIRE' as const


export const getToken = (): string => {
    return localStorage.getItem(TOKEN_KEY) || ''
}

export const setToken = (token: string): void => {
    localStorage.setItem(TOKEN_KEY, token)

    const expireTime = getJwtExpireTime(token)
    if(expireTime) {
        localStorage.setItem(TOKEN_EXPIRE_KEY, expireTime.toString())
    }
}

export const setRefreshToken = (refreshToken: string): void => {
    localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken)
}

export const getRefreshToken = (): string => {
    return localStorage.getItem(REFRESH_TOKEN_KEY) || ''
}

export const setUserInfo = (userInfo: UserInfo): void => {
    localStorage.setItem(USER_INFO_KEY, JSON.stringify(userInfo));
}

export const getUserInfo = (): UserInfo | null => {
    const data = localStorage.getItem(USER_INFO_KEY);
    return data ? JSON.parse(data) as UserInfo : null;
}

export const setModuleList = (moduleList: ModuleItem[]): void => {
    localStorage.setItem(MODULE_LIST_KEY, JSON.stringify(moduleList));
}

export const getModuleList = (): ModuleItem[] => {
    const data = localStorage.getItem(MODULE_LIST_KEY);
    return data ? JSON.parse(data) as ModuleItem[] : [];
}

export const setMenuList = (menuList: MenuItem[]): void => {
    localStorage.setItem(MENU_LIST_KEY, JSON.stringify(menuList));
}

export const getMenuList = (): MenuItem[] => {
    const data = localStorage.getItem(MENU_LIST_KEY);
    return data ? JSON.parse(data) as MenuItem[] : [];
}

export const setPermList = (permList: string[]): void => {
    localStorage.setItem(PERM_LIST_KEY, JSON.stringify(permList));
}

export const getPermList = (): string[] => {
    const data = localStorage.getItem(PERM_LIST_KEY);
    return data ? JSON.parse(data) as string[] : [];
}

const getJwtExpireTime = (token: string): number | null => {
    try {
        const parts: string[] = token.split('.')
        if (parts.length !== 3) {
            return null
        }

        const payload = JSON.parse(atob(parts[1] || ''))
        return payload.exp || null
    } catch (error) {
        console.error('解析Token失败:', error)
        return null
    }
}

export const isTokenExpired = (): boolean => {
    const expireTimeStr = localStorage.getItem(TOKEN_EXPIRE_KEY)
    if (!expireTimeStr) {
        return true
    }

    const expireTime = parseInt(expireTimeStr, 10)
    const currentTime = Math.floor(Date.now() / 1000)
    return currentTime >= expireTime
}

export const clearAuthStorage = (): void => {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(REFRESH_TOKEN_KEY)
    localStorage.removeItem(USER_INFO_KEY)
    localStorage.removeItem(TOKEN_EXPIRE_KEY)
    localStorage.removeItem(MODULE_LIST_KEY)
    localStorage.removeItem(MENU_LIST_KEY)
    localStorage.removeItem(PERM_LIST_KEY)
}

export const isLogin = (): boolean => {
    const token = getToken()
    if(!token) {
        return false
    }
    return !isTokenExpired()
}


