export default defineNuxtConfig({
  devtools: { enabled: true },

  css: ['~/assets/css/main.css'],

  modules: [
    '@nuxt/ui',
    '@pinia/nuxt',
  ],

  // 混合渲染：有 SEO 需求的頁面用 SSR，會員相關頁面用 SPA
  // /api/public/** proxy 到 Gateway:88——nitro.devProxy 在這個 Nitro 版本實測不會生效（SSR fetch
  // 直接 404，devProxy 的 h3 middleware 沒有真的攔到請求），改用 routeRules 的 proxy（Nitro 官方
  // 現行機制，dev/build 都適用）。path 用 /** 結尾會把前綴從來源路徑砍掉，所以 target 要手動補回
  // 完整前綴，不然 Gateway 收到的路徑比對不到任何路由規則。
  //
  // 刻意只轉發 /api/public/**，不是泛用的 /api/**——原本用 /api/** 一次踩到兩個坑：自己寫的
  // 登入/註冊 BFF 端點（server/api/auth/**）、還有 @nuxt/icon 內部的 /api/_nuxt_icon 端點都被
  // 這條規則整包吃掉、完全沒機會被真正的 handler 執行到（routeRules 對同一路徑多條規則疊加時，
  // 更明確的路徑覆蓋不了 proxy 這個屬性，試過 proxy:false 也一樣被吞掉，只能整條規則收窄）。
  // 收窄成 /api/public/** 不只是繞開這兩個坑，是這個架構本來就該有的邊界：/api/customer/**、
  // /api/admin/** 這種需要驗證身份的呼叫，本來就不該走「單純轉發」——cookie 換不成
  // Authorization header，只有 server/api/auth/** 這種手寫的 BFF 端點做得到，所以永遠不該把它們
  // 也塞進這條 proxy 規則。BFF 端點（/api/auth/**）跟這條 proxy 規則（/api/public/**）現在都在
  // /api/** 底下也沒關係——兩個 pattern 的下一層路徑段（auth vs public）本來就不重疊，不會有
  // 同一個路徑被兩條規則同時匹配到的情況，不用擔心會重演 /api/_nuxt_icon 那次的坑。
  routeRules: {
    '/':         { ssr: true },
    '/search':   { ssr: true },
    '/item/**':  { ssr: true },
    '/cart/**':  { ssr: false },
    '/order/**': { ssr: false },
    '/member/**': { ssr: false },
    '/login':    { ssr: false },
    '/register': { ssr: false },
    '/seckill/**': { ssr: false },
    '/api/public/**': { proxy: 'http://localhost:88/api/public/**' },
  },

  // 環境變數
  runtimeConfig: {
    cookieName: 'mall_token',
    refreshCookieName: 'mall_refresh_token',
    public: {
      apiBase: 'http://localhost:88',
    },
  },
})
