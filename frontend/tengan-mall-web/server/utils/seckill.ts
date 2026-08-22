/** 對齊 tengan-seckill PublicSeckillController 的回應形狀。 */
export interface PublicSeckillSku {
  skuId: number
  spuId: number
  name: string
  mainImage: string
  originalPrice: number
  seckillPrice: number
  limitPerUser: number
  remaining: number
}

/** status 是 "ACTIVE"（現正瘋搶，可下單）或 "PUBLISHED"（準時開搶，只能預覽）。 */
export interface PublicFlashSaleSession {
  activityId: number
  sessionId: number
  sessionName: string | null
  startTime: string
  endTime: string
  status: 'ACTIVE' | 'PUBLISHED'
  skus: PublicSeckillSku[]
}

export interface PublicLaunch {
  activityId: number
  startTime: string
  endTime: string
  skus: PublicSeckillSku[]
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
 * 供購物車/商品詳情頁顯示搶購價用——攤平成 skuId 對應表。**只收 ACTIVE 場次 + 首發**，PUBLISHED
 * （還沒開賣）的場次不進這張表，避免使用者在開賣前就用搶購價下單。活動結束後 tengan-seckill 那支
 * Redis key 過期，這裡自然拿不到該 skuId，顯示會自動變回原價，不用寫「是否過期」判斷式（延續後端
 * 同一個設計原則）。查詢失敗時回傳空表，讓頁面照樣能顯示原價，不因為這個展示用途的呼叫失敗而整頁掛掉。
 */
export async function fetchActiveSeckillMap(): Promise<Map<number, PublicSeckillSku>> {
  try {
    const { flashSaleSessions, launches } = await fetchSeckillActivities()
    const map = new Map<number, PublicSeckillSku>()
    for (const session of flashSaleSessions) {
      if (session.status !== 'ACTIVE') continue
      for (const sku of session.skus) {
        map.set(sku.skuId, sku)
      }
    }
    for (const launch of launches) {
      for (const sku of launch.skus) {
        map.set(sku.skuId, sku)
      }
    }
    return map
  } catch {
    return new Map()
  }
}
