import type { OrderSummary } from '~/types/order'

export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)
  const query = getQuery(event)

  return await callBackend<{ items: OrderSummary[]; total: number }>('/api/customer/orders', {
    query: { status: query.status, page: query.page ?? 1, pageSize: query.pageSize ?? 20 },
    headers: { Authorization: `Bearer ${accessToken}` },
  })
})
