import type { PointTransaction, TransactionQueryResult } from '~/types/points'

type BackendTransaction = Omit<PointTransaction, 'id'> & { id: number }
type BackendPage = { items: BackendTransaction[]; total: number }

export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)
  const query = getQuery(event)

  const page = await callBackend<BackendPage>('/api/customer/wallet/points/transactions', {
    query: {
      type: query.type,
      status: query.status,
      dateRange: query.dateRange,
      keyword: query.keyword,
      page: query.page ?? 1,
      pageSize: query.pageSize ?? 10,
    },
    headers: { Authorization: `Bearer ${accessToken}` },
  })
  const result: TransactionQueryResult = {
    items: page.items.map(t => ({ ...t, id: String(t.id) })),
    total: page.total,
    page: Number(query.page ?? 1),
    pageSize: Number(query.pageSize ?? 10),
  }
  return result
})
