export interface MyCoupon {
  id: number
  templateId: number
  templateName: string
  thresholdAmount: number
  discountAmount: number
  /** 1=UNUSED 2=USED（對齊 tengan-coupon MemberCoupon.useStatus） */
  useStatus: number
  orderSn: string | null
  receivedAt: string
}
