export interface SearchItem {
  skuId: number
  spuId: number
  skuName: string
  price: number
  mainImage: string
  saleCount: number
  brandId: number
  brandName: string
}

export interface BrandAgg {
  brandId: number
  brandName: string
  count: number
}

export interface AttrValueCount {
  value: string
  count: number
}

export interface AttrAgg {
  attrKey: string
  attrName: string
  values: AttrValueCount[]
}

export interface SearchAggregations {
  brands: BrandAgg[]
  attrs: AttrAgg[]
}

export interface SearchResponse {
  items: SearchItem[]
  total: number
  page: number
  pageSize: number
  aggregations: SearchAggregations
}

/**
 * query 的 key 大部分固定（keyword/catId/brandId/sort/order/page/pageSize），但屬性篩選是
 * `attrs[attrKey]` 這種動態 bracket key（attrKey 是 "BASE-10"/"SALE-10" 這種不透明字串，
 * 呼叫端從 aggregations 拿到什麼就原樣回填），型別上沒必要為了幾個動態 key 硬做複雜型別體操。
 */
export type ProductSearchQuery = Record<string, string | number | number[] | string[] | undefined>

export function useProductSearch(query: Ref<ProductSearchQuery>) {
  const { data, pending, error, refresh } = useFetch<SearchResponse>('/api/public/search', {
    query,
    watch: [query],
  })

  return { data, pending, error, refresh }
}
