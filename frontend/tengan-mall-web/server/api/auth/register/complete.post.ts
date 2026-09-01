/** 註冊完成即 auto-login（視同 rememberMe=true），跟 login.post.ts 一樣要設 cookie。 */
export default defineEventHandler(async (event) => {
  const body = await readBody<{ registrationToken: string; password: string }>(event)
  const result = await callBackend<{
    accountId: number
    accessToken: string
    refreshToken: string
    refreshTokenTtlSeconds: number
  }>('/api/public/auth/register/complete', {
    method: 'POST',
    body,
  })

  setAuthCookies(event, result.accessToken, result.refreshToken, result.refreshTokenTtlSeconds)

  return { accountId: result.accountId }
})
