export interface OrderItem {
  skuId: number
  skuName: string
  price: number
  count: number
  image: string
}

export type CancelReason = '買家自主取消' | '未按時付款，系統自動取消' | '包裹找尋中 / 買家逾期未取 / 拒收'

export interface Order {
  orderId: number
  orderSn: string
  status: 0 | 1 | 2 | 3 | 4
  totalAmount: number
  items: OrderItem[]
  createTime: string
  receiverName: string
  receiverPhone: string
  receiverAddress: string
  paymentMethod: 'credit_card' | 'mobile' | 'cod'
  couponName?: string
  couponDiscount?: number
  cancelReason?: CancelReason
}

export const ORDER_STATUS: Record<number, { label: string; color: string }> = {
  0: { label: '待付款', color: 'orange' },
  1: { label: '待出貨', color: 'blue' },
  2: { label: '待收貨', color: 'purple' },
  3: { label: '已完成', color: 'green' },
  4: { label: '不成立', color: 'gray' },
}

export const MOCK_ORDERS: Order[] = [
  {
    orderId: 1001,
    orderSn: 'TG20240101000001',
    status: 1,
    totalAmount: 39900,
    createTime: '2024-06-01 14:32:00',
    receiverName: '王小明',
    receiverPhone: '0912345678',
    receiverAddress: '台北市信義區信義路五段 7 號',
    paymentMethod: 'credit_card',
    couponName: '新會員優惠券',
    couponDiscount: 100,
    items: [
      { skuId: 1, skuName: 'Apple iPhone 15 Pro 256GB 原色鈦金屬', price: 39900, count: 1, image: 'https://placehold.co/80x80?text=iPhone' },
    ],
  },
  {
    orderId: 1002,
    orderSn: 'TG20240102000002',
    status: 3,
    totalAmount: 23800,
    createTime: '2024-05-28 09:15:00',
    receiverName: '王小明',
    receiverPhone: '0912345678',
    receiverAddress: '台北市信義區信義路五段 7 號',
    paymentMethod: 'mobile',
    couponName: '限時折扣券',
    couponDiscount: 50,
    items: [
      { skuId: 3, skuName: 'Sony WH-1000XM5 無線降噪耳機', price: 11900, count: 2, image: 'https://placehold.co/80x80?text=Sony' },
    ],
  },
  {
    orderId: 1003,
    orderSn: 'TG20240103000003',
    status: 0,
    totalAmount: 10980,
    createTime: '2024-06-10 20:45:00',
    receiverName: '王小明',
    receiverPhone: '0912345678',
    receiverAddress: '台北市信義區信義路五段 7 號',
    paymentMethod: 'cod',
    items: [
      { skuId: 8, skuName: 'Nintendo Switch OLED 白色主機', price: 10980, count: 1, image: 'https://placehold.co/80x80?text=Switch' },
    ],
  },
  {
    orderId: 1004,
    orderSn: 'TG20240104000004',
    status: 4,
    totalAmount: 3290,
    createTime: '2024-06-15 11:20:00',
    receiverName: '王小明',
    receiverPhone: '0912345678',
    receiverAddress: '台北市信義區信義路五段 7 號',
    paymentMethod: 'credit_card',
    cancelReason: '買家自主取消',
    items: [
      { skuId: 5, skuName: 'Logitech MX Master 3S 無線滑鼠', price: 3290, count: 1, image: 'https://placehold.co/80x80?text=Mouse' },
    ],
  },
  {
    orderId: 1005,
    orderSn: 'TG20240105000005',
    status: 4,
    totalAmount: 5990,
    createTime: '2024-06-18 09:00:00',
    receiverName: '王小明',
    receiverPhone: '0912345678',
    receiverAddress: '台北市信義區信義路五段 7 號',
    paymentMethod: 'credit_card',
    cancelReason: '未按時付款，系統自動取消',
    items: [
      { skuId: 6, skuName: 'Samsung Galaxy Tab S9 FE Wi-Fi 128GB', price: 5990, count: 1, image: 'https://placehold.co/80x80?text=Tab' },
    ],
  },
  {
    orderId: 1006,
    orderSn: 'TG20240106000006',
    status: 4,
    totalAmount: 1890,
    createTime: '2024-06-20 14:30:00',
    receiverName: '王小明',
    receiverPhone: '0912345678',
    receiverAddress: '台北市信義區信義路五段 7 號',
    paymentMethod: 'cod',
    cancelReason: '包裹找尋中 / 買家逾期未取 / 拒收',
    items: [
      { skuId: 7, skuName: 'Anker 充電器 65W GaN 三孔', price: 1890, count: 1, image: 'https://placehold.co/80x80?text=Anker' },
    ],
  },
]

export const MOCK_ADDRESS = {
  name: '王小明',
  phone: '0912-345-678',
  address: '台北市信義區信義路五段 7 號 10 樓',
}
