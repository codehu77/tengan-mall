export interface TransactionCountItem {
  type: string
  status: string
  count: number
}

export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)
  const { items } = await callBackend<{ items: TransactionCountItem[] }>(
    '/api/customer/wallet/points/transactions/counts',
    { headers: { Authorization: `Bearer ${accessToken}` } },
  )
  return items
})
