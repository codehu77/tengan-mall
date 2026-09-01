export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)
  const body = await readBody<{ newPhone: string; code: string }>(event)

  await callBackend('/api/customer/auth/contact/phone/verify', {
    method: 'POST',
    headers: { Authorization: `Bearer ${accessToken}` },
    body,
  })

  return { success: true }
})
