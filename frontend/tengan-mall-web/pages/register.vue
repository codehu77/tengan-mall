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

      <!-- Step 1：輸入手機/Email -->
      <form v-if="step === 1" class="space-y-5" @submit.prevent="handleStart">
        <UFormGroup label="手機或 Email" required>
          <UInput
            v-model="identifier"
            placeholder="請輸入手機號碼或 Email"
            icon="i-heroicons-user"
            size="lg"
            :disabled="loading"
          />
        </UFormGroup>

        <UAlert
          v-if="error"
          color="red"
          variant="soft"
          icon="i-heroicons-exclamation-circle"
          :description="error"
        />

        <UButton type="submit" color="red" size="lg" block :loading="loading">
          發送驗證碼
        </UButton>
      </form>

      <!-- Step 2：輸入驗證碼 -->
      <form v-else-if="step === 2" class="space-y-5" @submit.prevent="handleVerify">
        <p class="text-sm text-gray-500">驗證碼已發送至 {{ identifier }}</p>

        <UFormGroup label="驗證碼" required>
          <UInput
            v-model="code"
            placeholder="請輸入驗證碼"
            icon="i-heroicons-shield-check"
            size="lg"
            :disabled="loading"
          />
        </UFormGroup>

        <UAlert
          v-if="error"
          color="red"
          variant="soft"
          icon="i-heroicons-exclamation-circle"
          :description="error"
        />

        <UButton type="submit" color="red" size="lg" block :loading="loading">
          下一步
        </UButton>
        <UButton color="gray" variant="ghost" size="lg" block :disabled="loading" @click="step = 1">
          返回上一步
        </UButton>
      </form>

      <!-- Step 3：設定密碼 -->
      <form v-else class="space-y-5" @submit.prevent="handleComplete">
        <UFormGroup label="密碼" required>
          <PasswordInput v-model="password" placeholder="請設定密碼（至少 6 碼）" :disabled="loading" />
        </UFormGroup>

        <UAlert
          v-if="error"
          color="red"
          variant="soft"
          icon="i-heroicons-exclamation-circle"
          :description="error"
        />

        <UButton type="submit" color="red" size="lg" block :loading="loading">
          完成註冊
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
      v-if="otpCode"
      class="mt-4"
      color="blue"
      variant="soft"
      icon="i-heroicons-information-circle"
      title="開發模式"
      :description="`目前沒有接真實簡訊/郵件廠商，驗證碼直接顯示在這裡：${otpCode}`"
    />

  </div>
</template>

<script setup lang="ts">
definePageMeta({
  layout: 'auth',
})

useHead({ title: '註冊會員' })

const step = ref(1)
const identifier = ref('')
const code = ref('')
const password = ref('')
const registrationToken = ref('')
const { startRegister, verifyRegister, completeRegister, otpCode, loading, error } = useAuth()

async function handleStart() {
  const ok = await startRegister(identifier.value)
  if (ok) step.value = 2
}

async function handleVerify() {
  const token = await verifyRegister(identifier.value, code.value)
  if (token) {
    registrationToken.value = token
    step.value = 3
  }
}

async function handleComplete() {
  await completeRegister(registrationToken.value, password.value)
}
</script>
