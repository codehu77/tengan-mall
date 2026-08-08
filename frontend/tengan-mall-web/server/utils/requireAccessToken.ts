/**
 * server/api/member/** 底下每支路由都要讀 HttpOnly cookie 換 access token、沒有就 401，
 * 跟 server/api/auth/me.get.ts 原本內嵌的邏輯一致，這裡抽成共用工具避免七支路由各自複製貼上。
 */
export function requireAccessToken(event: Parameters<typeof getCookie>[0]) {
  const config = useRuntimeConfig()
  const accessToken = getCookie(event, config.cookieName)
  if (!accessToken) {
    throw createError({ statusCode: 401, statusMessage: '未登入' })
  }
  return accessToken
}
