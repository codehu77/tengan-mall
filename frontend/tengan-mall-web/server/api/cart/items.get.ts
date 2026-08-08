import type { BackendCartLine } from '../../utils/toCartItem'

export default defineEventHandler(async (event) => {
  const result = await callCartBackend<{ items: BackendCartLine[] }>(event, '/api/customer/cart/items')
  return result.items.map(toCartItem)
})
