import axios from "axios";
import type { AxiosInstance, AxiosResponse, InternalAxiosRequestConfig, AxiosRequestConfig } from "axios";
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

// 用于防止重复刷新 Token
let isRefreshing = false;
let pendingRequests: Array<(token: string) => void> = [];

// 响应拦截器
service.interceptors.response.use(
  async (response: AxiosResponse<ApiResponse>): Promise<AxiosResponse<ApiResponse>> => {
    const res = response.data;
    if(res.code !== 200) {
      ElMessage.error(res.message || '请求失败！');
      return Promise.reject(response);
    }
    return response;
  },
  async (error) => {
    const { response, config } = error;
    
    // 处理 401 未授权错误
    if (response?.status === 401) {
      // 检查是否是刷新 Token 接口本身的错误
      if (config.url?.includes('/auth/refresh')) {
        handleTokenExpired();
        return Promise.reject(error);
      }

      const userStore = useUserStore();

      // 如果正在刷新 Token，将请求加入队列
      if (isRefreshing) {
        return new Promise((resolve) => {
          pendingRequests.push((newToken: string) => {
            if (config.headers) {
              config.headers['Authorization'] = `Bearer ${newToken}`;
            }
            resolve(service(config));
          });
        });
      }

      isRefreshing = true;

      try {
        // 尝试刷新 Token
        const newToken = await userStore.refreshAccessToken();

        if (newToken) {
          // 更新当前请求的 Token
          if (config.headers) {
            config.headers['Authorization'] = `Bearer ${newToken}`;
          }

          // 重新发起当前请求
          const result = await service(config);

          // 处理队列中的请求
          pendingRequests.forEach((callback) => callback(newToken));
          pendingRequests = [];

          return result;
        } else {
          handleTokenExpired();
        }
      } catch (refreshError) {
        handleTokenExpired();
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    } else if (response?.status === 403) {
      ElMessage.error('您没有权限访问该资源！');
    } else if (response?.status === 404) {
      ElMessage.error('请求的资源不存在！');
    } else if (response?.status >= 500) {
      ElMessage.error('服务器内部错误，请稍后重试！');
    } else {
      ElMessage.error(error.message || '请求失败，请检查网络！');
    }

    return Promise.reject(error);
  }
)

// 处理 Token 过期
function handleTokenExpired(): void {
  const userStore = useUserStore();
  userStore.resetUser();
  clearAuthStorage();
  router.push({ name: 'Login' });
  ElMessage.warning('登录已过期，请重新登录！');
}

export const request = {
    get<T = any>(url: string, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
        return service.get(url, config).then(res => res.data);
    },
    post<T = any>(url: string, data?: object): Promise<ApiResponse<T>> {
        return service.post(url, data).then(res => res.data);
    },
    put<T = any>(url: string, data?: object): Promise<ApiResponse<T>> {
        return service.put(url, data).then(res => res.data);
    },
    delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
        return service.delete(url, config).then(res => res.data);
    }
}

export default request
