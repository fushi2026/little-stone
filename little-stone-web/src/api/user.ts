import request from '@/utils/request'
import { encryptPassword } from '@/utils/encrypt'
import { getDynamicSalt } from './auth'
import { useUserStore } from '@/stores/user'


export function getUserInfo() {
    return request.get('/user/info')
}

/**
 * 修改密码
 * @param oldPassword 旧密码
 * @param newPassword 新密码
 */
export async function changePassword(oldPassword: string, newPassword: string) {

    const userStore = useUserStore()
    const username = userStore.userInfo?.username || ''
    
    // 获取动态盐
    const saltResponse = await getDynamicSalt(username)
    
    // 加密旧密码和新密码
    const encryptedOldPassword = encryptPassword(oldPassword, saltResponse.salt)
    const encryptedNewPassword = encryptPassword(newPassword, saltResponse.salt)
    
    // 发送修改密码请求
    const res = await request.post('/user/change-password', {
        oldPassword: encryptedOldPassword,
        newPassword: encryptedNewPassword,
        nonce: saltResponse.nonce
    })
    
    return res.data
}