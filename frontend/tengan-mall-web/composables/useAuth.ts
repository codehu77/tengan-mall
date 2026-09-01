export function useAuth() {
  const authStore = useAuthStore()
  const loading = ref(false)
  const error = ref('')

  /** 展示用：沒有接真實簡訊/郵件廠商，發送後直接把驗證碼顯示在畫面上（見 sms_scope_decision）。 */
  const otpCode = ref('')

  async function login(identifier: string, password: string, rememberMe: boolean) {
    loading.value = true
    error.value = ''
    try {
      const data = await $fetch<{ accountId: number }>('/api/auth/login', {
        method: 'POST',
        body: { identifier, password, rememberMe },
      })
      authStore.setUser({ userId: data.accountId, phone: null, email: null })
      await authStore.fetchMe()
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

  /** 三步驟註冊 Step 1：identifier 是手機或 Email，後端依是否含 @ 判斷發送管道。 */
  async function startRegister(identifier: string) {
    error.value = ''
    if (!identifier) {
      error.value = '請先輸入手機號碼或 Email'
      return false
    }
    try {
      const data = await $fetch<{ code: string }>('/api/auth/register/start', {
        method: 'POST',
        body: { identifier },
      })
      otpCode.value = data.code
      return true
    } catch (e: any) {
      error.value = e.data?.message || e.statusMessage || '驗證碼發送失敗'
      return false
    }
  }

  /** Step 2：核對驗證碼，成功回傳 registrationToken 供 Step 3 使用。 */
  async function verifyRegister(identifier: string, code: string): Promise<string | null> {
    error.value = ''
    try {
      const data = await $fetch<{ registrationToken: string }>('/api/auth/register/verify', {
        method: 'POST',
        body: { identifier, code },
      })
      return data.registrationToken
    } catch (e: any) {
      error.value = e.data?.message || e.statusMessage || '驗證碼錯誤或已過期'
      return null
    }
  }

  /** Step 3：設定密碼完成註冊，後端已 auto-login 並設好 cookie，這裡只需要補齊前端狀態。 */
  async function completeRegister(registrationToken: string, password: string) {
    loading.value = true
    error.value = ''
    try {
      const data = await $fetch<{ accountId: number }>('/api/auth/register/complete', {
        method: 'POST',
        body: { registrationToken, password },
      })
      authStore.setUser({ userId: data.accountId, phone: null, email: null })
      await authStore.fetchMe()
      await useMemberStore().fetchProfile()
      await usePointsStore().loadCurrentTier()
      const newCartCount = await useCart().mergeCart()
      useCartStore().setCount(newCartCount)
      await navigateTo('/')
      return true
    } catch (e: any) {
      error.value = e.data?.message || e.statusMessage || '註冊失敗，請稍後再試'
      return false
    } finally {
      loading.value = false
    }
  }

  /** 忘記密碼 Step 1：不管帳號是否存在都回同一句訊息，前端統一顯示，不用另外判斷。 */
  async function forgotPassword(identifier: string) {
    error.value = ''
    try {
      await $fetch('/api/auth/password/forgot', { method: 'POST', body: { identifier } })
      return true
    } catch (e: any) {
      error.value = e.data?.message || e.statusMessage || '發送失敗，請稍後再試'
      return false
    }
  }

  /** 忘記密碼 Step 2：核對驗證碼，成功回傳 resetToken 供 Step 3 使用。 */
  async function verifyForgotPassword(identifier: string, code: string): Promise<string | null> {
    error.value = ''
    try {
      const data = await $fetch<{ resetToken: string }>('/api/auth/password/forgot-verify', {
        method: 'POST',
        body: { identifier, code },
      })
      return data.resetToken
    } catch (e: any) {
      error.value = e.data?.message || e.statusMessage || '驗證碼錯誤或已過期'
      return null
    }
  }

  /** 忘記密碼 Step 3：重設密碼完成，後端已 auto-login 並設好 cookie。 */
  async function resetPassword(resetToken: string, newPassword: string) {
    loading.value = true
    error.value = ''
    try {
      const data = await $fetch<{ accountId: number }>('/api/auth/password/reset', {
        method: 'POST',
        body: { resetToken, newPassword },
      })
      authStore.setUser({ userId: data.accountId, phone: null, email: null })
      await authStore.fetchMe()
      await useMemberStore().fetchProfile()
      await usePointsStore().loadCurrentTier()
      const newCartCount = await useCart().mergeCart()
      useCartStore().setCount(newCartCount)
      await navigateTo('/')
      return true
    } catch (e: any) {
      error.value = e.data?.message || e.statusMessage || '重設密碼失敗，請稍後再試'
      return false
    } finally {
      loading.value = false
    }
  }

  return {
    login,
    startRegister,
    verifyRegister,
    completeRegister,
    forgotPassword,
    verifyForgotPassword,
    resetPassword,
    otpCode,
    loading,
    error,
  }
}
