/**
 * 購物車要同時服務會員/訪客——跟 requireAccessToken 不同，這裡沒有 cookie 不丟 401，
 * 直接回傳 undefined 讓呼叫端當訪客處理。
 */
export function optionalAccessToken(event: Parameters<typeof getCookie>[0]) {
  const config = useRuntimeConfig()
  return getCookie(event, config.cookieName)
}
