export default defineEventHandler(async (event) => {
  await callCartBackend(event, '/api/customer/cart/items/checked', { method: 'DELETE' })
  const { count } = await callCartBackend<{ count: number }>(event, '/api/customer/cart/count')
  return count
})
