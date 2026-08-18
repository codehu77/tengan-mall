export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)

  await callBackend('/api/customer/payments/subscriptions/cancel', {
    method: 'PUT',
    headers: { Authorization: `Bearer ${accessToken}` },
  })
})
