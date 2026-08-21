import type {
  CreateOrderPayload,
  CreateOrderResult,
  OrderConfirmResult,
  OrderDetail,
  OrderProcessing,
  OrderStatus,
  OrderSummary,
} from '~/types/order'
import type { MyCoupon } from '~/types/coupon'

// 比照 useCart.ts：一律用 useRequestFetch()，SSR 頁面才能正確帶上瀏覽器原本的 Authorization cookie。
export function useOrder() {
  const fetch = useRequestFetch()

  async function confirmOrder(): Promise<OrderConfirmResult> {
    return fetch('/api/orders/confirm')
  }

  async function createOrder(payload: CreateOrderPayload): Promise<CreateOrderResult> {
    return fetch('/api/orders', { method: 'POST', body: payload })
  }

  async function fetchOrders(status?: OrderStatus, page = 1, pageSize = 20): Promise<{ items: OrderSummary[]; total: number }> {
    return fetch('/api/orders', { query: { status, page, pageSize } })
  }

  async function fetchOrderDetail(orderSn: string): Promise<OrderDetail | OrderProcessing> {
    return fetch(`/api/orders/${orderSn}`)
  }

  async function cancelOrder(orderSn: string): Promise<void> {
    await fetch(`/api/orders/${orderSn}/cancel`, { method: 'PUT' })
  }

  async function confirmReceipt(orderSn: string): Promise<void> {
    await fetch(`/api/orders/${orderSn}/confirm-receipt`, { method: 'PUT' })
  }

  async function fetchAvailableCoupons(amount: number): Promise<MyCoupon[]> {
    return fetch('/api/coupons/available', { query: { amount } })
  }

  return {
    confirmOrder,
    createOrder,
    fetchOrders,
    fetchOrderDetail,
    cancelOrder,
    confirmReceipt,
    fetchAvailableCoupons,
  }
}
