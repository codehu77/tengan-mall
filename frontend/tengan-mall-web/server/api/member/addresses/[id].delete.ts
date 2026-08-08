export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)
  const id = getRouterParam(event, 'id')

  await callBackend(`/api/customer/member/addresses/${id}`, {
    method: 'DELETE',
    headers: { Authorization: `Bearer ${accessToken}` },
  })

  return { success: true }
})
