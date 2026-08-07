/**
 * BFF pattern：access/refresh token 只存在 HttpOnly cookie，不回傳給前端 JS（見
 * project_overview「Nuxt3 前台技術決策」）。access token TTL 15 分鐘、refresh token TTL 7 天，
 * 跟 tengan-auth 那邊的設定對齊（AccessTokenIssuerAdapter / RedisRefreshTokenStoreAdapter）。
 */
export default defineEventHandler(async (event) => {
  const body = await readBody<{ username: string; password: string }>(event)
  const result = await callBackend<{
    accessToken: string
    refreshToken: string
    accountId: number
    username: string
  }>('/api/public/auth/login', {
    method: 'POST',
    body,
  })

  const config = useRuntimeConfig()
  setCookie(event, config.cookieName, result.accessToken, {
    httpOnly: true,
    sameSite: 'lax',
    path: '/',
    maxAge: 15 * 60,
  })
  setCookie(event, config.refreshCookieName, result.refreshToken, {
    httpOnly: true,
    sameSite: 'lax',
    path: '/',
    maxAge: 7 * 24 * 60 * 60,
  })

  return { accountId: result.accountId, username: result.username }
})
