import type { MySubscriptionResult, SubscribeResult, SubscriptionTargetTier } from '~/types/subscription'

// 比照 usePayment.ts：一律用 useRequestFetch()，SSR 頁面才能正確帶上瀏覽器原本的 Authorization cookie。
export function useSubscription() {
  const fetch = useRequestFetch()

  async function subscribe(targetTier: SubscriptionTargetTier): Promise<SubscribeResult> {
    return fetch('/api/subscriptions', { method: 'POST', body: { targetTier } })
  }

  async function fetchMySubscription(): Promise<MySubscriptionResult> {
    return fetch('/api/subscriptions/me')
  }

  async function cancelSubscription(): Promise<void> {
    await fetch('/api/subscriptions/cancel', { method: 'PUT' })
  }

  return {
    subscribe,
    fetchMySubscription,
    cancelSubscription,
  }
}
