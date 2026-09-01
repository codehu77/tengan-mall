/**
 * access token TTL 只有 15 分鐘，靠使用者重新登入撐過去的話前台等於每 15 分鐘就把人踢下線。
 * 在每個 request 進 route handler 前先檢查一次：access token 沒了或快過期、但 refresh token
 * 還在，就先用 refresh token 換一組新的塞回 cookie，並掛在 event.context 讓同一個 request
 * 內的 requireAccessToken/optionalAccessToken 直接拿到新值——它們讀 cookie 用的是 h3 從
 * request header 解出來的舊值，setCookie 只影響回應，不會讓同一 request 之後的 getCookie
 * 讀到新值。
 *
 * refresh cookie 的 maxAge 吃後端這次回傳的 refreshTokenTtlSeconds，不是寫死常數——
 * remember-me 的 session 每次自動續期都要維持 30 天，非 remember-me 的維持 ~1 天，
 * 兩者靠 rememberMe 存在 Redis 裡的 RefreshTokenEntry 一路沿用，見 tengan-auth 的
 * RedisRefreshTokenStoreAdapter.rotate()。
 *
 * 沒有 refresh token（訪客、或從沒登入過）直接 return，這是最大宗流量，成本只有一次
 * getCookie，不會打到後端。
 */
export default defineEventHandler(async (event) => {
  const config = useRuntimeConfig()
  const refreshToken = getCookie(event, config.refreshCookieName)
  if (!refreshToken) {
    return
  }

  const accessToken = getCookie(event, config.cookieName)
  if (accessToken && !isAccessTokenExpiringSoon(accessToken)) {
    return
  }

  try {
    const result = await refreshAccessToken(refreshToken)
    setAuthCookies(event, result.accessToken, result.refreshToken, result.refreshTokenTtlSeconds)
    event.context.accessToken = result.accessToken
  } catch {
    // refresh token 過期/已撤銷/重複使用被 revoke——清掉兩顆 cookie，讓後續 requireAccessToken
    // 照原本的 401 邏輯逼使用者重新登入，不要留著一顆換不到新 access token 的髒 cookie。
    deleteCookie(event, config.cookieName, { path: '/' })
    deleteCookie(event, config.refreshCookieName, { path: '/' })
  }
})
