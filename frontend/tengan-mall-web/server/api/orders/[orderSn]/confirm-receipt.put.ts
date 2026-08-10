export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)
  const orderSn = getRouterParam(event, 'orderSn')

  await callBackend(`/api/customer/orders/${orderSn}/confirm-receipt`, {
    method: 'PUT',
    headers: { Authorization: `Bearer ${accessToken}` },
  })
})
