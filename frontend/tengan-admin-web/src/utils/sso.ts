import { removeToken, setToken, type DataInfo } from "./auth";
import { subBefore, getQueryMap } from "@pureadmin/utils";

/**
 * 簡版前端單點登入，根據實際業務自行編寫，平臺啟動後本地可以跳後面這個連結進行測試 http://localhost:8090/#/permission/page/index?username=sso&roles=admin&accessToken=eyJhbGciOiJIUzUxMiJ9.admin
 * 劃重點：
 * 判斷是否為單點登入，不為則直接返回不再進行任何邏輯處理，下面是單點登入後的邏輯處理
 * 1.清空本地舊資訊；
 * 2.獲取url中的重要引數資訊，然後通過 setToken 儲存在本地；
 * 3.刪除不需要顯示在 url 的引數
 * 4.使用 window.location.replace 跳轉正確頁面
 */
(function () {
  // 獲取 url 中的引數
  const params = getQueryMap(location.href) as DataInfo<Date>;
  const must = ["username", "roles", "accessToken"];
  const mustLength = must.length;
  if (Object.keys(params).length !== mustLength) return;

  // url 引數滿足 must 裡的全部值，才判定為單點登入，避免非單點登入時重新整理頁面無限迴圈
  let sso = [];
  let start = 0;

  while (start < mustLength) {
    if (Object.keys(params).includes(must[start]) && sso.length <= mustLength) {
      sso.push(must[start]);
    } else {
      sso = [];
    }
    start++;
  }

  if (sso.length === mustLength) {
    // 判定為單點登入

    // 清空本地舊資訊
    removeToken();

    // 儲存新資訊到本地
    setToken(params);

    // 刪除不需要顯示在 url 的引數
    delete params.roles;
    delete params.accessToken;

    const newUrl = `${location.origin}${location.pathname}${subBefore(
      location.hash,
      "?"
    )}?${JSON.stringify(params)
      .replace(/["{}]/g, "")
      .replace(/:/g, "=")
      .replace(/,/g, "&")}`;

    // 替換歷史記錄項
    window.location.replace(newUrl);
  } else {
    return;
  }
})();
