export default defineNuxtConfig({
  devtools: { enabled: true },

  css: ['~/assets/css/main.css'],

  modules: [
    '@nuxt/ui',
    '@pinia/nuxt',
  ],

  // 混合渲染：有 SEO 需求的頁面用 SSR，會員相關頁面用 SPA
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
  },

  // 開發環境 API proxy：/api/* → Gateway:88
  nitro: {
    devProxy: {
      '/api': {
        target: 'http://localhost:88',
        changeOrigin: true,
      },
    },
  },

  // 環境變數
  runtimeConfig: {
    cookieName: 'mall_token',
    public: {
      apiBase: 'http://localhost:88',
    },
  },
})
