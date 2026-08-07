/**
 * App 啟動時（SSR 首次請求 or 純 client 初次載入）問一次「這顆 cookie 是誰」，讓 AppHeader
 * 等處第一次渲染就能正確反映登入狀態，不用等使用者觸發任何動作才知道。
 */
export default defineNuxtPlugin(async () => {
  const authStore = useAuthStore()
  await authStore.fetchMe()
})
