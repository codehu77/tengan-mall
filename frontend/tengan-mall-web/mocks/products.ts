export interface Category {
  id: number
  name: string
  icon: string
}

export interface CategoryTree {
  catId: number
  name: string
  icon?: string
  children?: CategoryTree[]
}

export interface Product {
  skuId: number
  spuId?: number
  skuName: string
  price: number
  skuDefaultImg: string
  saleCount: number
  categoryId: number
  /** 目前是否正處於進行中的限時搶購（ACTIVE 場次），用於商品卡右上角徽章。 */
  isSeckill?: boolean
  /** isSeckill 為 true 時，price 已經換成秒殺價，這裡放原價供劃線對比顯示（跟首頁限時搶購卡一致）。 */
  originalPrice?: number
}

export const MOCK_CATEGORIES: Category[] = [
  { id: 1, name: '手機', icon: '📱' },
  { id: 2, name: '電腦', icon: '💻' },
  { id: 3, name: '家電', icon: '📺' },
  { id: 4, name: '服飾', icon: '👗' },
  { id: 5, name: '食品', icon: '🍱' },
  { id: 6, name: '美妝', icon: '💄' },
  { id: 7, name: '運動', icon: '⚽' },
  { id: 8, name: '書籍', icon: '📚' },
  { id: 9, name: '玩具', icon: '🧸' },
  { id: 10, name: '汽車', icon: '🚗' },
]

export const MOCK_PRODUCTS: Product[] = [
  { skuId: 1, skuName: 'Apple iPhone 15 Pro 256GB 原色鈦金屬', price: 39900, skuDefaultImg: 'https://placehold.co/300x300?text=iPhone+15+Pro', saleCount: 1823, categoryId: 1 },
  { skuId: 2, skuName: 'Samsung Galaxy S24 Ultra 512GB 幻影黑', price: 42900, skuDefaultImg: 'https://placehold.co/300x300?text=S24+Ultra', saleCount: 956, categoryId: 1 },
  { skuId: 3, skuName: 'Sony WH-1000XM5 無線降噪耳機', price: 11900, skuDefaultImg: 'https://placehold.co/300x300?text=Sony+WH1000XM5', saleCount: 2341, categoryId: 3 },
  { skuId: 4, skuName: 'MacBook Air 13吋 M3 晶片 8GB / 256GB', price: 37900, skuDefaultImg: 'https://placehold.co/300x300?text=MacBook+Air+M3', saleCount: 782, categoryId: 2 },
  { skuId: 5, skuName: 'iPad Air 11吋 M2 Wi-Fi 128GB 藍色', price: 19900, skuDefaultImg: 'https://placehold.co/300x300?text=iPad+Air+M2', saleCount: 1134, categoryId: 2 },
  { skuId: 6, skuName: 'ASUS ROG Zephyrus G14 電競筆電 RTX 4060', price: 49900, skuDefaultImg: 'https://placehold.co/300x300?text=ROG+Zephyrus', saleCount: 445, categoryId: 2 },
  { skuId: 7, skuName: 'Dyson V15 Detect 無線吸塵器', price: 21900, skuDefaultImg: 'https://placehold.co/300x300?text=Dyson+V15', saleCount: 678, categoryId: 3 },
  { skuId: 8, skuName: 'Nintendo Switch OLED 白色主機', price: 10980, skuDefaultImg: 'https://placehold.co/300x300?text=Switch+OLED', saleCount: 3201, categoryId: 9 },
  { skuId: 9, skuName: 'Sony PlayStation 5 數位版主機', price: 13980, skuDefaultImg: 'https://placehold.co/300x300?text=PS5+Digital', saleCount: 2109, categoryId: 9 },
  { skuId: 10, skuName: 'Apple Watch Series 9 GPS 45mm 午夜色', price: 14900, skuDefaultImg: 'https://placehold.co/300x300?text=Apple+Watch+S9', saleCount: 1456, categoryId: 1 },
  { skuId: 11, skuName: 'LG OLED55C3PSA 55吋 OLED 4K 電視', price: 52900, skuDefaultImg: 'https://placehold.co/300x300?text=LG+OLED+55', saleCount: 312, categoryId: 3 },
  { skuId: 12, skuName: 'Panasonic 國際牌 變頻冷暖分離式冷氣 CS-UX22BDA2', price: 31900, skuDefaultImg: 'https://placehold.co/300x300?text=Panasonic+AC', saleCount: 567, categoryId: 3 },
]

