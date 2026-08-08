export default defineEventHandler(async (event) => {
  const itemId = getRouterParam(event, 'itemId')
  await callCartBackend(event, `/api/customer/cart/items/${itemId}`, { method: 'DELETE' })
  const { count } = await callCartBackend<{ count: number }>(event, '/api/customer/cart/count')
  return count
})
