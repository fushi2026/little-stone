import FingerprintJS from '@fingerprintjs/fingerprintjs'

// 定义类型
type FingerprintAgent = Awaited<ReturnType<typeof FingerprintJS.load>>
type FingerprintResult = Awaited<ReturnType<FingerprintAgent['get']>>

let fpPromise: Promise<FingerprintAgent> | null = null

/**
 * 初始化 FingerprintJS 代理
 */
const initFingerprint = async (): Promise<FingerprintAgent> => {
  if (!fpPromise) {
    fpPromise = FingerprintJS.load()
  }
  return fpPromise
}

/**
 * 生成设备指纹（使用 @fingerprintjs/fingerprintjs 开源库）
 * 该库是业界广泛使用的设备指纹识别库，具有以下特点：
 * 1. 高度准确的设备识别
 * 2. 跨浏览器兼容性好
 * 3. 支持各种设备类型（桌面、移动、平板）
 * 4. 定期更新以应对浏览器变化
 * 5. 社区活跃，安全性有保障
 */
export const generateDeviceFingerprint = async (): Promise<string> => {
  const fp = await initFingerprint()
  const result = await fp.get()
  return result.visitorId
}

/**
 * 获取设备指纹（带缓存）
 * 指纹会保存在 localStorage 中，避免每次都重新生成
 */
export const getDeviceFingerprint = async (): Promise<string> => {
  const storageKey = 'device_fingerprint'
  
  // 尝试从缓存获取
  const cached = localStorage.getItem(storageKey)
  if (cached) {
    return cached
  }
  
  // 使用 FingerprintJS 生成新的指纹
  const fingerprint = await generateDeviceFingerprint()
  // 缓存到 localStorage
  localStorage.setItem(storageKey, fingerprint)
  
  return fingerprint
}

/**
 * 清除设备指纹缓存
 */
export const clearDeviceFingerprint = (): void => {
  localStorage.removeItem('device_fingerprint')
}

/**
 * 获取详细的设备信息（可选）
 * 如果需要更详细的设备信息用于分析，可以使用此方法
 */
export const getDeviceInfo = async (): Promise<FingerprintResult | null> => {
  try {
    const fp = await initFingerprint()
    return await fp.get()
  } catch (error) {
    console.error('Failed to get device info:', error)
    return null
  }
}

