import CryptoJS from 'crypto-js'

// 默认加密密钥（实际项目中建议从环境变量获取）
const SECRET_KEY = import.meta.env.VITE_ENCRYPT_SECRET_KEY || 'little_stone_secret_key'
const SALT = import.meta.env.VITE_ENCRYPT_SALT || 'little_stone_salt'

/**
 * MD5加密（不可逆，常用于密码传输）
 * @param text 待加密文本
 * @returns 加密后的文本
 */
export const encryptMd5 = (text: string): string => {
  return CryptoJS.MD5(text).toString()
}

/**
 * SHA256加密（不可逆，安全性更高）
 * @param text 待加密文本
 * @returns 加密后的文本
 */
export const encryptSha256 = (text: string): string => {
  return CryptoJS.SHA256(text).toString()
}

/**
 * AES加密（可逆，用于需要解密的场景）
 * @param text 待加密文本
 * @param key 加密密钥
 * @returns 加密后的文本
 */
export const encryptAes = (text: string, key: string = SECRET_KEY): string => {
  return CryptoJS.AES.encrypt(text, key).toString()
}

/**
 * AES解密
 * @param encryptedText 加密文本
 * @param key 解密密钥
 * @returns 解密后的文本
 */
export const decryptAes = (encryptedText: string, key: string = SECRET_KEY): string => {
  const bytes = CryptoJS.AES.decrypt(encryptedText, key)
  return bytes.toString(CryptoJS.enc.Utf8)
}

/**
 * 带盐值的MD5加密（更安全）
 * @param text 待加密文本
 * @param salt 盐值（可选，默认使用固定盐值）
 * @returns 加密后的文本
 */
export const encryptMd5WithSalt = (text: string, salt: string = SALT): string => {
  return CryptoJS.MD5(text + salt).toString()
}

/**
 * AES可逆加密（用于密码传输，后端可解密还原原始密码）
 * 使用 密码 + 动态盐 的方式进行AES加密
 * @param password 原始密码
 * @param dynamicSalt 动态获取的盐值（后端存储在Redis中用于解密）
 * @returns 加密后的密码（后端可用相同密钥和盐解密）
 */
export const encryptPassword = (password: string, dynamicSalt: string): string => {
  // 密钥 = 动态盐（你要的就是这个）
  const key = CryptoJS.enc.Utf8.parse(dynamicSalt);
  
  // 固定安全模式（后端完全对应）
  const encrypted = CryptoJS.AES.encrypt(
    password,
    key,
    {
      mode: CryptoJS.mode.ECB,
      padding: CryptoJS.pad.Pkcs7
    }
  );

  // 返回密文传给后端
  return encrypted.toString();
}