import request from "@/utils/request";
import type { LoginForm, RegisterForm } from "@/types/auth";


export function login(data: LoginForm) {
    return request.post('/auth/login', data)
}

export function register(data: RegisterForm) {
    return request.post('/auth/register', data)
}

export function getUserInfo() {
    return request.get('/user/info')
}

export function logout() {
    return request.get('/user/logout')
}