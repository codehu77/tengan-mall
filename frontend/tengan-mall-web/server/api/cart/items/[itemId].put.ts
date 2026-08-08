export default defineEventHandler(async (event) => {
  const itemId = getRouterParam(event, 'itemId')
  const body = await readBody<{ count: number }>(event)
  await callCartBackend(event, `/api/customer/cart/items/${itemId}`, {
    method: 'PUT',
    body: { count: body.count },
  })
  const { count } = await callCartBackend<{ count: number }>(event, '/api/customer/cart/count')
  return count
})
