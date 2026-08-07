export default defineEventHandler(async (event) => {
  const body = await readBody<{ username: string; phone: string; password: string; code: string }>(event)
  return await callBackend<{ accountId: number; username: string }>('/api/public/auth/register', {
    method: 'POST',
    body,
  })
})
