export interface LoginForm {
    username: string;
    password: string;
    remember?: boolean;
}

export interface RegisterForm {
    username: string;
    password: string;
    confirmPassword: string;
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
    children?: MenuItem[]
}

export interface LoginResponse {
    token: string
    refreshToken: string
    userInfo: UserInfo
    moduleList: ModuleItem[]
    menuList: MenuItem[]
    permList: string[]
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