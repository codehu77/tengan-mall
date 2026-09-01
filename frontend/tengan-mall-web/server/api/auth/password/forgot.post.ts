export default defineEventHandler(async (event) => {
  const body = await readBody<{ identifier: string }>(event)
  return await callBackend<{ message: string }>('/api/public/auth/password/forgot', {
    method: 'POST',
    body,
  })
})
