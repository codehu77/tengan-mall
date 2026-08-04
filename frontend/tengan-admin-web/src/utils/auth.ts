import Cookies from "js-cookie";
import { useUserStoreHook } from "@/store/modules/user";
import { storageLocal, isString, isIncludeAllChildren } from "@pureadmin/utils";

export interface DataInfo<T> {
  /** token */
  accessToken: string;
  /** `accessToken`的過期時間（時間戳） */
  expires: T;
  /** 用於呼叫重新整理accessToken的介面時所需的token */
  refreshToken: string;
  /** 頭像 */
  avatar?: string;
  /** 使用者名稱 */
  username?: string;
  /** 暱稱 */
  nickname?: string;
  /** 當前登入使用者的角色 */
  roles?: Array<string>;
  /** 當前登入使用者的按鈕級別許可權 */
  permissions?: Array<string>;
}

export const userKey = "user-info";
export const TokenKey = "authorized-token";
/**
 * 通過`multiple-tabs`是否在`cookie`中，判斷使用者是否已經登入系統，
 * 從而支援多標籤頁開啟已經登入的系統後無需再登入。
 * 瀏覽器完全關閉後`multiple-tabs`將自動從`cookie`中銷燬，
 * 再次開啟瀏覽器需要重新登入系統
 * */
export const multipleTabsKey = "multiple-tabs";

/** 獲取`token` */
export function getToken(): DataInfo<number> {
  // 此處與`TokenKey`相同，此寫法解決初始化時`Cookies`中不存在`TokenKey`報錯
  return Cookies.get(TokenKey)
    ? JSON.parse(Cookies.get(TokenKey))
    : storageLocal().getItem(userKey);
}

/**
 * @description 設定`token`以及一些必要資訊並採用無感重新整理`token`方案
 * 無感重新整理：後端返回`accessToken`（訪問介面使用的`token`）、`refreshToken`（用於呼叫重新整理`accessToken`的介面時所需的`token`，`refreshToken`的過期時間（比如30天）應大於`accessToken`的過期時間（比如2小時））、`expires`（`accessToken`的過期時間）
 * 將`accessToken`、`expires`、`refreshToken`這三條資訊放在key值為authorized-token的cookie裡（過期自動銷燬）
 * 將`avatar`、`username`、`nickname`、`roles`、`permissions`、`refreshToken`、`expires`這七條資訊放在key值為`user-info`的localStorage裡（利用`multipleTabsKey`當瀏覽器完全關閉後自動銷燬）
 */
export function setToken(data: DataInfo<Date>) {
  let expires = 0;
  const { accessToken, refreshToken } = data;
  const { isRemembered, loginDay } = useUserStoreHook();
  expires = new Date(data.expires).getTime(); // 如果後端直接設定時間戳，將此處程式碼改為expires = data.expires，然後把上面的DataInfo<Date>改成DataInfo<number>即可
  const cookieString = JSON.stringify({ accessToken, expires, refreshToken });

  expires > 0
    ? Cookies.set(TokenKey, cookieString, {
        expires: (expires - Date.now()) / 86400000
      })
    : Cookies.set(TokenKey, cookieString);

  Cookies.set(
    multipleTabsKey,
    "true",
    isRemembered
      ? {
          expires: loginDay
        }
      : {}
  );

  function setUserKey({ avatar, username, nickname, roles, permissions }) {
    useUserStoreHook().SET_AVATAR(avatar);
    useUserStoreHook().SET_USERNAME(username);
    useUserStoreHook().SET_NICKNAME(nickname);
    useUserStoreHook().SET_ROLES(roles);
    useUserStoreHook().SET_PERMS(permissions);
    storageLocal().setItem(userKey, {
      refreshToken,
      expires,
      avatar,
      username,
      nickname,
      roles,
      permissions
    });
  }

  if (data.username && data.roles) {
    const { username, roles } = data;
    setUserKey({
      avatar: data?.avatar ?? "",
      username,
      nickname: data?.nickname ?? "",
      roles,
      permissions: data?.permissions ?? []
    });
  } else {
    const avatar =
      storageLocal().getItem<DataInfo<number>>(userKey)?.avatar ?? "";
    const username =
      storageLocal().getItem<DataInfo<number>>(userKey)?.username ?? "";
    const nickname =
      storageLocal().getItem<DataInfo<number>>(userKey)?.nickname ?? "";
    const roles =
      storageLocal().getItem<DataInfo<number>>(userKey)?.roles ?? [];
    const permissions =
      storageLocal().getItem<DataInfo<number>>(userKey)?.permissions ?? [];
    setUserKey({
      avatar,
      username,
      nickname,
      roles,
      permissions
    });
  }
}

/** 刪除`token`以及key值為`user-info`的localStorage資訊 */
export function removeToken() {
  Cookies.remove(TokenKey);
  Cookies.remove(multipleTabsKey);
  storageLocal().removeItem(userKey);
}

/** 格式化token（jwt格式） */
export const formatToken = (token: string): string => {
  return "Bearer " + token;
};

/** 是否有按鈕級別的許可權（根據登入介面返回的`permissions`欄位進行判斷）*/
export const hasPerms = (value: string | Array<string>): boolean => {
  if (!value) return false;
  const allPerms = "*:*:*";
  const { permissions } = useUserStoreHook();
  if (!permissions) return false;
  if (permissions.length === 1 && permissions[0] === allPerms) return true;
  const isAuths = isString(value)
    ? permissions.includes(value)
    : isIncludeAllChildren(value, permissions);
  return isAuths ? true : false;
};
