// 會員等級（僅 3 級：一般會員 / PRO / PRO+；訂閱付款流程見 types/subscription.ts）
export type MemberTier = 'FREE' | 'PRO' | 'PRO_PLUS'

// 點數交易「為什麼變動」，不可變起因，跟下面的 status（現在算不算數）是兩個獨立維度
// （不含退款：本專案刻意不做退款功能，見 admin_api_gap_fixes 決策）
export type PointTransactionType = 'EARN' | 'REDEEM' | 'EXPIRE' | 'ADJUST'

export type PointTransactionTypeFilter = PointTransactionType | 'ALL'

// 點數交易「現在算不算數」的生命週期：待入帳／已生效／已撤銷
export type PointTransactionStatus = 'PENDING' | 'CONFIRMED' | 'REVERSED'

export type PointTransactionStatusFilter = PointTransactionStatus | 'ALL'

export type PointDateRangeFilter = 'ALL' | '30D' | '90D' | '1Y'

// 目前會員資格與回饋比例
export interface TierInfo {
  tier: MemberTier
  label: string
  cashbackRate: number
  monthlyCap: number | null
  monthlyEarnedPoints: number
}

// FREE / PRO / PRO+ 比較表的單一欄位
export interface TierBenefit {
  tier: MemberTier
  label: string
  cashbackRateLabel: string
  monthlyCapLabel: string
  perks: string[]
  isCurrent: boolean
}

// 點數錢包摘要：可用 / 待入帳 / 即將到期
export interface PointAccountSummary {
  availablePoints: number
  pendingPoints: number
  expiringPoints: number
  expiringWithinDays: number
  pointValueRatio: number
}

export interface PointTransaction {
  id: string
  type: PointTransactionType
  status: PointTransactionStatus
  points: number
  balanceAfter: number | null
  title: string
  description: string
  orderSn?: string
  channel?: string
  operator?: string
  createdAt: string
  expiresAt?: string | null
}

// 即將到期的點數批次
export interface PointBatch {
  batchId: string
  points: number
  earnedAt: string
  expiresAt: string
  sourceOrderSn?: string
}

export interface PointFaqItem {
  id: string
  question: string
  answer: string
}

export interface TransactionQuery {
  // 主要分類（分頁籤）：交易的生命週期，不是交易的原因——待入帳/已撤銷這種「現在算不算數」的資訊
  // 才適合當作主要篩選依據，「為什麼變動」(type) 交給下面的次要下拉篩選（見設計討論：兩個維度硬塞進
  // 同一個篩選欄位曾經造成同一筆待入帳交易同時出現在「獲得」跟「待入帳」兩種分類底下）。
  status: PointTransactionStatusFilter
  type: PointTransactionTypeFilter
  dateRange: PointDateRangeFilter
  keyword: string
  page: number
  pageSize: number
}

export interface TransactionQueryResult {
  items: PointTransaction[]
  total: number
  page: number
  pageSize: number
}

// 交易明細分類 tabs 顯示筆數用——後端只回傳原始 (type, status) 分組數字，分類定義（例如「已過期」
// 對應 type=EXPIRE 不分狀態）只活在 TransactionFilterBar.vue 一個地方。
export interface TransactionCountItem {
  type: PointTransactionType
  status: PointTransactionStatus
  count: number
}

export const TRANSACTION_TYPE_META: Record<PointTransactionType, { label: string; color: string; icon: string }> = {
  EARN: { label: '獲得', color: 'green', icon: 'i-heroicons-plus-circle' },
  REDEEM: { label: '使用', color: 'red', icon: 'i-heroicons-shopping-bag' },
  EXPIRE: { label: '過期', color: 'gray', icon: 'i-heroicons-clock' },
  ADJUST: { label: '人工調整', color: 'orange', icon: 'i-heroicons-wrench-screwdriver' },
}

export const TRANSACTION_STATUS_META: Record<PointTransactionStatus, { label: string; color: string }> = {
  PENDING: { label: '待入帳', color: 'yellow' },
  CONFIRMED: { label: '已生效', color: 'gray' },
  REVERSED: { label: '已撤銷', color: 'gray' },
}

export const TIER_LABEL: Record<MemberTier, string> = {
  FREE: '一般會員',
  PRO: 'PRO',
  PRO_PLUS: 'PRO+',
}
