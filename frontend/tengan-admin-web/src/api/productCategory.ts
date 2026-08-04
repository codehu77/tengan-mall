import { http } from "@/utils/http";

/** 對齊 tengan-admin ProductCategoryController 的回應形狀（BFF 轉發 tengan-product）。 */
export type CategoryTreeItem = {
  id: number;
  name: string;
  icon?: string;
  sort: number;
  status: number;
  children?: Array<CategoryTreeItem>;
};

export type CategoryTreeResult = {
  items: Array<CategoryTreeItem>;
};

/** 分類樹（資料量小，不分頁）。 */
export const getCategoryTree = () => {
  return http.request<CategoryTreeResult>(
    "get",
    "/api/admin/products/categories"
  );
};

export type CategoryFormData = {
  parentId: number | null;
  name: string;
  icon?: string;
  sort: number;
};

export const createCategory = (data: CategoryFormData) => {
  return http.request<{ id: number }>(
    "post",
    "/api/admin/products/categories",
    { data }
  );
};

/** level 由後端依 parentId 自動推算，parentId 建立後不可改，編輯只送其餘欄位。 */
export const updateCategory = (
  id: number,
  data: Omit<CategoryFormData, "parentId">
) => {
  return http.request<void>(
    "put",
    `/api/admin/products/categories/${id}`,
    { data }
  );
};

/** 底下還有子分類時後端會回 409，呼叫端要接住錯誤訊息。 */
export const deleteCategory = (id: number) => {
  return http.request<void>("delete", `/api/admin/products/categories/${id}`);
};

export const showCategory = (id: number) => {
  return http.request<void>(
    "put",
    `/api/admin/products/categories/${id}/show`
  );
};

export const hideCategory = (id: number) => {
  return http.request<void>(
    "put",
    `/api/admin/products/categories/${id}/hide`
  );
};
