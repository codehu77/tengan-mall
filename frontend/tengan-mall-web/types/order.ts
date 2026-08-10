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
