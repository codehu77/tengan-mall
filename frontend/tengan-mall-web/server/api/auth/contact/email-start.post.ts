export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)
  const body = await readBody<{ newEmail: string; currentPassword: string }>(event)

  return await callBackend<{ code: string }>('/api/customer/auth/contact/email/start', {
    method: 'POST',
    headers: { Authorization: `Bearer ${accessToken}` },
    body,
  })
})
