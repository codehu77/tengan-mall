<template>
  <div>
    <h1 class="text-2xl font-semibold text-gray-800 mb-6">帳號安全</h1>

    <div class="max-w-xl space-y-4">
      <div class="bg-white rounded-xl border border-gray-100 p-5 flex items-center justify-between">
        <div class="flex items-center gap-3">
          <svg class="w-8 h-8 shrink-0" viewBox="0 0 24 24">
            <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
            <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
            <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l3.66-2.84z"/>
            <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
          </svg>
          <div>
            <p class="font-medium text-gray-800">Google</p>
            <p class="text-sm text-gray-400">{{ authStore.user?.googleLinked ? '已連結' : '尚未連結' }}</p>
          </div>
        </div>

        <UBadge v-if="authStore.user?.googleLinked" color="green" variant="soft">已連結</UBadge>
        <div v-else ref="googleButtonRef" />
      </div>

      <div class="bg-white rounded-xl border border-gray-100 p-5 flex items-center justify-between">
        <div class="flex items-center gap-3">
          <svg class="w-8 h-8 shrink-0" viewBox="0 0 24 24">
            <path fill="#1877F2" d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/>
          </svg>
          <div>
            <p class="font-medium text-gray-800">Facebook</p>
            <p class="text-sm text-gray-400">{{ authStore.user?.facebookLinked ? '已連結' : '尚未連結' }}</p>
          </div>
        </div>

        <UBadge v-if="authStore.user?.facebookLinked" color="green" variant="soft">已連結</UBadge>
        <UButton v-else color="blue" variant="soft" size="sm" :loading="facebookLinking" @click="handleLinkFacebook">
          連結
        </UButton>
      </div>

      <div class="bg-white rounded-xl border border-gray-100 p-5 flex items-center justify-between">
        <div class="flex items-center gap-3">
          <svg class="w-8 h-8 shrink-0" viewBox="0 0 24 24" fill="#06C755">
            <path d="M12 2C6.48 2 2 6.02 2 11c0 3.07 1.56 5.8 3.99 7.57L5 22l3.56-1.87C9.6 20.62 10.78 21 12 21c5.52 0 10-4.02 10-9s-4.48-9-10-9zm.88 12.12l-2.27-2.43-4.42 2.43 4.87-5.17 2.33 2.43 4.36-2.43-4.87 5.17z"/>
          </svg>
          <div>
            <p class="font-medium text-gray-800">LINE</p>
            <p class="text-sm text-gray-400">{{ authStore.user?.lineLinked ? '已連結' : '尚未連結' }}</p>
          </div>
        </div>

        <UBadge v-if="authStore.user?.lineLinked" color="green" variant="soft">已連結</UBadge>
        <UButton v-else color="green" variant="soft" size="sm" @click="handleLinkLine">
          連結
        </UButton>
      </div>

      <p v-if="error" class="text-sm text-red-500">{{ error }}</p>
      <p v-if="linked" class="text-sm text-green-600">{{ linked }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ middleware: 'auth', layout: 'member' })

useHead({ title: '帳號安全' })

const authStore = useAuthStore()
const { linkGoogle, linkFacebook, error } = useAuth()
const googleButtonRef = ref<HTMLElement | null>(null)
const linked = ref('')
const facebookLinking = ref(false)

onMounted(async () => {
  if (!authStore.user) {
    await authStore.fetchMe()
  }
  if (authStore.user?.googleLinked || !googleButtonRef.value) return
  try {
    await useGoogleIdentity().renderButton(googleButtonRef.value, async (idToken) => {
      const ok = await linkGoogle(idToken)
      linked.value = ok ? 'Google 帳號連結成功' : ''
    })
  } catch {
    // GIS script 載入失敗不擋整頁
  }
})

async function handleLinkFacebook() {
  facebookLinking.value = true
  try {
    await useFacebookIdentity().login(async (accessToken) => {
      const ok = await linkFacebook(accessToken)
      linked.value = ok ? 'Facebook 帳號連結成功' : ''
    })
  } catch {
    // Facebook SDK 載入失敗不擋整頁
  } finally {
    facebookLinking.value = false
  }
}

/** LINE 是整頁導轉，點下去瀏覽器就離開這個頁面，不需要 loading 狀態；結果在 pages/auth/line/callback.vue 處理完後導回這裡。 */
async function handleLinkLine() {
  await useLineIdentity().login('link')
}
</script>
