import type { PaymentMethod } from '~/types/order'
import type { InitiatePaymentResult, PaymentMethodsResult, PaymentStatusResult } from '~/types/payment'

// 比照 useOrder.ts：一律用 useRequestFetch()，SSR 頁面才能正確帶上瀏覽器原本的 Authorization cookie。
export function usePayment() {
  const fetch = useRequestFetch()

  async function fetchPaymentMethods(): Promise<PaymentMethodsResult> {
    return fetch('/api/payments/methods')
  }

  async function initiatePayment(orderSn: string, method: PaymentMethod): Promise<InitiatePaymentResult> {
    return fetch(`/api/payments/${orderSn}`, { method: 'PUT', body: { method } })
  }

  async function fetchPaymentStatus(orderSn: string): Promise<PaymentStatusResult> {
    return fetch(`/api/payments/${orderSn}`)
  }

  async function confirmLinePayPayment(orderSn: string, transactionId: string): Promise<void> {
    await fetch(`/api/payments/${orderSn}/linepay-confirm`, { method: 'POST', body: { transactionId } })
  }

  return {
    fetchPaymentMethods,
    initiatePayment,
    fetchPaymentStatus,
    confirmLinePayPayment,
  }
}
