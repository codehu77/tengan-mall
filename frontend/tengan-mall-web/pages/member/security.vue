<template>
  <div>
    <h1 class="text-2xl font-semibold text-gray-800 mb-2">帳號安全</h1>
    <p class="text-sm text-gray-500 mb-6">綁定社群帳號後，下次即可使用第三方快速登入此帳號。</p>

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
            <p class="text-sm text-gray-400">{{ authStore.user?.googleLinked ? '已綁定' : '尚未綁定' }}</p>
          </div>
        </div>

        <div v-if="!authStore.user?.googleLinked" class="relative h-10 w-44">
          <!-- 真正的 GIS 官方按鈕整顆設透明疊在最上層接收點擊（Google 官方文件認可的自訂按鈕做法），
               維持既有 ID Token 驗證流程不用動後端；底下這顆看得到的按鈕純顯示，文字才能自訂。 -->
          <div ref="googleButtonRef" class="absolute inset-0 opacity-0" />
          <button type="button" class="h-10 w-44 inline-flex items-center justify-center gap-2 rounded-lg border border-gray-300 hover:bg-gray-50 transition text-sm font-medium text-gray-700 pointer-events-none">
            <svg class="w-4 h-4 shrink-0" viewBox="0 0 24 24">
              <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
              <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
              <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l3.66-2.84z"/>
              <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
            </svg>
            綁定 Google 帳號
          </button>
        </div>
      </div>

      <div class="bg-white rounded-xl border border-gray-100 p-5 flex items-center justify-between">
        <div class="flex items-center gap-3">
          <svg class="w-8 h-8 shrink-0" viewBox="0 0 24 24">
            <path fill="#1877F2" d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/>
          </svg>
          <div>
            <p class="font-medium text-gray-800">Facebook</p>
            <p class="text-sm text-gray-400">{{ authStore.user?.facebookLinked ? '已綁定' : '尚未綁定' }}</p>
          </div>
        </div>

        <button
          v-if="!authStore.user?.facebookLinked"
          type="button"
          class="h-10 w-44 inline-flex items-center justify-center gap-2 rounded-lg bg-[#1877F2] hover:bg-[#166fe5] transition text-sm font-medium text-white disabled:opacity-60"
          :disabled="facebookLinking"
          @click="handleLinkFacebook"
        >
          <UIcon v-if="facebookLinking" name="i-heroicons-arrow-path" class="w-4 h-4 animate-spin" />
          <svg v-else class="w-4 h-4 shrink-0" viewBox="0 0 24 24" fill="white">
            <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/>
          </svg>
          綁定 Facebook 帳號
        </button>
      </div>

      <div class="bg-white rounded-xl border border-gray-100 p-5 flex items-center justify-between">
        <div class="flex items-center gap-3">
          <svg class="w-8 h-8 shrink-0" viewBox="0 0 24 24" fill="#06C755">
            <path d="M19.365 9.863c.349 0 .63.285.63.631 0 .345-.281.63-.63.63H17.61v1.125h1.755c.349 0 .63.283.63.63 0 .344-.281.629-.63.629h-2.386c-.345 0-.627-.285-.627-.629V8.108c0-.345.282-.63.63-.63h2.386c.346 0 .627.285.627.63 0 .349-.281.63-.63.63H17.61v1.125h1.755zm-3.855 3.016c0 .27-.174.51-.432.596-.064.021-.133.031-.199.031-.211 0-.391-.09-.51-.25l-2.443-3.317v2.94c0 .344-.279.629-.631.629-.346 0-.626-.285-.626-.629V8.108c0-.27.173-.51.43-.595.06-.023.136-.033.194-.033.195 0 .375.104.495.254l2.462 3.33V8.108c0-.345.282-.63.63-.63.345 0 .63.285.63.63v4.771zm-5.741 0c0 .344-.282.629-.631.629-.345 0-.627-.285-.627-.629V8.108c0-.345.282-.63.63-.63.346 0 .628.285.628.63v4.771zm-2.466.629H4.917c-.345 0-.63-.285-.63-.629V8.108c0-.345.285-.63.63-.63.348 0 .63.285.63.63v4.141h1.756c.348 0 .629.283.629.63 0 .344-.282.629-.629.629M24 10.314C24 4.943 18.615.572 12 .572S0 4.943 0 10.314c0 4.811 4.27 8.842 10.035 9.608.391.082.923.258 1.058.59.12.301.079.766.038 1.08l-.164 1.02c-.045.301-.24 1.186 1.049.645 1.291-.539 6.916-4.078 9.436-6.975C23.176 14.393 24 12.458 24 10.314"/>
          </svg>
          <div>
            <p class="font-medium text-gray-800">LINE</p>
            <p class="text-sm text-gray-400">{{ authStore.user?.lineLinked ? '已綁定' : '尚未綁定' }}</p>
          </div>
        </div>

        <button
          v-if="!authStore.user?.lineLinked"
          type="button"
          class="h-10 w-44 inline-flex items-center justify-center gap-2 rounded-lg bg-[#06C755] hover:bg-[#05b34c] transition text-sm font-medium text-white"
          @click="handleLinkLine"
        >
          <svg class="w-4 h-4 shrink-0" viewBox="0 0 24 24" fill="white">
            <path d="M19.365 9.863c.349 0 .63.285.63.631 0 .345-.281.63-.63.63H17.61v1.125h1.755c.349 0 .63.283.63.63 0 .344-.281.629-.63.629h-2.386c-.345 0-.627-.285-.627-.629V8.108c0-.345.282-.63.63-.63h2.386c.346 0 .627.285.627.63 0 .349-.281.63-.63.63H17.61v1.125h1.755zm-3.855 3.016c0 .27-.174.51-.432.596-.064.021-.133.031-.199.031-.211 0-.391-.09-.51-.25l-2.443-3.317v2.94c0 .344-.279.629-.631.629-.346 0-.626-.285-.626-.629V8.108c0-.27.173-.51.43-.595.06-.023.136-.033.194-.033.195 0 .375.104.495.254l2.462 3.33V8.108c0-.345.282-.63.63-.63.345 0 .63.285.63.63v4.771zm-5.741 0c0 .344-.282.629-.631.629-.345 0-.627-.285-.627-.629V8.108c0-.345.282-.63.63-.63.346 0 .628.285.628.63v4.771zm-2.466.629H4.917c-.345 0-.63-.285-.63-.629V8.108c0-.345.285-.63.63-.63.348 0 .63.285.63.63v4.141h1.756c.348 0 .629.283.629.63 0 .344-.282.629-.629.629M24 10.314C24 4.943 18.615.572 12 .572S0 4.943 0 10.314c0 4.811 4.27 8.842 10.035 9.608.391.082.923.258 1.058.59.12.301.079.766.038 1.08l-.164 1.02c-.045.301-.24 1.186 1.049.645 1.291-.539 6.916-4.078 9.436-6.975C23.176 14.393 24 12.458 24 10.314"/>
          </svg>
          綁定 LINE 帳號
        </button>
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
      linked.value = ok ? 'Google 帳號綁定成功' : ''
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
      linked.value = ok ? 'Facebook 帳號綁定成功' : ''
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
