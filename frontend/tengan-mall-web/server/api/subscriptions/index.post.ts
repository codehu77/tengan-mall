import type { SubscribeResult, SubscriptionTargetTier } from '~/types/subscription'

export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)
  const body = await readBody<{ targetTier: SubscriptionTargetTier }>(event)

  return await callBackend<SubscribeResult>('/api/customer/payments/subscriptions', {
    method: 'POST',
    body,
    headers: { Authorization: `Bearer ${accessToken}` },
  })
})
