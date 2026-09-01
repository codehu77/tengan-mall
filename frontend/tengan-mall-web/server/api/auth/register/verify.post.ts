export default defineEventHandler(async (event) => {
  const body = await readBody<{ identifier: string; code: string }>(event)
  return await callBackend<{ registrationToken: string }>('/api/public/auth/register/verify', {
    method: 'POST',
    body,
  })
})
