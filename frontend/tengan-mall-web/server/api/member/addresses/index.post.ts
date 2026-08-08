import type { AddressFormPayload } from '~/types/member'

export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)
  const body = await readBody<AddressFormPayload>(event)

  return await callBackend<{ id: number }>('/api/customer/member/addresses', {
    method: 'POST',
    body,
    headers: { Authorization: `Bearer ${accessToken}` },
  })
})
