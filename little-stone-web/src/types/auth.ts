export interface UserInfo {
    username: string
    realName: string
    menuTree: any[]
    permissions: string[]
    roles: string[]
    token: string
}

export interface LoginForm {
    username: string;
    password: string;
    remember?: boolean;
}

export interface MenuItem {
    id: number
    parentId: number
    path: string
    name?: string
    component?: string | null
    icon?: string | null
    title?: string
    permName?: string
    children?: MenuItem[]
    permType?: number
    status?: number
    sort?: number
}

export interface LoginResponse {
    menuTree: MenuItem[]
    permissions: string[]
    realName: string
    roles: string[]
    token: string
    username: string
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