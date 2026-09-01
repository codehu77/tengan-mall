export default defineEventHandler(async (event) => {
  const body = await readBody<{ identifier: string }>(event)
  return await callBackend<{ code: string }>('/api/public/auth/register/start', {
    method: 'POST',
    body,
  })
})
