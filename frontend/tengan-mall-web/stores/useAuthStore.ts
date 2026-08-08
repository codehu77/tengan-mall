import { defineStore } from 'pinia'

interface UserInfo {
  userId: number
  username: string
}

/**
 * 真實身分來源是 HttpOnly cookie（前端 JS 讀不到），這個 store 只是登入狀態的快取——
 * 不再用 localStorage 存使用者資訊，改成 fetchMe() 打 /api/auth/me 問後端「這顆 cookie 是誰」。
 */
export const useAuthStore = defineStore('auth', () => {
  const user = ref<UserInfo | null>(null)

  const isLoggedIn = computed(() => user.value !== null)
  const username = computed(() => user.value?.username ?? '')

  async function fetchMe() {
    const requestFetch = useRequestFetch()
    try {
      const data = await requestFetch<{ accountId: number; username: string; phone: string }>('/api/auth/me')
      user.value = { userId: data.accountId, username: data.username }
    } catch {
      user.value = null
    }
  }

  function setUser(info: UserInfo) {
    user.value = info
  }

  async function logout() {
    await $fetch('/api/auth/logout', { method: 'POST' }).catch(() => {})
    user.value = null
    useMemberStore().clear()
    navigateTo('/login')
  }

  return { user, isLoggedIn, username, setUser, fetchMe, logout }
})
