export default defineEventHandler(async (event) => {
  const { count } = await callCartBackend<{ count: number }>(event, '/api/customer/cart/count')
  return count
})
