export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)
  const body = await readBody<{ nickname: string; avatarUrl: string | null }>(event)

  await callBackend('/api/customer/member/profile', {
    method: 'PUT',
    body,
    headers: { Authorization: `Bearer ${accessToken}` },
  })

  return { success: true }
})
