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

export interface PublicSeckillActivity {
  id: number
  activityType: 'FLASH_SALE' | 'LAUNCH'
  startTime: string
  endTime: string
  skus: PublicSeckillSku[]
}

export interface PublicSeckillActivityListResult {
  activities: PublicSeckillActivity[]
}

/** 純轉發，不需要登入。 */
export async function fetchSeckillActivities(): Promise<PublicSeckillActivityListResult> {
  return callBackend<PublicSeckillActivityListResult>('/api/public/seckill/activities')
}

/**
 * 供購物車顯示搶購價用——攤平成 skuId 對應表。活動結束後 tengan-seckill 那支 Redis key 過期，
 * 這裡自然拿不到該 skuId，購物車顯示會自動變回原價，不用寫「是否過期」判斷式（延續後端同一個
 * 設計原則）。查詢失敗時回傳空表，讓購物車照樣能顯示原價，不因為這個展示用途的呼叫失敗而整頁掛掉。
 */
export async function fetchActiveSeckillMap(): Promise<Map<number, PublicSeckillSku>> {
  try {
    const { activities } = await fetchSeckillActivities()
    const map = new Map<number, PublicSeckillSku>()
    for (const activity of activities) {
      for (const sku of activity.skus) {
        map.set(sku.skuId, sku)
      }
    }
    return map
  } catch {
    return new Map()
  }
}
