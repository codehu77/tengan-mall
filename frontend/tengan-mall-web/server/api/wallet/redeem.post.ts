export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)
  const body = await readBody(event)

  return await callBackend<{ valid: boolean; discountAmount: number }>('/api/customer/wallet/points/redeem', {
    method: 'POST',
    body,
    headers: { Authorization: `Bearer ${accessToken}` },
  })
})
