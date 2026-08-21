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

export interface SeckillActivity {
  id: number
  activityType: 'FLASH_SALE' | 'LAUNCH'
  startTime: string
  endTime: string
  skus: SeckillSku[]
}

export interface SeckillActivityListResult {
  activities: SeckillActivity[]
}

/**
 * 取代 mocks/seckill.ts 的角色。多個頁面/元件（首頁輪播、/seckill 列表頁、商品詳情頁徽章）都呼叫
 * 同一個 URL，Nuxt useFetch 在同一次渲染裡會自動去重，不用自己另外做快取。
 */
export function useSeckill() {
  return useFetch<SeckillActivityListResult>('/api/seckill/activities')
}
