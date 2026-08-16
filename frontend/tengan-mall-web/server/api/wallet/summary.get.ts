import type { PointAccountSummary } from '~/types/points'

export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)
  return await callBackend<PointAccountSummary>('/api/customer/wallet/points/summary', {
    headers: { Authorization: `Bearer ${accessToken}` },
  })
})
