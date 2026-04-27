export interface UserInfo {
    id: string | number;
    username: string;
    role: 'admin' | 'user' | 'guest';
    permissions?: string[];
}

export interface LoginForm {
    username: string;
    password: string;
    rememberMe?: boolean;
}

export interface RouteMeta {
    title: string;
    requiresAuth?: boolean;
    roles?: Array<'admin' | 'user' | 'guest'>;
}

import 'vue-router';
declare module 'vue-router' {
    interface RouteMeta extends Record<string | number | symbol, unknown> {
        title?: string;
        requiresAuth?: boolean;
        roles?: Array<'admin' | 'user' | 'guest'>;
    }
}