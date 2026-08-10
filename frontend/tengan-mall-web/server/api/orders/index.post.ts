import type { CreateOrderPayload, CreateOrderResult } from '~/types/order'

export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)
  const body = await readBody<CreateOrderPayload>(event)

  return await callBackend<CreateOrderResult>('/api/customer/orders', {
    method: 'POST',
    body,
    headers: { Authorization: `Bearer ${accessToken}` },
  })
})
