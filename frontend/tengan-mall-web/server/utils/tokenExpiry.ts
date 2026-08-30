/**
 * 只解 JWT payload 讀 exp，不驗簽章——簽章驗證是 Gateway/後端的責任，這裡只是拿來判斷
 * 「要不要主動刷新」的本地捷徑，解不出來就當作已過期處理，讓後續刷新流程接手。
 */
export function isAccessTokenExpiringSoon(token: string, bufferMs = 10_000): boolean {
  const payload = decodeJwtPayload(token)
  if (!payload || typeof payload.exp !== 'number') {
    return true
  }
  return payload.exp * 1000 <= Date.now() + bufferMs
}

function decodeJwtPayload(token: string): Record<string, unknown> | null {
  try {
    const [, payloadSegment] = token.split('.')
    // atob 只吃標準 base64（+/），JWT 用的是 base64url（-_、無 padding），先轉過去。
    const base64 = payloadSegment.replace(/-/g, '+').replace(/_/g, '/')
    const json = atob(base64)
    return JSON.parse(json)
  } catch {
    return null
  }
}
