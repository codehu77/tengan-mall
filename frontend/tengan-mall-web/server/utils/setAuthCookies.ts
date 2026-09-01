/**
 * login/register-complete/password-reset 三個「登入完成」路徑共用同一套 cookie 設定邏輯——
 * access token cookie 固定 15 分鐘，refresh token cookie 的 maxAge 直接吃後端算好的
 * refreshTokenTtlSeconds（依 rememberMe 決定 1 天或 30 天），不在前端重複寫死秒數。
 */
export function setAuthCookies(
  event: Parameters<typeof setCookie>[0],
  accessToken: string,
  refreshToken: string,
  refreshTokenTtlSeconds: number,
) {
  const config = useRuntimeConfig()
  setCookie(event, config.cookieName, accessToken, {
    httpOnly: true,
    sameSite: 'lax',
    path: '/',
    maxAge: 15 * 60,
  })
  setCookie(event, config.refreshCookieName, refreshToken, {
    httpOnly: true,
    sameSite: 'lax',
    path: '/',
    maxAge: refreshTokenTtlSeconds,
  })
}
