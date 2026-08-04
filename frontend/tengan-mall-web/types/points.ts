// 會員等級（僅 3 級：一般會員 / PRO / PRO+，不含訂閱付款流程）
export type MemberTier = 'FREE' | 'PRO' | 'PRO_PLUS'

// 點數交易類型：獲得／使用／過期／人工調整／待入帳（不含退款：本專案刻意不做退款功能，見 admin_api_gap_fixes 決策）
export type PointTransactionType = 'EARN' | 'REDEEM' | 'EXPIRE' | 'ADJUST' | 'PENDING'

export type PointTransactionFilterType = PointTransactionType | 'ALL'

export type PointTransactionStatus = 'CONFIRMED' | 'PENDING' | 'EXPIRED' | 'REVERSED'

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
  highlight: boolean
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
  type: PointTransactionFilterType
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
