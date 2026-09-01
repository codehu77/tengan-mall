import { defineStore } from 'pinia'

interface UserInfo {
  userId: number
  phone: string | null
  email: string | null
}

/**
 * 真實身分來源是 HttpOnly cookie（前端 JS 讀不到），這個 store 只是登入狀態的快取——
 * 不再用 localStorage 存使用者資訊，改成 fetchMe() 打 /api/auth/me 問後端「這顆 cookie 是誰」。
 */
export const useAuthStore = defineStore('auth', () => {
  const user = ref<UserInfo | null>(null)

  const isLoggedIn = computed(() => user.value !== null)
  /** header/sidebar 暱稱抓不到時的顯示 fallback，account 不再有 username，改用 phone/email 二選一。 */
  const identifier = computed(() => user.value?.phone ?? user.value?.email ?? '')

  async function fetchMe() {
    const requestFetch = useRequestFetch()
    try {
      const data = await requestFetch<{ accountId: number; phone: string | null; email: string | null }>(
        '/api/auth/me',
      )
      user.value = { userId: data.accountId, phone: data.phone, email: data.email }
    } catch {
      user.value = null
    }
  }

  function setUser(info: UserInfo) {
    user.value = info
  }

  /** access token 過期等情境下，後端已經不認這個身分了，只清本地快取，不再打一次 /api/auth/logout。 */
  function clearSession() {
    user.value = null
    usePointsStore().clearCurrentTier()
  }

  async function logout() {
    await $fetch('/api/auth/logout', { method: 'POST' }).catch(() => {})
    user.value = null
    useMemberStore().clear()
    usePointsStore().clearCurrentTier()
    navigateTo('/login')
  }

  return { user, isLoggedIn, identifier, setUser, clearSession, fetchMe, logout }
})
