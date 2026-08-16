import type { PointFaqItem } from '~/types/points'

// 公開端點，不需要登入。
export default defineEventHandler(async () => {
  const { items } = await callBackend<{ items: PointFaqItem[] }>('/api/public/wallet/points/faq')
  return items
})
