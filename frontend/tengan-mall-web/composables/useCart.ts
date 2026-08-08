import type { CartItem } from '~/types/cart'

// P7 串接：內部改打 /api/cart/**（Nuxt BFF，會轉發 Authorization/tengan_guest_key cookie 給
// tengan-cart），呼叫端（AppHeader、cart/index.vue、item/[spuId].vue）簽名維持不變，
// 只有 toggleChecked 例外——後端 PUT 要求明確帶目標值，不是「切換」語意，呼叫端要自己傳 !checked。
//
// 一律用 useRequestFetch() 而不是裸的 $fetch——layouts/default.vue 在 SSR 頁面（/、/search、
// /item/**）用 useAsyncData 呼叫 fetchCartCount()，這段程式碼會在伺服器端執行，裸 $fetch 不會
// 自動帶上瀏覽器原本的 cookie（Authorization/tengan_guest_key），會永遠查到訪客且是空購物車。
// useRequestFetch() 在 SSR 情境自動轉發原始請求的 header，在純 client 情境等同 $fetch，兩邊都安全
// （跟 useAuthStore.fetchMe() 已經在用的模式一致）。
export function useCart() {
  const fetch = useRequestFetch()

  async function fetchCartItems(): Promise<CartItem[]> {
    return fetch('/api/cart/items')
  }

  async function fetchMiniCart(limit = 5): Promise<{ items: CartItem[]; total: number }> {
    return fetch('/api/cart/mini', { query: { limit } })
  }

  async function fetchCartCount(): Promise<number> {
    return fetch('/api/cart/count')
  }

  async function addToCart(product: { skuId: number; skuName?: string; price?: number; image?: string }, qty: number): Promise<number> {
    return fetch('/api/cart/items', { method: 'POST', body: { skuId: product.skuId, count: qty } })
  }

  async function removeFromCart(itemId: number): Promise<number> {
    return fetch(`/api/cart/items/${itemId}`, { method: 'DELETE' })
  }

  async function updateQty(itemId: number, count: number): Promise<number> {
    return fetch(`/api/cart/items/${itemId}`, { method: 'PUT', body: { count } })
  }

  async function toggleChecked(itemId: number, checked: boolean) {
    await fetch(`/api/cart/items/${itemId}/checked`, { method: 'PUT', body: { checked } })
  }

  async function toggleAllChecked(checked: boolean) {
    await fetch('/api/cart/checked-all', { method: 'PUT', body: { checked } })
  }

  async function removeCheckedItems(): Promise<number> {
    return fetch('/api/cart/items/checked', { method: 'DELETE' })
  }

  /** 登入成功後呼叫一次，訪客購物車併入會員帳號；沒有訪客購物車時後端本來就是 no-op。 */
  async function mergeCart(): Promise<number> {
    try {
      return await fetch('/api/cart/merge', { method: 'POST' })
    } catch {
      return fetchCartCount()
    }
  }

  return {
    fetchCartItems,
    fetchMiniCart,
    fetchCartCount,
    addToCart,
    removeFromCart,
    updateQty,
    toggleChecked,
    toggleAllChecked,
    removeCheckedItems,
    mergeCart,
  }
}
