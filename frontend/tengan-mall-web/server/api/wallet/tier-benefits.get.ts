import type { TierBenefit } from '~/types/points'

// 公開端點，不需要登入——前台會員等級比較表可以在未登入狀態瀏覽。
export default defineEventHandler(async () => {
  const { items } = await callBackend<{ items: TierBenefit[] }>('/api/public/wallet/points/tier/benefits')
  return items
})
