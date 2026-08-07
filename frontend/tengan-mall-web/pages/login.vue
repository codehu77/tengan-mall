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
          <UFormGroup label="帳號" required>
            <UInput
              v-model="loginId"
              placeholder="請輸入使用者名稱"
              icon="i-heroicons-user"
              size="lg"
              :disabled="loading"
            />
          </UFormGroup>

          <!-- 密碼 -->
          <UFormGroup label="密碼" required>
            <UInput
              v-model="password"
              type="password"
              placeholder="請輸入密碼"
              icon="i-heroicons-lock-closed"
              size="lg"
              :disabled="loading"
            />
          </UFormGroup>

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

        <!-- 社群登入（第四階段實作 OAuth2） -->
        <div class="space-y-3">
          <button
            type="button"
            class="w-full flex items-center justify-center gap-3 px-4 py-2.5 rounded-lg border border-gray-200 bg-white hover:bg-gray-50 transition text-sm font-medium text-gray-700"
            @click="showComingSoon"
          >
            <svg class="w-5 h-5" viewBox="0 0 24 24">
              <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
              <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
              <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l3.66-2.84z"/>
              <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
            </svg>
            使用 Google 登入
          </button>

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

const toast = useToast()
const loginId = ref('')
const password = ref('')
const { login, loading, error } = useAuth()

async function handleLogin() {
  await login(loginId.value, password.value)
}

function showComingSoon() {
  toast.add({
    title: '即將推出',
    description: '社群登入將於第四階段實作（Google / LINE / Facebook OAuth2）',
    color: 'blue',
    timeout: 3000,
  })
}
</script>
