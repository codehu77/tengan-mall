/**
 * 包裝 Facebook JS SDK 的登入流程——動態載入官方 script、初始化，`FB.login()` 拿到的 access token
 * 就是要送給後端驗證的憑證（後端打 Graph API 驗證+正規化，見 FacebookOAuthProviderVerifier）。
 * Facebook 沒有像 Google GIS 那樣可以自動渲染進任意 DOM 節點的官方按鈕元件，這裡沿用專案自己畫的
 * 按鈕（見 login.vue/security.vue），呼叫端只需要在 click handler 觸發這裡的 login()。
 */
let scriptLoadingPromise: Promise<void> | null = null

function loadFacebookScript(appId: string): Promise<void> {
  if (window.FB) {
    return Promise.resolve()
  }
  if (scriptLoadingPromise) {
    return scriptLoadingPromise
  }

  scriptLoadingPromise = new Promise((resolve, reject) => {
    window.fbAsyncInit = () => {
      window.FB!.init({ appId, cookie: false, xfbml: false, version: 'v26.0' })
      resolve()
    }
    const script = document.createElement('script')
    script.src = 'https://connect.facebook.net/zh_TW/sdk.js'
    script.async = true
    script.defer = true
    script.onerror = () => reject(new Error('Facebook SDK 載入失敗'))
    document.head.appendChild(script)
  })
  return scriptLoadingPromise
}

export function useFacebookIdentity() {
  const config = useRuntimeConfig()

  async function login(onAccessToken: (accessToken: string) => void, onCancel?: () => void) {
    await loadFacebookScript(config.public.facebookAppId)
    if (!window.FB) {
      throw new Error('Facebook SDK 尚未就緒')
    }
    window.FB.login((response) => {
      if (response.authResponse?.accessToken) {
        onAccessToken(response.authResponse.accessToken)
      } else {
        onCancel?.()
      }
    }, { scope: 'public_profile,email' })
  }

  return { login }
}
