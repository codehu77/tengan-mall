import { http } from "@/utils/http";

/** 對齊 tengan-admin 的 GET /api/admin/auth/menus，backend 直接回傳陣列，不包 {success,data}。 */
export const getAsyncRoutes = () => {
  return http.request<Array<any>>("get", "/api/admin/auth/menus");
};
