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
          <!-- Google 按鈕由 GIS 官方 script 直接渲染進這個容器，樣式由 Google 自己控制。這裡要有
               w-full——渲染當下這個 div 還沒有內容撐開寬度，useGoogleIdentity 量 el.clientWidth
               量到的是容器本身撐出來的寬度，沒有 w-full 會量到 0，退而用寫死的 300px，導致跟下面
               兩顆 w-full 的自訂按鈕寬度對不上、三顆按鈕的 icon 沒對齊。 -->
          <div ref="googleButtonRef" class="w-full flex justify-center" />

          <button
            type="button"
            class="relative w-full h-10 flex items-center justify-center rounded-lg bg-[#06C755] hover:bg-[#05b34c] transition text-sm font-medium text-white"
            @click="handleLineLogin"
          >
            <svg class="w-5 h-5 absolute left-3.5 top-1/2 -translate-y-1/2" viewBox="0 0 24 24" fill="white">
              <path d="M19.365 9.863c.349 0 .63.285.63.631 0 .345-.281.63-.63.63H17.61v1.125h1.755c.349 0 .63.283.63.63 0 .344-.281.629-.63.629h-2.386c-.345 0-.627-.285-.627-.629V8.108c0-.345.282-.63.63-.63h2.386c.346 0 .627.285.627.63 0 .349-.281.63-.63.63H17.61v1.125h1.755zm-3.855 3.016c0 .27-.174.51-.432.596-.064.021-.133.031-.199.031-.211 0-.391-.09-.51-.25l-2.443-3.317v2.94c0 .344-.279.629-.631.629-.346 0-.626-.285-.626-.629V8.108c0-.27.173-.51.43-.595.06-.023.136-.033.194-.033.195 0 .375.104.495.254l2.462 3.33V8.108c0-.345.282-.63.63-.63.345 0 .63.285.63.63v4.771zm-5.741 0c0 .344-.282.629-.631.629-.345 0-.627-.285-.627-.629V8.108c0-.345.282-.63.63-.63.346 0 .628.285.628.63v4.771zm-2.466.629H4.917c-.345 0-.63-.285-.63-.629V8.108c0-.345.285-.63.63-.63.348 0 .63.285.63.63v4.141h1.756c.348 0 .629.283.629.63 0 .344-.282.629-.629.629M24 10.314C24 4.943 18.615.572 12 .572S0 4.943 0 10.314c0 4.811 4.27 8.842 10.035 9.608.391.082.923.258 1.058.59.12.301.079.766.038 1.08l-.164 1.02c-.045.301-.24 1.186 1.049.645 1.291-.539 6.916-4.078 9.436-6.975C23.176 14.393 24 12.458 24 10.314"/>
            </svg>
            使用 LINE 登入
          </button>

          <button
            type="button"
            class="relative w-full h-10 flex items-center justify-center rounded-lg bg-[#1877F2] hover:bg-[#166fe5] transition text-sm font-medium text-white disabled:opacity-60"
            :disabled="loading"
            @click="handleFacebookLogin"
          >
            <svg class="w-5 h-5 absolute left-3.5 top-1/2 -translate-y-1/2" viewBox="0 0 24 24" fill="white">
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
const { login, loginWithGoogle, loginWithFacebook, loading, error } = useAuth()

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

async function handleLineLogin() {
  try {
    await useLineIdentity().login('login')
  } catch {
    toast.add({
      title: 'LINE 登入無法使用',
      description: '請稍後再試，或改用帳號密碼登入',
      color: 'red',
      timeout: 3000,
    })
  }
}

async function handleFacebookLogin() {
  try {
    await useFacebookIdentity().login((accessToken) => loginWithFacebook(accessToken))
  } catch {
    toast.add({
      title: 'Facebook 登入無法使用',
      description: '請稍後再試，或改用帳號密碼登入',
      color: 'red',
      timeout: 3000,
    })
  }
}
</script>
