/** 已登入狀態下自助連結 LINE 帳號，走 /api/customer/** 需要 access token。 */
export default defineEventHandler(async (event) => {
  const sessionAccessToken = requireAccessToken(event)
  const body = await readBody<{ code: string; state: string }>(event)
  const nonce = verifyLineOAuthState(event, 'link', body.state)

  await callBackend('/api/customer/auth/oauth2/line/link', {
    method: 'POST',
    headers: { Authorization: `Bearer ${sessionAccessToken}` },
    body: { code: body.code, nonce },
  })

  return { success: true }
})
