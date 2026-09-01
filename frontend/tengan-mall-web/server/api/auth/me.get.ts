export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)

  return await callBackend<{ accountId: number; phone: string | null; email: string | null }>(
    '/api/customer/auth/me',
    { headers: { Authorization: `Bearer ${accessToken}` } },
  )
})
