import type { BackendCartLine } from '../../utils/toCartItem'

export default defineEventHandler(async (event) => {
  const query = getQuery(event)
  const limit = Number(query.limit ?? 5)
  const result = await callCartBackend<{ items: BackendCartLine[]; totalItemCount: number }>(
    event, `/api/customer/cart/mini?limit=${limit}`)
  return { items: result.items.map(toCartItem), total: result.totalItemCount }
})
