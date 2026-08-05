export interface ApiImage {
  imageUrl: string
  sort: number
}

export interface ApiAttrValue {
  attrId: number
  attrName: string
  attrValue: string
}

export interface ApiSkuDetail {
  id: number
  spuId: number
  name: string
  price: number
  mainImage: string
  saleCount: number
  sort: number
  images: ApiImage[]
  saleAttrValues: ApiAttrValue[]
}

export interface ApiSpuDetail {
  id: number
  categoryId: number
  brandId: number
  name: string
  description: string
  mainImage: string
  status: number
  attrValues: ApiAttrValue[]
  images: ApiImage[]
  skus: ApiSkuDetail[]
}

// 詳情頁是 SPU 導向（見 pages/item/[spuId].vue）：一次查回整個 SPU 底下所有 SKU，規格切換
// 純前端狀態，不用再查一次後端。非 ON_SHELF 一律 404（GetPublicSpuDetailService 隱藏其存在），
// 統一吞掉錯誤回傳 null，頁面用 null 判斷「找不到此商品」。
export function useProductDetail(spuId: number) {
  return useAsyncData<ApiSpuDetail | null>(`product-detail-${spuId}`, () =>
    $fetch<ApiSpuDetail>(`/api/public/products/spus/${spuId}`).catch(() => null)
  )
}
