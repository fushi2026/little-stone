import request from '@/utils/request'
import { encryptPassword } from '@/utils/encrypt'
import type { LoginForm, SaltResponse, RegisterForm } from '@/types/auth'

/**
 * 获取动态盐值
 * 每次请求都会返回不同的盐值，用于加密密码
 * @param username 用户名（后端可根据用户名生成特定盐值）
 */
export const getDynamicSalt = async (username: string): Promise<SaltResponse> => {
  const res = await request.get('/auth/salt', {
    params: { username }
  })
  return res.data
}

/**
 * 登录（带动态盐加密）
 * @param form 登录表单
 */
export const loginWithEncrypt = async (form: LoginForm): Promise<any> => {
  // 1. 获取动态盐（传入用户名）
  const saltResponse = await getDynamicSalt(form.username)
  
  // 2. 使用动态盐加密密码
  const encryptedPassword = encryptPassword(form.password, saltResponse.salt)
  
  // 3. 发送登录请求（密码已加密）
  const res = await request.post('/auth/login', {
    ...form,
    encryptedPassword: encryptedPassword,
    nonce: saltResponse.nonce
  })
  
  return res.data
}

/**
 * 注册（带动态盐加密）
 * @param form 注册表单
 */
export const registerWithEncrypt = async (form: RegisterForm): Promise<any> => {
  // 1. 获取动态盐（传入用户名）
  const saltResponse = await getDynamicSalt(form.username)
  
  // 2. 使用动态盐加密密码
  const encryptedPassword = encryptPassword(form.password, saltResponse.salt)
  
  // 3. 发送注册请求（密码已加密）
  const res = await request.post('/auth/register', {
    ...form,
    encryptedPassword: encryptedPassword,
    nonce: saltResponse.nonce
  })
  
  return res.data
}