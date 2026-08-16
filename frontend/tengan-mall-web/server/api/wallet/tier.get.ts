import type { TierInfo } from '~/types/points'

export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)
  return await callBackend<TierInfo>('/api/customer/wallet/points/tier', {
    headers: { Authorization: `Bearer ${accessToken}` },
  })
})
