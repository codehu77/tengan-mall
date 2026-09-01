<template>
  <div class="w-full max-w-md">

    <!-- Logo -->
    <div class="text-center mb-8">
      <NuxtLink to="/" class="text-3xl font-bold text-red-600">天願商城</NuxtLink>
      <p class="text-gray-500 mt-2 text-sm">歡迎回來，請登入您的帳號</p>
    </div>

    <!-- 登入卡片 -->
    <UCard class="shadow-lg">
      <template #header>
        <h1 class="text-xl font-bold text-gray-800 text-center">會員登入</h1>
      </template>

      <div class="space-y-6">

        <form class="space-y-5" @submit.prevent="handleLogin">

          <!-- 帳號 -->
          <UFormGroup label="手機或 Email" required>
            <UInput
              v-model="identifier"
              placeholder="請輸入手機號碼或 Email"
              icon="i-heroicons-user"
              size="lg"
              :disabled="loading"
            />
          </UFormGroup>

          <!-- 密碼 -->
          <UFormGroup label="密碼" required>
            <PasswordInput v-model="password" placeholder="請輸入密碼" :disabled="loading" />
          </UFormGroup>

          <div class="flex items-center justify-between text-sm">
            <UCheckbox v-model="rememberMe" label="保持登入" :disabled="loading" />
            <NuxtLink to="/forgot-password" class="text-red-600 hover:underline">
              忘記密碼？
            </NuxtLink>
          </div>

          <!-- 錯誤訊息 -->
          <UAlert
            v-if="error"
            color="red"
            variant="soft"
            icon="i-heroicons-exclamation-circle"
            :description="error"
          />

          <!-- 登入按鈕 -->
          <div class="pt-3">
            <UButton
              type="submit"
              color="red"
              size="lg"
              block
              :loading="loading"
            >
              登入
            </UButton>
          </div>

        </form>

        <!-- 分隔線 -->
        <div class="relative">
          <div class="absolute inset-0 flex items-center">
            <div class="w-full border-t border-gray-200" />
          </div>
          <div class="relative flex justify-center text-sm">
            <span class="bg-white px-3 text-gray-400">或以下方式登入</span>
          </div>
        </div>

        <!-- 社群登入 -->
        <div class="space-y-3">
          <!-- Google 按鈕由 GIS 官方 script 直接渲染進這個容器，樣式由 Google 自己控制 -->
          <div ref="googleButtonRef" class="flex justify-center" />

          <button
            type="button"
            class="w-full flex items-center justify-center gap-3 px-4 py-2.5 rounded-lg bg-[#06C755] hover:bg-[#05b34c] transition text-sm font-medium text-white"
            @click="showComingSoon"
          >
            <svg class="w-5 h-5" viewBox="0 0 24 24" fill="white">
              <path d="M12 2C6.48 2 2 6.02 2 11c0 3.07 1.56 5.8 3.99 7.57L5 22l3.56-1.87C9.6 20.62 10.78 21 12 21c5.52 0 10-4.02 10-9s-4.48-9-10-9zm.88 12.12l-2.27-2.43-4.42 2.43 4.87-5.17 2.33 2.43 4.36-2.43-4.87 5.17z"/>
            </svg>
            使用 LINE 登入
          </button>

          <button
            type="button"
            class="w-full flex items-center justify-center gap-3 px-4 py-2.5 rounded-lg bg-[#1877F2] hover:bg-[#166fe5] transition text-sm font-medium text-white"
            @click="showComingSoon"
          >
            <svg class="w-5 h-5" viewBox="0 0 24 24" fill="white">
              <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/>
            </svg>
            使用 Facebook 登入
          </button>
        </div>

      </div>

      <template #footer>
        <div class="text-center text-sm text-gray-500">
          還沒有帳號？
          <NuxtLink to="/register" class="text-red-600 font-medium hover:underline">
            立即註冊
          </NuxtLink>
        </div>
      </template>
    </UCard>

  </div>
</template>

<script setup lang="ts">
definePageMeta({
  layout: 'auth',
})

useHead({ title: '會員登入' })

const toast = useToast()
const identifier = ref('')
const password = ref('')
const rememberMe = ref(false)
const { login, loginWithGoogle, loading, error } = useAuth()

const googleButtonRef = ref<HTMLElement | null>(null)

async function handleLogin() {
  await login(identifier.value, password.value, rememberMe.value)
}

onMounted(async () => {
  if (!googleButtonRef.value) return
  try {
    await useGoogleIdentity().renderButton(googleButtonRef.value, (idToken) => loginWithGoogle(idToken))
  } catch {
    // GIS script 載入失敗（例如網路擋第三方 script）不擋整頁，帳密登入仍可用
  }
})

function showComingSoon() {
  toast.add({
    title: '即將推出',
    description: '社群登入將於下一輪規劃實作（LINE / Facebook）',
    color: 'blue',
    timeout: 3000,
  })
}
</script>
