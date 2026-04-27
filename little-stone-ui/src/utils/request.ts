import axios from "axios";
import type { AxiosInstance, AxiosResponse, InternalAxiosRequestConfig } from "axios";
import { getToken, clearAuthStorage } from "@/utils/auth";
import { useUserStore } from "@/stores/user";
import { router } from "@/router";
import { ElMessage } from "element-plus"

interface ApiResponse<T = any> {
    code: number;
    message: string;
    data: T;
}

const service: AxiosInstance = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL as string,
    timeout: 10000,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
})

// 请求拦截器
service.interceptors.request.use(
    (config: InternalAxiosRequestConfig): InternalAxiosRequestConfig => {
      const token = getToken();
      if(token && config.headers) {
        config.headers['Authorization'] = `Bearer ${token}`;
      }
      return config;
    },
    (error) => {
      ElMessage.error('请求发送失败，请检查网络！');
      return Promise.reject(error);
    }
)

service.interceptors.response.use(
  async (response: AxiosResponse<ApiResponse>): Promise<AxiosResponse<ApiResponse>> => {
    const res = response.data;
    if(res.code !== 200) {
      ElMessage.error(res.message || '请求失败！');
      return Promise.reject(response);
    }
    return response;
  },
  (error) => {
    if(error.response?.status === 401) {
      const userStore = useUserStore();
      userStore.resetUser();
      clearAuthStorage();
      router.push({ name: 'Login' });
      ElMessage.warning('登录已过期，请重新登录！');
    } else {
      ElMessage.error(error.message || '响应接收失败，请检查网络！');
    }
    return Promise.reject(error);
  }
)

export const request = {
    get<T = any>(url: string, params?: object): Promise<ApiResponse<T>> {
        return service.get(url, { params });
    },
    post<T = any>(url: string, data?: object): Promise<ApiResponse<T>> {
        return service.post(url, data);
    }
}

export default request
