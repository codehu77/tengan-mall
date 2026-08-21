/** linepay/credit_card/cod 是全站唯一合法的付款方式命名（見 JWT設計.md 付款方式命名定案）。 */
export type PaymentMethod = 'linepay' | 'credit_card' | 'cod'

/** 1=PENDING_PAYMENT 2=PAID 3=SHIPPED 4=COMPLETED 5=CANCELLED（對齊 tengan-order OrderStatus）。 */
export type OrderStatus = 1 | 2 | 3 | 4 | 5

export interface ConfirmedOrderItem {
  skuId: number
  spuId: number
  name: string
  mainImage: string
  price: number
  count: number
  subtotal: number
}

export interface OrderConfirmResult {
  orderToken: string
  items: ConfirmedOrderItem[]
  totalAmount: number
}

export interface ReceiverInfoPayload {
  receiverName: string
  receiverPhone: string
  city: string
  district: string
  postalCode: string
  street: string
}

export interface CreateOrderPayload {
  orderToken: string
  receiverInfo: ReceiverInfoPayload
  paymentMethod: PaymentMethod
  couponId?: number | null
  pointsUsed?: number | null
  remark?: string
}

export interface CreateOrderResult {
  orderSn: string
  payAmount: number
}

export interface OrderSummary {
  id: number
  orderSn: string
  memberId: number
  status: OrderStatus
  payAmount: number
  paymentMethod: PaymentMethod
  createdAt: string
}

export interface OrderItem {
  skuId: number
  spuId: number
  skuName: string
  skuImage: string | null
  price: number
  count: number
  subtotal: number
}

export interface OrderDetail {
  id: number
  orderSn: string
  memberId: number
  status: OrderStatus
  cancelReason: string | null
  totalAmount: number
  discountAmount: number
  payAmount: number
  paymentMethod: PaymentMethod
  couponId: number | null
  pointsUsed: number | null
  pointsDiscountAmount: number
  receiverName: string
  receiverPhone: string
  city: string
  district: string
  postalCode: string
  street: string
  remark: string | null
  receiptTime: string | null
  createdAt: string
  items: OrderItem[]
}

/**
 * 含秒殺項目的訂單改走 MQ 非同步落地，提交後到真正寫進 DB 之間有一段空窗期——後端 202 對應這個
 * 型別，前端據此顯示「處理中」並輪詢，不是把它當成錯誤（見 tengan-order 規劃第 5 節）。
 */
export interface OrderProcessing {
  processing: true
  orderSn: string
}

export const ORDER_STATUS_META: Record<OrderStatus, { label: string; color: string }> = {
  1: { label: '待付款', color: 'orange' },
  2: { label: '已付款', color: 'blue' },
  3: { label: '已出貨', color: 'purple' },
  4: { label: '已完成', color: 'green' },
  5: { label: '已取消', color: 'gray' },
}

export const PAYMENT_METHOD_META: Record<PaymentMethod, { label: string; icon: string }> = {
  linepay: { label: 'LINE Pay', icon: 'i-heroicons-device-phone-mobile' },
  credit_card: { label: '信用卡付款', icon: 'i-heroicons-credit-card' },
  cod: { label: '貨到付款', icon: 'i-heroicons-banknotes' },
}
