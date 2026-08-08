import type { MemberAddress } from '~/types/member'

export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)

  return await callBackend<MemberAddress[]>('/api/customer/member/addresses', {
    headers: { Authorization: `Bearer ${accessToken}` },
  })
})
