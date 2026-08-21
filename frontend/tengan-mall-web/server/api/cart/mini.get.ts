import type { BackendCartLine } from '../../utils/toCartItem'

export default defineEventHandler(async (event) => {
  const query = getQuery(event)
  const limit = Number(query.limit ?? 5)
  const [result, seckillMap] = await Promise.all([
    callCartBackend<{ items: BackendCartLine[]; totalItemCount: number }>(
      event, `/api/customer/cart/mini?limit=${limit}`),
    fetchActiveSeckillMap(),
  ])
  return { items: result.items.map(line => toCartItem(line, seckillMap)), total: result.totalItemCount }
})
