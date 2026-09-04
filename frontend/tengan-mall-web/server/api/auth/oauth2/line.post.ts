/** LINE 登入即 auto-login（視同 rememberMe=true），跟 google.post.ts/facebook.post.ts 一樣要設 cookie。 */
export default defineEventHandler(async (event) => {
  const body = await readBody<{ code: string; state: string }>(event)
  const nonce = verifyLineOAuthState(event, 'login', body.state)

  const result = await callBackend<{
    accountId: number
    accessToken: string
    refreshToken: string
    refreshTokenTtlSeconds: number
  }>('/api/public/auth/oauth2/line', {
    method: 'POST',
    body: { code: body.code, nonce },
  })

  setAuthCookies(event, result.accessToken, result.refreshToken, result.refreshTokenTtlSeconds)

  return { accountId: result.accountId }
})
