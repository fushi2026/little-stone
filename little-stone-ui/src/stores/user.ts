import {defineStore} from 'pinia';
import { ref } from 'vue';
import type { LoginForm, UserInfo } from '@/types/auth';
import { setToken, getToken, clearAuthStorage, setUserInfo } from '@/utils/auth';
import request from '@/utils/request';

export const useUserStore = defineStore('user', () => {
    const token = ref<string>(getToken());
    const userInfo = ref<UserInfo | null>(null);

    /**
     * 登录
     * @param form 
     */
    const login = async (form: LoginForm): Promise<void> => {
        try {
            const res = await request.post('/auth/login', form);

            console.log('Login response:', res);

            token.value = res.data.token;
            userInfo.value = res.data.userInfo;
            setToken(res.data.token);
            setUserInfo(res.data.userInfo);
        } catch (error) {
            throw new Error('Login failed');
        }
    }

    /**
     * 获取用户信息
     */
    const fetchUserInfo = async (): Promise<void> => {
        try {
            const res = await request.get<UserInfo>('/user/userinfo');
            userInfo.value = res.data;
            setUserInfo(res.data);
        } catch (error) {
            throw new Error('Failed to fetch user info');
        }
    }

    /**
     * 退出登录
     */
    const logout = (): void => {
        token.value = '';
        userInfo.value = null;
        clearAuthStorage();
    }

    /**
     * 重置用户状态（Token过期时调用）
     */
    const resetUser = (): void => {
        token.value = '';
        userInfo.value = null;
    }

    return {
        token,
        userInfo,
        login,
        fetchUserInfo,
        logout,
        resetUser
    }

})