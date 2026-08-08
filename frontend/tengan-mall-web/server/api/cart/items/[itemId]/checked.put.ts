export default defineEventHandler(async (event) => {
  const itemId = getRouterParam(event, 'itemId')
  const body = await readBody<{ checked: boolean }>(event)
  await callCartBackend(event, `/api/customer/cart/items/${itemId}/checked`, {
    method: 'PUT',
    body: { checked: body.checked },
  })
})
