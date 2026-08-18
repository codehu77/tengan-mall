export function useAuth() {
  const authStore = useAuthStore()
  const loading = ref(false)
  const error = ref('')

  /** 展示用：沒有接真實簡訊廠商，發送後直接把驗證碼顯示在畫面上（見 sms_scope_decision）。 */
  const smsCode = ref('')

  async function login(username: string, password: string) {
    loading.value = true
    error.value = ''
    try {
      const data = await $fetch<{ accountId: number; username: string }>('/api/auth/login', {
        method: 'POST',
        body: { username, password },
      })
      authStore.setUser({ userId: data.accountId, username: data.username })
      await useMemberStore().fetchProfile()
      await usePointsStore().loadCurrentTier()
      // 登入成功才有 access token cookie 可用，merge 要在這之後呼叫；訪客購物車併入會員後
      // 立刻刷新 header 徽章數量，不用等下一次頁面 SSR 重新查
      const newCartCount = await useCart().mergeCart()
      useCartStore().setCount(newCartCount)
      await navigateTo('/')
    } catch (e: any) {
      error.value = e.data?.message || e.statusMessage || '登入失敗，請稍後再試'
    } finally {
      loading.value = false
    }
  }

  async function sendSmsCode(phone: string) {
    error.value = ''
    if (!phone) {
      error.value = '請先輸入手機號碼'
      return false
    }
    try {
      const data = await $fetch<{ code: string }>('/api/auth/sms-send', {
        method: 'POST',
        body: { phone },
      })
      smsCode.value = data.code
      return true
    } catch (e: any) {
      error.value = e.data?.message || e.statusMessage || '驗證碼發送失敗'
      return false
    }
  }

  async function register(username: string, phone: string, password: string, code: string) {
    loading.value = true
    error.value = ''
    try {
      await $fetch('/api/auth/register', {
        method: 'POST',
        body: { username, phone, password, code },
      })
      await navigateTo('/login')
    } catch (e: any) {
      error.value = e.data?.message || e.statusMessage || '註冊失敗，請稍後再試'
    } finally {
      loading.value = false
    }
  }

  return { login, register, sendSmsCode, smsCode, loading, error }
}
