import type { PaymentStatusResult } from '~/types/payment'

export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)
  const orderSn = getRouterParam(event, 'orderSn')

  return await callBackend<PaymentStatusResult>(`/api/customer/payments/${orderSn}`, {
    headers: { Authorization: `Bearer ${accessToken}` },
  })
})
