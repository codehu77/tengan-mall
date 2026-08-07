export default defineEventHandler(async (event) => {
  const config = useRuntimeConfig()
  const accessToken = getCookie(event, config.cookieName)
  if (!accessToken) {
    throw createError({ statusCode: 401, statusMessage: '未登入' })
  }

  return await callBackend<{ accountId: number; username: string; phone: string }>('/api/customer/auth/me', {
    headers: { Authorization: `Bearer ${accessToken}` },
  })
})
