<template>
  <div class="w-full max-w-md">

    <!-- Logo -->
    <div class="text-center mb-8">
      <NuxtLink to="/" class="text-3xl font-bold text-red-600">天願商城</NuxtLink>
      <p class="text-gray-500 mt-2 text-sm">建立您的天願商城帳號</p>
    </div>

    <!-- 註冊卡片 -->
    <UCard class="shadow-lg">
      <template #header>
        <h1 class="text-xl font-bold text-gray-800 text-center">會員註冊</h1>
      </template>

      <form class="space-y-5" @submit.prevent="handleRegister">

        <!-- 使用者名稱 -->
        <UFormGroup label="使用者名稱" required>
          <UInput
            v-model="username"
            placeholder="請輸入使用者名稱"
            icon="i-heroicons-user"
            size="lg"
            :disabled="loading"
          />
        </UFormGroup>

        <!-- 手機號碼 + 發送驗證碼 -->
        <UFormGroup label="手機號碼" required>
          <div class="flex gap-2">
            <UInput
              v-model="phone"
              placeholder="09xxxxxxxx"
              icon="i-heroicons-phone"
              size="lg"
              class="flex-1"
              :disabled="loading"
            />
            <UButton
              color="gray"
              size="lg"
              variant="outline"
              :disabled="loading || countdown > 0"
              @click="handleSendCode"
            >
              {{ countdown > 0 ? `${countdown}s` : '發送驗證碼' }}
            </UButton>
          </div>
        </UFormGroup>

        <!-- 驗證碼 -->
        <UFormGroup label="簡訊驗證碼" required>
          <UInput
            v-model="code"
            placeholder="請輸入驗證碼"
            icon="i-heroicons-shield-check"
            size="lg"
            :disabled="loading"
          />
        </UFormGroup>

        <!-- 密碼 -->
        <UFormGroup label="密碼" required>
          <UInput
            v-model="password"
            type="password"
            placeholder="請設定密碼（至少 8 碼）"
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

        <!-- 註冊按鈕 -->
        <UButton
          type="submit"
          color="red"
          size="lg"
          block
          :loading="loading"
        >
          立即註冊
        </UButton>

      </form>

      <template #footer>
        <div class="text-center text-sm text-gray-500">
          已有帳號？
          <NuxtLink to="/login" class="text-red-600 font-medium hover:underline">
            返回登入
          </NuxtLink>
        </div>
      </template>
    </UCard>

    <!-- 測試提示 -->
    <UAlert
      class="mt-4"
      color="blue"
      variant="soft"
      icon="i-heroicons-information-circle"
      title="開發模式"
      description="驗證碼請輸入 1234（Mock 固定值）。"
    />

  </div>
</template>

<script setup lang="ts">
definePageMeta({
  layout: 'auth',
})

const username = ref('')
const phone = ref('')
const code = ref('')
const password = ref('')
const countdown = ref(0)
const { register, loading, error } = useAuth()

function handleSendCode() {
  if (!phone.value) return
  countdown.value = 60
  const timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) clearInterval(timer)
  }, 1000)
}

async function handleRegister() {
  await register(username.value, phone.value, password.value, code.value)
}
</script>
