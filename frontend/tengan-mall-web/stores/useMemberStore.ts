import { defineStore } from 'pinia'
import type { MemberProfile } from '~/types/member'

/**
 * 會員 profile（暱稱/頭像/手機）獨立於 useAuthStore（純登入身分）之外——
 * 兩者生命週期一致（登入時一起取得、登出時一起清空），但職責不同，見 useAuthStore 的說明註解。
 */
export const useMemberStore = defineStore('member', () => {
  const profile = ref<MemberProfile | null>(null)

  async function fetchProfile() {
    const requestFetch = useRequestFetch()
    try {
      profile.value = await requestFetch<MemberProfile>('/api/member/profile')
    } catch {
      profile.value = null
    }
  }

  async function updateProfile(nickname: string, avatarUrl: string) {
    await $fetch('/api/member/profile', { method: 'PUT', body: { nickname, avatarUrl } })
    if (profile.value) {
      profile.value = { ...profile.value, nickname, avatarUrl }
    }
  }

  function clear() {
    profile.value = null
  }

  return { profile, fetchProfile, updateProfile, clear }
})
