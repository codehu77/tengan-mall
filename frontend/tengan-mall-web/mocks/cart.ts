export interface CartItem {
  itemId: number
  skuId: number
  skuName: string
  price: number
  image: string
  count: number
  checked: boolean
}

// 模擬後端購物車資料表，模組層級的可變陣列，開發階段當成「資料庫」使用
const cartState: CartItem[] = [
  {
    itemId: 1,
    skuId: 1,
    skuName: 'Apple iPhone 15 Pro 256GB 原色鈦金屬',
    price: 39900,
    image: 'https://placehold.co/100x100?text=iPhone+15+Pro',
    count: 1,
    checked: true,
  },
  {
    itemId: 2,
    skuId: 3,
    skuName: 'Sony WH-1000XM5 無線降噪耳機',
    price: 11900,
    image: 'https://placehold.co/100x100?text=Sony+WH1000XM5',
    count: 2,
    checked: true,
  },
  {
    itemId: 3,
    skuId: 8,
    skuName: 'Nintendo Switch OLED 白色主機',
    price: 10980,
    image: 'https://placehold.co/100x100?text=Switch+OLED',
    count: 1,
    checked: false,
  },
  {
    itemId: 4,
    skuId: 2,
    skuName: 'Samsung Galaxy S24 Ultra 512GB 幻影黑',
    price: 42900,
    image: 'https://placehold.co/100x100?text=S24+Ultra',
    count: 1,
    checked: true,
  },
  {
    itemId: 5,
    skuId: 4,
    skuName: 'MacBook Air 13吋 M3 晶片 8GB / 256GB',
    price: 37900,
    image: 'https://placehold.co/100x100?text=MacBook+Air+M3',
    count: 1,
    checked: true,
  },
  {
    itemId: 6,
    skuId: 5,
    skuName: 'iPad Air 11吋 M2 Wi-Fi 128GB 藍色',
    price: 19900,
    image: 'https://placehold.co/100x100?text=iPad+Air+M2',
    count: 1,
    checked: true,
  },
  {
    itemId: 7,
    skuId: 10,
    skuName: 'Apple Watch Series 9 GPS 45mm 午夜色',
    price: 14900,
    image: 'https://placehold.co/100x100?text=Apple+Watch+S9',
    count: 1,
    checked: true,
  },
]

let nextItemId = 8

export function getCartItems(): CartItem[] {
  // 回傳新陣列（複製每個 item），模擬 API 每次回應都是新的資料，
  // 避免呼叫端拿到跟上次一樣的參考導致 Vue 判斷「沒有變化」而不觸發畫面更新
  return cartState.map(item => ({ ...item }))
}

export function getCartCount(): number {
  return cartState.reduce((sum, item) => sum + item.count, 0)
}

export function addCartItem(product: { skuId: number; skuName: string; price: number; image: string }, qty: number): number {
  const existing = cartState.find(i => i.skuId === product.skuId)
  if (existing) {
    existing.count += qty
  } else {
    cartState.unshift({
      itemId: nextItemId++,
      skuId: product.skuId,
      skuName: product.skuName,
      price: product.price,
      image: product.image,
      count: qty,
      checked: true,
    })
  }
  return getCartCount()
}

export function removeCartItem(itemId: number): number {
  const idx = cartState.findIndex(i => i.itemId === itemId)
  if (idx !== -1) cartState.splice(idx, 1)
  return getCartCount()
}

export function updateCartItemQty(itemId: number, count: number): number {
  const item = cartState.find(i => i.itemId === itemId)
  if (item) item.count = Math.max(1, count)
  return getCartCount()
}

export function toggleCartItemChecked(itemId: number) {
  const item = cartState.find(i => i.itemId === itemId)
  if (item) item.checked = !item.checked
}

export function setAllChecked(checked: boolean) {
  cartState.forEach(i => (i.checked = checked))
}
