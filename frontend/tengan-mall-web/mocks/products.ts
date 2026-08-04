export interface Category {
  id: number
  name: string
  icon: string
}

export interface CategoryTree {
  catId: number
  name: string
  catLevel: number
  icon?: string
  children?: CategoryTree[]
}

export interface Product {
  skuId: number
  skuName: string
  price: number
  skuDefaultImg: string
  saleCount: number
  categoryId: number
}

export interface SkuAttr {
  attrName: string
  options: string[]
}

export interface ProductDetail extends Product {
  images: string[]
  attrs: SkuAttr[]
  description: string
  specs: { label: string; value: string }[]
  stock: number
}

export const MOCK_CATEGORY_TREE: CategoryTree[] = [
  {
    catId: 1, name: '手機', catLevel: 1, icon: '📱',
    children: [
      { catId: 11, name: '手機通訊', catLevel: 2, children: [
        { catId: 111, name: '智慧型手機', catLevel: 3 },
        { catId: 112, name: '折疊手機', catLevel: 3 },
        { catId: 113, name: '老人機', catLevel: 3 },
      ]},
      { catId: 12, name: '手機配件', catLevel: 2, children: [
        { catId: 121, name: '手機殼', catLevel: 3 },
        { catId: 122, name: '充電器', catLevel: 3 },
        { catId: 123, name: '藍牙耳機', catLevel: 3 },
        { catId: 124, name: '螢幕保護貼', catLevel: 3 },
      ]},
    ],
  },
  {
    catId: 2, name: '電腦', catLevel: 1, icon: '💻',
    children: [
      { catId: 21, name: '筆記型電腦', catLevel: 2, children: [
        { catId: 211, name: '輕薄筆電', catLevel: 3 },
        { catId: 212, name: '電競筆電', catLevel: 3 },
        { catId: 213, name: '商務筆電', catLevel: 3 },
      ]},
      { catId: 22, name: '電腦周邊', catLevel: 2, children: [
        { catId: 221, name: '鍵盤', catLevel: 3 },
        { catId: 222, name: '滑鼠', catLevel: 3 },
        { catId: 223, name: '螢幕', catLevel: 3 },
        { catId: 224, name: '耳機', catLevel: 3 },
      ]},
      { catId: 23, name: '桌上型電腦', catLevel: 2, children: [
        { catId: 231, name: '品牌電腦', catLevel: 3 },
        { catId: 232, name: '組裝主機', catLevel: 3 },
      ]},
    ],
  },
  {
    catId: 3, name: '家電', catLevel: 1, icon: '📺',
    children: [
      { catId: 31, name: '影音娛樂', catLevel: 2, children: [
        { catId: 311, name: '電視', catLevel: 3 },
        { catId: 312, name: '投影機', catLevel: 3 },
        { catId: 313, name: '音響', catLevel: 3 },
      ]},
      { catId: 32, name: '清潔家電', catLevel: 2, children: [
        { catId: 321, name: '吸塵器', catLevel: 3 },
        { catId: 322, name: '掃地機器人', catLevel: 3 },
        { catId: 323, name: '洗碗機', catLevel: 3 },
      ]},
      { catId: 33, name: '冷暖設備', catLevel: 2, children: [
        { catId: 331, name: '冷氣', catLevel: 3 },
        { catId: 332, name: '電風扇', catLevel: 3 },
        { catId: 333, name: '空氣清淨機', catLevel: 3 },
      ]},
    ],
  },
  {
    catId: 4, name: '服飾', catLevel: 1, icon: '👗',
    children: [
      { catId: 41, name: '男裝', catLevel: 2, children: [
        { catId: 411, name: 'T恤', catLevel: 3 },
        { catId: 412, name: '襯衫', catLevel: 3 },
        { catId: 413, name: '外套', catLevel: 3 },
        { catId: 414, name: '褲子', catLevel: 3 },
      ]},
      { catId: 42, name: '女裝', catLevel: 2, children: [
        { catId: 421, name: '上衣', catLevel: 3 },
        { catId: 422, name: '洋裝', catLevel: 3 },
        { catId: 423, name: '裙子', catLevel: 3 },
        { catId: 424, name: '外套', catLevel: 3 },
      ]},
    ],
  },
  {
    catId: 5, name: '食品', catLevel: 1, icon: '🍱',
    children: [
      { catId: 51, name: '零食點心', catLevel: 2, children: [
        { catId: 511, name: '餅乾', catLevel: 3 },
        { catId: 512, name: '糖果', catLevel: 3 },
        { catId: 513, name: '堅果', catLevel: 3 },
      ]},
      { catId: 52, name: '飲品', catLevel: 2, children: [
        { catId: 521, name: '咖啡', catLevel: 3 },
        { catId: 522, name: '茶飲', catLevel: 3 },
        { catId: 523, name: '果汁', catLevel: 3 },
      ]},
    ],
  },
  {
    catId: 6, name: '美妝', catLevel: 1, icon: '💄',
    children: [
      { catId: 61, name: '護膚', catLevel: 2, children: [
        { catId: 611, name: '精華液', catLevel: 3 },
        { catId: 612, name: '面膜', catLevel: 3 },
        { catId: 613, name: '乳液', catLevel: 3 },
      ]},
      { catId: 62, name: '彩妝', catLevel: 2, children: [
        { catId: 621, name: '口紅', catLevel: 3 },
        { catId: 622, name: '粉底', catLevel: 3 },
        { catId: 623, name: '眼影', catLevel: 3 },
      ]},
    ],
  },
  {
    catId: 7, name: '運動', catLevel: 1, icon: '⚽',
    children: [
      { catId: 71, name: '運動服飾', catLevel: 2, children: [
        { catId: 711, name: '運動上衣', catLevel: 3 },
        { catId: 712, name: '運動褲', catLevel: 3 },
        { catId: 713, name: '運動鞋', catLevel: 3 },
      ]},
      { catId: 72, name: '健身器材', catLevel: 2, children: [
        { catId: 721, name: '啞鈴', catLevel: 3 },
        { catId: 722, name: '瑜珈墊', catLevel: 3 },
        { catId: 723, name: '跑步機', catLevel: 3 },
      ]},
    ],
  },
  {
    catId: 8, name: '書籍', catLevel: 1, icon: '📚',
    children: [
      { catId: 81, name: '文學小說', catLevel: 2, children: [
        { catId: 811, name: '台灣文學', catLevel: 3 },
        { catId: 812, name: '日本文學', catLevel: 3 },
        { catId: 813, name: '歐美文學', catLevel: 3 },
      ]},
      { catId: 82, name: '學習進修', catLevel: 2, children: [
        { catId: 821, name: '語言學習', catLevel: 3 },
        { catId: 822, name: '程式設計', catLevel: 3 },
        { catId: 823, name: '商業管理', catLevel: 3 },
      ]},
    ],
  },
  {
    catId: 9, name: '玩具', catLevel: 1, icon: '🧸',
    children: [
      { catId: 91, name: '電玩遊戲', catLevel: 2, children: [
        { catId: 911, name: '主機遊戲', catLevel: 3 },
        { catId: 912, name: '手遊周邊', catLevel: 3 },
      ]},
      { catId: 92, name: '模型收藏', catLevel: 2, children: [
        { catId: 921, name: 'Gunpla 鋼彈', catLevel: 3 },
        { catId: 922, name: '公仔', catLevel: 3 },
        { catId: 923, name: '樂高', catLevel: 3 },
      ]},
    ],
  },
  {
    catId: 10, name: '汽車', catLevel: 1, icon: '🚗',
    children: [
      { catId: 101, name: '汽車用品', catLevel: 2, children: [
        { catId: 1011, name: '行車紀錄器', catLevel: 3 },
        { catId: 1012, name: '車用香氛', catLevel: 3 },
        { catId: 1013, name: '車用充電器', catLevel: 3 },
      ]},
      { catId: 102, name: '機車用品', catLevel: 2, children: [
        { catId: 1021, name: '安全帽', catLevel: 3 },
        { catId: 1022, name: '機車配件', catLevel: 3 },
      ]},
    ],
  },
]

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

export const MOCK_PRODUCT_DETAILS: Record<number, ProductDetail> = {
  1: {
    skuId: 1,
    skuName: 'Apple iPhone 15 Pro 256GB 原色鈦金屬',
    price: 39900,
    skuDefaultImg: 'https://placehold.co/600x600?text=iPhone+15+Pro',
    saleCount: 1823,
    categoryId: 1,
    stock: 99,
    images: [
      'https://placehold.co/600x600?text=iPhone+15+Pro+正面',
      'https://placehold.co/600x600?text=iPhone+15+Pro+背面',
      'https://placehold.co/600x600?text=iPhone+15+Pro+側面',
      'https://placehold.co/600x600?text=iPhone+15+Pro+盒裝',
    ],
    attrs: [
      { attrName: '顏色', options: ['原色鈦金屬', '黑色鈦金屬', '白色鈦金屬', '藍色鈦金屬'] },
      { attrName: '容量', options: ['128GB', '256GB', '512GB', '1TB'] },
    ],
    specs: [
      { label: '螢幕', value: '6.1 吋 Super Retina XDR' },
      { label: '晶片', value: 'A17 Pro' },
      { label: '相機', value: '4800 萬畫素主鏡頭' },
      { label: '電池', value: '最長 23 小時影片播放' },
      { label: '防水', value: 'IP68（最深 6 公尺，30 分鐘）' },
      { label: '重量', value: '187 公克' },
    ],
    description: 'iPhone 15 Pro 搭載 A17 Pro 晶片，採用航太級鈦金屬設計，配備 USB 3 連接埠，支援 Action 按鈕，是 Apple 最先進的智慧型手機。',
  },
  4: {
    skuId: 4,
    skuName: 'MacBook Air 13吋 M3 晶片 8GB / 256GB',
    price: 37900,
    skuDefaultImg: 'https://placehold.co/600x600?text=MacBook+Air+M3',
    saleCount: 782,
    categoryId: 2,
    stock: 45,
    images: [
      'https://placehold.co/600x600?text=MacBook+Air+M3+正面',
      'https://placehold.co/600x600?text=MacBook+Air+M3+側面',
      'https://placehold.co/600x600?text=MacBook+Air+M3+鍵盤',
      'https://placehold.co/600x600?text=MacBook+Air+M3+連接埠',
    ],
    attrs: [
      { attrName: '顏色', options: ['午夜色', '星光色', '太空灰', '天藍色'] },
      { attrName: '記憶體', options: ['8GB', '16GB', '24GB'] },
      { attrName: '儲存空間', options: ['256GB', '512GB', '1TB', '2TB'] },
    ],
    specs: [
      { label: '螢幕', value: '13.6 吋 Liquid Retina' },
      { label: '晶片', value: 'Apple M3' },
      { label: 'CPU', value: '8 核心' },
      { label: 'GPU', value: '10 核心' },
      { label: '電池', value: '最長 18 小時' },
      { label: '重量', value: '1.24 公斤' },
    ],
    description: 'MacBook Air 搭載 Apple M3 晶片，輕薄設計，無風扇靜音運作，提供卓越的效能與長達 18 小時的電池續航力。',
  },
}

export function getMockProductDetail(skuId: number): ProductDetail | null {
  return MOCK_PRODUCT_DETAILS[skuId] ?? {
    ...MOCK_PRODUCTS.find(p => p.skuId === skuId),
    stock: 50,
    images: [`https://placehold.co/600x600?text=Product+${skuId}`],
    attrs: [{ attrName: '規格', options: ['標準版'] }],
    specs: [{ label: '品牌', value: '天願商城' }],
    description: '這是一個商品描述範例，實際串接後端 API 後將顯示真實資料。',
  } as ProductDetail
}
