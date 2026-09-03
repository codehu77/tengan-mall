/** 已登入狀態下自助連結 Facebook 帳號，走 /api/customer/** 需要 access token。 */
export default defineEventHandler(async (event) => {
  const sessionAccessToken = requireAccessToken(event)
  const body = await readBody<{ accessToken: string }>(event)

  await callBackend('/api/customer/auth/oauth2/facebook/link', {
    method: 'POST',
    headers: { Authorization: `Bearer ${sessionAccessToken}` },
    body,
  })

  return { success: true }
})
