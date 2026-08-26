export interface SkuStockInfo {
  availableStock: number
  /** 開賣時間，null=沒有設定開賣時間限制（一般商品可能一開始就可買）。 */
  saleStartTime: string | null
  /** 後端權威判斷：現在是否可購買（時間到了才會是 true），跟庫存是否足夠是兩件事。 */
  purchasable: boolean
  /** 每人限購件數，null=不限購。 */
  purchaseLimitPerUser: number | null
}

// /api/public/inventory/** 不需要登入（訪客購物車也要能查庫存），用 nuxt.config.ts 既有的
// /api/public/** proxy 規則，直接 $fetch 相對路徑即可（跟 useProductDetail/useSeckill 同一套模式）。
export function useInventory() {
  async function fetchSkuStock(skuId: number): Promise<SkuStockInfo> {
    const res = await $fetch<{
      skuId: number
      availableStock: number
      saleStartTime: string | null
      purchasable: boolean
      purchaseLimitPerUser: number | null
    }>(`/api/public/inventory/skus/${skuId}`)
    return {
      availableStock: res.availableStock,
      saleStartTime: res.saleStartTime,
      purchasable: res.purchasable,
      purchaseLimitPerUser: res.purchaseLimitPerUser,
    }
  }

  /** 逐顆查（後端目前只有單顆查詢端點），數量通常不多（購物車/單一商品頁），平行呼叫即可。 */
  async function fetchSkuStocks(skuIds: number[]): Promise<Record<number, SkuStockInfo>> {
    const uniqueIds = Array.from(new Set(skuIds))
    const results = await Promise.all(uniqueIds.map(id => fetchSkuStock(id)))
    const map: Record<number, SkuStockInfo> = {}
    uniqueIds.forEach((id, i) => { map[id] = results[i] })
    return map
  }

  return { fetchSkuStock, fetchSkuStocks }
}
