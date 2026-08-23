export interface PublicBanner {
  id: number
  imageUrl: string
  linkUrl: string | null
  title: string | null
}

export interface PublicBannerListResult {
  banners: PublicBanner[]
}

/** 純轉發，不需要登入。對齊 tengan-media PublicMediaController 的回應形狀。 */
export default defineEventHandler(async () => {
  return await callBackend<PublicBannerListResult>('/api/public/media/banners')
})
