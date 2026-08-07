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
      const data = await $fetch<{ accountId: number; username: string }>('/bff/auth/login', {
        method: 'POST',
        body: { username, password },
      })
      authStore.setUser({ userId: data.accountId, username: data.username })
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
      const data = await $fetch<{ code: string }>('/bff/auth/sms-send', {
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
      await $fetch('/bff/auth/register', {
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
