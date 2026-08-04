import { defineStore } from "pinia";
import {
  type userType,
  store,
  router,
  resetRouter,
  routerArrays,
  storageLocal
} from "../utils";
import {
  type UserResult,
  type RefreshTokenResult,
  getLogin,
  refreshTokenApi,
  logoutApi
} from "@/api/user";
import { useMultiTagsStoreHook } from "./multiTags";
import {
  type DataInfo,
  setToken,
  removeToken,
  userKey,
  getToken
} from "@/utils/auth";

export const useUserStore = defineStore("pure-user", {
  state: (): userType => ({
    // 頭像
    avatar: storageLocal().getItem<DataInfo<number>>(userKey)?.avatar ?? "",
    // 使用者名稱
    username: storageLocal().getItem<DataInfo<number>>(userKey)?.username ?? "",
    // 暱稱
    nickname: storageLocal().getItem<DataInfo<number>>(userKey)?.nickname ?? "",
    // 頁面級別許可權
    roles: storageLocal().getItem<DataInfo<number>>(userKey)?.roles ?? [],
    // 按鈕級別許可權
    permissions:
      storageLocal().getItem<DataInfo<number>>(userKey)?.permissions ?? [],
    // 是否勾選了登入頁的免登入
    isRemembered: false,
    // 登入頁的免登入儲存幾天，預設7天
    loginDay: 7
  }),
  actions: {
    /** 儲存頭像 */
    SET_AVATAR(avatar: string) {
      this.avatar = avatar;
    },
    /** 儲存使用者名稱 */
    SET_USERNAME(username: string) {
      this.username = username;
    },
    /** 儲存暱稱 */
    SET_NICKNAME(nickname: string) {
      this.nickname = nickname;
    },
    /** 儲存角色 */
    SET_ROLES(roles: Array<string>) {
      this.roles = roles;
    },
    /** 儲存按鈕級別許可權 */
    SET_PERMS(permissions: Array<string>) {
      this.permissions = permissions;
    },
    /** 儲存是否勾選了登入頁的免登入 */
    SET_ISREMEMBERED(bool: boolean) {
      this.isRemembered = bool;
    },
    /** 設定登入頁的免登入儲存幾天 */
    SET_LOGINDAY(value: number) {
      this.loginDay = Number(value);
    },
    /** 登入 */
    async loginByUsername(data) {
      return new Promise<UserResult>((resolve, reject) => {
        getLogin(data)
          .then(result => {
            // 後端沒有回傳 expires（access token TTL 固定 15 分鐘，見
            // AdminAccessTokenIssuerAdapter），這裡自己算，只給前端「該不該提前換發」用。
            setToken({
              accessToken: result.accessToken,
              refreshToken: result.refreshToken,
              expires: new Date(Date.now() + 15 * 60 * 1000),
              avatar: "",
              username: result.username,
              nickname: result.realName || result.username,
              roles: result.roleCodes,
              permissions: result.permissions
            });
            resolve(result);
          })
          .catch(error => {
            reject(error);
          });
      });
    },
    /**
     * 登出：先呼叫後端撤銷 refresh token（tengan-admin 的 refresh token 是有狀態的，
     * 存在 Redis，不主動撤銷的話 7 天內都還能換發新 access token，見 AdminRefreshTokenStoreAdapter）。
     * 後端呼叫失敗也不擋前端登出，best-effort。
     */
    logOut() {
      const refreshToken = getToken()?.refreshToken;
      if (refreshToken) {
        logoutApi({ refreshToken }).catch(() => {});
      }
      this.username = "";
      this.roles = [];
      this.permissions = [];
      removeToken();
      useMultiTagsStoreHook().handleTags("equal", [...routerArrays]);
      resetRouter();
      router.push("/login");
    },
    /** 重新整理`token` */
    async handRefreshToken(data) {
      return new Promise<RefreshTokenResult>((resolve, reject) => {
        refreshTokenApi(data)
          .then(result => {
            setToken({
              accessToken: result.accessToken,
              refreshToken: result.refreshToken,
              expires: new Date(Date.now() + 15 * 60 * 1000)
            });
            resolve(result);
          })
          .catch(error => {
            reject(error);
          });
      });
    }
  }
});

export function useUserStoreHook() {
  return useUserStore(store);
}
