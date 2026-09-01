/** 已登入狀態下自助連結 Google 帳號，走 /api/customer/** 需要 access token。 */
export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)
  const body = await readBody<{ idToken: string }>(event)

  await callBackend('/api/customer/auth/oauth2/google/link', {
    method: 'POST',
    headers: { Authorization: `Bearer ${accessToken}` },
    body,
  })

  return { success: true }
})
