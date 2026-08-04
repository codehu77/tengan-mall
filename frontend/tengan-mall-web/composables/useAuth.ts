import { MOCK_USER } from '~/mocks/user'

export function useAuth() {
  const authStore = useAuthStore()
  const loading = ref(false)
  const error = ref('')

  async function login(loginId: string, password: string) {
    loading.value = true
    error.value = ''
    try {
      // Mock：模擬 500ms API 延遲，任意帳密都成功
      // 串接後換成：await $fetch('/api/auth/login', { method: 'POST', body: { loginId, password } })
      await new Promise(resolve => setTimeout(resolve, 500))

      if (!loginId || !password) {
        throw new Error('請填寫帳號與密碼')
      }

      authStore.setUser({
        userId: MOCK_USER.userId,
        username: loginId,
      })

      await navigateTo('/')
    } catch (e: any) {
      error.value = e.message || '登入失敗，請稍後再試'
    } finally {
      loading.value = false
    }
  }

  async function register(username: string, phone: string, password: string, code: string) {
    loading.value = true
    error.value = ''
    try {
      // Mock：模擬 500ms API 延遲
      await new Promise(resolve => setTimeout(resolve, 500))

      if (!username || !phone || !password || !code) {
        throw new Error('請填寫所有欄位')
      }

      // 驗證碼固定 1234（Mock）
      if (code !== '1234') {
        throw new Error('驗證碼錯誤，請輸入 1234（測試用）')
      }

      await navigateTo('/login')
    } catch (e: any) {
      error.value = e.message || '註冊失敗，請稍後再試'
    } finally {
      loading.value = false
    }
  }

  return { login, register, loading, error }
}
