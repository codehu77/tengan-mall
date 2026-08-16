/**
 * 攔截所有 /api/** 呼叫的 401——access token 過期時後端會回 401，但各頁面各自的
 * try/catch 只會顯示「XX失敗：401 Unauthorized」這種對使用者沒意義的錯誤，應該要
 * 清掉登入狀態、導回登入頁並提示「登入已逾期」。globalThis.$fetch 是 Nuxt 底層 ofetch
 * 的全域實例，useRequestFetch()（client 端）跟裸用的 $fetch 都會走同一顆，覆寫這裡
 * 一處就能攔到全站，不用每個 composable 各自處理。
 *
 * 排除清單：這些端點 401 是正常的業務語意（未登入/登入失敗/自我檢查），不是「session 中途過期」，
 * 不該觸發跳轉，否則登入頁本身、fetchMe() 的初始檢查都會被誤判成「登入已逾期」。
 */
const EXEMPT_PATHS = ['/api/auth/login', '/api/auth/logout', '/api/auth/register', '/api/auth/sms-send', '/api/auth/me']

export default defineNuxtPlugin(() => {
  globalThis.$fetch = $fetch.create({
    async onResponseError({ request, response }) {
      if (response.status !== 401) return

      const url = typeof request === 'string' ? request : request.url
      if (!url.includes('/api/') || EXEMPT_PATHS.some((p) => url.includes(p))) return

      const authStore = useAuthStore()
      if (!authStore.isLoggedIn) return // 同批並發請求只處理一次，避免重複導向/重複提示

      authStore.clearSession()
      useMemberStore().clear()
      useToast().add({ title: '登入已逾期，請重新登入', color: 'orange', timeout: 4000 })
      await navigateTo('/login')
    },
  })
})
