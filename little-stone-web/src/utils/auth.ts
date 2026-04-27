import type { UserInfo } from '@/types/auth';

const TOKEN_KEY = 'USER_TOKEN' as const;
const USER_INFO_KEY = 'USER_INFO' as const;

/**
 * 设置Token
 * @param token 鉴权凭证
 */
export const setToken = (token: string): void => {
    localStorage.setItem(TOKEN_KEY, token);
}

/**
 * 获取Token
 * @returns 鉴权凭证（空字符串表示无）
 */
export const getToken = (): string => {
    return localStorage.getItem(TOKEN_KEY) || '';
}

/**
 * 清除Token和用户信息
 */
export const clearAuthStorage = (): void => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_INFO_KEY);
}

/**
 * 判断是否已登录（有有效Token即认为已登录）
 * @returns 登录状态
 */
export const isLogin = (): boolean => {
    return !!getToken();
}

/**
 * 设置用户信息到本地存储
 * @param userInfo 用户信息
 */
export const setUserInfo = (userInfo: UserInfo): void => {
    localStorage.setItem(USER_INFO_KEY, JSON.stringify(userInfo));
}

/**
 * 获取本地存储的用户信息
 * @returns 用户信息（null表示无）
 */
export const getUserInfo = (): UserInfo | null => {
    const data = localStorage.getItem(USER_INFO_KEY);
    return data ? JSON.parse(data) as UserInfo : null;
}