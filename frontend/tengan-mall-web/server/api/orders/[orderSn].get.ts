import type { OrderDetail } from '~/types/order'

export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)
  const orderSn = getRouterParam(event, 'orderSn')

  return await callBackend<OrderDetail>(`/api/customer/orders/${orderSn}`, {
    headers: { Authorization: `Bearer ${accessToken}` },
  })
})
