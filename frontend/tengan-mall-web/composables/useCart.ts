import {
  getCartItems,
  getCartCount,
  addCartItem,
  removeCartItem,
  updateCartItemQty,
  toggleCartItemChecked,
  setAllChecked,
  type CartItem,
} from '~/mocks/cart'

// Mock 階段直接讀 mocks/cart.ts；P7 串接後端時把各函式內部換成 useFetch('/api/cart/...')，
// 呼叫端（AppHeader、cart/index.vue、item/[skuId].vue）完全不用改。
export function useCart() {
  async function fetchCartItems(): Promise<CartItem[]> {
    return getCartItems()
  }

  async function fetchMiniCart(limit = 5): Promise<{ items: CartItem[]; total: number }> {
    const items = getCartItems()
    return { items: items.slice(0, limit), total: items.length }
  }

  async function fetchCartCount(): Promise<number> {
    return getCartCount()
  }

  async function addToCart(product: { skuId: number; skuName: string; price: number; image: string }, qty: number): Promise<number> {
    return addCartItem(product, qty)
  }

  async function removeFromCart(itemId: number): Promise<number> {
    return removeCartItem(itemId)
  }

  async function updateQty(itemId: number, count: number): Promise<number> {
    return updateCartItemQty(itemId, count)
  }

  async function toggleChecked(itemId: number) {
    toggleCartItemChecked(itemId)
  }

  async function toggleAllChecked(checked: boolean) {
    setAllChecked(checked)
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
  }
}
