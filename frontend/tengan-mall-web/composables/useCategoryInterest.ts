export interface CategoryAffinityItem {
  categoryId: number
  score: number
}

/** 「猜你喜歡」的興趣訊號——只有登入會員才有意義（member/[spuId]、search.vue 呼叫前要自己判斷
 * authStore.isLoggedIn），這裡不重複判斷。比照 useCart.ts 用 useRequestFetch()，SSR/CSR 都安全。 */
export function useCategoryInterest() {
  const fetch = useRequestFetch()

  /** 記錄一次興趣訊號——非關鍵路徑，失敗不影響使用者瀏覽，比照 member/subscription.vue 的
   * 權益小卡慣例，靜默吞掉錯誤，不跳 toast、不擋渲染。 */
  async function recordInterest(categoryId: number, source: 'VIEW' | 'SEARCH') {
    await fetch('/api/member/category-interest', { method: 'POST', body: { categoryId, source } }).catch(() => {})
  }

  async function fetchAffinity(): Promise<CategoryAffinityItem[]> {
    return fetch('/api/member/category-affinity')
  }

  return { recordInterest, fetchAffinity }
}
