import type { PointBatch } from '~/types/points'

type BackendBatch = { batchId: number; points: number; earnedAt: string; expiresAt: string; sourceOrderSn?: string }

export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)
  const { items } = await callBackend<{ items: BackendBatch[] }>('/api/customer/wallet/points/expiring', {
    headers: { Authorization: `Bearer ${accessToken}` },
  })
  return items.map((b): PointBatch => ({ ...b, batchId: String(b.batchId) }))
})
