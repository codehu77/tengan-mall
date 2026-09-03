/** Facebook 登入即 auto-login（視同 rememberMe=true），跟 google.post.ts 一樣要設 cookie。 */
export default defineEventHandler(async (event) => {
  const body = await readBody<{ accessToken: string }>(event)
  const result = await callBackend<{
    accountId: number
    accessToken: string
    refreshToken: string
    refreshTokenTtlSeconds: number
  }>('/api/public/auth/oauth2/facebook', {
    method: 'POST',
    body,
  })

  setAuthCookies(event, result.accessToken, result.refreshToken, result.refreshTokenTtlSeconds)

  return { accountId: result.accountId }
})
