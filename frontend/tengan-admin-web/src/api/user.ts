import { http } from "@/utils/http";

/**
 * 對齊 tengan-admin 的 LoginResponse（backend 直接回傳資料物件，不包 {success,data}，
 * 跟 tengan-auth 的 API 風格一致，見 docs/ddd-standards.md 之外的協作偏好）。
 */
export type UserResult = {
  /** `token` */
  accessToken: string;
  /** 用於呼叫重新整理`accessToken`的介面時所需的`token` */
  refreshToken: string;
  /** 管理員 id */
  adminId: number;
  /** 使用者名稱 */
  username: string;
  /** 真實姓名 */
  realName: string;
  /** 當前登入使用者的角色代碼 */
  roleCodes: Array<string>;
  /** colon 式 RBAC 權限碼（system:user:write 這種），對應按鈕級別許可權 */
  permissions: Array<string>;
};

export type RefreshTokenResult = {
  /** `token` */
  accessToken: string;
  /** 用於呼叫重新整理`accessToken`的介面時所需的`token` */
  refreshToken: string;
};

/** 登入 */
export const getLogin = (data?: object) => {
  return http.request<UserResult>("post", "/api/admin/auth/login", { data });
};

/** 重新整理`token` */
export const refreshTokenApi = (data?: object) => {
  return http.request<RefreshTokenResult>("post", "/api/admin/auth/refresh", {
    data
  });
};

/** 登出 */
export const logoutApi = (data?: object) => {
  return http.request<void>("post", "/api/admin/auth/logout", { data });
};
