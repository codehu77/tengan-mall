import { http } from "@/utils/http";

/** 對齊 tengan-admin AdminUserController 的回應形狀，backend 直接回傳資料物件，不包 {success,data}。 */
export type AdminUserItem = {
  id: number;
  username: string;
  realName: string;
  status: number;
};

export type AdminUserListResult = {
  items: Array<AdminUserItem>;
  total: number;
};

export type AdminUserDetailResult = {
  id: number;
  username: string;
  realName: string;
  status: number;
  roleIds: Array<number>;
};

export const getAdminUserList = (params: {
  pageNum: number;
  pageSize: number;
}) => {
  return http.request<AdminUserListResult>(
    "get",
    "/api/admin/system/users",
    { params }
  );
};

/** 拿目前已指派的 roleIds，給指派角色對話框預先勾選用。 */
export const getAdminUserDetail = (id: number) => {
  return http.request<AdminUserDetailResult>(
    "get",
    `/api/admin/system/users/${id}`
  );
};

export const createAdminUser = (data: {
  username: string;
  password: string;
  realName: string;
}) => {
  return http.request<{ id: number }>("post", "/api/admin/system/users", {
    data
  });
};

export const updateAdminUserStatus = (id: number, active: boolean) => {
  return http.request<void>(
    "put",
    `/api/admin/system/users/${id}/status`,
    { data: { active } }
  );
};

export const assignAdminUserRoles = (id: number, roleIds: Array<number>) => {
  return http.request<void>("put", `/api/admin/system/users/${id}/roles`, {
    data: { roleIds }
  });
};
