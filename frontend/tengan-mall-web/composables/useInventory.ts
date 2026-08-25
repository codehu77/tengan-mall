// /api/public/inventory/** 不需要登入（訪客購物車也要能查庫存），用 nuxt.config.ts 既有的
// /api/public/** proxy 規則，直接 $fetch 相對路徑即可（跟 useProductDetail/useSeckill 同一套模式）。
export function useInventory() {
  async function fetchSkuStock(skuId: number): Promise<number> {
    const res = await $fetch<{ skuId: number; availableStock: number }>(`/api/public/inventory/skus/${skuId}`)
    return res.availableStock
  }

  /** 逐顆查（後端目前只有單顆查詢端點），數量通常不多（購物車/單一商品頁），平行呼叫即可。 */
  async function fetchSkuStocks(skuIds: number[]): Promise<Record<number, number>> {
    const uniqueIds = Array.from(new Set(skuIds))
    const results = await Promise.all(uniqueIds.map(id => fetchSkuStock(id)))
    const map: Record<number, number> = {}
    uniqueIds.forEach((id, i) => { map[id] = results[i] })
    return map
  }

  return { fetchSkuStock, fetchSkuStocks }
}
