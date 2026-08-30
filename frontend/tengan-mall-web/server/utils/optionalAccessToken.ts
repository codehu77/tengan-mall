/**
 * 購物車要同時服務會員/訪客——跟 requireAccessToken 不同，這裡沒有 cookie 不丟 401，
 * 直接回傳 undefined 讓呼叫端當訪客處理。同樣優先讀 event.context.accessToken，理由見
 * requireAccessToken 的註解。
 */
export function optionalAccessToken(event: Parameters<typeof getCookie>[0]) {
  const config = useRuntimeConfig()
  const contextAccessToken = (event as unknown as { context: { accessToken?: string } }).context.accessToken
  return contextAccessToken ?? getCookie(event, config.cookieName)
}
