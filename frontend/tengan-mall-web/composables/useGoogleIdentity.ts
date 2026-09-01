/**
 * 包裝 Google Identity Services (GIS) 的 ID Token 登入流程——動態載入官方 script、初始化、
 * 把渲染出來的按鈕掛進呼叫端傳入的 DOM 節點，callback 拿到的 credential 就是要送給後端驗證的
 * ID Token（JWT）。不用完整 oauth2Login redirect 流程，前端直接拿到簽好章的 token。
 */
let scriptLoadingPromise: Promise<void> | null = null

function loadGoogleScript(): Promise<void> {
  if (window.google?.accounts?.id) {
    return Promise.resolve()
  }
  if (scriptLoadingPromise) {
    return scriptLoadingPromise
  }

  scriptLoadingPromise = new Promise((resolve, reject) => {
    const script = document.createElement('script')
    script.src = 'https://accounts.google.com/gsi/client'
    script.async = true
    script.defer = true
    script.onload = () => resolve()
    script.onerror = () => reject(new Error('Google Identity Services 載入失敗'))
    document.head.appendChild(script)
  })
  return scriptLoadingPromise
}

export function useGoogleIdentity() {
  const config = useRuntimeConfig()

  async function renderButton(el: HTMLElement, onCredential: (idToken: string) => void) {
    await loadGoogleScript()
    if (!window.google?.accounts?.id) {
      throw new Error('Google Identity Services 尚未就緒')
    }
    window.google.accounts.id.initialize({
      client_id: config.public.googleClientId,
      callback: (response) => onCredential(response.credential),
    })
    window.google.accounts.id.renderButton(el, {
      theme: 'outline',
      size: 'large',
      width: el.clientWidth || 300,
    })
  }

  return { renderButton }
}
