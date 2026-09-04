/**
 * LINE Login 沒有官方 JS SDK 可以像 Google GIS/Facebook JS SDK 那樣在頁面內直接拿到已簽章的憑證，
 * 只支援整頁導轉的 Authorization Code flow：先跟 server/api/auth/oauth2/line-start.get.ts 拿組好
 * 的授權 URL（state/nonce 已經種進 HttpOnly cookie，見該檔案說明），整頁導去 LINE 授權頁，同意後
 * LINE 會把瀏覽器導回 pages/auth/line/callback.vue（route 是 /auth/line/callback，要跟
 * redirect-uri 設定完全一致，這裡務必是巢狀資料夾 auth/line/callback.vue，不能用
 * auth/line-callback.vue 這種連字號檔名——Nuxt file-based routing 兩者對應到不同 route）。
 */
export function useLineIdentity() {
  async function login(mode: 'login' | 'link' = 'login') {
    const { url } = await $fetch<{ url: string }>('/api/auth/oauth2/line-start', { query: { mode } })
    // 回跳頁只是拿來決定「呼叫哪支 API、成功後導去哪裡」的 UI 提示，不是安全機制——真正的 CSRF
    // 防護在後端 cookie 比對（見 server/utils/verifyLineOAuthState.ts）。
    sessionStorage.setItem('line_oauth_mode', mode)
    window.location.href = url
  }

  return { login }
}
