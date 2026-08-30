/**
 * server/api/member/** 底下每支路由都要讀 HttpOnly cookie 換 access token、沒有就 401，
 * 跟 server/api/auth/me.get.ts 原本內嵌的邏輯一致，這裡抽成共用工具避免七支路由各自複製貼上。
 *
 * 優先讀 event.context.accessToken——如果 server/middleware/refresh-token.ts 這個 request
 * 剛好刷新過 access token，新值只掛在 context，cookie 讀到的還是舊值（setCookie 只影響回應）。
 */
export function requireAccessToken(event: Parameters<typeof getCookie>[0]) {
  const config = useRuntimeConfig()
  const contextAccessToken = (event as unknown as { context: { accessToken?: string } }).context.accessToken
  const accessToken = contextAccessToken ?? getCookie(event, config.cookieName)
  if (!accessToken) {
    throw createError({ statusCode: 401, statusMessage: '未登入' })
  }
  return accessToken
}
