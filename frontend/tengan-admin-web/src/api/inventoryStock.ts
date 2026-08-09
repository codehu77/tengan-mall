import { http } from "@/utils/http";

/** 對齊 tengan-admin InventoryStockController 的回應形狀（BFF 轉發 tengan-inventory）。 */
export type SkuStockItem = {
  wareId: number;
  skuId: number;
  stock: number;
  lockedStock: number;
  spuId?: number;
  skuName?: string;
  mainImage?: string;
};

export type SkuStockListResult = {
  items: Array<SkuStockItem>;
  total: number;
};

export type SkuStockQuery = {
  wareId?: number;
  keyword?: number;
  page: number;
  pageSize: number;
};

export const getSkuStockList = (params: SkuStockQuery) => {
  return http.request<SkuStockListResult>("get", "/api/admin/inventory/skus", {
    params
  });
};

/** 全新 SKU 第一次建立庫存列，沒有舊值可覆蓋，用絕對值。 */
export type CreateStockData = {
  skuId: number;
  wareId: number;
  initialStock: number;
};

export const createStock = (data: CreateStockData) => {
  return http.request<void>("post", "/api/admin/inventory/skus", { data });
};

/** delta 可正可負（+10=補貨、-3=報損），不是覆蓋絕對值。 */
export type AdjustStockData = {
  wareId: number;
  delta: number;
  reason: string;
};

export const adjustStock = (skuId: number, data: AdjustStockData) => {
  return http.request<void>("put", `/api/admin/inventory/skus/${skuId}`, {
    data
  });
};
