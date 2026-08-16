export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)
  const orderSn = getRouterParam(event, 'orderSn')
  const body = await readBody<{ transactionId: string }>(event)

  await callBackend(`/api/customer/payments/${orderSn}/linepay-confirm`, {
    method: 'POST',
    body,
    headers: { Authorization: `Bearer ${accessToken}` },
  })
})
