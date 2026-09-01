export default defineEventHandler(async (event) => {
  const body = await readBody<{ identifier: string; code: string }>(event)
  return await callBackend<{ resetToken: string }>('/api/public/auth/password/forgot/verify', {
    method: 'POST',
    body,
  })
})
