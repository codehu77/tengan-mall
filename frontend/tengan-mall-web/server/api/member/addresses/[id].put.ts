export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)
  const id = getRouterParam(event, 'id')
  const body = await readBody<{
    receiverName: string
    receiverPhone: string
    city: string
    district: string
    postalCode: string
    street: string
  }>(event)

  await callBackend(`/api/customer/member/addresses/${id}`, {
    method: 'PUT',
    body,
    headers: { Authorization: `Bearer ${accessToken}` },
  })

  return { success: true }
})
