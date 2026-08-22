/** 對齊 tengan-seckill PublicSeckillController 的回應形狀（SPU 分組，一個商品一張卡，見「秒殺改成綁 SPU」規劃文件）。 */
export interface PublicSeckillSku {
  skuId: number
  variantLabel: string
  originalPrice: number
  seckillPrice: number
  limitPerUser: number
  remaining: number
}

export interface PublicSeckillProduct {
  spuId: number
  name: string
  mainImage: string
  skus: PublicSeckillSku[]
}

/** status 是 "ACTIVE"（現正瘋搶，可下單）或 "PUBLISHED"（準時開搶，只能預覽）。 */
export interface PublicFlashSaleSession {
  activityId: number
  sessionId: number
  sessionName: string | null
  startTime: string
  endTime: string
  status: 'ACTIVE' | 'PUBLISHED'
  products: PublicSeckillProduct[]
}

export interface PublicLaunch {
  activityId: number
  startTime: string
  endTime: string
  products: PublicSeckillProduct[]
}

export interface PublicSeckillDisplayResult {
  flashSaleSessions: PublicFlashSaleSession[]
  launches: PublicLaunch[]
}

/** 純轉發，不需要登入。 */
export async function fetchSeckillActivities(): Promise<PublicSeckillDisplayResult> {
  return callBackend<PublicSeckillDisplayResult>('/api/public/seckill/activities')
}

/**
 * 供購物車/商品詳情頁顯示搶購價用——攤平成 skuId 對應表。**只收 ACTIVE 場次 + 首發，且 remaining=0
 * 的規格不進這張表**（賣完的規格不該顯示搶購價/讓人加價購物車，商品頁另外用 fetchSeckillActivities()
 * 的原始資料判斷要不要把選項反灰，見場次機制/SPU 規劃文件）。活動結束後 tengan-seckill 那支 Redis key
 * 過期，這裡自然拿不到該 skuId，顯示會自動變回原價，不用寫「是否過期」判斷式（延續後端同一個設計原則）。
 * 查詢失敗時回傳空表，讓頁面照樣能顯示原價，不因為這個展示用途的呼叫失敗而整頁掛掉。
 */
export async function fetchActiveSeckillMap(): Promise<Map<number, PublicSeckillSku>> {
  try {
    const { flashSaleSessions, launches } = await fetchSeckillActivities()
    const map = new Map<number, PublicSeckillSku>()
    for (const session of flashSaleSessions) {
      if (session.status !== 'ACTIVE') continue
      for (const product of session.products) {
        for (const sku of product.skus) {
          if (sku.remaining <= 0) continue
          map.set(sku.skuId, sku)
        }
      }
    }
    for (const launch of launches) {
      for (const product of launch.products) {
        for (const sku of product.skus) {
          if (sku.remaining <= 0) continue
          map.set(sku.skuId, sku)
        }
      }
    }
    return map
  } catch {
    return new Map()
  }
}
