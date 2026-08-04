import { defineStore } from 'pinia'

interface UserInfo {
  userId: number
  username: string
}

const STORAGE_KEY = 'tengan_auth_user'

export const useAuthStore = defineStore('auth', () => {
  const stored = import.meta.client ? localStorage.getItem(STORAGE_KEY) : null
  const initial = stored ? JSON.parse(stored) as UserInfo : null

  const user = ref<UserInfo | null>(initial)

  const isLoggedIn = computed(() => user.value !== null)
  const username = computed(() => user.value?.username ?? '')

  function setUser(info: UserInfo) {
    user.value = info
    if (import.meta.client) {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(info))
    }
  }

  function logout() {
    user.value = null
    if (import.meta.client) {
      localStorage.removeItem(STORAGE_KEY)
    }
    navigateTo('/login')
  }

  return { user, isLoggedIn, username, setUser, logout }
})
