import { http } from "@/utils/http";

/** 對齊 tengan-admin AdminRoleController 的回應形狀，backend 直接回傳資料物件，不包 {success,data}。 */
export type RoleItem = {
  id: number;
  roleCode: string;
  roleName: string;
  status: number;
};

export type RoleListResult = {
  items: Array<RoleItem>;
};

export type RoleDetailResult = {
  id: number;
  roleCode: string;
  roleName: string;
  status: number;
  menuIds: Array<number>;
};

/** 角色列表（資料量小，不分頁）。 */
export const getRoleList = () => {
  return http.request<RoleListResult>("get", "/api/admin/system/roles");
};

/** 角色詳情，主要拿目前授權的 menuIds 給選單授權對話框預先勾選用。 */
export const getRoleDetail = (id: number) => {
  return http.request<RoleDetailResult>(
    "get",
    `/api/admin/system/roles/${id}`
  );
};

export const createRole = (data: { roleCode: string; roleName: string }) => {
  return http.request<{ id: number }>("post", "/api/admin/system/roles", {
    data
  });
};

export const updateRole = (
  id: number,
  data: { roleName: string; active: boolean }
) => {
  return http.request<void>("put", `/api/admin/system/roles/${id}`, {
    data
  });
};

export const assignRoleMenus = (id: number, menuIds: Array<number>) => {
  return http.request<void>("put", `/api/admin/system/roles/${id}/menus`, {
    data: { menuIds }
  });
};
