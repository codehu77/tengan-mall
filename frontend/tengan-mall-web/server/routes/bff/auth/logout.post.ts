export default defineEventHandler(async (event) => {
  const config = useRuntimeConfig()
  const refreshToken = getCookie(event, config.refreshCookieName)

  if (refreshToken) {
    // 後端登出失敗（例如 refresh token 已過期）也不擋前端清 cookie，使用者的登出意圖優先。
    await callBackend('/api/customer/auth/logout', {
      method: 'POST',
      body: { refreshToken },
    }).catch(() => {})
  }

  deleteCookie(event, config.cookieName, { path: '/' })
  deleteCookie(event, config.refreshCookieName, { path: '/' })
  return { success: true }
})
