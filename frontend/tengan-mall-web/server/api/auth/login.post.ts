/**
 * BFF pattern：access/refresh token 只存在 HttpOnly cookie，不回傳給前端 JS（見
 * project_overview「Nuxt3 前台技術決策」）。access token TTL 固定 15 分鐘；refresh token TTL
 * 依 rememberMe 而定（未勾選 ≈ 1 天／勾選 ≈ 30 天），實際秒數以後端回傳的
 * refreshTokenTtlSeconds 為準，不在這裡寫死。
 */
export default defineEventHandler(async (event) => {
  const body = await readBody<{ identifier: string; password: string; rememberMe: boolean }>(event)
  const result = await callBackend<{
    accessToken: string
    refreshToken: string
    accountId: number
    refreshTokenTtlSeconds: number
  }>('/api/public/auth/login', {
    method: 'POST',
    body,
  })

  setAuthCookies(event, result.accessToken, result.refreshToken, result.refreshTokenTtlSeconds)

  return { accountId: result.accountId }
})
