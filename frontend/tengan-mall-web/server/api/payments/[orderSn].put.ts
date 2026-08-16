import type { InitiatePaymentResult } from '~/types/payment'

export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)
  const orderSn = getRouterParam(event, 'orderSn')
  const body = await readBody<{ method: string }>(event)

  return await callBackend<InitiatePaymentResult>(`/api/customer/payments/${orderSn}`, {
    method: 'PUT',
    body,
    headers: { Authorization: `Bearer ${accessToken}` },
  })
})
