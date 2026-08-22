export interface SeckillSku {
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
export interface FlashSaleSession {
  activityId: number
  sessionId: number
  sessionName: string | null
  startTime: string
  endTime: string
  status: 'ACTIVE' | 'PUBLISHED'
  skus: SeckillSku[]
}

export interface Launch {
  activityId: number
  startTime: string
  endTime: string
  skus: SeckillSku[]
}

export interface SeckillDisplayResult {
  flashSaleSessions: FlashSaleSession[]
  launches: Launch[]
}

/**
 * 取代 mocks/seckill.ts 的角色。多個頁面/元件（首頁輪播、/seckill 列表頁、商品詳情頁徽章）都呼叫
 * 同一個 URL，Nuxt useFetch 在同一次渲染裡會自動去重，不用自己另外做快取。
 */
export function useSeckill() {
  return useFetch<SeckillDisplayResult>('/api/seckill/activities')
}
