import { http } from "@/utils/http";

/** 對齊 tengan-admin ProductBaseAttrGroupController 的回應形狀。 */
export type BaseAttrGroupItem = {
  id: number;
  categoryId: number;
  name: string;
  sort: number;
};

/** attrGroupId 一定歸屬某個 BaseAttrGroup，跟 SaleAttr 不同（見 BASE/SALE 拆域決策）。 */
export type BaseAttrItem = {
  id: number;
  categoryId: number;
  attrGroupId: number;
  name: string;
  searchable: boolean;
  sort: number;
};

/** SaleAttr 沒有分組概念，各 SPU 建 SKU 時自行決定要用哪些銷售屬性。 */
export type SaleAttrItem = {
  id: number;
  categoryId: number;
  name: string;
  searchable: boolean;
  sort: number;
};

export const getBaseAttrGroups = (categoryId: number) => {
  return http.request<{ items: Array<BaseAttrGroupItem> }>(
    "get",
    "/api/admin/products/base-attr-groups",
    { params: { categoryId } }
  );
};

export const createBaseAttrGroup = (data: {
  categoryId: number;
  name: string;
  sort: number;
}) => {
  return http.request<{ id: number }>(
    "post",
    "/api/admin/products/base-attr-groups",
    { data }
  );
};

export const updateBaseAttrGroup = (
  id: number,
  data: { name: string; sort: number }
) => {
  return http.request<void>(
    "put",
    `/api/admin/products/base-attr-groups/${id}`,
    { data }
  );
};

/** 底下還有 BaseAttr 時後端會回 409，呼叫端要接住錯誤訊息。 */
export const deleteBaseAttrGroup = (id: number) => {
  return http.request<void>(
    "delete",
    `/api/admin/products/base-attr-groups/${id}`
  );
};

export const getBaseAttrs = (categoryId: number) => {
  return http.request<{ items: Array<BaseAttrItem> }>(
    "get",
    "/api/admin/products/base-attrs",
    { params: { categoryId } }
  );
};

export const createBaseAttr = (data: {
  categoryId: number;
  attrGroupId: number;
  name: string;
  searchable: boolean;
  sort: number;
}) => {
  return http.request<{ id: number }>(
    "post",
    "/api/admin/products/base-attrs",
    { data }
  );
};

export const updateBaseAttr = (
  id: number,
  data: { attrGroupId: number; name: string; searchable: boolean; sort: number }
) => {
  return http.request<void>("put", `/api/admin/products/base-attrs/${id}`, {
    data
  });
};

export const deleteBaseAttr = (id: number) => {
  return http.request<void>("delete", `/api/admin/products/base-attrs/${id}`);
};

export const getSaleAttrs = (categoryId: number) => {
  return http.request<{ items: Array<SaleAttrItem> }>(
    "get",
    "/api/admin/products/sale-attrs",
    { params: { categoryId } }
  );
};

export const createSaleAttr = (data: {
  categoryId: number;
  name: string;
  searchable: boolean;
  sort: number;
}) => {
  return http.request<{ id: number }>(
    "post",
    "/api/admin/products/sale-attrs",
    { data }
  );
};

export const updateSaleAttr = (
  id: number,
  data: { name: string; searchable: boolean; sort: number }
) => {
  return http.request<void>("put", `/api/admin/products/sale-attrs/${id}`, {
    data
  });
};

export const deleteSaleAttr = (id: number) => {
  return http.request<void>("delete", `/api/admin/products/sale-attrs/${id}`);
};

/** 給左側分類樹畫「尚未設定屬性」提醒用。 */
export const getCategoriesWithAttrs = () => {
  return http.request<{ categoryIds: Array<number> }>(
    "get",
    "/api/admin/products/categories/with-attrs"
  );
};
