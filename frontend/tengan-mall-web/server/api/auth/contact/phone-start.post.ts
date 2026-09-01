export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)
  const body = await readBody<{ newPhone: string; currentPassword: string }>(event)

  return await callBackend<{ code: string }>('/api/customer/auth/contact/phone/start', {
    method: 'POST',
    headers: { Authorization: `Bearer ${accessToken}` },
    body,
  })
})
