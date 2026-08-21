import type { BackendCartLine } from '../../utils/toCartItem'

export default defineEventHandler(async (event) => {
  const [result, seckillMap] = await Promise.all([
    callCartBackend<{ items: BackendCartLine[] }>(event, '/api/customer/cart/items'),
    fetchActiveSeckillMap(),
  ])
  return result.items.map(line => toCartItem(line, seckillMap))
})
