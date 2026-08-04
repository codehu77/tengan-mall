import {
  getPointSummary,
  getTierInfo,
  getTierBenefits,
  getExpiringBatches,
  getFaqList,
  queryTransactions,
  getTransactionDetail,
} from '~/mocks/points'
import type {
  PointAccountSummary,
  TierInfo,
  TierBenefit,
  PointBatch,
  PointFaqItem,
  PointTransaction,
  TransactionQuery,
  TransactionQueryResult,
} from '~/types/points'

export interface ServiceOptions {
  // 開發除錯用：強制讓這次呼叫回傳失敗，驗證 Loading/Error 畫面
  simulateError?: boolean
}

// Mock 階段：內部直接讀 mocks/points.ts 的靜態資料並模擬網路延遲。
// 之後串接 Spring Boot（gulimall-member）時，只需把每個函式內部改成
// useFetch/$fetch 呼叫對應的 /api/member/points/** 端點，回傳型別維持不變，
// 呼叫端（stores/usePointsStore.ts）完全不用修改。
function delayOrThrow<T>(factory: () => T, opts?: ServiceOptions, ms = 400): Promise<T> {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (opts?.simulateError) {
        reject(new Error('伺服器發生錯誤，請稍後再試'))
        return
      }
      resolve(factory())
    }, ms)
  })
}

export const pointsService = {
  fetchSummary(opts?: ServiceOptions): Promise<PointAccountSummary> {
    return delayOrThrow(() => getPointSummary(), opts)
  },

  fetchTierInfo(opts?: ServiceOptions): Promise<{ current: TierInfo; benefits: TierBenefit[] }> {
    return delayOrThrow(() => ({ current: getTierInfo(), benefits: getTierBenefits() }), opts)
  },

  fetchExpiringBatches(opts?: ServiceOptions): Promise<PointBatch[]> {
    return delayOrThrow(() => getExpiringBatches(), opts)
  },

  fetchFaq(opts?: ServiceOptions): Promise<PointFaqItem[]> {
    return delayOrThrow(() => getFaqList(), opts, 300)
  },

  fetchTransactions(query: TransactionQuery, opts?: ServiceOptions): Promise<TransactionQueryResult> {
    return delayOrThrow(() => queryTransactions(query), opts, 500)
  },

  fetchTransactionDetail(id: string, opts?: ServiceOptions): Promise<PointTransaction | null> {
    return delayOrThrow(() => getTransactionDetail(id), opts, 250)
  },
}
