export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)
  const body = await readBody<{ categoryId: number; source: 'VIEW' | 'SEARCH' }>(event)

  await callBackend('/api/customer/member/category-interest', {
    method: 'POST',
    body,
    headers: { Authorization: `Bearer ${accessToken}` },
  })
})
