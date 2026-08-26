export interface LaunchTeaserProduct {
  spuId: number
  name: string
  mainImage: string
  price: number
  saleStartTime: string
}

interface LaunchTeaserApiItem {
  id: number
  name: string
  mainImage: string
  price: number
  saleStartTime: string
}

interface LaunchTeaserListApiResult {
  items: LaunchTeaserApiItem[]
}

interface LaunchTeaserPageApiResult {
  items: LaunchTeaserApiItem[]
  total: number
}

function toProduct(item: LaunchTeaserApiItem): LaunchTeaserProduct {
  return {
    spuId: item.id,
    name: item.name,
    mainImage: item.mainImage,
    price: item.price,
    saleStartTime: item.saleStartTime,
  }
}

/** 首頁「即將開賣」預告區塊用，不分頁，後端 cap 在 limit 筆內。 */
export function useLaunchTeaser(limit = 20) {
  return useAsyncData(`launch-teaser-${limit}`, async () => {
    const result = await $fetch<LaunchTeaserListApiResult>('/api/public/products/launch-teaser', {
      query: { limit },
    })
    return { items: (result.items ?? []).map(toProduct) }
  })
}

/** 「看更多」全部清單頁用，分頁版本。 */
export function useLaunchTeaserPage(pageNum: Ref<number>, pageSize = 20) {
  return useAsyncData(
    'launch-teaser-page',
    async () => {
      const result = await $fetch<LaunchTeaserPageApiResult>('/api/public/products/launch-teaser/page', {
        query: { pageNum: pageNum.value, pageSize },
      })
      return { items: (result.items ?? []).map(toProduct), total: result.total ?? 0 }
    },
    { watch: [pageNum] },
  )
}
