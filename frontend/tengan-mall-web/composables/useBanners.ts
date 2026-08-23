export interface Banner {
  id: number
  imageUrl: string
  linkUrl: string | null
  title: string | null
}

interface BannerListResult {
  banners: Banner[]
}

export function useBanners() {
  return useFetch<BannerListResult>('/api/banners')
}
