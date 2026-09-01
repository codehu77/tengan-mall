export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)
  const body = await readBody<{ newEmail: string; code: string }>(event)

  await callBackend('/api/customer/auth/contact/email/verify', {
    method: 'POST',
    headers: { Authorization: `Bearer ${accessToken}` },
    body,
  })

  return { success: true }
})
