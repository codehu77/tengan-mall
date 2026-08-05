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
};

export type SkuDetailItem = {
  id: number;
  spuId: number;
  name: string;
  price: number;
  mainImage?: string;
  saleCount: number;
  sort: number;
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
};

export type SkuFormData = {
  name: string;
  price: number;
  mainImage?: string;
  sort: number;
  images: Array<SkuImageFormData>;
  saleAttrValues: Array<SkuSaleAttrValueFormData>;
};

export type SpuFormData = {
  categoryId: number;
  brandId: number;
  name: string;
  description?: string;
  mainImage?: string;
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
