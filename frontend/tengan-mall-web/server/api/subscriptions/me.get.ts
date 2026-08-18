import type { MySubscriptionResult } from '~/types/subscription'

export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)

  return await callBackend<MySubscriptionResult>('/api/customer/payments/subscriptions/me', {
    headers: { Authorization: `Bearer ${accessToken}` },
  })
})
