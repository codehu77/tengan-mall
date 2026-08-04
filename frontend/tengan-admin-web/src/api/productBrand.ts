import { http } from "@/utils/http";

/** 對齊 tengan-admin ProductBrandController 的回應形狀（BFF 轉發 tengan-product）。 */
export type BrandItem = {
  id: number;
  name: string;
  logo?: string;
  descript?: string;
  firstLetter?: string;
  sort: number;
  status: number;
};

export type BrandListResult = {
  items: Array<BrandItem>;
};

/** 品牌列表（資料量小，不分頁）。 */
export const getBrandList = () => {
  return http.request<BrandListResult>("get", "/api/admin/products/brands");
};

export type BrandFormData = {
  name: string;
  logo?: string;
  descript?: string;
  firstLetter?: string;
  sort: number;
};

export const createBrand = (data: BrandFormData) => {
  return http.request<{ id: number }>("post", "/api/admin/products/brands", {
    data
  });
};

export const updateBrand = (id: number, data: BrandFormData) => {
  return http.request<void>("put", `/api/admin/products/brands/${id}`, {
    data
  });
};

export const deleteBrand = (id: number) => {
  return http.request<void>("delete", `/api/admin/products/brands/${id}`);
};

export const showBrand = (id: number) => {
  return http.request<void>("put", `/api/admin/products/brands/${id}/show`);
};

export const hideBrand = (id: number) => {
  return http.request<void>("put", `/api/admin/products/brands/${id}/hide`);
};
