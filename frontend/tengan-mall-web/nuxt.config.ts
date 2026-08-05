export default defineNuxtConfig({
  devtools: { enabled: true },

  css: ['~/assets/css/main.css'],

  modules: [
    '@nuxt/ui',
    '@pinia/nuxt',
  ],

  // 混合渲染：有 SEO 需求的頁面用 SSR，會員相關頁面用 SPA
  // /api/** proxy 到 Gateway:88——nitro.devProxy 在這個 Nitro 版本實測不會生效（SSR fetch 直接
  // 404，devProxy 的 h3 middleware 沒有真的攔到請求），改用 routeRules 的 proxy（Nitro 官方現行
  // 機制，dev/build 都適用）。path 用 /** 結尾會把 /api 前綴從來源路徑砍掉，所以 target 要手動補回
  // /api，不然 Gateway 收到的會是少了 /api 前綴的路徑，比對不到任何路由規則。
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
    '/api/**':   { proxy: 'http://localhost:88/api/**' },
  },

  // 環境變數
  runtimeConfig: {
    cookieName: 'mall_token',
    public: {
      apiBase: 'http://localhost:88',
    },
  },
})
