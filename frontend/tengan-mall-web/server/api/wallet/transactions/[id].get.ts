import type { PointTransaction } from '~/types/points'

type BackendTransaction = Omit<PointTransaction, 'id'> & { id: number }

export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)
  const id = getRouterParam(event, 'id')

  const t = await callBackend<BackendTransaction>(`/api/customer/wallet/points/transactions/${id}`, {
    headers: { Authorization: `Bearer ${accessToken}` },
  })
  return { ...t, id: String(t.id) } satisfies PointTransaction
})
