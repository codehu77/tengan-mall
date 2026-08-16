import type { PointAccountSummary } from '~/types/points'

// 比照 useOrder.ts：一律用 useRequestFetch()，SSR 頁面才能正確帶上瀏覽器原本的 Authorization cookie。
export function useWallet() {
  const fetch = useRequestFetch()

  async function fetchPointsSummary(): Promise<PointAccountSummary> {
    return fetch('/api/wallet/summary')
  }

  async function previewRedeemPoints(orderAmount: number, points: number): Promise<{ valid: boolean; discountAmount: number }> {
    return fetch('/api/wallet/redeem', { method: 'POST', body: { orderAmount, points } })
  }

  return {
    fetchPointsSummary,
    previewRedeemPoints,
  }
}
