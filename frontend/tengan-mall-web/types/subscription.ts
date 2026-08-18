import type { EcpayFormData } from '~/types/payment'

export type SubscriptionTargetTier = 'PRO' | 'PRO_PLUS'

export type SubscriptionStatus = 'PENDING' | 'ACTIVE' | 'CANCELLED'

export interface SubscribeResult {
  subscriptionId: number
  ecpayForm: EcpayFormData
}

/**
 * subscribed=false 代表從沒訂閱過或已經完全過期，其餘欄位為 null。
 * status=PENDING 代表剛送出訂閱、ECPay 首刷授權結果還沒回來，這時完全沒有 PRO 權益，UI 要顯示
 * 「確認中」，不能當成已訂閱成功處理。
 * status/autoRenew 只代表「還會不會繼續扣款」，不是「現在有沒有 PRO 權益」——
 * 取消後 status 會變 CANCELLED 但 paidUntil 前仍維持 PRO，前端要分開顯示，不能讓使用者以為取消就立刻失去權益。
 */
export interface MySubscriptionResult {
  subscribed: boolean
  targetTier: SubscriptionTargetTier | null
  status: SubscriptionStatus | null
  paidUntil: string | null
  autoRenew: boolean
}

export const SUBSCRIPTION_TIER_LABEL: Record<SubscriptionTargetTier, string> = {
  PRO: 'PRO',
  PRO_PLUS: 'PRO+',
}
