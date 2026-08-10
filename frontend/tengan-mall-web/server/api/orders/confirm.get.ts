import type { OrderConfirmResult } from '~/types/order'

export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)

  return await callBackend<OrderConfirmResult>('/api/customer/orders/confirm', {
    headers: { Authorization: `Bearer ${accessToken}` },
  })
})
