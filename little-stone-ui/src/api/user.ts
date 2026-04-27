import request from "@/utils/request";
import type { LoginForm } from "@/types/auth";

/**
 * 
 */
export function login(data: LoginForm) {
    return request.post('/auth/login', data)
}

/**
 * 
 * @returns 
 */
export function getUserInfo() {
    return request.get('/user/info')
}

/**
 * 
 * @returns 
 */
export function logout() {
    return request.get('/user/logout')
}