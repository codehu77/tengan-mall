export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)

  return await callBackend<{ accountId: number; username: string; phone: string }>('/api/customer/auth/me', {
    headers: { Authorization: `Bearer ${accessToken}` },
  })
})
