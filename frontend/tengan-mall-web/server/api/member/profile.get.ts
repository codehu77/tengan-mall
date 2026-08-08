export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)

  return await callBackend<{
    id: number
    username: string
    phone: string | null
    nickname: string
    avatarUrl: string | null
  }>('/api/customer/member/profile', {
    headers: { Authorization: `Bearer ${accessToken}` },
  })
})
