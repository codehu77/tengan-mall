import type {
  MemberTier,
  TierInfo,
  TierBenefit,
  PointAccountSummary,
  PointTransaction,
  PointTransactionType,
  PointBatch,
  PointFaqItem,
  TransactionQuery,
  TransactionQueryResult,
} from '~/types/points'

// 模擬「現在」時間；固定值避免不同時間執行 mock 產生不一致的篩選結果
const MOCK_NOW = new Date('2026-07-16T12:00:00+08:00')

export const TRANSACTION_TYPE_META: Record<PointTransactionType, { label: string; color: string; icon: string }> = {
  EARN: { label: '獲得', color: 'green', icon: 'i-heroicons-plus-circle' },
  REDEEM: { label: '使用', color: 'red', icon: 'i-heroicons-shopping-bag' },
  EXPIRE: { label: '過期', color: 'gray', icon: 'i-heroicons-clock' },
  ADJUST: { label: '人工調整', color: 'orange', icon: 'i-heroicons-wrench-screwdriver' },
  PENDING: { label: '待入帳', color: 'yellow', icon: 'i-heroicons-arrow-path' },
}

export const TIER_LABEL: Record<MemberTier, string> = {
  FREE: '一般會員',
  PRO: 'PRO',
  PRO_PLUS: 'PRO+',
}

const MOCK_TIER_INFO: TierInfo = {
  tier: 'PRO',
  label: 'PRO 會員',
  cashbackRate: 0.02,
  monthlyCap: 500,
  monthlyEarnedPoints: 320,
}

const MOCK_TIER_BENEFITS: TierBenefit[] = [
  {
    tier: 'FREE',
    label: '一般會員',
    cashbackRateLabel: '0%',
    monthlyCapLabel: '無點數回饋',
    perks: ['享有基本購物功能', '生日優惠通知'],
    isCurrent: false,
    highlight: false,
  },
  {
    tier: 'PRO',
    label: 'PRO',
    cashbackRateLabel: '2%',
    monthlyCapLabel: '單月最高 500 點',
    perks: ['消費滿額享 2% 點數回饋', '點數效期自入帳日起 365 天', '生日當月加碼回饋'],
    isCurrent: true,
    highlight: false,
  },
  {
    tier: 'PRO_PLUS',
    label: 'PRO+',
    cashbackRateLabel: '4%',
    monthlyCapLabel: '單月無上限',
    perks: ['消費滿額享 4% 點數回饋，單月無上限', '點數效期自入帳日起 365 天', '生日當月加碼回饋', '訂單完成後優先入帳'],
    isCurrent: false,
    highlight: true,
  },
]

const MOCK_SUMMARY: PointAccountSummary = {
  availablePoints: 1084,
  pendingPoints: 156,
  expiringPoints: 320,
  expiringWithinDays: 30,
  pointValueRatio: 1,
}

// 即將到期批次；總點數需與 MOCK_SUMMARY.expiringPoints 對齊（120+80+120=320）
const MOCK_EXPIRING_BATCHES: PointBatch[] = [
  { batchId: 'B20250716', points: 120, earnedAt: '2025-07-16', expiresAt: '2026-07-31', sourceOrderSn: 'TG20250716000018' },
  { batchId: 'B20250722', points: 80, earnedAt: '2025-07-22', expiresAt: '2026-08-05', sourceOrderSn: 'TG20250722000004' },
  { batchId: 'B20250801', points: 120, earnedAt: '2025-08-01', expiresAt: '2026-08-15', sourceOrderSn: 'TG20250801000027' },
]

const MOCK_FAQ: PointFaqItem[] = [
  {
    id: 'f1',
    question: '點數如何取得？',
    answer: 'PRO 會員消費可獲得 2%、PRO+ 會員可獲得 4% 點數回饋（PRO+ 單月無上限）。訂單到貨滿 7 天鑑賞期後，系統將自動把回饋點數轉為可用點數。',
  },
  {
    id: 'f2',
    question: '點數有使用期限嗎？',
    answer: '點數自入帳日起 365 天內有效，逾期未使用將自動失效，請留意頁面上「即將到期點數」提醒，優先使用即將到期的批次。',
  },
  {
    id: 'f3',
    question: '點數可以折抵多少金額？',
    answer: '每 1 點可折抵 NT$1，結帳時可依訂單金額選擇折抵數量，實際折抵上限依當次訂單規則為準。',
  },
  {
    id: 'f5',
    question: '「待入帳」點數是什麼？',
    answer: '訂單完成後仍在 7 天鑑賞期內的回饋點數會先顯示為待入帳，鑑賞期結束且無退換貨後，將自動轉入可用點數餘額。',
  },
  {
    id: 'f6',
    question: 'PRO 與 PRO+ 的點數回饋上限？',
    answer: 'PRO 會員單月最高可回饋 500 點；PRO+ 會員單月點數回饋無上限，消費越多、回饋越多。',
  },
]

// 交易明細僅保留近期紀錄（2025-12 起）；起始餘額 1421 點已包含更早期歷史入帳
// （含「即將到期批次」的原始來源訂單，未重複列在下方明細中）。
// balanceAfter 依交易時間正向推算，確保與 MOCK_SUMMARY.availablePoints (1084) 完全對帳一致。
// 不含退款交易（本專案刻意不做退款功能，見 admin_api_gap_fixes 決策）。
const MOCK_TRANSACTIONS: PointTransaction[] = [
  {
    id: 'PT20251201-1',
    type: 'EXPIRE',
    status: 'EXPIRED',
    points: -40,
    balanceAfter: 1381,
    title: '點數到期失效',
    description: '2024/12 取得之 40 點已於效期屆滿自動失效。',
    createdAt: '2025-12-01T00:05:00+08:00',
    expiresAt: null,
  },
  {
    id: 'PT20251218-1',
    type: 'EARN',
    status: 'CONFIRMED',
    points: 90,
    balanceAfter: 1471,
    title: '消費回饋入帳',
    description: '訂單 TG20251218000007 消費 NT$4,500，依 PRO 會員 2% 回饋 90 點。',
    orderSn: 'TG20251218000007',
    channel: '網站',
    createdAt: '2025-12-18T20:20:00+08:00',
    expiresAt: '2026-12-18',
  },
  {
    id: 'PT20260105-1',
    type: 'EARN',
    status: 'CONFIRMED',
    points: 36,
    balanceAfter: 1507,
    title: '消費回饋入帳',
    description: '訂單 TG20260105000031 消費 NT$1,800，依 PRO 會員 2% 回饋 36 點。',
    orderSn: 'TG20260105000031',
    channel: 'APP',
    createdAt: '2026-01-05T08:55:00+08:00',
    expiresAt: '2027-01-05',
  },
  {
    id: 'PT20260214-1',
    type: 'REDEEM',
    status: 'CONFIRMED',
    points: -300,
    balanceAfter: 1207,
    title: '結帳折抵點數',
    description: '訂單 TG20260214000002 結帳時使用 300 點折抵 NT$300（情人節加碼折抵）。',
    orderSn: 'TG20260214000002',
    channel: '網站',
    createdAt: '2026-02-14T10:08:00+08:00',
  },
  {
    id: 'PT20260322-1',
    type: 'EARN',
    status: 'CONFIRMED',
    points: 40,
    balanceAfter: 1247,
    title: '消費回饋入帳',
    description: '訂單 TG20260322000044 消費 NT$2,000，依 PRO 會員 2% 回饋 40 點。',
    orderSn: 'TG20260322000044',
    channel: '網站',
    createdAt: '2026-03-22T17:15:00+08:00',
    expiresAt: '2027-03-22',
  },
  {
    id: 'PT20260418-1',
    type: 'EARN',
    status: 'CONFIRMED',
    points: 64,
    balanceAfter: 1311,
    title: '消費回饋入帳',
    description: '訂單 TG20260418000009 消費 NT$3,200，依 PRO 會員 2% 回饋 64 點。',
    orderSn: 'TG20260418000009',
    channel: 'APP',
    createdAt: '2026-04-18T12:30:00+08:00',
    expiresAt: '2027-04-18',
  },
  {
    id: 'PT20260510-1',
    type: 'REDEEM',
    status: 'CONFIRMED',
    points: -150,
    balanceAfter: 1161,
    title: '結帳折抵點數',
    description: '訂單 TG20260510000015 結帳時使用 150 點折抵 NT$150。',
    orderSn: 'TG20260510000015',
    channel: '網站',
    createdAt: '2026-05-10T21:02:00+08:00',
  },
  {
    id: 'PT20260520-1',
    type: 'EARN',
    status: 'CONFIRMED',
    points: 58,
    balanceAfter: 1219,
    title: '消費回饋入帳',
    description: '訂單 TG20260520000027 消費 NT$2,900，依 PRO 會員 2% 回饋 58 點。',
    orderSn: 'TG20260520000027',
    channel: '網站',
    createdAt: '2026-05-20T19:44:00+08:00',
    expiresAt: '2027-05-20',
  },
  {
    id: 'PT20260602-1',
    type: 'ADJUST',
    status: 'CONFIRMED',
    points: -20,
    balanceAfter: 1199,
    title: '回饋金額修正',
    description: '訂單 TG20260601000010 回饋點數計算錯誤，系統稽核後扣回 20 點。',
    orderSn: 'TG20260601000010',
    operator: '系統自動稽核',
    createdAt: '2026-06-02T09:20:00+08:00',
  },
  {
    id: 'PT20260620-1',
    type: 'ADJUST',
    status: 'CONFIRMED',
    points: 50,
    balanceAfter: 1249,
    title: '客服補償點數',
    description: '因物流延誤造成不便，客服人工加發 50 點作為補償。',
    operator: '客服專員 Wei',
    createdAt: '2026-06-20T10:00:00+08:00',
  },
  {
    id: 'PT20260628-1',
    type: 'EARN',
    status: 'CONFIRMED',
    points: 72,
    balanceAfter: 1321,
    title: '消費回饋入帳',
    description: '訂單 TG20260628000050 消費 NT$3,600，依 PRO 會員 2% 回饋 72 點。',
    orderSn: 'TG20260628000050',
    channel: '網站',
    createdAt: '2026-06-28T13:10:00+08:00',
    expiresAt: '2027-06-28',
  },
  {
    id: 'PT20260701-1',
    type: 'EXPIRE',
    status: 'EXPIRED',
    points: -85,
    balanceAfter: 1236,
    title: '點數到期失效',
    description: '2025/07 取得之 85 點已於效期屆滿自動失效。',
    createdAt: '2026-07-01T00:05:00+08:00',
    expiresAt: null,
  },
  {
    id: 'PT20260702-1',
    type: 'PENDING',
    status: 'PENDING',
    points: 60,
    balanceAfter: null,
    title: '消費回饋待入帳',
    description: '訂單 TG20260702000008 商品尚在 7 天鑑賞期，預計 2026/07/09 自動入帳。',
    orderSn: 'TG20260702000008',
    channel: '網站',
    createdAt: '2026-07-02T16:50:00+08:00',
  },
  {
    id: 'PT20260705-1',
    type: 'PENDING',
    status: 'PENDING',
    points: 60,
    balanceAfter: null,
    title: '消費回饋待入帳',
    description: '訂單 TG20260705000019 商品尚在 7 天鑑賞期，預計 2026/07/12 自動入帳。',
    orderSn: 'TG20260705000019',
    channel: 'APP',
    createdAt: '2026-07-05T09:12:00+08:00',
  },
  {
    id: 'PT20260709-1',
    type: 'PENDING',
    status: 'PENDING',
    points: 36,
    balanceAfter: null,
    title: '消費回饋待入帳',
    description: '訂單 TG20260709000031 商品尚在 7 天鑑賞期，預計 2026/07/16 自動入帳。',
    orderSn: 'TG20260709000031',
    channel: '網站',
    createdAt: '2026-07-09T11:40:00+08:00',
  },
  {
    id: 'PT20260712-1',
    type: 'REDEEM',
    status: 'CONFIRMED',
    points: -200,
    balanceAfter: 1036,
    title: '結帳折抵點數',
    description: '訂單 TG20260712000033 結帳時使用 200 點折抵 NT$200。',
    orderSn: 'TG20260712000033',
    channel: '網站',
    createdAt: '2026-07-12T20:05:00+08:00',
  },
  {
    id: 'PT20260715-1',
    type: 'EARN',
    status: 'CONFIRMED',
    points: 48,
    balanceAfter: 1084,
    title: '消費回饋入帳',
    description: '訂單 TG20260715000041 消費 NT$2,400，依 PRO 會員 2% 回饋 48 點。',
    orderSn: 'TG20260715000041',
    channel: 'APP',
    createdAt: '2026-07-15T14:22:00+08:00',
    expiresAt: '2027-07-15',
  },
]

export function getPointSummary(): PointAccountSummary {
  return { ...MOCK_SUMMARY }
}

export function getTierInfo(): TierInfo {
  return { ...MOCK_TIER_INFO }
}

export function getTierBenefits(): TierBenefit[] {
  return MOCK_TIER_BENEFITS.map(b => ({ ...b, perks: [...b.perks] }))
}

export function getExpiringBatches(): PointBatch[] {
  return MOCK_EXPIRING_BATCHES.map(b => ({ ...b }))
}

export function getFaqList(): PointFaqItem[] {
  return MOCK_FAQ.map(f => ({ ...f }))
}

export function getTransactionDetail(id: string): PointTransaction | null {
  const found = MOCK_TRANSACTIONS.find(t => t.id === id)
  return found ? { ...found } : null
}

function withinDateRange(createdAt: string, range: TransactionQuery['dateRange']): boolean {
  if (range === 'ALL') return true
  const days = range === '30D' ? 30 : range === '90D' ? 90 : 365
  const cutoff = new Date(MOCK_NOW)
  cutoff.setDate(cutoff.getDate() - days)
  return new Date(createdAt) >= cutoff
}

export function queryTransactions(query: TransactionQuery): TransactionQueryResult {
  const keyword = query.keyword.trim().toLowerCase()

  const filtered = MOCK_TRANSACTIONS
    .filter(t => query.type === 'ALL' || t.type === query.type)
    .filter(t => withinDateRange(t.createdAt, query.dateRange))
    .filter((t) => {
      if (!keyword) return true
      return (
        t.title.toLowerCase().includes(keyword) ||
        t.description.toLowerCase().includes(keyword) ||
        (t.orderSn?.toLowerCase().includes(keyword) ?? false)
      )
    })
    .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())

  const total = filtered.length
  const start = (query.page - 1) * query.pageSize
  const items = filtered.slice(start, start + query.pageSize).map(t => ({ ...t }))

  return { items, total, page: query.page, pageSize: query.pageSize }
}
