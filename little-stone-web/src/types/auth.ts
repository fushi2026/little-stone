export interface LoginForm {
    username: string;
    password: string;
    remember?: boolean;
    deviceFingerprint?: string;
}

export interface SaltResponse {
    nonce: string
    salt: string
}

export interface RegisterForm {
    username: string;
    password: string;
    confirmPassword: string;
    deviceFingerprint?: string;
}

export interface UserInfo {
    username: string
    nickname: string
    avatar: string
    roleList: string[]
}

export interface ModuleItem {
    moduleCode: string
    moduleName: string
    icon: string
}

export interface MenuItem {
    id: number
    parentId: number
    menuName: string
    path: string
    component?: string | null
    icon?: string | null
    sort?: number
    moduleCode?: string  // 可选的模块代码，用于菜单过滤
    children?: MenuItem[]
}

export interface LoginResponse {
    token: string
    userInfo: UserInfo
    moduleList: ModuleItem[]
    menuList: MenuItem[]
    permList: string[]
}

export interface RefreshTokenResponse {
    token: string
}

export interface RouteMeta {
    title: string
    requiresAuth?: boolean
    roles?: Array<'admin' | 'user' | 'guest'>
    icon?: string
}

import 'vue-router';
declare module 'vue-router' {
    interface RouteMeta extends Record<string | number | symbol, unknown> {
        title?: string
        requiresAuth?: boolean
        roles?: Array<'admin' | 'user' | 'guest'>
        icon?: string
    }
}