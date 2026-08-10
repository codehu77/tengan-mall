import type { MyCoupon } from '~/types/coupon'

export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)
  const query = getQuery(event)

  const { items } = await callBackend<{ items: MyCoupon[] }>('/api/customer/coupons/available', {
    query: { amount: query.amount },
    headers: { Authorization: `Bearer ${accessToken}` },
  })
  return items
})
