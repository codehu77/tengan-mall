export interface CartItem {
  itemId: number
  skuId: number
  /** 商品詳情頁是 spu 導向路由（/item/{spuId}），不是 sku——sku 已刪除時為 null，該項目連結失效。 */
  spuId: number | null
  skuName: string
  price: number
  image: string
  count: number
  checked: boolean
  /** sku 已被刪除時 false（下架仍算 true——後端不因下架隱藏購物車項目，只有真的刪除才會）。 */
  available: boolean
  /** 只有目前活躍秒殺的 sku 才有值；活動結束後自動變回 undefined，不是「過期」這種特殊狀態。 */
  seckillPrice?: number
  seckillRemaining?: number
}
