/**
 * 同一顆 refresh token 在同一次瀏覽器頁面載入時，常常被好幾支 /api/** 併發打到（profile、
 * addresses、orders 等各自的 server route）——如果每支都各自呼叫一次 /refresh，會撞上
 * RefreshService 的 rotation + reuse detection：第一個成功後這顆 refresh token 就標記 used，
 * 第二個晚一步用同一顆舊 token 進來就會被判定重複使用，整個 family 被撤銷、使用者被強制登出。
 * 用 refresh token 當 key 把併發請求收斂成同一個 in-flight promise，確保同一顆舊 token
 * 只真的送出一次 /refresh。
 */
const inFlightRefreshes = new Map<string, Promise<{ accessToken: string; refreshToken: string }>>()

export function refreshAccessToken(refreshToken: string): Promise<{ accessToken: string; refreshToken: string }> {
  const existing = inFlightRefreshes.get(refreshToken)
  if (existing) {
    return existing
  }

  const promise = callBackend<{ accessToken: string; refreshToken: string }>('/api/customer/auth/refresh', {
    method: 'POST',
    body: { refreshToken },
  }).finally(() => {
    inFlightRefreshes.delete(refreshToken)
  })

  inFlightRefreshes.set(refreshToken, promise)
  return promise
}
