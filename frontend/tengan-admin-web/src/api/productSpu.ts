import { http } from "@/utils/http";

/** 對齊 tengan-admin ProductSpuController 的回應形狀（BFF 轉發 tengan-product）。 */
export type SpuSummaryItem = {
  id: number;
  categoryId: number;
  brandId: number;
  name: string;
  mainImage?: string;
  status: number;
  skuCount: number;
};

export type SpuBaseAttrValueItem = {
  attrId: number;
  attrName: string;
  attrValue: string;
  standardValueId: number | null;
};

/** Spu 層級共通圖片——所有底下的 Sku 共用，跟各自專屬的 SkuImageItem 分開存。 */
export type SpuImageItem = {
  imageUrl: string;
  sort: number;
};

export type SkuImageItem = {
  imageUrl: string;
  sort: number;
};

export type SkuSaleAttrValueItem = {
  attrId: number;
  attrName: string;
  attrValue: string;
  standardValueId: number | null;
};

export type SkuDetailItem = {
  id: number;
  spuId: number;
  name: string;
  price: number;
  mainImage?: string;
  saleCount: number;
  sort: number;
  purchaseLimitPerUser?: number;
  images: Array<SkuImageItem>;
  saleAttrValues: Array<SkuSaleAttrValueItem>;
};

export type SpuDetailItem = {
  id: number;
  categoryId: number;
  brandId: number;
  name: string;
  description?: string;
  mainImage?: string;
  status: number;
  saleStartTime?: string;
  showOnLaunchTeaser?: boolean;
  teaserRemoveAt?: string;
  attrValues: Array<SpuBaseAttrValueItem>;
  images: Array<SpuImageItem>;
  skus: Array<SkuDetailItem>;
};

export type SearchSpusParams = {
  categoryId?: number;
  brandId?: number;
  name?: string;
  status?: number;
  pageNum: number;
  pageSize: number;
};

export const searchSpus = (params: SearchSpusParams) => {
  return http.request<{ items: Array<SpuSummaryItem>; total: number }>(
    "get",
    "/api/admin/products/spus",
    { params }
  );
};

export const getSpuDetail = (id: number) => {
  return http.request<SpuDetailItem>("get", `/api/admin/products/spus/${id}`);
};

export type SpuBaseAttrValueFormData = {
  attrId: number;
  attrValue: string;
  standardValueId: number | null;
};

export type SpuImageFormData = {
  imageUrl: string;
  sort: number;
};

export type SkuImageFormData = {
  imageUrl: string;
  sort: number;
};

export type SkuSaleAttrValueFormData = {
  attrId: number;
  attrValue: string;
  standardValueId: number | null;
};

export type SkuFormData = {
  /** 編輯既有規格時要帶入原本的 id，後端才能保留 id/銷量做真正的更新，而不是刪除重建成新規格。 */
  id?: number;
  name: string;
  price: number;
  mainImage?: string;
  sort: number;
  purchaseLimitPerUser?: number;
  images: Array<SkuImageFormData>;
  saleAttrValues: Array<SkuSaleAttrValueFormData>;
};

export type SpuFormData = {
  categoryId: number;
  brandId: number;
  name: string;
  description?: string;
  mainImage?: string;
  saleStartTime?: string;
  showOnLaunchTeaser?: boolean;
  teaserRemoveAt?: string;
  attrValues: Array<SpuBaseAttrValueFormData>;
  images: Array<SpuImageFormData>;
  skus: Array<SkuFormData>;
};

export const createSpu = (data: SpuFormData) => {
  return http.request<{ id: number }>("post", "/api/admin/products/spus", {
    data
  });
};

export const updateSpu = (id: number, data: SpuFormData) => {
  return http.request<void>("put", `/api/admin/products/spus/${id}`, {
    data
  });
};

/** 底下至少要有一顆 Sku 才能上架，沒有的話後端回 409。 */
export const publishSpu = (id: number) => {
  return http.request<void>("put", `/api/admin/products/spus/${id}/publish`);
};

/** 只有上架中才能下架，否則後端回 409。 */
export const unlistSpu = (id: number) => {
  return http.request<void>("put", `/api/admin/products/spus/${id}/unlist`);
};

/** 上架中不能刪除，後端會回 409——前端也在列表頁把刪除按鈕 disable 掉，這是最後一道防線。 */
export const deleteSpu = (id: number) => {
  return http.request<void>("delete", `/api/admin/products/spus/${id}`);
};

/** 整份複製一個新 SPU 草稿（狀態=複製草稿），回傳新 spu id，前端拿去直接導到編輯頁。 */
export const duplicateSpu = (id: number) => {
  return http.request<{ id: number }>(
    "post",
    `/api/admin/products/spus/${id}/duplicate`
  );
};

/** gateWarmedAt 為 null 代表這顆 SPU 從未啟用過防超賣保護，列表頁「防超賣保護」欄位顯示「啟用」而非「重設」。 */
export type GateConfigItem = {
  spuId: number;
  gateWarmedAt?: string;
  gateCloseTime?: string;
};

/** SPU 列表頁「防超賣保護」欄位批次回填用，只回傳有資料的 spuId。 */
export const getGateStatusBySpus = (ids: Array<number>) => {
  return http.request<{ items: Array<GateConfigItem> }>("get", "/api/admin/products/spus/gate-status", {
    params: { ids: ids.join(",") }
  });
};

export type GateStockCheckItem = {
  skuId: number;
  skuName: string;
};

/** 「啟用/重設防超賣保護」點擊當下用，回傳目前沒有庫存的 SKU 清單（空陣列代表全部都有庫存）。 */
export const getGateStockCheck = (id: number) => {
  return http.request<{ items: Array<GateStockCheckItem> }>(
    "get",
    `/api/admin/products/spus/${id}/gate-stock-check`
  );
};

/** 「啟用/重設防超賣保護」真正送出，對這顆 SPU 底下當下所有 SKU 一次套用同一組保護結束時間。 */
export const configureSpuGate = (id: number, gateCloseTime: string) => {
  return http.request<void>("put", `/api/admin/products/spus/${id}/gate`, {
    data: { gateCloseTime }
  });
};
