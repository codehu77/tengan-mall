export default defineEventHandler(async (event) => {
  const body = await readBody<{ checked: boolean }>(event)
  await callCartBackend(event, '/api/customer/cart/checked-all', {
    method: 'PUT',
    body: { checked: body.checked },
  })
})
