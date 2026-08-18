import type {
  PointAccountSummary,
  TierInfo,
  TierBenefit,
  PointBatch,
  PointFaqItem,
  PointTransaction,
  TransactionQuery,
  TransactionQueryResult,
  TransactionCountItem,
} from '~/types/points'

export interface ServiceOptions {
  // 開發除錯用：強制讓這次呼叫回傳失敗，驗證 Loading/Error 畫面
  simulateError?: boolean
}

// 比照 useOrder.ts/useCart.ts：一律用 useRequestFetch()，SSR 頁面才能正確帶上瀏覽器原本的
// Authorization cookie。isCurrent 是後端 /api/public/wallet/points/tier/benefits
// （公開端點，不帶會員身份）不會回傳的欄位，這裡結合 /api/wallet/tier 的結果自己標記。
export const pointsService = {
  async fetchSummary(opts?: ServiceOptions): Promise<PointAccountSummary> {
    if (opts?.simulateError) throw new Error('伺服器發生錯誤，請稍後再試')
    return useRequestFetch()('/api/wallet/summary')
  },

  // 只拿目前會員資格，不帶比較表——給 Header 這類每頁都會渲染的地方用，
  // 避免比較表那支 API 被平白多打一次。
  async fetchCurrentTier(opts?: ServiceOptions): Promise<TierInfo> {
    if (opts?.simulateError) throw new Error('伺服器發生錯誤，請稍後再試')
    return useRequestFetch()('/api/wallet/tier')
  },

  async fetchTierInfo(opts?: ServiceOptions): Promise<{ current: TierInfo; benefits: TierBenefit[] }> {
    if (opts?.simulateError) throw new Error('伺服器發生錯誤，請稍後再試')
    const fetch = useRequestFetch()
    const [current, rawBenefits] = await Promise.all([
      fetch<TierInfo>('/api/wallet/tier'),
      fetch<Omit<TierBenefit, 'isCurrent'>[]>('/api/wallet/tier-benefits'),
    ])
    const benefits: TierBenefit[] = rawBenefits.map(b => ({
      ...b,
      isCurrent: b.tier === current.tier,
    }))
    return { current, benefits }
  },

  async fetchExpiringBatches(opts?: ServiceOptions): Promise<PointBatch[]> {
    if (opts?.simulateError) throw new Error('伺服器發生錯誤，請稍後再試')
    return useRequestFetch()('/api/wallet/expiring')
  },

  async fetchFaq(opts?: ServiceOptions): Promise<PointFaqItem[]> {
    if (opts?.simulateError) throw new Error('伺服器發生錯誤，請稍後再試')
    return useRequestFetch()('/api/wallet/faq')
  },

  async fetchTransactions(query: TransactionQuery, opts?: ServiceOptions): Promise<TransactionQueryResult> {
    if (opts?.simulateError) throw new Error('伺服器發生錯誤，請稍後再試')
    return useRequestFetch()('/api/wallet/transactions', { query })
  },

  async fetchTransactionDetail(id: string, opts?: ServiceOptions): Promise<PointTransaction | null> {
    if (opts?.simulateError) throw new Error('伺服器發生錯誤，請稍後再試')
    try {
      return await useRequestFetch()(`/api/wallet/transactions/${id}`)
    } catch (e: any) {
      if (e?.statusCode === 404 || e?.response?.status === 404) return null
      throw e
    }
  },

  async fetchTransactionCounts(opts?: ServiceOptions): Promise<TransactionCountItem[]> {
    if (opts?.simulateError) throw new Error('伺服器發生錯誤，請稍後再試')
    return useRequestFetch()('/api/wallet/transactions/counts')
  },
}
