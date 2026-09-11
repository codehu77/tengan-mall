import type { FreightRule } from '~/types/order'

/** 公開端點，訪客也能看——供購物車頁顯示免運門檻提示，不需要 access token。 */
export default defineEventHandler(async (): Promise<FreightRule> => {
  const config = useRuntimeConfig()
  return $fetch(`${config.public.apiBase}/api/public/orders/freight-rule`)
})
