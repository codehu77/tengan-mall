import type { PaymentMethodsResult } from '~/types/payment'

export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)

  return await callBackend<PaymentMethodsResult>('/api/customer/payments/methods', {
    headers: { Authorization: `Bearer ${accessToken}` },
  })
})
